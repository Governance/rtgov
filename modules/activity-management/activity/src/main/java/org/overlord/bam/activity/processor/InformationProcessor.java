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
package org.overlord.bam.activity.processor;

import org.overlord.bam.activity.model.ActivityType;

/**
 * This interface represents an information processor.
 *
 */
public interface InformationProcessor {

    /**
     * This method returns the name of the information processor.
     * 
     * @return The name
     */
    public String getName();
    
    /**
     * This method returns the version of the information processor.
     * 
     * @return The version
     */
    public String getVersion();
    
    /**
     * Initialize the information processor.
     * 
     * @throws Exception Failed to initialize
     */
    public void init() throws Exception;
    
    /**
     * This method determines whether the specified type
     * is handled by the information processor.
     * 
     * @param type The type
     * @return Whether the information processor handles the type
     */
    public boolean isSupported(String type);
    
    /**
     * This method processes supplied information to
     * extract relevant details, and then return an
     * appropriate representation of that information
     * for public distribution.
     * 
     * @param type The information type
     * @param info The information to be processed
     * @param actType The activity type to be annotated with
     *              details extracted from the information
     * @return The public representation of the information
     */
    public String process(String type, Object info, ActivityType actType);
    
    /**
     * Close the information processor.
     * 
     * @throws Exception Failed to close
     */
    public void close() throws Exception;

}
