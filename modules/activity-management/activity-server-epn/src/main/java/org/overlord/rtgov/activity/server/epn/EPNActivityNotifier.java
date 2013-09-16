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
package org.overlord.rtgov.activity.server.epn;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.server.ActivityNotifier;
import org.overlord.rtgov.epn.EPNManager;

/**
 * This class provides a bridge between the Activity Server, where
 * activity events are reported, and the Event Processor Network
 * which will be used to provide additional processing and analysis
 * of the activity events.
 *
 */
public class EPNActivityNotifier implements ActivityNotifier {

    private static final String SUBJECT_NAME = "ActivityUnits";

    private static final Logger LOG=Logger.getLogger(EPNActivityNotifier.class.getName());
    
    @Inject
    private EPNManager _epnManager=null;
    
    /**
     * This method returns the Event Processor Network Manager.
     * 
     * @return The EPN manager
     */
    public EPNManager getManager() {
        return (_epnManager);
    }
    
    /**
     * This method sets the Event Processor Network Manager.
     * 
     * @param epnm The EPN manager
     */
    public void setManager(EPNManager epnm) {
        _epnManager = epnm;
    }
    
    /**
     * This method initializes the Activity Server to EPN bridge.
     */
    @PostConstruct
    public void init() {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Initialize Activity Server to EPN bridge");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void notify(List<ActivityUnit> activities) throws Exception {
        _epnManager.publish(SUBJECT_NAME, activities);
    }

    /**
     * This method closes the Activity Server to EPN bridge.
     */
    @PreDestroy
    public void close() {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Initialize Activity Server to EPN (via JMS) bridge");
        }
    }
}
