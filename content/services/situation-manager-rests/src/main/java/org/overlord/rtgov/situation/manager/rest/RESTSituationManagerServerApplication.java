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
package org.overlord.rtgov.situation.manager.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.overlord.rtgov.situation.manager.SituationManager;

import java.util.HashSet;
import java.util.Set;

/**
 * This class represents the REST application for the situation manager
 * service.
 *
 */
@ApplicationPath("/situation/manager")
public class RESTSituationManagerServerApplication extends Application {

    private static Set<Object> _singletons = new HashSet<Object>();
    private Set<Class<?>> _empty = new HashSet<Class<?>>();

    /**
     * This is the default constructor.
     */
    public RESTSituationManagerServerApplication() {
        synchronized (_singletons) {
            if (_singletons.isEmpty()) {
                _singletons.add(new RESTSituationManagerServer());
            }
        }
    }

    /**
     * This method sets the situation manager.
     * 
     * @param sm The situation manager
     */
    public void setSituationManager(SituationManager sm) {
        synchronized (_singletons) {
            RESTSituationManagerServer server=null;
            
            if (!_singletons.isEmpty()) {
                server = (RESTSituationManagerServer)_singletons.iterator().next();
            } else {
                server = new RESTSituationManagerServer();                
                _singletons.add(server);
            }
            
            server.setSituationManager(sm);
        }
    }

    /**
     * This method returns the situation manager.
     * 
     * @return The situation manager
     */
    public SituationManager getSituationManager() {
        SituationManager ret=null;
        
        synchronized (_singletons) {
            if (!_singletons.isEmpty()) {
                RESTSituationManagerServer server = (RESTSituationManagerServer)_singletons.iterator().next();
                
                ret = server.getSituationManager();
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
