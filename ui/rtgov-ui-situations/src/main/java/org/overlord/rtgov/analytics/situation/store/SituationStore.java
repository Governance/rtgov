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
package org.overlord.rtgov.analytics.situation.store;

import org.overlord.rtgov.analytics.situation.Situation;
import org.overlord.rtgov.ui.client.model.ResolutionState;

/**
 * This interface provides access to the Situation store.
 *
 */
public interface SituationStore {

	public static final String HOST_PROPERTY = "host";
	public static final String RESOLUTION_STATE_PROPERTY = "resolutionState";
	public static final String ASSIGNED_TO_PROPERTY = "assignedTo";
	public static final String RESUBMIT_ERROR_MESSAGE = "resubmitErrorMessage";
	public static final String RESUBMIT_RESULT_PROPERTY = "resubmitResult";
	public static final String RESUBMIT_AT_PROPERTY = "resubmitAt";
	public static final String RESUBMIT_BY_PROPERTY = "resubmitBy";
	public static final String RESUBMIT_RESULT_ERROR = "Error";
	public static final String RESUBMIT_RESULT_SUCCESS = "Success";



	/**
     * This method returns the situation associated with the supplied id.
     *
     * @param id The id
     * @return The situation, or null if not found
     * @throws Exception Failed to get situation
     */
    public Situation getSituation(String id);

    /**
     * This method returns the list of situations that meet the criteria
     * specified in the query.
     * 
     * @param query The situations query
     * @return The list of situations
     * @throws Exception Failed to get situations
     */
    public java.util.List<Situation> getSituations(SituationsQuery query);

    /**
     * This method assigns a situation to a specified user.
     * 
     * @param situationId The situation id
     * @param userName The user
     * @throws Exception Failed to assign the situation
     */
    public void assignSituation(String situationId, String userName) throws Exception;
    
    /**
     * This method closes a situation.
     * 
     * @param situationId The situation id
     * @throws Exception Failed to close the situation
     */
    public void closeSituation(String situationId) throws Exception;
    
    /**
     * This method updates the resolution state of a situation.
     * 
     * @param situationId The situation id
     * @param resolutionState The resolution state
     * @throws Exception Failed to update the resolution state
     */
    public void updateResolutionState(String situationId, ResolutionState resolutionState) throws Exception;

	/**
	 * Record's a successful resubmit of the situation matching the given id
	 *
	 * @param situationId
	 *            The situation id
	 */
	public void recordSuccessfulResubmit(String situationId);

	/**
	 * Record's a failed resubmit of the situation matching the given id
	 *
	 * @param situationId
	 *            The situation id
	 * @param message
	 *            The exception message
	 */
	public void recordResubmitFailure(String situationId, String message);


}
