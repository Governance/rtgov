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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.overlord.rtgov.ui.client.model.ApplicationListBean;
import org.overlord.rtgov.ui.client.model.ReferenceBean;
import org.overlord.rtgov.ui.client.model.ServiceBean;
import org.overlord.rtgov.ui.client.model.ServiceResultSetBean;
import org.overlord.rtgov.ui.client.model.ServicesSearchBean;
import org.overlord.rtgov.ui.client.model.UiException;
import org.overlord.rtgov.ui.client.shared.services.IServicesService;

/**
 * Concrete implementation of the services service. :)
 *
 * @author eric.wittmann@redhat.com
 */
public class ServicesService implements IServicesService {
    
    private static final Logger LOG=Logger.getLogger(ServicesService.class.getName());

    @Inject IServicesServiceImpl impl;

    /**
     * Constructor.
     */
    public ServicesService() {
    }

    /**
     * @see org.overlord.rtgov.ui.client.shared.services.IServicesService#getApplicationNames()
     */
    @Override
    public ApplicationListBean getApplicationNames() throws UiException {
        return impl.getApplicationNames();
    }

    /**
     * @see org.overlord.rtgov.ui.client.shared.services.IServicesService#findServices(org.overlord.rtgov.ui.client.model.ServicesFilterBean, int, java.lang.String, boolean)
     */
    @Override
    public ServiceResultSetBean findServices(ServicesSearchBean search) throws UiException {
        return impl.findServices(search);
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.IServicesService#getService(java.lang.String)
     */
    @Override
    public ServiceBean getService(String id) throws UiException {
        try {
            id = java.net.URLDecoder.decode(id, "UTF-8");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to decode id", e);
        }
        return impl.getService(id);
    }

    /**
     * @see org.overlord.rtgov.ui.client.shared.services.IServicesService#getReference(java.lang.String)
     */
    @Override
    public ReferenceBean getReference(String id) throws UiException {
        try {
            id = java.net.URLDecoder.decode(id, "UTF-8");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to decode id", e);
        }
        return impl.getReference(id);
    }
}
