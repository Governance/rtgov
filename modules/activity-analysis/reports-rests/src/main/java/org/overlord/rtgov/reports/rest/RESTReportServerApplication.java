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
package org.overlord.rtgov.reports.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.overlord.rtgov.reports.ReportManager;

import java.util.HashSet;
import java.util.Set;

/**
 * This class represents the REST application for the report
 * server.
 *
 */
@ApplicationPath("/report")
public class RESTReportServerApplication extends Application {

    private static Set<Object> _singletons = new HashSet<Object>();
    private Set<Class<?>> _empty = new HashSet<Class<?>>();

    /**
     * This is the default constructor.
     */
    public RESTReportServerApplication() {
        synchronized (_singletons) {
            if (_singletons.isEmpty()) {
                _singletons.add(new RESTReportServer());
            }
        }
    }

    /**
     * This method sets the report manager.
     * 
     * @param rm The report manager
     */
    public void setReportManager(ReportManager rm) {
        synchronized (_singletons) {
            RESTReportServer server=null;
            
            if (!_singletons.isEmpty()) {
                server = (RESTReportServer)_singletons.iterator().next();
            } else {
                server = new RESTReportServer();                
                _singletons.add(server);
            }
            
            server.setReportManager(rm);
        }
    }

    /**
     * This method returns the report manager.
     * 
     * @return The report manager
     */
    public ReportManager getReportManager() {
        ReportManager ret=null;
        
        synchronized (_singletons) {
            if (!_singletons.isEmpty()) {
                RESTReportServer server = (RESTReportServer)_singletons.iterator().next();
                
                ret = server.getReportManager();
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
