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
import org.savara.bam.epn.util.NetworkUtil;

/**
 * This class represents the abstract Event Process Network Manager
 * used as the base for any concrete implementation.
 *
 */
public abstract class AbstractEPNManager implements EPNManager {
    
    private static final Logger LOG=Logger.getLogger(AbstractEPNManager.class.getName());

    private java.util.Map<String, NetworkList> _networkMap=new java.util.HashMap<String, NetworkList>();
    private java.util.Map<String, java.util.List<Network>> _subjectMap=
                        new java.util.HashMap<String, java.util.List<Network>>();
    private java.util.List<NodeListener> _nodeListeners=
                        new java.util.Vector<NodeListener>();
    
    /**
     * This method returns the Event Processor Network Container.
     * 
     * @return The container
     */
    protected abstract EPNContainer getContainer();
    
    /**
     * {@inheritDoc}
     */
    public void register(Network network) throws Exception {
        
        LOG.info("Registering EPN network '"+network.getName()+"' timestamp["+network.getTimestamp()+"]");
        
        synchronized (_networkMap) {
            NetworkList nl=_networkMap.get(network.getName());
            
            if (nl == null) {
                nl = new NetworkList();
                _networkMap.put(network.getName(), nl);
            }
            
            Network oldnet=nl.getCurrent();
            
            // Add registered network to the list
            nl.add(network);
            
            // Check if current instance has changed to the
            // newly registered network
            if (nl.getCurrent() == network) {
                currentNetworkChanged(oldnet, network);
            }
        }
        
        network.init(getContainer());
    }
    
    /**
     * {@inheritDoc}
     */
    public void unregister(String networkName, long timestamp) throws Exception {
        
        LOG.info("Unregistering EPN network '"+networkName+"' timestamp["+timestamp+"]");

        synchronized (_networkMap) {
            NetworkList nl=_networkMap.get(networkName);
            
            if (nl != null) {
                Network network=(timestamp == 0 ? nl.getCurrent() : nl.getVersion(timestamp));
                
                if (network != null) {
                    Network oldcur=nl.getCurrent();
                    
                    nl.remove(network);
                    
                    Network newcur=nl.getCurrent();
                    
                    if (newcur != oldcur) {
                        currentNetworkChanged(oldcur, newcur);
                    }
                }
                
                if (nl.size() == 0) {
                    _networkMap.remove(networkName);
                }
            }
        }
    }
    
    /**
     * This method is called to handle a change in the current version
     * of a network, due to a new (more recent) version being registered
     * or a current version being unregistered.
     * 
     * @param oldNet The old network version
     * @param newNet The new network version
     */
    protected void currentNetworkChanged(Network oldNet, Network newNet) {
        if (oldNet != null) {
            unregisterSubjects(oldNet);
        }
        
        if (newNet != null) {
            registerSubjects(newNet);
        }
    }
    
    /**
     * This method registers the supplied network against the
     * subjects it is interested in.
     * 
     * @param network The network
     */
    protected void registerSubjects(Network network) {
        for (String subject : network.getSubjects()) {
            java.util.List<Network> networks=_subjectMap.get(subject);
            
            if (networks == null) {
                networks = new java.util.ArrayList<Network>();
                _subjectMap.put(subject, networks);
            }
            
            networks.add(network);
        }
    }
    
    /**
     * This method unregisters the supplied network from the
     * subjects it is interested in.
     * 
     * @param network The network
     */
    protected void unregisterSubjects(Network network) {
        for (String subject : network.getSubjects()) {
            java.util.List<Network> networks=_subjectMap.get(subject);
            
            if (networks != null) {
                networks.remove(network);
                if (networks.size() == 0) {
                    _subjectMap.remove(subject);
                }
            }
        }
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
     * @param timestamp The timestamp, or 0 for current version
     * @return The network, or null if not found
     */
    protected Network getNetwork(String name, long timestamp) {
        Network ret=null;
        NetworkList nl=_networkMap.get(name);
        
        if (nl != null) {
            ret = (timestamp == 0 ? nl.getCurrent() : nl.getVersion(timestamp));
        }
        
        return (ret);
    }
    
    /**
     * This method returns the list of networks that subscribe to
     * the supplied subject.
     * 
     * @param subject The subject
     * @return The list of networks, or null of no networks subscribe to the subject
     */
    protected java.util.List<Network> getNetworksForSubject(String subject) {
        return (_subjectMap.get(subject));
    }

    /**
     * This method returns the node associated with the
     * supplied network and node name.
     * 
     * @param networkName The network name
     * @param timestamp The timestamp, or 0 for current
     * @param nodeName The node name, or null if wanting the root node
     * @return The node, or null if not found
     * @throws Exception Failed to find the specified node
     */
    protected Node getNode(String networkName, long timestamp, String nodeName) throws Exception {
        Network net=getNetwork(networkName, timestamp);
        
        if (net == null) {
            throw new Exception("No network '"+networkName+"' timestamp["+timestamp+"] was found");
        }
        
        // Check if node name has been specified, if not use root node name
        if (nodeName == null) {
            nodeName = net.getRootNodeName();
        }
        
        Node node=net.getNodes().get(nodeName);
        
        if (node == null) {
            throw new Exception("No node '"+nodeName+"' was found in network '"+networkName+
                            "' timestamp["+timestamp+"");
        }
        
        return (node);
    }

    /**
     * This method dispatches a set of events directly to the supplied
     * node.
     * 
     * @param networkName The network name
     * @param timestamp The timestamp
     * @param nodeName The node name
     * @param node The node
     * @param source The source node, or null if sending to root
     * @param events The list of events to be processed
     * @param retriesLeft The number of retries left
     * @return The events to retry, or null if no retries necessary
     * @throws Exception Failed to dispatch the events for processing
     */
    protected EventList process(String networkName, long timestamp, String nodeName,
                    Node node, String source, EventList events,
                            int retriesLeft) throws Exception {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Process events on network="+networkName+" node="+nodeName
                    +" source="+source+" retriesLeft="+retriesLeft+" events="+events);
        }

        EventList ret=node.process(getContainer(), source, events, retriesLeft);
        
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
                notifyEventsProcessed(networkName, timestamp, nodeName, notify);
            }
        }
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Processed events on network="+networkName+
                    " timestamp="+timestamp+" node="+nodeName
                    +" source="+source+" ret="+ret);
        }

        return (ret);
    }

    /**
     * This method sends a notification that the supplied list of events have
     * been processed by the named network and node.
     * 
     * @param networkName The network name
     * @param timestamp The timestamp
     * @param nodeName The node name
     * @param processed The list of processed events
     * @throws Exception Failed to notify events processed
     */
    protected void notifyEventsProcessed(String networkName, long timestamp,
                    String nodeName, EventList processed) throws Exception {
        dispatchEventsProcessedToListeners(networkName, timestamp, nodeName, processed);
    }
    
    /**
     * This method dispatches the notifications to the registered node listeners.
     * 
     * @param networkName The network name
     * @param timestamp The timestamp
     * @param nodeName The node name
     * @param processed The list of processed events
     */
    protected void dispatchEventsProcessedToListeners(String networkName, long timestamp,
                    String nodeName, EventList processed) {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Notify processed events on network="+networkName+" node="+nodeName
                    +" processed="+processed);
        }

        for (NodeListener nl : _nodeListeners) {
            nl.eventsProcessed(networkName, timestamp, nodeName, processed);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void close() throws Exception {
    }

    /**
     * This class represents a list of Network instances
     * for the same name, but different versions (timestamps).
     *
     */
    public class NetworkList {
        
        private java.util.List<Network> _networks=new java.util.LinkedList<Network>();
        
        /**
         * The constructor.
         */
        public NetworkList() {
        }
        
        /**
         * This method adds a new instance of the Network to the list.
         * 
         * @param network The network
         */
        public void add(Network network) {
            synchronized (_networks) {
                boolean f_inserted=false;
                for (int i=0; i < _networks.size(); i++) {
                    //if (NetworkUtil.isNewerVersion(_networks.get(i).getTimestamp(),
                    //                network.getTimestamp()) {
                    if (_networks.get(i).getTimestamp() < network.getTimestamp()) {
                        _networks.add(i, network);
                        f_inserted = true;
                        break;
                    }
                }
                if (!f_inserted) {
                    _networks.add(network);
                }
            }
        }
        
        /**
         * This method removes an instance of the Network from the list.
         * 
         * @param network The network
         */
        public void remove(Network network) {
            synchronized (_networks) {
                _networks.remove(network);
            }
        }
        
        /**
         * This method returns the most recent instance of the network.
         * 
         * @return The current instance of the network
         */
        public Network getCurrent() {
            Network ret=null;
            
            synchronized (_networks) {
                if (_networks.size() > 0) {
                    ret = _networks.get(0);
                }
            }

            return (ret);
        }
        
        /**
         * This method returns the network instance associated with
         * the supplied timestamp.
         * 
         * @param timestamp The timestamp
         * @return The network instance, or null if not found
         */
        public Network getVersion(long timestamp) {
            Network ret=null;
            
            synchronized (_networks) {
                for (Network network : _networks) {
                    if (network.getTimestamp() == timestamp) {
                        ret = network;
                        break;
                    }
                }
            }

            return (ret);
        }
        
        /**
         * This method returns the number of networks in the list.
         * 
         * @return The number of networks
         */
        public int size() {
            return (_networks.size());
        }
    }
}
