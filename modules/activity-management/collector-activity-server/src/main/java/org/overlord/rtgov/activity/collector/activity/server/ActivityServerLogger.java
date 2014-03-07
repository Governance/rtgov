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

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;

import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.server.ActivityServer;
import org.overlord.rtgov.activity.collector.BatchedActivityUnitLogger;
import org.overlord.rtgov.common.util.RTGovProperties;

/**
 * This class provides a bridge between the Collector and Activity Server,
 * where activity events are logged, causing them to be sent to the
 * configured activity server.
 *
 */
@Singleton
public class ActivityServerLogger extends BatchedActivityUnitLogger
            implements ActivityServerLoggerMBean, javax.management.NotificationEmitter {

    private static final Logger LOG=Logger.getLogger(ActivityServerLogger.class.getName());
    
    private static final int DURATION_BETWEEN_FAILURE_REPORTS = 5*60*1000;
    private static final int MAX_THREADS = 10;

    private static final int FREE_ACTIVITY_LIST_QUEUE_SIZE = 100;
    private static final int ACTIVITY_LIST_QUEUE_SIZE = 10000;

    private Integer _durationBetweenFailureReports=DURATION_BETWEEN_FAILURE_REPORTS;

    private Integer _maxThreads=MAX_THREADS;
    
    private Integer _freeActivityListQueueSize=FREE_ACTIVITY_LIST_QUEUE_SIZE;
    
    private Integer _activityListQueueSize=ACTIVITY_LIST_QUEUE_SIZE;
    
    private java.util.concurrent.BlockingQueue<java.util.List<ActivityUnit>> _queue=null;    
    private java.util.concurrent.BlockingQueue<java.util.List<ActivityUnit>> _freeActivityLists=null;

    @Inject
    private ActivityServer _activityServer=null;
    
    private java.util.List<ActivityUnit> _activities=null;
    
    private java.util.Set<Thread> _threads=new java.util.HashSet<Thread>();
    
    private java.util.List<NotificationDetails> _notificationDetails=
            new java.util.ArrayList<NotificationDetails>();
    
    private int _sequenceNumber=1;
    
    private long _lastFailure=0;
    private int _failuresSinceLastSuccess=0;

    /**
     * This method initializes the Activity Server Logger.
     */
    @PostConstruct
    public void init() {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Initialize Logger for Activity Server (max threads "+_maxThreads+"): "+_activityServer);
        }
        
        _maxThreads = RTGovProperties.getPropertyAsInteger("ActivityServerLogger.maxThreads",
                MAX_THREADS);
        _durationBetweenFailureReports = RTGovProperties.getPropertyAsInteger("ActivityServerLogger.durationBetweenFailureReports",
                DURATION_BETWEEN_FAILURE_REPORTS);
        _freeActivityListQueueSize = RTGovProperties.getPropertyAsInteger("ActivityServerLogger.freeActivityListQueueSize",
                FREE_ACTIVITY_LIST_QUEUE_SIZE);
        _activityListQueueSize = RTGovProperties.getPropertyAsInteger("ActivityServerLogger.activityListQueueSize",
                ACTIVITY_LIST_QUEUE_SIZE);
        
        _queue=new java.util.concurrent.ArrayBlockingQueue<java.util.List<ActivityUnit>>(_activityListQueueSize);
        
        _freeActivityLists=new java.util.concurrent.ArrayBlockingQueue<java.util.List<ActivityUnit>>(_freeActivityListQueueSize);

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
                            
                            // Add protection to make sure list cannot grow unnecessarily large
                            if (_freeActivityLists.size() < _maxThreads*2) {
                                _freeActivityLists.offer(list);
                            }
                            
                            _failuresSinceLastSuccess = 0;
                            
                        } catch (Exception e) {
                            reportFailure(e);
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
     * This method handles reporting failures.
     * 
     * @param e The exception
     */
    protected void reportFailure(Exception e) {
        
        _failuresSinceLastSuccess++;
        
        long currentTime=System.currentTimeMillis();
        
        if (currentTime > _lastFailure + _durationBetweenFailureReports) {            
            LOG.log(Level.SEVERE, "Failed to store list of activity units", e);
            
            _lastFailure = currentTime;
            
            Notification notification=new Notification(java.util.PropertyResourceBundle.getBundle(
                    "collector-activity-server.Messages").getString("COLLECTOR-ACTIVITY-SERVER-1"), this,
                    _sequenceNumber++, MessageFormat.format(
                            java.util.PropertyResourceBundle.getBundle(
                                "collector-activity-server.Messages").getString("COLLECTOR-ACTIVITY-SERVER-2"),
                                        e.getMessage()));
            
            for (NotificationDetails n : _notificationDetails) {
                n.getListener().handleNotification(notification, n.getHandback());
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public int getFailuresSinceLastSuccess() {
        return (_failuresSinceLastSuccess);
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

    /**
     * {@inheritDoc}
     */
    public void addNotificationListener(NotificationListener l,
            NotificationFilter filter, Object handback)
            throws IllegalArgumentException {
        _notificationDetails.add(new NotificationDetails(l, filter, handback));
    }

    /**
     * {@inheritDoc}
     */
    public MBeanNotificationInfo[] getNotificationInfo() {
        return new MBeanNotificationInfo[0];
    }

    /**
     * {@inheritDoc}
     */
    public void removeNotificationListener(NotificationListener l)
            throws ListenerNotFoundException {
        boolean f_found=false;
        
        for (int i=_notificationDetails.size()-1; i >= 0; i--) {
            NotificationDetails n=_notificationDetails.get(i);

            if (n.getListener() == l) {
                _notificationDetails.remove(i);
                f_found = true;
            }
        }
        
        if (!f_found) {
            throw new ListenerNotFoundException();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeNotificationListener(NotificationListener l,
            NotificationFilter filter, Object handback)
            throws ListenerNotFoundException {
        boolean f_found=false;
        
        for (int i=_notificationDetails.size()-1; i >= 0; i--) {
            NotificationDetails n=_notificationDetails.get(i);

            if (n.getListener() == l && n.getFilter() == filter
                    && n.getHandback() == handback) {
                _notificationDetails.remove(i);
                f_found = true;
                
                break;
           }
        }
        
        if (!f_found) {
            throw new ListenerNotFoundException();
        }
    }

    /**
     * This class provides a container for the listener details.
     *
     */
    protected class NotificationDetails {
        
        private NotificationListener _listener=null;
        private NotificationFilter _filter=null;
        private Object _handback=null;
        
        /**
         * This is the constructor.
         * 
         * @param listener The listener
         * @param filter The filter
         * @param handback The handback
         */
        public NotificationDetails(NotificationListener listener,
                NotificationFilter filter, Object handback) {
            _listener = listener;
            _filter = filter;
            _handback = handback;
        }
        
        /**
         * The listener.
         * 
         * @return The listener
         */
        public NotificationListener getListener() {
            return (_listener);
        }
        
        /**
         * The filter.
         * 
         * @return The filter
         */
        public NotificationFilter getFilter() {
            return (_filter);
        }
        
        /**
         * The handback.
         * 
         * @return The handback
         */
        public Object getHandback() {
            return (_handback);
        }
    }
}
