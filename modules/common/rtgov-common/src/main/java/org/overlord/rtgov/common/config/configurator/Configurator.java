/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.rtgov.common.config.configurator;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;

/**
 * Allows to read the Configuration properties from a specific server instance.
 *
 * @author David Virgil Naranjo
 */
public interface Configurator {

    /**
     * Returns true if this configurator accepts the responsiblity of providing
     * configuration information for the application.  Implementations of this
     * interface are, generally, created for each supported runtime platform.  
     * The implementation of 'accept' should return true only if the current 
     * runtime platform matches the one supported by the impl.
     * 
     * @return Whether accepted
     */
    public boolean accept();

    /**
     * Provides the actual application configuration.  Typically this means 
     * locating the configuration file for the supported runtime platform.
     *
     * @param configName the name of the config - e.g. used to lookup a properties file
     * @param refreshDelay how often the properties are reloaded
     * @return The configuration
     * @throws ConfigurationException Failed to get configuration
     */
    public Configuration provideConfiguration(String configName, Long refreshDelay) throws ConfigurationException;
}
