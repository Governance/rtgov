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
package org.overlord.rtgov.samples.sla;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.overlord.rtgov.analytics.service.ResponseTime;
import org.overlord.rtgov.analytics.situation.Situation;
import org.overlord.rtgov.epn.EventList;
import org.overlord.rtgov.epn.Network;
import org.overlord.rtgov.epn.NotificationListener;
import org.overlord.rtgov.epn.embedded.EmbeddedEPNManager;
import org.overlord.rtgov.epn.util.NetworkUtil;

public class SLAEPNTest {

    private static final EmbeddedEPNManager EPNM=new EmbeddedEPNManager();
    private static final TestNotificationListener LISTENER=new TestNotificationListener();;
    
    @BeforeClass
    public static void setup() {
       
        // Load network
        Network network=null;
        
        try {
            java.io.InputStream is=ClassLoader.getSystemResourceAsStream("epn.json");
            
            byte[] b=new byte[is.available()];
            is.read(b);
            
            is.close();
            
            network = NetworkUtil.deserialize(b);
            
            EPNM.register(network);
        } catch (Exception e) {
            fail("Failed to register network: "+e);
        }
        
        EPNM.addNotificationListener("Situations", LISTENER);
    }
    
    @Before
    public void initTest() {
        LISTENER.clear();
    }
    
    @Test
    public void testSituationNone() {
       
        java.util.List<java.io.Serializable> events=
                new java.util.ArrayList<java.io.Serializable>();
    
        ResponseTime rt=new ResponseTime();
        rt.setServiceType("TestServiceType");
        rt.setOperation("TestOperation");
        rt.setAverage(150);
    
        events.add(rt);
        
        try {
            EPNM.publish("ServiceResponseTimes", events);
            
            synchronized (this) {
                wait(1000);
            }
        } catch (Exception e) {
            fail("Failed to publish events: "+e);
        }
        
        if (LISTENER.getEvents().size() > 0) {
            fail("Expecting 0 situations: "+LISTENER.getEvents().size());
        }
    }
    
    @Test
    public void testSituationSeverityLow() {
       
        java.util.List<java.io.Serializable> events=
                new java.util.ArrayList<java.io.Serializable>();
    
        ResponseTime rt=new ResponseTime();
        rt.setServiceType("TestServiceType");
        rt.setOperation("TestOperation");
        rt.setAverage(250);
    
        events.add(rt);
        
        try {
            EPNM.publish("ServiceResponseTimes", events);
            
            synchronized (this) {
                wait(1000);
            }
        } catch (Exception e) {
            fail("Failed to publish events: "+e);
        }
        
        if (LISTENER.getEvents().size() != 1) {
            fail("Expecting 1 situation: "+LISTENER.getEvents().size());
        }
        
        Situation sit=(Situation)LISTENER.getEvents().get(0);
        
        if (sit.getSeverity() != Situation.Severity.Low) {
            fail("Severity wasn't low: "+sit.getSeverity());
        }
    }
    
    @Test
    public void testSituationSeverityHigh() {
       
        java.util.List<java.io.Serializable> events=
                new java.util.ArrayList<java.io.Serializable>();
    
        ResponseTime rt=new ResponseTime();
        rt.setServiceType("TestServiceType");
        rt.setOperation("TestOperation");
        rt.setAverage(350);
    
        events.add(rt);
        
        try {
            EPNM.publish("ServiceResponseTimes", events);
            
            synchronized (this) {
                wait(1000);
            }
        } catch (Exception e) {
            fail("Failed to publish events: "+e);
        }
        
        if (LISTENER.getEvents().size() != 1) {
            fail("Expecting 1 situation: "+LISTENER.getEvents().size());
        }
        
        Situation sit=(Situation)LISTENER.getEvents().get(0);
        
        if (sit.getSeverity() != Situation.Severity.High) {
            fail("Severity wasn't high: "+sit.getSeverity());
        }
    }

    public static class TestNotificationListener implements NotificationListener {

        private java.util.List<Object> _events=new java.util.ArrayList<Object>();
        
        @Override
        public void notify(String subject, EventList events) {
            for (Object obj : events) {
                _events.add(obj);
            }
        }
        
        public void clear() {
            _events.clear();
        }
        
        public java.util.List<Object> getEvents() {
            return (_events);
        }
    }
}
