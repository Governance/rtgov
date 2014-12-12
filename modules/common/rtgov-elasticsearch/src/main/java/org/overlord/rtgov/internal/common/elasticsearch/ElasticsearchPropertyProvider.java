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
package org.overlord.rtgov.internal.common.elasticsearch;

import org.apache.commons.configuration.Configuration;
import org.overlord.commons.config.ConfigurationFactory;

import java.util.Iterator;
import java.util.Properties;

/**
 * User: imk@redhat.com.
 * Date: 07/07/14.
 * Time: 15:33.
 */
class ElasticsearchPropertyProvider {

    /**
     * location of elasticsearch property file
     */
    private static final String ELASTICSEARCH_CONFIG = "elasticsearch.config";

    /**
     * DEFAULT configuration of elastic search bundled with the deployment
     */
    private static final String ELASTICSEARCH_CONFIG_DEFAULT = "overlord-rtgov-elasticsearch.properties";

    /**
     * Refresh delay
     */
    private static final long ELASTICSEARCH_CONFIG_REFRESH = 60000;
    private static Configuration _configuration;

    /**
     * This is the default constructor.
     */
    public ElasticsearchPropertyProvider() {
    }

    /**
     * This method returns the properties.
     *
     * @return The properties
     */
    public java.util.Properties getProperties() {

        if (_configuration == null) {
            String configFile = System.getProperty(ELASTICSEARCH_CONFIG);

            _configuration = ConfigurationFactory.createConfig(
                    configFile,
                    ELASTICSEARCH_CONFIG_DEFAULT,
                    ELASTICSEARCH_CONFIG_REFRESH,
                    null,
                    ElasticsearchPropertyProvider.class);
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
}
