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
package org.overlord.bam.internal.epn;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.overlord.bam.epn.EPNContext;

/**
 * This class provides services to the EventProcessor
 * implementations that process the events.
 *
 */
public class DefaultEPNContext implements EPNContext {

    private static final Logger LOG=Logger.getLogger(DefaultEPNContext.class.getName());

    private ThreadLocal<Object> _result=new ThreadLocal<Object>();
    
    /**
     * The default constructor.
     */
    public DefaultEPNContext() {
    }
    
    /**
     * {@inheritDoc}
     */
    public void logInfo(String info) {
        LOG.info(info);
    }

    /**
     * {@inheritDoc}
     */
    public void logError(String error) {
        LOG.severe(error);
    }

    /**
     * {@inheritDoc}
     */
    public void logDebug(String debug) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine(debug);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void forward(Object result) {
        _result.set(result);
    }

    /**
     * This method retrieves the result forwarded by the rule.
     * 
     * @return The result, or null if not defined
     */
    public Object getResult() {
        return _result.get();
    }
}
