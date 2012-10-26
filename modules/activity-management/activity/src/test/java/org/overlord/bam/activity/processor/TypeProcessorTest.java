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
package org.overlord.bam.activity.processor;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.bam.activity.model.Context.Type;
import org.overlord.bam.activity.model.soa.RequestReceived;
import org.overlord.bam.activity.processor.TypeProcessor.ContextEvaluator;
import org.overlord.bam.activity.processor.TypeProcessor.PropertyEvaluator;

public class TypeProcessorTest {

    private static final String VALUE2 = "Value2";
    private static final String VALUE1 = "Value1";
    private static final String THE_REPRESENTATION = "The Representation";

    @Test
    public void testProcessRepresentation() {
        TypeProcessor processor=new TypeProcessor();
        
        MVELExpressionEvaluator rep=new MVELExpressionEvaluator();
        
        rep.setExpression("\""+THE_REPRESENTATION+"\"");
        
        processor.setRepresentation(rep);
        
        try {
            processor.init();
        } catch (Exception e) {
            fail("Failed to initialize: "+e);
        }
        
        String result=processor.process(new TestObject(), new RequestReceived());
     
        if (result == null) {
            fail("Result is null");
        }
        
        if (!result.equals(THE_REPRESENTATION)) {
            fail("Unexpected result: "+result);
        }
    }

    @Test
    public void testProcessContext() {
        TypeProcessor processor=new TypeProcessor();
        
        MVELExpressionEvaluator eval1=new MVELExpressionEvaluator();        
        eval1.setExpression("value1");
        
        ContextEvaluator ce1=new ContextEvaluator();
        ce1.setType(Type.Message);
        ce1.setEvaluator(eval1);
        
        processor.getContextEvaluators().add(ce1);
        
        MVELExpressionEvaluator eval2=new MVELExpressionEvaluator();        
        eval2.setExpression("value2");
        
        ContextEvaluator ce2=new ContextEvaluator();
        ce2.setType(Type.Endpoint);
        ce2.setEvaluator(eval2);
        
        processor.getContextEvaluators().add(ce2);
        
        try {
            processor.init();
        } catch (Exception e) {
            fail("Failed to initialize: "+e);
        }
        
        TestObject to=new TestObject();
        to.value1 = VALUE1;
        to.value2 = VALUE2;
        
        RequestReceived rr=new RequestReceived();

        if (rr.getContext().size() != 0) {
            fail("Expecting 0 context: "+rr.getContext().size());
        }

        String result=processor.process(to, rr);
     
        if (result != null) {
            fail("Result should be null");
        }
        
        if (rr.getContext().size() != 2) {
            fail("Expecting 2 context: "+rr.getContext().size());
        }
        
        if (rr.getContext().get(0).getType() != Type.Message) {
            fail("First context type should be message: "+rr.getContext().get(0).getType());
        }
        
        if (!rr.getContext().get(0).getValue().equals(VALUE1)) {
            fail("First context value incorrect: "+rr.getContext().get(0).getValue());
        }
        
        if (rr.getContext().get(1).getType() != Type.Endpoint) {
            fail("Second context type should be endpoint: "+rr.getContext().get(1).getType());
        }
        
        if (!rr.getContext().get(1).getValue().equals(VALUE2)) {
            fail("Second context value incorrect: "+rr.getContext().get(1).getValue());
        }
    }

    @Test
    public void testProcessProperties() {
        TypeProcessor processor=new TypeProcessor();
        
        MVELExpressionEvaluator eval1=new MVELExpressionEvaluator();        
        eval1.setExpression("value1");
        
        PropertyEvaluator pe1=new PropertyEvaluator();
        pe1.setName("name1");
        pe1.setEvaluator(eval1);
        
        processor.getPropertyEvaluators().add(pe1);
        
        MVELExpressionEvaluator eval2=new MVELExpressionEvaluator();        
        eval2.setExpression("value2");
        
        PropertyEvaluator pe2=new PropertyEvaluator();
        pe2.setName("name2");
        pe2.setEvaluator(eval2);
        
        processor.getPropertyEvaluators().add(pe2);
        
        try {
            processor.init();
        } catch (Exception e) {
            fail("Failed to initialize: "+e);
        }
        
        TestObject to=new TestObject();
        to.value1 = VALUE1;
        to.value2 = VALUE2;
        
        RequestReceived rr=new RequestReceived();

        if (rr.getProperties().size() != 0) {
            fail("Expecting 0 properties: "+rr.getProperties().size());
        }

        String result=processor.process(to, rr);
     
        if (result != null) {
            fail("Result should be null");
        }
        
        if (rr.getProperties().size() != 2) {
            fail("Expecting 2 properties: "+rr.getProperties().size());
        }
        
        if (!rr.getProperties().containsKey("name1")) {
            fail("No 'name1' property");
        }
        
        if (!rr.getProperties().get("name1").equals(VALUE1)) {
            fail("Property 'name1' value incorrect: "+rr.getProperties().get("name1"));
        }
        
        if (!rr.getProperties().containsKey("name2")) {
            fail("No 'name2' property");
        }
        
        if (!rr.getProperties().get("name2").equals(VALUE2)) {
            fail("Property 'name2' value incorrect: "+rr.getProperties().get("name2"));
        }
        
    }

    public class TestObject {
        
        public String value1=null;
        public String value2=null;
    }
}
