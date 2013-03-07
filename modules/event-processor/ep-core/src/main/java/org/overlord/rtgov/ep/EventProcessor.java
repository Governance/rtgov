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
package org.overlord.rtgov.ep;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.overlord.rtgov.ep.service.EPService;

/**
 * This interface defines an event processor responsible
 * for processing events, and where appropriate, forwarding
 * results to other awaiting event processors.
 *
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class EventProcessor {
    
    private java.util.Map<String,EPService> _services=
                            new java.util.HashMap<String,EPService>();
    
    /**
     * This method returns the map of names to services.
     * 
     * @return The services
     */
    public java.util.Map<String,EPService> getServices() {
        return (_services);
    }
    
    /**
     * This method sets the map of names to services.
     * 
     * @param services The services
     */
    public void setServices(java.util.Map<String,EPService> services) {
        _services = services;
    }
    
    /**
     * This method initializes the event processor.
     * 
     * @throws Exception Failed to initialize
     */
    public void init() throws Exception {
        
        // Iterate through the services initializing them
        for (EPService service : _services.values()) {
            service.init();
        }
    }
    
    /**
     * This method processes the supplied event, and optionally
     * returns a transformed representation to be forwarded to
     * other processors. If multiple objects result from the,
     * transformation, then they should be returned in a collection.
     * <p>
     * If the event cannot be processed at
     * this time, then an exception should be thrown to initiate
     * a retry. The number of remaining retries is supplied,
     * to enable the processor to take appropriate error
     * reporting action.
     *
     * @param source The source event processor name that generated the event
     * @param event The event to process
     * @param retriesLeft The number of retries left
     * @return The optional transformed representation of the event
     * @throws Exception Failed to process event, requesting retry
     */
    public abstract java.io.Serializable process(String source,
                java.io.Serializable event, int retriesLeft) throws Exception;

}
