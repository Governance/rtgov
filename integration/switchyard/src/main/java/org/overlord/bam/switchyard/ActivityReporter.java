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
package org.overlord.bam.switchyard;

/**
 * This interface represents the capability for recording
 * activity information from a switchyard application.
 *
 */
public interface ActivityReporter {

    /**
     * This method can be used to report general information.
     * 
     * @param info The information
     */
    public void logInfo(String info);
    
    /**
     * This method can be used to report warning information.
     * 
     * @param warning The warning description
     */
    public void logWarning(String warning);
    
    /**
     * This method can be used to report error information.
     * 
     * @param error The error description
     */
    public void logError(String error);
    
    /**
     * This method can be used to report activity information.
     * 
     * @param type The activity type
     * @param props The properties
     */
    public void logActivity(String type, java.util.Map<String,String> props);

}
