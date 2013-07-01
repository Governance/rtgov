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
package org.overlord.rtgov.quickstarts.demos.orders.auditors;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Named;
import javax.xml.transform.dom.DOMSource;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.overlord.rtgov.active.collection.ActiveMap;
import org.overlord.rtgov.jee.CollectionManager;
import org.overlord.rtgov.jee.DefaultCollectionManager;
import org.switchyard.ExchangePhase;
import org.switchyard.Message;
import org.switchyard.Property;
import org.switchyard.bus.camel.audit.Audit;
import org.switchyard.bus.camel.audit.Auditor;
import org.switchyard.bus.camel.processors.Processors;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@Audit({Processors.TRANSFORMATION})
@Named("PolicyEnforcer")
public class PolicyEnforcer implements Auditor {
    
    private static final String PRINCIPALS = "Principals";

    private static final Logger LOG=Logger.getLogger(PolicyEnforcer.class.getName());
    
    private CollectionManager _collectionManager=new DefaultCollectionManager();
    
    private ActiveMap _principals=null;
    
    private boolean _initialized=false;
    
    private static final ObjectMapper MAPPER=new ObjectMapper();

    static {
        SerializationConfig config=MAPPER.getSerializationConfig()
                .withSerializationInclusion(JsonSerialize.Inclusion.NON_NULL)
                .withSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
        
        MAPPER.setSerializationConfig(config);
    }

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

    /**
     * {@inheritDoc}
     */
    public void afterCall(Processors processor, org.apache.camel.Exchange exch) {
    }
    
    /**
     * {@inheritDoc}
     */
    public void beforeCall(Processors processor, org.apache.camel.Exchange exch) {

        ExchangePhase phase=exch.getProperty("org.switchyard.bus.camel.phase", ExchangePhase.class);        

        if (phase != ExchangePhase.IN) {
            return;
        }

        if (!_initialized) {
            init();
        }
        
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("********* Exchange="+exch);
        }
        
        if (_principals != null) {            
            org.switchyard.bus.camel.CamelMessage mesg=(org.switchyard.bus.camel.CamelMessage)exch.getIn();
            
            if (mesg == null) {
                LOG.severe("Could not obtain message for phase ("+phase+") and exchange: "+exch);
                return;
            }

            org.switchyard.Context context=new org.switchyard.bus.camel.CamelCompositeContext(exch, mesg);
            
            java.util.Set<Property> contextProps=context.getProperties(
                    org.switchyard.Scope.MESSAGE);

            Property p=null;
            
            for (Property prop : contextProps) {
                if (prop.getName().equals("org.switchyard.contentType")) {
                    p = prop;
                    break;
                }
            }
            
            if (p != null && p.getValue().toString().equals(
                            "{urn:switchyard-quickstart-demo:orders:1.0}submitOrder")) {

                String customer=getCustomer(mesg);
                       
                if (customer != null) {
                    if (_principals.containsKey(customer)) {
                        
                        @SuppressWarnings("unchecked")
                        java.util.Map<String,java.io.Serializable> props=
                                (java.util.Map<String,java.io.Serializable>)
                                        _principals.get(customer);
                        
                        // Check if customer is suspended
                        if (props.containsKey("suspended")
                                && props.get("suspended").equals(Boolean.TRUE)) {                            
                            throw new RuntimeException("Customer '"+customer
                                            +"' has been suspended");
                        }
                    }
                    
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine("*********** Policy Enforcer: customer '"
                                +customer+"' has not been suspended");
                        LOG.fine("*********** Principal: "+_principals.get(customer));
                    }
                } else {
                    LOG.warning("Unable to find customer name");
                }
            }
        }
    }

    /**
     * This method returns the customer associated with the
     * exchange.
     * 
     * @param msg The message
     * @return The customer
     */
    protected String getCustomer(Message msg) {
        String customer=null;

        Object content=msg.getContent();
        
        if (content instanceof String) {
            String text=(String)content;
            
            int start=text.indexOf("<customer>");
            
            if (start != -1) {
                int end=text.indexOf("</", start);
                
                if (end != -1) {
                    customer=text.substring(start+10, end);
                }
            }
        } else if (content instanceof DOMSource) {
            Node n=((DOMSource)content).getNode();
            
            if (n instanceof Element) {
                org.w3c.dom.NodeList nl=((Element)n).getElementsByTagName("customer");
                
                if (nl.getLength() == 1) {
                    customer = nl.item(0).getFirstChild().getNodeValue();
                } else {
                    LOG.warning("Customer nodes found: "+nl.getLength());
                }
            } else {
                LOG.warning("Not an element");
            }
        } else {
            LOG.warning("Message content type '"+content.getClass()+"' cannot be handled");
        }
        
        return (customer);
    }
    
}
