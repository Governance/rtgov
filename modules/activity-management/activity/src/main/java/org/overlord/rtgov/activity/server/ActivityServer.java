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
package org.overlord.rtgov.activity.server;

import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.model.Context;

/**
 * This interface represents an activity server.
 *
 */
public interface ActivityServer {

    /**
     * This method stores the supplied activity events.
     * 
     * @param activities The activity events
     * @throws Exception Failed to store the activities
     */
    public void store(java.util.List<ActivityUnit> activities) throws Exception;
    
    /**
     * This method queries the server for an activity unit
     * with the supplied id.
     * 
     * @param id The activity unit id
     * @return The activity unit, or null if not found
     * @throws Exception Failed to get the activity unit
     */
    public ActivityUnit getActivityUnit(String id) throws Exception;
    
    /**
     * This method retrieves a set of activity events associated
     * with the supplied context value.
     * 
     * @param context The context value
     * @return The list of activities
     * @throws Exception Failed to retrieve the activities
     */
    public java.util.List<ActivityType> getActivityTypes(Context context) throws Exception;
    
    /**
     * This method retrieves a set of activity events associated
     * with the supplied context value and time range.
     * 
     * @param context The context value
     * @param from The 'from' timestamp
     * @param to The 'to' timestamp
     * @return The list of activities
     * @throws Exception Failed to retrieve the activities
     */
    public java.util.List<ActivityType> getActivityTypes(Context context,
                        long from, long to) throws Exception;
    
    /**
     * This method retrieves a set of activity events associated
     * with the supplied query.
     * 
     * @param query The query
     * @return The list of activities
     * @throws Exception Failed to query the activities
     */
    public java.util.List<ActivityType> query(QuerySpec query) throws Exception;
    
}
