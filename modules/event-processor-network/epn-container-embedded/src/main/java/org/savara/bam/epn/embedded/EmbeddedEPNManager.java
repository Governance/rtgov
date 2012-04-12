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
package org.savara.bam.epn.embedded;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.savara.bam.epn.AbstractEPNManager;
import org.savara.bam.epn.Channel;
import org.savara.bam.epn.Destination;
import org.savara.bam.epn.EPNContext;
import org.savara.bam.epn.Network;
import org.savara.bam.epn.Node;
import org.savara.bam.epn.internal.EventList;

/**
 * This class provides the embedded implementation of
 * the EPN Manager.
 *
 */
public class EmbeddedEPNManager extends AbstractEPNManager {
    
    private static final Logger LOG=Logger.getLogger(EmbeddedEPNManager.class.getName());
    
    private static final int MAX_THREADS = 10;

    private ExecutorService _executor=Executors.newFixedThreadPool(MAX_THREADS);
    private EPNContext _context=new EmbeddedEPNContext();
    
    private java.util.Map<String,Channel> _entryPoints=new java.util.HashMap<String,Channel>();
    
    /**
     * {@inheritDoc}
     */
    protected EPNContext getContext() {
        return (_context);
    }
    
    /**
     * {@inheritDoc}
     */
    public void register(Network network) throws Exception {
        super.register(network);
        
        Node rootNode=network.getNodes().get(network.getRootNodeName());
        
        _entryPoints.put(network.getName(), new EmbeddedChannel(network.getName(),
                network.getRootNodeName(), rootNode, null));
    }

    /**
     * {@inheritDoc}
     */
    public void unregister(String networkName) throws Exception {
        super.unregister(networkName);
        
        _entryPoints.remove(networkName);
    }

    /**
     * {@inheritDoc}
     */
    public void enqueue(String network, java.util.List<java.io.Serializable> events) throws Exception {
        Channel channel=_entryPoints.get(network);
        
        if (channel == null) {
            throw new Exception("No channel for network '"+network+"'");
        }
        
        channel.send(new EventList(events));
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws Exception {
        // TODO: Should be configurable??
        _executor.awaitTermination(5, TimeUnit.SECONDS);
    }
    
    /**
     * The embedded implementation of the EPNContext.
     *
     */
    protected class EmbeddedEPNContext implements EPNContext {

        /**
         * {@inheritDoc}
         */
        public Channel getChannel(String source, Destination dest)
                throws Exception {
            return (new EmbeddedChannel(dest.getNetwork(), dest.getNode(),
                        getNode(dest.getNetwork(),dest.getNode()), source));
        }
        
    }
    
    /**
     * This is the embedded implementation of the Context interface.
     *
     */
    protected class EmbeddedChannel implements Channel {
        
        private String _networkName=null;
        private String _nodeName=null;
        private Node _node=null;
        private String _source=null;
        
        /**
         * The constructor.
         * 
         * @param networkName The network name
         * @param nodeName The node name
         * @param node The node
         * @param source The source node name
         */
        public EmbeddedChannel(String networkName, String nodeName, Node node, String source) {
            _networkName = networkName;
            _nodeName = nodeName;
            _node = node;
            _source = source;
        }

        /**
         * {@inheritDoc}
         */
        public void send(EventList events) throws Exception {
            send(events, _node.getMaxRetries());
        }

        /**
         * {@inheritDoc}
         */
        public void send(EventList events, int retriesLeft) throws Exception {
            _executor.execute(new EPNTask(_networkName, _nodeName,
                        _node, _source, events, retriesLeft, this));
        }

        /**
         * {@inheritDoc}
         */
        public void close() throws Exception {
        }
        
    }

    /**
     * This class implements the task for dispatching the event list
     * to the node.
     *
     */
    protected class EPNTask implements Runnable {
        
        private String _networkName=null;
        private String _nodeName=null;
        private Node _node=null;
        private String _source=null;
        private EventList _events=null;
        private int _retriesLeft=0;
        private Channel _channel=null;
        
        /**
         * This is the constructor for the task.
         * 
         * @param networkName The network name
         * @param nodeName The node name
         * @param node The node
         * @param source The source node name
         * @param events The list of events
         * @param retriesLeft The number of retries left
         * @param channel The channel
         */
        public EPNTask(String networkName, String nodeName, Node node,
                String source, EventList events, int retriesLeft, Channel channel) {
            _networkName = networkName;
            _nodeName = nodeName;
            _node = node;
            _source = source;
            _events = events;
            _retriesLeft = retriesLeft;
            _channel = channel;
        }

        /**
         * {@inheritDoc}
         */
        public void run() {
            EventList retries=null;
     
            try {
                retries = process(_networkName, _nodeName, _node,
                                _source, _events, _retriesLeft);            
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Failed to handle events", e);
                
                retries = _events;
            }

            if (retries != null) {
                if (_retriesLeft > 0) {
                    try {
                        _channel.send(retries, _retriesLeft-1);
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, "Failed to retry events", e);
                    }
                } else {
                    // TODO: Should this be reported via the manager?
                    LOG.severe("No more retries left");
                }
            }
        }
        
    }
}
