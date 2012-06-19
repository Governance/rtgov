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
package org.overlord.bam.epn.jee;

import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import static javax.ejb.ConcurrencyManagementType.BEAN;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.Singleton;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.overlord.bam.epn.AbstractEPNManager;
import org.overlord.bam.epn.Channel;
import org.overlord.bam.epn.EPNContainer;
import org.overlord.bam.epn.EventList;
import org.overlord.bam.epn.Network;
import org.overlord.bam.epn.Node;
import org.overlord.bam.epn.NotificationType;

/**
 * This class provides the JMS implementation of
 * the EPN Manager.
 *
 */
@Singleton(name="EPNManager")
@ConcurrencyManagement(BEAN)
public class JEEEPNManagerImpl extends AbstractEPNManager implements JEEEPNManager {
    
    @Resource(mappedName = "java:/JmsXA")
    private ConnectionFactory _connectionFactory;
    
    @Resource(mappedName = "java:/EPNEvents")
    private Destination _epnEventsDestination;
    
    @Resource(mappedName = "java:/EPNNotifications")
    private Destination _epnNotificationsDestination;
    
    /** The subscription subjects. **/
    public static final String EPN_SUBJECTS = "EPNSubjects";
    /** The EPN Network Name. **/
    public static final String EPN_NETWORK = "EPNNetwork";
    /** The EPN Version. **/
    public static final String EPN_VERSION = "EPNVersion";
    /** The EPN Destination Node Names. **/
    public static final String EPN_DESTINATION_NODES = "EPNDestinationNodes";
    /** The EPN Source Node Name. **/
    public static final String EPN_SOURCE_NODE = "EPNSourceNode";
    /** The EPN Number of Retries Left. **/
    public static final String EPN_RETRIES_LEFT = "EPNRetriesLeft";
    /** The EPN notification type. **/
    public static final String EPN_NOTIFY_TYPE = "EPNNotifyType";
    
    private Connection _connection=null;
    private Session _session=null;
    private MessageProducer _eventsProducer=null;
    private MessageProducer _notificationsProducer=null;
    
    private EPNContainer _container=new JMSEPNContainer();
    
    private static final Logger LOG=Logger.getLogger(JEEEPNManagerImpl.class.getName());

    /**
     * {@inheritDoc}
     */
    protected EPNContainer getContainer() {
        return (_container);
    }
    
    /**
     * The initialize method.
     */
    @PostConstruct
    public void init() {
        LOG.info("Initialize JMS EPN Manager");
        
        try {
            _connection = _connectionFactory.createConnection();
            
            // TODO: Issue - must be non-transacted, to enable arquillian test
            // to work, but ideally needs to be transacted in production to
            // ensure transactional consistency
            _session = _connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            
            _eventsProducer = _session.createProducer(_epnEventsDestination);
            _notificationsProducer = _session.createProducer(_epnNotificationsDestination);
            
            _connection.start();
            
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to initialize the JMS EPN Manager", e);
        }
        
        setUsePrePostEventListProcessing(true);
    }   

    /**
     * {@inheritDoc}
     */
    public void publish(String subject, java.util.List<? extends java.io.Serializable> events) throws Exception {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Publish "+events+" to subject '"+subject+"'");
        }
        
        javax.jms.ObjectMessage mesg=_session.createObjectMessage(new EventList(events));
        mesg.setStringProperty(JEEEPNManagerImpl.EPN_SUBJECTS, subject);
        
        _eventsProducer.send(mesg);
    }

    /**
     * This method handles an events message received via JMS.
     * 
     * @param message The JMS message carrying the events
     * @throws Exception Failed to handle message
     */
    public void handleEventsMessage(Message message) throws Exception {
        if (message instanceof ObjectMessage) {
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("EPNManager("+this+"): Received event batch: "+message);
            }
            
            EventList events=(EventList)((ObjectMessage)message).getObject();
            
            if (message.propertyExists(EPN_SUBJECTS)) {
                dispatchToSubjects(message.getStringProperty(EPN_SUBJECTS), events);
            }
            
            if (message.propertyExists(EPN_NETWORK)) {
                String network=message.getStringProperty(JEEEPNManagerImpl.EPN_NETWORK);
                String version=message.getStringProperty(JEEEPNManagerImpl.EPN_VERSION);
                String node=message.getStringProperty(JEEEPNManagerImpl.EPN_DESTINATION_NODES);
                String source=message.getStringProperty(JEEEPNManagerImpl.EPN_SOURCE_NODE);
                int retriesLeft=message.getIntProperty(JEEEPNManagerImpl.EPN_RETRIES_LEFT);
                
                dispatchToNodes(network, version, node, source, events, retriesLeft);
            }
        } else {
            LOG.severe("Unsupport message '"+message+"' received");
        }
    }
    
    /**
     * This method dispatches the events to any network that has subscribed to any one of
     * the supplied list of subjects.
     * 
     * @param subjectList The subject list
     * @param events The list of events
     * @throws Exception Failed to dispatch events to the networks associated with the
     *                          supplied list of subjects
     */
    protected void dispatchToSubjects(String subjectList, EventList events) throws Exception {
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Dispatch to subjects="+subjectList);
        }

        String[] subjects=subjectList.split(",");
        for (String subject : subjects) {
            java.util.List<Network> networks=getNetworksForSubject(subject);
            
            if (networks == null) {
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("No networks exist for subject="+subject);
                }
            } else {
                for (Network network : networks) {
                    java.util.List<Node> nodes=network.getNodesForSubject(subject);

                    if (nodes != null) {
                        preProcessEvents(events, network);
                        
                        for (int i=0; i < nodes.size(); i++) {
                            dispatch(network, nodes.get(i), null, events, -1);
                        }
                        
                        postProcessEvents(events);
                    }
                }
            }
        }
    }

    /**
     * This method dispatches the events to the list of nodes that are associated with
     * the named network.
     * 
     * @param networkName The name of the network
     * @param version The version, or null for current
     * @param nodeList The list of nodes
     * @param source The source node, or null if sending to root
     * @param events The list of events to be processed
     * @param retriesLeft The number of retries left, or -1 if should be max value
     * @throws Exception Failed to dispatch the events for processing
     */
    protected void dispatchToNodes(String networkName, String version, String nodeList, String source,
                        EventList events, int retriesLeft) throws Exception {
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Dispatch to network="+networkName+" and nodes="+nodeList);
        }
        
        Network network=getNetwork(networkName, version);
        
        if (network == null) {
            String mesg="Unable to find network '"+networkName+"' version '"+version+"'";
            
            LOG.severe(mesg);
            
            throw new IllegalArgumentException(mesg);
        } else {
            preProcessEvents(events, network);

            String[] nodes=nodeList.split(",");
            for (String nodeName : nodes) {
                Node node=network.getNode(nodeName);
                
                dispatch(network, node, source, events, retriesLeft);
            }
            
            postProcessEvents(events);
        }
    }

    /**
     * This method handles a notification message received via JMS.
     * 
     * @param message The JMS message carrying the notification
     * @throws Exception Failed to handle notification
     */
    public void handleNotificationsMessage(Message message) throws Exception {
        if (message instanceof ObjectMessage) {
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("EPNManager("+this+"): Received notification batch: "+message);
            }
            
            EventList events=(EventList)((ObjectMessage)message).getObject();
            
            String networkName=message.getStringProperty(JEEEPNManagerImpl.EPN_NETWORK);
            String version=message.getStringProperty(JEEEPNManagerImpl.EPN_VERSION);
            String nodeName=message.getStringProperty(JEEEPNManagerImpl.EPN_DESTINATION_NODES);
            NotificationType type=NotificationType.valueOf(message.getStringProperty(JEEEPNManagerImpl.EPN_NOTIFY_TYPE));
            
            dispatchNotificationToListeners(networkName, version, nodeName, type, events);
        } else {
            LOG.severe("Unsupport message '"+message+"' received");
        }
    }

    /**
     * This method dispatches a set of events directly to the supplied
     * network and node.
     * 
     * @param network The network
     * @param node The node
     * @param source The source node, or null if sending to root
     * @param events The list of events to be processed
     * @param retriesLeft The number of retries left, or -1 if should be max value
     * @throws Exception Failed to dispatch the events for processing
     */
    protected void dispatch(Network network, Node node,
                            String source, EventList events,
                            int retriesLeft) throws Exception {
        
        if (retriesLeft == -1) {
            retriesLeft = node.getMaxRetries();
        }
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Dispatch "+network.getName()+"/"+network.getVersion()
                    +"/"+node.getName()+" ("+node
                    +") events="+events+" retriesLeft="+retriesLeft);
        }

        EventList retries=process(network, node,
                            source, events, retriesLeft);
        
        if (retries != null) {
            retry(network.getName(), network.getVersion(), node.getName(), source, retries, retriesLeft-1);
        }
    }
    
    /**
     * This method handles retrying the supplied set of events, if the number of
     * retries left is greater than 0.
     * 
     * @param networkName The name of the network
     * @param version The version
     * @param nodeName The optional node name, or root node if not specified
     * @param source The source
     * @param events The events
     * @param retriesLeft The number of retries now remaining after this failure to process them
     * @throws Exception Failed to retry the events processing
     */
    protected void retry(String networkName, String version, String nodeName, 
            String source, EventList events, int retriesLeft) throws Exception {
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Retry "+networkName+"/"+nodeName+" events="+events+" retriesLeft="+retriesLeft);
        }
        
        if (retriesLeft > 0) {
            javax.jms.ObjectMessage mesg=_session.createObjectMessage(events);
            mesg.setStringProperty(JEEEPNManagerImpl.EPN_NETWORK, networkName);
            mesg.setStringProperty(JEEEPNManagerImpl.EPN_VERSION, version);
            mesg.setStringProperty(JEEEPNManagerImpl.EPN_DESTINATION_NODES, nodeName);
            mesg.setStringProperty(JEEEPNManagerImpl.EPN_SOURCE_NODE, source);
            mesg.setIntProperty(JEEEPNManagerImpl.EPN_RETRIES_LEFT, retriesLeft);
            _eventsProducer.send(mesg);
        } else {
            // Events failed to be processed
            // TODO: Should this be reported via the manager?
            LOG.severe("Unable to process events");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void notifyListeners(String networkName, String version,
                    String nodeName, NotificationType type, EventList events) throws Exception {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Notify events processed "+networkName+"/"+version+"/"+nodeName+" events="+events);
        }

        javax.jms.ObjectMessage mesg=_session.createObjectMessage(events);
        mesg.setStringProperty(JEEEPNManagerImpl.EPN_NETWORK, networkName);
        mesg.setStringProperty(JEEEPNManagerImpl.EPN_VERSION, version);
        mesg.setStringProperty(JEEEPNManagerImpl.EPN_DESTINATION_NODES, nodeName);
        mesg.setStringProperty(JEEEPNManagerImpl.EPN_NOTIFY_TYPE, type.name());
        _notificationsProducer.send(mesg);
    }
    
    /**
     * {@inheritDoc}
     */
    @PreDestroy
    public void close() throws Exception {
        LOG.info("Closing JMS EPN Manager");
        try {
            _session.close();
            _connection.close();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to close JMS", e);
        }
    }
    
    /**
     * This class provides the JMS implementation of the EPN container.
     *
     */
    protected class JMSEPNContainer implements EPNContainer {

        /**
         * {@inheritDoc}
         */
        public Channel getChannel(Network network, String source, String dest)
                throws Exception {
            return new JMSChannel(_session, _eventsProducer, network, source, dest);
        }

        /**
         * {@inheritDoc}
         */
        public Channel getChannel(Network network, String source)
                throws Exception {
            return new JMSChannel(_session, _eventsProducer, network, source, null);
        }

        /**
         * {@inheritDoc}
         */
        public Channel getChannel(String subject) throws Exception {
            return new JMSChannel(_session, _eventsProducer, subject);
        }

        /**
         * {@inheritDoc}
         */
        public void send(EventList events, List<Channel> channels)
                throws Exception {
            
            if (channels.size() > 0) {
                javax.jms.ObjectMessage mesg=_session.createObjectMessage(events);
                
                String subjects=null;
                String destNodes=null;
                String networkName=null;
                String version=null;
                String sourceNode=null;
                boolean notification=false;
                
                for (Channel channel : channels) {
                    if (channel instanceof JMSChannel) {
                        JMSChannel jmsc=(JMSChannel)channel;
                        
                        if (jmsc.getSubject() != null) {
                            if (subjects == null) {
                                subjects = jmsc.getSubject();
                            } else {
                                subjects += ","+jmsc.getSubject();
                            }
                        } else if (jmsc.isNotificationChannel()) {
                            notification = true;
                            networkName = jmsc.getNetworkName();
                            version = jmsc.getVersion();
                            sourceNode = jmsc.getSourceNode();
                            
                        } else {
                            if (destNodes == null) {
                                destNodes = jmsc.getDestinationNode();
                                networkName = jmsc.getNetworkName();
                                version = jmsc.getVersion();
                                sourceNode = jmsc.getSourceNode();
                            } else {
                                destNodes += ","+jmsc.getDestinationNode();
                            }
                        }
                    } else {
                        LOG.severe("Unexpected channel type '"+channel+"'");
                    }
                }
                
                boolean sendResults=false;
                
                if (subjects != null) {
                    mesg.setStringProperty(JEEEPNManagerImpl.EPN_SUBJECTS, subjects);
                    sendResults = true;
                }
                
                if (destNodes != null) {
                    mesg.setStringProperty(JEEEPNManagerImpl.EPN_NETWORK, networkName);
                    mesg.setStringProperty(JEEEPNManagerImpl.EPN_VERSION, version);
                    mesg.setStringProperty(JEEEPNManagerImpl.EPN_DESTINATION_NODES, destNodes);
                    mesg.setStringProperty(JEEEPNManagerImpl.EPN_SOURCE_NODE, sourceNode);   
                    mesg.setIntProperty(JEEEPNManagerImpl.EPN_RETRIES_LEFT, -1);
                    sendResults = true;
                }
                
                if (sendResults) {
                    if (LOG.isLoggable(Level.FINEST)) {
                        LOG.finest("Send events network="+networkName
                                +" version="+version
                                +" sourceNode="+sourceNode
                                +" nodes="+destNodes
                                +" subjects="+subjects+" events="+events);
                    }
        
                    _eventsProducer.send(mesg);  
                }
                
                if (notification) {
                    notifyListeners(networkName, version, sourceNode, NotificationType.Results, events);
                }

            }
        }
    }
}
