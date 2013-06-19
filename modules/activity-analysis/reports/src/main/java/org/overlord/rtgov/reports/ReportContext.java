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
package org.overlord.rtgov.reports;

import org.overlord.rtgov.reports.model.Calendar;

/**
 * This interface represents the context available to the report generator.
 *
 */
public interface ReportContext {

    /**
     * This method locates a service based on the supplied
     * name.
     * 
     * @param name The name
     * @return The service, or null if not found
     */
    public Object getService(String name);
    
    /**
     * This method locates a service based on the supplied
     * type.
     * 
     * @param cls The class for the service
     * @return The service, or null if not found
     */
    public Object getService(Class<?> cls);
    
    /**
     * This method returns the named calendar configured for the
     * specified timezone. If the default calendar is required, then
     * use Calendar.DEFAULT as the name.
     * 
     * @param name The calendar name
     * @param timezone The timezone, or null if the default should be used
     * @return The business calendar
     */
    public Calendar getCalendar(String name, String timezone);
    
    /**
     * This method logs an error message.
     * 
     * @param mesg The error
     */
    public void logError(String mesg);

    /**
     * This method logs a warning message.
     * 
     * @param mesg The warning
     */
    public void logWarning(String mesg);

    /**
     * This method logs an info message.
     * 
     * @param mesg The info
     */
    public void logInfo(String mesg);

    /**
     * This method logs a debug message.
     * 
     * @param mesg The debug
     */
    public void logDebug(String mesg);

}
