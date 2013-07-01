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
package org.overlord.rtgov.activity.collector.activity.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.server.ActivityServer;
import org.overlord.rtgov.activity.collector.BatchedActivityUnitLogger;

/**
 * This class provides a bridge between the Collector and Activity Server,
 * where activity events are logged, causing them to be sent to the
 * configured activity server.
 *
 */
@Singleton
public class ActivityServerLogger extends BatchedActivityUnitLogger {

    private static final Logger LOG=Logger.getLogger(ActivityServerLogger.class.getName());
    
    private static final int MAX_THREADS = 10;

    private ExecutorService _executor=Executors.newFixedThreadPool(MAX_THREADS);

    @Inject
    private ActivityServer _activityServer=null;
    
    private java.util.List<ActivityUnit> _activities=null;
    
    /**
     * This method initializes the Activity Server Logger.
     */
    @PostConstruct
    public void init() {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Initialize Logger for Activity Server: "+_activityServer);
        }
        super.init();
    }
    
    /**
     * This method sets the activity server.
     * 
     * @param activityServer The activity server
     */
    public void setActivityServer(ActivityServer activityServer) {
        _activityServer = activityServer;
    }
    
    /**
     * This method gets the activity server.
     * 
     * @return The activity server
     */
    public ActivityServer getActivityServer() {
        return (_activityServer);
    }
    
    /**
     * {@inheritDoc}
     */
    protected void appendActivity(ActivityUnit act) throws Exception {
        if (_activities == null) {
            _activities = new java.util.Vector<ActivityUnit>();
        }
        _activities.add(act);
    }

    /**
     * {@inheritDoc}
     */
    protected void sendMessage() throws Exception {
        if (_activities != null) {
            final java.util.List<ActivityUnit> list=_activities;

            _executor.execute(new Runnable() {
                public void run() {
                    try {
                        _activityServer.store(list);    
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, "Failed to store list of activity units", e);
                    }
                }
            });

            // Clear the list
            _activities = new java.util.ArrayList<ActivityUnit>();
        }
    }

    /**
     * This method closes the Activity Server Logger.
     */
    @PreDestroy
    public void close() {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Close Logger for Activity Server: "+_activityServer);
        }
        super.close();
    }
}
