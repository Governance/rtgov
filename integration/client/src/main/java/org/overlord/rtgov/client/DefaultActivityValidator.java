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

import javax.annotation.PostConstruct;

import org.overlord.rtgov.activity.collector.ActivityCollector;
import org.overlord.rtgov.activity.collector.ActivityCollectorAccessor;
import org.overlord.rtgov.activity.model.ActivityType;

/**
 * This interface represents the capability for validating
 * activity information from a JEE application.
 *
 */
public class DefaultActivityValidator implements ActivityValidator {

    private static final Logger LOG=Logger.getLogger(DefaultActivityValidator.class.getName());
    
    private ActivityCollector _activityCollector=null;
    
    private boolean _initialized=false;

    /**
     * This method initializes the auditor.
     */
    @PostConstruct
    protected void init() {
        _activityCollector = ActivityCollectorAccessor.getActivityCollector();
        
        if (_activityCollector == null) {
            LOG.severe("Unable to obtain activity collector from Client Manager");
        }
        
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("*********** Default Activity Validator initialized with collector="+_activityCollector);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void validate(ActivityType actType) throws Exception {
        if (!_initialized) {
            init();
        }
        
        _activityCollector.validate(actType);
    }
    
}
