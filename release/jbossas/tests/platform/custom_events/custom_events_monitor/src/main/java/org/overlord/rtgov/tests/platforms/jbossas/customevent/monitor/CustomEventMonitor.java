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
package org.overlord.rtgov.tests.platforms.jbossas.customevent.monitor;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.naming.InitialContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.overlord.rtgov.active.collection.ActiveCollectionManager;
import org.overlord.rtgov.active.collection.ActiveList;
import org.overlord.rtgov.epn.EPNManager;
import org.overlord.rtgov.epn.EventList;
import org.overlord.rtgov.epn.NotificationListener;
import org.overlord.rtgov.tests.platforms.jbossas.customevent.data.CustomActivityEvent;

/**
 * This is the custom event monitor that receives node notifications
 * from the EPN, and makes the events available via a REST API.
 *
 */
@Path("/monitor")
@ApplicationScoped
public class CustomEventMonitor implements NotificationListener {

    private static final String ACS_NAME = "CustomEventsResults";

    private static final String CUSTOM_EVENTS_PROCESSED_SUBJECT = "CustomEventsProcessed";
    private static final String CUSTOM_EVENTS_RESULTS_SUBJECT = "CustomEventsResults";

    private static final Logger LOG=Logger.getLogger(CustomEventMonitor.class.getName());
    
    private static final String ACS_MANAGER = "java:global/overlord-rtgov/ActiveCollectionManager";

    private EPNManager _epnManager=null;
    private ActiveCollectionManager _activeCollectionManager;
    
    private java.util.List<CustomActivityEvent> _customEvents=
                        new java.util.ArrayList<CustomActivityEvent>();
    private ActiveList _customEventsACS=null;
    
    /**
     * This is the default constructor.
     */
    public CustomEventMonitor() {
        
        try {
            InitialContext ctx=new InitialContext();
            
            _epnManager = (EPNManager)ctx.lookup(EPNManager.URI);

            _epnManager.addNotificationListener(CUSTOM_EVENTS_PROCESSED_SUBJECT, this);
            _epnManager.addNotificationListener(CUSTOM_EVENTS_RESULTS_SUBJECT, this);
            
            _activeCollectionManager = (ActiveCollectionManager)ctx.lookup(ACS_MANAGER);
            
            _customEventsACS = (ActiveList)_activeCollectionManager.getActiveCollection(ACS_NAME);
            
            LOG.info("Custom Active Collection="+_customEventsACS+" Name="+ACS_NAME);
            
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to initialize custom event monitor", e);
        }

    }
    
    /**
     * This method returns the list of custom activity events
     * and then resets the list.
     * 
     * @return The custom activity events
     */
    @GET
    @Path("/events")
    @Produces("application/json")
    public java.util.List<CustomActivityEvent> getCustomActivityEvents() {
        java.util.List<CustomActivityEvent> ret=new java.util.ArrayList<CustomActivityEvent>(_customEvents);
        
        _customEvents.clear();
        
        return (ret);
    }

    /**
     * This method returns the list of custom events from the active collection.
     * 
     * @return The custom events
     */
    @GET
    @Path("/acsresults")
    @Produces("application/json")
    public java.util.List<CustomActivityEvent> getACSResults() {
        java.util.List<CustomActivityEvent> ret=new java.util.ArrayList<CustomActivityEvent>();

        LOG.info("Returning Custom Active Collection results (size="+_customEventsACS.getSize()+") ac="+_customEventsACS);
        
        for (Object obj : _customEventsACS) {
            if (obj instanceof CustomActivityEvent) {
                ret.add((CustomActivityEvent)obj);
            }
        }
        
        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    public void notify(String subject, EventList events) {
        System.out.println(">>> CUSTOM EVENT MONITOR: ("+subject+")");
        
        for (java.io.Serializable event : events) {
            System.out.println("\t"+event);
            
            if (event instanceof CustomActivityEvent) {
                _customEvents.add((CustomActivityEvent)event);
            }
        }
    }       

}
