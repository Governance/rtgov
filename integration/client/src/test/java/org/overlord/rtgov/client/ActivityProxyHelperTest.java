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
package org.overlord.rtgov.client;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.activity.collector.AbstractActivityCollector;
import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.soa.RequestReceived;
import org.overlord.rtgov.activity.model.soa.RequestSent;
import org.overlord.rtgov.activity.model.soa.ResponseReceived;
import org.overlord.rtgov.activity.model.soa.ResponseSent;

public class ActivityProxyHelperTest {

    private static final String HELLO_WORLD = "Hello World";
    private static final String EXCEPTION = "Throw Exception";

    @Test
    public void testClientProxySingleArg() {
        TestActivityCollector collector=new TestActivityCollector();
        
        ActivityProxyHelper.setActivityCollector(collector);
        
        TestServiceImpl tsi=new TestServiceImpl();
        
        TestService ts=ActivityProxyHelper.createClientProxy(TestService.class, this, tsi);
        
        try {
            String resp=ts.testMethod(HELLO_WORLD);
            
            if (!resp.equals(response(HELLO_WORLD))) {
                fail("Invalid response: "+resp);
            }
        } catch (Exception e) {
            fail("Should not have thrown exception: "+e);
        }
        
        if (collector.getEvents().size() != 2) {
            fail("Expecting 2 activity events: "+collector.getEvents().size());
        }
        
        if (!(collector.getEvents().get(0) instanceof RequestSent)) {
            fail("Event 0 should be RequestSent");
        }
        
        if (!(collector.getEvents().get(1) instanceof ResponseReceived)) {
            fail("Event 1 should be ResponseReceived");
        }
    }

    @Test
    public void testClientProxySingleArgWithFault() {
        TestActivityCollector collector=new TestActivityCollector();
        
        ActivityProxyHelper.setActivityCollector(collector);
        
        TestServiceImpl tsi=new TestServiceImpl();
        
        TestService ts=ActivityProxyHelper.createClientProxy(TestService.class, this, tsi);
        
        try {
            ts.testMethod(EXCEPTION);
            
            fail("Should have thrown exception");
        } catch (Exception e) {
        }
        
        if (collector.getEvents().size() != 2) {
            fail("Expecting 2 activity events: "+collector.getEvents().size());
        }
        
        if (!(collector.getEvents().get(0) instanceof RequestSent)) {
            fail("Event 0 should be RequestSent");
        }
        
        if (!(collector.getEvents().get(1) instanceof ResponseReceived)) {
            fail("Event 1 should be ResponseReceived");
        }
        
        if (((ResponseReceived)collector.getEvents().get(1)).getFault() == null) {
            fail("Event 1 should be a fault");
        }
        
        if (!((ResponseReceived)collector.getEvents().get(1)).getFault().equals("java.lang.Exception")) {
            fail("Event 1 fault name incorrect: "+((ResponseReceived)collector.getEvents().get(1)).getFault());
        }
    }

    @Test
    public void testServiceProxySingleArg() {
        TestActivityCollector collector=new TestActivityCollector();
        
        ActivityProxyHelper.setActivityCollector(collector);
        
        TestServiceImpl tsi=new TestServiceImpl();
        
        TestService ts=(TestService)ActivityProxyHelper.createServiceProxy(TestService.class.getName(), tsi);
        
        try {
            String resp=ts.testMethod(HELLO_WORLD);
            
            if (!resp.equals(response(HELLO_WORLD))) {
                fail("Invalid response: "+resp);
            }
        } catch (Exception e) {
            fail("Should not have thrown exception: "+e);
        }
        
        if (collector.getEvents().size() != 2) {
            fail("Expecting 2 activity events: "+collector.getEvents().size());
        }
        
        if (!(collector.getEvents().get(0) instanceof RequestReceived)) {
            fail("Event 0 should be RequestReceived");
        }
        
        if (!(collector.getEvents().get(1) instanceof ResponseSent)) {
            fail("Event 1 should be ResponseSent");
        }
    }

    @Test
    public void testServiceProxySingleArgWithFault() {
        TestActivityCollector collector=new TestActivityCollector();
        
        ActivityProxyHelper.setActivityCollector(collector);
        
        TestServiceImpl tsi=new TestServiceImpl();
        
        TestService ts=(TestService)ActivityProxyHelper.createServiceProxy(TestService.class.getName(), tsi);
        
        try {
            ts.testMethod(EXCEPTION);
            
            fail("Should have thrown exception");
        } catch (Exception e) {
        }
        
        if (collector.getEvents().size() != 2) {
            fail("Expecting 2 activity events: "+collector.getEvents().size());
        }
        
        if (!(collector.getEvents().get(0) instanceof RequestReceived)) {
            fail("Event 0 should be RequestReceived");
        }
        
        if (!(collector.getEvents().get(1) instanceof ResponseSent)) {
            fail("Event 1 should be ResponseSent");
        }
        
        if (((ResponseSent)collector.getEvents().get(1)).getFault() == null) {
            fail("Event 1 should be a fault");
        }
        
        if (!((ResponseSent)collector.getEvents().get(1)).getFault().equals("java.lang.Exception")) {
            fail("Event 1 fault name incorrect: "+((ResponseSent)collector.getEvents().get(1)).getFault());
        }
    }
    
    public interface TestService {
        
        public String testMethod(String name) throws Exception;        
    }
    
    public class TestServiceImpl implements TestService {
        private static final String HELLO_EXCEPTION = "Hello Exception";

        public String testMethod(String name) throws Exception {
            
            if (name == EXCEPTION) {
                throw new Exception(HELLO_EXCEPTION);
            }
            return (response(name));
        }
    }
    
    protected String response(String input) {
        return ("Response["+input+"]");
    }
    
    public class TestActivityCollector extends AbstractActivityCollector {

        private java.util.List<ActivityType> _events=new java.util.ArrayList<ActivityType>();
        
        public java.util.List<ActivityType> getEvents() {
            return (_events);
        }
        
        public void record(ActivityType actType) {
            _events.add(actType);
        }
        
        public void startScope() {
        }
        
        public void endScope() {
        }
    };

}
