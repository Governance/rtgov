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
package org.savara.bam.epn;

/**
 * This interfaces represents the Event Process Network
 * Manager.
 *
 */
public interface EPNManager {

    /**
     * This method registers a network.
     * 
     * @param network The network
     * @throws Exception Failed to register the network
     */
    public void register(Network network) throws Exception;
    
    /**
     * This method unregisters a network.
     * 
     * @param networkName The network name
     * @throws Exception Failed to unregister the network
     */
    public void unregister(String networkName) throws Exception;
    
    /**
     * This method registers a node listener.
     * 
     * @param l The listener
     */
    public void addNodeListener(NodeListener l);
    
    /**
     * This method unregisters a node listener.
     * 
     * @param l The listener
     */
    public void removeNodeListener(NodeListener l);
    
    /**
     * This method queues up the supplied events to be processed
     * by the specified network.
     * 
     * @param network The name of the network
     * @param events The list of events to be processed
     * @throws Exception Failed to enqueue the events
     */
    public void enqueue(String network,
                 java.util.List<java.io.Serializable> events) throws Exception;
    
    /**
     * This method closes the manager.
     * 
     * @throws Exception Failed to close manager
     */
    public void close() throws Exception;
    
}
