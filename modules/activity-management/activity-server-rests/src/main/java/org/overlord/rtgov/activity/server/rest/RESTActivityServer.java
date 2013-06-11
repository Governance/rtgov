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
package org.overlord.rtgov.activity.server.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.activity.server.ActivityServer;
import org.overlord.rtgov.activity.server.QuerySpec;
import org.overlord.rtgov.activity.util.ActivityUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * This class represents the RESTful interface to the activity server.
 *
 */
@Path("/activity")
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
            LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                    "activity-server-rests.Messages").getString("CALL-TRACE-RESTS-1"), e);
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
     * @param id The id
     * @return The list of activity events
     * @throws Exception Failed to query activity events
     */
    @GET
    @Path("/unit")
    @Produces("application/json")
    public String getActivityUnit(@QueryParam("id") String id) throws Exception {
        String ret="";
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Activity Server Get Activity Unit="+id);        
        }
        
        if (_activityServer == null) {
            throw new Exception("Activity Server is not available");
        }
        
        ActivityUnit au=_activityServer.getActivityUnit(id);
        
        if (au != null) {
            byte[] b=ActivityUtil.serializeActivityUnit(au);
            
            ret = new String(b);
        }
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Activity Server Query Result="+ret);        
        }

        return (ret);
    }

    /**
     * This method returns activity types (events) associated with
     * the supplied context value.
     * 
     * @param type The type
     * @param value The value
     * @param from The optional 'from' timestamp
     * @param to The optional 'to' timestamp
     * @return The list of activity types
     * @throws Exception Failed to obtain activity types
     */
    @GET
    @Path("/events")
    @Produces("application/json")
    public String getActivityTypes(@QueryParam("type") String type,
            @QueryParam("value") String value,
            @DefaultValue("0") @QueryParam("from") long from,
            @DefaultValue("0") @QueryParam("to") long to) throws Exception {
        String ret="";
        
        Context context=new Context();
        
        if (type != null) {
            context.setType(Context.Type.valueOf(type));
        }
        
        context.setValue(value);
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Activity Server: Get Activity Types for Context="+context);        
        }
        
        if (_activityServer == null) {
            throw new Exception("Activity Server is not available");
        }
        
        java.util.List<ActivityType> list=null;
        
        if (from > 0 || to > 0) {
            list = _activityServer.getActivityTypes(context, from, to);
        } else {
            list = _activityServer.getActivityTypes(context);
        }
        
        if (list != null) {
            byte[] b=ActivityUtil.serializeActivityTypeList(list);
            
            ret = new String(b);
        }
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Activity Server: Get Activity Types Result="+ret);        
        }

        return (ret);
    }
    
    /**
     * This method handles queries for activity events.
     * 
     * @param qspec The query spec
     * @return The list of activity events
     * @throws Exception Failed to query activity events
     */
    @POST
    @Path("/query")
    @Produces("application/json")
    public String query(String qspec) throws Exception {
        String ret="";
        
        QuerySpec qs=ActivityUtil.deserializeQuerySpec(qspec.getBytes());
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Activity Server Query Spec="+qs);        
        }
        
        if (_activityServer == null) {
            throw new Exception("Activity Server is not available");
        }
        
        java.util.List<ActivityType> list=_activityServer.query(qs);
        
        if (list != null) {
            byte[] b=ActivityUtil.serializeActivityTypeList(list);
            
            ret = new String(b);
        }
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Activity Server Query Result="+ret);        
        }

        return (ret);
    }

    /**
     * This method stores the supplied list of activity events.
     * 
     * @param acts The list of activity events
     * @return A response indicating success or failure
     * @throws Exception Failed to perform store operation
     */
    @POST
    @Path("/store")
    public Response store(String acts) throws Exception {
 
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Store activities="+acts);        
        }
        
        java.util.List<ActivityUnit> activities=
                    ActivityUtil.deserializeActivityUnitList(acts.getBytes());
        
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
