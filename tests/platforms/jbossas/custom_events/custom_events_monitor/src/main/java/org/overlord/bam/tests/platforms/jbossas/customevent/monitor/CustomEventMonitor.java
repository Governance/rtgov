/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-12, Red Hat Middleware LLC, and others contributors as indicated
 * by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.overlord.bam.tests.platforms.jbossas.customevent.monitor;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.naming.InitialContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.overlord.bam.active.collection.ActiveCollectionManager;
import org.overlord.bam.active.collection.ActiveList;
import org.overlord.bam.epn.EPNManager;
import org.overlord.bam.epn.EventList;
import org.overlord.bam.epn.NodeListener;
import org.overlord.bam.epn.NotifyType;
import org.overlord.bam.tests.platforms.jbossas.customevent.data.CustomActivityEvent;

/**
 * This is the custom event monitor that receives node notifications
 * from the EPN, and makes the events available via a REST API.
 *
 */
@Path("/monitor")
@ApplicationScoped
public class CustomEventMonitor implements NodeListener {

    private static final String ACS_NAME = "CustomEvents";

	private static final String CUSTOM_EVENTS_EPN = "CustomEventsEPN";

    private static final Logger LOG=Logger.getLogger(CustomEventMonitor.class.getName());
    
    private static final String EPN_MANAGER = "java:global/overlord-bam/EPNManager";
    private static final String ACS_MANAGER = "java:global/overlord-bam/ActiveCollectionManager";

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
            
            _epnManager = (EPNManager)ctx.lookup(EPN_MANAGER);

            _epnManager.addNodeListener(CUSTOM_EVENTS_EPN, this);
            
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

        LOG.info("Returning Custom Active Collection results (size="+_customEventsACS.size()+") ac="+_customEventsACS);
        
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
    public void notify(String network, String version, String node,
            NotifyType type, EventList events) {
        System.out.println(">>> CUSTOM EVENT MONITOR: ("+type+")");
        
        for (java.io.Serializable event : events) {
            System.out.println("\t"+event);
            
            if (event instanceof CustomActivityEvent) {
                _customEvents.add((CustomActivityEvent)event);
            }
        }
    }       

}
