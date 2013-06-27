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
package org.overlord.rtgov.tests.platforms.jbossas.customevent.data;

import org.overlord.rtgov.activity.model.ActivityType;

/**
 * This class provides a custom event to be passed between nodes
 * locally defined in the EPN.
 *
 */
public class CustomActivityEvent implements java.io.Serializable {

    private static final long serialVersionUID = -5932057374131602070L;

    private ActivityType _activityType=null;
    
    /**
     * This is the constructor used to initialize the
     * associated activity type.
     * 
     * @param actType The activity type
     */
    public CustomActivityEvent(ActivityType actType) {
        _activityType = actType;
    }
    
    /**
     * This method returns the activity type.
     * 
     * @return The activity type
     */
    public ActivityType getActivityType() {
        return (_activityType);
    }
}
