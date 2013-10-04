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
package org.overlord.rtgov.epn;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides access to the EPN Manager once an appropriate
 * implementation has been independently instantiated.
 *
 */
public final class EPNManagerAccessor {

    private static final int DEFAULT_WAIT_TIME = 300000;

    private static final Logger LOG=Logger.getLogger(EPNManagerAccessor.class.getName());
    
    private static EPNManager _epnManager=null;
    
    private static final Object SYNC=new Object();
    
    /**
     * The default constructor.
     */
    private EPNManagerAccessor() {
    }
    
    /**
     * This method sets the EPN manager.
     * 
     * @param manager The manager
     */
    protected static void setEPNManager(EPNManager manager) {
        synchronized (SYNC) {
            if (_epnManager != null) {
                LOG.severe("EPN manager already set");
            }
            
            _epnManager = manager;
            
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Set EPN manager="+manager);
            }

            SYNC.notifyAll();
        }
    }
    
    /**
     * This method returns the EPN manager.
     * 
     * @return The EPN manager
     */
    public static EPNManager getEPNManager() {

        // Avoid unnecessary sync once set (runs in 1/4 to 1/3 of the time)
        if (_epnManager == null) {
            synchronized (SYNC) {
                if (_epnManager == null) {
                    try {
                        SYNC.wait(getWaitTime());
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, "Failed to wait for EPNManager to become available", e);
                    }
                    
                    if (_epnManager == null) {
                        LOG.severe("EPNManager is not available");
                    }
                }
            }
        }

        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Get activity collection manager="+_epnManager);
        }

        return (_epnManager);
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
