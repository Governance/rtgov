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

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract service registry implementation.
 *
 */
public abstract class AbstractServiceRegistry implements ServiceRegistry {
    
    private static final Logger LOG=Logger.getLogger(AbstractServiceRegistry.class.getName());

    /**
     * {@inheritDoc}
     */
    public <T> void addServiceListener(Class<T> serviceInterface, ServiceListener<T> listener) {
        java.util.Set<T> services=getServices(serviceInterface);
        
        if (services != null) {
            for (T service : services) {
                listener.registered(service);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public <T> void removeServiceListener(ServiceListener<T> listener) {
    }
    
    /**
     * This method initializes the supplied service if it has a method
     * annotated with @ServiceInit.
     * 
     * @param service The service
     */
    protected void init(Object service) {
        
        if (service != null) {            
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Service created by ["+getClass().getSimpleName()+"] is: "+service); //$NON-NLS-1$ //$NON-NLS-2$
            }
            
            Method[] methods=service.getClass().getMethods();
            
            for (int i=0; i < methods.length; i++) {
                if (methods[i].isAnnotationPresent(ServiceInit.class)
                        && methods[i].getReturnType() == void.class
                        && methods[i].getParameterTypes().length == 0) {
                    try {
                        methods[i].invoke(service);
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
        }
    }

    /**
     * This method closes the supplied service if it has a method
     * annotated with @ServiceClose.
     * 
     * @param service The service
     */
    protected void close(Object service) {
        
        if (service != null) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Service closed by ["+getClass().getSimpleName()+"] is: "+service); //$NON-NLS-1$ //$NON-NLS-2$
            }

            Method[] methods=service.getClass().getMethods();
            
            for (int i=0; i < methods.length; i++) {
                if (methods[i].isAnnotationPresent(ServiceClose.class)
                        && methods[i].getReturnType() == void.class
                        && methods[i].getParameterTypes().length == 0) {
                    try {
                        methods[i].invoke(service);
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
        }
    }

}
