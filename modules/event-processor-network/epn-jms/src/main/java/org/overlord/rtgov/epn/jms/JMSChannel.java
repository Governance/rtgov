/*
 * 2012-3 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.rtgov.epn.jms;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.overlord.rtgov.epn.Channel;
import org.overlord.rtgov.epn.EventList;
import org.overlord.rtgov.epn.Network;

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
    private boolean _notification=false;

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
     * @param network The network
     * @param subject The subject
     * @param notification Whether the subject is for notification
     */
    public JMSChannel(javax.jms.Session session, javax.jms.MessageProducer producer,
                Network network, String subject, boolean notification) {
        _session = session;
        _producer = producer;
        _network = network;
        _subject = subject;
        _notification = notification;
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
        return (_notification);
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
            mesg.setStringProperty(JMSEPNManagerImpl.EPN_SUBJECTS, _subject);
           
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Sending events '"+events+"' to subject="+_subject);
            }
        } else {
            mesg.setStringProperty(JMSEPNManagerImpl.EPN_NETWORK, _network.getName());
            mesg.setStringProperty(JMSEPNManagerImpl.EPN_VERSION, _network.getVersion());
            mesg.setStringProperty(JMSEPNManagerImpl.EPN_DESTINATION_NODES, _destinationNode);
            mesg.setStringProperty(JMSEPNManagerImpl.EPN_SOURCE, _sourceNode);
            mesg.setIntProperty(JMSEPNManagerImpl.EPN_RETRIES_LEFT, retriesLeft);
            
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
