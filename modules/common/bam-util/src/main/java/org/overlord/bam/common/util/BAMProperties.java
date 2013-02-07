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
package org.overlord.bam.common.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;

/**
 * This class provides access to BAM properties.
 *
 */
public final class BAMProperties {
    
    private static final Logger LOG=Logger.getLogger(BAMProperties.class.getName());
    
    private static BAMPropertiesProvider _provider=null;
    private static boolean _initialized=false;
    
    /**
     * Make constructor private.
     */
    private BAMProperties() {
    }

    /**
     * This method sets the BAM properties.
     * 
     * @param props The properties
     */
    public static void setPropertiesProvider(BAMPropertiesProvider props) {
        _provider = props;
    }
    
    /**
     * This method gets the BAM properties.
     * 
     * @return The properties
     */
    @SuppressWarnings("unchecked")
    public static BAMPropertiesProvider getPropertiesProvider() {

        // If provider is null, see if can be resolved from bean manager
        if (_provider == null && !_initialized) {
            try {
                BeanManager bm=InitialContext.doLookup("java:comp/BeanManager");
                
                java.util.Set<Bean<?>> beans=bm.getBeans(BAMPropertiesProvider.class);
                
                for (Bean<?> b : beans) {                
                    CreationalContext<Object> cc=new CreationalContext<Object>() {
                        public void push(Object arg0) {
                        }
                        public void release() {
                        }                   
                    };
                    
                    _provider = (BAMPropertiesProvider)((Bean<Object>)b).create(cc);
                    
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine("Found BAMProperties provider="+_provider);
                    }
                    
                    if (_provider != null) {
                        break;
                    }
                }
            } catch (Exception e) {                
                LOG.warning(java.util.PropertyResourceBundle.getBundle(
                        "bam-util.Messages").getString("BAM-UTIL-1"));
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
            LOG.fine("BAM property '"+name+"' = "+ret);
        }

        return (ret);
    }
    
    /**
     * This method returns the BAM properties.
     * 
     * @return The properties, or null if not available
     */
    public static java.util.Properties getProperties() {
        java.util.Properties ret=null;
        
        if (getPropertiesProvider() != null) {
            ret = getPropertiesProvider().getProperties();
        }
        
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("BAM properties = "+ret);
        }

        return (ret);
    }
}
