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
package org.overlord.rtgov.epn;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class represents an Event Processor Network.
 *
 */
public class Network implements NetworkMBean {

    private String _name=null;
    private String _version=null;
    private java.util.List<Subscription> _subscriptions=new java.util.ArrayList<Subscription>();
    private java.util.List<Node> _nodes=new java.util.ArrayList<Node>();
    private java.util.Map<String,Node> _namedNodes=new java.util.HashMap<String,Node>();
    private java.util.Map<String,java.util.List<Node>> _subjectNodes=
                        new java.util.HashMap<String,java.util.List<Node>>();
    private long _lastAccessed=0;
    
    private boolean _preinitialized=false;
    private ClassLoader _contextClassLoader=null;
    
    private static final Logger LOG=Logger.getLogger(Network.class.getName());
    
    /**
     * The default constructor.
     */
    public Network() {
    }
    
    /**
     * This method returns the name of the network. This can be used
     * to locate the network by name.
     * 
     * @return The name of the network
     */
    public String getName() {
        return (_name);
    }
    
    /**
     * This method sets the name of the network.
     * 
     * @param name The name of the network
     */
    public void setName(String name) {
        _name = name;
    }
    
    /**
     * This method returns the version associated with the network.
     * 
     * @return The version
     */
    public String getVersion() {
        return (_version);
    }
    
    /**
     * This method sets the version associated with the network.
     * 
     * @param version The version
     */
    public void setVersion(String version) {
        _version = version;
    }
    
    /**
     * This method returns the timestamp the network was
     * last accessed.
     * 
     * @return When the network was last accessed
     */
    protected long lastAccessed() {
        return (_lastAccessed);
    }
    
    /**
     * This method returns the date/time the network was
     * last accessed.
     * 
     * @return When the network was last accessed
     */
    public java.util.Date getLastAccessed() {
        return (new java.util.Date(_lastAccessed));
    }
    
    /**
     * This method sets the timestamp the network was
     * last accessed.
     * 
     * @param timestamp When the network was last accessed
     */
    protected void lastAccessed(long timestamp) {
        _lastAccessed = timestamp;
    }
    
    /**
     * This method returns the list of subscriptions that the network will subscribe
     * to for events.
     * 
     * @return The list of subscriptions
     */
    public java.util.List<Subscription> getSubscriptions() {
        return (_subscriptions);
    }
    
    /**
     * This method sets the list of subscriptions that the network will subscribe
     * to for events.
     * 
     * @param subscriptions The list of subscriptions
     */
    public void setSubscriptions(java.util.List<Subscription> subscriptions) {
        _subscriptions = subscriptions;
    }
    
    /**
     * This method returns the list of nodes associated with the specified
     * subject.
     * 
     * @param subject The subject 
     * @return The list of nodes, or null if none associated with the subject
     */
    public java.util.List<Node> getNodesForSubject(String subject) {
        return (_subjectNodes.get(subject));
    }
    
    /**
     * This method returns the set of subjects that this network
     * subscribes to.
     * 
     * @return The set of subscription subjects
     */
    public java.util.Set<String> subjects() {
        return (_subjectNodes.keySet());
    }
    
    /**
     * This method returns the event processor nodes.
     * 
     * @return The event processor nodes
     */
    public java.util.List<Node> getNodes() {
        return (_nodes);
    }
    
    /**
     * This method sets the event processor nodes.
     * 
     * @param nodes The event processor nodes
     */
    public void setNodes(java.util.List<Node> nodes) {
        _nodes = nodes;
    }
    
    /**
     * This method returns the node associated with the
     * supplied name.
     * 
     * @param name The name
     * @return The node, or null if not found
     */
    public Node getNode(String name) {
        return (_namedNodes.get(name));
    }
    
    /**
     * This method is called on occasions where the network's
     * event processors need to be initialized in a different
     * classloader context to the container related configuration
     * (i.e. channels between nodes).
     * 
     * @throws Exception Failed to pre-initialize the network
     */
    protected void preInit() throws Exception {
        
        if (!_preinitialized) {
            _preinitialized = true;
            
            for (Node node : _nodes) {
                
                // Initialize the node
                node.init();
            }
            
            // Cache context classloader for use deserializing
            // events in this context
            _contextClassLoader = Thread.currentThread().getContextClassLoader();
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Pre-initialized '"+_name+"/"+_version+"': classloader="+_contextClassLoader);
            }
        }
    }
    
    /**
     * This method returns the context class loader
     * in which the network was pre-initialized.
     * 
     * @return The context classloader, or null if not relevant
     */
    protected ClassLoader contextClassLoader() {
        return (_contextClassLoader);
    }

    /**
     * This method initializes the name to node map.
     * 
     */
    protected void initNameMap() {
        // Initialize mapping from name to node
        for (Node node : _nodes) {
            _namedNodes.put(node.getName(), node);
        }
    }
    
    /**
     * This method initializes the network.
     * 
     * @param container The container
     * @throws Exception Failed to initialize the network
     */
    protected void init(EPNContainer container) throws Exception {
        
    	initNameMap();
    	
        for (Node node : _nodes) {
           // Initialize channels
            if (node.getSourceNodes() != null) {
                for (String nodeName : node.getSourceNodes()) {
                    Node sourceNode=getNode(nodeName);
                    
                    if (sourceNode == null) {
                        LOG.severe(MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                                "epn-core.Messages").getString("EPN-CORE-2"),
                                getName(), getVersion(), node.getName(), nodeName));
                    } else {
                        sourceNode.getChannels().add(container.getChannel(this, nodeName, node.getName()));
                    }
                }
            }
            
            // If results should be notified, then add a channel to be sent the results
            if (container != null) {
                for (Notification no : node.getNotifications()) {
                    if (no.getType().equals(NotificationType.Results)) {
                        node.getChannels().add(container.getNotificationChannel(this,
                                no.getSubject()));
                    }
                }
            }
            
            if (node.getDestinationSubjects() != null) {
                for (String subject : node.getDestinationSubjects()) {
                    node.getChannels().add(container.getChannel(subject));
                }
            }
            
            if (!_preinitialized) {
                // Initialize the node
                node.init();
            }
        }
        
        // Initialize subject/node mapping
        for (Subscription sub : _subscriptions) {
            Node node=getNode(sub.getNodeName());
            
            if (node == null) {
                throw new Exception("Network '"+getName()+"' version '"+getVersion()
                        +"' subscription has unknown node name '"+sub.getNodeName()+"'");
            } else {
                java.util.List<Node> nodes=_subjectNodes.get(sub.getSubject());
                
                if (nodes == null) {
                    nodes = new java.util.ArrayList<Node>();
                    _subjectNodes.put(sub.getSubject(), nodes);
                }
                
                nodes.add(node);
            }
        }
        
        // If classloader not set, then use current one
        if (_contextClassLoader == null) {
            _contextClassLoader = Thread.currentThread().getContextClassLoader();
        }
    }
    
    /**
     * This method closes the network.
     * 
     * @param container The container
     * @throws Exception Failed to close the network
     */
    protected void close(EPNContainer container) throws Exception {
        for (Node node : _nodes) {
            node.close(container);
        }
    }
    
}
