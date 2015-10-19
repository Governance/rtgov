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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.hawkular.btm.api.model.config.CollectorConfiguration;
import org.hawkular.btm.api.services.ConfigurationService;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * REST interface for configyuration capabilities.
 *
 * @author gbrown
 *
 */
@Path("admin")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ConfigHandler {

    private static final Logger LOG = Logger.getLogger(ConfigHandler.class.getName());

    @Inject
    private ConfigurationService _configService;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Get the config.
     *
     * @param host The host
     * @param server The server
     * @return The response
     */
    @GET
    @Path("config")
    @Produces(APPLICATION_JSON)
    public Response getConfiguration(@QueryParam("host") String host, @QueryParam("server") String server) {

        try {
            CollectorConfiguration config = _configService.getCollector(null,
                    host, server);

            return Response.status(Response.Status.OK).entity(MAPPER.writeValueAsString(config))
                    .type(APPLICATION_JSON_TYPE).build();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
            Map<String, String> errors = new HashMap<String, String>();
            errors.put("errorMsg", "Internal Error: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errors).type(APPLICATION_JSON_TYPE).build();
        }

    }
}
