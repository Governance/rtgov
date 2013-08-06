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
package org.overlord.rtgov.activity.validator;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides access to the Activity Validator Manager once an appropriate
 * implementation has been independently instantiated.
 *
 */
public final class ActivityValidatorManagerAccessor {

    private static final Logger LOG=Logger.getLogger(ActivityValidatorManagerAccessor.class.getName());
    
    private static ActivityValidatorManager _activityValidatorManager=null;
    
    private static final Object SYNC=new Object();
    
    /**
     * The default constructor.
     */
    private ActivityValidatorManagerAccessor() {
    }
    
    /**
     * This method sets the activity validator manager.
     * 
     * @param avm The activity validator manager
     */
    protected static void setActivityValidatorManager(ActivityValidatorManager avm) {
        synchronized (SYNC) {
            if (_activityValidatorManager != null) {
                LOG.severe("Activity validator manager already set");
            }
            
            _activityValidatorManager = avm;
            
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Set activity validator manager="+avm);
            }
            
            SYNC.notifyAll();
        }
    }
    
    /**
     * This method returns the activity validator manager.
     * 
     * @return The activity validator manager
     */
    public static ActivityValidatorManager getActivityValidatorManager() {
        
        // Avoid unnecessary sync once set (runs in 1/4 to 1/3 of the time)
        if (_activityValidatorManager == null) {
            synchronized (SYNC) {
                if (_activityValidatorManager == null) {
                    try {
                        SYNC.wait(10000);
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, "Failed to wait for ActivityValidatorManager to become available", e);
                    }
                    
                    if (_activityValidatorManager == null) {
                        LOG.severe("ActivityValidatorManager is not available");
                    }
                }
            }
        }

        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Get activity validator manager="+_activityValidatorManager);
        }

        return (_activityValidatorManager);
    }
}
