/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-12, Red Hat Middleware LLC, and others contributors as indicated
 * by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.overlord.bam.call.trace;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.overlord.bam.activity.model.ActivityType;
import org.overlord.bam.activity.model.ActivityUnit;
import org.overlord.bam.activity.model.Context;
import org.overlord.bam.activity.server.ActivityServer;
import org.overlord.bam.call.trace.model.TraceNode;

/**
 * This class is responsible for deriving a call trace from
 * activity information.
 *
 */
public class CallTraceProcessor {
    
    private static final Logger LOG=Logger.getLogger(CallTraceProcessor.class.getName());

    private ActivityServer _activityServer=null;
    
    /**
     * This method sets the activity server.
     * 
     * @param as The activity server
     */
    public void setActivityServer(ActivityServer as) {
        _activityServer = as;
    }
    
    /**
     * This method gets the activity server.
     * 
     * @return The activity server
     */
    public ActivityServer getActivityServer() {
        return (_activityServer);
    }
    
    /**
     * This method creates a call trace associated with the
     * supplied correlation value.
     * 
     * @param correlation The correlation value
     * @return The call trace, or null if not found
     * @throws Exception Failed to create call trace
     */
    public TraceNode createCallTrace(String correlation) 
                            throws Exception {
        TraceNode ret=null;
        CTState state=new CTState();
        
        // Recursively load activity units that are directly or
        // indirectly associated with the correlation key
        loadActivityUnits(state, correlation);
        
        return (ret);
    }
    
    /**
     * This method loads activity units associated with the supplied
     * correlation key.
     * 
     * @param state The state
     * @param correlation The correlation key
     */
    protected void loadActivityUnits(CTState state, String correlation) {
        
        if (!state.isCorrelationInitialized(correlation)) {
            
            // Retrieve activity types associated with correlation
            try {
                java.util.List<ActivityType> ats=
                        _activityServer.getActivityTypes(correlation);
                
                // Check each activity type's unit id to see whether
                // it needs to be retrieved
                java.util.List<ActivityUnit> aus=
                        new java.util.ArrayList<ActivityUnit>();
                
                for (ActivityType at : ats) {
                    if (!state.isActivityUnitLoaded(at.getUnitId())) {
                        ActivityUnit au=_activityServer.getActivityUnit(at.getUnitId());
                        
                        aus.add(au);
                        
                        // Add to state
                        state.add(au);
                    }
                }
                
                // Mark this correlation value as initialized
                state.initialized(correlation);

                // For each new activity unit, scan for unknown correlation
                // fields, and recursively load their associated units
                for (ActivityUnit au : aus) {                    
                    for (ActivityType at : au.getActivityTypes()) {
                        for (Context c : at.getContext()) {                            
                            loadActivityUnits(state, c.getValue());
                        }
                    }
                }
                
            } catch (Exception e) {
                LOG.log(Level.SEVERE, MessageFormat.format(
                        java.util.PropertyResourceBundle.getBundle(
                        "call-trace.Messages").getString("CALL-TRACE-1"),
                            correlation), e);
            }
        }
    }
    
    /**
     * This class maintains state information associated with the
     * derivation of a call trace.
     *
     */
    public static class CTState {
        
        private java.util.List<String> _correlations=new java.util.ArrayList<String>();
        private java.util.List<ActivityUnit> _units=new java.util.ArrayList<ActivityUnit>();
        private java.util.Map<String,ActivityUnit> _unitIndex=new java.util.HashMap<String,ActivityUnit>();
        
        /**
         * This method determines whether the supplied correlation
         * key is already initialized.
         * 
         * @param correlation The correlation to check
         * @return Whether the correlation key is already initialized
         */
        public boolean isCorrelationInitialized(String correlation) {
            return (_correlations.contains(correlation));
        }
        
        /**
         * This method indicates that the supplied correlation
         * key has now been initialized.
         * 
         * @param correlation The correlation key
         */
        public void initialized(String correlation) {
            if (!_correlations.contains(correlation)) {
                _correlations.add(correlation);
            }
        }
        
        /**
         * This method determines whether the activity unit,
         * associated with the supplied id, has already been loaded.
         * 
         * @param id The id
         * @return Whether the activity unit has been loaded
         */
        public boolean isActivityUnitLoaded(String id) {
            return (_unitIndex.containsKey(id));
        }
        
        /**
         * This method adds the supplied activity unit to the
         * state information.
         * 
         * @param au The activity unit
         */
        public void add(ActivityUnit au) {
            _units.add(au);
            _unitIndex.put(au.getId(), au);
        }
        
        /**
         * This method returns the list of activity units.
         * 
         * @return The activity units
         */
        public java.util.List<ActivityUnit> getActivityUnits() {
            return (_units);
        }
        
        /**
         * This method sorts the list of activity units
         * based on time.
         */
        public void sortActivityUnitsByTime() {
            Collections.sort(_units, new Comparator<ActivityUnit>() {

                public int compare(ActivityUnit o1, ActivityUnit o2) {
                    return ((int)(o1.getActivityTypes().get(0).getTimestamp()-
                            o2.getActivityTypes().get(0).getTimestamp()));
                }
                
            });
        }
    }
}
