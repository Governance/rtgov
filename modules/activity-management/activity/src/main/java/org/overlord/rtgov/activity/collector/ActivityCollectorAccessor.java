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

import org.overlord.commons.services.ServiceRegistryUtil;

/**
 * This class provides access to the Activity Collector once an appropriate
 * implementation has been independently instantiated.
 *
 */
public final class ActivityCollectorAccessor {

    private static final Logger LOG=Logger.getLogger(ActivityCollectorAccessor.class.getName());
    
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
    public static void setActivityCollector(ActivityCollector collector) {
        LOG.warning("Setting the active collector - no longer required");
    }
    
    /**
     * This method returns the activity collector.
     * 
     * @return The activity collector
     */
    public static ActivityCollector getActivityCollector() {
        ActivityCollector ret=ServiceRegistryUtil.getSingleService(ActivityCollector.class);
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Get activity collector="+ret);
        }

        return (ret);
    }
}
