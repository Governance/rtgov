/*
 * Copyright 2013-4 Red Hat Inc
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
package org.overlord.rtgov.ui.provider;

import java.io.OutputStream;

import org.overlord.rtgov.ui.client.model.BatchRetryResult;
import org.overlord.rtgov.ui.client.model.MessageBean;
import org.overlord.rtgov.ui.client.model.ResolutionState;
import org.overlord.rtgov.ui.client.model.SituationBean;
import org.overlord.rtgov.ui.client.model.SituationSummaryBean;
import org.overlord.rtgov.ui.client.model.SituationsFilterBean;
import org.overlord.rtgov.ui.client.model.UiException;

/**
 * This interface represents a provider that can supply information about
 * 'Situations'.
 *
 */
public interface SituationsProvider {
	
	/**
	 * This method returns the name of the provider.
	 * 
	 * @return The provider name
	 */
	public String getName();
	
	/**
	 * This method adds a new situation event listener.
	 * 
	 * @param l The situation event listener
	 */
	public void addSituationEventListener(SituationEventListener l);
	
	/**
	 * This method removes a situation event listener.
	 * 
	 * @param l The situation event listener
	 */
	public void removeSituationEventListener(SituationEventListener l);
	
    /**
     * Search for services using the given filters and search text.
     * 
     * @param filters The filters
     * @return The list of situation summaries
     * @throws UiException Failed to search situations
     */
    public java.util.List<SituationSummaryBean> search(SituationsFilterBean filters) throws UiException;

    /**
     * Fetches a full situation by its id.
     * 
     * @param id The situation id
     * @throws UiException Failed to retrieve the situation
     */
    public SituationBean getSituation(String id) throws UiException;

    /**
     * This method resubmits the supplied message to the service associated
     * with the identified situation.
     * 
     * @param situationId The situation id
     * @param message The message
     * @throws UiException
     */
    public void resubmit(String situationId, MessageBean message) throws UiException;

    /**
     * This method export all situation's matching the given filter criteria
     * 
     * @param situationsFilterBean
     * @param outputStream
     *            Failed to search situations
     */
    public void export(SituationsFilterBean situationsFilterBean, OutputStream outputStream);

    /**
     * This method resubmits all situation's matching the given filter criteria
     * 
     * @param filters The filters
     * @return The {@link BatchRetryResult}
     * @throws UiException
     *             Failed to search situations
     */
    public BatchRetryResult resubmit(SituationsFilterBean situationsFilterBean) throws UiException;
  
	public void assign(final String situationId, final String userName) throws UiException;
	
	public void close(final String situationId) throws UiException;
	
	public void updateResolutionState(final String situationId, final ResolutionState resolutionState)
									throws UiException;

}
