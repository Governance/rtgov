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
package org.overlord.rtgov.ui.client.local.services;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.overlord.rtgov.ui.client.local.services.rpc.DelegatingErrorCallback;
import org.overlord.rtgov.ui.client.local.services.rpc.DelegatingRemoteCallback;
import org.overlord.rtgov.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.rtgov.ui.client.model.QName;
import org.overlord.rtgov.ui.client.model.ReferenceBean;
import org.overlord.rtgov.ui.client.model.ServiceBean;
import org.overlord.rtgov.ui.client.model.ServiceResultSetBean;
import org.overlord.rtgov.ui.client.model.ServicesFilterBean;
import org.overlord.rtgov.ui.client.model.UiException;
import org.overlord.rtgov.ui.client.shared.services.IServicesService;

/**
 * Client-side service for making RPC calls to the remote deployments service.
 *
 * @author eric.wittmann@redhat.com
 */
@ApplicationScoped
public class ServicesRpcService {

    @Inject
    private Caller<IServicesService> remoteServicesService;

    /**
     * Constructor.
     */
    public ServicesRpcService() {
    }

    /**
     * @see org.overlord.rtgov.ui.client.shared.services.IServicesService#getApplicationNames()
     */
    public void getApplicationNames(final IRpcServiceInvocationHandler<List<QName>> handler) {
        RemoteCallback<List<QName>> successCallback = new DelegatingRemoteCallback<List<QName>>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            remoteServicesService.call(successCallback, errorCallback).getApplicationNames();
        } catch (UiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * @see org.overlord.rtgov.ui.client.shared.services.IServicesService#findServices(ServicesFilterBean, int, String, boolean)
     */
    public void findServices(ServicesFilterBean filters, int page, String sortColumn, boolean ascending,
            final IRpcServiceInvocationHandler<ServiceResultSetBean> handler) {
        // TODO only allow one search at a time.  If another search comes in before the previous one
        // finished, cancel the previous one.  In other words, only return the results of the *last*
        // search performed.
        RemoteCallback<ServiceResultSetBean> successCallback = new DelegatingRemoteCallback<ServiceResultSetBean>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            remoteServicesService.call(successCallback, errorCallback).findServices(filters, page, sortColumn, ascending);
        } catch (UiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * @see org.overlord.rtgov.ui.client.shared.services.IServicesService#getService(String)
     */
    public void getService(String serviceId, IRpcServiceInvocationHandler<ServiceBean> handler) {
        RemoteCallback<ServiceBean> successCallback = new DelegatingRemoteCallback<ServiceBean>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            remoteServicesService.call(successCallback, errorCallback).getService(serviceId);
        } catch (UiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * @see org.overlord.rtgov.ui.client.shared.services.IServicesService#getReference(String)
     */
    public void getReference(String referenceId, IRpcServiceInvocationHandler<ReferenceBean> handler) {
        RemoteCallback<ReferenceBean> successCallback = new DelegatingRemoteCallback<ReferenceBean>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            remoteServicesService.call(successCallback, errorCallback).getReference(referenceId);
        } catch (UiException e) {
            errorCallback.error(null, e);
        }
    }

}
