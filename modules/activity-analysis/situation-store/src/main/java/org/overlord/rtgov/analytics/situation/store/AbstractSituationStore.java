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

import static java.lang.System.currentTimeMillis;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.overlord.rtgov.analytics.situation.Situation;

/**
 * This class provides an abstract base implementation of the SituationsStore interface.
 *
 */
public abstract class AbstractSituationStore implements SituationStore {

    private static final Logger LOG=Logger.getLogger(AbstractSituationStore.class.getName());
    
    /**
     * The situation repository constructor.
     */
    public AbstractSituationStore() {
    }
    
    /**
     * {@inheritDoc}
     */
    public void store(Situation situation) throws Exception {
        
        processResubmittedSituation(situation);
        
        doStore(situation);
    }
    
    /**
     * {@inheritDoc}
     */
    public Situation getSituation(final String id) {
        return (doGetSituation(id));
    }
    
    protected abstract Situation doGetSituation(String id);

    /**
     * This method checks whether the situation is the result of a resubmission
     * failure, and updates the parent situation accordingly.
     * 
     * @param situation The situation
     * @throws Exception Failed to process
     */
    protected void processResubmittedSituation(Situation situation) throws Exception {
        // RTGOV-649 Check if situation has a parent, and if so copy the
        // assignment value, set the resolution state to IN_PROGRESS, and
        // set the parent's resolution state to IN_PROGRESS.
        if (situation.getSituationProperties().containsKey(Situation.RESUBMITTED_SITUATION_ID)) {
            String parentId=situation.getSituationProperties().get(Situation.RESUBMITTED_SITUATION_ID);
            
            Situation parent=doGetSituation(parentId);
            
            if (parent != null) {
                situation.getSituationProperties().put(ASSIGNED_TO_PROPERTY,
                        parent.getSituationProperties().get(ASSIGNED_TO_PROPERTY));
                situation.getSituationProperties().put(RESOLUTION_STATE_PROPERTY,
                        ResolutionState.IN_PROGRESS.name());
                updateResolutionState(parent.getId(), ResolutionState.IN_PROGRESS);
            }
        }
    }
    
    /**
     * This method implements the 'store' functionality.
     * 
     * @param situation The situation to store
     * @throws Exception Failed to store
     */
    protected abstract void doStore(Situation situation) throws Exception;
    
    /**
     * Assign the situation to the user.
     * 
     * @param situationId The id
     * @param userName The user
     * @param context The context
     */
    protected void doAssignSituation(final String situationId, final String userName,
                                final SituationContext context) {
        SituationsAction action=new SituationsAction() {
            @Override
            public boolean perform(Situation situation) {
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("Assign situation '"+situation.getId()+"' for userName: "+userName); //$NON-NLS-1$
                }
                situation.getSituationProperties().put(ASSIGNED_TO_PROPERTY, userName);
                return (true);
            }
        };
        
        Situation root=getRootSituation(situationId);
        
        if (root != null) {
            performAction(root, action, context);
        }
    }

    /**
     * Unassign the situation.
     * 
     * @param situationId Id
     * @param context The context
     */
    protected void doUnassignSituation(final String situationId, final SituationContext context) {
        SituationsAction action=new SituationsAction() {
            @Override
            public boolean perform(Situation situation) {               
                java.util.Map<String, String> properties = situation.getSituationProperties();
                properties.remove(ASSIGNED_TO_PROPERTY);
                // remove current state if not already resolved
                String resolutionState = properties.get(RESOLUTION_STATE_PROPERTY);
                if (resolutionState != null && ResolutionState.RESOLVED != ResolutionState.valueOf(resolutionState)) {
                    properties.remove(RESOLUTION_STATE_PROPERTY);
                }
                return (true);
            }
        };
        
        Situation root=getRootSituation(situationId);
        
        if (root != null) {
            performAction(root, action, context);
        }
    }

    /**
     * Update the resolution state.
     * 
     * @param situationId The id
     * @param resolutionState The state
     * @param context The context
     */
    protected void doUpdateResolutionState(final String situationId, final ResolutionState resolutionState,
                            final SituationContext context) {
        SituationsAction action=new SituationsAction() {
            @Override
            public boolean perform(Situation situation) {
                situation.getSituationProperties().put(RESOLUTION_STATE_PROPERTY, resolutionState.name());               
                return (true);
            }
        };
        
        Situation root=getRootSituation(situationId);
        
        if (root != null) {
            performAction(root, action, context);
        }
    }

    protected void performAction(Situation root, SituationsAction action, SituationContext context) {
        if (action.perform(root)) {
            context.put(root);
        }
        
        // Check if situation has child situations
        java.util.List<Situation> resubmitted=getResubmittedSituations(root.getId(), true);
        
        for (Situation sit : resubmitted) {
            if (action.perform(sit)) {
                context.put(sit);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public java.util.List<Situation> getResubmittedSituations(String situationId, boolean deep) {
        java.util.List<Situation> results=new java.util.ArrayList<Situation>();
        
        queryResubmittedSituations(situationId, deep, results);
        
        return (results);
    }
    
    /**
     * This method queries the situation store to obtain situations resubmitted
     * by the situation associated with the supplied id.
     * 
     * @param situationId The parent situation id
     * @param deep Whether this should be done recursively
     * @param results The list of situations
     */
    protected void queryResubmittedSituations(String situationId, boolean deep,
                                        java.util.List<Situation> results) {
        SituationsQuery query=new SituationsQuery();
        query.getProperties().put(Situation.RESUBMITTED_SITUATION_ID, situationId);
        
        java.util.List<Situation> resubmitted=getSituations(query);
        
        for (Situation sit : resubmitted) {
            
            // Need to double check id, as fuzzy 'like' used when retrieving situations based on
            // properties to enable partial strings to be provided.
            if (!sit.getSituationProperties().containsKey(Situation.RESUBMITTED_SITUATION_ID)
                    || !sit.getSituationProperties().get(Situation.RESUBMITTED_SITUATION_ID)
                                .equals(situationId)) {
                continue;
            }
            
            if (!results.contains(sit)) {
                results.add(sit);

                if (deep) {
                    queryResubmittedSituations(sit.getId(), deep, results);
                }
            }
        }
    }

    /**
     * This method returns the root situation associated with
     * a resubmit hierarchy (i.e. where one situation is created
     * as the result of a resubmission failure from a previous
     * situation).
     * 
     * @param situationId The situation id
     * @return The root situation, or null if this situation does
     *        not have a resubmitted situation
     */
    protected Situation getRootSituation(String situationId) {
        return (getRootSituation(getSituation(situationId)));
    }
    
    /**
     * This method returns the root situation associated with
     * a resubmit hierarchy (i.e. where one situation is created
     * as the result of a resubmission failure from a previous
     * situation).
     * 
     * @param original The original situation
     * @return The root situation, or null if this situation does
     *        not have a resubmitted situation
     */
    protected Situation getRootSituation(Situation original) {
        Situation root=original;
        
        if (original != null) {
            String parentId=original.getSituationProperties().get(Situation.RESUBMITTED_SITUATION_ID);
            
            while (parentId != null) {
                root = getSituation(parentId);
                
                if (root == null) {
                    // Failed to retrieve parent situation
                    throw new RuntimeException("Failed to locate parent situation");
                }
                
                parentId = root.getSituationProperties().get(Situation.RESUBMITTED_SITUATION_ID);
            }
        }
        
        return (root);
    }
    
    @Override
    public void recordSuccessfulResubmit(final String situationId, final String userName) {
        doRecordSuccessfulResubmit(getSituation(situationId), userName);
    }

    /**
     * Record successful resubmit.
     * 
     * @param situationId The id
     */
    protected void doRecordSuccessfulResubmit(final Situation situation, final String userName) {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Record successful resubmit: situationId="+situation.getId()+" userName="+userName); //$NON-NLS-1$
        }
        Map<String, String> properties = situation.getSituationProperties();
        properties.put(RESUBMIT_BY_PROPERTY, userName);
        properties.put(RESUBMIT_AT_PROPERTY, Long.toString(currentTimeMillis()));
        properties.put(RESUBMIT_RESULT_PROPERTY, RESUBMIT_RESULT_SUCCESS);
        properties.remove(RESUBMIT_ERROR_MESSAGE);
    }

    @Override
    public void recordResubmitFailure(final String situationId, final String errorMessage,
                                final String userName) {
        doRecordResubmitFailure(getSituation(situationId), errorMessage, userName);
    }
    
    /**
     * Record resubmit failure.
     * 
     * @param situationId The id
     * @param errorMessage The error
     * @param userName The optional user id who resubmitted the message
     */
    protected void doRecordResubmitFailure(final Situation situation, final String errorMessage,
                                final String userName) {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Record unsuccessful resubmit: situationId="+situation.getId()+" userName="+userName); //$NON-NLS-1$
        }
        // RTGOV-499: Use deprecated method until no longer needing to support FSW6.0
        Map<String, String> properties = situation.getSituationProperties();
        properties.put(RESUBMIT_BY_PROPERTY, userName);
        properties.put(RESUBMIT_AT_PROPERTY, Long.toString(currentTimeMillis()));
        properties.put(RESUBMIT_RESULT_PROPERTY, RESUBMIT_RESULT_ERROR);
        properties.put(RESUBMIT_ERROR_MESSAGE, errorMessage);
    }
    
    @Override
    public int delete(final SituationsQuery situationQuery) {
        return (doDelete(situationQuery));
    }
    
    /**
     * This method deletes the situations that meet the supplied query.
     * 
     * @param situationQuery The query
     * @return The number of deleted situations
     */
    protected int doDelete(final SituationsQuery situationQuery) {
        java.util.List<Situation> situations=getSituations(situationQuery);
        for (Situation situation : situations) {
            doDelete(situation);
        }
        
        return (situations.size());
    }
    
    @Override
    public void delete(final Situation situation) {
        doDelete(situation);
    }

    /**
     * This method deletes the specified situation.
     * 
     * @param situation The situation
     */
    protected abstract void doDelete(Situation situation);
    
    /**
     * Interface for performing an action on a situation.
     *
     */
    public interface SituationsAction {

        /**
         * This method performs the action on a specified situation.
         * 
         * @param situation The situation
         * @return Whether the situation was changed
         */
        public boolean perform(Situation situation);
    }

    /**
     * This interface represents a context for retrieving and
     * storing situations.
     *
     */
    public interface SituationContext {
        
        /**
         * This method retrieves a situation with a specified
         * id.
         * 
         * @param id The situation id
         * @return The situation, or null if not found
         */
        public Situation get(String id);
        
        /**
         * This method stores the supplied situation.
         * 
         * @param situation The situation
         */
        public void put(Situation situation);
        
    }
}
