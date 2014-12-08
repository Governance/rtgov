/*
 * 2012-3 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.rtgov.activity.processor.loader.osgi;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.overlord.rtgov.activity.processor.InformationProcessor;
import org.overlord.rtgov.activity.processor.InformationProcessorManager;
import org.overlord.rtgov.activity.processor.validation.IPValidationListener;
import org.overlord.rtgov.activity.processor.validation.IPValidator;
import org.overlord.rtgov.activity.util.InformationProcessorUtil;
import org.overlord.rtgov.common.registry.ServiceRegistryUtil;

/**
 * This class provides the capability to load Information Processors from a
 * defined file.
 *
 */
public class IPActivator implements BundleActivator {
    
    private static final Logger LOG=Logger.getLogger(IPActivator.class.getName());
    
    private static final String IP_JSON = "ip.json";
    
    private java.util.List<InformationProcessor> _informationProcessors=null;
    
    private org.overlord.rtgov.common.registry.ServiceListener<InformationProcessorManager> _listener;

    /**
     * {@inheritDoc}
     */
    public void start(final BundleContext context) throws Exception {
        _listener = new org.overlord.rtgov.common.registry.ServiceListener<InformationProcessorManager>() {

            @Override
            public void registered(InformationProcessorManager service) {
                registerInformationProcessor(service);
            }

            @Override
            public void unregistered(InformationProcessorManager service) {
                unregisterInformationProcessor(service);
            }
        };
        
        ServiceRegistryUtil.addServiceListener(InformationProcessorManager.class, _listener);
    }
    
    /**
     * This method registers the information processor with the manager.
     * 
     * @param ipManager The information processor manager
     */
    protected void registerInformationProcessor(InformationProcessorManager ipManager) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Register InformationProcessorManager");
        }
        
        try {
            java.io.InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(IP_JSON);
            
            if (is == null) {
                LOG.severe(java.util.PropertyResourceBundle.getBundle(
                        "ip-loader-osgi.Messages").getString("IP-LOADER-OSGI-1"));
            } else {
                byte[] b=new byte[is.available()];
                is.read(b);
                is.close();
                
                _informationProcessors = InformationProcessorUtil.deserializeInformationProcessorList(b);
                
                if (_informationProcessors == null) {
                    LOG.severe(java.util.PropertyResourceBundle.getBundle(
                            "ip-loader-osgi.Messages").getString("IP-LOADER-OSGI-2"));
                } else {
                    for (InformationProcessor ip : _informationProcessors) {
                        ip.init();
                        
                        if (IPValidator.validate(ip, getValidationListener())) {
                            ipManager.register(ip);
                        } else {
                            ip.close();
                            
                            // TODO: Do we need to halt the deployment due to failures? (RTGOV-199)
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                    "ip-loader-osgi.Messages").getString("IP-LOADER-OSGI-3"), e);
        }
    }
    
    /**
     * This method returns the validation listener.
     * 
     * @return The validation listener
     */
    protected IPValidationListener getValidationListener() {
        return (new IPValidationListener() {

            public void error(InformationProcessor ip, Object target, String issue) {
                LOG.severe(issue);
            }
            
        });
    }
    
    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) throws Exception {
        if (_informationProcessors != null) {
            InformationProcessorManager ipManager=ServiceRegistryUtil.getSingleService(InformationProcessorManager.class);
            
            if (ipManager != null) {
                unregisterInformationProcessor(ipManager);
            }
        }
        
        ServiceRegistryUtil.removeServiceListener(_listener);
        _listener = null;
    }
        
    /**
     * This method unregisters the information processor.
     * 
     * @param ipManager Information processor manager
     */
    protected void unregisterInformationProcessor(InformationProcessorManager ipManager) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Unregister InformationProcessor");
        }
        
        if (ipManager != null && _informationProcessors != null) {
            try {
                for (InformationProcessor ip : _informationProcessors) {
                    ipManager.unregister(ip);
                }
                
                _informationProcessors = null;
            } catch (Exception e) {
                LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                        "ip-loader-osgi.Messages").getString("IP-LOADER-OSGI-4"), e);
            }
        }
    }       
}
