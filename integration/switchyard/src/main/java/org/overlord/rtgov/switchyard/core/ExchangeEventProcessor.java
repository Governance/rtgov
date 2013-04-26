/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-13, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.rtgov.switchyard.core;

import java.util.EventObject;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.overlord.rtgov.activity.model.soa.RPCActivityType;
import org.overlord.rtgov.activity.model.soa.RequestReceived;
import org.overlord.rtgov.activity.model.soa.RequestSent;
import org.overlord.rtgov.activity.model.soa.ResponseReceived;
import org.overlord.rtgov.activity.model.soa.ResponseSent;
import org.overlord.rtgov.switchyard.AbstractEventProcessor;
import org.switchyard.Exchange;
import org.switchyard.ExchangePhase;
import org.switchyard.Message;
import org.switchyard.Property;
import org.switchyard.extensions.wsdl.WSDLService;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.metadata.java.JavaService;
import org.switchyard.security.credential.Credential;

/**
 * This class provides the BPEL component implementation of the
 * event processor.
 *
 */
public class ExchangeEventProcessor extends AbstractEventProcessor {
	
	private static final Logger LOG=Logger.getLogger(ExchangeEventProcessor.class.getName());

	/**
	 * This is the default constructor.
	 */
	public ExchangeEventProcessor() {
		super(org.switchyard.runtime.event.ExchangeCompletionEvent.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void notify(EventObject event) {
		org.switchyard.Exchange exchange=
				((org.switchyard.runtime.event.ExchangeCompletionEvent)event).getExchange();
		
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("********* Exchange="+exchange);
        }
        
        if (exchange == null) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Returning as not a switchyard exchange");
            }
            return;
        }
        
        if (exchange.getProvider() == null
                && LOG.isLoggable(Level.FINEST)) {
            LOG.finest("No provider specified - probably an exception: "
                        +exchange.getMessage().getContent());
        }
        
        // TODO: If message is transformed, then should the contentType
        // be updated to reflect the transformed type?
        
        String messageId=null;
        String relatesTo=null;
        String contentType=null;
        
        for (Property p : exchange.getContext().getProperties(
                org.switchyard.Scope.valueOf(exchange.getPhase().toString()))) {
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Switchyard property: name="+p.getName()+" value="+p.getValue());
            }
            
            if (p.getName().equals("org.switchyard.messageId")) {
                messageId = (String)p.getValue();
            } else if (p.getName().equals("org.switchyard.relatesTo")) {
                relatesTo = (String)p.getValue();
            } else if (p.getName().equals("org.switchyard.contentType")) {
                contentType = ((QName)p.getValue()).toString();
            }
        }
        
        // Extract service type and operation from the consumer
        // (service reference), as provider is not always available
        QName serviceType=exchange.getConsumer().getName();
        String opName=exchange.getContract().getConsumerOperation().getName();
        
        if (exchange.getPhase() == ExchangePhase.IN) {
            if (exchange.getConsumer().getConsumerMetadata().isBinding()) {
                getActivityCollector().startScope();
            } else {
                // Only record the request being sent, if the
                // source is a component, not a binding
            
                RequestSent sent=new RequestSent();
                
                // Only report service type if provider is not a binding
                if (exchange.getProvider() == null
                        || !exchange.getProvider().getProviderMetadata().isBinding()) {
                	sent.setServiceType(serviceType.toString()); 
                }
                
                sent.setInterface(getInterface(exchange));                
                sent.setOperation(opName);
                sent.setMessageId(messageId);
                
                record(exchange, contentType, sent); 
            }
            
            if (exchange.getProvider() == null
                    || !exchange.getProvider().getProviderMetadata().isBinding()) {
                RequestReceived recvd=new RequestReceived();
                
                recvd.setServiceType(serviceType.toString());                
                recvd.setInterface(getInterface(exchange));                
                recvd.setOperation(opName);
                recvd.setMessageId(messageId);
                
                record(exchange, contentType, recvd); 
            }
            
        } else if (exchange.getPhase() == ExchangePhase.OUT) {
            if (exchange.getProvider() == null
                    || !exchange.getProvider().getProviderMetadata().isBinding()) {
                ResponseSent sent=new ResponseSent();
                                
                // Only report service type if provider is not a binding
                if (exchange.getProvider() == null
                        || !exchange.getProvider().getProviderMetadata().isBinding()) {
                	sent.setServiceType(serviceType.toString()); 
                }

                sent.setInterface(getInterface(exchange));                
                sent.setOperation(opName);
                sent.setMessageId(messageId);
                sent.setReplyToId(relatesTo);
                
                record(exchange, contentType, sent); 
            }
            
            if (exchange.getConsumer().getConsumerMetadata().isBinding()) {
            	getActivityCollector().endScope();
            } else {
                // Only record the response being received, if the
                // target is a component, not a binding
                ResponseReceived recvd=new ResponseReceived();
                
                recvd.setServiceType(serviceType.toString());                
                recvd.setInterface(getInterface(exchange));                
                recvd.setOperation(opName);
                recvd.setMessageId(messageId);
                recvd.setReplyToId(relatesTo);
                
                record(exchange, contentType, recvd); 
            }
        }
    }
    
    /**
     * This method extracts the interface from the exchange details.
     * 
     * @param exchange The exchange
     * @return The interface
     */
    protected String getInterface(Exchange exchange) {
    	String ret=null;
    	ServiceInterface intf=null;
    	
    	if (exchange.getConsumer().getConsumerMetadata().isBinding()) {
    		intf = exchange.getConsumer().getInterface();
    	} else {
    		intf = exchange.getProvider().getInterface();
    	}
    	
    	if (JavaService.TYPE.equals(intf.getType())) {
    		ret = ((JavaService)intf).getJavaInterface().getName();
    	} else if (WSDLService.TYPE.equals(intf.getType())) {
    		// TODO: Can remove this guard once switchyard 1.0 is released
    		try {
    			ret = ((WSDLService)intf).getPortType().toString();
    		} catch (Throwable t) {
    			LOG.log(Level.SEVERE, "Older version of switchyard. Require 1.0 or older", t);
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
     */
    protected void record(Exchange exchange, String contentType,
                RPCActivityType at) {
        if (at != null) {
            at.setMessageType(contentType);
            
            Message msg = exchange.getMessage();
            
            at.setContent(getActivityCollector().processInformation(null,
                          contentType, msg.getContent(), null, at));
            
            // Check if principal has been defined
            org.switchyard.security.SecurityContext sc=org.switchyard.security.SecurityContext.get(exchange);
            
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
            	getActivityCollector().record(at);
            } catch (Exception e) {
            	// Strip the exception and just return the message
            	throw new org.switchyard.exception.SwitchYardException(e.getMessage());
            }
        }
    }

}
