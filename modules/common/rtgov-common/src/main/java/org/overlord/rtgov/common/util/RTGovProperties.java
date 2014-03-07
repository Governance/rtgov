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

/**
 * This class provides access to Runtime Governance properties.
 *
 */
public final class RTGovProperties {
    
    private static final Logger LOG=Logger.getLogger(RTGovProperties.class.getName());
    
    private static RTGovPropertiesProvider _provider=new DefaultRTGovPropertiesProvider();
    
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
    public static RTGovPropertiesProvider getPropertiesProvider() {
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
     * This method returns the named property.
     * 
     * @param name The property name
     * @param def The optional default
     * @return The value, or null if not found
     */
    public static String getProperty(String name, String def) {
        String ret=getProperty(name);

        if (ret == null) {
            ret = def;
        }
        
        return (ret);
    }
    
    /**
     * This method returns the named property as a
     * boolean.
     * 
     * @param name The property name
     * @return The value, or null if not found
     */
    public static Boolean getPropertyAsBoolean(String name) {
        Boolean ret=null;
        
        String prop=getProperty(name);
        
        if (prop != null) {
            ret = Boolean.valueOf(prop);
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Runtime Governance boolean value = "+ret);
            }
        }

        return (ret);
    }
    
    /**
     * This method returns the named property as a
     * boolean.
     * 
     * @param name The property name
     * @param def The optional default
     * @return The value, or null if not found
     */
    public static Boolean getPropertyAsBoolean(String name, Boolean def) {
        Boolean ret=getPropertyAsBoolean(name);

        if (ret == null) {
            ret = def;
        }
        
        return (ret);
    }
    
    /**
     * This method returns the named property as an
     * integer.
     * 
     * @param name The property name
     * @return The value, or null if not found
     */
    public static Integer getPropertyAsInteger(String name) {
        Integer ret=null;
        
        String prop=getProperty(name);
        
        if (prop != null) {
            ret = Integer.valueOf(prop);
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Runtime Governance integer value = "+ret);
            }
        }

        return (ret);
    }
    
    /**
     * This method returns the named property as an
     * integer.
     * 
     * @param name The property name
     * @param def The optional default
     * @return The value, or null if not found
     */
    public static Integer getPropertyAsInteger(String name, Integer def) {
        Integer ret=getPropertyAsInteger(name);

        if (ret == null) {
            ret = def;
        }
        
        return (ret);
    }
    
    /**
     * This method returns the named property as an
     * long.
     * 
     * @param name The property name
     * @return The value, or null if not found
     */
    public static Long getPropertyAsLong(String name) {
        Long ret=null;
        
        String prop=getProperty(name);
        
        if (prop != null) {
            ret = Long.valueOf(prop);
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Runtime Governance long value = "+ret);
            }
        }

        return (ret);
    }
    
    /**
     * This method returns the named property as an
     * long.
     * 
     * @param name The property name
     * @param def The optional default
     * @return The value, or null if not found
     */
    public static Long getPropertyAsLong(String name, Long def) {
        Long ret=getPropertyAsLong(name);
        
        if (ret == null) {
            ret = def;
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
