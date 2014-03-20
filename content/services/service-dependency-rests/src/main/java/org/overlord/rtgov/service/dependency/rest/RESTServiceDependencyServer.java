/*
 * 2012-3 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.rtgov.service.dependency.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.overlord.rtgov.active.collection.ActiveCollection;
import org.overlord.rtgov.active.collection.ActiveCollectionManager;
import org.overlord.rtgov.active.collection.ActiveCollectionManagerAccessor;
import org.overlord.rtgov.active.collection.ActiveMap;
import org.overlord.rtgov.analytics.service.ServiceDefinition;
import org.overlord.rtgov.analytics.situation.Situation;
import org.overlord.rtgov.common.util.BeanResolverUtil;
import org.overlord.rtgov.service.dependency.ServiceDependencyBuilder;
import org.overlord.rtgov.service.dependency.ServiceGraph;
import org.overlord.rtgov.service.dependency.layout.ServiceGraphLayoutImpl;
import org.overlord.rtgov.service.dependency.presentation.SeverityAnalyzer;
import org.overlord.rtgov.service.dependency.svg.SVGServiceGraphGenerator;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * This class represents the RESTful interface to the service dependency server.
 *
 */
@Path("/service/dependency")
@ApplicationScoped
public class RESTServiceDependencyServer {

    private static final Logger LOG=Logger.getLogger(RESTServiceDependencyServer.class.getName());
    
    private ActiveCollectionManager _acmManager=null;
    
    private ActiveCollection _servDefns=null;
    private ActiveCollection _situations=null;
    
    private SeverityAnalyzer _severityAnalyzer=null;

    /**
     * This is the default constructor.
     */
    public RESTServiceDependencyServer() {
    }
    
    /**
     * This method initializes the service dependency REST service.
     */
    @PostConstruct
    public void init() {
        
        // Only access CDI if service not set, to support both OSGi and CDI
        if (_acmManager == null) {
            _acmManager = ActiveCollectionManagerAccessor.getActiveCollectionManager();
                
            if (_acmManager == null) {
                LOG.severe(java.util.PropertyResourceBundle.getBundle(
                        "service-dependency-rests.Messages").getString("SERVICE-DEPENDENCY-RESTS-1"));
            }
        }

        // Only access CDI if service not set, to support both OSGi and CDI
        if (_severityAnalyzer == null) {
            _severityAnalyzer = BeanResolverUtil.getBean(SeverityAnalyzer.class);

            if (_severityAnalyzer == null) {
                LOG.severe(java.util.PropertyResourceBundle.getBundle(
                        "service-dependency-rests.Messages").getString("SERVICE-DEPENDENCY-RESTS-2"));
            }
        }
    }
    
    /**
     * This method sets the active collection manager.
     * 
     * @param acm The active collection manager
     */
    public void setActiveCollectionManager(ActiveCollectionManager acm) {
        LOG.info("Set Active Collection Manager="+acm);
        _acmManager = acm;
    }
    
    /**
     * This method returns the active collection manager.
     * 
     * @return The active collection manager
     */
    public ActiveCollectionManager getActiveCollectionManager() {
        return (_acmManager);
    }
    
    /**
     * This method sets the severity analyzer.
     * 
     * @param sa The severity analyzer
     */
    public void setSeverityAnalyzer(SeverityAnalyzer sa) {
        _severityAnalyzer = sa;
    }
    
    /**
     * This method gets the severity analyzer.
     * 
     * @return The severity analyzer
     */
    public SeverityAnalyzer getSeverityAnalyzer() {
        return (_severityAnalyzer);
    }
    
    /**
     * This method handles queries.
     * 
     * @param width The optional width
     * @return The list of objects
     * @throws Exception Failed to query
     */
    @GET
    @Path("/overview")
    @Produces("image/svg+xml")
    public String overview(@DefaultValue("0") @QueryParam("width") int width) throws Exception {
        String ret="";
        
        init();
        
        // Obtain service definition collection
        if (_acmManager != null && _servDefns == null) {
            _servDefns=_acmManager.getActiveCollection("ServiceDefinitions");
        }
        
        if (_acmManager != null && _situations == null) {
            _situations=_acmManager.getActiveCollection("Situations");
        }
        
        if (_servDefns == null) {
            throw new Exception("Service definitions are not available");
        }
        
        if (_situations == null) {
            throw new Exception("Situations are not available");
        }
        
        java.util.Set<ServiceDefinition> sds=new java.util.HashSet<ServiceDefinition>();
        
        for (Object entry : _servDefns) {
            if (entry instanceof ActiveMap.Entry
                    && ((ActiveMap.Entry)entry).getValue() instanceof ServiceDefinition) {
                sds.add((ServiceDefinition)((ActiveMap.Entry)entry).getValue());
            }
        }
        
        java.util.List<Situation> situations=new java.util.ArrayList<Situation>();
        
        for (Object obj : _situations) {
            if (obj instanceof Situation) {
                situations.add((Situation)obj);
            }
        }
        
        ServiceGraph graph=
                ServiceDependencyBuilder.buildGraph(sds, situations);
        
        if (graph == null) {
            throw new Exception("Failed to generate service dependency overview");
        }
        
        graph.setDescription("Generated: "+new java.util.Date());
        
        ServiceGraphLayoutImpl layout=new ServiceGraphLayoutImpl();
        
        layout.layout(graph);
        
        // Check some of the dimensions
        SVGServiceGraphGenerator generator=new SVGServiceGraphGenerator();        
        generator.setSeverityAnalyzer(_severityAnalyzer);
        
        java.io.ByteArrayOutputStream os=new java.io.ByteArrayOutputStream();
        
        generator.generate(graph, width, os);
        
        os.close();
        
        ret = new String(os.toByteArray());
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Overview="+ret);        
        }

        return (ret);
    }

}
