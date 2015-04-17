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

/**
 * This interface provides access to the Situation store.
 *
 */
public interface SituationStore {

    /**
     * Host property.
     */
    public static final String HOST_PROPERTY = "host";

    /**
     * Resolution state property.
     */
    public static final String RESOLUTION_STATE_PROPERTY = "resolutionState";

    /**
     * Situation 'assigned to' property.
     */
    public static final String ASSIGNED_TO_PROPERTY = "assignedTo";

    /**
     * Resubmit error message property.
     */
    public static final String RESUBMIT_ERROR_MESSAGE = "resubmitErrorMessage";

    /**
     * Resubmit result property.
     */
    public static final String RESUBMIT_RESULT_PROPERTY = "resubmitResult";

    /**
     * Resubmit 'at' property.
     */
    public static final String RESUBMIT_AT_PROPERTY = "resubmitAt";

    /**
     * Resubmit 'by' property.
     */
    public static final String RESUBMIT_BY_PROPERTY = "resubmitBy";

    /**
     * Resubmit result error property.
     */
    public static final String RESUBMIT_RESULT_ERROR = "Error";

    /**
     * Resubmit result successful property.
     */
    public static final String RESUBMIT_RESULT_SUCCESS = "Success";


    /**
     * This method persists the supplied situation.
     * 
     * @param situation The situation
     * @throws Exception Failed to store situation
     */
    public void store(Situation situation) throws Exception;

    /**
     * This method returns the situation associated with the supplied id.
     *
     * @param id The id
     * @return The situation, or null if not found
     */
    public Situation getSituation(String id);

    /**
     * This method returns the list of situations that meet the criteria
     * specified in the query.
     * 
     * @param query The situations query
     * @return The list of situations
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
     * This method unassigns a situation.
     * 
     * @param situationId The situation id
     * @throws Exception Failed to unassign the situation
     */
    public void unassignSituation(String situationId) throws Exception;
    
    /**
     * This method updates the resolution state of a situation.
     * 
     * @param situationId The situation id
     * @param resolutionState The resolution state
     * @throws Exception Failed to update the resolution state
     */
    public void updateResolutionState(String situationId, ResolutionState resolutionState) throws Exception;

    /**
     * Record's a successful resubmit of the situation matching the given id.
     *
     * @param situationId The situation id
     * @param userName The optional user id who resubmitted the message for this situation
     */
    public void recordSuccessfulResubmit(String situationId, String userName);

    /**
     * Record's a failed resubmit of the situation matching the given id.
     *
     * @param situationId
     *            The situation id
     * @param message
     *            The exception message
     * @param userName The optional user id who resubmitted the message for this situation
     */
    public void recordResubmitFailure(String situationId, String message, String userName);

    /**
     * This method returns the resubmitted situations.
     * 
     * @param situationId The parent situation id
     * @param deep Whether to traverse the tree (true) or just return the immediate child situations (false)
     * @return The list of resubmitted situations
     */
    public java.util.List<Situation> getResubmittedSituations(String situationId, boolean deep);
    
    /**
     * This method deletes all situations that meet the criteria
     * specified in the query.
     * 
     * @param query The situations query
     * @return The number of deleted situations
     */
    public int delete(SituationsQuery query);

    /**
     * This method deletes the supplied situation.
     * 
     * @param situation The situation
     */
    public void delete(Situation situation);
    
}
