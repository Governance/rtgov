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
package org.overlord.rtgov.activity.processor;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.activity.model.Context.Type;
import org.overlord.rtgov.activity.model.soa.RequestReceived;
import org.overlord.rtgov.activity.processor.TypeProcessor;
import org.overlord.rtgov.activity.processor.TypeProcessor.ContextEvaluator;
import org.overlord.rtgov.activity.processor.TypeProcessor.PropertyEvaluator;
import org.overlord.rtgov.activity.processor.mvel.MVELExpressionEvaluator;

public class TypeProcessorTest {

    private static final String VALUE2 = "Value2";
    private static final String VALUE1 = "Value1";
    private static final String HEADER1 = "Header1";
    private static final String THE_REPRESENTATION = "The Representation";

    @Test
    public void testInformationTransformer() {
        TypeProcessor processor=new TypeProcessor();
        
        SerializeInformationTransformer rep=new SerializeInformationTransformer();
        
        processor.setTransformer(rep);
        
        try {
            processor.init();
        } catch (Exception e) {
            fail("Failed to initialize: "+e);
        }
        
        String result=processor.process(THE_REPRESENTATION, null, new RequestReceived());
     
        if (result == null) {
            fail("Result is null");
        }
        
        if (!result.equals(THE_REPRESENTATION)) {
            fail("Unexpected result: "+result);
        }
    }

    @Test
    public void testProcessContextFromInformation() {
        TypeProcessor processor=new TypeProcessor();
        
        MVELExpressionEvaluator eval1=new MVELExpressionEvaluator();        
        eval1.setExpression("value1");
        
        ContextEvaluator ce1=new ContextEvaluator();
        ce1.setType(Type.Message);
        ce1.setEvaluator(eval1);
        
        processor.getContexts().add(ce1);
        
        MVELExpressionEvaluator eval2=new MVELExpressionEvaluator();        
        eval2.setExpression("value2");
        
        ContextEvaluator ce2=new ContextEvaluator();
        ce2.setType(Type.Endpoint);
        ce2.setEvaluator(eval2);
        
        processor.getContexts().add(ce2);
        
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

        String result=processor.process(to, null, rr);
     
        if (result != null) {
            fail("Result should be null");
        }
        
        if (rr.getContext().size() != 2) {
            fail("Expecting 2 context: "+rr.getContext().size());
        }
        
        Context c1=new Context(Type.Message, VALUE1);
        
        if (!rr.getContext().contains(c1)) {
            fail("Message context not found");
        }
        
        Context c2=new Context(Type.Endpoint, VALUE2);
        
        if (!rr.getContext().contains(c2)) {
            fail("Endpoint context not found");
        }
    }

    @Test
    public void testProcessContextFromHeader() {
        TypeProcessor processor=new TypeProcessor();
        
        MVELExpressionEvaluator eval1=new MVELExpressionEvaluator();        
        eval1.setExpression("value1");
        
        ContextEvaluator ce1=new ContextEvaluator();
        ce1.setType(Type.Message);
        ce1.setEvaluator(eval1);
        
        processor.getContexts().add(ce1);
        
        MVELExpressionEvaluator eval2=new MVELExpressionEvaluator();        
        eval2.setExpression("value2");
        
        ContextEvaluator ce2=new ContextEvaluator();
        ce2.setType(Type.Endpoint);
        ce2.setHeader(HEADER1);
        ce2.setEvaluator(eval2);
        
        processor.getContexts().add(ce2);
        
        try {
            processor.init();
        } catch (Exception e) {
            fail("Failed to initialize: "+e);
        }
        
        TestObject to1=new TestObject();
        to1.value1 = VALUE1;
        
        TestObject to2=new TestObject();
        to2.value2 = VALUE2;
        
        RequestReceived rr=new RequestReceived();

        if (rr.getContext().size() != 0) {
            fail("Expecting 0 context: "+rr.getContext().size());
        }

        java.util.Map<String,Object> headers=new java.util.HashMap<String, Object>();
        headers.put(HEADER1, to2);
        
        String result=processor.process(to1, headers, rr);
     
        if (result != null) {
            fail("Result should be null");
        }
        
        if (rr.getContext().size() != 2) {
            fail("Expecting 2 context: "+rr.getContext().size());
        }
        
        Context c1=new Context(Type.Message, VALUE1);
        
        if (!rr.getContext().contains(c1)) {
            fail("Message context not found");
        }
        
        Context c2=new Context(Type.Endpoint, VALUE2);
        
        if (!rr.getContext().contains(c2)) {
            fail("Endpoint context not found");
        }
    }

    @Test
    public void testProcessContextLinkWithTimeframe() {
        TypeProcessor processor=new TypeProcessor();
        
        MVELExpressionEvaluator eval1=new MVELExpressionEvaluator();        
        eval1.setExpression("value1");
        
        ContextEvaluator ce1=new ContextEvaluator();
        ce1.setType(Type.Link);
        ce1.setTimeframe(100);
        ce1.setEvaluator(eval1);
        
        processor.getContexts().add(ce1);
        
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

        String result=processor.process(to, null, rr);
     
        if (result != null) {
            fail("Result should be null");
        }
        
        if (rr.getContext().size() != 1) {
            fail("Expecting 1 context: "+rr.getContext().size());
        }
        
        Context c=rr.getContext().iterator().next();
        
        if (c.getType() != Type.Link) {
            fail("First context type should be link: "+c.getType());
        }
        
        if (c.getTimeframe() != 100) {
            fail("First context type should have timeframe 100: "+c.getTimeframe());
        }
        
    }

    @Test
    public void testProcessPropertiesFromInformation() {
        TypeProcessor processor=new TypeProcessor();
        
        MVELExpressionEvaluator eval1=new MVELExpressionEvaluator();        
        eval1.setExpression("value1");
        
        PropertyEvaluator pe1=new PropertyEvaluator();
        pe1.setName("name1");
        pe1.setEvaluator(eval1);
        
        processor.getProperties().add(pe1);
        
        MVELExpressionEvaluator eval2=new MVELExpressionEvaluator();        
        eval2.setExpression("value2");
        
        PropertyEvaluator pe2=new PropertyEvaluator();
        pe2.setName("name2");
        pe2.setEvaluator(eval2);
        
        processor.getProperties().add(pe2);
        
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

        String result=processor.process(to, null, rr);
     
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

    @Test
    public void testProcessPropertiesFromHeader() {
        TypeProcessor processor=new TypeProcessor();
        
        MVELExpressionEvaluator eval1=new MVELExpressionEvaluator();        
        eval1.setExpression("value1");
        
        PropertyEvaluator pe1=new PropertyEvaluator();
        pe1.setName("name1");
        pe1.setEvaluator(eval1);
        
        processor.getProperties().add(pe1);
        
        MVELExpressionEvaluator eval2=new MVELExpressionEvaluator();        
        eval2.setExpression("value2");
        
        PropertyEvaluator pe2=new PropertyEvaluator();
        pe2.setName("name2");
        pe2.setHeader(HEADER1);
        pe2.setEvaluator(eval2);
        
        processor.getProperties().add(pe2);
        
        try {
            processor.init();
        } catch (Exception e) {
            fail("Failed to initialize: "+e);
        }
        
        TestObject to1=new TestObject();
        to1.value1 = VALUE1;
        
        TestObject to2=new TestObject();
        to2.value2 = VALUE2;
        
        RequestReceived rr=new RequestReceived();

        if (rr.getProperties().size() != 0) {
            fail("Expecting 0 properties: "+rr.getProperties().size());
        }

        java.util.Map<String,Object> headers=new java.util.HashMap<String, Object>();
        headers.put(HEADER1, to2);
        
        String result=processor.process(to1, headers, rr);
     
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
