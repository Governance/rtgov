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
package org.overlord.rtgov.ui.server.services;

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
public interface IServicesServiceImpl {

    /**
     * Return a list of all application names.
     * @throws UiException
     */
    public ApplicationListBean getApplicationNames() throws UiException;

    /**
     * Search for services using the given filters.
     * @param search
     * @throws UiException
     */
    public ServiceResultSetBean findServices(ServicesSearchBean search) throws UiException;

    /**
     * Fetches a full service by its name.
     * @param serviceId
     * @throws UiException
     */
    public ServiceBean getService(String serviceId) throws UiException;

    /**
     * Fetches a full reference by its id.
     * @param referenceId
     * @throws UiException
     */
    public ReferenceBean getReference(String referenceId) throws UiException;

}
