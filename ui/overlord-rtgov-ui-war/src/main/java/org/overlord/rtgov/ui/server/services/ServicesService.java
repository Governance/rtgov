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

import java.util.List;

import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.overlord.rtgov.ui.client.model.QName;
import org.overlord.rtgov.ui.client.model.ReferenceBean;
import org.overlord.rtgov.ui.client.model.ServiceBean;
import org.overlord.rtgov.ui.client.model.ServiceResultSetBean;
import org.overlord.rtgov.ui.client.model.ServicesFilterBean;
import org.overlord.rtgov.ui.client.model.UiException;
import org.overlord.rtgov.ui.client.shared.services.IServicesService;

/**
 * Concrete implementation of the services service. :)
 *
 * @author eric.wittmann@redhat.com
 */
@Service
public class ServicesService implements IServicesService {

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
    public List<QName> getApplicationNames() throws UiException {
        return impl.getApplicationNames();
    }

    /**
     * @see org.overlord.rtgov.ui.client.shared.services.IServicesService#findServices(org.overlord.rtgov.ui.client.model.ServicesFilterBean, int, java.lang.String, boolean)
     */
    @Override
    public ServiceResultSetBean findServices(ServicesFilterBean filters, int page, String sortColumn,
            boolean ascending) throws UiException {
        return impl.findServices(filters, page, sortColumn, ascending);
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.IServicesService#getService(java.lang.String)
     */
    @Override
    public ServiceBean getService(String uuid) throws UiException {
        return impl.getService(uuid);
    }

    /**
     * @see org.overlord.rtgov.ui.client.shared.services.IServicesService#getReference(java.lang.String)
     */
    @Override
    public ReferenceBean getReference(String referenceId) throws UiException {
        return impl.getReference(referenceId);
    }
}
