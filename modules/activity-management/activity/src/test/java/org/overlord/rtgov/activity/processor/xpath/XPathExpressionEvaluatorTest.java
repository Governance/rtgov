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
package org.overlord.rtgov.activity.processor.xpath;

import static org.junit.Assert.*;

import javax.xml.soap.SOAPMessage;

import org.junit.Test;
import org.overlord.rtgov.activity.processor.xpath.XPathExpressionEvaluator;

public class XPathExpressionEvaluatorTest {

    @Test
    public void testEvaluateStringNoNamespace() {
        String value="hello";
        String xml="<mydoc><field>"+value+"</field></mydoc>";
        
        XPathExpressionEvaluator evaluator=new XPathExpressionEvaluator();
        
        evaluator.setExpression("/mydoc/field");
        
        try {
            evaluator.init();
        } catch(Exception e) {
            fail("Failed to initialize: "+e);
        }
        
        String result=evaluator.evaluate(xml);
        
        if (result == null) {
            fail("Failed to get result");
        }
        
        if (!result.equals(value)) {
            fail("Result not expected: "+result);
        }
    }

    @Test
    public void testEvaluateStringNamespace() {
        String value="hello";
        String xml="<ns1:mydoc xmlns:ns1=\"http://www.mynamespace\" ><ns1:field>"+value+"</ns1:field></ns1:mydoc>";
        
        XPathExpressionEvaluator evaluator=new XPathExpressionEvaluator();
        
        evaluator.setExpression("/ns2:mydoc/ns2:field");
        
        evaluator.getNamespaces().put("ns2", "http://www.mynamespace");
        
        try {
            evaluator.init();
        } catch(Exception e) {
            fail("Failed to initialize: "+e);
        }
        
        String result=evaluator.evaluate(xml);
        
        if (result == null) {
            fail("Failed to get result");
        }
        
        if (!result.equals(value)) {
            fail("Result not expected: "+result);
        }
    }

    @Test
    public void testEvaluateStringDefaultNamespace() {
        String value="hello";
        String xml="<mydoc xmlns=\"http://www.mynamespace\" ><field>"+value+"</field></mydoc>";
        
        XPathExpressionEvaluator evaluator=new XPathExpressionEvaluator();
        
        evaluator.setExpression("/ns2:mydoc/ns2:field");
        
        evaluator.getNamespaces().put("ns2", "http://www.mynamespace");
        
        try {
            evaluator.init();
        } catch(Exception e) {
            fail("Failed to initialize: "+e);
        }
        
        String result=evaluator.evaluate(xml);
        
        if (result == null) {
            fail("Failed to get result");
        }
        
        if (!result.equals(value)) {
            fail("Result not expected: "+result);
        }
    }
    
    @Test
    public void testEvaluateDOMNamespace() {
        String value="hello";
        String xml="<ns1:mydoc xmlns:ns1=\"http://www.mynamespace\" ><ns1:field>"+value+"</ns1:field></ns1:mydoc>";
        
        XPathExpressionEvaluator evaluator=new XPathExpressionEvaluator();
        
        evaluator.setExpression("/ns2:mydoc/ns2:field/text()");
        
        evaluator.getNamespaces().put("ns2", "http://www.mynamespace");
        
        try {
            evaluator.init();
        } catch(Exception e) {
            fail("Failed to initialize: "+e);
        }
        
        org.w3c.dom.Document doc=null;
        
        try {
            javax.xml.parsers.DocumentBuilderFactory factory=
                    javax.xml.parsers.DocumentBuilderFactory.newInstance();
            
            factory.setNamespaceAware(true);
            
            javax.xml.parsers.DocumentBuilder builder=
                    factory.newDocumentBuilder();
            
            java.io.InputStream is=
                    new java.io.ByteArrayInputStream(xml.getBytes());
            
            doc = builder.parse(is);
                    
            is.close();
        } catch(Exception e) {
            fail("Failed to parse xml: "+e);
        }
        
        String result=evaluator.evaluate(doc.getDocumentElement());
        
        if (result == null) {
            fail("Failed to get result");
        }
        
        if (!result.equals(value)) {
            fail("Result not expected: "+result);
        }
    }
    
    @Test
    public void testEvaluateDOMNamespace2() {
        String expected="<?xml version=\"1.0\" encoding=\"UTF-8\"?><ns1:field xmlns:ns1=\"http://www.mynamespace\">hello</ns1:field>";
        String xml="<ns1:mydoc xmlns:ns1=\"http://www.mynamespace\" ><ns1:field>hello</ns1:field></ns1:mydoc>";
        
        XPathExpressionEvaluator evaluator=new XPathExpressionEvaluator();
        
        evaluator.setExpression("/ns2:mydoc/ns2:field");
        
        evaluator.getNamespaces().put("ns2", "http://www.mynamespace");
        
        try {
            evaluator.init();
        } catch(Exception e) {
            fail("Failed to initialize: "+e);
        }
        
        org.w3c.dom.Document doc=null;
        
        try {
            javax.xml.parsers.DocumentBuilderFactory factory=
                    javax.xml.parsers.DocumentBuilderFactory.newInstance();
            
            factory.setNamespaceAware(true);
            
            javax.xml.parsers.DocumentBuilder builder=
                    factory.newDocumentBuilder();
            
            java.io.InputStream is=
                    new java.io.ByteArrayInputStream(xml.getBytes());
            
            doc = builder.parse(is);
                    
            is.close();
        } catch(Exception e) {
            fail("Failed to parse xml: "+e);
        }
        
        String result=evaluator.evaluate(doc.getDocumentElement());
        
        if (result == null) {
            fail("Failed to get result");
        }
        
        if (!result.equals(expected)) {
            fail("Result not expected: "+result);
        }
    }
    
    @Test
    public void testEvaluateDOMNamespaceUnaware() {
        String value="hello";
        String xml="<ns1:mydoc xmlns:ns1=\"http://www.mynamespace\" ><ns1:field>"+value+"</ns1:field></ns1:mydoc>";
        
        XPathExpressionEvaluator evaluator=new XPathExpressionEvaluator();
        
        evaluator.setExpression("/ns2:mydoc/ns2:field/text()");
        
        evaluator.getNamespaces().put("ns2", "http://www.mynamespace");
        
        try {
            evaluator.init();
        } catch(Exception e) {
            fail("Failed to initialize: "+e);
        }
        
        org.w3c.dom.Document doc=null;
        
        try {
            javax.xml.parsers.DocumentBuilderFactory factory=
                    javax.xml.parsers.DocumentBuilderFactory.newInstance();
            
            javax.xml.parsers.DocumentBuilder builder=
                    factory.newDocumentBuilder();
            
            java.io.InputStream is=
                    new java.io.ByteArrayInputStream(xml.getBytes());
            
            doc = builder.parse(is);
                    
            is.close();
        } catch(Exception e) {
            fail("Failed to parse xml: "+e);
        }
        
        String result=evaluator.evaluate(doc.getDocumentElement());
        
        if (result == null) {
            fail("Failed to get result");
        }
        
        if (!result.equals(value)) {
            fail("Result not expected: "+result);
        }
    }
    
    @Test
    public void testEvaluateDOMNamespaceUnaware2() {
        String expected="<?xml version=\"1.0\" encoding=\"UTF-8\"?><ns1:field xmlns:ns1=\"http://www.mynamespace\">hello</ns1:field>";
        String xml="<ns1:mydoc xmlns:ns1=\"http://www.mynamespace\" ><ns1:field>hello</ns1:field></ns1:mydoc>";
        
        XPathExpressionEvaluator evaluator=new XPathExpressionEvaluator();
        
        evaluator.setExpression("/ns2:mydoc/ns2:field");
        
        evaluator.getNamespaces().put("ns2", "http://www.mynamespace");
        
        try {
            evaluator.init();
        } catch(Exception e) {
            fail("Failed to initialize: "+e);
        }
        
        org.w3c.dom.Document doc=null;
        
        try {
            javax.xml.parsers.DocumentBuilderFactory factory=
                    javax.xml.parsers.DocumentBuilderFactory.newInstance();
            
            javax.xml.parsers.DocumentBuilder builder=
                    factory.newDocumentBuilder();
            
            java.io.InputStream is=
                    new java.io.ByteArrayInputStream(xml.getBytes());
            
            doc = builder.parse(is);
                    
            is.close();
        } catch(Exception e) {
            fail("Failed to parse xml: "+e);
        }
        
        String result=evaluator.evaluate(doc.getDocumentElement());
        
        if (result == null) {
            fail("Failed to get result");
        }
        
        if (!result.equals(expected)) {
            fail("Result not expected: "+result);
        }
    }
    
    @Test
    public void testEvaluateDOMSource() {
        String value="hello";
        String xml="<ns1:mydoc xmlns:ns1=\"http://www.mynamespace\" ><ns1:field>"+value+"</ns1:field></ns1:mydoc>";
        
        XPathExpressionEvaluator evaluator=new XPathExpressionEvaluator();
        
        evaluator.setExpression("/ns2:mydoc/ns2:field/text()");
        
        evaluator.getNamespaces().put("ns2", "http://www.mynamespace");
        
        try {
            evaluator.init();
        } catch(Exception e) {
            fail("Failed to initialize: "+e);
        }
        
        org.w3c.dom.Document doc=null;
        
        try {
            javax.xml.parsers.DocumentBuilderFactory factory=
                    javax.xml.parsers.DocumentBuilderFactory.newInstance();
            
            factory.setNamespaceAware(true);
            
            javax.xml.parsers.DocumentBuilder builder=
                    factory.newDocumentBuilder();
            
            java.io.InputStream is=
                    new java.io.ByteArrayInputStream(xml.getBytes());
            
            doc = builder.parse(is);
                    
            is.close();
        } catch(Exception e) {
            fail("Failed to parse xml: "+e);
        }
        
        javax.xml.transform.dom.DOMSource source=new javax.xml.transform.dom.DOMSource(doc.getDocumentElement());
        
        String result=evaluator.evaluate(source);
        
        if (result == null) {
            fail("Failed to get result");
        }
        
        if (!result.equals(value)) {
            fail("Result not expected: "+result);
        }
    }
    
    @Test
    public void testEvaluateDOMSource1() {
        String xml="<ns1:mydoc xmlns:ns1=\"http://www.mynamespace\" ><ns1:field>hello</ns1:field></ns1:mydoc>";
        String expected="<?xml version=\"1.0\" encoding=\"UTF-8\"?><ns1:field xmlns:ns1=\"http://www.mynamespace\">hello</ns1:field>";
        
        XPathExpressionEvaluator evaluator=new XPathExpressionEvaluator();
        
        evaluator.setExpression("/ns2:mydoc/ns2:field");
        
        evaluator.getNamespaces().put("ns2", "http://www.mynamespace");
        
        try {
            evaluator.init();
        } catch(Exception e) {
            fail("Failed to initialize: "+e);
        }
        
        org.w3c.dom.Document doc=null;
        
        try {
            javax.xml.parsers.DocumentBuilderFactory factory=
                    javax.xml.parsers.DocumentBuilderFactory.newInstance();
            
            factory.setNamespaceAware(true);
            
            javax.xml.parsers.DocumentBuilder builder=
                    factory.newDocumentBuilder();
            
            java.io.InputStream is=
                    new java.io.ByteArrayInputStream(xml.getBytes());
            
            doc = builder.parse(is);
                    
            is.close();
        } catch(Exception e) {
            fail("Failed to parse xml: "+e);
        }
        
        javax.xml.transform.dom.DOMSource source=new javax.xml.transform.dom.DOMSource(doc.getDocumentElement());
        
        String result=evaluator.evaluate(source);
        
        if (result == null) {
            fail("Failed to get result");
        }
        
        if (!result.equals(expected)) {
            fail("Result not expected: "+result);
        }
    }
    
    @Test
    public void testEvaluateDOMSource2() {
        String value="Fred";
        String xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?><orders:submitOrder xmlns:orders=\"urn:switchyard-quickstart-demo:orders:1.0\">            <order>                <orderId>PO-19838-XYZ</orderId>                <itemId>BUTTER</itemId>                <quantity>10</quantity>                <customer>Fred</customer>            </order>        </orders:submitOrder>";
        
        XPathExpressionEvaluator evaluator=new XPathExpressionEvaluator();
        
        evaluator.setExpression("/orders:submitOrder/order/customer/text()");
        
        evaluator.getNamespaces().put("orders", "urn:switchyard-quickstart-demo:orders:1.0");
        
        try {
            evaluator.init();
        } catch(Exception e) {
            fail("Failed to initialize: "+e);
        }
        
        org.w3c.dom.Document doc=null;
        
        try {
            javax.xml.parsers.DocumentBuilderFactory factory=
                    javax.xml.parsers.DocumentBuilderFactory.newInstance();
            
            factory.setNamespaceAware(true);
            
            javax.xml.parsers.DocumentBuilder builder=
                    factory.newDocumentBuilder();
            
            java.io.InputStream is=
                    new java.io.ByteArrayInputStream(xml.getBytes());
            
            doc = builder.parse(is);
                    
            is.close();
        } catch(Exception e) {
            fail("Failed to parse xml: "+e);
        }
        
        javax.xml.transform.dom.DOMSource source=new javax.xml.transform.dom.DOMSource(doc.getDocumentElement());
        
        String result=evaluator.evaluate(source);
        
        if (result == null) {
            fail("Failed to get result");
        }
        
        if (!result.equals(value)) {
            fail("Result not expected: "+result);
        }
    }
    
    @Test
    public void testEvaluateDOMSource3() {
        String innerxml="<order>                <orderId>PO-19838-XYZ</orderId>                <itemId>BUTTER</itemId>                <quantity>10</quantity>                <customer>Fred</customer>            </order>";
        String xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?><orders:submitOrder xmlns:orders=\"urn:switchyard-quickstart-demo:orders:1.0\">            "+innerxml+"        </orders:submitOrder>";
        String expected="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+innerxml;
        
        XPathExpressionEvaluator evaluator=new XPathExpressionEvaluator();
        
        evaluator.setExpression("/orders:submitOrder/order");
        
        evaluator.getNamespaces().put("orders", "urn:switchyard-quickstart-demo:orders:1.0");
        
        try {
            evaluator.init();
        } catch(Exception e) {
            fail("Failed to initialize: "+e);
        }
        
        org.w3c.dom.Document doc=null;
        
        try {
            javax.xml.parsers.DocumentBuilderFactory factory=
                    javax.xml.parsers.DocumentBuilderFactory.newInstance();
            
            factory.setNamespaceAware(true);
            
            javax.xml.parsers.DocumentBuilder builder=
                    factory.newDocumentBuilder();
            
            java.io.InputStream is=
                    new java.io.ByteArrayInputStream(xml.getBytes());
            
            doc = builder.parse(is);
                    
            is.close();
        } catch(Exception e) {
            fail("Failed to parse xml: "+e);
        }
        
        javax.xml.transform.dom.DOMSource source=new javax.xml.transform.dom.DOMSource(doc.getDocumentElement());
        
        String result=evaluator.evaluate(source);
        
        if (result == null) {
            fail("Failed to get result");
        }
        
        if (!result.equals(expected)) {
            fail("Result not expected: "+result);
        }
    }
    
    @Test
    public void testEvaluateDOMSource2NamespaceUnaware() {
        String value="Fred";
        String xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?><orders:submitOrder xmlns:orders=\"urn:switchyard-quickstart-demo:orders:1.0\">            <order>                <orderId>PO-19838-XYZ</orderId>                <itemId>BUTTER</itemId>                <quantity>10</quantity>                <customer>Fred</customer>            </order>        </orders:submitOrder>";
        
        XPathExpressionEvaluator evaluator=new XPathExpressionEvaluator();
        
        evaluator.setExpression("/orders:submitOrder/order/customer/text()");
        
        evaluator.getNamespaces().put("orders", "urn:switchyard-quickstart-demo:orders:1.0");
        
        try {
            evaluator.init();
        } catch(Exception e) {
            fail("Failed to initialize: "+e);
        }
        
        org.w3c.dom.Document doc=null;
        
        try {
            javax.xml.parsers.DocumentBuilderFactory factory=
                    javax.xml.parsers.DocumentBuilderFactory.newInstance();
            
            javax.xml.parsers.DocumentBuilder builder=
                    factory.newDocumentBuilder();
            
            java.io.InputStream is=
                    new java.io.ByteArrayInputStream(xml.getBytes());
            
            doc = builder.parse(is);
                    
            is.close();
        } catch(Exception e) {
            fail("Failed to parse xml: "+e);
        }
        
        javax.xml.transform.dom.DOMSource source=new javax.xml.transform.dom.DOMSource(doc.getDocumentElement());
        
        String result=evaluator.evaluate(source);
        
        if (result == null) {
            fail("Failed to get result");
        }
        
        if (!result.equals(value)) {
            fail("Result not expected: "+result);
        }
    }
    
    @Test
    public void testEvaluateDOMSource3NamespaceUnaware() {
        String xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?><orders:submitOrder xmlns:orders=\"urn:switchyard-quickstart-demo:orders:1.0\">            <order>                <orderId>PO-19838-XYZ</orderId>                <itemId>BUTTER</itemId>                <quantity>10</quantity>                <customer>Fred</customer>            </order>        </orders:submitOrder>";
        String expected="<?xml version=\"1.0\" encoding=\"UTF-8\"?><customer>Fred</customer>";
        
        XPathExpressionEvaluator evaluator=new XPathExpressionEvaluator();
        
        evaluator.setExpression("/orders:submitOrder/order/customer");
        
        evaluator.getNamespaces().put("orders", "urn:switchyard-quickstart-demo:orders:1.0");
        
        try {
            evaluator.init();
        } catch(Exception e) {
            fail("Failed to initialize: "+e);
        }
        
        org.w3c.dom.Document doc=null;
        
        try {
            javax.xml.parsers.DocumentBuilderFactory factory=
                    javax.xml.parsers.DocumentBuilderFactory.newInstance();
            
            javax.xml.parsers.DocumentBuilder builder=
                    factory.newDocumentBuilder();
            
            java.io.InputStream is=
                    new java.io.ByteArrayInputStream(xml.getBytes());
            
            doc = builder.parse(is);
                    
            is.close();
        } catch(Exception e) {
            fail("Failed to parse xml: "+e);
        }
        
        javax.xml.transform.dom.DOMSource source=new javax.xml.transform.dom.DOMSource(doc.getDocumentElement());
        
        String result=evaluator.evaluate(source);
        
        if (result == null) {
            fail("Failed to get result");
        }
        
        if (!result.equals(expected)) {
            fail("Result not expected: "+result);
        }
    }
    
    @Test
    public void testEvaluateDOMSourceSOAPMessage() {
        String value="hello";
        String xml="<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body>" +
        		"<ns1:mydoc xmlns:ns1=\"http://www.mynamespace\" ><ns1:field>"+value+"</ns1:field></ns1:mydoc>" +
        		"</soap:Body></soap:Envelope>";
        
        XPathExpressionEvaluator evaluator=new XPathExpressionEvaluator();
        
        evaluator.setExpression("ns2:field/text()");
        
        evaluator.getNamespaces().put("ns2", "http://www.mynamespace");
        
        try {
            evaluator.init();
        } catch(Exception e) {
            fail("Failed to initialize: "+e);
        }
        
        org.w3c.dom.Node node=null;
        
        try {
        	java.io.InputStream is=new java.io.ByteArrayInputStream(xml.getBytes());
        	
        	SOAPMessage soapm=javax.xml.soap.MessageFactory.newInstance().createMessage(null, is);
        	
        	node = soapm.getSOAPBody().getFirstChild();
         	
            is.close();
        } catch(Exception e) {
            fail("Failed to parse xml: "+e);
        }
        
        javax.xml.transform.dom.DOMSource source=new javax.xml.transform.dom.DOMSource(node);
        
        String result=evaluator.evaluate(source);
        
        if (result == null) {
            fail("Failed to get result");
        }
        
        if (!result.equals(value)) {
            fail("Result not expected: "+result);
        }
    }
    
    @Test
    public void testEvaluateDOMSourceSOAPMessage1() {
        String expected="<?xml version=\"1.0\" encoding=\"UTF-8\"?><ns1:field xmlns:ns1=\"http://www.mynamespace\">hello</ns1:field>";
        String xml="<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body>" +
                "<ns1:mydoc xmlns:ns1=\"http://www.mynamespace\" ><ns1:field>hello</ns1:field></ns1:mydoc>" +
                "</soap:Body></soap:Envelope>";
        
        XPathExpressionEvaluator evaluator=new XPathExpressionEvaluator();
        
        evaluator.setExpression("ns2:field");
        
        evaluator.getNamespaces().put("ns2", "http://www.mynamespace");
        
        try {
            evaluator.init();
        } catch(Exception e) {
            fail("Failed to initialize: "+e);
        }
        
        org.w3c.dom.Node node=null;
        
        try {
            java.io.InputStream is=new java.io.ByteArrayInputStream(xml.getBytes());
            
            SOAPMessage soapm=javax.xml.soap.MessageFactory.newInstance().createMessage(null, is);
            
            node = soapm.getSOAPBody().getFirstChild();
            
            is.close();
        } catch(Exception e) {
            fail("Failed to parse xml: "+e);
        }
        
        javax.xml.transform.dom.DOMSource source=new javax.xml.transform.dom.DOMSource(node);
        
        String result=evaluator.evaluate(source);
        
        if (result == null) {
            fail("Failed to get result");
        }
        
        if (!result.equals(expected)) {
            fail("Result not expected: "+result);
        }
    }
    
    @Test
    public void testEvaluateDOMSourceSOAPMessage2() {
        String value="hello";
        String xml="<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body>" +
        		"<ns1:mydoc xmlns:ns1=\"http://www.mynamespace\" ><ns1:field>"+value+"</ns1:field></ns1:mydoc>" +
        		"</soap:Body></soap:Envelope>";
        
        XPathExpressionEvaluator evaluator=new XPathExpressionEvaluator();
        
        evaluator.setExpression("/ns2:mydoc/ns2:field/text()");
        
        evaluator.getNamespaces().put("ns2", "http://www.mynamespace");
        
        try {
            evaluator.init();
        } catch(Exception e) {
            fail("Failed to initialize: "+e);
        }
        
        org.w3c.dom.Node node=null;
        
        try {
        	java.io.InputStream is=new java.io.ByteArrayInputStream(xml.getBytes());
        	
        	SOAPMessage soapm=javax.xml.soap.MessageFactory.newInstance().createMessage(null, is);
        	
        	node = soapm.getSOAPBody().getFirstChild();
         	
            is.close();
        } catch(Exception e) {
            fail("Failed to parse xml: "+e);
        }
        
        javax.xml.transform.dom.DOMSource source=new javax.xml.transform.dom.DOMSource(node);
        
        String result=evaluator.evaluate(source);
        
        if (result == null) {
            fail("Failed to get result");
        }
        
        if (!result.equals(value)) {
            fail("Result not expected: "+result);
        }
    }
    
    @Test
    public void testEvaluateDOMSourceSOAPMessage3() {
        String expected="<?xml version=\"1.0\" encoding=\"UTF-8\"?><ns1:field xmlns:ns1=\"http://www.mynamespace\">hello</ns1:field>";
        String xml="<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body>" +
                "<ns1:mydoc xmlns:ns1=\"http://www.mynamespace\" ><ns1:field>hello</ns1:field></ns1:mydoc>" +
                "</soap:Body></soap:Envelope>";
        
        XPathExpressionEvaluator evaluator=new XPathExpressionEvaluator();
        
        evaluator.setExpression("/ns2:mydoc/ns2:field");
        
        evaluator.getNamespaces().put("ns2", "http://www.mynamespace");
        
        try {
            evaluator.init();
        } catch(Exception e) {
            fail("Failed to initialize: "+e);
        }
        
        org.w3c.dom.Node node=null;
        
        try {
            java.io.InputStream is=new java.io.ByteArrayInputStream(xml.getBytes());
            
            SOAPMessage soapm=javax.xml.soap.MessageFactory.newInstance().createMessage(null, is);
            
            node = soapm.getSOAPBody().getFirstChild();
            
            is.close();
        } catch(Exception e) {
            fail("Failed to parse xml: "+e);
        }
        
        javax.xml.transform.dom.DOMSource source=new javax.xml.transform.dom.DOMSource(node);
        
        String result=evaluator.evaluate(source);
        
        if (result == null) {
            fail("Failed to get result");
        }
        
        if (!result.equals(expected)) {
            fail("Result not expected: "+result);
        }
    }
    
    @Test
    public void testEvaluateJavaBeans() {
        String value="Fred";
        Order order=new Order();
        Customer cust=new Customer();
        cust.setName(value);
        order.setCustomer(cust);
        
        XPathExpressionEvaluator evaluator=new XPathExpressionEvaluator();
        
        evaluator.setExpression("customer/name");
        
        try {
            evaluator.init();
        } catch(Exception e) {
            fail("Failed to initialize: "+e);
        }
        
       String result=evaluator.evaluate(order);
        
        if (result == null) {
            fail("Failed to get result");
        }
        
        if (!result.equals(value)) {
            fail("Result not expected: "+result);
        }
    }

    public static class Customer {
        private String _name;
        
        public String getName() {
            return (_name);
        }
        
        public void setName(String name) {
            _name = name;
        }
    }
    
    public static class Order {
        
        private Customer _customer=null;
        
        public Customer getCustomer() {
            return (_customer);
        }
        
        public void setCustomer(Customer cust) {
            _customer = cust;
        }
    }
}
