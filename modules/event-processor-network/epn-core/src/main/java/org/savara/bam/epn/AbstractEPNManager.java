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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.savara.bam.epn.internal.EventList;

/**
 * This class represents the abstract Event Process Network Manager
 * used as the base for any concrete implementation.
 *
 */
public abstract class AbstractEPNManager implements EPNManager {
    
    private static final Logger LOG=Logger.getLogger(AbstractEPNManager.class.getName());

    private java.util.Map<String, Network> _networkMap=new java.util.HashMap<String, Network>();
    private java.util.List<NodeListener> _nodeListeners=
                        new java.util.Vector<NodeListener>();
    
    /**
     * This method returns the Event Processor Network Context.
     * 
     * @return The context
     */
    protected abstract EPNContext getContext();
    
    /**
     * {@inheritDoc}
     */
    public void register(Network network) throws Exception {
        
        LOG.info("Registering EPN network '"+network.getName()+"'");
        
        _networkMap.put(network.getName(), network);
        
        network.init(getContext());
    }

    /**
     * {@inheritDoc}
     */
    public void unregister(String networkName) throws Exception {
        
        LOG.info("Unregistering EPN network '"+networkName+"'");

        _networkMap.remove(networkName);
    }
    
    /**
     * {@inheritDoc}
     */
    public void addNodeListener(NodeListener l) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Register node listener="+l);
        }
        _nodeListeners.add(l);
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeNodeListener(NodeListener l) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Unregister node listener="+l);
        }
        _nodeListeners.remove(l);
    }
    
    /**
     * This method returns the network associated with the
     * supplied name.
     * 
     * @param name The network name
     * @return The network, or null if not found
     */
    protected Network getNetwork(String name) {
        return (_networkMap.get(name));
    }

    /**
     * This method returns the node associated with the
     * supplied network and node name.
     * 
     * @param networkName The network name
     * @param nodeName The node name
     * @return The node, or null if not found
     * @throws Exception Failed to find the specified node
     */
    protected Node getNode(String networkName, String nodeName) throws Exception {
        Network net=getNetwork(networkName);
        
        if (net == null) {
            throw new Exception("No network '"+networkName+"' was found");
        }
        
        Node node=net.getNodes().get(nodeName);
        
        if (node == null) {
            throw new Exception("No node '"+nodeName+"' was found in network '"+networkName+"'");
        }
        
        return (node);
    }

    /**
     * This method dispatches a set of events directly to the supplied
     * node.
     * 
     * @param networkName The network name
     * @param nodeName The node name
     * @param node The node
     * @param source The source node, or null if sending to root
     * @param events The list of events to be processed
     * @param retriesLeft The number of retries left
     * @return The events to retry, or null if no retries necessary
     * @throws Exception Failed to dispatch the events for processing
     */
    protected EventList process(String networkName, String nodeName,
                    Node node, String source, EventList events,
                            int retriesLeft) throws Exception {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Process events on network="+networkName+" node="+nodeName
                    +" source="+source+" retriesLeft="+retriesLeft+" events="+events);
        }

        EventList ret=node.process(getContext(), source, events, retriesLeft);
        
        if (node.getNotificationEnabled()
                && (ret == null || ret.size() < events.size())) { 
            EventList notify=null;
            
            if (ret != null) {
                EventList processed = new EventList();
                
                for (java.io.Serializable event : events) {
                    if (!ret.contains(event)) {
                        processed.add(event);
                    }
                }
                
                if (processed.size() > 0) {
                    notify = processed;
                }
            } else {
                notify = events;
            }
            
            if (notify != null) {
                notifyEventsProcessed(networkName, nodeName, notify);
            }
        }
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Processed events on network="+networkName+" node="+nodeName
                    +" source="+source+" ret="+ret);
        }

        return (ret);
    }

    /**
     * This method sends a notification that the supplied list of events have
     * been processed by the named network and node.
     * 
     * @param networkName The network name
     * @param nodeName The node name
     * @param processed The list of processed events
     */
    protected void notifyEventsProcessed(String networkName, String nodeName, EventList processed) {
        dispatchEventsProcessedToListeners(networkName, nodeName, processed);
    }
    
    /**
     * This method dispatches the notifications to the registered node listeners.
     * 
     * @param networkName The network name
     * @param nodeName The node name
     * @param processed The list of processed events
     */
    protected void dispatchEventsProcessedToListeners(String networkName, String nodeName, EventList processed) {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Notify processed events on network="+networkName+" node="+nodeName
                    +" processed="+processed);
        }

        for (NodeListener nl : _nodeListeners) {
            nl.eventsProcessed(networkName, nodeName, processed);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void close() throws Exception {
    }

}
