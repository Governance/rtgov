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
import org.overlord.rtgov.ui.client.model.BatchRetryResult;
import org.overlord.rtgov.ui.client.model.ResolutionState;
import org.overlord.rtgov.ui.client.model.SituationBean;
import org.overlord.rtgov.ui.client.model.SituationResultSetBean;
import org.overlord.rtgov.ui.client.model.SituationsFilterBean;
import org.overlord.rtgov.ui.client.model.UiException;
import org.overlord.rtgov.ui.client.shared.services.ISituationsService;

/**
 * Client-side service for making RPC calls to the remote deployments service.
 *
 * @author eric.wittmann@redhat.com
 */
@ApplicationScoped
public class SituationsRpcService {

    @Inject
    private Caller<ISituationsService> remoteSituationsService;

    /**
     * Constructor.
     */
    public SituationsRpcService() {
    }

    /**
     * @see org.overlord.rtgov.ui.client.shared.services.ISituationsService#search(SituationsFilterBean, int, String, boolean)
     */
    public void search(SituationsFilterBean filters, int page, String sortColumn, boolean ascending,
            final IRpcServiceInvocationHandler<SituationResultSetBean> handler) {
        // TODO only allow one search at a time.  If another search comes in before the previous one
        // finished, cancel the previous one.  In other words, only return the results of the *last*
        // search performed.
        RemoteCallback<SituationResultSetBean> successCallback = new DelegatingRemoteCallback<SituationResultSetBean>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            remoteSituationsService.call(successCallback, errorCallback).search(filters, page, sortColumn, ascending);
        } catch (UiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * @see org.overlord.rtgov.ui.client.shared.services.ISituationsService#get(String)
     */
    public void get(String situationId, IRpcServiceInvocationHandler<SituationBean> handler) {
        RemoteCallback<SituationBean> successCallback = new DelegatingRemoteCallback<SituationBean>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            remoteSituationsService.call(successCallback, errorCallback).get(situationId);
        } catch (UiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * @see org.overlord.rtgov.ui.client.shared.services.ISituationsService#resubmit(String,String)
     */
    public void resubmit(String situationId, String message, IRpcServiceInvocationHandler<Void> handler) {
        RemoteCallback<Void> successCallback = new DelegatingRemoteCallback<Void>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            remoteSituationsService.call(successCallback, errorCallback).resubmit(situationId, message);
        } catch (UiException e) {
            errorCallback.error(null, e);
        }
    }
    
    /**
     * @see org.overlord.rtgov.ui.client.shared.services.ISituationsService#resubmit(String,String)
     */
    public void resubmit(SituationsFilterBean situationsFilterBean, IRpcServiceInvocationHandler<BatchRetryResult> handler) {
        RemoteCallback<BatchRetryResult> successCallback = new DelegatingRemoteCallback<BatchRetryResult>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            remoteSituationsService.call(successCallback, errorCallback).resubmit(situationsFilterBean);
        } catch (UiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * @see org.overlord.rtgov.ui.client.shared.services.ISituationsService#assign(String)
     */
    public void assign(String situationId, IRpcServiceInvocationHandler<Void> handler) {
        RemoteCallback<Void> successCallback = new DelegatingRemoteCallback<Void>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            remoteSituationsService.call(successCallback, errorCallback).assign(situationId);
        } catch (UiException e) {
            errorCallback.error(null, e);
        }
    }
    
    /**
     * @see org.overlord.rtgov.ui.client.shared.services.ISituationsService#deassign(String)
     */
    public void close(String situationId, IRpcServiceInvocationHandler<Void> handler) {
        RemoteCallback<Void> successCallback = new DelegatingRemoteCallback<Void>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            remoteSituationsService.call(successCallback, errorCallback).deassign(situationId);
        } catch (UiException e) {
            errorCallback.error(null, e);
        }
    }

	/**
	 * @see org.overlord.rtgov.ui.client.shared.services.ISituationsService#updateResolutionState(String,
	 *      ResolutionState)
	 */
	public void updateResolutionState(String situationId, String resolutionState,
			IRpcServiceInvocationHandler<Void> handler) {
		RemoteCallback<Void> successCallback = new DelegatingRemoteCallback<Void>(handler);
		ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
		try {
			remoteSituationsService.call(successCallback, errorCallback).updateResolutionState(situationId,
					resolutionState);
		} catch (UiException e) {
			errorCallback.error(null, e);
		}
	}

}
