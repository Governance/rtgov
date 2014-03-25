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
package org.overlord.rtgov.activity.server.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.overlord.rtgov.activity.server.ActivityServer;

import java.util.HashSet;
import java.util.Set;

/**
 * This class represents the REST application for the activity
 * server.
 *
 */
@ApplicationPath("/activity")
public class RESTActivityServerApplication extends Application {

    private static Set<Object> _singletons = new HashSet<Object>();
    private Set<Class<?>> _empty = new HashSet<Class<?>>();

    /**
     * This is the default constructor.
     */
    public RESTActivityServerApplication() {
        synchronized (_singletons) {
            if (_singletons.isEmpty()) {
                _singletons.add(new RESTActivityServer());
            }
        }
    }
    
    /**
     * This method sets the activity server.
     * 
     * @param as The activity server
     */
    public void setActivityServer(ActivityServer as) {
        synchronized (_singletons) {
            RESTActivityServer server=null;
            
            if (!_singletons.isEmpty()) {
                server = (RESTActivityServer)_singletons.iterator().next();
            } else {
                server = new RESTActivityServer();                
                _singletons.add(server);
            }
            
            server.setActivityServer(as);
        }
    }

    /**
     * This method returns the activity server.
     * 
     * @return The activity server
     */
    public ActivityServer getActivityServer() {
        ActivityServer ret=null;
        
        synchronized (_singletons) {
            if (!_singletons.isEmpty()) {
                RESTActivityServer server = (RESTActivityServer)_singletons.iterator().next();
                
                ret = server.getActivityServer();
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
