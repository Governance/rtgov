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
import org.overlord.rtgov.ep.EventProcessor;
import org.overlord.rtgov.ep.Predicate;

/**
 * This class represents an activity validator, associated with the
 * collector mechanism, to process the observed activities.
 *
 */
public class ActivityValidator {

    //private static final String ACTIVITY_SOURCE = "collector";
    
    private String _name=null;
    private String _version=null;

    private Predicate _predicate=null;
    private EventProcessor _eventProcessor=null;
    
    private boolean _initialized=false;
    
    /**
     * Default constructor.
     */
    public ActivityValidator() {
    }
    
    /**
     * This method returns the name of the activity validator.
     * 
     * @return The name
     */
    public String getName() {
        return (_name);
    }
    
    /**
     * This method sets the name of the activity validator.
     * 
     * @param name The name
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * This method returns the version of the activity validator.
     * 
     * @return The version
     */
    public String getVersion() {
        return (_version);
    }

    /**
     * This method sets the version of the activity validator.
     * 
     * @param version The version
     */
    public void setVersion(String version) {
        _version = version;
    }
    
    /**
     * This method sets the optional predicate that can be
     * used to assess whether a supplied activity is
     * applicable to the validator.
     * 
     * @param predicate The predicate
     */
    public void setPredicate(Predicate predicate) {
        _predicate = predicate;
    }
    
    /**
     * This method gets the optional predicate that can be
     * used to assess whether a supplied activity is
     * applicable to the validator.
     * 
     * @return The predicate, or null if not relevant
     */
    public Predicate getPredicate() {
        return (_predicate);
    }
    
    /**
     * This method sets the event processor used to
     * intercept and analyse the activity event.
     * 
     * @param ep The event processor
     */
    public void setEventProcessor(EventProcessor ep) {
        _eventProcessor = ep;
    }
    
    /**
     * This method gets the event processor used to
     * intercept and analyse the activity event.
     * 
     * @return The event processor
     */
    public EventProcessor getEventProcessor() {
        return (_eventProcessor);
    }
    
    /**
     * Initialize the activity validator.
     * 
     * @throws Exception Failed to initialize
     */
    public void init() throws Exception {
        
        if (!_initialized) {
            _initialized = true;
            
            if (_predicate != null) {
                _predicate.init();
            }
            if (_eventProcessor != null) {
                _eventProcessor.init();
            }
        }
    }

    /**
     * This method validates the intercepted activity event.
     * 
     * @param actType The activity type
     * @throws Exception Failed to validate activity
     */
    public void validate(ActivityType actType) throws Exception {
        
        // Check if predicate defined and if so, the event is applicable
        if (_predicate == null || _predicate.evaluate(actType)) {
            
            // Process the event
            _eventProcessor.process(null, actType, 0);
        }
    }
    
}
