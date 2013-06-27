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
package org.overlord.rtgov.activity.collector;

import org.overlord.rtgov.activity.model.ActivityType;

/**
 * This interface represents an activity event collector.
 *
 */
public interface ActivityCollector {

    /**
     * This method indicates whether activity collection is
     * currently enabled.
     * 
     * @return Whether collection is enabled
     */
    public boolean isCollectionEnabled();
    
    /**
     * This method can be used to create an application controlled
     * scope. Within this scope, all activity types will
     * be recorded in a single activity unit. If a client starts
     * a scope, then it is also responsible for ending it
     * using the 'endScope' method.
     */
    public void startScope();
    
    /**
     * This method determines whether a scope is already active.
     * 
     * @return Whether a scope is already active
     */
    public boolean isScopeActive();
    
    /**
     * This method completes the scope. However this
     * method should only be called by the application if it
     * had previously explicitly started a scope by calling
     * the 'startScope' method.
     */
    public void endScope();
    
    /**
     * This method processes the information associated with the
     * supplied type, and returns the representation of that
     * information that should be included with the activity
     * event. Any additional correlation or property details
     * derived from this information will be directly associated
     * with the supplied activity type.
     * 
     * @param processor The optional information processor to use
     * @param type The information type
     * @param info The information
     * @param headers The optional header information
     * @param actType The activity type being initialized
     * @return The information representation to include with the
     *                  activity event
     */
    public String processInformation(String processor, String type,
                    Object info, java.util.Map<String, Object> headers, ActivityType actType);
    
    /**
     * This method validates the supplied activity type.
     * 
     * @param actType The activity type
     * @throws Exception Failed to validate activity
     */
    public void validate(ActivityType actType) throws Exception;
    
    /**
     * This method records the supplied activity type. If a
     * scope has not been explicitly started, using the 'startScope'
     * then calling this method may result in a scope
     * being started automatically. Otherwise the activity type may just
     * be recorded as a single event within an activity unit.
     * 
     * @param actType The activity type
     */
    public void record(ActivityType actType);
    
}
