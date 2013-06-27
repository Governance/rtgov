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
package org.overlord.rtgov.activity.server.jms.client;

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
import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.activity.server.ActivityServer;
import org.overlord.rtgov.activity.server.QuerySpec;

/**
 * This class provides the JMS client implementation of the activity server.
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
            LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                    "activity-server-jmsc.Messages").getString("ACTIVITY-SERVER-JMSC-1"));
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
    public ActivityUnit getActivityUnit(String id) throws Exception {
        throw new java.lang.UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public List<ActivityType> getActivityTypes(Context context) throws Exception {
        throw new java.lang.UnsupportedOperationException();
    }
    
    /**
     * {@inheritDoc}
     */
    public List<ActivityType> getActivityTypes(Context context,
                            long from, long to) throws Exception {
        throw new java.lang.UnsupportedOperationException();
    }
    
    /**
     * {@inheritDoc}
     */
    public List<ActivityType> query(QuerySpec query) throws Exception {
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
            LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                    "activity-server-jmsc.Messages").getString("ACTIVITY-SERVER-JMSC-2"));
        }
    }

}
