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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.common.registry.ServiceClose;
import org.overlord.rtgov.common.registry.ServiceInit;
import org.overlord.rtgov.common.util.VersionUtil;

/**
 * This class manages a set of ActivityValidator
 * implementations.
 *
 */
public abstract class AbstractActivityValidatorManager implements ActivityValidatorManager {
    
    private static final Logger LOG=Logger.getLogger(AbstractActivityValidatorManager.class.getName());

    private java.util.Map<String,ActivityValidator> _activityValidatorIndex=
            new java.util.HashMap<String,ActivityValidator>();
    private java.util.List<ActivityValidator> _activityValidators=
                new java.util.ArrayList<ActivityValidator>();
    
    /**
     * The default constructor.
     */
    public AbstractActivityValidatorManager() {
    }
    
    /**
     * This method initializes the activity validator manager.
     */
    @ServiceInit
    public void init() {
    }
    
    /**
     * {@inheritDoc}
     */
    public void register(ActivityValidator ai) throws Exception {
        
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Register: activity validator name="
                        +ai.getName()+" version="
                        +ai.getVersion()+" ai="+ai);
        }
        
        // Initialize the activity validator
        ai.init();
        
        synchronized (_activityValidatorIndex) {
            boolean f_add=false;
            
            // Check if activity validator for same name already exists
            ActivityValidator existing=_activityValidatorIndex.get(ai.getName());
            
            if (existing != null) {
                
                // Check whether newer version
                if (VersionUtil.isNewerVersion(existing.getVersion(),
                        ai.getVersion())) {
                    
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine("Replace existing activity validator version="
                                    +existing.getVersion());
                    }
                    
                    // Unregister old version
                    unregister(existing);
                    
                    // Add new version
                    f_add = true;                  
                } else {
                                      
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine("Newer version '"+existing.getVersion()
                                +"' already registered");
                    }
                }
            } else {
                f_add = true;
            }
            
            if (f_add) {
                _activityValidatorIndex.put(ai.getName(), ai);
                _activityValidators.add(ai);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public ActivityValidator getActivityValidator(String name) {
        synchronized (_activityValidatorIndex) {
            return (_activityValidatorIndex.get(name));
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void validate(ActivityType actType) throws Exception {        
        synchronized (_activityValidatorIndex) {            
            for (int i=0; i < _activityValidators.size(); i++) {
                _activityValidators.get(i).validate(actType);
            }
        }        
    }
    
    /**
     * {@inheritDoc}
     */
    public void unregister(ActivityValidator ai) throws Exception {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Unregister: activity validator name="
                        +ai.getName()+" version="
                        +ai.getVersion()+" ai="+ai);
        }
        
        synchronized (_activityValidatorIndex) {
            
            if (_activityValidators.contains(ai)) {
                ActivityValidator removed=
                        _activityValidatorIndex.remove(ai.getName());
                _activityValidators.remove(removed);
                
            } else if (_activityValidatorIndex.containsKey(ai.getName())) {
                
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Another version of activity validator name="
                            +ai.getName()+" is currently registered: existing version ="
                            +_activityValidatorIndex.get(ai.getName()).getVersion());
                }
            }
        }
    }
    
    /**
     * This metohd closes the activity validator manager.
     * 
     * @throws Exception Failed to close
     */
    @ServiceClose
    public void close() throws Exception {
        synchronized (_activityValidatorIndex) {
            for (int i=_activityValidators.size()-1; i >= 0; i--) {
                unregister(_activityValidators.get(i));
            }
        }
    }
}
