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
package org.overlord.rtgov.internal.epn.jee;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.overlord.rtgov.common.registry.ServiceRegistryUtil;
import org.overlord.rtgov.epn.EPNManager;
import org.overlord.rtgov.epn.jms.JMSEPNManager;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is the JMS receiver for events being processed by the Event Processor
 * Network.
 *
 */
@MessageDriven(name = "EPNEventsServer", messageListenerInterface = MessageListener.class,
               activationConfig =
                     {
                        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
                        @ActivationConfigProperty(propertyName = "destination", propertyValue = "EPNEvents")
                     })
@TransactionManagement(value= TransactionManagementType.CONTAINER)
@TransactionAttribute(value= TransactionAttributeType.REQUIRED)
public class EPNEventsServer implements MessageListener {

    private static final Logger LOG=Logger.getLogger(EPNEventsServer.class.getName());
    
    private JMSEPNManager _epnManager;
    
    /**
     * The default constructor.
     */
    public EPNEventsServer() {
    }
    
    /**
     * The initialize method.
     */
    @PostConstruct
    public void init() {       
        EPNManager epnManager=ServiceRegistryUtil.getSingleService(EPNManager.class);
        
        if (epnManager instanceof JMSEPNManager) {
            _epnManager = (JMSEPNManager)epnManager;
        } else {
            LOG.severe(MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                    "epn-container-jee.Messages").getString("EPN-CONTAINER-JEE-7"), epnManager));
        }
        
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Initialize EPN Events Server with EPN Manager="+_epnManager);
        }
    }
    
    /**
     * The close method.
     */
    @PreDestroy
    public void close() {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Closing EPN Events Server");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void onMessage(Message message) {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Received events '"+message+"' - sending to "+_epnManager);        
        }
        
        if (_epnManager != null) {
            try {
                _epnManager.handleEventsMessage(message);
            } catch (Exception e) {
                LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                        "epn-container-jee.Messages").getString("EPN-CONTAINER-JEE-1"), e);
            }
        }
    }

}
