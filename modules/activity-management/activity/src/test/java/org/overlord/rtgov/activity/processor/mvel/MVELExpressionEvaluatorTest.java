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
package org.overlord.rtgov.activity.processor.mvel;

import static org.junit.Assert.*;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.overlord.rtgov.activity.model.soa.RequestReceived;
import org.overlord.rtgov.activity.processor.mvel.MVELExpressionEvaluator;

public class MVELExpressionEvaluatorTest {

    private static final String TEST_MESSAGE_ID = "TestMID";

    @Test
    public void testEvaluate() {
        MVELExpressionEvaluator evaluator=new MVELExpressionEvaluator();
        
        RequestReceived rr=new RequestReceived();
        rr.setMessageId(TEST_MESSAGE_ID);
        
        evaluator.setExpression("messageId");
        
        try {
            evaluator.init();
        } catch (Exception e) {
            fail("Evaluator should have initialized: "+e);
        }
        
        String result=evaluator.evaluate(rr);
        
        if (result == null) {
            fail("Result is null");
        }
        
        if (!result.equals(TEST_MESSAGE_ID)) {
            fail("Unexpected result: "+result);
        }
    }

    @Test
    public void testEvaluateBoolean() {
        MVELExpressionEvaluator evaluator=new MVELExpressionEvaluator();
        
        RequestReceived rr=new RequestReceived();
        rr.setMessageId(TEST_MESSAGE_ID);
        
        evaluator.setExpression("request");
        
        try {
            evaluator.init();
        } catch (Exception e) {
            fail("Evaluator should have initialized: "+e);
        }
        
        String result=evaluator.evaluate(rr);
        
        if (result == null) {
            fail("Result is null");
        }
        
        if (!result.equals("true")) {
            fail("Unexpected result: "+result);
        }
    }

    @Test
    public void testEvaluateTestValue() {
        MVELExpressionEvaluator evaluator=new MVELExpressionEvaluator();
        
        TestHolder th=new TestHolder();
        
        evaluator.setExpression("value");
        
        try {
            evaluator.init();
        } catch (Exception e) {
            fail("Evaluator should have initialized: "+e);
        }
        
        String result=evaluator.evaluate(th);
        
        if (result == null) {
            fail("Result is null");
        }
        
        if (!result.equals("TheValue")) {
            fail("Unexpected result: "+result);
        }
    }

    @Test
    public void testEvaluateTransformFromDOM() {
        MVELExpressionEvaluator evaluator=new MVELExpressionEvaluator();
        
        String expression = "import javax.xml.transform.TransformerFactory; import javax.xml.transform.Transformer; "
                + "import javax.xml.transform.dom.DOMSource; import javax.xml.transform.stream.StreamResult; "
                + "import java.io.ByteArrayOutputStream; ByteArrayOutputStream out = new ByteArrayOutputStream(); "
                + "DOMSource source = new DOMSource(this); StreamResult result = new StreamResult(out); "
                + "TransformerFactory transFactory = TransformerFactory.newInstance(); "
                + "Transformer transformer = transFactory.newTransformer(); "
                + "transformer.transform(source, result); out.close(); "
                + "return new String(out.toByteArray());";
        
        evaluator.setExpression(expression);
        
        try {
            evaluator.init();
        } catch (Exception e) {
            fail("Evaluator should have initialized: "+e);
        }
        
        org.w3c.dom.Document doc=null;

        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            org.w3c.dom.Element elem=doc.createElement("test");
            doc.appendChild(elem);
            elem.appendChild(doc.createTextNode("This is a test"));
        } catch (Exception e) {
            fail("Failed to build DOM: "+e);
        }

        String result=evaluator.evaluate(doc);
        
        if (result == null) {
            fail("Result is null");
        }
        
        if (!result.contains("<test>")) {
            fail("Unexpected result: "+result);
        }
    }

    @Test
    public void testEvaluateBadExpression() {
        MVELExpressionEvaluator evaluator=new MVELExpressionEvaluator();
        
        evaluator.setExpression("X X");
        
        try {
            evaluator.init();
            fail("Evaluator should NOT have initialized");
        } catch (Exception e) {
        }
    }

    public static class TestValue {
        
        public String toString() {
            return ("TheValue");
        }
    }
    
    public static class TestHolder {
        
        public TestValue getValue() {
            return (new TestValue());
        }
    }
}
