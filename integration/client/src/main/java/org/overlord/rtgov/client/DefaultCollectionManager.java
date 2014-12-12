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
package org.overlord.rtgov.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.overlord.commons.services.ServiceListener;
import org.overlord.commons.services.ServiceRegistryUtil;
import org.overlord.rtgov.active.collection.ActiveCollection;
import org.overlord.rtgov.active.collection.ActiveCollectionManager;
import org.overlord.rtgov.active.collection.ActiveList;
import org.overlord.rtgov.active.collection.ActiveMap;

/**
 * This interface provides access to active collections.
 *
 */
public class DefaultCollectionManager implements CollectionManager {

    private static final Logger LOG=Logger.getLogger(DefaultCollectionManager.class.getName());
    
    private ActiveCollectionManager _activeCollectionManager=null;
    
    private boolean _initialized=false;

    protected void init() {
        ServiceRegistryUtil.addServiceListener(ActiveCollectionManager.class, new ServiceListener<ActiveCollectionManager>() {

            @Override
            public void registered(ActiveCollectionManager service) {
                _activeCollectionManager = service;
                
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Default Collection Manager ="+_activeCollectionManager);
                }
            }

            @Override
            public void unregistered(ActiveCollectionManager service) {
                _activeCollectionManager = null;
                
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Default Collection Manager unset");
                }
            }            
        });
        
        _initialized = true;
    }

    /**
     * This method returns the active list associated with the
     * supplied name.
     * 
     * @param name The name
     * @return The list, or null if not found
     */
    public ActiveList getList(String name) {
        ActiveCollection ac=getCollection(name);
        
        if (ac instanceof ActiveList) {
            return ((ActiveList)ac);
        } else {
            LOG.warning("Active collection '"+name+"' is not a list: "+ac);
        }
        
        return null;
    }
    
    /**
     * This method returns the active map associated with the
     * supplied name.
     * 
     * @param name The name
     * @return The map, or null if not found
     */
    public ActiveMap getMap(String name) {
        ActiveCollection ac=getCollection(name);
        
        if (ac instanceof ActiveMap) {
            return ((ActiveMap)ac);
        } else {
            LOG.warning("Active collection '"+name+"' is not a map: "+ac);
        }
        
        return null;
    }
    
    /**
     * This method returns the active collection associated with the
     * supplied name.
     * 
     * @param name The name
     * @return The collection, or null if not found
     */
    protected ActiveCollection getCollection(String name) {
        
        if (!_initialized) {
            init();
        }
        
        if (_activeCollectionManager != null) {
            return (_activeCollectionManager.getActiveCollection(name));
        }
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Active collection manager not initialized");
        }
        
        return null;
    }
    
}
