/*
 * 2012-4 Red Hat Inc. and/or its affiliates and other contributors.
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

import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.overlord.rtgov.common.config.ConfigurationFactory;

/**
 * This class provides the default properties provider.
 *
 */
public class DefaultRTGovPropertiesProvider implements RTGovPropertiesProvider {
    
    private static final String OVERLORD_RTGOV_PROPERTIES = "overlord-rtgov.properties";
    private static final String RTGOV_CONFIG_FILE_NAME     = "rtgov.config.file.name";
    private static final String RTGOV_CONFIG_FILE_REFRESH  = "rtgov.config.file.refresh";

    private static Configuration _configuration;
    
    /**
     * This is the default constructor.
     */
    public DefaultRTGovPropertiesProvider() {
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
                    null,
                    DefaultRTGovPropertiesProvider.class);
        }
        
        Properties properties = new Properties();
        Iterator<?> keys = _configuration.getKeys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String value = _configuration.getString(key);
            if (value != null) {
                properties.setProperty(key, value);
            }
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
