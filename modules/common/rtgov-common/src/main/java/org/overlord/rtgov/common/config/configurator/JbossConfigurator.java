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
 * Reads the configuration from a Jboss instance.
 * 
 * @author David Virgil Naranjo
 */
public class JbossConfigurator extends AbstractPropertiesFileConfigurator {
    
    /**
     * Constructor.
     */
    public JbossConfigurator() {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept() {
        String jbossConfigDir = System.getProperty("jboss.server.config.dir"); //$NON-NLS-1$
        String jbossConfigUrl = System.getProperty("jboss.server.config.url"); //$NON-NLS-1$
        return jbossConfigDir != null || jbossConfigUrl != null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected URL findConfigUrl(String configName) {
        URL rval = null;
        String jbossConfigDir = System.getProperty("jboss.server.config.dir"); //$NON-NLS-1$
        if (jbossConfigDir != null) {
            File dirFile = new File(jbossConfigDir);
            rval = findConfigUrlInDirectory(dirFile, configName);
            if (rval != null) {
                return rval;
            }
        }
        String jbossConfigUrl = System.getProperty("jboss.server.config.url"); //$NON-NLS-1$
        if (jbossConfigUrl != null) {
            File dirFile = new File(jbossConfigUrl);
            rval = findConfigUrlInDirectory(dirFile, configName);
            if (rval != null) {
                return rval;
            }
        }
        return null;
    }

}
