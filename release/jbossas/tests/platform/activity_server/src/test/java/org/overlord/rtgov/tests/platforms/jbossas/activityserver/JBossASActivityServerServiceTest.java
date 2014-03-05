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
package org.overlord.rtgov.tests.platforms.jbossas.activityserver;

import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.soa.RequestReceived;
import org.overlord.rtgov.activity.model.soa.ResponseSent;
import org.overlord.rtgov.activity.server.QuerySpec;
import org.overlord.rtgov.activity.util.ActivityUtil;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class JBossASActivityServerServiceTest {

    private static final String ORDER_SERVICE_URL = "http://127.0.0.1:8080/demo-orders/OrderService";
    
    @Deployment(name="orders-app", order=1)
    public static JavaArchive createDeployment1() {
        String version=System.getProperty("rtgov.version");

        java.io.File archiveFile=Maven.resolver().resolve("org.overlord.rtgov.samples.jbossas.ordermgmt:samples-jbossas-ordermgmt-app:"+version)
                .withoutTransitivity().asSingleFile();
        
        return ShrinkWrap.createFromZipFile(JavaArchive.class, archiveFile);
    }
    
    @Deployment(name="orders-ip", order=2)
    public static WebArchive createDeployment2() {
        String version=System.getProperty("rtgov.version");

        java.io.File archiveFile=Maven.resolver().resolve("org.overlord.rtgov.samples.jbossas.ordermgmt:samples-jbossas-ordermgmt-ip:war:"+version)
                .withoutTransitivity().asSingleFile();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class, archiveFile);
    }
    
    @Test @OperateOnDeployment("orders-app")
    public void testQueryActivityServer() {
        
        try {
            SOAPConnectionFactory factory=SOAPConnectionFactory.newInstance();
            SOAPConnection con=factory.createConnection();
            
            java.net.URL url=new java.net.URL(ORDER_SERVICE_URL);
            
            String mesg="<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
                        "   <soap:Body>"+
                        "       <orders:submitOrder xmlns:orders=\"urn:switchyard-quickstart-demo:orders:1.0\">"+
                        "            <order>"+
                        "                <orderId>1</orderId>"+
                        "                <itemId>BUTTER</itemId>"+
                        "                <quantity>100</quantity>"+
                        "                <customer>Fred</customer>"+
                        "            </order>"+
                        "        </orders:submitOrder>"+
                        "    </soap:Body>"+
                        "</soap:Envelope>";
            
            java.util.List<ActivityType> initialActivities = getActivityEvents();
            
            java.io.InputStream is=new java.io.ByteArrayInputStream(mesg.getBytes());
            
            SOAPMessage request=MessageFactory.newInstance().createMessage(null, is);
            
            is.close();
            
            SOAPMessage response=con.call(request, url);

            java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
            
            response.writeTo(baos);
            
            baos.close();
            
            // Wait for events to propagate
            Thread.sleep(4000);
            
            java.util.List<ActivityType> acts = getActivityEvents();
            
            if (acts == null) {
                fail("Activity event list is null");
            }
            
            System.out.println("LIST SIZE="+acts.size());
            
            System.out.println("LIST="+acts);
            
            if ((acts.size()-initialActivities.size()) != 12) {
                fail("Expecting 12 activity events: "+(acts.size()-initialActivities.size()));
            }
            
            // RTGOV-256 Check that first activity type has header value extracted as a property
            ActivityType at=acts.get(initialActivities.size());
            
            if (!at.getProperties().containsKey("contentType")) {
                fail("Property 'contentType' not found");
            }
            
            if (!at.getProperties().get("contentType").equals("{urn:switchyard-quickstart-demo:orders:1.0}submitOrder")) {
                fail("Incorrect content type, expecting '{urn:switchyard-quickstart-demo:orders:1.0}submitOrder' but got: "
                                +at.getProperties().get("contentType"));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to invoke service: "+e);
        }
    }

    @Test @OperateOnDeployment("orders-app")
    public void testQueryActivityServerFaultResponse() {
        
        try {
            SOAPConnectionFactory factory=SOAPConnectionFactory.newInstance();
            SOAPConnection con=factory.createConnection();
            
            java.net.URL url=new java.net.URL(ORDER_SERVICE_URL);
            
            String mesg="<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
                        "   <soap:Body>"+
                        "       <orders:submitOrder xmlns:orders=\"urn:switchyard-quickstart-demo:orders:1.0\">"+
                        "            <order>"+
                        "                <orderId>1</orderId>"+
                        "                <itemId>Laptop</itemId>"+
                        "                <quantity>100</quantity>"+
                        "                <customer>Fred</customer>"+
                        "            </order>"+
                        "        </orders:submitOrder>"+
                        "    </soap:Body>"+
                        "</soap:Envelope>";
            
            java.util.List<ActivityType> initialActivities = getActivityEvents();
            
            java.io.InputStream is=new java.io.ByteArrayInputStream(mesg.getBytes());
            
            SOAPMessage request=MessageFactory.newInstance().createMessage(null, is);
            
            is.close();
            
            SOAPMessage response=con.call(request, url);

            java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
            
            response.writeTo(baos);
            
            baos.close();
            
            // Wait for events to propagate
            Thread.sleep(4000);
            
            java.util.List<ActivityType> acts = getActivityEvents();
            
            if (acts == null) {
                fail("Activity event list is null");
            }
            
            System.out.println("LIST SIZE="+acts.size());
            
            System.out.println("LIST="+acts);
            
            if ((acts.size()-initialActivities.size()) != 7) {
                fail("Expecting 7 activity events: "+(acts.size()-initialActivities.size()));
            }
            
            ActivityType at1=acts.get(initialActivities.size()+4);
            
             if ((at1 instanceof ResponseSent) == false) {
                fail("Expecting a 'response sent' event");
            }
            
            ResponseSent resp=(ResponseSent)at1;
            
            if (resp.getMessageType() == null) {
                fail("Message type should not be null");
            }
            
            if (resp.getFault() == null) {
                fail("Fault should not be null");
            }
            
            if (!resp.getFault().equals("ItemNotFound")) {
                fail("Fault should be 'ItemNotFound': "+resp.getFault());
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to invoke service: "+e);
        }
    }


    @Test @OperateOnDeployment("orders-app")
    public void testQueryActivityServerInvalidRequestStructure() {
        
        try {
            SOAPConnectionFactory factory=SOAPConnectionFactory.newInstance();
            SOAPConnection con=factory.createConnection();
            
            java.net.URL url=new java.net.URL(ORDER_SERVICE_URL);
            
            String mesg="<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
                        "   <soap:Body>"+
                        "       <orders:submitOrder xmlns:orders=\"urn:switchyard-quickstart-demo:orders:1.0\">"+
                        "            <order>"+
                        "                <orderId>1</orderId>"+
                        "                <itemId>BUTTER</itemId>"+
                        "                <quantity>100</quantity>"+
                        "                <customerx>Fred</customerx>"+
                        "            </order>"+
                        "        </orders:submitOrder>"+
                        "    </soap:Body>"+
                        "</soap:Envelope>";
            
            java.util.List<ActivityType> initialActivities = getActivityEvents();
            
            java.io.InputStream is=new java.io.ByteArrayInputStream(mesg.getBytes());
            
            SOAPMessage request=MessageFactory.newInstance().createMessage(null, is);
            
            is.close();
            
            SOAPMessage response=con.call(request, url);

            java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
            
            response.writeTo(baos);
            
            baos.close();
            
            // Wait for events to propagate
            Thread.sleep(4000);
            
            java.util.List<ActivityType> acts = getActivityEvents();
            
            if (acts == null) {
                fail("Activity event list is null");
            }
            
            System.out.println("LIST SIZE="+acts.size());
            
            System.out.println("LIST="+acts);
            
            if ((acts.size()-initialActivities.size()) != 2) {
                fail("Expecting 2 activity events: "+(acts.size()-initialActivities.size()));
            }
            
            // RTGOV-262 Check response has replyTo id
            ActivityType at1=acts.get(initialActivities.size());
            ActivityType at2=acts.get(initialActivities.size()+1);
            
            if ((at1 instanceof RequestReceived) == false) {
                fail("Expecting a 'request received' event");
            }
            
            if ((at2 instanceof ResponseSent) == false) {
                fail("Expecting a 'response sent' event");
            }
            
            RequestReceived req=(RequestReceived)at1;
            ResponseSent resp=(ResponseSent)at2;
            
            if (resp.getReplyToId() == null) {
                fail("Response 'replyTo' id not set");
            }

            if (!req.getMessageId().equals(resp.getReplyToId())) {
                fail("Response 'replyTo' id not same as request message id");
            }
            
            if (resp.getMessageType() != null) {
                fail("Message type of fault response, for validation error, should be null: "+resp.getMessageType());
            }
            
            if (resp.getFault() == null) {
                fail("Fault for invalid response should not be null");
            }
            
            if (!resp.getFault().equals("ERROR")) {
                fail("Fault for invalid response should be 'ERROR': "+resp.getFault());
            }
            
            if (resp.getContent() == null) {
                fail("Fault response should have message content representing a description of the fault");
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to invoke service: "+e);
        }
    }

    @Test @OperateOnDeployment("orders-app")
    public void testInvalidQuery() {
        
        try {            
            sendRequest("NotASelect");

            fail("Server should not return a response");
            
        } catch (java.io.IOException ioe) {
            // Ignore as expected to fail
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to invoke service: "+e);
        }
    }

    public static byte[] sendRequest(String expression) throws Exception {
        Authenticator.setDefault(new DefaultAuthenticator());
        
        QuerySpec query=new QuerySpec().setFormat("jpql").setExpression(expression);
        
        URL queryUrl = new URL("http://localhost:8080/overlord-rtgov/activity/query");
        
        HttpURLConnection connection = (HttpURLConnection) queryUrl.openConnection();
        connection.setRequestMethod("POST");

        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setAllowUserInteraction(false);
        connection.setRequestProperty("Content-Type",
                    "application/json");

        java.io.OutputStream os=connection.getOutputStream();
        
        byte[] b=ActivityUtil.serializeQuerySpec(query);
        os.write(b);
        
        os.flush();
        os.close();
        
        java.io.InputStream is=connection.getInputStream();

        java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
        
        while (is.available() > 0) {
            b = new byte[is.available()];
            is.read(b);
            baos.write(b);
        }
        
        is.close();
        baos.close();
        
        System.out.println(">>>> JSON="+new String(baos.toByteArray()));
        
        return (b);
    }
    
    public static java.util.List<ActivityType> getActivityEvents() throws Exception {
        byte[] b=sendRequest("SELECT at FROM ActivityType at");
        
        return (ActivityUtil.deserializeActivityTypeList(b));
    }
    
    static class DefaultAuthenticator extends Authenticator {

        public PasswordAuthentication getPasswordAuthentication () {
            return new PasswordAuthentication ("admin", "overlord".toCharArray());
        }
    }
}