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
package org.savara.bam.epn.jee;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.savara.bam.epn.Channel;
import org.savara.bam.epn.EventList;
import org.savara.bam.epn.Network;

/**
 * This class represents a JMS implementation of the event destination
 * for sending a list of events.
 *
 */
public class JMSChannel implements Channel {
    
    private static final Logger LOG=Logger.getLogger(JMSChannel.class.getName());
    
    private javax.jms.Session _session=null;
    private javax.jms.MessageProducer _producer=null;
    private Network _network=null;
    private String _destinationNode=null;
    private String _sourceNode=null;
    private String _subject=null;

    /**
     * This is the constructor for the JMS channel.
     * 
     * @param session The session
     * @param producer The producer
     * @param network The network
     * @param sourceNode The source node name
     * @param destNode The destination node name
     */
    public JMSChannel(javax.jms.Session session, javax.jms.MessageProducer producer,
                            Network network, String sourceNode, String destNode) {
        _session = session;
        _producer = producer;
        _network = network;
        _destinationNode = destNode;
        _sourceNode = sourceNode;
    }
    
    /**
     * This is the constructor for the JMS channel.
     * 
     * @param session The session
     * @param producer The producer
     * @param subject The subject
     */
    public JMSChannel(javax.jms.Session session, javax.jms.MessageProducer producer,
                            String subject) {
        _session = session;
        _producer = producer;
        _subject = subject;
    }
    
    /**
     * This method returns the subject.
     * 
     * @return The subject
     */
    public String getSubject() {
        return (_subject);
    }
    
    /**
     * This method determines whether this channel is a notification
     * channel.
     * 
     * @return Whether this is a notification channel
     */
    public boolean isNotificationChannel() {
        return (_destinationNode == null);
    }
    
    /**
     * This method returns the network name.
     * 
     * @return The network name
     */
    public String getNetworkName() {
        return (_network.getName());
    }
    
    /**
     * This method returns the version.
     * 
     * @return The version
     */
    public String getVersion() {
        return (_network.getVersion());
    }
    
    /**
     * This method returns the destination node.
     * 
     * @return The destination node
     */
    public String getDestinationNode() {
        return (_destinationNode);
    }
    
    /**
     * This method returns the source node.
     * 
     * @return The source node
     */
    public String getSourceNode() {
        return (_sourceNode);
    }
    
    /**
     * {@inheritDoc}
     */
    public void send(EventList events) throws Exception {
        send(events, -1);
    }
 
    /**
     * {@inheritDoc}
     */
    public void send(EventList events, int retriesLeft) throws Exception {
        javax.jms.ObjectMessage mesg=_session.createObjectMessage(events);
        
        if (_subject != null) {
            mesg.setStringProperty(JEEEPNManagerImpl.EPN_SUBJECTS, _subject);
           
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Sending events '"+events+"' to subject="+_subject);
            }
        } else {
            mesg.setStringProperty(JEEEPNManagerImpl.EPN_NETWORK, _network.getName());
            mesg.setStringProperty(JEEEPNManagerImpl.EPN_VERSION, _network.getVersion());
            mesg.setStringProperty(JEEEPNManagerImpl.EPN_DESTINATION_NODES, _destinationNode);
            mesg.setStringProperty(JEEEPNManagerImpl.EPN_SOURCE_NODE, _sourceNode);
            mesg.setIntProperty(JEEEPNManagerImpl.EPN_RETRIES_LEFT, retriesLeft);
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Sending events '"+events+"' to network="+_network.getName()
                        +" version="+_network.getVersion()+" node="+_destinationNode);
            }
        }
        
        _producer.send(mesg);
    }
 
    /**
     * This method closes the JMS channel.
     * 
     * @throws Exception Failed to close
     */
    public void close() throws Exception {
        // Creator is responsible for closing JMS session
    }

}
