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
import org.overlord.rtgov.service.dependency.ServiceDependencyBuilder;
import org.overlord.rtgov.service.dependency.ServiceGraph;
import org.overlord.rtgov.service.dependency.layout.ServiceGraphLayoutImpl;
import org.overlord.rtgov.service.dependency.presentation.SeverityAnalyzer;
import org.overlord.rtgov.service.dependency.svg.SVGServiceGraphGenerator;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
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
    @SuppressWarnings("unchecked")
    public RESTServiceDependencyServer() {
        
        _acmManager = ActiveCollectionManagerAccessor.getActiveCollectionManager();
            
        if (_acmManager == null) {
            LOG.severe(java.util.PropertyResourceBundle.getBundle(
                    "service-dependency-rests.Messages").getString("SERVICE-DEPENDENCY-RESTS-1"));
        }

        try {
            // Need to obtain active collection manager directly, as inject does not
            // work for REST service, and RESTeasy/CDI integration did not
            // appear to work in AS7. Directly accessing the bean manager
            // should be portable.
            BeanManager bm=InitialContext.doLookup("java:comp/BeanManager");
            
            java.util.Set<Bean<?>> beans=bm.getBeans(SeverityAnalyzer.class);
            
            for (Bean<?> b : beans) {                
                CreationalContext<Object> cc=new CreationalContext<Object>() {
                    public void push(Object arg0) {
                    }
                    public void release() {
                    }                   
                };
                
                _severityAnalyzer = (SeverityAnalyzer)((Bean<Object>)b).create(cc);
                
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Severity analyzer="+_severityAnalyzer+" for bean="+b);
                }
                
                if (_severityAnalyzer != null) {
                    break;
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                    "service-dependency-rests.Messages").getString("SERVICE-DEPENDENCY-RESTS-2"), e);
        }
    }
    
    /**
     * This method sets the activity server.
     * 
     * @param acm The activity server
     */
    public void setActivityCollectionManager(ActiveCollectionManager acm) {
        LOG.info("Set Active Collection Manager="+acm);
        _acmManager = acm;
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
