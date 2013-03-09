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
package org.overlord.rtgov.activity.processor;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.activity.processor.XPathExpressionEvaluator;

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
        
        if (!result.equals(value)) {
            fail("Result not expected: "+result);
        }
    }
    
    @Test
    public void testEvaluateDOMSource() {
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
    public void testEvaluateDOMSource2() {
        String value="Fred";
        String xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?><orders:submitOrder xmlns:orders=\"urn:switchyard-quickstart-demo:orders:1.0\">            <order>                <orderId>PO-19838-XYZ</orderId>                <itemId>BUTTER</itemId>                <quantity>10</quantity>                <customer>Fred</customer>            </order>        </orders:submitOrder>";
        
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
