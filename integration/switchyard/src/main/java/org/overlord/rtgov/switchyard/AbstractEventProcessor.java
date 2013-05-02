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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.overlord.rtgov.activity.collector.ActivityCollector;
import org.overlord.rtgov.activity.model.ActivityType;
import org.switchyard.event.EventObserver;

/**
 * This interface represents an event processor that is used to process
 * events observed from applications running on SwitchYard.
 *
 */
public abstract class AbstractEventProcessor implements EventObserver, EventProcessor {
	
	private static final Logger LOG=Logger.getLogger(AbstractEventProcessor.class.getName());

	private Class<?> _class=null;
	
	private ActivityCollector _collector=null;
	
	/**
	 * This is the constructor for the abstract event processor.
	 *  
	 * @param cls The class associated with the processor
	 */
	public AbstractEventProcessor(Class<?> cls) {
		_class = cls;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void init(ActivityCollector collector) {
		_collector = collector;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Class<?> getEventType() {
		return (_class);
	}
	
	/**
	 * This method returns the activity collector.
	 * 
	 * @return The activity collector
	 */
	protected ActivityCollector getActivityCollector() {
		return (_collector);
	}
	
	/**
	 * This method records the activity type.
	 * 
	 * @param at The activity type
	 */
	protected void recordActivity(Object event, ActivityType at) {
		
		if (LOG.isLoggable(Level.FINEST)) {
			LOG.finest("Record event '"+event+"' as activity type: "+at);
		}
		
        try {
        	getActivityCollector().record(at);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to report activity event", e);
        }
	}
}
