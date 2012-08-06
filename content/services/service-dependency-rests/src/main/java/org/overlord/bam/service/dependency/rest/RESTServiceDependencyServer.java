/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-11, Red Hat Middleware LLC, and others contributors as indicated
 * by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.overlord.bam.service.dependency.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.overlord.bam.active.collection.ActiveCollection;
import org.overlord.bam.active.collection.ActiveCollectionManager;
import org.overlord.bam.active.collection.ActiveMap;
import org.overlord.bam.analytics.service.ServiceDefinition;
import org.overlord.bam.service.dependency.ServiceDependencyBuilder;
import org.overlord.bam.service.dependency.ServiceGraph;
import org.overlord.bam.service.dependency.layout.ServiceGraphLayoutImpl;
import org.overlord.bam.service.dependency.svg.SVGServiceGraphGenerator;

import javax.enterprise.context.ApplicationScoped;
import javax.naming.InitialContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * This class represents the RESTful interface to the service dependency server.
 *
 */
@Path("/service/dependency")
@ApplicationScoped
public class RESTServiceDependencyServer {

    private static final Logger LOG=Logger.getLogger(RESTServiceDependencyServer.class.getName());
    
    private static final String ACT_COLL_MANAGER = "java:global/overlord-bam/ActiveCollectionManager";

    private ActiveCollectionManager _acmManager=null;
    
    private ActiveCollection _servDefns=null;

    /**
     * This is the default constructor.
     */
    public RESTServiceDependencyServer() {
        
        try {
            InitialContext ctx=new InitialContext();
            
            _acmManager = (ActiveCollectionManager)ctx.lookup(ACT_COLL_MANAGER);
            
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to obtain Active Collection Manager", e);
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
     * @param qspec The query spec
     * @return The list of objects
     * @throws Exception Failed to query
     */
    @GET
    @Path("/overview")
    @Produces("image/svg+xml")
    public String overview() throws Exception {
        String ret="";
        
        // Obtain service definition collection
        if (_acmManager != null && _servDefns == null) {
            _servDefns=_acmManager.getActiveCollection("ServiceDefinitions");
        }
        
        if (_servDefns == null) {
            throw new Exception("Service definitions are not available");
        }
        
        java.util.Set<ServiceDefinition> sds=new java.util.HashSet<ServiceDefinition>();
        
        for (Object entry : _servDefns) {
            if (entry instanceof ActiveMap.Entry &&
                    ((ActiveMap.Entry)entry).getValue() instanceof ServiceDefinition) {
                sds.add((ServiceDefinition)((ActiveMap.Entry)entry).getValue());
            }
        }
        
        ServiceGraph graph=
                ServiceDependencyBuilder.buildGraph(sds);
        
        if (graph == null) {
            throw new Exception("Failed to generate service dependency overview");
        }
        
        graph.setDescription("Generated: "+new java.util.Date());
        
        ServiceGraphLayoutImpl layout=new ServiceGraphLayoutImpl();
        
        layout.layout(graph);
        
        // Check some of the dimensions
        SVGServiceGraphGenerator generator=new SVGServiceGraphGenerator();
        
        java.io.ByteArrayOutputStream os=new java.io.ByteArrayOutputStream();
        
        generator.generate(graph, os);
        
        os.close();
        
        ret = new String(os.toByteArray());
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Overview="+ret);        
        }

        return (ret);
    }

}
