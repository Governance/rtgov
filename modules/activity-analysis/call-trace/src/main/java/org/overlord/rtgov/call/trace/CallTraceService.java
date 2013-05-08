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
package org.overlord.rtgov.call.trace;

import org.overlord.rtgov.activity.server.ActivityServer;
import org.overlord.rtgov.call.trace.model.CallTrace;

/**
 * This interface is responsible for deriving a call trace from
 * activity information.
 *
 */
public interface CallTraceService {
    
    /**
     * This method sets the activity server.
     * 
     * @param as The activity server
     */
    public void setActivityServer(ActivityServer as);
    
    /**
     * This method gets the activity server.
     * 
     * @return The activity server
     */
    public ActivityServer getActivityServer();
    
    /**
     * This method creates a call trace associated with the
     * supplied correlation value.
     * 
     * @param correlation The correlation value
     * @return The call trace, or null if not found
     * @throws Exception Failed to create call trace
     */
    public CallTrace createCallTrace(String correlation) 
                            throws Exception;

}
