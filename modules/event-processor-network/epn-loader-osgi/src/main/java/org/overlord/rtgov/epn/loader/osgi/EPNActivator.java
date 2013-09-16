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
import org.overlord.rtgov.epn.AbstractEPNLoader;
import org.overlord.rtgov.epn.EPNManager;
import org.overlord.rtgov.epn.EPNManagerAccessor;
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

    private EPNManager _epnManager=null;
 
    private Network _network=null;
    
    /**
     * {@inheritDoc}
     */
    public void start(final BundleContext context) throws Exception {
        init();
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) throws Exception {
        close();
    }

    /**
     * This method sets the EPN manager.
     * 
     * @param manager The EPN manager
     */
    public void setManager(EPNManager manager) {
        _epnManager = manager;
    }
    
    /**
     * This method returns the EPN manager.
     * 
     * @return The EPN manager
     */
    public EPNManager getManager() {
        return (_epnManager);
    }
    
    /**
     * This method initializes the EPN loader.
     */
    public void init() {
        
        if (_epnManager == null) {
            _epnManager = EPNManagerAccessor.getEPNManager();
        }
        
        if (_epnManager == null) {
            LOG.severe("Failed to obtain reference to EPNManager");
            throw new java.lang.IllegalStateException("Failed to obtain reference to EPNManager");
        }
        
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
                _epnManager.register(_network);
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
     * This method closes the EPN loader.
     */
    public void close() {
        
        if (_epnManager != null && _network != null) {
            try {
                _epnManager.unregister(_network.getName(), _network.getVersion());
            } catch (Throwable t) {
                if (LOG.isLoggable(Level.FINER)) {
                    LOG.log(Level.FINER, java.util.PropertyResourceBundle.getBundle(
                        "epn-loader-osgi.Messages").getString("EPN-LOADER-OSGI-3"), t);
                }
            }
        }
    }       
}
