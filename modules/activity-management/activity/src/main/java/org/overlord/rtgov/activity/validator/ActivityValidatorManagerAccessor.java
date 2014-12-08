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

import org.overlord.rtgov.common.registry.ServiceRegistryUtil;

/**
 * This class provides access to the Activity Validator Manager once an appropriate
 * implementation has been independently instantiated.
 *
 */
public final class ActivityValidatorManagerAccessor {

    private static final Logger LOG=Logger.getLogger(ActivityValidatorManagerAccessor.class.getName());
    
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
    public static void setActivityValidatorManager(ActivityValidatorManager avm) {
        LOG.warning("Setting the activity validator manager - no longer required");
    }
    
    /**
     * This method returns the activity validator manager.
     * 
     * @return The activity validator manager
     */
    public static ActivityValidatorManager getActivityValidatorManager() {
        ActivityValidatorManager ret=ServiceRegistryUtil.getSingleService(ActivityValidatorManager.class);
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Get activity validator manager="+ret);
        }

        return (ret);
    }
}
