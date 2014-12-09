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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.overlord.rtgov.ui.client.local.services.rpc.DelegatingErrorCallback;
import org.overlord.rtgov.ui.client.local.services.rpc.DelegatingRemoteCallback;
import org.overlord.rtgov.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.rtgov.ui.client.model.ApplicationListBean;
import org.overlord.rtgov.ui.client.model.ReferenceBean;
import org.overlord.rtgov.ui.client.model.ServiceBean;
import org.overlord.rtgov.ui.client.model.ServiceResultSetBean;
import org.overlord.rtgov.ui.client.model.ServicesFilterBean;
import org.overlord.rtgov.ui.client.model.ServicesSearchBean;
import org.overlord.rtgov.ui.client.model.UiException;
import org.overlord.rtgov.ui.client.shared.services.IServicesService;

/**
 * Client-side service for making RPC calls to the remote deployments service.
 *
 * @author eric.wittmann@redhat.com
 */
@ApplicationScoped
public class ServicesServiceCaller {

    @Inject
    private Caller<IServicesService> remoteServicesService;

    /**
     * Constructor.
     */
    public ServicesServiceCaller() {
    }

    /**
     * @see org.overlord.rtgov.ui.client.shared.services.IServicesService#getApplicationNames()
     */
    public void getApplicationNames(final IRpcServiceInvocationHandler<ApplicationListBean> handler) {
        RemoteCallback<ApplicationListBean> successCallback = new DelegatingRemoteCallback<ApplicationListBean>(handler);
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
            ServicesSearchBean search=new ServicesSearchBean();
            search.setFilters(filters);
            search.setPage(page);
            search.setSortColumnId(sortColumn);
            search.setSortAscending(ascending);
            
            remoteServicesService.call(successCallback, errorCallback).findServices(search);
        } catch (UiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * @see org.overlord.rtgov.ui.client.shared.services.IServicesService#getService(String)
     */
    public void getService(String id, IRpcServiceInvocationHandler<ServiceBean> handler) {
        RemoteCallback<ServiceBean> successCallback = new DelegatingRemoteCallback<ServiceBean>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            String encodedId=com.google.gwt.http.client.URL.encode(id);
            remoteServicesService.call(successCallback, errorCallback).getService(encodedId);
        } catch (UiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * @see org.overlord.rtgov.ui.client.shared.services.IServicesService#getReference(String)
     */
    public void getReference(String id, IRpcServiceInvocationHandler<ReferenceBean> handler) {
        RemoteCallback<ReferenceBean> successCallback = new DelegatingRemoteCallback<ReferenceBean>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            String encodedId=com.google.gwt.http.client.URL.encode(id);
            remoteServicesService.call(successCallback, errorCallback).getReference(encodedId);
        } catch (UiException e) {
            errorCallback.error(null, e);
        }
    }

}
