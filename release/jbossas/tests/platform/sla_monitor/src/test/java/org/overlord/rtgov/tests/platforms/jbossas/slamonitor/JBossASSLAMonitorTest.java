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
package org.overlord.rtgov.tests.platforms.jbossas.slamonitor;

import java.io.Serializable;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

import javax.annotation.Resource;
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
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.overlord.rtgov.active.collection.QuerySpec;
import org.overlord.rtgov.active.collection.predicate.MVEL;
import org.overlord.rtgov.epn.EPNManager;
import org.overlord.rtgov.epn.EventList;
import org.overlord.rtgov.epn.NotificationListener;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class JBossASSLAMonitorTest {

    private static ObjectMapper MAPPER=new ObjectMapper();
    
    private static final String ORDER_SERVICE_URL = "http://127.0.0.1:8080/demo-orders/OrderService";

    private static final String SERVICE_RESPONSE_TIMES = "ServiceResponseTimes";
    private static final String SITUATIONS = "Situations";
    private static final String SITUATIONS_PROCESSED = "SituationsProcessed";
    
    // NOTE: Had to use resource, as injection didn't seem to work when there
    // was multiple deployments, even though the method defined the
    // 'overlord-rtgov' as the deployment it should operate on.
    @Resource(mappedName=EPNManager.URI)
    org.overlord.rtgov.epn.EPNManager _epnManager;
    
    @Deployment(name="orders-app", order=1)
    public static JavaArchive createDeployment1() {
        String version=System.getProperty("rtgov.version");

        java.io.File[] archiveFiles=DependencyResolvers.use(MavenDependencyResolver.class)
                .artifacts("org.overlord.rtgov.samples.jbossas.ordermgmt:samples-jbossas-ordermgmt-app:"+version)
                .resolveAsFiles();
        
        return ShrinkWrap.createFromZipFile(JavaArchive.class, archiveFiles[0]);
    }
    
    @Deployment(name="orders-ip", order=2)
    public static WebArchive createDeployment2() {
        String version=System.getProperty("rtgov.version");

        java.io.File[] archiveFiles=DependencyResolvers.use(MavenDependencyResolver.class)
                .artifacts("org.overlord.rtgov.samples.jbossas.ordermgmt:samples-jbossas-ordermgmt-ip:war:"+version)
                .resolveAsFiles();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class, archiveFiles[0]);
    }
    
    @Deployment(name="epn", order=3)
    public static WebArchive createDeployment3() {
        String version=System.getProperty("rtgov.version");

        java.io.File[] archiveFiles=DependencyResolvers.use(MavenDependencyResolver.class)
                .artifacts("org.overlord.rtgov.samples.jbossas.sla:samples-jbossas-sla-epn:war:"+version)
                .resolveAsFiles();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class, archiveFiles[0]);
    }
    
    @Deployment(name="monitor", order=4)
    public static WebArchive createDeployment4() {
        String version=System.getProperty("rtgov.version");

        java.io.File[] archiveFiles=DependencyResolvers.use(MavenDependencyResolver.class)
                .artifacts("org.overlord.rtgov.samples.jbossas.sla:samples-jbossas-sla-monitor:war:"+version)
                .resolveAsFiles();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class,
                        TestUtils.copyToTmpFile(archiveFiles[0],"slamonitor.war"));
    }

    @Test @OperateOnDeployment("orders-app")
    public void testViolationsFromACMgr() {
        
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
            
            QuerySpec qs1=new QuerySpec();
            qs1.setCollection(SITUATIONS);
            
            java.util.List<?> result0 = performACMQuery(qs1);
            
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
            
            java.util.List<?> result1 = performACMQuery(qs1);
            
            System.out.println("RETRIEVED RESULTS 1="+result1);
            
            if (result1 == null) {
                fail("Result 1 is null");
            }
            
            if (result1.size()-result0.size() != 2) {
                fail("2 event expected, but got: "+-result1.size()+" - "+-result0.size()
                		+" = "+(result1.size()-result0.size()));
            }
            
            @SuppressWarnings("unchecked")
            java.util.Map<String,?> results=(java.util.Map<String,?>)result1.get(result1.size()-1);
            
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

        	if (properties.size() != 5) {
                fail("Expecting 5 entries in property list: "+properties.size());
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

    @Test @OperateOnDeployment("orders-app")
    public void testViolationsFromSLAMonitor() {
        
        try {
            SOAPConnectionFactory factory=SOAPConnectionFactory.newInstance();
            SOAPConnection con=factory.createConnection();
            
            java.net.URL url=new java.net.URL(ORDER_SERVICE_URL);
            
            String mesg="<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
                        "   <soap:Body>"+
                        "       <orders:submitOrder xmlns:orders=\"urn:switchyard-quickstart-demo:orders:1.0\">"+
                        "            <order>"+
                        "                <orderId>PO-19838-XYZ</orderId>"+
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
            
            java.util.List<?> violationsBefore=getViolations();
            
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
            
            java.util.List<?> violations=getViolations();
            
            if (violations == null) {
                fail("No violations returned");
            }
            
            System.out.println("VIOLATIONS="+violations);
            
            if (violations.size()-violationsBefore.size() != 2) {
                fail("2 violation expected, but got: "+violations.size()+" - "+violationsBefore.size()
                				+" = "+(violations.size()-violationsBefore.size()));
            }

        } catch (Exception e) {
            fail("Failed to invoke service: "+e);
        }
    }
    
    @Test @OperateOnDeployment("orders-app")
    public void testResponseTimesFromACMgr() {
        
        try {
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
            
            // Get base results
            QuerySpec qs1=new QuerySpec();
            qs1.setCollection(SERVICE_RESPONSE_TIMES);
            
            java.util.List<?> result1base = performACMQuery(qs1);
            
            QuerySpec qs2=new QuerySpec();
            qs2.setCollection("OrderService");
            qs2.setParent(SERVICE_RESPONSE_TIMES);
            qs2.setPredicate(new MVEL("serviceType == \"{urn:switchyard-quickstart-demo:orders:0.1.0}OrderService\" && "
                    +"operation == \"submitOrder\""));
            
            java.util.List<?> result2base = performACMQuery(qs2);

            // Send message
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
            
            java.util.List<?> result1 = performACMQuery(qs1);
            
            System.out.println("RETRIEVED RESULTS 1="+result1);
            
            if (result1 == null) {
                fail("Result 1 is null");
            }
            
            if (result1.size()-result1base.size() != 3) {
                fail("3 events expected, but got: "+result1.size()+" - "+result1base.size()
                		+" = "+(result1.size()-result1base.size()));
            }

            java.util.List<?> result2 = performACMQuery(qs2);
            
            System.out.println("RETRIEVED RESULTS 2="+result2);
            
            if (result2 == null) {
                fail("Result 2 is null");
            }
            
            if (result2.size()-result2base.size() != 1) {
                fail("1 event expected, but got: "+result2.size()+" - "+result2base.size()
                		+" = "+(result2.size()-result2base.size()));
            }

        } catch (Exception e) {
            fail("Failed to invoke service: "+e);
        }
    }

    @Test @OperateOnDeployment("orders-app")
    public void testResponseTimesFromSLAMonitor() {
        
        try {
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
            
            // Preload lists
            java.util.List<?> respTimes1before=getResponseTimes(null);
            java.util.List<?> respTimes2before=getResponseTimes("operation=submitOrder");
           
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
            
            java.util.List<?> respTimes1=getResponseTimes(null);
            
            if (respTimes1 == null) {
                fail("No events returned");
            }
            
            if (respTimes1.size()-respTimes1before.size() != 3) {
                fail("3 events expected, but got: "+respTimes1.size()+" - "+respTimes1before.size()
                		+" = "+(respTimes1.size()-respTimes1before.size()));
            }
            
            System.out.println("RESPONSE TIMES="+respTimes1);

            // TODO: Sort out encoding to be able to pass fully qualified name of serviceType
            
            // {urn:switchyard-quickstart-demo:orders:0.1.0}OrderService
            java.util.List<?> respTimes2=getResponseTimes(//"serviceType={urn%3Aswitchyard-quickstart-demo%3Aorders%3A0.1.0}OrderService&"+
            		        "operation=submitOrder");
            
            if (respTimes2 == null) {
                fail("No events returned");
            }
            
            if (respTimes2.size()-respTimes2before.size() != 1) {
                fail("1 event expected, but got: "+respTimes2.size()+" - "+respTimes2before.size()+
                		" = "+(respTimes2.size()-respTimes2before.size()));
            }
            
            System.out.println("RESPONSE TIMES (buy)="+respTimes2);

        } catch (Exception e) {
            fail("Failed to invoke service: "+e);
        }
    }
    
    @Test @OperateOnDeployment("orders-app")
    @Ignore
    public void testResponseTimesOnFaultFromACMgr() {
        
        try {
            SOAPConnectionFactory factory=SOAPConnectionFactory.newInstance();
            SOAPConnection con=factory.createConnection();
            
            java.net.URL url=new java.net.URL(ORDER_SERVICE_URL);
            
            String mesg="<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
                        "   <soap:Body>"+
                        "       <orders:submitOrder xmlns:orders=\"urn:switchyard-quickstart-demo:orders:1.0\">"+
                        "            <order>"+
                        "                <orderId>PO-19838-XYZ</orderId>"+
                        "                <itemId>LAPTOP</itemId>"+
                        "                <quantity>200</quantity>"+
                        "                <customer>Fred</customer>"+
                        "            </order>"+
                        "        </orders:submitOrder>"+
                        "    </soap:Body>"+
                        "</soap:Envelope>";
            
            java.io.InputStream is=new java.io.ByteArrayInputStream(mesg.getBytes());
            
            SOAPMessage request=MessageFactory.newInstance().createMessage(null, is);
            
            is.close();
            
            // Preload results
            QuerySpec qs1=new QuerySpec();
            qs1.setCollection(SERVICE_RESPONSE_TIMES);
            
            java.util.List<?> result1base = performACMQuery(qs1);
            
            SOAPMessage response=con.call(request, url);

            java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
            
            response.writeTo(baos);
            
            String resp=baos.toString();

            baos.close();
            
            if (!resp.contains("Item Not Available")) {
                fail("Item not available response not received");
            }
            
            // Wait for events to propagate
            Thread.sleep(4000);
            
            java.util.List<?> result1 = performACMQuery(qs1);
            
            System.out.println("RETRIEVED RESULTS (Fault)="+result1);
            
            if (result1 == null) {
                fail("Result 1 is null");
            }
            
            if (result1.size()-result1base.size() != 2) {
                fail("2 events expected, but got: "+result1.size()+" - "+result1base.size()
                		+" = "+(result1.size()-result1base.size()));
            }
            
        } catch (Exception e) {
            fail("Failed to invoke service: "+e);
        }
    }

    @Test @OperateOnDeployment("orders-app")
    public void testActivityEventsProcessed() {
        
        TestListener tl=new TestListener();
        
        _epnManager.addNotificationListener(SITUATIONS_PROCESSED, tl);

        try {
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
            Thread.sleep(2000);
            
            if (tl.getEvents(SITUATIONS_PROCESSED) == null) {
                fail("Expecting situations processed");
            }
            
            if (tl.getEvents(SITUATIONS_PROCESSED).size() != 3) {
                fail("Expecting 3 (sla situations) processed events, but got: "+tl.getEvents(SITUATIONS_PROCESSED).size());
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to invoke service via SOAP: "+e);
        } finally {
        	_epnManager.removeNotificationListener(SITUATIONS_PROCESSED, tl);
        }
    }   

    @Test @OperateOnDeployment("orders-app")
    public void testActivityEventsResults() {
        
        TestListener tl=new TestListener();
        
        _epnManager.addNotificationListener(SITUATIONS, tl);

        try {
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
            Thread.sleep(2000);
            
            // Check that all events have been processed
            if (tl.getEvents(SITUATIONS) == null) {
                fail("Expecting sla violations results");
            }
            
            if (tl.getEvents(SITUATIONS).size() != 2) {
                fail("Expecting 2 (sla violations) results events, but got: "+tl.getEvents(SITUATIONS).size());
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to invoke service via SOAP: "+e);
        } finally {
        	_epnManager.removeNotificationListener(SITUATIONS, tl);
        }
    }
    
    public static java.util.List<?> performACMQuery(QuerySpec qs) throws Exception {
        Authenticator.setDefault(new DefaultAuthenticator());
        
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
        java.util.List<?> result=null;
        
        try {
        	result = MAPPER.readValue(is, java.util.List.class);
        } catch (Exception e) {
        	System.err.println("Exception when reading ACMQuery '"+qs+"': "+e);
        	result = new java.util.ArrayList<Object>();
        }
        
        is.close();
        
        return (result);
    }
    
    /**
     * This method deserializes the events into a list of hashmaps. The
     * actual objects are not deserialized, as this would require the
     * domain objects to be included in all deployments, which would
     * make verifying classloading/isolation difficult.
     * 
     * @return The list of objects representing violations
     * @throws Exception Failed to deserialize the violations
     */
    protected java.util.List<?> getViolations() throws Exception {
        java.util.List<?> ret=null;
        
        String urlStr="http://localhost:8080/slamonitor/monitor/situations";
        
        Authenticator.setDefault(new DefaultAuthenticator());
        
        URL getUrl = new URL(urlStr);
        
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
     * @param query The query string to supply when requesting response times
     * @return The list of objects representing events
     * @throws Exception Failed to deserialize the events
     */
    protected java.util.List<?> getResponseTimes(String query) throws Exception {
        java.util.List<?> ret=null;
        
        String urlStr="http://localhost:8080/slamonitor/monitor/responseTimes";
        
        if (query != null) {
            urlStr += "?"+query;
        }
        
        Authenticator.setDefault(new DefaultAuthenticator());
        
        URL getUrl = new URL(urlStr);
        
        HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
        connection.setRequestMethod("GET");
        System.out.println("Content-Type: " + connection.getContentType());

        java.io.InputStream is=connection.getInputStream();
        
        ret = MAPPER.readValue(is, java.util.List.class);
       
        return (ret);
    }

    public class TestListener implements NotificationListener {
        
        private java.util.Map<String, java.util.List<Serializable>> _events=
                    new java.util.HashMap<String, java.util.List<Serializable>>();

        /**
         * {@inheritDoc}
         */
        public void notify(String subject, EventList events) {
            java.util.List<Serializable> list=_events.get(subject);
            if (list == null) {
                list = new java.util.ArrayList<Serializable>();
                _events.put(subject, list);
            }
            for (Serializable event : events) {
                list.add(event);
            }
        }
        
        public java.util.List<Serializable> getEvents(String subject) {
            return (_events.get(subject));
        }
    }
    
    static class DefaultAuthenticator extends Authenticator {

        public PasswordAuthentication getPasswordAuthentication () {
            return new PasswordAuthentication ("admin", "overlord".toCharArray());
        }
    }
}
