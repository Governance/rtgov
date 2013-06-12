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
package org.overlord.rtgov.internal.ep;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.overlord.rtgov.common.service.Service;
import org.overlord.rtgov.ep.EPContext;

/**
 * This class provides services to the EventProcessor
 * implementations that process the events.
 *
 */
public class DefaultEPContext implements EPContext {

    private static final Logger LOG=Logger.getLogger(DefaultEPContext.class.getName());

    private static final ThreadLocal<Object> RESULT=new ThreadLocal<Object>();
    
    private java.util.Map<String,Service> _services=null;
    
    /**
     * The default constructor.
     */
    public DefaultEPContext() {
    }
    
    /**
     * This constructor initializes the service map.
     * 
     * @param services The map of services available
     */
    public DefaultEPContext(java.util.Map<String,Service> services) {
        _services = services;
    }
    
    /**
     * This method sets the service map.
     * 
     * @param services The services
     */
    public void setServices(java.util.Map<String,Service> services) {
        _services = services;
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
    public void handle(Object result) {
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest(">>> "+this+": Handle result="+result);
        }
        
        RESULT.set(result);
    }

    /**
     * This method retrieves the result forwarded by the rule.
     * 
     * @return The result, or null if not defined
     */
    public Object getResult() {
        Object ret=RESULT.get();
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest(">>> "+this+": Get result="+ret);
        }
        
        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    public Service getService(String name) {
        Service ret=null;
        
        if (_services != null) {
            ret = _services.get(name);
        }

        return (ret);
    }
}
