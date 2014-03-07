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
package org.overlord.rtgov.acs.loader.osgi;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.overlord.rtgov.active.collection.AbstractACSLoader;
import org.overlord.rtgov.active.collection.ActiveCollectionManager;
import org.overlord.rtgov.active.collection.ActiveCollectionManagerAccessor;
import org.overlord.rtgov.active.collection.ActiveCollectionSource;
import org.overlord.rtgov.active.collection.util.ActiveCollectionUtil;

/**
 * This class provides the activator capability for an Active Collection Source.
 *
 */
public class ACSActivator extends AbstractACSLoader implements BundleActivator {
    
    private static final Logger LOG=Logger.getLogger(ACSActivator.class.getName());
    
    private static final String ACS_JSON = "/acs.json";

    private ActiveCollectionManager _acmManager=null;
    
    private java.util.List<ActiveCollectionSource> _activeCollectionSources=null;
    
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
     * This method sets the active collection manager.
     * 
     * @param manager The active collection manager
     */
    public void setManager(ActiveCollectionManager manager) {
        _acmManager = manager;
    }
    
    /**
     * This method returns the active collection manager.
     * 
     * @return The active collection manager
     */
    public ActiveCollectionManager getManager() {
        return (_acmManager);
    }
    
    /**
     * This method initializes the EPN loader.
     */
    public void init() {
        
        if (_acmManager == null) {
            _acmManager = ActiveCollectionManagerAccessor.getActiveCollectionManager();
        }
        
        if (_acmManager == null) {
            LOG.severe("Failed to obtain reference to ActiveCollectionManager");
            throw new java.lang.IllegalStateException("Failed to obtain reference to ActiveCollectionManager");
        }
        
        java.lang.ClassLoader cl=Thread.currentThread().getContextClassLoader();
        
        try {
            java.io.InputStream is=ACSActivator.class.getResourceAsStream(ACS_JSON);
            
            if (is == null) {
                LOG.severe(java.util.PropertyResourceBundle.getBundle(
                        "acs-loader-osgi.Messages").getString("ACS-LOADER-OSGI-1"));
            } else {
                byte[] b=new byte[is.available()];
                is.read(b);
                is.close();
                
                // Set context class loader
                Thread.currentThread().setContextClassLoader(ACSActivator.class.getClassLoader());
                
                _activeCollectionSources = ActiveCollectionUtil.deserializeACS(b);
                
                if (_activeCollectionSources == null) {
                    LOG.severe(java.util.PropertyResourceBundle.getBundle(
                            "acs-loader-osgi.Messages").getString("ACS-LOADER-OSGI-2"));
                } else {
                    for (ActiveCollectionSource acs : _activeCollectionSources) {
                        // Pre-initialize the source to avoid any contextual class
                        // loading issues. Within JEE, the registration of the source
                        // will be done in the context of the core war, while as the
                        // source requires the classloading context associated
                        // with the ActiveCollectionSource deployment.
                        preInit(acs);
                        
                        _acmManager.register(acs);
                    }
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                    "acs-loader-osgi.Messages").getString("ACS-LOADER-OSGI-3"), e);
            
        } finally {
            Thread.currentThread().setContextClassLoader(cl);
        }
    }
    
    /**
     * This method closes the EPN loader.
     */
    public void close() {
        
        if (_acmManager != null && _activeCollectionSources != null) {
            try {
                for (ActiveCollectionSource acs : _activeCollectionSources) {
                    _acmManager.unregister(acs);
                }
            } catch (Throwable t) {
                if (LOG.isLoggable(Level.FINER)) {
                    LOG.log(Level.FINER, java.util.PropertyResourceBundle.getBundle(
                        "acs-loader-osgi.Messages").getString("ACS-LOADER-OSGI-4"), t);
                }
            }
        }
    }       
}
