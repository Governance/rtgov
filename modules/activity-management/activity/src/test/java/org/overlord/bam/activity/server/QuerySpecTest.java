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
package org.overlord.bam.activity.server;

import static org.junit.Assert.*;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.overlord.bam.activity.model.ActivityUnit;
import org.overlord.bam.activity.model.Context;
import org.overlord.bam.activity.model.soa.RequestReceived;
import org.overlord.bam.activity.model.soa.ResponseSent;
import org.overlord.bam.activity.server.QuerySpec;

public class QuerySpecTest {

    @Test
    public void testQueryIdValid() {
        QuerySpec qs=new QuerySpec().setId("TestId");
        
        ActivityUnit au=new ActivityUnit();
        au.setId("TestId");
        
        if (!qs.evaluate(au)) {
            fail("Query should evaluate to true");
        }
    }

    @Test
    public void testQueryIdInvalid() {
        QuerySpec qs=new QuerySpec().setId("TestId");
        
        ActivityUnit au=new ActivityUnit();
        au.setId("NotTestId");
        
        if (qs.evaluate(au)) {
            fail("Query should evaluate to false");
        }
    }

    @Test
    public void testQueryTimestampRangeValid1() {
        QuerySpec qs=new QuerySpec()
                .setFromTimestamp(100)
                .setToTimestamp(200);
        
        ActivityUnit au=new ActivityUnit();
        au.setId("TestId");
        
        RequestReceived at1=new RequestReceived();
        at1.setTimestamp(150);
        
        ResponseSent at2=new ResponseSent();
        at2.setTimestamp(250);
        
        au.getActivityTypes().add(at1);
        au.getActivityTypes().add(at2);
        
        if (!qs.evaluate(au)) {
            fail("Query should evaluate to true");
        }
    }

    @Test
    public void testQueryTimestampRangeValid2() {
        QuerySpec qs=new QuerySpec()
                .setFromTimestamp(200)
                .setToTimestamp(300);
        
        ActivityUnit au=new ActivityUnit();
        au.setId("TestId");
        
        RequestReceived at1=new RequestReceived();
        at1.setTimestamp(150);
        
        ResponseSent at2=new ResponseSent();
        at2.setTimestamp(250);
        
        au.getActivityTypes().add(at1);
        au.getActivityTypes().add(at2);
        
        if (!qs.evaluate(au)) {
            fail("Query should evaluate to true");
        }
    }

    @Test
    public void testQueryTimestampRangeInvalid1() {
        QuerySpec qs=new QuerySpec()
                .setFromTimestamp(100)
                .setToTimestamp(200);
        
        ActivityUnit au=new ActivityUnit();
        au.setId("TestId");
        
        RequestReceived at1=new RequestReceived();
        at1.setTimestamp(50);
        
        ResponseSent at2=new ResponseSent();
        at2.setTimestamp(90);
        
        au.getActivityTypes().add(at1);
        au.getActivityTypes().add(at2);
        
        if (qs.evaluate(au)) {
            fail("Query should evaluate to false");
        }
    }

    @Test
    public void testQueryTimestampRangeInvalid2() {
        QuerySpec qs=new QuerySpec()
                .setFromTimestamp(200)
                .setToTimestamp(300);
        
        ActivityUnit au=new ActivityUnit();
        au.setId("TestId");
        
        RequestReceived at1=new RequestReceived();
        at1.setTimestamp(350);
        
        ResponseSent at2=new ResponseSent();
        at2.setTimestamp(450);
        
        au.getActivityTypes().add(at1);
        au.getActivityTypes().add(at2);
        
        if (qs.evaluate(au)) {
            fail("Query should evaluate to false");
        }
    }

    @Test
    public void testQueryContextAND() {
        QuerySpec qs=new QuerySpec()
                .addContext(new Context(Context.CONVERSATION_ID,"txnId","123"))
                .addContext(new Context(Context.PROPERTY_ID,"trader","Joe"));
        
        ActivityUnit au=new ActivityUnit();
        au.setId("TestId");
        
        au.getContext().add(new Context(Context.CONVERSATION_ID,"txnId","123"));
        au.getContext().add(new Context(Context.PROPERTY_ID,"trader","Joe"));
        
        if (!qs.evaluate(au)) {
            fail("Query should evaluate to true");
        }
    }

    @Test
    public void testQueryContextANDFail() {
        QuerySpec qs=new QuerySpec()
                .addContext(new Context(Context.CONVERSATION_ID,"txnId","123"))
                .addContext(new Context(Context.MESSAGE_ID,"mid","5"));
        
        ActivityUnit au=new ActivityUnit();
        au.setId("TestId");
        
        au.getContext().add(new Context(Context.CONVERSATION_ID,"txnId","123"));
        au.getContext().add(new Context(Context.PROPERTY_ID,"trader","Joe"));
        
        if (qs.evaluate(au)) {
            fail("Query should evaluate to false");
        }
    }
    
    @Test
    public void testQueryContextOR() {
        QuerySpec qs=new QuerySpec()
                .addContext(new Context(Context.CONVERSATION_ID,"txnId","123"))
                .addContext(new Context(Context.PROPERTY_ID,"trader","Joe"))
                .setContextAND(false);
        
        ActivityUnit au=new ActivityUnit();
        au.setId("TestId");
        
        au.getContext().add(new Context(Context.CONVERSATION_ID,"txnId","123"));
        au.getContext().add(new Context(Context.PROPERTY_ID,"trader","Fred"));
        
        if (!qs.evaluate(au)) {
            fail("Query should evaluate to true");
        }
    }

    @Test
    public void testQueryContextORFail() {
        QuerySpec qs=new QuerySpec()
                .addContext(new Context(Context.CONVERSATION_ID,"txnId","123"))
                .addContext(new Context(Context.MESSAGE_ID,"mid","5"))
                .setContextAND(false);
        
        ActivityUnit au=new ActivityUnit();
        au.setId("TestId");
        
        au.getContext().add(new Context(Context.CONVERSATION_ID,"txnId","321"));
        au.getContext().add(new Context(Context.PROPERTY_ID,"trader","Fred"));
        
        if (qs.evaluate(au)) {
            fail("Query should evaluate to false");
        }
    }
    
    @Test
    public void testQuerySerialize() {
        QuerySpec qs=new QuerySpec()
                .setId("TestId")
                .setFromTimestamp(500)
                .setToTimestamp(600)
                .addContext(new Context(Context.CONVERSATION_ID,"txnId","123"))
                .addContext(new Context(Context.MESSAGE_ID,"mid","5"))
                .setContextAND(false);
        
        ObjectMapper mapper=new ObjectMapper();
        
        try {
            java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
            
            mapper.writeValue(baos, qs);
            
            baos.close();
            
            System.out.println("QUERY SPEC=\r\n"+new String(baos.toByteArray()));
            
        } catch(Exception e) {
            fail("Failed to serialize: "+e);
        }
    }
    
    @Test
    public void testQueryDeserialize() {
        ObjectMapper mapper=new ObjectMapper();
        
        String qspec="{\"id\":\"TestId\",\"fromTimestamp\":500,\"toTimestamp\":600," +
        		"\"contextAND\":false,\"contexts\":[{\"name\":\"txnId\",\"value\":\"123\"," +
        		"\"type\":0},{\"name\":\"mid\",\"value\":\"5\",\"type\":2}]}";
        
        try {
            java.io.ByteArrayInputStream bais=new java.io.ByteArrayInputStream(qspec.getBytes());
            
            QuerySpec qs=mapper.readValue(bais, QuerySpec.class);
            
            bais.close();
            
            System.out.println("QUERY SPEC="+qs);
            
        } catch(Exception e) {
            fail("Failed to deserialize: "+e);
        }
    }
}
