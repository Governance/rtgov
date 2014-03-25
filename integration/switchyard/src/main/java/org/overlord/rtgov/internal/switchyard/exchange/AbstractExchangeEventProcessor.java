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
package org.overlord.rtgov.internal.switchyard.exchange;

import java.util.EventObject;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.overlord.rtgov.activity.model.soa.RPCActivityType;
import org.overlord.rtgov.activity.model.soa.RequestReceived;
import org.overlord.rtgov.activity.model.soa.RequestSent;
import org.overlord.rtgov.activity.model.soa.ResponseReceived;
import org.overlord.rtgov.activity.model.soa.ResponseSent;
import org.overlord.rtgov.internal.switchyard.AbstractEventProcessor;
import org.switchyard.Exchange;
import org.switchyard.ExchangePhase;
import org.switchyard.Message;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.Service;
import org.switchyard.ServiceReference;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.extensions.wsdl.WSDLService;
import org.switchyard.label.BehaviorLabel;
import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.Registrant;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.extensions.java.JavaService;
import org.switchyard.runtime.event.ExchangeCompletionEvent;
import org.switchyard.security.context.SecurityContext;
import org.switchyard.security.context.SecurityContextManager;
import org.switchyard.security.credential.Credential;

/**
 * This class provides the abstract Exchange event based implementation of the
 * event processor.
 *
 */
public abstract class AbstractExchangeEventProcessor extends AbstractEventProcessor {
    
    private static final String GATEWAY_PROPERTY = "gateway";

    private static final String UNEXPECTED_FAULT = "ERROR";

    private static final String RTGOV_REQUEST_SENT = "rtgov.request.sent";

    private static final String RTGOV_REQUEST_RECEIVED = "rtgov.request.received";

    private static final Logger LOG=Logger.getLogger(AbstractExchangeEventProcessor.class.getName());

    private boolean _completedEvent=false;
    
    /**
     * This is the constructor.
     * 
     * @param eventType The event type associated with the processor
     * @param completed Whether the event processor represents a completed event
     */
    public AbstractExchangeEventProcessor(Class<? extends EventObject> eventType, boolean completed) {
        super(eventType);    
        
        _completedEvent = completed;
    }
    
    /**
     * This method obtains the exchange from the supplied event.
     * 
     * @param event The event
     * @return The exchange
     */
    protected abstract Exchange getExchange(EventObject event);

    /**
     * {@inheritDoc}
     */
    public void handleEvent(EventObject event) {
        try {
            Exchange exch=getExchange(event);
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("********* Exchange="+exch);
            }
            
            org.switchyard.Message mesg=exch.getMessage();
            
            ExchangePhase phase=exch.getPhase();
    
            if (phase == null) {
                LOG.severe("Could not obtain phase from exchange: "+exch);
                return;
            }
    
            if (mesg == null) {
                LOG.severe("Could not obtain message for phase ("+phase+") and exchange: "+exch);
                return;
            }
            
            org.switchyard.Context context=exch.getContext();
            
            Service provider=exch.getProvider();
            ServiceReference consumer=exch.getConsumer();
            
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
            
            if (phase == ExchangePhase.IN) {
                handleInExchange(exch, provider, consumer, messageId, contentType, mesg);
                
            } else if (phase == ExchangePhase.OUT) {            
                String relatesTo=null;
                Property rtp=context.getProperty(Exchange.RELATES_TO, org.switchyard.Scope.MESSAGE);
                if (rtp != null) {
                    relatesTo = (String)rtp.getValue();
                }
                
                handleOutExchange(exch, provider, consumer, messageId, relatesTo, contentType, mesg);
            }
        } catch (Throwable t) {
            LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                    "rtgov-switchyard.Messages").getString("RTGOV-SWITCHYARD-1"), t);
        }
    }
    
    /**
     * This method handles the 'in' exchange.
     * 
     * @param exch The exchange
     * @param provider The provider
     * @param consumer The consumer
     * @param messageId The message id
     * @param contentType The content type
     * @param mesg The message
     */
    protected void handleInExchange(Exchange exch,
            Service provider, ServiceReference consumer, String messageId,
            String contentType, org.switchyard.Message mesg) {
        Registrant consumerReg=consumer.getServiceMetadata().getRegistrant();
        
        if (_completedEvent) {
            // The return side of a one way exchange, so record a response sent/received
            handleOutExchange(exch, provider, consumer, messageId+"onewayreturn", messageId, null, null);
            
            // Nothing to do, as appears to be a one-way exchange
            return;
        }

        Registrant providerReg=(provider == null ? null : provider.getServiceMetadata().getRegistrant());

        String intf=getInterface(consumer, provider, consumerReg);
        
        SecurityContextManager scm=new SecurityContextManager(exch.getConsumer().getDomain());
        
        SecurityContext securityContext=scm.getContext(exch);

        ExchangeContract contract=exch.getContract();
        
        // Extract service type and operation from the consumer
        // (service reference), as provider is not always available
        QName serviceType=consumer.getName();
        String opName=contract.getConsumerOperation().getName();
        
        if (consumerReg.isBinding()) {
            getActivityCollector().startScope();
        } else {
            // Only record the request being sent, if the
            // source is a component, not a binding
        
            RequestSent sent=new RequestSent();
            
            // Only report service type if provider is not a binding
            if (providerReg == null
                    || !providerReg.isBinding()) {
                sent.setServiceType(serviceType.toString()); 

            } else if (providerReg.isBinding()) {
                String gatewayName=exch.getContext().<String>getPropertyValue(ExchangeCompletionEvent.GATEWAY_NAME);
                
                java.util.List<BindingModel> bindings=providerReg.<java.util.List<BindingModel>>getConfig();
                
                for (int i=0; gatewayName != null && i < bindings.size(); i++) {
                    BindingModel bm=bindings.get(i);
                    if (gatewayName.equals(bm.getName())) {
                        sent.getProperties().put(GATEWAY_PROPERTY, bm.getType());
                    }
                }                
            }
            
            sent.setInterface(intf);                
            sent.setOperation(opName);
            sent.setMessageId(messageId);
           
            record(mesg, contentType, sent, securityContext, exch); 
            
            if (intf == null) {
                // Save activity event in exchange
                Property prop=exch.getContext().setProperty(RTGOV_REQUEST_SENT, sent, Scope.EXCHANGE);
                prop.addLabels(BehaviorLabel.TRANSIENT.label());
            }
        }
        
        if (providerReg == null
                || !providerReg.isBinding()) {
            RequestReceived recvd=new RequestReceived();
            
            recvd.setServiceType(serviceType.toString());                
            recvd.setInterface(intf);                
            recvd.setOperation(opName);
            recvd.setMessageId(messageId);
            
            if (consumerReg.isBinding()) {
                String gatewayName=exch.getContext().<String>getPropertyValue(ExchangeCompletionEvent.GATEWAY_NAME);
                
                java.util.List<BindingModel> bindings=consumerReg.<java.util.List<BindingModel>>getConfig();
                
                for (int i=0; gatewayName != null && i < bindings.size(); i++) {
                    BindingModel bm=bindings.get(i);
                    if (gatewayName.equals(bm.getName())) {
                        recvd.getProperties().put(GATEWAY_PROPERTY, bm.getType());
                    }
                }
            }
            
            record(mesg, contentType, recvd, securityContext, exch); 
            
            // Save activity event in exchange
            // RTGOV-262 Need to store this event, event if interface set,
            // in case needs to establish relationship from exception response
            Property prop=exch.getContext().setProperty(RTGOV_REQUEST_RECEIVED, recvd, Scope.EXCHANGE);
            prop.addLabels(BehaviorLabel.TRANSIENT.label());
        }
    }
    
    /**
     * This method handles the 'in' exchange.
     * 
     * @param exch The exchange
     * @param provider The provider
     * @param consumer The consumer
     * @param messageId The message id
     * @param relatesTo The relates-to id
     * @param contentType The content type
     * @param mesg The message
     */
    protected void handleOutExchange(Exchange exch,
            Service provider, ServiceReference consumer, String messageId, String relatesTo,
            String contentType, org.switchyard.Message mesg) {

        Registrant consumerReg=consumer.getServiceMetadata().getRegistrant();
        Registrant providerReg=(provider == null ? null : provider.getServiceMetadata().getRegistrant());

        // Check if interface on associated request needs to be set
        String intf=getInterface(consumer, provider, consumerReg);
        
        SecurityContextManager scm=new SecurityContextManager(exch.getConsumer().getDomain());
        
        SecurityContext securityContext=scm.getContext(exch);

        ExchangeContract contract=exch.getContract();
        
        // Extract service type and operation from the consumer
        // (service reference), as provider is not always available
        QName serviceType=consumer.getName();
        String opName=contract.getConsumerOperation().getName();
        
        // Attempt to retrieve any stored request activity event
        Property rrtw=exch.getContext().getProperty(RTGOV_REQUEST_RECEIVED);
        Property rstw=exch.getContext().getProperty(RTGOV_REQUEST_SENT);
        
        RequestReceived rr=(rrtw == null ? null : (RequestReceived)rrtw.getValue());
        RequestSent rs=(rstw == null ? null : (RequestSent)rstw.getValue());
 
        if (intf != null) {
            if (rr != null) {
                rr.setInterface(intf);
            }
            if (rs != null) {
                rs.setInterface(intf);
            }
        }
        
        // Record the response
        if (providerReg == null
                || !providerReg.isBinding()) {
            ResponseSent sent=new ResponseSent();
                            
            // Only report service type if provider is not a binding
            if (providerReg == null
                    || !providerReg.isBinding()) {
                sent.setServiceType(serviceType.toString()); 
            }

            sent.setInterface(intf);                
            sent.setOperation(opName);
            sent.setMessageId(messageId);
            
            if (consumerReg.isBinding()) {
                String gatewayName=exch.getContext().<String>getPropertyValue(ExchangeCompletionEvent.GATEWAY_NAME);
                
                java.util.List<BindingModel> bindings=consumerReg.<java.util.List<BindingModel>>getConfig();
                
                for (int i=0; gatewayName != null && i < bindings.size(); i++) {
                    BindingModel bm=bindings.get(i);
                    if (gatewayName.equals(bm.getName())) {
                        sent.getProperties().put(GATEWAY_PROPERTY, bm.getType());
                    }
                }
            }
            
            // RTGOV-262 Check if replyTo id not set, due to exception - if so, then
            // use request received id if available
            if (relatesTo == null && rr != null) {
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("Exception seems to have occurred, " +
                    		"so establishing relationship to original request: "+rr.getMessageId());
                }
                relatesTo = rr.getMessageId();
            }
            
            sent.setReplyToId(relatesTo);
            
            record(mesg, contentType, sent, securityContext, exch); 
        }
        
        if (consumerReg.isBinding()) {
            getActivityCollector().endScope();
        } else {
            // Only record the response being received, if the
            // target is a component, not a binding
            ResponseReceived recvd=new ResponseReceived();
            
            recvd.setServiceType(serviceType.toString());                
            recvd.setInterface(intf);                
            recvd.setOperation(opName);
            recvd.setMessageId(messageId);
            recvd.setReplyToId(relatesTo);
            
            if (providerReg != null && providerReg.isBinding()) {
                String gatewayName=exch.getContext().<String>getPropertyValue(ExchangeCompletionEvent.GATEWAY_NAME);
                
                java.util.List<BindingModel> bindings=providerReg.<java.util.List<BindingModel>>getConfig();
                
                for (int i=0; gatewayName != null && i < bindings.size(); i++) {
                    BindingModel bm=bindings.get(i);
                    if (gatewayName.equals(bm.getName())) {
                        recvd.getProperties().put(GATEWAY_PROPERTY, bm.getType());
                    }
                }
            }
            
            record(mesg, contentType, recvd, securityContext, exch); 
        }
    }
    
    /**
     * This method extracts the interface from the exchange details.
     * 
     * @param consumer The exchange consumer
     * @param provider The exchange provider
     * @param consumerReg The consumer registrant
     * @return The interface
     */
    protected String getInterface(ServiceReference consumer, Service provider, Registrant consumerReg) {
        String ret=null;
        ServiceInterface intf=null;
        
        if (consumerReg.isBinding()) {
            intf = consumer.getInterface();
        } else if (provider != null) {
            intf = provider.getInterface();
        }
        
        if (intf != null) {
            if (JavaService.TYPE.equals(intf.getType())) {
                ret = ((JavaService)intf).getJavaInterface().getName();
            } else if (WSDLService.TYPE.equals(intf.getType())) {
                ret = ((WSDLService)intf).getPortType().toString();
            }
        }
        
        return (ret);
    }
    
    /**
     * This method records the supplied information as an activity
     * event.
     * 
     * @param exchange The exchange
     * @param contentType The message content type
     * @param at The activity type
     * @param sc The optional security context
     * @param exch The original exchange event
     */
    protected void record(Message msg, String contentType,
                RPCActivityType at, SecurityContext sc, Exchange exch) {
        if (at != null) {
            if (msg != null) {
                Object content=msg.getContent();
                
                // Check if content type not set
                if (contentType == null) {
                    
                    // Determine whether checked exception (RTGOV-356)
                    if (!(content instanceof org.switchyard.HandlerException)) {
                        contentType = content.getClass().getName();
                        
                        // By default, set the fault to the exception class name
                        // This can always be overridden by the information processor
                        // To make it more readable, if the name ends in Exception, then
                        // remove it
                        String faultName=content.getClass().getSimpleName();
                        
                        if (faultName != null && faultName.endsWith("Exception")) {
                            faultName = faultName.substring(0, faultName.length()-9);
                        }
                        
                        if (LOG.isLoggable(Level.FINEST)) {
                            LOG.finest("Setting fault for type '"+contentType+"' to: "+faultName);
                        }
                        
                        at.setFault(faultName);
                    } else {
                        at.setFault(UNEXPECTED_FAULT);
                    }
                }
                
                if (contentType != null) {
                    at.setContent(getActivityCollector().processInformation(null,
                              contentType, content, new PropertyAccessor(msg.getContext()), at));
                    
                } else if (content != null) {
                    // Assume this is an exception response
                    at.setContent(content.toString());
                }
            }
            
            at.setMessageType(contentType);
            
            // Check if principal has been defined
            if (sc != null && sc.getCredentials().size() > 0) {
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
            
            recordActivity(exch, at);
        }
    }
}

