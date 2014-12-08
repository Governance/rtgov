/*
 * Copyright 2013-5 Red Hat Inc
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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.configuration.AbstractConfiguration;

/**
 * A {@link Configuration} implementation based on the current java system
 * properties.  This implementation differs from {@link SystemConfiguration}
 * because it pulls the system properties live, rather than caching them
 * when the configuration instance is created.  This allows configuration to
 * change by modifying the system properties at runtime.
 * @author eric.wittmann@redhat.com
 */
public class SystemPropertiesConfiguration extends AbstractConfiguration {

    /**
     * Constructor.
     */
    public SystemPropertiesConfiguration() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsKey(String key) {
        return System.getProperties().containsKey(key);
    }

    /**
     * @{@inheritDoc}
     */
    @Override
    public Object getProperty(String key) {
        return System.getProperties().getProperty(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<String> getKeys() {
        Set<String> keys = new HashSet<String>();
        Set<Object> keySet = System.getProperties().keySet();
        for (Object object : keySet) {
            keys.add(String.valueOf(object));
        }
        return keys.iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addPropertyDirect(String key, Object value) {
        System.getProperties().setProperty(key, String.valueOf(value));
    }
}
