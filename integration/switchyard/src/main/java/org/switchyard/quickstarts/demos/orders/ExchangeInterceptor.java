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
package org.switchyard.quickstarts.demos.orders;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.xml.namespace.QName;

import org.overlord.bam.activity.model.soa.RPCActivityType;
import org.overlord.bam.activity.model.soa.RequestReceived;
import org.overlord.bam.activity.model.soa.RequestSent;
import org.overlord.bam.activity.model.soa.ResponseReceived;
import org.overlord.bam.activity.model.soa.ResponseSent;
import org.overlord.bam.activity.util.ActivityUtil;
import org.overlord.bam.activity.collector.ActivityCollector;
import org.switchyard.Exchange;
import org.switchyard.ExchangePhase;
import org.switchyard.Message;
import org.switchyard.Property;
import org.switchyard.bus.camel.audit.Audit;
import org.switchyard.bus.camel.audit.Auditor;
import org.switchyard.bus.camel.processors.Processors;

/**
 * This class observes exchanges and uses the information to create activity
 * events.
 *
 */
@Audit({Processors.TRANSFORMATION})
@Named("BAMInterceptor")
public class ExchangeInterceptor implements Auditor {
    
    private static final Logger LOG=Logger.getLogger(ExchangeInterceptor.class.getName());
    
    private static final String ACTIVITY_COLLECTOR = "java:global/overlord-bam/ActivityCollector";

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
                
            } catch(Exception e) {
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
    }

    /**
     * {@inheritDoc}
     */
    public void beforeCall(Processors processor, org.apache.camel.Exchange exch) {

        if (!_initialized) {
            init();
        }
        
        // Obtain switchyard exchange
        org.switchyard.Exchange exchange =
                exch.getProperty(org.switchyard.bus.camel.ExchangeDispatcher.SY_EXCHANGE,
                        org.switchyard.Exchange.class);
        
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("********* Exchange="+exchange);
        }
        
        if (exchange == null) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Returning as not a switchyard exchange");
            }
            return;
        }
        
        if (exchange.getProvider() == null) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Unable to obtain activity information from exchange, " +
            		"as no provider - probably an exception: "+exchange.getMessage().getContent());
            }
            return;
        }
        
        if (_activityCollector != null) {
            
            // TODO: If message is transformed, then should the contentType
            // be updated to reflect the transformed type?
            
            String messageId=null;
            String relatesTo=null;
            String contentType=null;
            
            for (Property p : exchange.getContext().getProperties(
                    org.switchyard.Scope.valueOf(exchange.getPhase().toString()))) {
                if (p.getName().equals("org.switchyard.messageId")) {
                    messageId = (String)p.getValue();
                } else if (p.getName().equals("org.switchyard.relatesTo")) {
                    relatesTo = (String)p.getValue();
                } else if (p.getName().equals("org.switchyard.contentType")) {
                    contentType = ((QName)p.getValue()).toString();
                }
            }
            
            // Extract service type and operation from the consumer
            // (service reference), as provider is not available until
            // request handled
            QName serviceType=exchange.getConsumer().getName();
            String opName=exchange.getContract().getConsumerOperation().getName();
            
            if (exchange.getPhase() == ExchangePhase.IN) {
                if (exchange.getConsumer().getConsumerMetadata().isBinding()) {
                    _activityCollector.startScope();
                } else {
                    // Only record the request being sent, if the
                    // source is a component, not a binding
                
                    RequestSent sent=new RequestSent();
                    
                    sent.setServiceType(serviceType.toString());   
                    sent.setOperation(opName);
                    sent.setMessageId(messageId);
                    
                    record(exchange, contentType, sent); 
                }
                
                if (!exchange.getProvider().getProviderMetadata().isBinding()) {
                    RequestReceived recvd=new RequestReceived();
                    
                    recvd.setServiceType(serviceType.toString());                
                    recvd.setOperation(opName);
                    recvd.setMessageId(messageId);
                    
                    record(exchange, contentType, recvd); 
                }
                
            } else if (exchange.getPhase() == ExchangePhase.OUT) {
                if (!exchange.getProvider().getProviderMetadata().isBinding()) {
                    ResponseSent sent=new ResponseSent();
                                    
                    sent.setServiceType(serviceType.toString());                
                    sent.setOperation(opName);
                    sent.setMessageId(messageId);
                    sent.setReplyToId(relatesTo);
                    
                    record(exchange, contentType, sent); 
                }
                
                if (exchange.getConsumer().getConsumerMetadata().isBinding()) {
                    _activityCollector.endScope();
                } else {
                    // Only record the response being received, if the
                    // target is a component, not a binding
                    ResponseReceived recvd=new ResponseReceived();
                    
                    recvd.setServiceType(serviceType.toString());                
                    recvd.setOperation(opName);
                    recvd.setMessageId(messageId);
                    recvd.setReplyToId(relatesTo);
                    
                    record(exchange, contentType, recvd); 
                }
            }
        }
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
            
            at.setContent(_activityCollector.processInformation(null,
                          contentType, msg.getContent(), at));
            
            // Check if content has been set
            if (at.getContent() == null) {
                at.setContent(getMessageContent(exchange));
            }
            
            _activityCollector.record(at);
        }
    }

    /**
     * This method returns a string representation of the
     * message content, or null if no available.
     * 
     * @param exchange The exchange
     * @return The string representation, or null if not possible
     */
    protected String getMessageContent(Exchange exchange) {
        String ret=null;
        
        // try to convert the payload to a string
        Message msg = exchange.getMessage();

        try {    
            ret = msg.getContent(String.class);
            
            // check to see if we have to put content back into the message 
            // after the conversion to string
            if (java.io.InputStream.class.isAssignableFrom(msg.getContent().getClass())) {
                msg.setContent(new java.io.ByteArrayInputStream(ret.getBytes()));
            } else if (java.io.Reader.class.isAssignableFrom(msg.getContent().getClass())) {
                msg.setContent(new java.io.StringReader(ret));
            }

        } catch (Exception ex) {
            try {
                // If contents cannot be represented as a string, then try a
                // JSON serialized form
                ret = ActivityUtil.objectToJSONString(msg.getContent());
            } catch (Exception ex2) {
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("Failed to convert message content for '"+exchange
                            +"' to string: ex="+ex+" ex2="+ex2);
                }
            }
        }

        return(ret);
    }
}
