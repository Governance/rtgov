/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-13, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.rtgov.activity.collector;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides access to the Activity Collector once an appropriate
 * implementation has been independently instantiated.
 *
 */
public final class ActivityCollectorAccessor {

    private static final int DEFAULT_WAIT_TIME = 300000;

    private static final Logger LOG=Logger.getLogger(ActivityCollectorAccessor.class.getName());
    
    private static ActivityCollector _activityCollector=null;
    
    private static final Object SYNC=new Object();
    
    /**
     * The default constructor.
     */
    private ActivityCollectorAccessor() {
    }
    
    /**
     * This method sets the activity collector.
     * 
     * @param collector The collector
     */
    protected static void setActivityCollector(ActivityCollector collector) {
        synchronized (SYNC) {
            if (_activityCollector != null) {
                LOG.severe("Activity collector already set");
            }
            
            _activityCollector = collector;
            
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Set activity collector="+collector);
            }
            
            SYNC.notifyAll();
        }
    }
    
    /**
     * This method returns the activity collector.
     * 
     * @return The activity collector
     */
    public static ActivityCollector getActivityCollector() {
        
        // Avoid unnecessary sync once set (runs in 1/4 to 1/3 of the time)
        if (_activityCollector == null) {
            synchronized (SYNC) {
                if (_activityCollector == null) {
                    try {
                        SYNC.wait(getWaitTime());
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, "Failed to wait for ActivityCollector to become available", e);
                    }
                    
                    if (_activityCollector == null) {
                        LOG.severe("ActivityCollector is not available");
                    }
                }
            }
        }

        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Get activity collector="+_activityCollector);
        }

        return (_activityCollector);
    }
    
    private static long getWaitTime() {
        final String waitTimeVal = System.getProperty("org.overlord.ServiceWaitTime");
        long waitTime = DEFAULT_WAIT_TIME;
        if (waitTimeVal != null) {
            try {
                waitTime = Long.parseLong(waitTimeVal);
            } catch (final NumberFormatException nfe) {
                LOG.warning("Failed to parse ServiceWaitTime " + waitTimeVal + ", using default value of " + waitTime);
            }
        }
        return waitTime;
    }
}
