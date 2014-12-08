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
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;

/**
 * Implements a service registry by delegating to the osgi service
 * registry.
 *
 * @author eric.wittmann@redhat.com
 */
public class OSGiServiceRegistry extends AbstractServiceRegistry {
    
    private static final Logger LOG=Logger.getLogger(OSGiServiceRegistry.class.getName());

    private java.util.Map<ServiceListener<?>, ServiceListenerAdapter<?>> _listeners=
                            new java.util.HashMap<ServiceListener<?>, ServiceListenerAdapter<?>>();
    
    private java.util.Map<Class<?>,java.util.List<Object>> _services=new java.util.HashMap<Class<?>,java.util.List<Object>>();
    private java.util.Map<Class<?>,ServiceListener<?>> _serviceListeners=new java.util.HashMap<Class<?>,ServiceListener<?>>();

    /**
     * Constructor.
     */
    public OSGiServiceRegistry() {
    }

    /**
     * This method initializes the supplied service.
     * 
     * @param serviceInterface The service interface
     * @param service The service
     */
    protected <T> void init(final Class<T> serviceInterface, Object service) {
        boolean createListener=false;
        boolean initService=false;
        
        synchronized (_services) {
            java.util.List<Object> services=_services.get(serviceInterface);
            
            if (services == null) {
                createListener = true;
                services = new java.util.ArrayList<Object>();
                _services.put(serviceInterface, services);
            }
            
            if (!services.contains(service)) {
                initService = true;                
                services.add(service);
            }
        }
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Initialize interface="+serviceInterface+" service="+service+" initService=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        +initService+" createListener="+createListener); //$NON-NLS-1$
        }
        
        if (initService) {
            super.init(service);
        }
        
        if (createListener) {
            ServiceListener<T> sl=new ServiceListener<T>() {

                @Override
                public void registered(T service) {
                }

                @Override
                public void unregistered(T service) {
                    close(serviceInterface, service);
                }                
            };
            
            addServiceListener(serviceInterface, sl);
            
            synchronized (_serviceListeners) {
                _serviceListeners.put(serviceInterface, sl);
            }
        }
    }
    
    /**
     * Close the supplied service.
     * 
     * @param serviceInterface The service interface
     * @param service The service
     */
    protected void close(Class<?> serviceInterface, Object service) {
        boolean removeListener=false;
        boolean closeService=false;
        
        synchronized (_services) {
            java.util.List<Object> services=_services.get(serviceInterface);
            
            if (services != null) {                
                closeService = services.remove(service);
                
                if (services.size() == 0) {
                    removeListener = true;
                    _services.remove(serviceInterface);
                }
            }
        }
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Close interface="+serviceInterface+" service="+service+" closeService=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        +closeService+" removeListener="+removeListener); //$NON-NLS-1$
        }
        
        if (closeService) {
            super.close(service);
        }
        
        if (removeListener) {
            ServiceListener<?> l=null;
            
            synchronized (_serviceListeners) {
                l = _serviceListeners.remove(serviceInterface);
            }
            
            if (l != null) {
                removeServiceListener(l);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getSingleService(Class<T> serviceInterface) throws IllegalStateException {
        // TODO use the osgi service tracker here
        T service = null;
        try {
            Bundle bundle = FrameworkUtil.getBundle(serviceInterface);
            if (bundle != null) {
                BundleContext context = bundle.getBundleContext();
                
                if (context != null) {
                    ServiceReference<?>[] serviceReferences = context.getServiceReferences(serviceInterface.getName(), null);
                    if (serviceReferences != null) {
                        if (serviceReferences.length == 1) {
                            service = (T) context.getService(serviceReferences[0]);
                            init(serviceInterface, service);
                        } else {
                            throw new IllegalStateException(MessageFormat.format(
                                            java.util.PropertyResourceBundle.getBundle(
                                                    "rtgov-common.Messages").getString("RTGOV-COMMON-2"),
                                                    serviceInterface)); //$NON-NLS-1$
                        }
                    }
                } else {
                    LOG.warning(MessageFormat.format(
                            java.util.PropertyResourceBundle.getBundle(
                                    "rtgov-common.Messages").getString("RTGOV-COMMON-3"),
                                    serviceInterface)); //$NON-NLS-1$
                }
            }
        } catch (InvalidSyntaxException e) {
            throw new RuntimeException(e);
        }
        return service;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> Set<T> getServices(Class<T> serviceInterface) {
        Set<T> services = new HashSet<T>();
        try {
            Bundle bundle = FrameworkUtil.getBundle(serviceInterface);
            if (bundle != null) {
                if (bundle.getState() == Bundle.RESOLVED) {
                    bundle.start();
                }
                BundleContext context = bundle.getBundleContext();
                if (context != null) {
                    ServiceReference<?>[] serviceReferences = context.getServiceReferences(
                            serviceInterface.getName(), null);
                    if (serviceReferences != null) {
                        for (ServiceReference<?> serviceReference : serviceReferences) {
                            T service = (T) context.getService(serviceReference);
                            init(serviceInterface, service);
                            services.add(service);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return services;
    }

    /**
     * {@inheritDoc}
     */
    public <T> void addServiceListener(Class<T> serviceInterface, ServiceListener<T> listener) {
        ServiceListenerAdapter<T> adapter=new ServiceListenerAdapter<T>(serviceInterface, listener, this);
        
        synchronized (_listeners) {
            _listeners.put(listener, adapter);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T> void removeServiceListener(ServiceListener<T> listener) {
        ServiceListenerAdapter<T> adapter=null;
        
        synchronized (_listeners) {
            adapter = (ServiceListenerAdapter<T>)_listeners.remove(listener);
        }
        
        if (adapter != null) {
            adapter.close();
        }
    }

    /**
     * This class bridges between the OSGi service listener and the commons service listener.
     * 
     */
    public static class ServiceListenerAdapter<T> {
        
        private Class<T> _serviceInterface;
        private ServiceListener<T> _serviceListener;
        private OSGiServiceRegistry _serviceRegistry;
        
        private org.osgi.framework.ServiceListener _osgiListener=null;
        
        /**
         * The constructor.
         * 
         * @param serviceInterface The service interface
         * @param listener The listener
         * @param reg The service registry
         */
        public ServiceListenerAdapter(Class<T> serviceInterface, ServiceListener<T> listener, OSGiServiceRegistry reg) {
            _serviceInterface = serviceInterface;
            _serviceListener = listener;
            _serviceRegistry = reg;
            
            init();
        }
        
        /**
         * Initialize the adapter.
         */
        @SuppressWarnings("unchecked")
        protected void init() {
            Bundle bundle = FrameworkUtil.getBundle(_serviceInterface);
            if (bundle != null) {
                final BundleContext context = bundle.getBundleContext();
                
                _osgiListener = new org.osgi.framework.ServiceListener() {
                    public void serviceChanged(ServiceEvent ev) {
                        ServiceReference<?> sr = ev.getServiceReference();
                        T service=(T)context.getService(sr);
                        switch(ev.getType()) {
                        case ServiceEvent.REGISTERED:
                            _serviceRegistry.init(_serviceInterface, service);
                            _serviceListener.registered(service);
                            break;
                        case ServiceEvent.UNREGISTERING:
                            _serviceListener.unregistered(service);
                            _serviceRegistry.close(_serviceInterface, service);
                            break;
                        default:
                            break;
                        }
                    }           
                };
                
                String filter = "(objectclass=" + _serviceInterface.getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
                try {
                    context.addServiceListener(_osgiListener, filter);
                } catch (InvalidSyntaxException e) { 
                    LOG.log(Level.SEVERE, 
                            MessageFormat.format(
                                    java.util.PropertyResourceBundle.getBundle(
                                            "rtgov-common.Messages").getString("RTGOV-COMMON-4"),
                                            _serviceInterface.getName())); //$NON-NLS-1$
                }

                ServiceReference<?>[] srefs;
                try {
                    srefs = context.getServiceReferences(_serviceInterface.getName(), null);
                    
                    if (srefs != null) {
                        for (int i=0; i < srefs.length; i++) {
                            T service=(T)context.getService(srefs[i]);
                            _serviceRegistry.init(service);
                            _serviceListener.registered(service);
                        }
                    }
                } catch (InvalidSyntaxException e) {
                    LOG.log(Level.SEVERE, MessageFormat.format(
                            java.util.PropertyResourceBundle.getBundle(
                                    "rtgov-common.Messages").getString("RTGOV-COMMON-5"),
                                    _serviceInterface.getName())); //$NON-NLS-1$
                }
            }
        }
        
        /**
         * Close.
         */
        public void close() {
            
        }
    }
}

