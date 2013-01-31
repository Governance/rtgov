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
package org.overlord.bam.config.jbossas7;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.overlord.bam.common.util.BAMConfig;
import org.overlord.bam.common.util.BAMPropertiesProvider;

/**
 * This class is responsible for providing configuration values
 * for code that injects the BAMConfig annotation.
 *
 */
public class JBossAS7BAMConfig implements BAMPropertiesProvider {
    
    private static final String OVERLORD_BAM_PROPERTIES = "overlord-bam.properties";

    private static final Logger LOG=Logger.getLogger(JBossAS7BAMConfig.class.getName());
    
    private static java.util.Properties _properties=null;
    
    /**
     * This method provides configuration information for injection
     * points identified by the BAMConfig annotation.
     * 
     * @param p The injection point
     * @return The configuration value, or null if not known
     */
    public @Produces @BAMConfig String getConfiguration(InjectionPoint p) {
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
            
            if (ret == null && LOG.isLoggable(Level.FINER)) {
                LOG.finer("Unknown BAM property '"+propName+"'");
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
                    java.io.File f=new java.io.File(configPath, OVERLORD_BAM_PROPERTIES);
                    
                    if (!f.exists()) {
                        LOG.warning(java.util.PropertyResourceBundle.getBundle(
                                "bam-jbossas.Messages").getString("BAM-JBOSSAS-2"));
                    } else {
                        java.io.InputStream is=new java.io.FileInputStream(f);
                        
                        _properties.load(is);
                        
                        is.close();
                    }
                }
                
            } catch (Exception e) {
                LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                        "bam-jbossas.Messages").getString("BAM-JBOSSAS-3"), e);
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
