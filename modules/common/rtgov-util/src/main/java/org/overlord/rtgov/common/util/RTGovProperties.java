/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-12, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.rtgov.common.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;

/**
 * This class provides access to Runtime Governance properties.
 *
 */
public final class RTGovProperties {
    
    private static final Logger LOG=Logger.getLogger(RTGovProperties.class.getName());
    
    private static RTGovPropertiesProvider _provider=null;
    private static boolean _initialized=false;
    
    /**
     * Make constructor private.
     */
    private RTGovProperties() {
    }

    /**
     * This method sets the Runtime Governance properties.
     * 
     * @param props The properties
     */
    public static void setPropertiesProvider(RTGovPropertiesProvider props) {
        _provider = props;
    }
    
    /**
     * This method gets the Runtime Governance properties.
     * 
     * @return The properties
     */
    @SuppressWarnings("unchecked")
    public static RTGovPropertiesProvider getPropertiesProvider() {

        // If provider is null, see if can be resolved from bean manager
        if (_provider == null && !_initialized) {
            try {
                BeanManager bm=InitialContext.doLookup("java:comp/BeanManager");
                
                java.util.Set<Bean<?>> beans=bm.getBeans(RTGovPropertiesProvider.class);
                
                for (Bean<?> b : beans) {                
                    CreationalContext<Object> cc=new CreationalContext<Object>() {
                        public void push(Object arg0) {
                        }
                        public void release() {
                        }                   
                    };
                    
                    _provider = (RTGovPropertiesProvider)((Bean<Object>)b).create(cc);
                    
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine("Found Runtime Governance Properties provider="+_provider);
                    }
                    
                    if (_provider != null) {
                        break;
                    }
                }
            } catch (Exception e) {                
                LOG.warning(java.util.PropertyResourceBundle.getBundle(
                        "rtgov-util.Messages").getString("RTGOV-UTIL-1"));
            }
            
            _initialized = true;
        }
        
        return (_provider);
    }
    
    /**
     * This method returns the named property.
     * 
     * @param name The property name
     * @return The value, or null if not found
     */
    public static String getProperty(String name) {
        String ret=null;
        
        if (getPropertiesProvider() != null) {
            ret = getPropertiesProvider().getProperty(name);
        }
        
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Runtime Governance property '"+name+"' = "+ret);
        }

        return (ret);
    }
    
    /**
     * This method returns the Runtime Governance properties.
     * 
     * @return The properties, or null if not available
     */
    public static java.util.Properties getProperties() {
        java.util.Properties ret=null;
        
        if (getPropertiesProvider() != null) {
            ret = getPropertiesProvider().getProperties();
        }
        
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Runtime Governance properties = "+ret);
        }

        return (ret);
    }
}
