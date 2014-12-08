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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.overlord.rtgov.activity.collector.ActivityCollector;
import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.common.registry.ServiceListener;
import org.overlord.rtgov.common.registry.ServiceRegistryUtil;

/**
 * This interface represents the capability for validating
 * activity information from an application.
 *
 */
public class DefaultActivityValidator implements ActivityValidator {

    private static final Logger LOG=Logger.getLogger(DefaultActivityValidator.class.getName());
    
    private ActivityCollector _activityCollector=null;
    
    private boolean _initialized=false;

    /**
     * This method initializes the auditor.
     */
    protected void init() {
        ServiceRegistryUtil.addServiceListener(ActivityCollector.class, new ServiceListener<ActivityCollector>() {

            @Override
            public void registered(ActivityCollector service) {
                _activityCollector = service;
                
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Default Activity Validator collector="+_activityCollector);
                }
            }

            @Override
            public void unregistered(ActivityCollector service) {
                _activityCollector = null;
                
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Default Activity Validator collector unset");
                }
            }            
        });
        
        _initialized = true;
    }

    /**
     * {@inheritDoc}
     */
    public void validate(ActivityType actType) throws Exception {
        if (!_initialized) {
            init();
        }
        
        if (_activityCollector != null) {
            _activityCollector.validate(actType);
        } else {
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Default Activity Validator unable to validate '"+actType+"' as not collector available");
            }
        }
    }
    
}
