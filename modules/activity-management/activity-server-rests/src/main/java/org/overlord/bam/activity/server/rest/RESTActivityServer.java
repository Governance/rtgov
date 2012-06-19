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
package org.overlord.bam.activity.server.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.overlord.bam.activity.model.ActivityUnit;
import org.overlord.bam.activity.server.QuerySpec;
import org.overlord.bam.activity.server.ActivityServer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * This class represents the RESTful interface to the activity server.
 *
 */
@Path("/server")
@ApplicationScoped
public class RESTActivityServer {

    private static final Logger LOG=Logger.getLogger(RESTActivityServer.class.getName());
    
    //@javax.inject.Inject
    private ActivityServer _activityServer=null;

    /**
     * This is the default constructor.
     */
    @SuppressWarnings("unchecked")
    public RESTActivityServer() {
        
        try {
            // Need to obtain activity server directly, as inject does not
            // work for REST service, and RESTeasy/CDI integration did not
            // appear to work in AS7. Directly accessing the bean manager
            // should be portable.
            BeanManager bm=InitialContext.doLookup("java:comp/BeanManager");
            
            java.util.Set<Bean<?>> beans=bm.getBeans(ActivityServer.class);
            
            for (Bean<?> b : beans) {                
                CreationalContext<Object> cc=new CreationalContext<Object>() {
                    public void push(Object arg0) {
                    }
                    public void release() {
                    }                   
                };
                
                _activityServer = (ActivityServer)((Bean<Object>)b).create(cc);
                
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Activity server="+_activityServer+" for bean="+b);
                }
                
                if (_activityServer != null) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This method sets the activity server.
     * 
     * @param as The activity server
     */
    public void setActivityServer(ActivityServer as) {
        LOG.info("Set Activity Server="+as);
        _activityServer = as;
    }
    
    /**
     * This method handles queries for activity events.
     * 
     * @param qs The query spec
     * @return The list of activity events
     * @throws Exception Failed to query activity events
     */
    @POST
    @Path("/query")
    @Produces("application/json")
    @Consumes("application/json")
    public java.util.List<ActivityUnit> query(QuerySpec qs) throws Exception {
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Query="+qs);        
        }
        
        if (_activityServer == null) {
            throw new Exception("Activity Server is not available");
        }
        
        return (_activityServer.query(qs));
    }

    /**
     * This method stores the supplied list of activity events.
     * 
     * @param activities The list of activity events
     * @return A response indicating success or failure
     */
    @POST
    @Path("/store")
    @Consumes("application/json")
    public Response store(java.util.List<ActivityUnit> activities) {
 
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Store "+activities.size()+" activities");        
        }
        
        if (_activityServer == null) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity("Activity Server is not available").build();
        }

        try {
            _activityServer.store(activities);
            
            return Response.status(Status.OK).entity("Activities stored").build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Failed to store activities: "+e).build();
        }
    }
}
