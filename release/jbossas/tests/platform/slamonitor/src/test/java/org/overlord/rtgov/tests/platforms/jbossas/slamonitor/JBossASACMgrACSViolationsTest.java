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
package org.overlord.rtgov.tests.platforms.jbossas.slamonitor;

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
import org.overlord.rtgov.active.collection.QuerySpec;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class JBossASACMgrACSViolationsTest {

    private static ObjectMapper MAPPER=new ObjectMapper();
    
    private static final String ORDER_SERVICE_URL = "http://127.0.0.1:8080/demo-orders/OrderService";

    private static final String SITUATIONS = "Situations";
    
    @Deployment(name="overlord-rtgov", order=1)
    public static WebArchive createDeployment1() {
        String version=System.getProperty("rtgov.version");

        java.io.File[] archiveFiles=DependencyResolvers.use(MavenDependencyResolver.class)
                .artifacts("org.overlord.rtgov.release.jbossas:overlord-rtgov:war:"+version)
                .resolveAsFiles();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class,
                TestUtils.copyToTmpFile(archiveFiles[0],"overlord-rtgov.war"));
    }
    
    @Deployment(name="overlord-rtgov-acs", order=2)
    public static WebArchive createDeployment2() {
        String version=System.getProperty("rtgov.version");

        java.io.File[] archiveFiles=DependencyResolvers.use(MavenDependencyResolver.class)
                .artifacts("org.overlord.rtgov.content:overlord-rtgov-acs:war:"+version)
                .resolveAsFiles();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class,
                TestUtils.copyToTmpFile(archiveFiles[0],"overlord-rtgov-acs.war"));
    }
    
    @Deployment(name="overlord-rtgov-epn", order=3)
    public static WebArchive createDeployment3() {
        String version=System.getProperty("rtgov.version");

        java.io.File[] archiveFiles=DependencyResolvers.use(MavenDependencyResolver.class)
                .artifacts("org.overlord.rtgov.content:overlord-rtgov-epn:war:"+version)
                .resolveAsFiles();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class,
                TestUtils.copyToTmpFile(archiveFiles[0],"overlord-rtgov-epn.war"));
    }
    
    @Deployment(name="orders-app", order=4)
    public static WebArchive createDeployment4() {
        String version=System.getProperty("rtgov.version");

        java.io.File[] archiveFiles=DependencyResolvers.use(MavenDependencyResolver.class)
                .artifacts("org.overlord.rtgov.samples.jbossas.ordermgmt:samples-jbossas-ordermgmt-app:war:"+version)
                .resolveAsFiles();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class, archiveFiles[0]);
    }
    
    @Deployment(name="orders-ip", order=5)
    public static WebArchive createDeployment5() {
        String version=System.getProperty("rtgov.version");

        java.io.File[] archiveFiles=DependencyResolvers.use(MavenDependencyResolver.class)
                .artifacts("org.overlord.rtgov.samples.jbossas.ordermgmt:samples-jbossas-ordermgmt-ip:war:"+version)
                .resolveAsFiles();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class, archiveFiles[0]);
    }
    
    @Deployment(name="epn", order=6)
    public static WebArchive createDeployment6() {
        String version=System.getProperty("rtgov.version");

        java.io.File[] archiveFiles=DependencyResolvers.use(MavenDependencyResolver.class)
                .artifacts("org.overlord.rtgov.samples.jbossas.slamonitor:samples-jbossas-slamonitor-epn:war:"+version)
                .resolveAsFiles();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class, archiveFiles[0]);
    }
    
    @Deployment(name="monitor", order=7)
    public static WebArchive createDeployment7() {
        String version=System.getProperty("rtgov.version");

        java.io.File[] archiveFiles=DependencyResolvers.use(MavenDependencyResolver.class)
                .artifacts("org.overlord.rtgov.samples.jbossas.slamonitor:samples-jbossas-slamonitor-monitor:war:"+version)
                .resolveAsFiles();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class,
                        TestUtils.copyToTmpFile(archiveFiles[0],"slamonitor.war"));
    }
    
    @Test @OperateOnDeployment("overlord-rtgov")
    public void testViolations() {
        
        try {
            SOAPConnectionFactory factory=SOAPConnectionFactory.newInstance();
            SOAPConnection con=factory.createConnection();
            
            java.net.URL url=new java.net.URL(ORDER_SERVICE_URL);
            
            String orderId="PO-19838-XYZ";
            
            String mesg="<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
                        "   <soap:Body>"+
                        "       <orders:submitOrder xmlns:orders=\"urn:switchyard-quickstart-demo:orders:1.0\">"+
                        "            <order>"+
                        "                <orderId>"+orderId+"</orderId>"+
                        "                <itemId>JAM</itemId>"+
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
            Thread.sleep(4000);
            
            QuerySpec qs1=new QuerySpec();
            qs1.setCollection(SITUATIONS);
            
            java.util.List<?> result1 = performACMQuery(qs1);
            
            System.out.println("RETRIEVED RESULTS 1="+result1);
            
            if (result1 == null) {
                fail("Result 1 is null");
            }
            
            if (result1.size() != 2) {
                fail("2 event expected, but got: "+result1.size());
            }
            
            @SuppressWarnings("unchecked")
            java.util.Map<String,?> results=(java.util.Map<String,?>)result1.get(1);
            
            System.out.println("RESULT KEYS="+results.keySet());
            
            // Check that conversation id is in the context
            java.util.List<?> contextList=(java.util.List<?>)results.get("context");
            
            if (contextList.size() != 3) {
                fail("Expecting 3 entries in context list: "+contextList.size());
            }

            boolean f_found=false;
            
            for (int i=0; i < contextList.size(); i++) {
	            @SuppressWarnings("unchecked")
	            java.util.Map<String,String> contextEntry=(java.util.Map<String,String>)contextList.get(i);
	            
	            if (contextEntry.containsKey("type") && contextEntry.get("type").equalsIgnoreCase("conversation")) {
	            	
	                if (!contextEntry.containsKey("value")) {
	                    fail("'value' property not found in context entry");
	                }
	                
	                if (!contextEntry.get("value").equals(orderId)) {
	                    fail("Value should be order id '"+orderId+"': "+contextEntry.get("value"));
	                }
	                
	                f_found = true;
	            }
            }
            
            if (!f_found) {
            	fail("Conversation context not found");
            }
            
            // Check that the customer properties has been included
            @SuppressWarnings("unchecked")
            java.util.Map<String,String> properties=
                    (java.util.Map<String,String>)results.get("properties");
            
        	System.err.println("PROPS="+properties);

        	if (properties.size() != 3) {
                fail("Expecting 3 entries in property list: "+properties.size());
            }
            
            if (!properties.containsKey("customer")) {
                fail("Properties did not contain customer");
            }
            
            if (!properties.get("customer").equals("Fred")) {
                fail("Customer property not Fred: "+properties.get("customer"));
            }
            
        } catch (Exception e) {
            fail("Failed to invoke service: "+e);
        }
    }

    public static java.util.List<?> performACMQuery(QuerySpec qs) throws Exception {
        URL getUrl = new URL("http://localhost:8080/overlord-rtgov/acm/query");
        HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
        connection.setRequestMethod("POST");

        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setAllowUserInteraction(false);
        connection.setRequestProperty("Content-Type",
                    "application/json");

        java.io.OutputStream os=connection.getOutputStream();
        
        MAPPER.writeValue(os, qs);
        
        os.flush();
        os.close();
        
        java.io.InputStream is=connection.getInputStream();
        
        java.util.List<?> result=MAPPER.readValue(is, java.util.List.class);
        
        is.close();
        
        return (result);
    }
}