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

import org.jboss.errai.bus.server.annotations.Remote;
import org.overlord.rtgov.ui.client.model.BatchRetryResult;
import org.overlord.rtgov.ui.client.model.SituationBean;
import org.overlord.rtgov.ui.client.model.SituationResultSetBean;
import org.overlord.rtgov.ui.client.model.SituationsFilterBean;
import org.overlord.rtgov.ui.client.model.UiException;

/**
 * Provides a way to manage situations.
 *
 * @author eric.wittmann@redhat.com
 */
@Remote
public interface ISituationsService {

    /**
     * Search for situations using the given filters and search text.
     * @param filters
     * @param page
     * @param sortColumn
     * @param ascending
     * @throws UiException
     */
    public SituationResultSetBean search(SituationsFilterBean filters, int page, String sortColumn, boolean ascending) throws UiException;

    /**
     * Fetches a full situation by its name.
     * @param situationId
     * @throws UiException
     */
    public SituationBean get(String situationId) throws UiException;
    
    /**
     * Resubmits a message.
     * @param situationId
     * @param message
     * @throws UiException
     */
    public void resubmit(String situationId, String message) throws UiException;
    /**
     * Assign a situation to the current uUser.
     * @param situationId
     * @throws UiException
     */
    public void assign(String situationId) throws UiException;
    /**
     * Deassign a situation from an assigned user.
     * @param situationId
     * @throws UiException
     */
    public void deassign(String situationId) throws UiException;

    /**
     * Updates a situation with the given resolutionState.
     * @param situationId
     * @throws UiException
     */
	public void updateResolutionState(String situationId, String resolutionState) throws UiException;

	/**
     * Resubmits all situation's matching the given filter.
     * @param situationsFilterBean
     * @throws UiException
     */
    public BatchRetryResult resubmit(SituationsFilterBean situationsFilterBean) throws UiException;

}
