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
package org.overlord.rtgov.config.jbossas7;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.overlord.rtgov.common.util.RTGovConfig;
import org.overlord.rtgov.common.util.RTGovPropertiesProvider;

/**
 * This class is responsible for providing configuration values
 * for code that injects the BAMConfig annotation.
 *
 */
public class JBossAS7RTGovConfig implements RTGovPropertiesProvider {
    
    private static final String OVERLORD_RTGOV_PROPERTIES = "overlord-rtgov.properties";

    private static final Logger LOG=Logger.getLogger(JBossAS7RTGovConfig.class.getName());
    
    private static java.util.Properties _properties=null;
    
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
