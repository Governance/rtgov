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
package org.overlord.rtgov.client;

import org.overlord.rtgov.activity.model.ActivityType;

/**
 * This interface represents the capability for recording
 * activity information from an application.
 *
 */
public interface ActivityReporter {

    /**
     * This method can be used to report general information.
     * 
     * @param info The information
     */
    public void logInfo(String info);
    
    /**
     * This method can be used to report warning information.
     * 
     * @param warning The warning description
     */
    public void logWarning(String warning);
    
    /**
     * This method can be used to report error information.
     * 
     * @param error The error description
     */
    public void logError(String error);
    
    /**
     * This method can be used to report activity information.
     * 
     * @param type The activity type
     * @param props The properties
     */
    public void report(String type, java.util.Map<String,String> props);

    /**
     * This method reports the activity event to the
     * collector.
     * 
     * @param actType The activity type
     */
    public void report(ActivityType actType);

}
