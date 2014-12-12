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
package org.overlord.rtgov.internal.epn.loader.jee;

import static javax.ejb.ConcurrencyManagementType.BEAN;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;

import org.overlord.commons.services.ServiceRegistryUtil;
import org.overlord.rtgov.epn.AbstractEPNLoader;
import org.overlord.rtgov.epn.EPNManager;
import org.overlord.rtgov.epn.Network;
import org.overlord.rtgov.epn.util.NetworkUtil;

/**
 * This class provides the capability to load an EPN Network from a
 * defined file.
 *
 */
@ApplicationScoped
@Singleton
@Startup
@ConcurrencyManagement(BEAN)
public class JEEEPNLoader extends AbstractEPNLoader {
    
    private static final Logger LOG=Logger.getLogger(JEEEPNLoader.class.getName());
    
    private static final String EPN_JSON = "epn.json";

    private Network _network=null;
    
    private org.overlord.commons.services.ServiceListener<EPNManager> _listener;

    /**
     * The constructor.
     */
    public JEEEPNLoader() {
    }
    
    /**
     * This method initializes the EPN loader.
     */
    @PostConstruct
    public void init() {
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
     * This method registers the EPN with the EPNManager.
     * 
     * @param epnManager The EPN manager
     */
    protected void registerEPN(EPNManager epnManager) {
        
        try {
            java.io.InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(EPN_JSON);
            
            if (is == null) {
                LOG.severe(java.util.PropertyResourceBundle.getBundle(
                        "epn-loader-jee.Messages").getString("EPN-LOADER-JEE-1"));
            } else {
                byte[] b=new byte[is.available()];
                is.read(b);
                is.close();
                
                _network=NetworkUtil.deserialize(b);
                
                // Pre-initialize the network to avoid any contextual class
                // loading issues. Within JEE, the registration of the network
                // will be done in the context of the core war, while as the
                // event processors require the classloading context associated
                // with the network deployment.
                preInit(_network);
                
                // TODO: Do we need to halt the deployment due to failures? (RTGOV-199)
                epnManager.register(_network);
            }
        } catch (Exception e) {
            String mesg=java.util.PropertyResourceBundle.getBundle(
                    "epn-loader-jee.Messages").getString("EPN-LOADER-JEE-2");
            
            LOG.log(Level.SEVERE, mesg, e);
            
            // Throw runtime exception to cause the deployment to fail
            throw new RuntimeException(mesg, e);
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
                            "epn-loader-jee.Messages").getString("EPN-LOADER-JEE-3"), t);
                }
            }
        }
    }       

    /**
     * This method closes the EPN loader.
     */
    @PreDestroy
    public void close() {
        if (_network != null) {
            EPNManager epnManager=ServiceRegistryUtil.getSingleService(EPNManager.class);
            
            if (epnManager != null) {
                unregisterEPN(epnManager);
            }
        }
        
        ServiceRegistryUtil.removeServiceListener(_listener);
        _listener = null;
    }       
}
