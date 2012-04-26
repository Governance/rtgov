/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-11, Red Hat Middleware LLC, and others contributors as indicated
 * by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.savara.bam.collector.spi;

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.savara.bam.activity.model.ActivityUnit;

/**
 * This class provides the abstract activity logger implementation that
 * batches activity events based on time slots and size.
 *
 */
public abstract class BatchedActivityLogger implements ActivityLogger {

    private static final Logger LOG=Logger.getLogger(BatchedActivityLogger.class.getName());
    
    private int _messageCounter=0;
    private java.util.Timer _timer;
    private java.util.TimerTask _timerTask;
    
    private long _maxTimeInterval=500;
    private int _maxMessageCount=1000;
    
    /**
     * This method initializes the activity logger.
     */
    @PostConstruct
    public void init() {
        _timer = new java.util.Timer();
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
        return (_maxTimeInterval);
    }
    
    /**
     * This method sets the maximum number of messages
     * that should be logged within a single batch.
     * 
     * @param max The maximum number of messages
     */
    public void setMaxMessageCount(int max) {
        _maxMessageCount = max;
    }
    
    /**
     * This method returns the maximum number of messages
     * that should be logged within a single batch.
     * 
     * @return The maximum number of messages
     */
    public int getMaxMessageCount() {
        return (_maxMessageCount);
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
                                    synchronized(_timer) {
                                        sendMessage();
                                    
                                        reset();
                                    }
                                } catch (Exception e) {
                                    LOG.log(Level.SEVERE, "Failed to send activity event", e);
                                }
                            }                         
                         };
                         
                     // Schedule send
                     _timer.schedule(_timerTask, _maxTimeInterval);
                 }

                 appendActivity(act);
                 
                 _messageCounter++;

                 if (_messageCounter > _maxMessageCount) {
                     sendMessage();
                     
                     reset();
                 }
             }
             
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to send activity event", e);
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
    }
}
