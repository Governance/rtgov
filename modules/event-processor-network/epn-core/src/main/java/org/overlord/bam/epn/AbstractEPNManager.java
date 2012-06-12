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
package org.overlord.bam.epn;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.overlord.bam.epn.util.NetworkUtil;

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
    private java.util.Map<String, java.util.List<NodeListener>> _nodeListeners=
                        new java.util.HashMap<String, java.util.List<NodeListener>>();
    private boolean _usePrePostEventListProcessing=false;
    
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
        
        LOG.info("Registering EPN network '"+network.getName()+"' version["+network.getVersion()+"]");
        
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
    public void unregister(String networkName, String version) throws Exception {
        
        LOG.info("Unregistering EPN network '"+networkName+"' version["+version+"]");

        synchronized (_networkMap) {
            NetworkList nl=_networkMap.get(networkName);
            
            if (nl != null) {
                Network network=(version == null ? nl.getCurrent() : nl.getVersion(version));
                
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
    public void addNodeListener(String network, NodeListener l) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Register node listener="+l+" on network="+network);
        }
        
        synchronized (_nodeListeners) {
            java.util.List<NodeListener> listeners=_nodeListeners.get(network);
            
            if (listeners == null) {
                listeners = new java.util.ArrayList<NodeListener>();
                _nodeListeners.put(network, listeners);
            }
            
            listeners.add(l);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeNodeListener(String network, NodeListener l) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Unregister node listener="+l+" on network="+network);
        }
        
        synchronized (_nodeListeners) {
            java.util.List<NodeListener> listeners=_nodeListeners.get(network);
            
            if (listeners != null) {                
                listeners.remove(l);
                
                if (listeners.size() == 0) {
                    _nodeListeners.remove(network);
                }
            }
        }
    }
    
    /**
     * This method returns the network associated with the
     * supplied name.
     * 
     * @param name The network name
     * @param version The version, or null for current version
     * @return The network, or null if not found
     */
    protected Network getNetwork(String name, String version) {
        Network ret=null;
        NetworkList nl=_networkMap.get(name);
        
        if (nl != null) {
            ret = (version == null ? nl.getCurrent() : nl.getVersion(version));
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
     * @param version The version, or null for current
     * @param nodeName The node name, or null if wanting the root node
     * @return The node, or null if not found
     * @throws Exception Failed to find the specified node
     */
    protected Node getNode(String networkName, String version, String nodeName) throws Exception {
        Network net=getNetwork(networkName, version);
        
        if (net == null) {
            throw new Exception("No network '"+networkName+"' version["+version+"] was found");
        }
        
        // Check if node name has been specified, if not use root node name
        if (nodeName == null) {
            nodeName = net.getRootNodeName();
        }
        
        Node node=net.getNodes().get(nodeName);
        
        if (node == null) {
            throw new Exception("No node '"+nodeName+"' was found in network '"+networkName
                    +"' version["+version+"");
        }
        
        return (node);
    }

    /**
     * This method sets whether to use pre/post event list processing.
     * 
     * @param b Whether to use pre/post event list processing
     */
    protected void setUsePrePostEventListProcessing(boolean b) {
        _usePrePostEventListProcessing = b;
    }
    
    /**
     * This method deserializes the events in the context of the supplied
     * network. This method is only relevant for EPN manager implementations
     * that load EPN networks in their own classloader context.
     * 
     * @param events The events
     * @param network The network
     */
    protected void preProcessEvents(EventList events, Network network) {
        preProcessEvents(events, network.contextClassLoader());
    }
    
    /**
     * This method deserializes the events in the context of the supplied
     * classloader. This method is only relevant for EPN manager implementations
     * that load EPN networks in their own classloader context.
     * 
     * @param events The events
     * @param classloader The classloader
     */
    protected void preProcessEvents(EventList events, ClassLoader classloader) {
        if (classloader != null) {
            events.resolve(classloader);
        }
    }
    
    /**
     * This method resets the events, to enable them to be used in the
     * context of another classloader. This method is only relevant for EPN
     * manager implementations that load EPN networks in their own 
     * classloader context.
     * 
     * @param events The events
     */
    protected void postProcessEvents(EventList events) {
        events.reset();
    }
    
    /**
     * This method dispatches a set of events directly to the supplied
     * node.
     * 
     * @param networkName The network name
     * @param version The version
     * @param nodeName The node name
     * @param node The node
     * @param source The source node, or null if sending to root
     * @param events The list of events to be processed
     * @param retriesLeft The number of retries left
     * @return The events to retry, or null if no retries necessary
     * @throws Exception Failed to dispatch the events for processing
     */
    protected EventList process(String networkName, String version, String nodeName,
                    Node node, String source, EventList events,
                            int retriesLeft) throws Exception {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Process events on network="+networkName+" node="+nodeName
                    +" source="+source+" retriesLeft="+retriesLeft+" events="+events);
        }

        EventList ret=node.process(getContainer(), source, events, retriesLeft);
        
        if (node.getNotificationEnabled()
                && (ret == null || ret.size() < events.size())) { 
            EventList notifyList=null;
            
            if (ret != null) {
                java.util.List<java.io.Serializable> processed = new java.util.ArrayList<java.io.Serializable>();
                
                for (java.io.Serializable event : events) {
                    if (!ret.contains(event)) {
                        processed.add(event);
                    }
                }
                
                if (processed.size() > 0) {
                    notifyList = new EventList(processed);
                }
            } else {
                notifyList = events;
            }
            
            if (notifyList != null) {
                notifyListeners(networkName, version, nodeName, NotifyType.Processed, notifyList);
            }
        }

        if (ret != null) {
            LOG.warning("Processed events on network="+networkName
                    +" version="+version+" node="+nodeName
                    +" source="+source+" ret="+ret);
            
        } else if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Processed events on network="+networkName
                    +" version="+version+" node="+nodeName
                    +" source="+source+": no retries");
        }

        return (ret);
    }

    /**
     * This method sends a notification to any registered listeners that
     * a situation has occurred on the named network/version/node.
     * 
     * @param networkName The network name
     * @param version The version
     * @param nodeName The node name
     * @param type The type of notification
     * @param events The list of events
     * @throws Exception Failed to notify
     */
    protected void notifyListeners(String networkName, String version,
                    String nodeName, NotifyType type, EventList events) throws Exception {
        dispatchNotificationToListeners(networkName, version, nodeName, type, events);
    }
    
    /**
     * This method dispatches the notifications to the registered node listeners.
     * 
     * @param networkName The network name
     * @param version The version
     * @param nodeName The node name
     * @param type The type of notification
     * @param events The list of events
     */
    protected void dispatchNotificationToListeners(String networkName, String version,
                    String nodeName, NotifyType type, EventList events) {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Notify processed events on network="+networkName+" node="+nodeName
                    +" type="+type+" events="+events);
        }
        
        java.util.List<NodeListener> listeners=_nodeListeners.get(networkName);

        if (listeners != null) {
            for (int i=0; i < listeners.size(); i++) {
                NodeListener nl=listeners.get(i);
                
                if (_usePrePostEventListProcessing) {
                	try {
                		preProcessEvents(events, nl.getClass().getClassLoader());
                	} catch (Throwable t) {
                		LOG.log(Level.SEVERE, "Unable to dispatch events to listener '"+nl.getClass().getName()+"'", t);
                		
                		// Don't attempt to send events to the node listener
                		continue;
                	}
                }
                
                nl.notify(networkName, version, nodeName, type, events);
                
                if (_usePrePostEventListProcessing) {
                    postProcessEvents(events);
                }
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void close() throws Exception {
    }

    /**
     * This class represents a list of Network instances
     * for the same name, but different versions.
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
                boolean inserted=false;
                for (int i=0; i < _networks.size(); i++) {
                    if (NetworkUtil.isNewerVersion(_networks.get(i).getVersion(),
                                    network.getVersion())) {
                        _networks.add(i, network);
                        inserted = true;
                        break;
                    }
                }
                if (!inserted) {
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
         * the supplied version.
         * 
         * @param version The version
         * @return The network instance, or null if not found
         */
        public Network getVersion(String version) {
            Network ret=null;
            
            synchronized (_networks) {
                for (Network network : _networks) {
                    if (network.getVersion().equals(version)) {
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
