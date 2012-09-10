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
import org.overlord.bam.activity.model.Property;
import org.overlord.bam.activity.model.soa.RequestReceived;
import org.overlord.bam.activity.model.soa.ResponseSent;
import org.overlord.bam.activity.server.QuerySpec;
import org.overlord.bam.activity.server.QuerySpec.Operator;

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
    public void testQueryContextMATCH() {
        QuerySpec qs=new QuerySpec().setExpression(
                new QuerySpec.Expression(Operator.Match,
                        new Context(Context.Type.Endpoint,"12345")));
        
        ActivityUnit au=new ActivityUnit();
        au.setId("TestId");
        
        au.getContext().add(new Context(Context.Type.Endpoint,"12345"));
        
        if (!qs.evaluate(au)) {
            fail("Query should evaluate to true");
        }
    }

    @Test
    public void testQueryPropertyMATCH() {
        QuerySpec qs=new QuerySpec().setExpression(
                new QuerySpec.Expression(Operator.Match,
                        new Property("trader","Joe")));
        
        ActivityUnit au=new ActivityUnit();
        au.setId("TestId");
        
        RequestReceived rr=new RequestReceived();
        au.getActivityTypes().add(rr);
        
        rr.getProperties().add(new Property("trader","Joe"));
        
        if (!qs.evaluate(au)) {
            fail("Query should evaluate to true");
        }
    }

    @Test
    public void testQueryContextMATCHFail() {
        QuerySpec qs=new QuerySpec().setExpression(
                new QuerySpec.Expression(Operator.Match,
                        new Context(Context.Type.Message,"5")));
        
        ActivityUnit au=new ActivityUnit();
        au.setId("TestId");
        
        au.getContext().add(new Context(Context.Type.Endpoint,"Joe"));
        
        if (qs.evaluate(au)) {
            fail("Query should evaluate to false");
        }
    }
    
    @Test
    public void testQueryPropertyMATCHFail() {
        QuerySpec qs=new QuerySpec().setExpression(
                new QuerySpec.Expression(Operator.Match,
                        new Property("mid","5")));
        
        ActivityUnit au=new ActivityUnit();
        au.setId("TestId");
        
        RequestReceived rr=new RequestReceived();
        au.getActivityTypes().add(rr);
        
        rr.getProperties().add(new Property("trader","Joe"));
        
        if (qs.evaluate(au)) {
            fail("Query should evaluate to false");
        }
    }
    
    @Test
    public void testQueryContextAND() {
        QuerySpec qs=new QuerySpec().setExpression(
                new QuerySpec.Expression(Operator.And,
                        new Context(Context.Type.Conversation,"123")));
        qs.getExpression().getProperties().add(
                        new Property("trader","Joe"));
        
        ActivityUnit au=new ActivityUnit();
        au.setId("TestId");
        
        RequestReceived rr=new RequestReceived();
        au.getActivityTypes().add(rr);
        
        rr.getContext().add(new Context(Context.Type.Conversation,"123"));
        rr.getProperties().add(new Property("trader","Joe"));
        
        if (!qs.evaluate(au)) {
            fail("Query should evaluate to true");
        }
    }

    @Test
    public void testQueryContextANDFail() {
        QuerySpec qs=new QuerySpec().setExpression(
                new QuerySpec.Expression(Operator.And,
                        new Context(Context.Type.Conversation,"123"),
                        new Context(Context.Type.Message,"5")));
        
        ActivityUnit au=new ActivityUnit();
        au.setId("TestId");
        
        RequestReceived rr=new RequestReceived();
        au.getActivityTypes().add(rr);
        
        rr.getContext().add(new Context(Context.Type.Conversation,"123"));
        rr.getProperties().add(new Property("trader","Joe"));
        
        if (qs.evaluate(au)) {
            fail("Query should evaluate to false");
        }
    }
    
    @Test
    public void testQueryContextExpressionAND() {
        QuerySpec qs=new QuerySpec().setExpression(
                new QuerySpec.Expression(Operator.And,
                        new Context(Context.Type.Conversation,"123")));
        
        QuerySpec.Expression subexpr= new QuerySpec.Expression(Operator.Match,
                new Property("trader","Joe"));
        
        qs.getExpression().getExpressions().add(subexpr);
        
        ActivityUnit au=new ActivityUnit();
        au.setId("TestId");
        
        RequestReceived rr=new RequestReceived();
        au.getActivityTypes().add(rr);
        
        rr.getContext().add(new Context(Context.Type.Conversation,"123"));
        rr.getProperties().add(new Property("trader","Joe"));
        
        if (!qs.evaluate(au)) {
            fail("Query should evaluate to true");
        }
    }

    @Test
    public void testQueryContextExpressionANDFail() {
        QuerySpec qs=new QuerySpec().setExpression(
                new QuerySpec.Expression(Operator.And,
                        new Context(Context.Type.Conversation,"123")));
        
        QuerySpec.Expression subexpr= new QuerySpec.Expression(Operator.Match,
                new Context(Context.Type.Message,"5"));
        
        qs.getExpression().getExpressions().add(subexpr);
        
        ActivityUnit au=new ActivityUnit();
        au.setId("TestId");
        
        RequestReceived rr=new RequestReceived();
        au.getActivityTypes().add(rr);
        
        rr.getContext().add(new Context(Context.Type.Conversation,"123"));
        rr.getProperties().add(new Property("trader","Joe"));
        
        if (qs.evaluate(au)) {
            fail("Query should evaluate to false");
        }
    }

    @Test
    public void testQueryContextOR() {
        QuerySpec qs=new QuerySpec().setExpression(
                new QuerySpec.Expression(Operator.Or,
                        new Context(Context.Type.Conversation,"123")));
        
        qs.getExpression().getProperties().add(
                        new Property("trader","Joe"));
        
        ActivityUnit au=new ActivityUnit();
        au.setId("TestId");
        
        RequestReceived rr=new RequestReceived();
        au.getActivityTypes().add(rr);
        
        rr.getContext().add(new Context(Context.Type.Conversation,"123"));
        rr.getProperties().add(new Property("trader","Fred"));
        
        if (!qs.evaluate(au)) {
            fail("Query should evaluate to true");
        }
    }

    @Test
    public void testQueryContextORFail() {
        QuerySpec qs=new QuerySpec().setExpression(
                new QuerySpec.Expression(Operator.Or,
                        new Context(Context.Type.Conversation,"123"),
                        new Context(Context.Type.Message,"5")));
        
        ActivityUnit au=new ActivityUnit();
        au.setId("TestId");
        
        RequestReceived rr=new RequestReceived();
        au.getActivityTypes().add(rr);
        
        rr.getContext().add(new Context(Context.Type.Conversation,"321"));
        rr.getProperties().add(new Property("trader","Fred"));
        
        if (qs.evaluate(au)) {
            fail("Query should evaluate to false");
        }
    }
    
    @Test
    public void testQueryContextExpressionOR() {
        QuerySpec qs=new QuerySpec().setExpression(
                new QuerySpec.Expression(Operator.Or,
                        new Context(Context.Type.Conversation,"123")));
        
        QuerySpec.Expression subexpr= new QuerySpec.Expression(Operator.Match,
                new Property("trader","Joe"));
        
        qs.getExpression().getExpressions().add(subexpr);
        
        ActivityUnit au=new ActivityUnit();
        au.setId("TestId");
        
        RequestReceived rr=new RequestReceived();
        au.getActivityTypes().add(rr);
        
        rr.getContext().add(new Context(Context.Type.Conversation,"321"));
        rr.getProperties().add(new Property("trader","Joe"));
        
        if (!qs.evaluate(au)) {
            fail("Query should evaluate to true");
        }
    }

    @Test
    public void testQueryContextExpressionORFail() {
        QuerySpec qs=new QuerySpec().setExpression(
                new QuerySpec.Expression(Operator.Or,
                        new Context(Context.Type.Conversation,"123")));
        
        QuerySpec.Expression subexpr= new QuerySpec.Expression(Operator.Match,
                new Context(Context.Type.Message,"5"));
        
        qs.getExpression().getExpressions().add(subexpr);
        
        ActivityUnit au=new ActivityUnit();
        au.setId("TestId");
        
        RequestReceived rr=new RequestReceived();
        au.getActivityTypes().add(rr);
        
        rr.getContext().add(new Context(Context.Type.Conversation,"321"));
        rr.getProperties().add(new Property("trader","Joe"));
        
        if (qs.evaluate(au)) {
            fail("Query should evaluate to false");
        }
    }

    @Test
    public void testQueryContextNOT() {
        QuerySpec qs=new QuerySpec().setExpression(
                new QuerySpec.Expression(Operator.Not,
                        new Context(Context.Type.Message,"5")));
        
        ActivityUnit au=new ActivityUnit();
        au.setId("TestId");
        
        RequestReceived rr=new RequestReceived();
        au.getActivityTypes().add(rr);
        
        rr.getProperties().add(new Property("trader","Joe"));
        
        if (!qs.evaluate(au)) {
            fail("Query should evaluate to true");
        }
    }

    @Test
    public void testQueryContextNOTFail() {
        QuerySpec qs=new QuerySpec().setExpression(
                new QuerySpec.Expression(Operator.Not,
                        new Property("trader","Joe")));
        
        ActivityUnit au=new ActivityUnit();
        au.setId("TestId");
        
        RequestReceived rr=new RequestReceived();
        au.getActivityTypes().add(rr);
        
        rr.getProperties().add(new Property("trader","Joe"));
        
        if (qs.evaluate(au)) {
            fail("Query should evaluate to false");
        }
    }
    
    @Test
    public void testQueryExpressionNOT() {
        QuerySpec qs=new QuerySpec().setExpression(
                new QuerySpec.Expression(Operator.Or,
                new QuerySpec.Expression(Operator.Not,
                        new Context(Context.Type.Message,"5"))));
        
        ActivityUnit au=new ActivityUnit();
        au.setId("TestId");
        
        RequestReceived rr=new RequestReceived();
        au.getActivityTypes().add(rr);
        
        rr.getProperties().add(new Property("trader","Joe"));
        
        if (!qs.evaluate(au)) {
            fail("Query should evaluate to true");
        }
    }

    
    @Test
    public void testQueryExpressionNOTFail() {
        QuerySpec qs=new QuerySpec().setExpression(
                new QuerySpec.Expression(Operator.Or,
                new QuerySpec.Expression(Operator.Not,
                        new Context(Context.Type.Message,"5"))));
        
        ActivityUnit au=new ActivityUnit();
        au.setId("TestId");
        
        au.getContext().add(new Context(Context.Type.Message,"5"));
        
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
                .setExpression(new QuerySpec.Expression(Operator.Or,
                        new Context(Context.Type.Conversation,"123"),
                        new Context(Context.Type.Message,"5")));
        qs.getExpression().getProperties().add(new Property("trader","joe"));
        
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
        
        String qspec="{\"id\":\"TestId\",\"fromTimestamp\":500,\"toTimestamp\":600,"
                +"\"expression\":{\"properties\":[{\"name\":\"trader\",\"value\":\"joe\""
                +"}],\"expressions\":[],\"operator\":\"Or\",\"contexts\":[{\"value\""
                +":\"123\",\"type\":\"Conversation\"},{\"value\":\"5\",\"type\":\"Message\"}]}}";
                
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
