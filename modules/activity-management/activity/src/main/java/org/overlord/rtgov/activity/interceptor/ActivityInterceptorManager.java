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
package org.overlord.rtgov.activity.interceptor;

import org.overlord.rtgov.activity.model.ActivityType;

/**
 * This interface manages a set of ActivityInterceptor
 * implementations.
 *
 */
public interface ActivityInterceptorManager {
    
    /**
     * This method registers the activity interceptor.
     * 
     * @param ai The activity interceptor
     * @throws Exception Failed to register
     */
    public void register(ActivityInterceptor ai) throws Exception;
    
    /**
     * This method returns the activity interceptor associated
     * with the supplied name.
     * 
     * @param name The name
     * @return The activity interceptor, or null if not found
     */
    public ActivityInterceptor getActivityInterceptor(String name);
    
    /**
     * This method supplies the supplied activity event to
     * the set of interceptors registered with the manager.
     * 
     * @param actType The activity event
     * @throws Exception Failed to process activity event
     */
    public void process(ActivityType actType) throws Exception;
    
    /**
     * This method registers the activity interceptor.
     * 
     * @param ai The activity interceptor
     * @throws Exception Failed to unregister
     */
    public void unregister(ActivityInterceptor ai) throws Exception;
    
}
