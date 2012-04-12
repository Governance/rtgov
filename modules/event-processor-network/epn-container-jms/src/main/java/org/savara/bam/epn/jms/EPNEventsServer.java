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
package org.savara.bam.epn.jms;

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
import javax.naming.InitialContext;

import java.util.logging.Level;
import java.util.logging.Logger;

@MessageDriven(name = "EPNEventsServer", messageListenerInterface = MessageListener.class,
               activationConfig =
                     {
                        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
                        @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/EPNEvents")
                     })
@TransactionManagement(value= TransactionManagementType.CONTAINER)
@TransactionAttribute(value= TransactionAttributeType.REQUIRED)
public class EPNEventsServer implements MessageListener {

    private static final Logger LOG=Logger.getLogger(EPNEventsServer.class.getName());
    
    private JMSEPNManager _epnManager;
    
    public EPNEventsServer() {
    }
    
    @PostConstruct
    public void init() {
        LOG.info("Initialize EPN Events Server");
System.out.println("GPB: >>> Initialize EPN Events Server");
        
        try {
            InitialContext context=new InitialContext();
            
            _epnManager = (JMSEPNManager)context.lookup("java:/env/EPNManager");
        } catch(Exception e) {
            LOG.log(Level.SEVERE, "EPNManager was not found", e);
        }
    }
    
    @PreDestroy
    public void close() {
        LOG.info("Closing EPN Events Server");
    }

    public void onMessage(Message message) {
        if (_epnManager != null) {
            _epnManager.handleEventsMessage(message);
        }
    }

}
