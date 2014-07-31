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
package org.overlord.rtgov.internal.acs.loader.jee;

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
import org.overlord.rtgov.active.collection.AbstractACSLoader;
import org.overlord.rtgov.active.collection.ActiveCollectionManager;
import org.overlord.rtgov.active.collection.ActiveCollectionSource;
import org.overlord.rtgov.active.collection.util.ActiveCollectionUtil;

/**
 * This class provides the capability to load an Active Collection Source from a
 * defined file.
 *
 */
@ApplicationScoped
@Singleton
@Startup
@ConcurrencyManagement(BEAN)
public class JEEACSLoader extends AbstractACSLoader {
    
    private static final Logger LOG=Logger.getLogger(JEEACSLoader.class.getName());
    
    private static final String ACS_JSON = "acs.json";

    private java.util.List<ActiveCollectionSource> _activeCollectionSources=null;
    
    private org.overlord.commons.services.ServiceListener<ActiveCollectionManager> _listener;

    /**
     * The constructor.
     */
    public JEEACSLoader() {
    }
    
    /**
     * This method initializes the ACS loader.
     */
    @PostConstruct
    public void init() {
        
        _listener = new org.overlord.commons.services.ServiceListener<ActiveCollectionManager>() {

            @Override
            public void registered(ActiveCollectionManager service) {
                registerActiveCollectionSource(service);
            }

            @Override
            public void unregistered(ActiveCollectionManager service) {
                unregisterActiveCollectionSource(service);
            }
        };
        
        ServiceRegistryUtil.addServiceListener(ActiveCollectionManager.class, _listener);
    }
    
    /**
     * This method registers the activity collection sources with the manager.
     * 
     * @param acmManager The active collection manager
     */
    protected void registerActiveCollectionSource(ActiveCollectionManager acmManager) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Register ActiveCollectionSource");
        }
        
        try {
            java.io.InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(ACS_JSON);
            
            if (is == null) {
                LOG.severe(java.util.PropertyResourceBundle.getBundle(
                        "acs-loader-jee.Messages").getString("ACS-LOADER-JEE-1"));
            } else {
                byte[] b=new byte[is.available()];
                is.read(b);
                is.close();
                
                _activeCollectionSources = ActiveCollectionUtil.deserializeACS(b);
                
                if (_activeCollectionSources == null) {
                    LOG.severe(java.util.PropertyResourceBundle.getBundle(
                            "acs-loader-jee.Messages").getString("ACS-LOADER-JEE-2"));
                } else {
                    for (ActiveCollectionSource acs : _activeCollectionSources) {
                        // Pre-initialize the source to avoid any contextual class
                        // loading issues. Within JEE, the registration of the source
                        // will be done in the context of the core war, while as the
                        // source requires the classloading context associated
                        // with the ActiveCollectionSource deployment.
                        preInit(acs);
                        
                        acmManager.register(acs);
                    }
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                    "acs-loader-jee.Messages").getString("ACS-LOADER-JEE-3"), e);
        }
    }
    
    /**
     * This method unregisters the active collection manager.
     * 
     * @param acmManager Active collection manager
     */
    protected void unregisterActiveCollectionSource(ActiveCollectionManager acmManager) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Unregister ActiveCollectionSource");
        }
        
        if (acmManager != null && _activeCollectionSources != null) {
            try {
                for (ActiveCollectionSource acs : _activeCollectionSources) {
                    acmManager.unregister(acs);
                }
                
                _activeCollectionSources = null;
            } catch (Throwable t) {
                if (LOG.isLoggable(Level.FINER)) {
                    LOG.log(Level.FINER, java.util.PropertyResourceBundle.getBundle(
                            "acs-loader-jee.Messages").getString("ACS-LOADER-JEE-4"), t);
                }
            }
        }
    }       

    /**
     * This method closes the EPN loader.
     */
    @PreDestroy
    public void close() {
        if (_activeCollectionSources != null) {
            ActiveCollectionManager acManager=ServiceRegistryUtil.getSingleService(ActiveCollectionManager.class);
            
            if (acManager != null) {
                unregisterActiveCollectionSource(acManager);
            }
        }
        
        ServiceRegistryUtil.removeServiceListener(_listener);
        _listener = null;
    }       
}
