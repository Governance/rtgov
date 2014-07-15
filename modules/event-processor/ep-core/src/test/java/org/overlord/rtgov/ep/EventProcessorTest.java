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
package org.overlord.rtgov.ep;

import static org.junit.Assert.*;

import java.io.Serializable;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class EventProcessorTest {

    @Test
    public void testEventProcessorSerialization() {
        ObjectMapper mapper=new ObjectMapper();
        
        TestEventProcessor ep=new TestEventProcessor();
        ep.getParameters().put("testparam1", "hello");
        ep.getParameters().put("testparam2", 5);
        
        try {
            String result=mapper.writeValueAsString(ep);
            
            TestEventProcessor epresult=mapper.readValue(result, TestEventProcessor.class);
            
            if (epresult == null) {
                fail("Result is null");
            }
            
            if (!epresult.getParameters().get("testparam1").equals("hello")) {
                fail("Test param 1 is incorrect: "+epresult.getParameters().get("testparam1"));
            }
            
            int val=(Integer)epresult.getParameters().get("testparam2");
            
            if (val != 5) {
                fail("Test param 2 is not 5: "+val);
            }
            
        } catch (Exception e) {
            fail("Failed to serialize ep: "+e);
        }
    }

    @Test
    public void testEventProcessorSerializationUnknownType() {
        ObjectMapper mapper=new ObjectMapper();
        
        TestEventProcessor ep=new TestEventProcessor();
        ep.getParameters().put("testparam1", "hello");
        ep.getParameters().put("testparam2", new TestObject());
        
        try {
            String result=mapper.writeValueAsString(ep);
            
            TestEventProcessor epresult=mapper.readValue(result, TestEventProcessor.class);
            
            if (epresult == null) {
                fail("Result 1 is null");
            }
            
            if (!epresult.getParameters().get("testparam1").equals("hello")) {
                fail("Test param 1 is incorrect: "+epresult.getParameters().get("testparam1"));
            }
            
            Object obj=epresult.getParameters().get("testparam2");
            
            if (obj == null) {
                fail("Result 2 is null");
            }
            
            @SuppressWarnings("unchecked")
            java.util.Map<String,Object> map=(java.util.Map<String,Object>)obj;
            
            String val2=(String)map.get("val");
            
            if (!val2.equals("TestValue")) {
                fail("Incorrect value: "+val2);
            }
            
        } catch (Exception e) {
            fail("Failed to serialize ep: "+e);
        }
    }

    public static class TestEventProcessor extends EventProcessor {

        @Override
        public Serializable process(String source, Serializable event,
                int retriesLeft) throws Exception {
            // TODO Auto-generated method stub
            return null;
        }
        
    }
    
    public static class TestObject {
        
        private String _val="TestValue";
        
        public String getVal() {
            return (_val);
        }
        
        public void setVal(String val) {
            _val = val;
        }
    }
}
