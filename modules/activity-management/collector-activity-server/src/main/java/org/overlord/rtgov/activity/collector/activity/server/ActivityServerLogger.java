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

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.server.ActivityServer;
import org.overlord.rtgov.activity.collector.BatchedActivityUnitLogger;
import org.overlord.rtgov.common.util.RTGovConfig;

/**
 * This class provides a bridge between the Collector and Activity Server,
 * where activity events are logged, causing them to be sent to the
 * configured activity server.
 *
 */
@Singleton
public class ActivityServerLogger extends BatchedActivityUnitLogger implements ActivityServerLoggerMBean {

    private static final Logger LOG=Logger.getLogger(ActivityServerLogger.class.getName());
    
    private static final int MAX_THREADS = 10;

    @Inject @RTGovConfig
    private Integer _maxThreads=MAX_THREADS;
    
    private java.util.concurrent.BlockingQueue<java.util.List<ActivityUnit>> _queue=
            new java.util.concurrent.ArrayBlockingQueue<java.util.List<ActivityUnit>>(10000);
    
    private java.util.concurrent.BlockingQueue<java.util.List<ActivityUnit>> _freeActivityLists=
            new java.util.concurrent.ArrayBlockingQueue<java.util.List<ActivityUnit>>(100);

    @Inject
    private ActivityServer _activityServer=null;
    
    private java.util.List<ActivityUnit> _activities=null;
    
    private java.util.Set<Thread> _threads=new java.util.HashSet<Thread>();
    
    /**
     * This method initializes the Activity Server Logger.
     */
    @PostConstruct
    public void init() {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Initialize Logger for Activity Server (max threads "+_maxThreads+"): "+_activityServer);
        }
        
        if (_maxThreads == null) {
            _maxThreads = MAX_THREADS;
        }
        
        // Start dispatch thread
        for (int i=0; i < _maxThreads; i++) {
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        try {
                            java.util.List<ActivityUnit> list=_queue.take();
                            
                            _activityServer.store(list);
                            
                            // Free up the list
                            list.clear();
                            
                            _freeActivityLists.offer(list);
                            
                        } catch (Exception e) {
                            LOG.log(Level.SEVERE, "Failed to store list of activity units", e);
                        }
                    }
                }            
            });
            
            _threads.add(thread);
            
            thread.setDaemon(true);
            thread.start();
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
    public int getPendingActivityUnits() {
        return (_queue.size());
    }
    
    /**
     * {@inheritDoc}
     */
    protected void appendActivity(ActivityUnit act) throws Exception {
        if (_activities == null) {
            _activities = new java.util.ArrayList<ActivityUnit>();
        }
        _activities.add(act);
    }

    /**
     * {@inheritDoc}
     */
    protected void sendMessage() throws Exception {        
        if (_activities != null) {

            if (!_queue.offer(_activities, 500, TimeUnit.MILLISECONDS)) {
                LOG.warning("Failed to send message - queue is full");
            }

            // Clear the list
            _activities = _freeActivityLists.poll();
            
            if (_activities == null) {
                _activities = new java.util.ArrayList<ActivityUnit>();
            }
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
