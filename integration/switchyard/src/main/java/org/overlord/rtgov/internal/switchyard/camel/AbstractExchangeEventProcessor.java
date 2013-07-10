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
package org.overlord.rtgov.internal.switchyard.camel;

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
import org.switchyard.ExchangePhase;
import org.switchyard.Message;
import org.switchyard.Property;
import org.switchyard.Service;
import org.switchyard.ServiceReference;
import org.switchyard.extensions.wsdl.WSDLService;
import org.switchyard.metadata.BaseExchangeContract;
import org.switchyard.metadata.Registrant;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.extensions.java.JavaService;
import org.switchyard.security.SecurityContext;
import org.switchyard.security.credential.Credential;

/**
 * This class provides the abstract Exchange event based implementation of the
 * event processor.
 *
 */
public abstract class AbstractExchangeEventProcessor extends AbstractEventProcessor {
    
    private static final Logger LOG=Logger.getLogger(AbstractExchangeEventProcessor.class.getName());

    /**
     * This is the constructor.
     * 
     * @param eventType The event type associated with the processor
     */
    public AbstractExchangeEventProcessor(Class<? extends EventObject> eventType) {
        super(eventType);       
    }

    /**
     * {@inheritDoc}
     */
    public void handleEvent(EventObject event) {
        org.apache.camel.Exchange exch=
                ((org.apache.camel.management.event.AbstractExchangeEvent)event).getExchange();
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("********* Exchange="+exch);
        }
        
        org.switchyard.bus.camel.CamelMessage mesg=(org.switchyard.bus.camel.CamelMessage)exch.getIn();
        ExchangePhase phase=exch.getProperty("org.switchyard.bus.camel.phase", ExchangePhase.class);        

        if (phase == null) {
            LOG.severe("Could not obtain phase from exchange: "+exch);
            return;
        }

        if (mesg == null) {
            LOG.severe("Could not obtain message for phase ("+phase+") and exchange: "+exch);
            return;
        }
        
        org.switchyard.Context context=new org.switchyard.bus.camel.CamelCompositeContext(exch, mesg);
        
        Service provider=exch.getProperty("org.switchyard.bus.camel.provider", Service.class);
        ServiceReference consumer=exch.getProperty("org.switchyard.bus.camel.consumer", ServiceReference.class);
        
        // TODO: If message is transformed, then should the contentType
        // be updated to reflect the transformed type?
        
        String messageId=null;
        Property mip=context.getProperty("org.switchyard.messageId", org.switchyard.Scope.MESSAGE);
        if (mip != null) {
            messageId = (String)mip.getValue();
        }
        
        String contentType=null;
        Property ctp=context.getProperty("org.switchyard.contentType", org.switchyard.Scope.MESSAGE);
        if (ctp != null) {
            contentType = ((QName)ctp.getValue()).toString();
        }
        
        if (phase == ExchangePhase.IN) {
            handleInExchange(exch, provider, consumer, messageId, contentType, mesg);
            
        } else if (phase == ExchangePhase.OUT) {
            
            if (contentType == null) {
                // Ignore as probably due to exception on handling the request
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("No content type - possibly due to exception on handling the request");
                }
                return;
            }
            
            String relatesTo=null;
            Property rtp=context.getProperty("org.switchyard.relatesTo", org.switchyard.Scope.MESSAGE);
            if (rtp != null) {
                relatesTo = (String)rtp.getValue();
            }
            
            handleOutExchange(exch, provider, consumer, messageId, relatesTo, contentType, mesg);
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
    protected void handleInExchange(org.apache.camel.Exchange exch,
            Service provider, ServiceReference consumer, String messageId,
            String contentType, org.switchyard.bus.camel.CamelMessage mesg) {
        Registrant consumerReg=consumer.getServiceMetadata().getRegistrant();
        Registrant providerReg=(provider == null ? null : provider.getServiceMetadata().getRegistrant());

        String intf=getInterface(consumer, provider, consumerReg);
        
        SecurityContext securityContext=exch.getProperty("org.switchyard.bus.camel.securityContext", SecurityContext.class);

        BaseExchangeContract contract=exch.getProperty("org.switchyard.bus.camel.contract", BaseExchangeContract.class);
        
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
            if (provider == null
                    || !providerReg.isBinding()) {
                sent.setServiceType(serviceType.toString()); 
            }
            
            sent.setInterface(intf);                
            sent.setOperation(opName);
            sent.setMessageId(messageId);
            
            record(mesg, contentType, sent, securityContext, exch); 
            
            if (intf == null) {
                // Save activity event in exchange
                exch.setProperty("rtgov.request.sent", sent);
            }
        }
        
        if (provider == null
                || !providerReg.isBinding()) {
            RequestReceived recvd=new RequestReceived();
            
            recvd.setServiceType(serviceType.toString());                
            recvd.setInterface(intf);                
            recvd.setOperation(opName);
            recvd.setMessageId(messageId);
            
            record(mesg, contentType, recvd, securityContext, exch); 
            
            if (intf == null) {
                // Save activity event in exchange
                exch.setProperty("rtgov.request.received", recvd);
            }               
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
    protected void handleOutExchange(org.apache.camel.Exchange exch,
            Service provider, ServiceReference consumer, String messageId, String relatesTo,
            String contentType, org.switchyard.bus.camel.CamelMessage mesg) {

        Registrant consumerReg=consumer.getServiceMetadata().getRegistrant();
        Registrant providerReg=(provider == null ? null : provider.getServiceMetadata().getRegistrant());

        // Check if interface on associated request needs to be set
        String intf=getInterface(consumer, provider, consumerReg);
        
        SecurityContext securityContext=exch.getProperty("org.switchyard.bus.camel.securityContext", SecurityContext.class);

        BaseExchangeContract contract=exch.getProperty("org.switchyard.bus.camel.contract", BaseExchangeContract.class);
        
        // Extract service type and operation from the consumer
        // (service reference), as provider is not always available
        QName serviceType=consumer.getName();
        String opName=contract.getConsumerOperation().getName();
        
        RequestReceived rr=(RequestReceived)exch.getProperty("rtgov.request.received");
        RequestSent rs=(RequestSent)exch.getProperty("rtgov.request.sent");
        
        if (intf != null) {
            if (rr != null) {
                rr.setInterface(intf);
            }
            if (rs != null) {
                rs.setInterface(intf);
            }
        }
        
        // Record the response
        if (provider == null
                || !providerReg.isBinding()) {
            ResponseSent sent=new ResponseSent();
                            
            // Only report service type if provider is not a binding
            if (provider == null
                    || !providerReg.isBinding()) {
                sent.setServiceType(serviceType.toString()); 
            }

            sent.setInterface(intf);                
            sent.setOperation(opName);
            sent.setMessageId(messageId);
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
                RPCActivityType at, SecurityContext sc, org.apache.camel.Exchange exch) {
        if (at != null) {
            at.setMessageType(contentType);
            
            Object content=msg.getContent();
            
            at.setContent(getActivityCollector().processInformation(null,
                          contentType, content, null, at));
            
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

