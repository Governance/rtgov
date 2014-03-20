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
package org.overlord.rtgov.active.collection.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.overlord.rtgov.active.collection.ActiveCollectionManager;

import java.util.HashSet;
import java.util.Set;

/**
 * This class represents the REST application for the active
 * collection manager.
 *
 */
@ApplicationPath("/acm")
public class RESTActiveCollectionServerApplication extends Application {

    private static Set<Object> _singletons = new HashSet<Object>();
    private Set<Class<?>> _empty = new HashSet<Class<?>>();

    /**
     * This is the default constructor.
     */
    public RESTActiveCollectionServerApplication() {
        synchronized (_singletons) {
            if (_singletons.isEmpty()) {
                _singletons.add(new RESTActiveCollectionServer());
            }
        }
    }

    /**
     * This method sets the active collection manager on the server.
     * 
     * @param acm The active collection manager
     */
    public void setActiveCollectionManager(ActiveCollectionManager acm) {
        synchronized (_singletons) {
            RESTActiveCollectionServer server=null;
            
            if (!_singletons.isEmpty()) {
                server = (RESTActiveCollectionServer)_singletons.iterator().next();
            } else {
                server = new RESTActiveCollectionServer();                
                _singletons.add(server);
            }
            
            server.setActiveCollectionManager(acm);
        }
    }

    /**
     * This method returns the active collection manager on the server.
     * 
     * @return The active collection manager
     */
    public ActiveCollectionManager getActiveCollectionManager() {
        ActiveCollectionManager ret=null;
        
        synchronized (_singletons) {
            if (!_singletons.isEmpty()) {
                RESTActiveCollectionServer server = (RESTActiveCollectionServer)_singletons.iterator().next();
                
                ret = server.getActiveCollectionManager();
            }
        }
        
        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Class<?>> getClasses() {
       return _empty;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Object> getSingletons() {
       return _singletons;
    }
}
