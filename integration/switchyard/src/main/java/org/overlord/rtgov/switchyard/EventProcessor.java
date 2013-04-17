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

import org.overlord.rtgov.activity.collector.ActivityCollector;

/**
 * This interface represents an event processor that is used to process
 * events observed from applications running on SwitchYard.
 *
 */
public interface EventProcessor {

	/**
	 * This method initializes the event processor, supplying
	 * the activity collector to use for reporting activities.
	 * 
	 * @param collector The activity collector
	 */
	public void init(ActivityCollector collector);
	
	/**
	 * This method returns the event type that can be handled
	 * by this event processor.
	 * 
	 * @return The event type associated with the processor
	 */
	public Class<?> getEventType();
	
}
