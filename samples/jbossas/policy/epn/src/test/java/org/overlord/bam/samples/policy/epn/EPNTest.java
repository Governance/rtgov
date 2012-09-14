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
package org.overlord.bam.samples.policy.epn;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.bam.activity.model.soa.RequestReceived;
import org.overlord.bam.activity.model.soa.RequestSent;
import org.overlord.bam.activity.model.soa.ResponseReceived;
import org.overlord.bam.activity.model.soa.ResponseSent;
import org.overlord.bam.epn.EventList;
import org.overlord.bam.epn.Network;
import org.overlord.bam.epn.NotificationListener;
import org.overlord.bam.epn.NotificationType;
import org.overlord.bam.epn.embedded.EmbeddedEPNManager;
import org.overlord.bam.epn.util.NetworkUtil;

public class EPNTest {

    @Test
    public void testSuspendCustomer() {
        EmbeddedEPNManager epnm=new EmbeddedEPNManager();
        
        TestNotificationListener l=new TestNotificationListener();
        
        epnm.addNotificationListener("SuspendedCustomers", l);
        
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
        
        java.util.List<java.io.Serializable> events=
                    new java.util.ArrayList<java.io.Serializable>();
        
        RequestSent rqs=new RequestSent();
        rqs.setOperation("submitOrder");
        rqs.setServiceType("{urn:switchyard-quickstart-demo:orders:1.0}OrderService");
        rqs.setContent("<Order customer=\"Fred\" total=\"100\" />");
        
        RequestReceived rqr=new RequestReceived();
        rqr.setOperation("submitOrder");
        rqr.setServiceType("{urn:switchyard-quickstart-demo:orders:1.0}OrderService");
        rqr.setContent("<Order customer=\"Fred\" total=\"100\" />");
        
        ResponseSent rps=new ResponseSent();
        rps.setOperation("submitOrder");
        rps.setServiceType("{urn:switchyard-quickstart-demo:orders:1.0}OrderService");
        rps.setContent("<OrderConfirmed customer=\"Fred\" total=\"100\" />");
        
        rps.getProperties().put("total", "100");
        rps.getProperties().put("customer", "Fred");
        
        ResponseReceived rpr=new ResponseReceived();
        rpr.setOperation("submitOrder");
        rpr.setServiceType("{urn:switchyard-quickstart-demo:orders:1.0}OrderService");
        rpr.setContent("<OrderConfirmed customer=\"Fred\" total=\"100\" />");
        
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
        
        if (l.getEvents().size() != 0) {
            fail("Was not expecting any notified results: "+l.getEvents().size());
        }
        
        // Make second purchase that takes the customer above the level
        events=new java.util.ArrayList<java.io.Serializable>();
    
        rqs=new RequestSent();
        rqs.setOperation("submitOrder");
        rqs.setServiceType("{urn:switchyard-quickstart-demo:orders:1.0}OrderService");
        rqs.setContent("<Order customer=\"Fred\" total=\"170\" />");
        
        rqr=new RequestReceived();
        rqr.setOperation("submitOrder");
        rqr.setServiceType("{urn:switchyard-quickstart-demo:orders:1.0}OrderService");
        rqr.setContent("<Order customer=\"Fred\" total=\"170\" />");
        
        rps=new ResponseSent();
        rps.setOperation("submitOrder");
        rps.setServiceType("{urn:switchyard-quickstart-demo:orders:1.0}OrderService");
        rps.setContent("<OrderConfirmed customer=\"Fred\" total=\"170\" />");
        
        rps.getProperties().put("total", "170");
        rps.getProperties().put("customer", "Fred");
        
        rpr=new ResponseReceived();
        rpr.setOperation("submitOrder");
        rpr.setServiceType("{urn:switchyard-quickstart-demo:orders:1.0}OrderService");
        rpr.setContent("<OrderConfirmed customer=\"Fred\" total=\"170\" />");
        
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
        
        if (l.getEvents().size() != 1) {
            fail("Was expecting 1 notified results: "+l.getEvents().size());
        }
        
        if (!l.getEvents().get(0).equals("+Fred")) {
            fail("Expecting +Fred");
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
        
        java.util.List<java.io.Serializable> events=
                    new java.util.ArrayList<java.io.Serializable>();
        
        RequestSent rqs=new RequestSent();
        rqs.setOperation("submitOrder");
        rqs.setServiceType("{urn:switchyard-quickstart-demo:orders:1.0}OrderService");
        rqs.setContent("<Order customer=\"Fred\" total=\"200\" />");
        
        RequestReceived rqr=new RequestReceived();
        rqr.setOperation("submitOrder");
        rqr.setServiceType("{urn:switchyard-quickstart-demo:orders:1.0}OrderService");
        rqr.setContent("<Order customer=\"Fred\" total=\"200\" />");
        
        ResponseSent rps=new ResponseSent();
        rps.setOperation("submitOrder");
        rps.setServiceType("{urn:switchyard-quickstart-demo:orders:1.0}OrderService");
        rps.setContent("<OrderConfirmed customer=\"Fred\" total=\"200\" />");
        
        rps.getProperties().put("total", "200");
        rps.getProperties().put("customer", "Fred");
        
        ResponseReceived rpr=new ResponseReceived();
        rpr.setOperation("submitOrder");
        rpr.setServiceType("{urn:switchyard-quickstart-demo:orders:1.0}OrderService");
        rps.setContent("<OrderConfirmed customer=\"Fred\" total=\"200\" />");
        
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
        
        TestNotificationListener l=new TestNotificationListener();
        
        epnm.addNotificationListener("SuspendedCustomers", l);
        
        // Make second purchase that takes the customer above the level
        events=new java.util.ArrayList<java.io.Serializable>();
    
        rqs=new RequestSent();
        rqs.setOperation("makePayment");
        rqs.setServiceType("{urn:switchyard-quickstart-demo:orders:1.0}OrderService");
        rqs.setContent("<payment customer=\"Fred\" amount=\"170\" />");
        
        rqr=new RequestReceived();
        rqr.setOperation("makePayment");
        rqr.setServiceType("{urn:switchyard-quickstart-demo:orders:1.0}OrderService");
        rqr.setContent("<payment customer=\"Fred\" amount=\"170\" />");
        
        rps=new ResponseSent();
        rps.setOperation("makePayment");
        rps.setServiceType("{urn:switchyard-quickstart-demo:orders:1.0}OrderService");
        rps.setContent("<receipt customer=\"Fred\" amount=\"170\" />");
        
        rps.getProperties().put("total", "170");
        rps.getProperties().put("customer", "Fred");
        
        rpr=new ResponseReceived();
        rpr.setOperation("makePayment");
        rpr.setServiceType("{urn:switchyard-quickstart-demo:orders:1.0}OrderService");
        rpr.setContent("<receipt customer=\"Fred\" amount=\"170\" />");
        
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
        
        if (l.getEvents().size() != 1) {
            fail("Was expecting 1 notified results: "+l.getEvents().size());
        }
        
        if (!l.getEvents().get(0).equals("-Fred")) {
            fail("Expecting -Fred");
        }
    }

    @Test
    public void testCustomerIsolation() {
        EmbeddedEPNManager epnm=new EmbeddedEPNManager();
        
        TestNotificationListener l=new TestNotificationListener();
        
        epnm.addNotificationListener("SuspendedCustomers", l);
        
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
        
        java.util.List<java.io.Serializable> events=
                    new java.util.ArrayList<java.io.Serializable>();
        
        RequestSent rqs=new RequestSent();
        rqs.setOperation("submitOrder");
        rqs.setServiceType("{urn:switchyard-quickstart-demo:orders:1.0}OrderService");
        rqs.setContent("<Order customer=\"Fred\" total=\"100\" />");
        
        RequestReceived rqr=new RequestReceived();
        rqr.setOperation("submitOrder");
        rqr.setServiceType("{urn:switchyard-quickstart-demo:orders:1.0}OrderService");
        rqr.setContent("<Order customer=\"Fred\" total=\"100\" />");
        
        ResponseSent rps=new ResponseSent();
        rps.setOperation("submitOrder");
        rps.setServiceType("{urn:switchyard-quickstart-demo:orders:1.0}OrderService");
        rps.setContent("<OrderConfirmed customer=\"Fred\" total=\"100\" />");
        
        rps.getProperties().put("total", "100");
        rps.getProperties().put("customer", "Fred");
        
        ResponseReceived rpr=new ResponseReceived();
        rpr.setOperation("submitOrder");
        rpr.setServiceType("{urn:switchyard-quickstart-demo:orders:1.0}OrderService");
        rpr.setContent("<OrderConfirmed customer=\"Fred\" total=\"100\" />");
        
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
        
        if (l.getEvents().size() != 0) {
            fail("Was not expecting any notified results: "+l.getEvents().size());
        }
        
        // Make second purchase that takes the customer above the level
        events=new java.util.ArrayList<java.io.Serializable>();
    
        rqs=new RequestSent();
        rqs.setOperation("submitOrder");
        rqs.setServiceType("{urn:switchyard-quickstart-demo:orders:1.0}OrderService");
        rqs.setContent("<Order customer=\"Joe\" total=\"100\" />");
        
        rqr=new RequestReceived();
        rqr.setOperation("submitOrder");
        rqr.setServiceType("{urn:switchyard-quickstart-demo:orders:1.0}OrderService");
        rqr.setContent("<Order customer=\"Joe\" total=\"100\" />");
        
        rps=new ResponseSent();
        rps.setOperation("submitOrder");
        rps.setServiceType("{urn:switchyard-quickstart-demo:orders:1.0}OrderService");
        rps.setContent("<OrderConfirmed customer=\"Joe\" total=\"100\" />");
        
        rps.getProperties().put("total", "100");
        rps.getProperties().put("customer", "Joe");
        
        rpr=new ResponseReceived();
        rpr.setOperation("submitOrder");
        rpr.setServiceType("{urn:switchyard-quickstart-demo:orders:1.0}OrderService");
        rpr.setContent("<OrderConfirmed customer=\"Joe\" total=\"100\" />");
        
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
        
        if (l.getEvents().size() != 0) {
            fail("Was still expecting no notified results: "+l.getEvents().size());
        }
    }

    public class TestNotificationListener implements NotificationListener {
        
        private java.util.List<Object> _events=new java.util.ArrayList<Object>();

        public void notify(String subject, String network, String version,
                String node, NotificationType type, EventList events) {
            
            for (Object evt : events) {
                _events.add(evt);
            }
        }
        
        public java.util.List<Object> getEvents() {
            return (_events);
        }
    }
}
