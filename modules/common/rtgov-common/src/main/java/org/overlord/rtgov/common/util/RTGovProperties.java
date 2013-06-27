/*
 * 2012-3 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
                        "rtgov-common.Messages").getString("RTGOV-COMMON-1"));
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
