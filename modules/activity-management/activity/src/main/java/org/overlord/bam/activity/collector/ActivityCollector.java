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
package org.overlord.bam.activity.collector;

import org.overlord.bam.activity.model.ActivityType;

/**
 * This interface represents an activity event collector.
 *
 */
public interface ActivityCollector {

    /**
     * This method sets the collector context.
     *
     * @param cc The collector context
     */
    public void setCollectorContext(CollectorContext cc);
        
    /**
     * This method gets the collector context.
     *
     * @return The collector context
     */
    public CollectorContext getCollectorContext();
        
    /**
     * This method sets the activity unit logger.
     *
     * @param activityUnitLogger The activity unit logger
     */
    public void setActivityUnitLogger(ActivityUnitLogger activityUnitLogger);
        
    /**
     * This method gets the activity unit logger.
     *
     * @return The activity unit logger
     */
    public ActivityUnitLogger getActivityUnitLogger();
     
    /**
     * This method can be used to create an application controlled
     * scope. Within this scope, all activity types will
     * be recorded in a single activity unit. If the method returns
     * true, then it indicates the application started a
     * scope, and is therefore now responsible for ending that
     * scope at a suitable time using the 'endScope' method.
     * If however a 'false' value is returned from calling this
     * method, then the application should not call the 'endScope'
     * method, as it implies a scope was started by
     * some other means.
     * 
     * @return Whether the scope was started
     */
    public boolean startScope();
    
    /**
     * This method completes the scope. However this
     * method should only be called by the application if it
     * previously called the 'startScope' method, and received
     * a response that indicated that a scope had been started.
     */
    public void endScope();
    
    /**
     * This method records the supplied activity type. If a
     * scope has not been explicitly started, using the 'startScope'
     * then calling this method may result in a scope
     * being started automatically. Otherwise the activity type may just
     * be recorded as a single event within an activity unit.
     * 
     * @param actType The activity type
     */
    public void record(ActivityType actType);
    
}
