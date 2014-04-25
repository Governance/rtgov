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
package org.overlord.rtgov.situation.manager.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.overlord.rtgov.analytics.situation.IgnoreSubject;
import org.overlord.rtgov.analytics.util.SituationUtil;
import org.overlord.rtgov.common.util.BeanResolverUtil;
import org.overlord.rtgov.situation.manager.SituationManager;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

/**
 * This class represents the RESTful interface to the situation manager server.
 *
 */
@Path("/situation/manager")
@ApplicationScoped
@Deprecated
public class RESTSituationManagerServer {

    private static final Logger LOG=Logger.getLogger(RESTSituationManagerServer.class.getName());
    
    private SituationManager _situationManager=null;
    
    /**
     * This is the default constructor.
     */
    public RESTSituationManagerServer() {
    }

    /**
     * This method initializes the situation manager REST service.
     */
    @PostConstruct
    public void init() {
        
        // Only access CDI if service not set, to support both OSGi and CDI
        if (_situationManager == null) {
            _situationManager = BeanResolverUtil.getBean(SituationManager.class);
        }
    }
    
    /**
     * This method sets the situation manager.
     * 
     * @param sm The situation manager
     */
    public void setSituationManager(SituationManager sm) {
        LOG.info("Set Situation Manager="+sm);
        _situationManager = sm;
    }
    
    /**
     * This method returns the situation manager.
     * 
     * @return The situation manager
     */
    public SituationManager getSituationManager() {
        return (_situationManager);
    }
    
    /**
     * This method ignores a situation subject.
     * 
     * @param details The ignore subject details
     * @param context The security context
     * @return A response indicating success or failure
     * @throws Exception Failed to ignore the subject
     */
    @POST
    @Path("/ignore")
    public Response ignore(String details, @Context SecurityContext context) throws Exception {
        
        init();
 
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Ignore situation: "+details);        
        }
        
        IgnoreSubject ignore=SituationUtil.deserializeIgnoreSubject(details.getBytes());       
        
        // Override the principal and timestamp
        if (context.getUserPrincipal() != null) {
            ignore.setPrincipal(context.getUserPrincipal().getName());
        }
        ignore.setTimestamp(System.currentTimeMillis());
        
        try {
            _situationManager.ignore(ignore);
            
            return Response.status(Status.OK).entity("Subject ignored").build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Failed to ignore subject: "+e).build();
        }
    }
    
    /**
     * This method observes a situation subject, which means
     * stop ignoring it.
     * 
     * @param subject The subject
     * @param context The security context
     * @return A response indicating success or failure
     * @throws Exception Failed to observe the subject
     */
    @POST
    @Path("/observe")
    public Response observe(String subject, @Context SecurityContext context) throws Exception {
        
        init();
 
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Observe subject: "+subject);        
        }
        
        // Obtain the principal
        String principal=null;
        
        if (context.getUserPrincipal() != null) {
            principal = context.getUserPrincipal().getName();
        }
        
        try {
            _situationManager.observe(subject, principal);
            
            return Response.status(Status.OK).entity("Subject observed").build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Failed to observe subject: "+e).build();
        }
    }
}
