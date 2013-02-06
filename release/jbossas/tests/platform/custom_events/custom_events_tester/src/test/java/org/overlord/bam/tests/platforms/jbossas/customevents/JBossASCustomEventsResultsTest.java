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
package org.overlord.bam.tests.platforms.jbossas.customevents;

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
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class JBossASCustomEventsResultsTest {

    private static final String ORDER_SERVICE_URL = "http://127.0.0.1:8080/demo-orders/OrderService";
    
    private static final ObjectMapper MAPPER=new ObjectMapper();

    @Deployment(name="overlord-bam", order=1)
    public static WebArchive createDeployment1() {
        String version=System.getProperty("bam.version");

        java.io.File[] archiveFiles=DependencyResolvers.use(MavenDependencyResolver.class)
                .artifacts("org.overlord.bam.release.jbossas:overlord-bam:war:"+version)
                .resolveAsFiles();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class,
                copyToTmpFile(archiveFiles[0],"overlord-bam.war"));
    }
    
    @Deployment(name="orders-app", order=2)
    public static WebArchive createDeployment2() {
        String version=System.getProperty("bam.version");

        java.io.File[] archiveFiles=DependencyResolvers.use(MavenDependencyResolver.class)
                .artifacts("org.overlord.bam.samples.jbossas.ordermgmt:samples-jbossas-ordermgmt-app:war:"+version)
                .resolveAsFiles();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class, archiveFiles[0]);
    }
    
    @Deployment(name="orders-ip", order=3)
    public static WebArchive createDeployment3() {
        String version=System.getProperty("bam.version");

        java.io.File[] archiveFiles=DependencyResolvers.use(MavenDependencyResolver.class)
                .artifacts("org.overlord.bam.samples.jbossas.ordermgmt:samples-jbossas-ordermgmt-ip:war:"+version)
                .resolveAsFiles();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class, archiveFiles[0]);
    }
    
    @Deployment(name="epn", order=4)
    public static WebArchive createDeployment4() {
        String version=System.getProperty("bam.version");

        java.io.File[] archiveFiles=DependencyResolvers.use(MavenDependencyResolver.class)
                .artifacts("org.overlord.bam.release.jbossas.tests.platform.custom_events:tests-jbossas-custom-events-epn:war:"+version)
                .resolveAsFiles();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class, archiveFiles[0]);
    }
    
    @Deployment(name="acs", order=5)
    public static WebArchive createDeployment5() {
        String version=System.getProperty("bam.version");

        java.io.File[] archiveFiles=DependencyResolvers.use(MavenDependencyResolver.class)
                .artifacts("org.overlord.bam.release.jbossas.tests.platform.custom_events:tests-jbossas-custom-events-acs:war:"+version)
                .resolveAsFiles();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class, 
                copyToTmpFile(archiveFiles[0],"tests-jbossas-custom-events-acs.war"));
    }
    
    @Deployment(name="monitor", order=6)
    public static WebArchive createDeployment6() {
        String version=System.getProperty("bam.version");

        java.io.File[] archiveFiles=DependencyResolvers.use(MavenDependencyResolver.class)
                .artifacts("org.overlord.bam.release.jbossas.tests.platform.custom_events:tests-jbossas-custom-events-monitor:war:"+version)
                .resolveAsFiles();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class,
                        copyToTmpFile(archiveFiles[0],"custom-events-monitor.war"));
    }
    
    @Deployment(name="overlord-bam-epn", order=7)
    public static WebArchive createDeployment7() {
        String version=System.getProperty("bam.version");

        java.io.File[] archiveFiles=DependencyResolvers.use(MavenDependencyResolver.class)
                .artifacts("org.overlord.bam.content:overlord-bam-epn:war:"+version)
                .resolveAsFiles();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class,
                        copyToTmpFile(archiveFiles[0],"overlord-bam-epn.war"));
    }
    
    private static java.io.File copyToTmpFile(java.io.File source, String filename) {
        String tmpdir=System.getProperty("java.io.tmpdir");
        java.io.File dir=new java.io.File(tmpdir+java.io.File.separator+"bamtests"+System.currentTimeMillis());
        
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
    @OperateOnDeployment(value="monitor")
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
            
            // Should be 10 processed events and 2 result
            if (events.size() != 12) {
                fail("12 events expected, but got: "+events.size());
            }
            
            acsresults = getACSEvents();
            
            if (acsresults == null) {
                fail("No acsresults returned");
            }
            
            if (acsresults.size() != acsSize+2) {
                fail("Size of acs results should be 2 more: was "
                                +acsSize+" now "+acsresults.size());
            }
            
        } catch (Exception e) {
            fail("Failed to invoke service via SOAP: "+e);
        }
    }

    @Test
    @OperateOnDeployment(value="monitor")
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
            Thread.sleep(3000);
            
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
