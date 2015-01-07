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
package org.overlord.rtgov.tests.platforms.jbossas.situationmgr;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.overlord.rtgov.active.collection.QuerySpec;
import org.overlord.rtgov.analytics.situation.IgnoreSubject;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class JBossASSituationManagerServiceTest {

    private static ObjectMapper MAPPER=new ObjectMapper();
    
    private static final String ORDER_SERVICE_URL = "http://127.0.0.1:8080/demo-orders/OrderService";
    
    private static final String SITUATIONS = "Situations";
    private static final String FILTERED_SITUATIONS = "FilteredSituations";
    
    private static final String FILTERED_SUBJECT = "{urn:switchyard-quickstart-demo:orders:0.1.0}OrderService|submitOrder";

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

        java.io.File archiveFile=Maven.resolver().resolve("org.overlord.rtgov.samples.jbossas.sla:samples-jbossas-sla-epn:war:"+version)
                .withoutTransitivity().asSingleFile();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class, archiveFile);
    }
    
    @Test @OperateOnDeployment("orders-app")
    public void testFilteredSituations() {
        
        try {
            SOAPConnectionFactory factory=SOAPConnectionFactory.newInstance();
            SOAPConnection con=factory.createConnection();
            
            java.net.URL url=new java.net.URL(ORDER_SERVICE_URL);
            
            String mesg="<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
                        "   <soap:Body>"+
                        "       <orders:submitOrder xmlns:orders=\"urn:switchyard-quickstart-demo:orders:1.0\">"+
                        "            <order>"+
                        "                <orderId>3</orderId>"+
                        "                <itemId>JAM</itemId>"+
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
            
            QuerySpec qsSituations=new QuerySpec();
            qsSituations.setCollection(SITUATIONS);
            
            QuerySpec qsFilteredSituations=new QuerySpec();
            qsFilteredSituations.setCollection(FILTERED_SITUATIONS);
            
            java.util.List<?> result0 = performACMQuery(qsSituations);
            
            if (result0.size() != 2) {
                fail("Expecting two situations: "+result0.size());
            }
            
            java.util.List<?> result1 = performACMQuery(qsFilteredSituations);
            
            if (result1.size() != 2) {
                fail("Expecting two filtered situations: "+result1.size());
            }
            
            System.out.println("RESULT="+result1);
            
            // Send 'ignore' for filtered subject
            IgnoreSubject ignore=new IgnoreSubject();
            ignore.setSubject(FILTERED_SUBJECT);
            ignore.setReason("No particular reason");
            
            ignore(ignore);
            
            java.util.List<?> result2 = performACMQuery(qsSituations);
            
            if (result2.size() != 2) {
                fail("Still expecting two situations: "+result2.size());
            }
            
            java.util.List<?> result3 = performACMQuery(qsFilteredSituations);
            
            if (result3.size() != 1) {
                fail("Now expecting just one filtered situations: "+result3.size());
            }
            
            observe(ignore.getSubject());
            
            java.util.List<?> result4 = performACMQuery(qsSituations);
            
            if (result4.size() != 2) {
                fail("Still again expecting two situations: "+result4.size());
            }
            
            java.util.List<?> result5 = performACMQuery(qsFilteredSituations);
            
            if (result5.size() != 2) {
                fail("Again expecting two filtered situations: "+result5.size());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
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
        
        initAuth(connection);

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
    
    public static void ignore(IgnoreSubject ignore) throws Exception {
        URL getUrl = new URL("http://localhost:8080/overlord-rtgov/situation/manager/ignore");
        HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
        connection.setRequestMethod("POST");

        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setAllowUserInteraction(false);
        connection.setRequestProperty("Content-Type",
                    "application/json");
        
        initAuth(connection);

        java.io.OutputStream os=connection.getOutputStream();
        
        MAPPER.writeValue(os, ignore);
        
        os.flush();
        os.close();
        
        java.io.InputStream is=connection.getInputStream();
        
        try {
            byte[] b=new byte[is.available()];
            is.read(b);
            
            System.out.println("Ignore response="+new String(b));
        } catch (Exception e) {
            System.err.println("Exception when reading response: "+e);
         }
        
        is.close();
        
    }
    
    public static void observe(String subject) throws Exception {
        URL getUrl = new URL("http://localhost:8080/overlord-rtgov/situation/manager/observe");
        HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
        connection.setRequestMethod("POST");

        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setAllowUserInteraction(false);
        connection.setRequestProperty("Content-Type",
                    "application/json");
        
        initAuth(connection);

        java.io.OutputStream os=connection.getOutputStream();
        
        os.write(subject.getBytes());
        
        os.flush();
        os.close();
        
        java.io.InputStream is=connection.getInputStream();
        
        try {
            byte[] b=new byte[is.available()];
            is.read(b);
            
            System.out.println("Observe response="+new String(b));
        } catch (Exception e) {
            System.err.println("Exception when reading response: "+e);
         }
        
        is.close();
        
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