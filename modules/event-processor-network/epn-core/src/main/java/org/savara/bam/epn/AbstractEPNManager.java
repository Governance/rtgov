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

import org.savara.bam.epn.internal.EventList;


public abstract class AbstractEPNManager implements EPNManager {

    private java.util.Map<String, Network> _networkMap=new java.util.HashMap<String, Network>();
    private java.util.List<NodeListener> _nodeListeners=
                        new java.util.Vector<NodeListener>();
    
    protected abstract EPNContext getContext();
    
    public void register(Network network) throws Exception {
        _networkMap.put(network.getName(), network);
        
        network.init(getContext());
    }

    public void unregister(String networkName) throws Exception {
        _networkMap.remove(networkName);
    }
    
    /**
     * {@inheritDoc}
     */
    public void addNodeListener(NodeListener l) {
        _nodeListeners.add(l);
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeNodeListener(NodeListener l) {
        _nodeListeners.remove(l);
    }
    
    protected Network getNetwork(String name) {
        return (_networkMap.get(name));
    }

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
        EventList ret=node.process(getContext(), source, events, retriesLeft);
        
        if (node.getNotificationEnabled() &&
                (ret == null || ret.size() < events.size())){ 
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
                notify(networkName, nodeName, notify);
            }
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
    protected void notify(String networkName, String nodeName, EventList processed) {
        for (NodeListener nl : _nodeListeners) {
            nl.eventsProcessed(networkName, nodeName, processed);
        }
    }
    
    public void close() throws Exception {
    }

}
