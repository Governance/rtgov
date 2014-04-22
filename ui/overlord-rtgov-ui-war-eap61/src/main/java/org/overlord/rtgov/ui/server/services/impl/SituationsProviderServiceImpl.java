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
package org.overlord.rtgov.ui.server.services.impl;

import java.io.OutputStream;
import java.util.Collections;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.overlord.rtgov.ui.client.model.BatchRetryResult;
import org.overlord.rtgov.ui.client.model.Constants;
import org.overlord.rtgov.ui.client.model.MessageBean;
import org.overlord.rtgov.ui.client.model.ResolutionState;
import org.overlord.rtgov.ui.client.model.SituationBean;
import org.overlord.rtgov.ui.client.model.SituationResultSetBean;
import org.overlord.rtgov.ui.client.model.SituationSummaryBean;
import org.overlord.rtgov.ui.client.model.SituationsFilterBean;
import org.overlord.rtgov.ui.client.model.UiException;
import org.overlord.rtgov.ui.provider.SituationsProvider;
import org.overlord.rtgov.ui.server.services.ISituationsServiceImpl;

/**
 * Concrete implementation of the faults service using RTGov situations.
 *
 */
@ApplicationScoped
@Alternative
public class SituationsProviderServiceImpl implements ISituationsServiceImpl {

	private static final int SITUATIONS_PER_PAGE=5;
	
	@Inject
	private SituationsProvider _provider;

    /**
     * Constructor.
     */
    public SituationsProviderServiceImpl() {
    }

    /**
     * @see org.overlord.rtgov.ui.server.services.ISituationsServiceImpl#search(org.overlord.rtgov.ui.client.model.SituationsFilterBean, int, java.lang.String, boolean)
     */
    @Override
    public SituationResultSetBean search(SituationsFilterBean filters, int page, String sortColumn,
            				final boolean ascending) throws UiException {        
        SituationResultSetBean rval = new SituationResultSetBean();
        java.util.List<SituationSummaryBean> situations = _provider.search(filters);
        
        int total=situations.size();
        
        // Sort based on column and direction
        if (sortColumn != null) {
        	java.util.Comparator<SituationSummaryBean> comp=null;
        	
        	if (sortColumn.equals(Constants.SORT_COLID_TIMESTAMP)) {
        		comp = new java.util.Comparator<SituationSummaryBean>() {

    				@Override
    				public int compare(SituationSummaryBean o1,
    						SituationSummaryBean o2) {
    					int ret=o1.getTimestamp().compareTo(o2.getTimestamp());
    					if (!ascending) {
    						ret = 0 - ret;
    					}
    					return ret;
    				}
            	};
        	} else if (sortColumn.equals(Constants.SORT_COLID_SUBJECT)) {
        		comp = new java.util.Comparator<SituationSummaryBean>() {

    				@Override
    				public int compare(SituationSummaryBean o1,
    						SituationSummaryBean o2) {
    					int ret=o1.getSubject().compareTo(o2.getSubject());
    					if (!ascending) {
    						ret = 0 - ret;
    					}
    					return ret;
    				}
            	};
			} else if (sortColumn.equals(Constants.SORT_COLID_RESOLUTION_STATE)) {
				comp = new java.util.Comparator<SituationSummaryBean>() {

					@Override
					public int compare(SituationSummaryBean o1, SituationSummaryBean o2) {
						int ret = o1.getResolutionState().compareTo(o2.getResolutionState());
						if (!ascending) {
							ret = 0 - ret;
						}
						return ret;
					}
				};
        	} else if (sortColumn.equals(Constants.SORT_COLID_TYPE)) {
        		comp = new java.util.Comparator<SituationSummaryBean>() {

    				@Override
    				public int compare(SituationSummaryBean o1,
    						SituationSummaryBean o2) {
    					int ret=o1.getType().compareTo(o2.getType());
    					if (!ascending) {
    						ret = 0 - ret;
    					}
    					return ret;
    				}
            	};
        	}

        	if (comp != null) {
        		Collections.sort(situations, comp);
        	}
        }

        int startIndex=(SITUATIONS_PER_PAGE*(page-1));
       
        if (situations.size() >= startIndex) {
        	// Remove initial entries
        	for (int i=0; i < startIndex; i++) {
        		situations.remove(0);
        	}
        	
        	// Remove trailing entries
        	while (situations.size() > SITUATIONS_PER_PAGE) {
        		situations.remove(SITUATIONS_PER_PAGE);
        	}
        	
        } else {
        	situations.clear();
        }
        
        rval.setSituations(situations);
        rval.setItemsPerPage(SITUATIONS_PER_PAGE);
        rval.setStartIndex((page-1)*SITUATIONS_PER_PAGE);
        rval.setTotalResults(total);

        return (rval);
    }

    /**
     * @see org.overlord.rtgov.ui.server.services.ISituationsServiceImpl#getService(java.lang.String)
     */
    @Override
    public SituationBean get(String situationId) throws UiException {
    	return (_provider.getSituation(situationId));
    }

    /**
     * @see org.overlord.rtgov.ui.server.services.ISituationsServiceImpl#resubmit(java.lang.String, java.lang.String)
     */
    @Override
    public void resubmit(String situationId, String message) throws UiException {
    	MessageBean mb=new MessageBean();
    	mb.setContent(message);
    	
    	_provider.resubmit(situationId, mb);
    }
    
    /**
     * @see org.overlord.rtgov.ui.server.services.ISituationsServiceImpl#resubmit(SituationsFilterBean)
     */
    @Override
    public BatchRetryResult resubmit(SituationsFilterBean situationsFilterBean) throws UiException {
        return _provider.resubmit(situationsFilterBean);
    }

	@Override
	public void assign(String situationId, String userName) throws UiException {
		_provider.assign(situationId, userName);
	}

	@Override
	public void close(String situationId) throws UiException {
		_provider.close(situationId);
	}

	@Override
	public void updateResolutionState(String situationId,
			ResolutionState resolutionState) throws UiException {
		_provider.updateResolutionState(situationId, resolutionState);
	}

    @Override
    public void export(SituationsFilterBean situationsFilterBean, OutputStream outputStream) {
        _provider.export(situationsFilterBean, outputStream);
    }
}
