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
package org.overlord.rtgov.internal.config.jbossas;

import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.apache.commons.configuration.Configuration;
import org.overlord.commons.config.ConfigurationFactory;
import org.overlord.rtgov.common.util.RTGovConfig;
import org.overlord.rtgov.common.util.RTGovProperties;
import org.overlord.rtgov.common.util.RTGovPropertiesProvider;

/**
 * This class is responsible for providing configuration values
 * for code that injects the BAMConfig annotation.
 *
 */
public class JBossASRTGovConfig implements RTGovPropertiesProvider {
    
    private static final String OVERLORD_RTGOV_PROPERTIES = "overlord-rtgov.properties";
    private static final String RTGOV_CONFIG_FILE_NAME     = "rtgov.config.file.name";
    private static final String RTGOV_CONFIG_FILE_REFRESH  = "rtgov.config.file.refresh";

    private static final Logger LOG=Logger.getLogger(JBossASRTGovConfig.class.getName());
    
    private static Configuration _configuration;
    
    /**
     * This is the default constructor.
     */
    public JBossASRTGovConfig() {
        // Initialize the static reference to the provider, so that it
        // can be used outside of the archive injecting this implementation
        RTGovProperties.setPropertiesProvider(this);
    }
    
    /**
     * This method provides configuration information for injection
     * points identified by the RTGovConfig annotation.
     * 
     * @param p The injection point
     * @return The configuration value, or null if not known
     */
    public @Produces @RTGovConfig String getConfiguration(InjectionPoint p) {
        String ret=null;

        String memberName=p.getMember().getName();
        
        if (memberName.startsWith("_")) {
            memberName = memberName.substring(1);
        }
        
        String propName=p.getMember().getDeclaringClass().getSimpleName()+"."+memberName;
        
        ret = getProperties().getProperty(propName);
        
        if (ret == null) {
            
            // Check if general property has been specified
            ret = getProperties().getProperty(memberName);
            
             if (LOG.isLoggable(Level.FINEST)) {
                 LOG.finest("Runtime Governance member '"+memberName+"' = "+ret);
             }
        } else if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Runtime Governance property '"+propName+"' = "+ret);
        }
        
        return (ret);
    }
    
    /**
     * This method provides configuration information for injection
     * points identified by the RTGovConfig annotation.
     * 
     * @param p The injection point
     * @return The configuration value, or null if not known
     */
    public @Produces @RTGovConfig Boolean getConfigurationAsBoolean(InjectionPoint p) {
        Boolean ret=null;
        
        String prop=getConfiguration(p);
        
        if (prop != null) {
            ret = Boolean.valueOf(prop);
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Runtime Governance boolean value = "+ret);
            }
        }

        return (ret);
    }
    
    /**
     * This method provides configuration information for injection
     * points identified by the RTGovConfig annotation.
     * 
     * @param p The injection point
     * @return The configuration value, or null if not known
     */
    public @Produces @RTGovConfig Integer getConfigurationAsInteger(InjectionPoint p) {
        Integer ret=null;
        
        String prop=getConfiguration(p);
        
        if (prop != null) {
            ret = Integer.valueOf(prop);
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Runtime Governance integer value = "+ret);
            }
        }

        return (ret);
    }

    /**
     * This method provides configuration information for injection
     * points identified by the RTGovConfig annotation.
     * 
     * @param p The injection point
     * @return The configuration value, or null if not known
     */
    public @Produces @RTGovConfig Long getConfigurationAsLong(InjectionPoint p) {
        Long ret=null;
        
        String prop=getConfiguration(p);
        
        if (prop != null) {
            ret = Long.valueOf(prop);
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Runtime Governance integer value = "+ret);
            }
        }

        return (ret);
    }

    /**
     * This method returns the properties.
     * 
     * @return The properties
     */
    public java.util.Properties getProperties() {
        
        if (_configuration == null) {
            String configFile = System.getProperty(RTGOV_CONFIG_FILE_NAME);
            String refreshDelayStr = System.getProperty(RTGOV_CONFIG_FILE_REFRESH);
            Long refreshDelay = 5000L;
            if (refreshDelayStr != null) {
                refreshDelay = new Long(refreshDelayStr);
            }

            _configuration = ConfigurationFactory.createConfig(
                    configFile,
                    OVERLORD_RTGOV_PROPERTIES,
                    refreshDelay,
                    "/META-INF/config/org.overlord.sramp.ui.server.api.properties",
                    JBossASRTGovConfig.class);
        }
        
        Properties properties = new Properties();
        Iterator<?> keys = _configuration.getKeys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String value = _configuration.getString(key);
            properties.setProperty(key, value);
        }
        return properties;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getProperty(String name) {
        java.util.Properties props=getProperties();
        
        if (props != null) {
            return (props.getProperty(name));
        }
        
        return (null);
    }

}
