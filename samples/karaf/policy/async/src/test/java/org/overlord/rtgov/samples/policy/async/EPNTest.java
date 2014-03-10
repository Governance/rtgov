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
package org.overlord.rtgov.samples.policy.async;

import static org.junit.Assert.*;

import org.infinispan.manager.CacheContainer;
import org.junit.Test;
import org.overlord.rtgov.activity.model.soa.RequestReceived;
import org.overlord.rtgov.activity.model.soa.RequestSent;
import org.overlord.rtgov.activity.model.soa.ResponseReceived;
import org.overlord.rtgov.activity.model.soa.ResponseSent;
import org.overlord.rtgov.common.infinispan.InfinispanManager;
import org.overlord.rtgov.epn.Network;
import org.overlord.rtgov.epn.embedded.EmbeddedEPNManager;
import org.overlord.rtgov.epn.util.NetworkUtil;

public class EPNTest {

    @Test
    public void testSuspendCustomer() {
        EmbeddedEPNManager epnm=new EmbeddedEPNManager();
        
        // Load network
        Network network=null;
        
        try {
            java.io.InputStream is=ClassLoader.getSystemResourceAsStream("epn.json");
            
            byte[] b=new byte[is.available()];
            is.read(b);
            
            is.close();
            
            network = NetworkUtil.deserialize(b);
            
            epnm.register(network);
        } catch (Exception e) {
            fail("Failed to register network: "+e);
        }
        
        // Obtain Principals cache
        java.util.Map<Object,Object> cache=null;
        
        try {
            CacheContainer cc=InfinispanManager.getCacheContainer(null);
            
            cache = cc.getCache("Principals");
            
            cache.clear();
            
        } catch (Exception e) {
            fail("Failed to get default cache container: "+e);
        }
        
        java.util.List<java.io.Serializable> events=
                    new java.util.ArrayList<java.io.Serializable>();
        
        RequestSent rqs=new RequestSent();
        rqs.setOperation("submitOrder");
        rqs.setServiceType("org.overlord.rtgov.quickstarts.demos.ordermgmt.orderservice.OrderServiceBean");
        
        RequestReceived rqr=new RequestReceived();
        rqr.setOperation("submitOrder");
        rqr.setServiceType("org.overlord.rtgov.quickstarts.demos.ordermgmt.orderservice.OrderServiceBean");
        
        ResponseSent rps=new ResponseSent();
        rps.setOperation("submitOrder");
        rps.setServiceType("org.overlord.rtgov.quickstarts.demos.ordermgmt.orderservice.OrderServiceBean");
        
        rps.getProperties().put("total", "100");
        rps.getProperties().put("customer", "Fred");
        
        ResponseReceived rpr=new ResponseReceived();
        rpr.setOperation("submitOrder");
        rpr.setServiceType("org.overlord.rtgov.quickstarts.demos.ordermgmt.orderservice.OrderServiceBean");
        
        events.add(rqs);
        events.add(rqr);
        events.add(rps);
        events.add(rpr);
        
        try {
            epnm.publish("SOAEvents", events);
            
            synchronized (this) {
                wait(1000);
            }
        } catch (Exception e) {
            fail("Failed to publish events: "+e);
        }
        
        if (!cache.containsKey("Fred")) {
            fail("Principal is not Fred: "+cache);
        }
        
        @SuppressWarnings("unchecked")
        java.util.Map<String,Object> props1=(java.util.Map<String,Object>)cache.get("Fred");
        
        if (props1.containsKey("suspended")) {
            fail("Fred should not have a 'suspended' property yet: "+props1.get("suspended"));
        }
        
        // Make second purchase that takes the customer above the level
        events=new java.util.ArrayList<java.io.Serializable>();
    
        rqs=new RequestSent();
        rqs.setOperation("submitOrder");
        rqs.setServiceType("org.overlord.rtgov.quickstarts.demos.ordermgmt.orderservice.OrderServiceBean");
        
        rqr=new RequestReceived();
        rqr.setOperation("submitOrder");
        rqr.setServiceType("org.overlord.rtgov.quickstarts.demos.ordermgmt.orderservice.OrderServiceBean");
        
        rps=new ResponseSent();
        rps.setOperation("submitOrder");
        rps.setServiceType("org.overlord.rtgov.quickstarts.demos.ordermgmt.orderservice.OrderServiceBean");
        
        rps.getProperties().put("total", "100");
        rps.getProperties().put("customer", "Fred");
        
        rpr=new ResponseReceived();
        rpr.setOperation("submitOrder");
        rpr.setServiceType("org.overlord.rtgov.quickstarts.demos.ordermgmt.orderservice.OrderServiceBean");
        
        events.add(rqs);
        events.add(rqr);
        events.add(rps);
        events.add(rpr);
        
        try {
            epnm.publish("SOAEvents", events);
            
            synchronized (this) {
                wait(1000);
            }
        } catch (Exception e) {
            fail("Failed to publish events: "+e);
        }
        
        if (!cache.containsKey("Fred")) {
            fail("Principal is not Fred: "+cache);
        }
        
        @SuppressWarnings("unchecked")
        java.util.Map<String,Object> props2 = (java.util.Map<String,Object>)cache.get("Fred");
        
        if (props2.get("suspended") != Boolean.TRUE) {
            fail("Fred is not suspended");
        }
    }

    @Test
    public void testCustomerIsolation() {
        EmbeddedEPNManager epnm=new EmbeddedEPNManager();
        
        // Load network
        try {
            java.io.InputStream is=ClassLoader.getSystemResourceAsStream("epn.json");
            
            byte[] b=new byte[is.available()];
            is.read(b);
            
            is.close();
            
            Network network=NetworkUtil.deserialize(b);
            
            epnm.register(network);
        } catch (Exception e) {
            fail("Failed to register network: "+e);
        }
        
        // Obtain Principals cache
        java.util.Map<Object,Object> cache=null;
        
        try {
            CacheContainer cc=InfinispanManager.getCacheContainer(null);
            
            cache = cc.getCache("Principals");
            
            cache.clear();
            
        } catch (Exception e) {
            fail("Failed to get default cache container: "+e);
        }
        
        java.util.List<java.io.Serializable> events=
                    new java.util.ArrayList<java.io.Serializable>();
        
        RequestSent rqs=new RequestSent();
        rqs.setOperation("submitOrder");
        rqs.setServiceType("org.overlord.rtgov.quickstarts.demos.ordermgmt.orderservice.OrderServiceBean");
        
        RequestReceived rqr=new RequestReceived();
        rqr.setOperation("submitOrder");
        rqr.setServiceType("org.overlord.rtgov.quickstarts.demos.ordermgmt.orderservice.OrderServiceBean");
        
        ResponseSent rps=new ResponseSent();
        rps.setOperation("submitOrder");
        rps.setServiceType("org.overlord.rtgov.quickstarts.demos.ordermgmt.orderservice.OrderServiceBean");
        
        rps.getProperties().put("total", "100");
        rps.getProperties().put("customer", "Fred");
        
        ResponseReceived rpr=new ResponseReceived();
        rpr.setOperation("submitOrder");
        rpr.setServiceType("org.overlord.rtgov.quickstarts.demos.ordermgmt.orderservice.OrderServiceBean");
        
        events.add(rqs);
        events.add(rqr);
        events.add(rps);
        events.add(rpr);
        
        try {
            epnm.publish("SOAEvents", events);
            
            synchronized (this) {
                wait(1000);
            }
        } catch (Exception e) {
            fail("Failed to publish events: "+e);
        }
        
        if (!cache.containsKey("Fred")) {
            fail("Principal is not Fred: "+cache);
        }
        
        @SuppressWarnings("unchecked")
        java.util.Map<String,Object> props1=(java.util.Map<String,Object>)cache.get("Fred");
        
        if (props1.containsKey("suspended")) {
            fail("Fred should not have a 'suspended' property yet: "+props1.get("suspended"));
        }
        
        // Make second purchase but for different customer
        events=new java.util.ArrayList<java.io.Serializable>();
    
        rqs=new RequestSent();
        rqs.setOperation("submitOrder");
        rqs.setServiceType("org.overlord.rtgov.quickstarts.demos.ordermgmt.orderservice.OrderServiceBean");
        
        rqr=new RequestReceived();
        rqr.setOperation("submitOrder");
        rqr.setServiceType("org.overlord.rtgov.quickstarts.demos.ordermgmt.orderservice.OrderServiceBean");
        
        rps=new ResponseSent();
        rps.setOperation("submitOrder");
        rps.setServiceType("org.overlord.rtgov.quickstarts.demos.ordermgmt.orderservice.OrderServiceBean");
        
        rps.getProperties().put("total", "100");
        rps.getProperties().put("customer", "Joe");
        
        rpr=new ResponseReceived();
        rpr.setOperation("submitOrder");
        rpr.setServiceType("org.overlord.rtgov.quickstarts.demos.ordermgmt.orderservice.OrderServiceBean");
         
        events.add(rqs);
        events.add(rqr);
        events.add(rps);
        events.add(rpr);
        
        try {
            epnm.publish("SOAEvents", events);
            
            synchronized (this) {
                wait(1000);
            }
        } catch (Exception e) {
            fail("Failed to publish events: "+e);
        }
        
        if (!cache.containsKey("Joe")) {
            fail("Principal is not Joe: "+cache);
        }
        
        @SuppressWarnings("unchecked")
        java.util.Map<String,Object> props2=(java.util.Map<String,Object>)cache.get("Joe");
        
        if (props2.containsKey("suspended")) {
            fail("Joe should not have a 'suspended' property yet: "+props2.get("suspended"));
        }
        
    }

    @Test
    public void testUnsuspendCustomer() {
        EmbeddedEPNManager epnm=new EmbeddedEPNManager();
        
        // Load network
        try {
            java.io.InputStream is=ClassLoader.getSystemResourceAsStream("epn.json");
            
            byte[] b=new byte[is.available()];
            is.read(b);
            
            is.close();
            
            Network network=NetworkUtil.deserialize(b);
            
            epnm.register(network);
        } catch (Exception e) {
            fail("Failed to register network: "+e);
        }
        
        // Obtain Principals cache
        java.util.Map<Object,Object> cache=null;
        
        try {
            CacheContainer cc=InfinispanManager.getCacheContainer(null);
            
            cache = cc.getCache("Principals");
            
            cache.clear();
            
        } catch (Exception e) {
            fail("Failed to get default cache container: "+e);
        }
        
        java.util.List<java.io.Serializable> events=
                    new java.util.ArrayList<java.io.Serializable>();
        
        RequestSent rqs=new RequestSent();
        rqs.setOperation("submitOrder");
        rqs.setServiceType("org.overlord.rtgov.quickstarts.demos.ordermgmt.orderservice.OrderServiceBean");
        
        RequestReceived rqr=new RequestReceived();
        rqr.setOperation("submitOrder");
        rqr.setServiceType("org.overlord.rtgov.quickstarts.demos.ordermgmt.orderservice.OrderServiceBean");
        
        ResponseSent rps=new ResponseSent();
        rps.setOperation("submitOrder");
        rps.setServiceType("org.overlord.rtgov.quickstarts.demos.ordermgmt.orderservice.OrderServiceBean");
        
        rps.getProperties().put("total", "200");
        rps.getProperties().put("customer", "Fred");
        
        ResponseReceived rpr=new ResponseReceived();
        rpr.setOperation("submitOrder");
        rpr.setServiceType("org.overlord.rtgov.quickstarts.demos.ordermgmt.orderservice.OrderServiceBean");
        
        events.add(rqs);
        events.add(rqr);
        events.add(rps);
        events.add(rpr);
        
        try {
            epnm.publish("SOAEvents", events);
            
            synchronized (this) {
                wait(1000);
            }
        } catch (Exception e) {
            fail("Failed to publish events: "+e);
        }
        
        if (!cache.containsKey("Fred")) {
            fail("Principal is not Fred: "+cache);
        }
        
        @SuppressWarnings("unchecked")
        java.util.Map<String,Object> props1=(java.util.Map<String,Object>)cache.get("Fred");
        
        if (!props1.containsKey("suspended")) {
            fail("Fred should have a 'suspended' property");
        }
        
        if (props1.get("suspended") != Boolean.TRUE) {
            fail("Fred should be suspended");
        }
        
        // Make second purchase that takes the customer above the level
        events=new java.util.ArrayList<java.io.Serializable>();
    
        rqs=new RequestSent();
        rqs.setOperation("makePayment");
        rqs.setServiceType("org.overlord.rtgov.quickstarts.demos.ordermgmt.orderservice.OrderServiceBean");
        
        rqr=new RequestReceived();
        rqr.setOperation("makePayment");
        rqr.setServiceType("org.overlord.rtgov.quickstarts.demos.ordermgmt.orderservice.OrderServiceBean");
        
        rps=new ResponseSent();
        rps.setOperation("makePayment");
        rps.setServiceType("org.overlord.rtgov.quickstarts.demos.ordermgmt.orderservice.OrderServiceBean");
        
        rps.getProperties().put("total", "170");
        rps.getProperties().put("customer", "Fred");
        
        rpr=new ResponseReceived();
        rpr.setOperation("makePayment");
        rpr.setServiceType("org.overlord.rtgov.quickstarts.demos.ordermgmt.orderservice.OrderServiceBean");
        
        events.add(rqs);
        events.add(rqr);
        events.add(rps);
        events.add(rpr);
        
        try {
            epnm.publish("SOAEvents", events);
            
            synchronized (this) {
                wait(1000);
            }
        } catch (Exception e) {
            fail("Failed to publish events: "+e);
        }
        
        if (!cache.containsKey("Fred")) {
            fail("Principal is not Fred: "+cache);
        }
        
        @SuppressWarnings("unchecked")
        java.util.Map<String,Object> props2=(java.util.Map<String,Object>)cache.get("Fred");
        
        if (!props2.containsKey("suspended")) {
            fail("Fred should have a 'suspended' property");
        }
        
        if (props2.get("suspended") != Boolean.FALSE) {
            fail("Fred should be unsuspended");
        }
    }
}
