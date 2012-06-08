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
import javax.naming.InitialContext;
import javax.xml.namespace.QName;

import org.savara.bam.activity.model.soa.RequestReceived;
import org.savara.bam.activity.model.soa.RequestSent;
import org.savara.bam.activity.model.soa.ResponseReceived;
import org.savara.bam.activity.model.soa.ResponseSent;
import org.savara.bam.activity.util.ActivityUtil;
import org.savara.bam.collector.ActivityCollector;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangePhase;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Property;

public class ExchangeInterceptor implements ExchangeHandler {
    
    private static final String START_SCOPE = "org.overlord.bam.activity.collector.startScope";

    private static final Logger LOG=Logger.getLogger(ExchangeInterceptor.class.getName());
    
    private static final String ACTIVITY_COLLECTOR = "java:global/overlord-bam/ActivityCollector";

    private ActivityCollector _activityCollector=null;
    
    private boolean _initialized=false;
    
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

    public void handleMessage(Exchange exchange) throws HandlerException {
        if (!_initialized) {
            init();
        }
        
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("********* Exchange="+exchange);
        }
        
        if (_activityCollector != null) {
            
            // TODO: If message is transformed, then should the contentType
            // be updated to reflect the transformed type?
            
            String content=getMessageContent(exchange);
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
            
            if (exchange.getPhase() == ExchangePhase.IN) {
                
                if (_activityCollector.startScope()) {
                    exchange.getContext().setProperty(START_SCOPE, Boolean.TRUE);
                }
                
                RequestSent sent=new RequestSent();
                
                sent.setServiceType(exchange.getServiceName().toString());   
                sent.setOperation(exchange.getContract().getServiceOperation().getName());
                sent.setContent(content);
                sent.setMessageType(contentType);
                sent.setMessageId(messageId);
                
                _activityCollector.record(sent);
                
                RequestReceived recvd=new RequestReceived();
                
                recvd.setServiceType(exchange.getServiceName().toString());                
                recvd.setOperation(exchange.getContract().getServiceOperation().getName());
                recvd.setContent(content);
                recvd.setMessageType(contentType);
                recvd.setMessageId(messageId);
                
                _activityCollector.record(recvd);
                
            } else if (exchange.getPhase() == ExchangePhase.OUT) {
                ResponseSent sent=new ResponseSent();
                                
                sent.setServiceType(exchange.getServiceName().toString());                
                sent.setOperation(exchange.getContract().getServiceOperation().getName());
                sent.setContent(content);
                sent.setMessageType(contentType);
                sent.setMessageId(messageId);
                sent.setReplyToId(relatesTo);
                
                _activityCollector.record(sent);
                
                ResponseReceived recvd=new ResponseReceived();
                
                recvd.setServiceType(exchange.getServiceName().toString());                
                recvd.setOperation(exchange.getContract().getServiceOperation().getName());
                recvd.setContent(content);
                recvd.setMessageType(contentType);
                recvd.setMessageId(messageId);
                recvd.setReplyToId(relatesTo);
                
                _activityCollector.record(recvd);
                
                if (exchange.getContext().getProperty(START_SCOPE) != null) {
                    _activityCollector.endScope();
                }

            }
        }
    }

    public void handleFault(Exchange exchange) {
        if (!_initialized) {
            init();
        }
        
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("********* Fault="+exchange);
        }
        
        // TODO: Handle faults
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
