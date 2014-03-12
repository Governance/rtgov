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
package org.overlord.rtgov.call.trace.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.overlord.rtgov.call.trace.CallTraceService;

import java.util.HashSet;
import java.util.Set;

/**
 * This class represents the REST application for the call trace
 * service.
 *
 */
@ApplicationPath("/call/trace")
public class RESTCallTraceServerApplication extends Application {

    private static Set<Object> _singletons = new HashSet<Object>();
    private Set<Class<?>> _empty = new HashSet<Class<?>>();

    /**
     * This is the default constructor.
     */
    public RESTCallTraceServerApplication() {
        synchronized (_singletons) {
            if (_singletons.isEmpty()) {
                _singletons.add(new RESTCallTraceServer());
            }
        }
    }

    /**
     * This method sets the call trace service.
     * 
     * @param cts The call trace service
     */
    public void setCallTraceService(CallTraceService cts) {
        synchronized (_singletons) {
            RESTCallTraceServer server=null;
            
            if (!_singletons.isEmpty()) {
                server = (RESTCallTraceServer)_singletons.iterator().next();
            } else {
                server = new RESTCallTraceServer();                
                _singletons.add(server);
            }
            
            server.setCallTraceService(cts);
        }
    }

    /**
     * This method returns the call trace service.
     * 
     * @return The call trace service
     */
    public CallTraceService getCallTraceService() {
        CallTraceService ret=null;
        
        synchronized (_singletons) {
            if (!_singletons.isEmpty()) {
                RESTCallTraceServer server = (RESTCallTraceServer)_singletons.iterator().next();
                
                ret = server.getCallTraceService();
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
