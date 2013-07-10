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
package org.overlord.rtgov.tests.actmgmt.jbossas.beanservice;

import javax.inject.Inject;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.model.soa.RequestReceived;
import org.overlord.rtgov.activity.model.soa.RequestSent;
import org.overlord.rtgov.activity.model.soa.ResponseReceived;
import org.overlord.rtgov.activity.model.soa.ResponseSent;
import org.overlord.rtgov.tests.actmgmt.jbossas.beanservice.ExchangeInterceptor;
import org.overlord.rtgov.tests.actmgmt.jbossas.beanservice.InventoryService;
import org.overlord.rtgov.tests.actmgmt.jbossas.beanservice.InventoryServiceBean;
import org.overlord.rtgov.tests.actmgmt.jbossas.beanservice.Item;
import org.overlord.rtgov.tests.actmgmt.jbossas.beanservice.ItemNotFoundException;
import org.overlord.rtgov.tests.actmgmt.jbossas.beanservice.Order;
import org.overlord.rtgov.tests.actmgmt.jbossas.beanservice.OrderAck;
import org.overlord.rtgov.tests.actmgmt.jbossas.beanservice.OrderService;
import org.overlord.rtgov.tests.actmgmt.jbossas.beanservice.OrderServiceBean;
import org.overlord.rtgov.tests.actmgmt.jbossas.beanservice.TestActivityStore;
import org.overlord.rtgov.tests.actmgmt.jbossas.beanservice.Transformers;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class BeanServiceTest {

    private static final String ORDER_SERVICE_URL = "http://localhost:8080/quickstart-bean/OrderService";

    @Deployment
    public static WebArchive createDeployment() {
        String version=System.getProperty("rtgov.version");
        
        return ShrinkWrap.create(WebArchive.class)
            .addClass(InventoryService.class)
            .addClass(InventoryServiceBean.class)
            .addClass(Item.class)
            .addClass(ItemNotFoundException.class)
            .addClass(Order.class)
            .addClass(OrderAck.class)
            .addClass(OrderService.class)
            .addClass(OrderServiceBean.class)
            .addClass(Transformers.class)
            .addClass(ExchangeInterceptor.class)
            .addClass(TestActivityStore.class)
            .addAsResource("wsdl/OrderService.wsdl")
            .addAsResource("META-INF/switchyard.xml")
            .addAsResource(EmptyAsset.INSTANCE, "META-INF/beans.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsManifestResource("switchyard.xml")
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsLibraries(
                    DependencyResolvers
                    .use(MavenDependencyResolver.class)
                    .artifacts("org.overlord.rtgov.activity-management:activity:"+version,
                            "org.overlord.rtgov.common:rtgov-common:"+version,
                    		"org.overlord.rtgov.activity-management:activity-jee:"+version,
                            "org.overlord.rtgov.integration:rtgov-jbossas:"+version,
                            "org.overlord.rtgov.activity-management:collector-activity-server:"+version)
                    .resolveAsFiles());
    }

    @Inject
    org.overlord.rtgov.tests.actmgmt.jbossas.beanservice.OrderService _orderService;

    @Inject
    org.overlord.rtgov.activity.collector.CollectorContext _collectorContext;

    @Test
    public void submitOrderDirectNoTxn() {

        if (_orderService == null) {
            fail("Order Service has not been set");
        }
        
        TestActivityStore.reset();
        
        Order order=new Order();
        order.setOrderId("abc");
        order.setItemId("BUTTER");
        order.setQuantity(10);
        
        OrderAck ack=_orderService.submitOrder(order);
        
        if (ack == null) {
            fail("Acknowledgement is null");
        } else if (!ack.isAccepted()) {
            fail("Order was not accepted");
        }
        
        // Delay awaiting results
        try {
            Thread.sleep(1000);
        } catch(Exception e) {
            fail("Failed to wait for events: "+e);
        }
        
        // Check that store method only called once
        if (TestActivityStore.getStoreCount() != 1) {
            fail("Store count was not 1: "+TestActivityStore.getStoreCount());
        }
        
        // Check that the four expected activity units occurred
        if (TestActivityStore.getActivities().size() != 4) {
            fail("Activity count should be 4: "+TestActivityStore.getActivities().size());
        }
        
        // Check that each activity unit only has a single event
        for (ActivityUnit au : TestActivityStore.getActivities()) {
            if (au.getActivityTypes().size() != 1) {
                fail("Activity unit does not have single activity type: "+au.getActivityTypes().size());
            }
        }
        
        if (!(TestActivityStore.getActivities().get(0).getActivityTypes().get(0) instanceof RequestSent)) {
            fail("Expected 'RequestSent'");
        }
        
        if (!(TestActivityStore.getActivities().get(1).getActivityTypes().get(0) instanceof RequestReceived)) {
            fail("Expected 'RequestReceived'");
        }
        
        if (!(TestActivityStore.getActivities().get(2).getActivityTypes().get(0) instanceof ResponseSent)) {
            fail("Expected 'ResponseSent'");
        }
        
        if (!(TestActivityStore.getActivities().get(3).getActivityTypes().get(0) instanceof ResponseReceived)) {
            fail("Expected 'ResponseReceived'");
        }
    }
    
    @Test
    public void submitOrderDirectTxn() {

        if (_orderService == null) {
            fail("Order Service has not been set");
        }
        
        if (_collectorContext == null) {
            fail("Collector context not available");
        }
        
        TestActivityStore.reset();
        
        Order order=new Order();
        order.setOrderId("abc");
        order.setItemId("BUTTER");
        order.setQuantity(10);
        
        // Start a transaction
        try {
            _collectorContext.getTransactionManager().begin();
            
            if (_collectorContext.getTransactionManager().getTransaction() == null) {
                fail("Transaction was not started");
            }
        } catch (Exception e) {
            fail("Failed to begin transaction");
        }
        
        OrderAck ack=_orderService.submitOrder(order);
        
        try {
            _collectorContext.getTransactionManager().commit();
        } catch (Exception e) {
            fail("Failed to commit transaction");
        }
        
        if (ack == null) {
            fail("Acknowledgement is null");
        } else if (!ack.isAccepted()) {
            fail("Order was not accepted");
        }
        
        // Delay awaiting results
        try {
            Thread.sleep(1000);
        } catch(Exception e) {
            fail("Failed to wait for events: "+e);
        }
        
        // Check that store method only called once
        if (TestActivityStore.getStoreCount() != 1) {
            fail("Store count was not 1: "+TestActivityStore.getStoreCount());
        }
        
        // Check that the four expected activity units occurred
        if (TestActivityStore.getActivities().size() != 1) {
            fail("Activity count should be 1: "+TestActivityStore.getActivities().size());
        }
        
        ActivityUnit au=TestActivityStore.getActivities().get(0);
        
        // Check that single activity unit has four activity types
        if (au.getActivityTypes().size() != 4) {
            fail("Activity unit does not have 4 activity types: "+au.getActivityTypes().size());
        }
        
        if (!(au.getActivityTypes().get(0) instanceof RequestSent)) {
            fail("Expected 'RequestSent'");
        }
        
        if (!(au.getActivityTypes().get(1) instanceof RequestReceived)) {
            fail("Expected 'RequestReceived'");
        }
        
        if (!(au.getActivityTypes().get(2) instanceof ResponseSent)) {
            fail("Expected 'ResponseSent'");
        }
        
        if (!(au.getActivityTypes().get(3) instanceof ResponseReceived)) {
            fail("Expected 'ResponseReceived'");
        }
    }
    
    @Test
    public void submitOrderSOAPNoTxn() {

        if (_orderService == null) {
            fail("Order Service has not been set");
        }
        
        TestActivityStore.reset();

        try {
            SOAPConnectionFactory factory=SOAPConnectionFactory.newInstance();
            SOAPConnection con=factory.createConnection();
            
            java.net.URL url=new java.net.URL(ORDER_SERVICE_URL);
            
            String mesg="<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
                        "   <soap:Body>"+
                        "       <orders:submitOrder xmlns:orders=\"urn:switchyard-quickstart:bean-service:1.0\">"+
                        "            <order>"+
                        "                <orderId>PO-19838-XYZ</orderId>"+
                        "                <itemId>BUTTER</itemId>"+
                        "                <quantity>200</quantity>"+
                        "            </order>"+
                        "        </orders:submitOrder>"+
                        "    </soap:Body>"+
                        "</soap:Envelope>";
            
            java.io.InputStream is=new java.io.ByteArrayInputStream(mesg.getBytes());
            
            SOAPMessage request=MessageFactory.newInstance().createMessage(null, is);
            
            is.close();
            
            SOAPMessage response=con.call(request, url);

            java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
            
            response.writeTo(baos);
            
            String resp=baos.toString();

            baos.close();
            
            if (!resp.contains("<accepted>true</accepted>")) {
                fail("Order was not accepted: "+resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to invoke service via SOAP: "+e);
        }
        
        // Delay awaiting results
        try {
            Thread.sleep(1000);
        } catch(Exception e) {
            fail("Failed to wait for events: "+e);
        }
        
        // Check that store method only called once
        if (TestActivityStore.getStoreCount() != 1) {
            fail("Store count was not 1: "+TestActivityStore.getStoreCount());
        }
        
        // Check that the eight expected activity units occurred
        if (TestActivityStore.getActivities().size() != 8) {
            fail("Activity count should be 8: "+TestActivityStore.getActivities().size());
        }
        
        // Check that each activity unit only has a single event
        for (ActivityUnit au : TestActivityStore.getActivities()) {
            if (au.getActivityTypes().size() != 1) {
                fail("Activity unit does not have single activity type: "+au.getActivityTypes().size());
            }
        }
        
        if (!(TestActivityStore.getActivities().get(0).getActivityTypes().get(0) instanceof RequestSent)) {
            fail("Expected 'RequestSent'");
        }
        
        if (!(TestActivityStore.getActivities().get(1).getActivityTypes().get(0) instanceof RequestReceived)) {
            fail("Expected 'RequestReceived'");
        }
        
        if (!(TestActivityStore.getActivities().get(2).getActivityTypes().get(0) instanceof RequestSent)) {
            fail("Expected 2nd 'RequestSent'");
        }
        
        if (!(TestActivityStore.getActivities().get(3).getActivityTypes().get(0) instanceof RequestReceived)) {
            fail("Expected 2nd 'RequestReceived'");
        }
        
        if (!(TestActivityStore.getActivities().get(4).getActivityTypes().get(0) instanceof ResponseSent)) {
            fail("Expected 'ResponseSent'");
        }
        
        if (!(TestActivityStore.getActivities().get(5).getActivityTypes().get(0) instanceof ResponseReceived)) {
            fail("Expected 'ResponseReceived'");
        }
        
        if (!(TestActivityStore.getActivities().get(6).getActivityTypes().get(0) instanceof ResponseSent)) {
            fail("Expected 2nd 'ResponseSent'");
        }
        
        if (!(TestActivityStore.getActivities().get(7).getActivityTypes().get(0) instanceof ResponseReceived)) {
            fail("Expected 2nd 'ResponseReceived'");
        }
    }
}
