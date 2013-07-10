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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

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

    private static final Logger LOG=Logger.getLogger(JBossASRTGovConfig.class.getName());
    
    private static java.util.Properties _properties=null;
    
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
     * This method returns the properties.
     * 
     * @return The properties
     */
    public java.util.Properties getProperties() {
        
        if (_properties == null) {
            _properties = new java.util.Properties();
            
            try {
                String configPath=System.getProperty("jboss.server.config.dir");
                
                if (configPath == null) {
                    LOG.warning("Unable to find JBoss server configuration directory (jboss.server.config.dir)");
                } else {
                    java.io.File f=new java.io.File(configPath, OVERLORD_RTGOV_PROPERTIES);
                    
                    if (!f.exists()) {
                        LOG.warning(java.util.PropertyResourceBundle.getBundle(
                                "rtgov-jbossas.Messages").getString("RTGOV-JBOSSAS-2"));
                    } else {
                        java.io.InputStream is=new java.io.FileInputStream(f);
                        
                        _properties.load(is);
                        
                        is.close();
                    }
                }
                
            } catch (Exception e) {
                LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                        "rtgov-jbossas.Messages").getString("RTGOV-JBOSSAS-3"), e);
            }
        }
        
        return (_properties);
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
