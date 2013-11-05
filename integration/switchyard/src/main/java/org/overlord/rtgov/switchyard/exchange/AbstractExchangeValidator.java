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
package org.overlord.rtgov.switchyard.exchange;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.transaction.TransactionManager;
import javax.xml.namespace.QName;

import org.overlord.rtgov.activity.model.soa.RPCActivityType;
import org.overlord.rtgov.activity.model.soa.RequestReceived;
import org.overlord.rtgov.activity.model.soa.RequestSent;
import org.overlord.rtgov.activity.model.soa.ResponseReceived;
import org.overlord.rtgov.activity.model.soa.ResponseSent;
import org.overlord.rtgov.activity.collector.ActivityCollector;
import org.overlord.rtgov.activity.collector.ActivityCollectorAccessor;
import org.overlord.rtgov.common.util.RTGovProperties;
import org.overlord.rtgov.internal.switchyard.exchange.PropertyAccessor;
import org.switchyard.Exchange;
import org.switchyard.ExchangePhase;
import org.switchyard.Message;
import org.switchyard.Property;
import org.switchyard.Service;
import org.switchyard.ServiceReference;
import org.switchyard.extensions.java.JavaService;
import org.switchyard.extensions.wsdl.WSDLService;
import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.security.context.SecurityContext;
import org.switchyard.security.context.SecurityContextManager;
import org.switchyard.security.credential.Credential;

/**
 * This class observes exchanges and uses the information to create activity
 * events to be validateda.
 *
 */
public class AbstractExchangeValidator {
    
    private static final Logger LOG=Logger.getLogger(AbstractExchangeValidator.class.getName());
    
    private static final String JAVAX_TRANSACTION_MANAGER = "javax.transaction.manager";
    private static final String JBOSS_TRANSACTION_MANAGER = "java:jboss/TransactionManager";

    private TransactionManager _transactionManager=null;

    private ActivityCollector _activityCollector=null;
    
    /**
     * This method initializes the auditor.
     */
    @PostConstruct
    protected void init() {
        
        _activityCollector = ActivityCollectorAccessor.getActivityCollector();
        
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("*********** Exchange Interceptor Initialized with collector="+_activityCollector);
        }

        if (_activityCollector == null) {
            LOG.severe("Failed to get activity collector");
        }
        
        try {
            String txnMgr=RTGovProperties.getProperty(JAVAX_TRANSACTION_MANAGER);
            
            if (txnMgr == null) {
                txnMgr = JBOSS_TRANSACTION_MANAGER;
            }
            
            InitialContext ctx=new InitialContext();
            
            _transactionManager = (TransactionManager)ctx.lookup(txnMgr);
            
            if (LOG.isLoggable(Level.FINER)) {
                LOG.finer("Transaction manager '"+txnMgr+"' = "+_transactionManager);
            }
            
        } catch (Exception e) {
            LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                    "rtgov-jbossas.Messages").getString("RTGOV-JBOSSAS-1"), e);
        }
    }

    /**
     * This method handles the exchange.
     * 
     * @param exch The exchange
     */
    protected void handleExchange(Exchange exch) {        
        org.switchyard.Message mesg=exch.getMessage();
        ExchangePhase phase=exch.getPhase();
        
        if (mesg == null) {
            LOG.severe("Could not obtain message for phase ("+phase+") and exchange: "+exch);
            return;
        }
        
        org.switchyard.Context context=exch.getContext();
        
        Service provider=exch.getProvider();
        ServiceReference consumer=exch.getConsumer();
        
        SecurityContextManager scm=new SecurityContextManager(exch.getConsumer().getDomain());
        
        SecurityContext securityContext=scm.getContext(exch);

        ExchangeContract contract=exch.getContract();
        
        if (provider == null
                && LOG.isLoggable(Level.FINEST)) {
            LOG.finest("No provider specified - probably an exception: "
                        +mesg.getContent());
        }
        
        if (_activityCollector != null) {
            
            // Check if transaction should be started
            boolean f_txnStarted=false;
            
            try {
                if (_transactionManager != null && _transactionManager.getTransaction() == null) {
                    _transactionManager.begin();
                    f_txnStarted = true;
                    
                    if (LOG.isLoggable(Level.FINEST)) {
                        LOG.finest("Validator txn has started");
                    }
                }
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Failed to start validator transaction", e);
            }
            
            try {
                // TODO: If message is transformed, then should the contentType
                // be updated to reflect the transformed type?
                
                String messageId=null;
                Property mip=context.getProperty(Exchange.MESSAGE_ID, org.switchyard.Scope.MESSAGE);
                if (mip != null) {
                    messageId = (String)mip.getValue();
                }
                
                String contentType=null;
                Property ctp=context.getProperty(Exchange.CONTENT_TYPE, org.switchyard.Scope.MESSAGE);
                if (ctp != null) {
                    contentType = ((QName)ctp.getValue()).toString();
                    
                    // RTGOV-250 - remove java: prefix from Java types, to make the type consistent with
                    // events reported outside switchyard
                    if (contentType != null && contentType.startsWith("java:")) {
                        contentType = contentType.substring(5);
                    }
                }
                
                // Extract service type and operation from the consumer
                // (service reference), as provider is not always available
                QName serviceType=consumer.getName();
                String opName=contract.getConsumerOperation().getName();
                
                if (phase == ExchangePhase.IN) {
                    if (!consumer.getServiceMetadata().getRegistrant().isBinding()) {
                        // Only record the request being sent, if the
                        // source is a component, not a binding
                    
                        RequestSent sent=new RequestSent();
                        
                        // Only report service type if provider is not a binding
                        if (provider == null
                                || !provider.getServiceMetadata().getRegistrant().isBinding()) {
                            sent.setServiceType(serviceType.toString()); 
                        }
                        
                        sent.setInterface(getInterface(consumer, provider));                
                        sent.setOperation(opName);
                        sent.setMessageId(messageId);
                        
                        validate(mesg, contentType, sent, securityContext); 
                    }
                    
                    if (provider == null
                            || !provider.getServiceMetadata().getRegistrant().isBinding()) {
                        RequestReceived recvd=new RequestReceived();
                        
                        recvd.setServiceType(serviceType.toString());                
                        recvd.setInterface(getInterface(consumer, provider));                
                        recvd.setOperation(opName);
                        recvd.setMessageId(messageId);
                        
                        validate(mesg, contentType, recvd, securityContext); 
                    }
                    
                } else if (phase == ExchangePhase.OUT) {
                    
                    if (contentType == null) {
                        // Ignore as probably due to exception on handling the request
                        if (LOG.isLoggable(Level.FINEST)) {
                            LOG.finest("No content type - possibly due to exception on handling the request");
                        }
                        return;
                    }
                    
                    String relatesTo=null;
                    Property rtp=context.getProperty(Exchange.RELATES_TO, org.switchyard.Scope.MESSAGE);
                    if (rtp != null) {
                        relatesTo = (String)rtp.getValue();
                    }
                    
                    if (provider == null
                            || !provider.getServiceMetadata().getRegistrant().isBinding()) {
                        ResponseSent sent=new ResponseSent();
                                        
                        // Only report service type if provider is not a binding
                        if (provider == null
                                || !provider.getServiceMetadata().getRegistrant().isBinding()) {
                            sent.setServiceType(serviceType.toString()); 
                        }
    
                        sent.setInterface(getInterface(consumer, provider));                
                        sent.setOperation(opName);
                        sent.setMessageId(messageId);
                        sent.setReplyToId(relatesTo);
                        
                        validate(mesg, contentType, sent, securityContext); 
                    }
                    
                    if (!consumer.getServiceMetadata().getRegistrant().isBinding()) {
                        // Only record the response being received, if the
                        // target is a component, not a binding
                        ResponseReceived recvd=new ResponseReceived();
                        
                        recvd.setServiceType(serviceType.toString());                
                        recvd.setInterface(getInterface(consumer, provider));                
                        recvd.setOperation(opName);
                        recvd.setMessageId(messageId);
                        recvd.setReplyToId(relatesTo);
                        
                        validate(mesg, contentType, recvd, securityContext); 
                    }
                }
            } finally {
                if (f_txnStarted) {
                    try {
                        _transactionManager.commit();
                        
                        if (LOG.isLoggable(Level.FINEST)) {
                            LOG.finest("Validator txn has committed");
                        }
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, "Failed to commit validator transaction", e);
                    }
                }
            }
        }
    }
    
    /**
     * This method extracts the interface from the exchange details.
     * 
     * @param consumer The exchange consumer
     * @param provider The exchange provider
     * @return The interface
     */
    protected String getInterface(ServiceReference consumer, Service provider) {
        String ret=null;
        ServiceInterface intf=null;
        
        if (consumer.getServiceMetadata().getRegistrant().isBinding()) {
            intf = consumer.getInterface();
        } else {
            intf = provider.getInterface();
        }
        
        if (JavaService.TYPE.equals(intf.getType())) {
            ret = ((JavaService)intf).getJavaInterface().getName();
        } else if (WSDLService.TYPE.equals(intf.getType())) {
            ret = ((WSDLService)intf).getPortType().toString();
        }
        
        return (ret);
    }
    
    /**
     * This method validates the supplied information as an activity
     * event.
     * 
     * @param exchange The exchange
     * @param contentType The message content type
     * @param at The activity type
     * @param sc The optional security context
     */
    protected void validate(Message msg, String contentType,
                RPCActivityType at, SecurityContext sc) {
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Validate msg="+msg+" contentType="+contentType+" at="+at);
        }
        
        if (at != null) {
            at.setMessageType(contentType);
            
            Object content=msg.getContent();
            
            at.setContent(_activityCollector.processInformation(null,
                          contentType, content, new PropertyAccessor(msg.getContext()), at));
            
            // Check if principal has been defined
            if (sc != null) {
                for (Credential cred : sc.getCredentials()) {
                    if (cred instanceof org.switchyard.security.credential.NameCredential) {
                        at.setPrincipal(((org.switchyard.security.credential.NameCredential)cred).getName());
                        break;
                    } else if (cred instanceof org.switchyard.security.credential.PrincipalCredential) {
                        at.setPrincipal(((org.switchyard.security.credential.PrincipalCredential)cred)
                                            .getPrincipal().getName());
                        break;
                    }
                }
            }
            
            try {
                _activityCollector.validate(at);
            } catch (Exception e) {
                // Strip the exception and just return the message
                throw new RuntimeException(e.getMessage());
            }
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Activity is valid: at="+at);
            }
            
        }
    }

}
