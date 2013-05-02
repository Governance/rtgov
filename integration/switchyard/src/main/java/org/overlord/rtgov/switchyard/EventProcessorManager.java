/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-13, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.rtgov.switchyard;

import static javax.ejb.ConcurrencyManagementType.BEAN;

import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.InitialContext;

import org.overlord.rtgov.activity.collector.ActivityCollector;

/**
 * This class is responsible for registering the configured set of
 * event processor implementations against 
 *
 */
@ApplicationScoped
@Singleton
@Startup
@ConcurrencyManagement(BEAN)
public class EventProcessorManager {
	
	private static final String SWITCHAYRD_MANAGEMENT_LOCAL = "org.switchyard.admin:type=Management.Local";

	private static final Logger LOG=Logger.getLogger(EventProcessorManager.class.getName());

    private static final String ACTIVITY_COLLECTOR = "java:global/overlord-rtgov/ActivityCollector";

    @Resource(lookup=ACTIVITY_COLLECTOR)
    private ActivityCollector _activityCollector=null;

    @Inject @Any
	private Instance<EventProcessor> _eventProcessors=null;
	
	/**
	 * Initialize the event processors.
	 */
	@PostConstruct
	public void init() {
        
        try {
            InitialContext ctx=new InitialContext();
            
            _activityCollector = (ActivityCollector)ctx.lookup(ACTIVITY_COLLECTOR);
            
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to initialize activity collector", e);
        }

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("SwitchYard EventProcessorManager Initialized with collector="+_activityCollector);
        }

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
        ObjectName objname=null;
        
        try {
            objname = new ObjectName(SWITCHAYRD_MANAGEMENT_LOCAL);
            
            for (EventProcessor ep : _eventProcessors) {
            	
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("SwitchYard EventProcessorManager register event processor="+ep);
                }

                // Initialize with the activity collector
            	ep.init(_activityCollector);
            	
	            java.util.List<Class<?>> eventTypes=new java.util.ArrayList<Class<?>>();
	            eventTypes.add(ep.getEventType());               
	            
	            Object[] params={ep, eventTypes};
	            
	            String[] types={org.switchyard.event.EventObserver.class.getName(),
	            		java.util.List.class.getName()};
	            
	            mbs.invoke(objname, "addObserver", params, types);
            }
            
        } catch (Exception e) {
        	if (LOG.isLoggable(Level.FINE)) {
        		LOG.log(Level.FINE, "Failed to register SwitchYard event observer via MBean", e);
        	}
        }

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("SwitchYard EventProcessorManager Initialization Completed");
        }
	}
	
	/**
	 * Close the event processors.
	 */
	@PreDestroy
	public void close() {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
        ObjectName objname=null;
        
        try {
            objname = new ObjectName(SWITCHAYRD_MANAGEMENT_LOCAL);
            
            for (EventProcessor ep : _eventProcessors) {
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("SwitchYard EventProcessorManager unregister event processor="+ep);
                }

                Object[] params={ep};
	            
	            String[] types={org.switchyard.event.EventObserver.class.getName()};
	            
	            mbs.invoke(objname, "removeObserver", params, types);
           }
            
        } catch (Exception e) {
        	if (LOG.isLoggable(Level.FINE)) {
        		LOG.log(Level.FINE, "Failed to unregister SwitchYard event observer via MBean", e);
        	}
        }

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("SwitchYard EventProcessorManager Close Completed");
        }
	}
}
