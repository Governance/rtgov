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
package org.overlord.rtgov.activity.server.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.enunciate.jaxrs.TypeHint;
import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.activity.server.ActivityServer;
import org.overlord.rtgov.activity.server.QuerySpec;
import org.overlord.rtgov.activity.util.ActivityUtil;
import org.overlord.rtgov.common.util.BeanResolverUtil;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
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
@SuppressWarnings("deprecation")
@Path("/activity")
@ApplicationScoped
public class RESTActivityServer {

    private static final Logger LOG=Logger.getLogger(RESTActivityServer.class.getName());
    
    //@javax.inject.Inject
    private ActivityServer _activityServer=null;

    /**
     * This is the default constructor.
     */
    public RESTActivityServer() {
    }
    
    /**
     * This method initializes the activity server REST service.
     */
    @PostConstruct
    public void init() {
        // Only access CDI if service not set, to support both OSGi and CDI
        if (_activityServer == null) {
            _activityServer = BeanResolverUtil.getBean(ActivityServer.class);
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
     * This method returns the activity server.
     * 
     * @return The activity server
     */
    public ActivityServer getActivityServer() {
        return (_activityServer);
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
    @TypeHint(ActivityUnit.class)
    public Response getActivityUnit(@QueryParam("id") String id) throws Exception {
        init();
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Activity Server Get Activity Unit="+id);        
        }
        
        if (_activityServer == null) {
            throw new Exception("Activity Server is not available");
        }
        
        ActivityUnit au=_activityServer.getActivityUnit(id);
               
        String text=null;
        
        if (au != null) {
            byte[] b=ActivityUtil.serializeActivityUnit(au);
            
            text = new String(b);
        }
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Activity Server: Activity Unit for id '"+id+"': "+text);        
        }

        return (Response.ok(text).build());
    }

    /**
     * This method returns a list of ActivityType (activity event) objects associated with
     * the optional context type and value and optional time range. If a context is not provided
     * then at time range must be supplied, otherwise an error will be reported.
     * 
     * @param type The optional type
     * @param value The optional value
     * @param from The optional 'from' timestamp
     * @param to The optional 'to' timestamp
     * @return The list of ActivityType event objects
     * @throws Exception Failed to obtain activity types
     */
    @GET
    @Path("/events")
    @Produces("application/json")
    @TypeHint(ActivityType.class)
    public Response getActivityTypes(@QueryParam("type") String type,
            @QueryParam("value") String value,
            @DefaultValue("0") @QueryParam("from") long from,
            @DefaultValue("0") @QueryParam("to") long to) throws Exception {
        init();
        
        Context context=null;
        
        if (type != null || value != null) {
            context = new Context();
            
            if (type != null) {
                context.setType(Context.Type.valueOf(type));
            }
            
            context.setValue(value);
        }
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Activity Server: Get Activity Types for Context="+context+" from="+from+" to="+to);        
        }
        
        if (_activityServer == null) {
            throw new Exception("Activity Server is not available");
        }
        
        java.util.List<ActivityType> list=null;
        
        try {
            if (from > 0 || to > 0) {
                list = _activityServer.getActivityTypes(context, from, to);
            } else {
                list = _activityServer.getActivityTypes(context);
            }
        } catch (Exception e) {
            return (Response.serverError().entity(e.getMessage()).build());
        }
        
        String text=null;
        
        if (list != null) {
            byte[] b=ActivityUtil.serializeActivityTypeList(list);
            
            text = new String(b);
        }

        if (LOG.isLoggable(Level.FINEST)) {            
            LOG.finest("Activity Server: Get ActivityTypes for type='"+type+"' value='"
                            +value+"' from="+from+" to="+to+": "+text);        
        }

        return (Response.ok(text).build());
    }
    
    /**
     * This method handles queries for activity events.
     * 
     * @param qspec The query spec
     * @return The list of activity events
     * @throws Exception Failed to query activity events
     * @deprecated No longer supported as it introduces a dependency between clients and the ActivityStore implementation
     */
    @POST
    @Path("/query")
    @Produces("application/json")
    public Response query(String qspec) throws Exception {
        String ret="";
        
        init();
        
        try {
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
        } catch (Exception e) {
            return (Response.serverError().entity(e.getMessage()).build());
        }

        return (Response.ok(ret).build());
    }

    /**
     * This method stores the supplied list of ActivityUnit objects.
     * 
     * @param acts The list of ActivityUnit objects
     * @return A response indicating success or failure
     * @throws Exception Failed to perform store operation
     */
    @POST
    @Path("/store")
    @Consumes("application/json")
    @Produces("text/plain")
    public Response store(java.util.List<ActivityUnit> acts) throws Exception {
        init();
        
        if (LOG.isLoggable(Level.FINEST)) {
            byte[] b=ActivityUtil.serializeActivityUnitList(acts);
            LOG.finest("Store "+acts.size()+" activities: "+new String(b));        
        }
        
        if (_activityServer == null) {
            return Response.status(Status.SERVICE_UNAVAILABLE).entity("Activity Server is not available").build();
        }

        try {
            _activityServer.store(acts);
            
            return Response.status(Status.OK).entity("Activities stored").build();
        } catch (Exception e) {
            if (LOG.isLoggable(Level.SEVERE)) {
                LOG.log(Level.SEVERE, "Failed to store activities", e);
            }
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Failed to store activities: "+e).build();
        }
    }
}
