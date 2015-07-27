/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.rtgov.integration.btm;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.hawkular.btm.api.model.btxn.BusinessTransaction;
import org.overlord.commons.services.ServiceRegistryUtil;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.server.ActivityServer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * REST interface for reporting and querying business transactions.
 *
 * @author gbrown
 *
 */
@Path("transactions")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class BusinessTransactionHandler {

    private static final Logger LOG = Logger.getLogger(BusinessTransactionHandler.class.getName());

    private BTMFragmentToActivityUnitConverter _converter=new BTMFragmentToActivityUnitConverter();

    private ActivityServer _activityServer=null;

    private static final TypeReference<java.util.List<BusinessTransaction>> BUSINESS_TXN_LIST =
            new TypeReference<java.util.List<BusinessTransaction>>() {
    };

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * This method initializes the activity server REST service.
     */
    @PostConstruct
    public void init() {
        if (_activityServer == null) {
            _activityServer = ServiceRegistryUtil.getSingleService(ActivityServer.class);
        }
    }
    
    /**
     * Add a list of business transactions.
     *
     * @param btxnsjson The business transactions
     * @return The response
     */
    @POST
    public Response addBusinessTransactions(String btxnsjson) {

        List<BusinessTransaction> btxns=null;
        try {
            btxns = MAPPER.readValue(btxnsjson, BUSINESS_TXN_LIST);
        } catch (Exception e) {
            LOG.severe("Failed to deserialize business transactions: "+e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .type(APPLICATION_JSON_TYPE).build();
        }

        if (btxns.size() == 0) {
            return Response.status(Response.Status.OK).build();
        }

        try {
            List<ActivityUnit> aus=_converter.convert(btxns);

            if (aus == null || aus.isEmpty()) {
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("No activity units extracted from business transaction fragments");
                }
                return Response.status(Response.Status.OK).build();
            }
            
            if (_activityServer == null) {
                LOG.severe("No activity server available");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .type(APPLICATION_JSON_TYPE).build();
            }

            _activityServer.store(aus);

            return Response.status(Response.Status.OK).build();

        } catch (Throwable t) {
            LOG.log(Level.SEVERE, t.getMessage(), t);
            Map<String, String> errors = new HashMap<String, String>();
            errors.put("errorMsg", "Internal Error: " + t.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errors).type(APPLICATION_JSON_TYPE).build();
        }
    }

    /**
     * Get btxn.
     *
     * @param id The id
     * @return The response
     */
    @GET
    @Path("{id}")
    @Produces(APPLICATION_JSON)
    public Response getBusinessTransaction(@PathParam("id") String id) {
        return Response.status(Response.Status.BAD_REQUEST).type(APPLICATION_JSON_TYPE).build();
    }

    /**
     * Query.
     *
     * @param startTime The start
     * @param endTime The end
     * @param properties The props
     * @param correlations The correlations
     * @return The response
     */
    @GET
    @Produces(APPLICATION_JSON)
    public Response queryBusinessTransactions(@QueryParam("startTime") long startTime,
                    @QueryParam("endTime") long endTime,
                    @DefaultValue("") @QueryParam("properties") String properties,
                    @DefaultValue("") @QueryParam("correlations") String correlations) {
        return Response.status(Response.Status.BAD_REQUEST).type(APPLICATION_JSON_TYPE).build();
    }

}
