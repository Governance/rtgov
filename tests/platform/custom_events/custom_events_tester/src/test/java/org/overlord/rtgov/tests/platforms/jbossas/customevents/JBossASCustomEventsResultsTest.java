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
package org.overlord.rtgov.tests.platforms.jbossas.customevents;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;

import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class JBossASCustomEventsResultsTest {

    private static final String ORDER_SERVICE_URL = "http://127.0.0.1:8080/demo-orders/OrderService";
    
    private static final ObjectMapper MAPPER=new ObjectMapper();

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
    
    @Deployment(name="epn", order=3)
    public static WebArchive createDeployment3() {
        String version=System.getProperty("rtgov.version");

        java.io.File archiveFile=Maven.resolver().resolve("org.overlord.rtgov.tests:tests-custom-events-epn:war:"+version)
                .withoutTransitivity().asSingleFile();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class, archiveFile);
    }
    
    @Deployment(name="acs", order=4)
    public static WebArchive createDeployment4() {
        String version=System.getProperty("rtgov.version");

        java.io.File archiveFile=Maven.resolver().resolve("org.overlord.rtgov.tests:tests-custom-events-acs:war:"+version)
                .withoutTransitivity().asSingleFile();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class, 
                copyToTmpFile(archiveFile,"tests-custom-events-acs.war"));
    }
    
    @Deployment(name="av", order=5)
    public static WebArchive createDeployment5() {
        String version=System.getProperty("rtgov.version");

        java.io.File archiveFile=Maven.resolver().resolve("org.overlord.rtgov.tests:tests-custom-events-av:war:"+version)
                .withoutTransitivity().asSingleFile();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class, archiveFile);
    }
    
    @Deployment(name="monitor", order=6)
    public static WebArchive createDeployment6() {
        String version=System.getProperty("rtgov.version");

        java.io.File archiveFile=Maven.resolver().resolve("org.overlord.rtgov.tests:tests-custom-events-monitor:war:"+version)
                .withoutTransitivity().asSingleFile();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class,
                        copyToTmpFile(archiveFile,"custom-events-monitor.war"));
    }
    
    private static java.io.File copyToTmpFile(java.io.File source, String filename) {
        String tmpdir=System.getProperty("java.io.tmpdir");
        java.io.File dir=new java.io.File(tmpdir+java.io.File.separator+"rtgovtests"+System.currentTimeMillis());
        
        dir.mkdir();
        
        dir.deleteOnExit();
        
        java.io.File ret=new java.io.File(dir, filename);
        ret.deleteOnExit();
        
        // Copy contents to the tmp file
        try {
            java.io.FileInputStream fis=new java.io.FileInputStream(source);
            java.io.FileOutputStream fos=new java.io.FileOutputStream(ret);
            
            byte[] b=new byte[10240];
            int len=0;
            
            while ((len=fis.read(b)) > 0) {
                fos.write(b, 0, len);
            }
            
            fis.close();
            
            fos.flush();
            fos.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to copy file '"+filename+"': "+e);
        }
        
        return(ret);
    }

    /**
     * This method deserializes the events into a list of hashmaps. The
     * actual objects are not deserialized, as this would require the
     * domain objects to be included in all deployments, which would
     * make verifying classloading/isolation difficult.
     * 
     * @return The list of objects representing events
     * @throws Exception Failed to deserialize the events
     */
    protected java.util.List<?> getEvents() throws Exception {
        java.util.List<?> ret=null;
         
        URL getUrl = new URL("http://localhost:8080/custom-events-monitor/monitor/events");
        HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
        connection.setRequestMethod("GET");
        System.out.println("Content-Type: " + connection.getContentType());

        java.io.InputStream is=connection.getInputStream();
        
        ret = MAPPER.readValue(is, java.util.List.class);
       
        return (ret);
    }

    /**
     * This method deserializes the events into a list of hashmaps. The
     * actual objects are not deserialized, as this would require the
     * domain objects to be included in all deployments, which would
     * make verifying classloading/isolation difficult.
     * 
     * @return The list of objects representing events
     * @throws Exception Failed to deserialize the events
     */
    protected java.util.List<?> getACSEvents() throws Exception {
        java.util.List<?> ret=null;
         
        URL getUrl = new URL("http://localhost:8080/custom-events-monitor/monitor/acsresults");
        HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
        connection.setRequestMethod("GET");
        System.out.println("Content-Type: " + connection.getContentType());

        java.io.InputStream is=connection.getInputStream();
        
        ret = MAPPER.readValue(is, java.util.List.class);

        System.out.println("ACS EVENTS="+ret);
        
        return (ret);
    }

    @Test
    @OperateOnDeployment(value="orders-app")
    public void testActivityEventsResults() {
        
        try {
            // Reset event list
            getEvents();
            
            java.util.List<?> acsresults=getACSEvents();
            
            int acsSize=acsresults.size();
            
            SOAPConnectionFactory factory=SOAPConnectionFactory.newInstance();
            SOAPConnection con=factory.createConnection();
            
            java.net.URL url=new java.net.URL(ORDER_SERVICE_URL);
            
            String mesg="<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
                        "   <soap:Body>"+
                        "       <orders:submitOrder xmlns:orders=\"urn:switchyard-quickstart-demo:orders:1.0\">"+
                        "            <order>"+
                        "                <orderId>PO-13739-ABC</orderId>"+
                        "                <itemId>JAM</itemId>"+
                        "                <quantity>50</quantity>"+
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
            
            String resp=baos.toString();

            baos.close();
            
            if (!resp.contains("<accepted>true</accepted>")) {
                fail("Order was not accepted: "+resp);
            }
            
            // Wait for events to propagate
            Thread.sleep(5000);
            
            java.util.List<?> events=getEvents();
            
            if (events == null) {
                fail("No events returned");
            }
            
            // Should be 10 processed events and 1 result
            if (events.size() != 11) {
                fail("11 events expected, but got: "+events.size());
            }
            
            acsresults = getACSEvents();
            
            if (acsresults == null) {
                fail("No acsresults returned");
            }
            
            if (acsresults.size() != acsSize+1) {
                fail("Size of acs results should be 1 more: was "
                                +acsSize+" now "+acsresults.size());
            }
            
        } catch (Exception e) {
            fail("Failed to invoke service via SOAP: "+e);
        }
    }

    @Test
    @OperateOnDeployment(value="orders-app")
    public void testActivityEventsProcessed() {
        
        try {
            
            // Pre-request events, to initialize the rest service and
            // reset the event list
            getEvents();
            
            java.util.List<?> acsresults=getACSEvents();
            
            int acsSize=acsresults.size();
            
            SOAPConnectionFactory factory=SOAPConnectionFactory.newInstance();
            SOAPConnection con=factory.createConnection();
            
            java.net.URL url=new java.net.URL(ORDER_SERVICE_URL);
            
            String mesg="<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
                        "   <soap:Body>"+
                        "       <orders:submitOrder xmlns:orders=\"urn:switchyard-quickstart-demo:orders:1.0\">"+
                        "            <order>"+
                        "                <orderId>PO-19838-XYZ</orderId>"+
                        "                <itemId>BUTTER</itemId>"+
                        "                <quantity>200</quantity>"+
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
            
            String resp=baos.toString();

            baos.close();
            
            if (!resp.contains("<accepted>true</accepted>")) {
                fail("Order was not accepted: "+resp);
            }
            
            // Wait for events to propagate
            Thread.sleep(5000);
            
            java.util.List<?> events=getEvents();
            
            if (events == null) {
                fail("No events returned");
            }
            
            if (events.size() != 10) {
                fail("10 events expected, but got: "+events.size());
            }
            
            acsresults = getACSEvents();
            
            if (acsresults == null) {
                fail("No acsresults returned");
            }
            
            if (acsresults.size() != acsSize) {
                fail("Size if acs results should not have changed: was "
                                +acsSize+" now "+acsresults.size());
            }
            
        } catch (Exception e) {
            fail("Failed to invoke service via SOAP: "+e);
        }
    }
    
}
