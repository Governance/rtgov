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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

/**
 * Contains the main configurator code. It tries to read the configuration from
 * the server api, if it is implemented. If not, it reads the configuration from
 * a file located in the server installation path. This file location changes
 * depends on the configurator.
 * 
 * @author David Virgil Naranjo
 */
public abstract class AbstractPropertiesFileConfigurator implements Configurator {
    
    /**
     * Constructor.
     */
    public AbstractPropertiesFileConfigurator() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration provideConfiguration(String configName, Long refreshDelay)
            throws ConfigurationException {
        URL url = findConfigUrl(configName);
        if (url != null) {
            PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(url);
            FileChangedReloadingStrategy fileChangedReloadingStrategy = new FileChangedReloadingStrategy();
            fileChangedReloadingStrategy.setRefreshDelay(refreshDelay);
            propertiesConfiguration.setReloadingStrategy(fileChangedReloadingStrategy);
            return propertiesConfiguration;
        } else {
            return null;
        }
    }

    /**
     * Locates the config file and returns a URL to it.
     * @param configName The config name
     * @return The URL
     */
    protected abstract URL findConfigUrl(String configName);

    /**
     * Returns a URL to a file with the given name inside the given directory.
     * @param directory
     * 
     * @param directory The directory
     * @param configName The config name
     * @return The URL
     */
    protected URL findConfigUrlInDirectory(File directory, String configName) {
        if (directory.isDirectory()) {
            File cfile = new File(directory, configName);
            if (cfile.isFile()) {
                try {
                    return cfile.toURI().toURL();
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

}
