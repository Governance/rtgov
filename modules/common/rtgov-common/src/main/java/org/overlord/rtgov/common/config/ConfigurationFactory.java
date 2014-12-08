/*
 * Copyright 2013 JBoss Inc
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
package org.overlord.rtgov.common.config;

import java.io.File;
import java.net.URL;
import java.util.Set;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.interpol.ConfigurationInterpolator;
import org.overlord.rtgov.common.config.configurator.Configurator;
import org.overlord.rtgov.common.config.crypt.CryptLookup;
import org.overlord.rtgov.common.registry.ServiceRegistryUtil;

/**
 * Factory used to create instances of {@link Configuration}, used by various
 * overlord projects.
 *
 * @author eric.wittmann@redhat.com
 */
public final class ConfigurationFactory {

    private static boolean globalLookupsRegistered = false;

    /**
     * Default constructor.
     */
    private ConfigurationFactory() {
    }

    /**
     * Shared method used to locate and load configuration information from a number of
     * places, aggregated into a single {@link Configuration} instance.
     * @param configFileOverride Config file override
     * @param standardConfigFileName Standard Config file
     * @param refreshDelay The refresh delay
     * @param defaultConfigPath The config path
     * @param defaultConfigLoader The config loader
     * @return Configuration
     */
    public static Configuration createConfig(String configFileOverride, String standardConfigFileName,
            Long refreshDelay, String defaultConfigPath, Class<?> defaultConfigLoader) {
        registerGlobalLookups();
        try {
            CompositeConfiguration compositeConfig = new CompositeConfiguration();

            // System properties always win - add that first
            compositeConfig.addConfiguration(new SystemPropertiesConfiguration());

            // If an override file is provided, add that first.
            if (configFileOverride != null) {
                File file = new File(configFileOverride);
                if (file.isFile()) {
                    compositeConfig.addConfiguration(new PropertiesConfiguration(file));
                } else {
                    // Check for a file on the classpath
                    URL resource = Thread.currentThread().getContextClassLoader().getResource(configFileOverride);
                    if (resource == null && defaultConfigLoader != null) {
                        resource = defaultConfigLoader.getResource(configFileOverride);
                    }
                    if (resource != null) {
                        compositeConfig.addConfiguration(new PropertiesConfiguration(resource));
                    } else {
                        throw new ConfigurationException("Failed to find config file: " + configFileOverride); //$NON-NLS-1$
                    }
                }
            }

            // Now add platform specific properties file (e.g. EAP/standalone/configuration/${standardConfigFileName})
            Set<Configurator> configurators = ServiceRegistryUtil.getServices(Configurator.class);
            if (!configurators.isEmpty()) {
                for (Configurator configurator : configurators) {
                    if (configurator.accept()) {
                        Configuration providedConfig = configurator.provideConfiguration(standardConfigFileName, refreshDelay);
                        if (providedConfig != null) {
                            compositeConfig.addConfiguration(providedConfig);
                        }
                    }
                }
                if (!standardConfigFileName.equals("overlord.properties")) { //$NON-NLS-1$
                    for (Configurator configurator : configurators) {
                        if (configurator.accept()) {
                            Configuration providedConfig = configurator.provideConfiguration("overlord.properties", refreshDelay); //$NON-NLS-1$
                            if (providedConfig != null) {
                                compositeConfig.addConfiguration(providedConfig);
                            }
                        }
                    }
                }
            }

            // Finally add the default config properties if provided
            if (defaultConfigPath != null) {
                compositeConfig.addConfiguration(new PropertiesConfiguration(defaultConfigLoader.getResource(defaultConfigPath)));
            }

            return compositeConfig;
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Registers global lookups for overlord configuration.  This allows custom
     * property interpolation to take place.
     */
    private synchronized static void registerGlobalLookups() {
        if (!globalLookupsRegistered) {
            ConfigurationInterpolator.registerGlobalLookup("crypt", new CryptLookup()); //$NON-NLS-1$
            globalLookupsRegistered = true;
        }
    }


}
