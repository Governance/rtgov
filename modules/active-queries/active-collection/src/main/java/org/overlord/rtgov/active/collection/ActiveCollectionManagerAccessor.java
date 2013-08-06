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
package org.overlord.rtgov.active.collection;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides access to the Active Collection Manager once an appropriate
 * implementation has been independently instantiated.
 *
 */
public final class ActiveCollectionManagerAccessor {

    private static final Logger LOG=Logger.getLogger(ActiveCollectionManagerAccessor.class.getName());
    
    private static ActiveCollectionManager _acManager=null;
    
    private static final Object SYNC=new Object();
    
    /**
     * The default constructor.
     */
    private ActiveCollectionManagerAccessor() {
    }
    
    /**
     * This method sets the active collection manager.
     * 
     * @param manager The manager
     */
    protected static void setActiveCollectionManager(ActiveCollectionManager manager) {
        synchronized (SYNC) {
            if (_acManager != null) {
                LOG.severe("Active collection manager already set");
            }
            
            _acManager = manager;
            
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Set activity collection manager="+manager);
            }
            
            SYNC.notifyAll();
        }
    }
    
    /**
     * This method returns the active collection manager.
     * 
     * @return The active collection manager
     */
    public static ActiveCollectionManager getActiveCollectionManager() {
        
        // Avoid unnecessary sync once set (runs in 1/4 to 1/3 of the time)
        if (_acManager == null) {
            synchronized (SYNC) {
                if (_acManager == null) {
                    try {
                        SYNC.wait(10000);
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, "Failed to wait for ActiveCollectionManager to become available", e);
                    }
                    
                    if (_acManager == null) {
                        LOG.severe("ActiveCollectionManager is not available");
                    }
                }
            }
        }

        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Get activity collection manager="+_acManager);
        }

        return (_acManager);
    }
}
