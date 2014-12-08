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

package org.overlord.rtgov.common.registry;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements a service registry by using the standard java {@link ServiceLoader}
 * mechanism.
 *
 * @author eric.wittmann@redhat.com
 */
public class ServiceLoaderServiceRegistry extends AbstractServiceRegistry {
    
    private static final Logger LOG=Logger.getLogger(ServiceLoaderServiceRegistry.class.getName());
    
    private Map<Class<?>, Set<?>> _servicesCache = new HashMap<Class<?>, Set<?>>();

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getSingleService(Class<T> serviceInterface) throws IllegalStateException {
        // Cached single service values are derived from the values cached when checking
        // for multiple services
        T rval = null;
        Set<T> services=getServices(serviceInterface);
        
        if (services.size() > 1) {
            throw new IllegalStateException(MessageFormat.format(
                            java.util.PropertyResourceBundle.getBundle(
                                    "rtgov-common.Messages").getString("RTGOV-COMMON-1"),
                                    serviceInterface)); //$NON-NLS-1$
        } else if (!services.isEmpty()) {
            rval = services.iterator().next();
        }
        return rval;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> Set<T> getServices(Class<T> serviceInterface) {
        synchronized (_servicesCache) {
            if (_servicesCache.containsKey(serviceInterface)) {
                return (Set<T>) _servicesCache.get(serviceInterface);
            }
    
            Set<T> services = new LinkedHashSet<T>();
            try {
                for (T service : ServiceLoader.load(serviceInterface)) {
                    init(service);
                    services.add(service);
                }
            } catch (ServiceConfigurationError sce) {
                // No services found - don't check again.
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.log(Level.FINE, "No services found - don't check again", sce);
                }
            }
            _servicesCache.put(serviceInterface, services);
            return services;
        }
    }

}
