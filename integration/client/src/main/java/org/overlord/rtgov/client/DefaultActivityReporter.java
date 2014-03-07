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
import org.overlord.rtgov.activity.model.app.CustomActivity;
import org.overlord.rtgov.activity.model.app.LogMessage;

/**
 * This interface represents the capability for recording
 * activity information from a JEE application.
 *
 */
public class DefaultActivityReporter implements ActivityReporter {

    private static final Logger LOG=Logger.getLogger(DefaultActivityReporter.class.getName());
    
    private ActivityCollector _activityCollector=null;
    
    private boolean _initialized=false;
    
    /**
     * This method initializes the auditor.
     */
    @PostConstruct
    protected void init() {
        if (_activityCollector == null) {
            _activityCollector = ActivityCollectorAccessor.getActivityCollector();
            
            if (_activityCollector == null) {
                LOG.severe("Failed to obtain Activity Collector from Client Manager");
            }
        }

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("*********** Default Activity Reporter initialized with collector="+_activityCollector);
        }
        
        _initialized = true;
    }
    
    /**
     * This method sets the activity collector.
     * 
     * @param ac The activity collector
     */
    public void setActivityCollector(ActivityCollector ac) {
        _activityCollector = ac;
    }

    /**
     * {@inheritDoc}
     */
    public void logInfo(String info) {
        LogMessage lm=new LogMessage();
        lm.setMessage(info);
        lm.setLevel(LogMessage.Level.Information);
        
        try {
            report(lm);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to report Info activity: "+info, e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void logWarning(String warning) {
        LogMessage lm=new LogMessage();
        lm.setMessage(warning);
        lm.setLevel(LogMessage.Level.Warning);
        
        try {
            report(lm);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to report Warning activity: "+warning, e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void logError(String error) {
        LogMessage lm=new LogMessage();
        lm.setMessage(error);
        lm.setLevel(LogMessage.Level.Error);
        
        try {
            report(lm);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to report Error activity: "+error, e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void report(String type, java.util.Map<String,String> props) {
        CustomActivity ca=new CustomActivity();
        ca.setCustomType(type);        
        ca.setProperties(props);
        
        report(ca);
    }
    
    /**
     * {@inheritDoc}
     */
    public void report(ActivityType actType) {
        if (!_initialized) {
            init();
        }
        
        _activityCollector.record(actType);
    }
    
}
