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
package org.overlord.rtgov.activity.collector;

import java.lang.management.ManagementFactory;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.common.util.RTGovProperties;

/**
 * This class provides the abstract activity unit logger implementation that
 * batches activity units based on time slots and size.
 *
 */
public abstract class BatchedActivityUnitLogger implements ActivityUnitLogger,
                            BatchedActivityUnitLoggerMBean {

    private static final Logger LOG=Logger.getLogger(BatchedActivityUnitLogger.class.getName());

    private static final int MAX_UNIT_COUNT = 1000;
    private static final long MAX_TIME_INTERVAL = 500;
    
    private static final String OBJECT_NAME_DOMAIN = "overlord.rtgov.collector";    
    private static final String OBJECT_NAME_LOGGER = OBJECT_NAME_DOMAIN+":name=ActivityLogger";
    
    private int _messageCounter=0;
    private java.util.Timer _timer;
    private java.util.TimerTask _timerTask;
    
    private Long _maxTimeInterval;
    private Integer _maxUnitCount;
    
    private boolean _initialized=false;
    
    /**
     * This method initializes the activity logger.
     */
    @PostConstruct
    public synchronized void init() {
        
        if (!_initialized) {
            _timer = new java.util.Timer();
            
            _maxTimeInterval = RTGovProperties.getPropertyAsLong("BatchedActivityUnitLogger.maxTimeInterval", MAX_TIME_INTERVAL);
            _maxUnitCount = RTGovProperties.getPropertyAsInteger("BatchedActivityUnitLogger.maxUnitCount", MAX_UNIT_COUNT);
    
            try {
                MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
                
               if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Register the ActivityUnitLogger MBean["
                                +OBJECT_NAME_LOGGER+"]: "+this);
                }
                
                ObjectName objname2=new ObjectName(OBJECT_NAME_LOGGER);            
                mbs.registerMBean(this, objname2);
    
            } catch (Exception e) {
                LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                        "activity.Messages").getString("ACTIVITY-18"), e);
            }
            
            _initialized = true;
        }
    }
    
    /**
     * This method sets the maximum time interval
     * that should be logged within a single batch.
     * 
     * @param max The maximum number of messages
     */
    public void setMaxTimeInterval(long max) {
        _maxTimeInterval = max;
    }
    
    /**
     * This method returns the maximum time interval
     * that should be logged within a single batch.
     * 
     * @return The maximum number of messages
     */
    public long getMaxTimeInterval() {
        if (_maxTimeInterval == null) {
            return (MAX_TIME_INTERVAL);
        }
        return (_maxTimeInterval);
    }
    
    /**
     * This method sets the maximum number of activity units
     * that should be logged within a single batch.
     * 
     * @param max The maximum number of activity units
     */
    public void setMaxUnitCount(int max) {
        _maxUnitCount = max;
    }
    
    /**
     * This method returns the maximum number of activity units
     * that should be logged within a single batch.
     * 
     * @return The maximum number of activity units
     */
    public int getMaxUnitCount() {
        if (_maxUnitCount == null) {
            return (MAX_UNIT_COUNT);
        }
        return (_maxUnitCount);
    }
    
    /**
     * {@inheritDoc}
     */
    public void log(ActivityUnit act) {
        
        try {
             synchronized (_timer) {
                 
                 // Cancel any current scheduled task
                 if (_timerTask == null) {
                     _timerTask = new TimerTask() {
                            public void run() {
                                try {
                                    synchronized (_timer) {
                                        sendMessage();
                                    
                                        reset();
                                    }
                                } catch (Exception e) {
                                    LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                                            "activity.Messages").getString("ACTIVITY-3"), e);
                                }
                            }                         
                         };
                         
                     // Schedule send
                     _timer.schedule(_timerTask, getMaxTimeInterval());
                 }

                 appendActivity(act);
                 
                 _messageCounter++;

                 if (_messageCounter > getMaxUnitCount()) {
                     sendMessage();
                     
                     reset();
                 }
             }
             
        } catch (Exception e) {
            LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                    "activity.Messages").getString("ACTIVITY-3"), e);
        }
    }

    /**
     * This method appends the supplied activity event to the log.
     * 
     * @param act The activity event
     * @throws Exception Failed to append the activity event to the log
     */
    protected abstract void appendActivity(ActivityUnit act) throws Exception;
    
    /**
     * This method sends the message. This method should be overridden
     * by the specific logger implementation to send the message using
     * the appropriate transport mechanism. However the overridden method
     * should also invoke this method to manage the message count and
     * timer.
     * 
     * @throws Exception Failed to send the message
     */
    protected abstract void sendMessage() throws Exception;
    
    /**
     * This method resets the counters and timers.
     */
    protected void reset() {
        _messageCounter = 0;
         
        if (_timerTask != null) {
            _timerTask.cancel();
            _timerTask = null;
        }
    }

    /**
     * This method closes the activity logger.
     */
    @PreDestroy
    public void close() {
        _timer.cancel();
        
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 

            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Unregister the ActivityUnitLogger MBean["
                            +OBJECT_NAME_LOGGER+"]: "+this);
            }

            ObjectName objname2=new ObjectName(OBJECT_NAME_LOGGER);            
            mbs.unregisterMBean(objname2);
            
        } catch (Throwable t) {
            if (LOG.isLoggable(Level.FINER)) {
                LOG.log(Level.FINER, java.util.PropertyResourceBundle.getBundle(
                    "activity.Messages").getString("ACTIVITY-19"), t);
            }
        }
    }
}
