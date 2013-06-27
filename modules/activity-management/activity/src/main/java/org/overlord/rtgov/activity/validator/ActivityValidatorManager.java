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
package org.overlord.rtgov.activity.validator;

import org.overlord.rtgov.activity.model.ActivityType;

/**
 * This interface manages a set of ActivityValidator
 * implementations.
 *
 */
public interface ActivityValidatorManager {
    
    /**
     * This method registers the activity validator.
     * 
     * @param ai The activity validator
     * @throws Exception Failed to register
     */
    public void register(ActivityValidator ai) throws Exception;
    
    /**
     * This method returns the activity validator associated
     * with the supplied name.
     * 
     * @param name The name
     * @return The activity validator, or null if not found
     */
    public ActivityValidator getActivityValidator(String name);
    
    /**
     * This method supplies the activity event to
     * the set of validators registered with the manager.
     * 
     * @param actType The activity event
     * @throws Exception Failed to validate activity event
     */
    public void validate(ActivityType actType) throws Exception;
    
    /**
     * This method registers the activity validator.
     * 
     * @param ai The activity validator
     * @throws Exception Failed to unregister
     */
    public void unregister(ActivityValidator ai) throws Exception;
    
}
