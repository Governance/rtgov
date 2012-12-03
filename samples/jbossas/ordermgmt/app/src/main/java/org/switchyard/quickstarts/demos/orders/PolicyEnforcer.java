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

import org.overlord.bam.active.collection.ActiveMap;
import org.overlord.bam.activity.util.ActivityUtil;
import org.overlord.bam.switchyard.CollectionManager;
import org.overlord.bam.switchyard.DefaultCollectionManager;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangePhase;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Property;

public class PolicyEnforcer implements ExchangeHandler {
    
    private static final String PRINCIPALS = "Principals";

    private static final Logger LOG=Logger.getLogger(PolicyEnforcer.class.getName());
    
    private CollectionManager _collectionManager=new DefaultCollectionManager();
    
    private ActiveMap _principals=null;
    
    private boolean _initialized=false;
    
    protected void init() {
                
        if (_collectionManager != null) {
            _principals = _collectionManager.getMap(PRINCIPALS);
        }
        
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("*********** Policy Enforcer Initialized with acm="
                        +_collectionManager+" ac="+_principals);
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
        
        if (_principals != null) {            
            Property p = exchange.getContext().getProperty("org.switchyard.contentType",
                    org.switchyard.Scope.valueOf(exchange.getPhase().toString()));
            
            if (p != null && exchange.getPhase() == ExchangePhase.IN
                    && p.getValue().toString().equals(
                            "{urn:switchyard-quickstart-demo:orders:1.0}submitOrder")) {

                String customer=getCustomer(exchange);
                       
                if (customer != null) {
                    if (_principals.containsKey(customer)) {
                        
                        @SuppressWarnings("unchecked")
                        java.util.Map<String,java.io.Serializable> props=
                                (java.util.Map<String,java.io.Serializable>)
                                        _principals.get(customer);
                        
                        // Check if customer is suspended
                        if (props.containsKey("suspended")
                                && props.get("suspended").equals(Boolean.TRUE)) {                            
                            throw new HandlerException("Customer '"+customer
                                    +"' has been suspended");
                        }
                    }
                    
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine("*********** Policy Enforcer: customer '"
                                +customer+"' has not been suspended");
                        LOG.fine("*********** Principal: "+_principals.get(customer));
                    }
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
    }

    /**
     * This method returns the customer associated with the
     * exchange.
     * 
     * @param exchange The exchange
     * @return The customer
     */
    protected String getCustomer(Exchange exchange) {
        String customer=null;

        String content=getMessageContent(exchange);

        int start=content.indexOf("<customer>");
        
        if (start != -1) {
            int end=content.indexOf("</", start);
            
            if (end != -1) {
                customer = content.substring(start+10, end);
            }
        }
        
        return (customer);
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
