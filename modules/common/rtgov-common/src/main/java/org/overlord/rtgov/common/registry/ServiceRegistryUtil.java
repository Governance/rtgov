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

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides simple access to services.
 *
 * @author eric.wittmann@redhat.com
 */
public final class ServiceRegistryUtil {
    
    private static final Logger LOG=Logger.getLogger(ServiceRegistryUtil.class.getName());

    private static CompositeServiceRegistry registry = new CompositeServiceRegistry();
    static {
        try {
            registry.addRegistry(new ServiceLoaderServiceRegistry());
        } catch (Throwable t) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "Failed to add ServiceLoader service registry", t);
            }
        }
        try {
            registry.addRegistry(new OSGiServiceRegistry());
        } catch (Throwable t) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "Failed to add OSGi service registry", t);
            }
        }
    }

    /**
     * Default constructor.
     */
    private ServiceRegistryUtil() {
    }
    
    /**
     * @see org.overlord.rtgov.common.registry.ServiceRegistry#getSingleService(Class)
     * 
     * @param serviceInterface The service interface
     * @return The service, or null if not found
     * @throws IllegalStateException Failed to get service
     * @param <T> The service interface type
     */
    public static <T> T getSingleService(Class<T> serviceInterface) throws IllegalStateException {
        return registry.getSingleService(serviceInterface);
    }

    /**
     * @see org.overlord.rtgov.common.registry.ServiceRegistry#getServices(Class)
     * 
     * @param serviceInterface The service interface
     * @return The services
     * @param <T> The service interface type
     */
    public static <T> Set<T> getServices(Class<T> serviceInterface) {
        return registry.getServices(serviceInterface);
    }

    /**
     * @see org.overlord.rtgov.common.registry.ServiceRegistry#addServiceListener(Class,ServiceListener)
     * 
     * @param serviceInterface The service type
     * @param listener The service listener
     * @param <T> Service interface type
     */
    public static <T> void addServiceListener(Class<T> serviceInterface, ServiceListener<T> listener) {
        registry.addServiceListener(serviceInterface, listener);
    }

    /**
     * @see org.overlord.rtgov.common.registry.ServiceRegistry#removeServiceListener(ServiceListener)
     * 
     * @param listener The service listener
     * @param <T> Service interface type
     */
    public static <T> void removeServiceListener(ServiceListener<T> listener) {
        registry.removeServiceListener(listener);
    }

}
