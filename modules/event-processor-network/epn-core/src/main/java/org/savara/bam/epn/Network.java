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
package org.savara.bam.epn;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class represents an Event Processor Network.
 *
 */
public class Network {

    private String _name=null;
    private String _version=null;
    private java.util.List<String> _subjects=new java.util.ArrayList<String>();
    private String _rootNodeName=null;
    private java.util.Map<String,Node> _nodes=new java.util.HashMap<String,Node>();
    
    private Node _root=null;
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
     * This method returns the list of subjects that the network will subscribe
     * to for events.
     * 
     * @return The list of subjects
     */
    public java.util.List<String> getSubjects() {
        return (_subjects);
    }
    
    /**
     * This method sets the list of subjects that the network will subscribe
     * to for events.
     * 
     * @param subjects The list of subjects
     */
    public void setSubjects(java.util.List<String> subjects) {
        _subjects = subjects;
    }
    
    /**
     * This method returns the root node name.
     * 
     * @return The root node name
     */
    public String getRootNodeName() {
        return (_rootNodeName);
    }
    
    /**
     * This method sets the root node name.
     * 
     * @param rootNodeName The root node name
     */
    public void setRootNodeName(String rootNodeName) {
        _rootNodeName = rootNodeName;
    }
    
    /**
     * This method returns the event processor nodes.
     * 
     * @return The event processor nodes
     */
    public java.util.Map<String,Node> getNodes() {
        return (_nodes);
    }
    
    /**
     * This method sets the event processor nodes.
     * 
     * @param nodes The event processor nodes
     */
    public void setNodes(java.util.Map<String,Node> nodes) {
        _nodes = nodes;
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
            
            for (String name : _nodes.keySet()) {
                Node node=_nodes.get(name);
                
                // Initialize the node
                node.init();
            }
            
            // Cache context classloader for us deserializing
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
     * This method initializes the network.
     * 
     * @param container The container
     * @throws Exception Failed to initialize the network
     */
    protected void init(EPNContainer container) throws Exception {
        for (String name : _nodes.keySet()) {
            Node node=_nodes.get(name);
            
            // Initialize channels
            if (node.getSourceNodes() != null) {
                for (String nodeName : node.getSourceNodes()) {
                    Node sourceNode=_nodes.get(nodeName);
                    
                    if (sourceNode == null) {
                        LOG.severe("Network '"+getName()+"' version '"+getVersion()
                                +"' node '"+name+"' has unknown source node '"+nodeName+"'");
                    } else {
                        sourceNode.getChannels().add(container.getChannel(this, nodeName, name));
                    }
                }
            }
            
            if (node.getNotificationEnabled() && container != null) {
                node.getChannels().add(container.getChannel(this, name));
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
            
            if (name.equals(getRootNodeName())) {
                _root = node;                
            }
        }
        
        // If classloader not set, then use current one
        if (_contextClassLoader == null) {
            _contextClassLoader = Thread.currentThread().getContextClassLoader();
        }
        
        if (_root == null) {
            throw new Exception("Network does not contain a root node of name '"+getRootNodeName()+"'");
        }
    }
    
    /**
     * This method processes the supplied list of events against the root
     * event processor node associated with the network.
     * 
     * @param container The container
     * @param events The list of events to be processed
     * @throws Exception Failed to process events, and should result in transaction rollback
     */
    protected void process(EPNContainer container, EventList events) throws Exception {
 
        if (_root != null) {
            _root.process(container, null, events, _root.getMaxRetries());
        }
    }

    /**
     * This method closes the network.
     * 
     * @param container The container
     * @throws Exception Failed to close the network
     */
    protected void close(EPNContainer container) throws Exception {
        for (String name : _nodes.keySet()) {
            Node node=_nodes.get(name);
            node.close(container, name);
        }
    }
    
}
