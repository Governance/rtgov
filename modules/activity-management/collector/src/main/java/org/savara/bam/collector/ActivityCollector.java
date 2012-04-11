/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-11, Red Hat Middleware LLC, and others contributors as indicated
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
package org.savara.bam.collector;

import org.savara.bam.activity.model.ActivityType;

/**
 * This interface represents an activity event collector.
 *
 */
public interface ActivityCollector {

	/**
	 * This method associates a transaction id and optional principal
	 * with the current thread, such that any subsequent activity
	 * types that are recorded will be associated with this id.
	 */
	public void startTransaction();
	
	/**
	 * This method clears the transaction id and principal associated
	 * with the current thread.
	 */
	public void endTransaction();
	
	/**
	 * This method records the supplied activity event.
	 * 
	 * @param actType The activity type
	 */
	public void record(ActivityType actType);
	
}
