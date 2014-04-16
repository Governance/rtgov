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

import static org.jboss.errai.bus.server.api.RpcContext.getServletRequest;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.jboss.errai.bus.server.annotations.Service;
import org.overlord.rtgov.ui.client.model.BatchRetryResult;
import org.overlord.rtgov.ui.client.model.ResolutionState;
import org.overlord.rtgov.ui.client.model.SituationBean;
import org.overlord.rtgov.ui.client.model.SituationResultSetBean;
import org.overlord.rtgov.ui.client.model.SituationsFilterBean;
import org.overlord.rtgov.ui.client.model.UiException;
import org.overlord.rtgov.ui.client.shared.services.ISituationsService;
import org.overlord.rtgov.ui.server.interceptors.IUserContext;

/**
 * Concrete implementation of the situations service.
 *
 * @author eric.wittmann@redhat.com
 */
@Service
@IUserContext.Binding
public class SituationsService implements ISituationsService {

    @Inject ISituationsServiceImpl impl;

    /**
     * Constructor.
     */
    public SituationsService() {
    }
    
    /**
     * @see org.overlord.rtgov.ui.client.shared.services.ISituationsService#search(org.overlord.rtgov.ui.client.model.SituationsFilterBean, int, java.lang.String, boolean)
     */
    @Override
    public SituationResultSetBean search(SituationsFilterBean filters, int page, String sortColumn,
            boolean ascending) throws UiException {
        return impl.search(filters, page, sortColumn, ascending);
    }

    /**
     * @see org.overlord.ISituationsService.ui.client.shared.services.ISituationsService#getService(java.lang.String)
     */
	@Override
	public SituationBean get(String situationId) throws UiException {
		HttpServletRequest servletRequest = (HttpServletRequest) getServletRequest();
		SituationBean situationBean = impl.get(situationId);
		if (situationBean.getAssignedTo() != null
				&& situationBean.getAssignedTo().equals(servletRequest.getRemoteUser())) {
			situationBean.setAssignedToCurrentUser(true);
		}
		if (servletRequest.isUserInRole("ROLE_ADMIN")) {
			situationBean.setTakeoverPossible(true);
		}
		return situationBean;
	}

	/**
     * @see org.overlord.rtgov.ui.client.shared.services.ISituationsService#resubmit(java.lang.String, java.lang.String)
     */
    @Override
    public void resubmit(String situationId, String message) throws UiException {
        impl.resubmit(situationId, message);
    }

	@Override
	public void assign(String situationId) throws UiException {
		HttpServletRequest servletRequest = (HttpServletRequest) getServletRequest();
		impl.assign(situationId, servletRequest.getRemoteUser());
	}

	@Override
	public void deassign(String situationId) throws UiException {
		impl.close(situationId);
	}

	@Override
	public void updateResolutionState(String situationId, String resolutionState) throws UiException {
		impl.updateResolutionState(situationId, ResolutionState.valueOf(resolutionState));
	}

    @Override
    public BatchRetryResult resubmit(SituationsFilterBean situationsFilterBean) throws UiException {
        return impl.resubmit(situationsFilterBean);
    }

}
