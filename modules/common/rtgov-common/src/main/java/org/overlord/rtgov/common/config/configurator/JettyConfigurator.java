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
import java.net.URL;

/**
 * Reads the configuration from a Jetty instance.
 * 
 * @author David Virgil Naranjo
 */
public class JettyConfigurator extends AbstractPropertiesFileConfigurator {
    
    /**
     * Constructor.
     */
    public JettyConfigurator() {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept() {
        String jettyDir = System.getProperty("jetty.home"); //$NON-NLS-1$
        String karafDir = System.getProperty("karaf.home"); //$NON-NLS-1$
        return jettyDir != null && karafDir == null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected URL findConfigUrl(String configName) {
        String jettyDir = System.getProperty("jetty.home"); //$NON-NLS-1$
        if (jettyDir != null) {
            File dirFile = new File(jettyDir, "etc"); //$NON-NLS-1$
            return findConfigUrlInDirectory(dirFile, configName);
        }
        return null;
    }

}
