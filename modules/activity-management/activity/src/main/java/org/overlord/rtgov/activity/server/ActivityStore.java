/*
 * 2012-3 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.rtgov.activity.server;

import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.model.Context;

/**
 * This interface represents a persistence storage for Activity
 * Events.
 *
 */
public interface ActivityStore {

    /**
     * This method stores the list of activity events.
     * 
     * @param activities The list of activity events to store
     * @throws Exception Failed to store events
     */
    public void store(java.util.List<ActivityUnit> activities) throws Exception;
    
    /**
     * This method queries the persistent store for an activity unit
     * with the supplied id.
     * 
     * @param id The activity unit id
     * @return The activity unit, or null if not found
     * @throws Exception Failed to query activity unit
     */
    public ActivityUnit getActivityUnit(String id) throws Exception;
    
    /**
     * This method retrieves a set of activity events associated
     * with the supplied context value.
     * 
     * @param context The context value
     * @return The list of activities
     * @throws Exception Failed to retrieve the activities
     */
    public java.util.List<ActivityType> getActivityTypes(Context context) throws Exception;
    
    /**
     * This method retrieves a set of activity events associated
     * with the supplied context value and date/time range.
     * 
     * @param context The context value
     * @param from The 'from' timestamp
     * @param to The 'to' timestamp
     * @return The list of activities
     * @throws Exception Failed to retrieve the activities
     */
    public java.util.List<ActivityType> getActivityTypes(Context context, long from, long to) throws Exception;
    
    /**
     * This method queries the persistent store for activity events
     * that satisfy the supplied query.
     * 
     * @param query The query
     * @return The list of activities that satisfy the query
     * @throws Exception Failed to query events
     */
    public java.util.List<ActivityType> query(QuerySpec query) throws Exception;
    
}
