/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.rtgov.ui.client.shared.services;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.overlord.rtgov.ui.client.model.ApplicationListBean;
import org.overlord.rtgov.ui.client.model.ReferenceBean;
import org.overlord.rtgov.ui.client.model.ServiceBean;
import org.overlord.rtgov.ui.client.model.ServiceResultSetBean;
import org.overlord.rtgov.ui.client.model.ServicesSearchBean;
import org.overlord.rtgov.ui.client.model.UiException;

/**
 * Provides a way to manage services.
 *
 * @author eric.wittmann@redhat.com
 */
@Path("/rest/services")
public interface IServicesService {

    /**
     * Return a list of all application names.
     * @throws UiException
     */
    @GET
    @Path("applications")
    @Produces(MediaType.APPLICATION_JSON)
    public ApplicationListBean getApplicationNames() throws UiException;

    /**
     * Search for services using the given search criteria.
     * @param search
     * @param page
     * @param sortColumn
     * @param ascending
     * @throws UiException
     */
    @POST
    @Path("search")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ServiceResultSetBean findServices(ServicesSearchBean search) throws UiException;

    /**
     * Fetches a full service by its id.
     * @param id
     * @throws UiException
     */
    @GET
    @Path("service")
    @Produces(MediaType.APPLICATION_JSON)
    public ServiceBean getService(@QueryParam("id") String id) throws UiException;

    /**
     * Fetches a full reference by its id.
     * @param id
     * @throws UiException
     */
    @GET
    @Path("reference")
    @Produces(MediaType.APPLICATION_JSON)
    public ReferenceBean getReference(@QueryParam("id") String id) throws UiException;

}
