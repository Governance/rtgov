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
package org.overlord.rtgov.activity.processor;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.overlord.rtgov.common.registry.ServiceRegistryUtil;

/**
 * This class provides access to the Information Processor Manager once an appropriate
 * implementation has been independently instantiated.
 *
 */
public final class InformationProcessorManagerAccessor {

    private static final Logger LOG=Logger.getLogger(InformationProcessorManagerAccessor.class.getName());
    
    /**
     * The default constructor.
     */
    private InformationProcessorManagerAccessor() {
    }
    
    /**
     * This method sets the information processor manager.
     * 
     * @param ipm The information processor manager
     */
    public static void setInformationProcessorManager(InformationProcessorManager ipm) {
        LOG.warning("Setting the information processor manager - no longer required");
    }
    
    /**
     * This method returns the information processor manager.
     * 
     * @return The information processor manager
     */
    public static InformationProcessorManager getInformationProcessorManager() {
        InformationProcessorManager ret=ServiceRegistryUtil.getSingleService(InformationProcessorManager.class);

        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Get information processor manager="+ret);
        }

        return (ret);
    }
}
