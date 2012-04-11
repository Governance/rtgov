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
package org.savara.bam.activity.server.spi;

import org.savara.bam.activity.model.Activity;
import org.savara.bam.activity.server.ActivityQuery;

/**
 * This interface represents a persistence storage for Activity
 * Events.
 *
 */
public interface ActivityStore {

    /**
     * This method stores the list of activity events.
     * 
     * @param activities The list of activity events to store
     * @throws Exception Failed to store events
     */
    public void store(java.util.List<Activity> activities) throws Exception;
    
    /**
     * This method queries the persistent store for activity events
     * that satisfy the supplied query.
     * 
     * @param query The query
     * @return The list of activities that satisfy the query
     * @throws Exception Failed to query events
     */
    public java.util.List<Activity> query(ActivityQuery query) throws Exception;
    
}
