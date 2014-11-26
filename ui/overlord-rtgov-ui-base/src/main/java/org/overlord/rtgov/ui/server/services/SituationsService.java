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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.overlord.rtgov.ui.client.model.ResolutionState;
import org.overlord.rtgov.ui.client.model.BatchRetryResult;
import org.overlord.rtgov.ui.client.model.SituationBean;
import org.overlord.rtgov.ui.client.model.SituationResultSetBean;
import org.overlord.rtgov.ui.client.model.SituationsFilterBean;
import org.overlord.rtgov.ui.client.model.UiException;
import org.overlord.rtgov.ui.client.shared.services.ISituationsService;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Concrete implementation of the situations service.
 *
 * @author eric.wittmann@redhat.com
 */
public class SituationsService implements ISituationsService {

    private static final Logger LOG=Logger.getLogger(SituationsService.class.getName());
    
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    @Inject ISituationsServiceImpl impl;
    
    @Context SecurityContext securityContext;
    
    private Cache<String, SituationsFilterBean> exportFilterCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES).expireAfterAccess(1, TimeUnit.SECONDS)
            .<String, SituationsFilterBean> build();

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
		SituationBean situationBean = impl.get(situationId);
		if (situationBean.assignedTo() != null
				&& situationBean.assignedTo().equals(securityContext.getUserPrincipal().getName())) {
			situationBean.setAssignedToCurrentUser(true);
		}
		if (securityContext.isUserInRole("ROLE_ADMIN")) {
			situationBean.setTakeoverPossible(true);
		}
		return situationBean;
	}

	/**
     * @see org.overlord.rtgov.ui.client.shared.services.ISituationsService#resubmit(java.lang.String, java.lang.String)
     */
    @Override
    public void resubmit(String situationId, String message) throws UiException {
        impl.resubmit(situationId, message, securityContext.getUserPrincipal().getName());
    }

	@Override
	public void assign(String situationId) throws UiException {
		impl.assign(situationId, securityContext.getUserPrincipal().getName());
	}

	@Override
	public void unassign(String situationId) throws UiException {
		impl.unassign(situationId);
	}

	@Override
	public void updateResolutionState(String situationId, String resolutionState) throws UiException {
		impl.updateResolutionState(situationId, ResolutionState.valueOf(resolutionState));
	}

    @Override
    public BatchRetryResult resubmit(SituationsFilterBean situationsFilterBean) throws UiException {
        return impl.resubmit(situationsFilterBean, securityContext.getUserPrincipal().getName());
    }

    @Override
    public int delete(SituationsFilterBean situationsFilterBean) throws UiException {
        return impl.delete(situationsFilterBean);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String filter(SituationsFilterBean situationsFilterBean) throws UiException {
        String id=UUID.randomUUID().toString();
        exportFilterCache.put(id, situationsFilterBean);
        return (id);
    }
    
    /**
     * {@inheritDoc}
     */
    public Response export(String id) throws UiException {
        SituationsFilterBean situationsFilterBean = exportFilterCache.getIfPresent(id);
        String exportedText="";
        
        if (situationsFilterBean != null) {
            java.io.ByteArrayOutputStream os=new java.io.ByteArrayOutputStream();
            
            try {
                impl.export(situationsFilterBean, os);  
            } finally {
                try {
                    os.close();
                } catch (IOException e) {
                    LOG.log(Level.WARNING, "Failed to close export output stream", e);
                }
            }
            
            exportedText = new String(os.toByteArray());
        }
        
        return (Response.ok(exportedText)
                .type(MediaType.TEXT_PLAIN_TYPE)
                .header("Content-Disposition", "attachment; filename=situations_export_"
                        + new SimpleDateFormat(DATE_TIME_FORMAT).format(new Date()) + ".txt")
                .build());
    }
}
