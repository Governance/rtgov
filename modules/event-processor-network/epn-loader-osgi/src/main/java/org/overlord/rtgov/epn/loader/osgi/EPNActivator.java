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
package org.overlord.rtgov.epn.loader.osgi;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.overlord.commons.services.ServiceRegistryUtil;
import org.overlord.rtgov.epn.AbstractEPNLoader;
import org.overlord.rtgov.epn.EPNManager;
import org.overlord.rtgov.epn.Network;
import org.overlord.rtgov.epn.util.NetworkUtil;

/**
 * This class provides the activator capability for an Event Processor
 * Network.
 *
 */
public class EPNActivator extends AbstractEPNLoader implements BundleActivator {
    
    private static final Logger LOG=Logger.getLogger(EPNActivator.class.getName());
    
    private static final String EPN_JSON = "/epn.json";

    private Network _network=null;
    
    private org.overlord.commons.services.ServiceListener<EPNManager> _listener;
    
    /**
     * {@inheritDoc}
     */
    public void start(final BundleContext context) throws Exception {
        _listener = new org.overlord.commons.services.ServiceListener<EPNManager>() {

            @Override
            public void registered(EPNManager service) {
                registerEPN(service);
            }

            @Override
            public void unregistered(EPNManager service) {
                unregisterEPN(service);
            }
        };
        
        ServiceRegistryUtil.addServiceListener(EPNManager.class, _listener);
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) throws Exception {
        if (_network != null) {
            EPNManager epnManager=ServiceRegistryUtil.getSingleService(EPNManager.class);
            
            if (epnManager != null) {
                unregisterEPN(epnManager);
            }
        }       

        ServiceRegistryUtil.removeServiceListener(_listener);
        _listener = null;
    }

    /**
     * This method registers the EPN with the EPNManager.
     * 
     * @param epnManager The EPN manager
     */
    protected void registerEPN(EPNManager epnManager) {
        java.lang.ClassLoader cl=Thread.currentThread().getContextClassLoader();
        
        try {
            java.io.InputStream is=EPNActivator.class.getResourceAsStream(EPN_JSON);
            
            if (is == null) {
                LOG.severe(java.util.PropertyResourceBundle.getBundle(
                        "epn-loader-osgi.Messages").getString("EPN-LOADER-OSGI-1"));
            } else {
                byte[] b=new byte[is.available()];
                is.read(b);
                is.close();
                
                // Set context class loader
                Thread.currentThread().setContextClassLoader(EPNActivator.class.getClassLoader());
                
                _network=NetworkUtil.deserialize(b);
                
                // Pre-initialize the network to avoid any contextual class
                // loading issues. Within OSGI, the registration of the network
                // will be done in the context of the core war, while as the
                // event processors require the classloading context associated
                // with the network deployment.
                preInit(_network);
                
                // TODO: Do we need to halt the deployment due to failures? (RTGOV-199)
                epnManager.register(_network);
            }
        } catch (Exception e) {
            String mesg=java.util.PropertyResourceBundle.getBundle(
                    "epn-loader-osgi.Messages").getString("EPN-LOADER-OSGI-2");
            
            LOG.log(Level.SEVERE, mesg, e);
            
            // Throw runtime exception to cause the deployment to fail
            throw new RuntimeException(mesg, e);
            
        } finally {
            Thread.currentThread().setContextClassLoader(cl);
        }
    }
    
    /**
     * This method unregisters the EPN.
     * 
     * @param context The context
     */
    protected void unregisterEPN(EPNManager epnManager) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Unregister EPN");
        }
        
        if (_network != null) {
            try {
                epnManager.unregister(_network.getName(), _network.getVersion());
                
                _network = null;
            } catch (Throwable t) {
                if (LOG.isLoggable(Level.FINER)) {
                    LOG.log(Level.FINER, java.util.PropertyResourceBundle.getBundle(
                        "epn-loader-osgi.Messages").getString("EPN-LOADER-OSGI-3"), t);
                }
            }
        }
    }       
}
