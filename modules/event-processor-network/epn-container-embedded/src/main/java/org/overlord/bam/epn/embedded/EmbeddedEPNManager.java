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
package org.overlord.bam.epn.embedded;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.overlord.bam.epn.AbstractEPNManager;
import org.overlord.bam.epn.Channel;
import org.overlord.bam.epn.EPNContainer;
import org.overlord.bam.epn.EventList;
import org.overlord.bam.epn.Network;
import org.overlord.bam.epn.Node;
import org.overlord.bam.epn.NotifyType;

/**
 * This class provides the embedded implementation of
 * the EPN Manager.
 *
 */
public class EmbeddedEPNManager extends AbstractEPNManager {
    
    private static final Logger LOG=Logger.getLogger(EmbeddedEPNManager.class.getName());
    
    private static final int MAX_THREADS = 10;

    private ExecutorService _executor=Executors.newFixedThreadPool(MAX_THREADS);
    private EPNContainer _container=new EmbeddedEPNContainer();
    
    private java.util.Map<String,java.util.List<EmbeddedChannel>> _entryPoints=
                        new java.util.HashMap<String,java.util.List<EmbeddedChannel>>();
    
    /**
     * {@inheritDoc}
     */
    protected EPNContainer getContainer() {
        return (_container);
    }
    
    /**
     * This method provides access to the registered channels against subjects.
     * 
     * @return The entry points
     */
    protected java.util.Map<String,java.util.List<EmbeddedChannel>> getEntryPoints() {
        return (_entryPoints);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void currentNetworkChanged(Network oldNet, Network newNet) {
        super.currentNetworkChanged(oldNet, newNet);
        
        if (oldNet != null) {
            unregisterEntryPoints(oldNet);
        }
        
        if (newNet != null) {
            registerEntryPoints(newNet);
        }
    }
    
    /**
     * This method unregisters the entry points associated with the supplied
     * network.
     * 
     * @param network The network
     */
    protected void unregisterEntryPoints(Network network) {
        synchronized (_entryPoints) {
            for (String subject : network.getSubjects()) {
                java.util.List<EmbeddedChannel> channels=_entryPoints.get(subject);
                
                if (channels != null) {
                    for (EmbeddedChannel ch : channels) {
                        if (ch.getNetworkName().equals(network.getName())) {
                            channels.remove(ch);
                            if (channels.size() == 0) {
                                _entryPoints.remove(subject);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * This method registers the network's entry point against its
     * list of subscription subjects.
     * 
     * @param network The network
     */
    protected void registerEntryPoints(Network network) {
        synchronized (_entryPoints) {
            Node rootNode=network.getNodes().get(network.getRootNodeName());
            
            for (String subject : network.getSubjects()) {
                java.util.List<EmbeddedChannel> channels=_entryPoints.get(subject);
                
                if (channels == null) {
                    channels = new java.util.ArrayList<EmbeddedChannel>();
                    _entryPoints.put(subject, channels);
                }
                
                channels.add(new EmbeddedChannel(network,
                        network.getRootNodeName(), rootNode, null));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void publish(String subject, java.util.List<? extends java.io.Serializable> events) throws Exception {
        publish(subject, new EventList(events));
    }
    
    /**
     * This method publishes the event list to the specific subject.
     * 
     * @param subject The subject
     * @param events The events
     * @throws Exception Failed to publish events
     */
    protected void publish(String subject, EventList events) throws Exception {
        synchronized (_entryPoints) {
            java.util.List<EmbeddedChannel> channels=_entryPoints.get(subject);
            
            if (channels != null) {
                for (EmbeddedChannel channel : channels) {
                    channel.send(events);                
                }
            }   
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws Exception {
        // TODO: Should be configurable??
        _executor.awaitTermination(5, TimeUnit.SECONDS);
    }
    
    /**
     * The embedded implementation of the EPNContainer.
     *
     */
    protected class EmbeddedEPNContainer implements EPNContainer {

        /**
         * {@inheritDoc}
         */
        public Channel getChannel(Network network, String source, String dest)
                throws Exception {
            return (new EmbeddedChannel(network, dest,
                    getNode(network.getName(), network.getVersion(), dest), source));
        }

        /**
         * {@inheritDoc}
         */
        public Channel getChannel(Network network, String source)
                throws Exception {
            return (new EmbeddedChannel(network, source));
        }

        /**
         * {@inheritDoc}
         */
        public Channel getChannel(String subject) throws Exception {
            return (new EmbeddedChannel(subject));
        }

        /**
         * {@inheritDoc}
         */
        public void send(EventList events, List<Channel> channels)
                throws Exception {
            for (Channel channel : channels) {
                if (channel instanceof EmbeddedChannel) {
                    ((EmbeddedChannel)channel).send(events);
                } else {
                    LOG.severe("Unknown channel type '"+channel+"'");
                }
            }
        }
        
    }
    
    /**
     * This is the embedded implementation of the Channel interface.
     *
     */
    protected class EmbeddedChannel implements Channel {
        
        private Network _network=null;
        private String _nodeName=null;
        private Node _node=null;
        private String _source=null;
        private String _subject=null;
        
        /**
         * The constructor.
         * 
         * @param network The network
         * @param nodeName The node name
         * @param node The node
         * @param sourceNode The source node name
         */
        public EmbeddedChannel(Network network,
                        String nodeName, Node node, String sourceNode) {
            _network = network;
            _nodeName = nodeName;
            _node = node;
            _source = sourceNode;
        }
        
        /**
         * The constructor for the notification channel.
         * 
         * @param network The network
         * @param sourceNode The source node name
         */
        public EmbeddedChannel(Network network, String sourceNode) {
            _network = network;
            _source = sourceNode;
        }
        
        /**
         * The constructor.
         * 
         * @param subject The subject
         */
        public EmbeddedChannel(String subject) {
            _subject = subject;
        }
        
        /**
         * This method determines whether this channel is used for
         * notifications.
         * 
         * @return Whether this is a notification channel
         */
        public boolean isNotificationChannel() {
            return (_subject == null && _node == null);
        }
        
        /**
         * This method returns the network name.
         * 
         * @return The network name
         */
        protected String getNetworkName() {
            return (_network.getName());
        }

        /**
         * This method returns the network version.
         * 
         * @return The network version
         */
        protected String getVersion() {
            return (_network.getVersion());
        }

        /**
         * This method returns the node name.
         * 
         * @return The node name
         */
        protected String getNodeName() {
            return (_nodeName);
        }

        /**
         * {@inheritDoc}
         */
        public void send(EventList events) throws Exception {
            if (isNotificationChannel()) {
                notifyListeners(_network.getName(), _network.getVersion(),
                                _source, NotifyType.Results, events);
            } else {
                send(events, _node.getMaxRetries());
            }
        }

        /**
         * {@inheritDoc}
         */
        public void send(EventList events, int retriesLeft) throws Exception {
            if (_subject != null) {
                publish(_subject, events);
            } else {
                if (retriesLeft == -1) {
                    retriesLeft = _node.getMaxRetries();
                }
                _executor.execute(new EPNTask(_network, _nodeName,
                        _node, _source, events, retriesLeft, this));
            }
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
        private String _version=null;
        private String _nodeName=null;
        private Node _node=null;
        private String _source=null;
        private EventList _events=null;
        private int _retriesLeft=0;
        private EmbeddedChannel _channel=null;
        
        /**
         * This is the constructor for the task.
         * 
         * @param network The network
         * @param nodeName The node name
         * @param node The node
         * @param source The source node name
         * @param events The list of events
         * @param retriesLeft The number of retries left
         * @param channel The channel
         */
        public EPNTask(Network network, String nodeName, Node node,
                String source, EventList events, int retriesLeft, EmbeddedChannel channel) {
            _networkName = network.getName();
            _version = network.getVersion();
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
                retries = process(_networkName, _version, _nodeName, _node,
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
