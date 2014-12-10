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
package org.overlord.rtgov.tests.platforms.jbossas.calltrace;

import java.net.HttpURLConnection;
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
import org.overlord.rtgov.call.trace.model.Call;
import org.overlord.rtgov.call.trace.model.CallTrace;
import org.overlord.rtgov.call.trace.util.CallTraceUtil;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class JBossASCallTraceServiceTest {

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
    public void testCallTrace() {
        String id="ID"+System.currentTimeMillis(); //UUID.randomUUID().toString();
        
        try {
            SOAPConnectionFactory factory=SOAPConnectionFactory.newInstance();
            SOAPConnection con=factory.createConnection();
            
            java.net.URL url=new java.net.URL(ORDER_SERVICE_URL);
            
            String mesg="<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
                        "   <soap:Body>"+
                        "       <orders:submitOrder xmlns:orders=\"urn:switchyard-quickstart-demo:orders:1.0\">"+
                        "            <order>"+
                        "                <orderId>"+id+"</orderId>"+
                        "                <itemId>BUTTER</itemId>"+
                        "                <quantity>100</quantity>"+
                        "                <customer>Fred</customer>"+
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
            
            baos.close();
            
            // Wait for events to propagate
            Thread.sleep(4000);
            
            String ct = getCallTrace(id);
            
            if (ct == null) {
                fail("Call trace is null");
            }
            
            CallTrace trace=CallTraceUtil.deserializeCallTrace(ct.getBytes());
            
            if (trace == null) {
                fail("Failed to deserialize call trace");
            }
            
            if (trace.getTasks().size() != 1) {
                fail("Should only be one top level task: "+trace.getTasks().size());
            }
            
            if (!(trace.getTasks().get(0) instanceof Call)) {
                fail("Top level task should be a Call: "+trace.getTasks().get(0));
            }
            
            Call call=(Call)trace.getTasks().get(0);
            
            if (call.getTasks().size() != 2) {
                fail("Should be 2 child tasks: "+call.getTasks().size());
            }
            
            // Verify that the top level call had two inner calls

            if (!(call.getTasks().get(0) instanceof Call)) {
                fail("Child task 0 should be a Call: "+call.getTasks().get(0));
            }
            
            if (!(call.getTasks().get(1) instanceof Call)) {
                fail("Child task 1 should be a Call: "+call.getTasks().get(1));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to invoke service: "+e);
        }
    }

    @Test @OperateOnDeployment("orders-app")
    @org.junit.Ignore("RTGOV-459 - using Elasticsearch activity store caused this test to stop working")
    public void testCallTraceWithException() {
        String id="ID"+System.currentTimeMillis(); //UUID.randomUUID().toString();
        
        try {
            SOAPConnectionFactory factory=SOAPConnectionFactory.newInstance();
            SOAPConnection con=factory.createConnection();
            
            java.net.URL url=new java.net.URL(ORDER_SERVICE_URL);
            
            String mesg="<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
                        "   <soap:Body>"+
                        "       <orders:submitOrder xmlns:orders=\"urn:switchyard-quickstart-demo:orders:1.0\">"+
                        "            <order>"+
                        "                <orderId>"+id+"</orderId>"+
                        "                <itemId>ERROR</itemId>"+
                        "                <quantity>100</quantity>"+
                        "                <customer>Fred</customer>"+
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
            
            baos.close();
            
            // Wait for events to propagate
            Thread.sleep(4000);
            
            String ct = getCallTrace(id);
            
            if (ct == null) {
                fail("Call trace is null");
            }
            
            CallTrace trace=CallTraceUtil.deserializeCallTrace(ct.getBytes());
            
            if (trace == null) {
                fail("Failed to deserialize call trace");
            }
            
            if (trace.getTasks().size() != 1) {
                fail("Should only be one top level task: "+trace.getTasks().size());
            }
            
            if (!(trace.getTasks().get(0) instanceof Call)) {
                fail("Top level task should be a Call: "+trace.getTasks().get(0));
            }
            
            Call call=(Call)trace.getTasks().get(0);
            
            if (call.getTasks().size() != 1) {
                fail("Should be 1 child tasks: "+call.getTasks().size());
            }
            
            // Verify that the top level call had one inner call

            if (!(call.getTasks().get(0) instanceof Call)) {
                fail("Child task 0 should be a Call: "+call.getTasks().get(0));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to invoke service: "+e);
        }
    }

    public static String getCallTrace(String id) throws Exception {
        
        URL getUrl = new URL("http://localhost:8080/overlord-rtgov/call/trace/instance?value="+id);
        HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
        connection.setRequestMethod("GET");

        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setAllowUserInteraction(false);
        connection.setRequestProperty("Content-Type",
                    "application/json");
        
        initAuth(connection);

        java.io.InputStream is=connection.getInputStream();
        
        byte[] b=new byte[is.available()];
        is.read(b);
        
        String result=new String(b);
        
        is.close();
        
        System.out.println("JSON RESULT="+result);
        
        return (result);
    }
    
    protected static void initAuth(HttpURLConnection connection) {
        String userPassword = "admin:admin";
        String encoding = org.apache.commons.codec.binary.Base64.encodeBase64String(userPassword.getBytes());
        
        StringBuffer buf=new StringBuffer(encoding);
        
        for (int i=0; i < buf.length(); i++) {
            if (Character.isWhitespace(buf.charAt(i))) {
                buf.deleteCharAt(i);
                i--;
            }
        }
        
        connection.setRequestProperty("Authorization", "Basic " + buf.toString());
    }
}