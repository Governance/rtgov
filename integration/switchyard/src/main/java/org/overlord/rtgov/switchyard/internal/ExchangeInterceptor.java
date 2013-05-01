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
package org.overlord.rtgov.switchyard.internal;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.xml.namespace.QName;

import org.overlord.rtgov.activity.model.soa.RPCActivityType;
import org.overlord.rtgov.activity.model.soa.RequestReceived;
import org.overlord.rtgov.activity.model.soa.RequestSent;
import org.overlord.rtgov.activity.model.soa.ResponseReceived;
import org.overlord.rtgov.activity.model.soa.ResponseSent;
import org.overlord.rtgov.activity.collector.ActivityCollector;
import org.switchyard.ExchangePhase;
import org.switchyard.Message;
import org.switchyard.Property;
import org.switchyard.Service;
import org.switchyard.ServiceReference;
import org.switchyard.bus.camel.audit.Audit;
import org.switchyard.bus.camel.audit.Auditor;
import org.switchyard.bus.camel.processors.Processors;
import org.switchyard.extensions.wsdl.WSDLService;
import org.switchyard.metadata.BaseExchangeContract;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.metadata.java.JavaService;
import org.switchyard.security.SecurityContext;
import org.switchyard.security.credential.Credential;

/**
 * This class observes exchanges and uses the information to create activity
 * events.
 *
 */
@Audit({Processors.TRANSFORMATION})
@Named("RTGovInterceptor")
public class ExchangeInterceptor implements Auditor {
    
    private static final Logger LOG=Logger.getLogger(ExchangeInterceptor.class.getName());
    
    private static final String ACTIVITY_COLLECTOR = "java:global/overlord-rtgov/ActivityCollector";

    private ActivityCollector _activityCollector=null;
    
    private boolean _initialized=false;
    
    /**
     * This method initializes the auditor.
     */
    @PostConstruct
    protected void init() {
        if (_activityCollector == null) {
            try {
                InitialContext ctx=new InitialContext();
                
                _activityCollector = (ActivityCollector)ctx.lookup(ACTIVITY_COLLECTOR);
                
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Failed to initialize activity collector", e);
            }
        }
        
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("*********** Exchange Interceptor Initialized with collector="+_activityCollector);
        }
        
        _initialized = true;
    }

    /**
     * {@inheritDoc}
     */
    public void afterCall(Processors processor, org.apache.camel.Exchange exch) {
        
        ExchangePhase phase=exch.getProperty("org.switchyard.bus.camel.phase", ExchangePhase.class);        

        if (phase == ExchangePhase.OUT) {
        	handleExchange(exch, phase);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void beforeCall(Processors processor, org.apache.camel.Exchange exch) {

        if (!_initialized) {
            init();
        }
        
        ExchangePhase phase=exch.getProperty("org.switchyard.bus.camel.phase", ExchangePhase.class);        

        if (phase == ExchangePhase.IN) {
        	handleExchange(exch, phase);
        }
    }
    
    /**
     * This method handles the exchange.
     * 
     * @param exch The exchange
     * @param phase The phase
     */
    protected void handleExchange(org.apache.camel.Exchange exch, ExchangePhase phase) {
        org.switchyard.bus.camel.CamelMessage mesg=(org.switchyard.bus.camel.CamelMessage)exch.getIn();
        
        if (mesg == null) {
        	LOG.severe("Could not obtain message for phase ("+phase+") and exchange: "+exch);
        	return;
        }
        
        org.switchyard.Context context=new org.switchyard.bus.camel.CamelCompositeContext(exch, mesg);
        
        Service provider=exch.getProperty("org.switchyard.bus.camel.provider", Service.class);
        ServiceReference consumer=exch.getProperty("org.switchyard.bus.camel.consumer", ServiceReference.class);
        
        SecurityContext securityContext=exch.getProperty("org.switchyard.bus.camel.securityContext", SecurityContext.class);

        BaseExchangeContract contract=exch.getProperty("org.switchyard.bus.camel.contract", BaseExchangeContract.class);
        
        if (provider == null
                && LOG.isLoggable(Level.FINEST)) {
            LOG.finest("No provider specified - probably an exception: "
                        +mesg.getContent());
        }
        
        if (_activityCollector != null) {
            
            // TODO: If message is transformed, then should the contentType
            // be updated to reflect the transformed type?
            
            String messageId=null;
            String relatesTo=null;
            String contentType=null;
            
            java.util.Set<Property> props=context.getProperties(
                    org.switchyard.Scope.MESSAGE);
            
            for (Property p : props) {
                
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
            QName serviceType=consumer.getName();
            String opName=contract.getConsumerOperation().getName();
            
            if (phase == ExchangePhase.IN) {
                if (consumer.getConsumerMetadata().isBinding()) {
                    _activityCollector.startScope();
                } else {
                    // Only record the request being sent, if the
                    // source is a component, not a binding
                
                    RequestSent sent=new RequestSent();
                    
                    // Only report service type if provider is not a binding
                    if (provider == null
                            || !provider.getProviderMetadata().isBinding()) {
                    	sent.setServiceType(serviceType.toString()); 
                    }
                    
                    sent.setInterface(getInterface(consumer, provider));                
                    sent.setOperation(opName);
                    sent.setMessageId(messageId);
                    
                    record(mesg, contentType, sent, securityContext); 
                }
                
                if (provider == null
                        || !provider.getProviderMetadata().isBinding()) {
                    RequestReceived recvd=new RequestReceived();
                    
                    recvd.setServiceType(serviceType.toString());                
                    recvd.setInterface(getInterface(consumer, provider));                
                    recvd.setOperation(opName);
                    recvd.setMessageId(messageId);
                    
                    record(mesg, contentType, recvd, securityContext); 
                }
                
            } else if (phase == ExchangePhase.OUT) {
            	
            	if (contentType == null) {
            		// Ignore as probably due to exception on handling the request
            		if (LOG.isLoggable(Level.FINEST)) {
            			LOG.finest("No content type - possibly due to exception on handling the request");
            		}
            		return;
            	}
            	
                if (provider == null
                        || !provider.getProviderMetadata().isBinding()) {
                    ResponseSent sent=new ResponseSent();
                                    
                    // Only report service type if provider is not a binding
                    if (provider == null
                            || !provider.getProviderMetadata().isBinding()) {
                    	sent.setServiceType(serviceType.toString()); 
                    }

                    sent.setInterface(getInterface(consumer, provider));                
                    sent.setOperation(opName);
                    sent.setMessageId(messageId);
                    sent.setReplyToId(relatesTo);
                    
                    record(mesg, contentType, sent, securityContext); 
                }
                
                if (consumer.getConsumerMetadata().isBinding()) {
                    _activityCollector.endScope();
                } else {
                    // Only record the response being received, if the
                    // target is a component, not a binding
                    ResponseReceived recvd=new ResponseReceived();
                    
                    recvd.setServiceType(serviceType.toString());                
                    recvd.setInterface(getInterface(consumer, provider));                
                    recvd.setOperation(opName);
                    recvd.setMessageId(messageId);
                    recvd.setReplyToId(relatesTo);
                    
                    record(mesg, contentType, recvd, securityContext); 
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
    	
    	if (consumer.getConsumerMetadata().isBinding()) {
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
     * This method records the supplied information as an activity
     * event.
     * 
     * @param exchange The exchange
     * @param contentType The message content type
     * @param at The activity type
     * @param sc The optional security context
     */
    protected void record(Message msg, String contentType,
                RPCActivityType at, SecurityContext sc) {
        if (at != null) {
            at.setMessageType(contentType);
            
            Object content=msg.getContent();
            
            at.setContent(_activityCollector.processInformation(null,
                          contentType, content, null, at));
            
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
            	_activityCollector.record(at);
            } catch (Exception e) {
            	// Strip the exception and just return the message
            	throw new org.switchyard.exception.SwitchYardException(e.getMessage());
            }
        }
    }

}
