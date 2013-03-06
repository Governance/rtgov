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
package org.overlord.rtgov.epn.jee;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;


import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the JMS receiver for the Event Processor Network notifications.
 *
 */
@MessageDriven(name = "EPNNotificationsServer", messageListenerInterface = MessageListener.class,
               activationConfig =
                     {
                        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
                        @ActivationConfigProperty(propertyName = "destination", propertyValue = "EPNNotifications")
                     })
@TransactionManagement(value= TransactionManagementType.CONTAINER)
@TransactionAttribute(value= TransactionAttributeType.REQUIRED)
public class EPNNotificationServer implements MessageListener {

    private static final Logger LOG=Logger.getLogger(EPNNotificationServer.class.getName());
    
    @Inject
    private JEEEPNManager _epnManager;
    
    /**
     * This is the default constructor.
     */
    public EPNNotificationServer() {
    }
    
    /**
     * The initialize method.
     */
    @PostConstruct
    public void init() {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Initialize EPN Notifications Server with EPN Manager="+_epnManager);
        }
    }
    
    /**
     * The close method.
     */
    @PreDestroy
    public void close() {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Closing EPN Notifications Server");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void onMessage(Message message) {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Received notifications '"+message+"' - sending to "+_epnManager);        
        }

        if (_epnManager != null) {
            try {
                _epnManager.handleNotificationsMessage(message);
            } catch (Exception e) {
                LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                        "epn-container-jee.Messages").getString("EPN-CONTAINER-JEE-6"), e);
            }
        }
    }

}
