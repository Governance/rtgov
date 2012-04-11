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
package org.savara.bam.epn.jms;

import static javax.ejb.ConcurrencyManagementType.BEAN;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.savara.bam.epn.AbstractEPNManager;
import org.savara.bam.epn.Channel;
import org.savara.bam.epn.EPNContext;
import org.savara.bam.epn.EPNManager;
import org.savara.bam.epn.Network;
import org.savara.bam.epn.Node;
import org.savara.bam.epn.NodeListener;
import org.savara.bam.epn.internal.EventList;

/**
 * This class provides the JMS implementation of
 * the EPN Manager.
 *
 */
@Singleton
@ConcurrencyManagement(BEAN)
@Startup
@Local(EPNManager.class)
public class JMSEPNManager extends AbstractEPNManager implements javax.jms.MessageListener {
    
    @Resource(mappedName = "java:/JmsXA")
    ConnectionFactory _connectionFactory;
    
    @Resource(mappedName = "java:/queue/EPNServer")
    Destination _epnServerDestination;
    
    @Resource(mappedName = "java:/topic/EPNNotifications")
    Destination _epnNotificationsDestination;
    
    public static final String EPN_NETWORK = "EPNNetwork";
    public static final String EPN_DESTINATION_NODE = "EPNDestinationNode";
    public static final String EPN_SOURCE_NODE = "EPNSourceNode";
    public static final String EPN_RETRIES_LEFT = "EPNRetriesLeft";
    
    private Connection _connection=null;
    private Session _session=null;
    private MessageProducer _producer=null;
    
    private java.util.Map<String, JMSChannel> _networkChannels=new java.util.HashMap<String, JMSChannel>();
    
    private EPNContext _context=new JMSEPNContext();
    
    private static final Logger LOG=Logger.getLogger(JMSEPNManager.class.getName());

    protected EPNContext getContext() {
        return(_context);
    }
    
    public void register(Network network) throws Exception {
        super.register(network);
        
        _networkChannels.put(network.getName(), new JMSChannel(_session,
                _producer, null, new org.savara.bam.epn.Destination(network.getName(),
                        network.getRootNodeName())));
    }

    public void unregister(String networkName) throws Exception {
        super.unregister(networkName);
        
        _networkChannels.remove(networkName);
    }
    
    public void enqueue(String network, java.util.List<java.io.Serializable> events) throws Exception {
        JMSChannel channel=_networkChannels.get(network);
        
        if (channel == null) {
            throw new Exception("Unable to find channel for network '"+network+"'");
        }
        
        channel.send(new EventList(events));
    }

    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("EPNManager("+this+"): Received event batch: "+message);
            }
            
            try {
                EventList events=(EventList)((ObjectMessage)message).getObject();
                
                String network=message.getStringProperty(JMSEPNManager.EPN_NETWORK);
                String node=message.getStringProperty(JMSEPNManager.EPN_DESTINATION_NODE);
                String source=message.getStringProperty(JMSEPNManager.EPN_SOURCE_NODE);
                int retriesLeft=message.getIntProperty(JMSEPNManager.EPN_RETRIES_LEFT);
                
                dispatch(network, node, source, events, retriesLeft);
                
            } catch(Exception e) {
                LOG.severe("Failed to handle events: "+e);
            }
        }
    }

    /**
     * This method dispatches a set of events directly to the named
     * network and node. If the node is not specified, then it will
     * be dispatched to the 'root' node of the network.
     * 
     * @param networkName The name of the network
     * @param nodeName The optional node name, or root node if not specified
     * @param source The source node, or null if sending to root
     * @param events The list of events to be processed
     * @param retriesLeft The number of retries left, or -1 if should be max value
     * @throws Exception Failed to dispatch the events for processing
     */
    protected void dispatch(String networkName, String nodeName, String source, EventList events,
                            int retriesLeft) throws Exception {
        Node node=getNode(networkName, nodeName);
        
        if (retriesLeft == -1) {
            retriesLeft = node.getMaxRetries();
        }
        
        EventList retries=process(networkName, nodeName, node, source, events, retriesLeft);
        
        if (retries != null) {
            retry(networkName, nodeName, source, events, retriesLeft-1);
        }
    }
    
    /**
     * This method handles retrying the supplied set of events, if the number of
     * retries left is greater than 0.
     * 
     * @param networkName The name of the network
     * @param nodeName The optional node name, or root node if not specified
     * @param source The source
     * @param events The events
     * @param retriesLeft The number of retries now remaining after this failure to process them
     * @throws Exception Failed to retry the events processing
     */
    protected void retry(String networkName, String nodeName, 
            String source, EventList events, int retriesLeft) throws Exception {
        
        if (retriesLeft > 0) {
            javax.jms.ObjectMessage mesg=_session.createObjectMessage(events);
            mesg.setStringProperty(JMSEPNManager.EPN_NETWORK, networkName);
            mesg.setStringProperty(JMSEPNManager.EPN_DESTINATION_NODE, nodeName);
            mesg.setStringProperty(JMSEPNManager.EPN_SOURCE_NODE, source);
            mesg.setIntProperty(JMSEPNManager.EPN_RETRIES_LEFT, retriesLeft);
            _producer.send(mesg);
        } else {
            // Events failed to be processed
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void notify(String networkName, String nodeName, EventList processed) {
        
        // TODO: Send to JMS topic
    }
    
    public void close() throws Exception {
        try {
            _session.close();
            _connection.close();
        } catch(Exception e) {
            LOG.log(Level.SEVERE, "Failed to close JMS", e);
        }
    }
    
    protected class JMSEPNContext implements EPNContext {

        public Channel getChannel(String source,
                org.savara.bam.epn.Destination dest) throws Exception {
            return new JMSChannel(_session, _producer, source, dest);
        }

    }
}
