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
package org.savara.bam.activity.server.jms.client;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.JMSFactoryType;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.savara.bam.activity.model.ActivityUnit;
import org.savara.bam.activity.server.ActivityQuery;
import org.savara.bam.activity.server.ActivityServer;

/**
 * This class provides the JMS implementation of the activity logger.
 *
 */
public class JMSActivityServer implements ActivityServer {

    private static final String ACTIVITY_MONITOR_SERVER = "ActivityMonitorServer";

    private static final Logger LOG=Logger.getLogger(JMSActivityServer.class.getName());
    
    private Connection _connection;
    private Session _session;
    private MessageProducer _producer;
    
    /**
     * This method initializes the JMS activity server client.
     */
    @PostConstruct
    public void init() {
        
        try {
            Queue queue = HornetQJMSClient.createQueue(ACTIVITY_MONITOR_SERVER);

            TransportConfiguration transportConfiguration = new TransportConfiguration(NettyConnectorFactory.class.getName());

            ConnectionFactory cf = (ConnectionFactory) HornetQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF, transportConfiguration);

            _connection = cf.createConnection();

            _session = _connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            _producer = _session.createProducer(queue);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to setup JMS connection", e);
        }
    }
    

    /**
     * {@inheritDoc}
     */
    public void store(List<ActivityUnit> activities) throws Exception {
        javax.jms.ObjectMessage mesg=_session.createObjectMessage();
        
        mesg.setObject((java.io.Serializable)activities);
        
        _producer.send(mesg);
    }

    /**
     * {@inheritDoc}
     */
    public List<ActivityUnit> query(ActivityQuery query) throws Exception {
        throw new java.lang.UnsupportedOperationException();
    }

    /**
     * This method closes the JMS activity logger.
     */
    @PreDestroy
    public void close() {
        try {
            _session.close();
            _connection.close();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to close JMS connection", e);
        }
    }
}
