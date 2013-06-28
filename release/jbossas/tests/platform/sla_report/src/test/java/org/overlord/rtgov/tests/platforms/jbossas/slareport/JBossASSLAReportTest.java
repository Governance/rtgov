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
package org.overlord.rtgov.tests.platforms.jbossas.slareport;

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
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.overlord.rtgov.reports.model.Report;
import org.overlord.rtgov.reports.model.Tabular;
import org.overlord.rtgov.reports.util.ReportsUtil;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class JBossASSLAReportTest {

    private static final String ORDER_SERVICE_URL = "http://127.0.0.1:8080/demo-orders/OrderService";
    
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
    
    @Deployment(name="report", order=3)
    public static WebArchive createDeployment3() {
        String version=System.getProperty("rtgov.version");

        java.io.File[] archiveFiles=DependencyResolvers.use(MavenDependencyResolver.class)
                .artifacts("org.overlord.rtgov.samples.jbossas.sla:samples-jbossas-sla-report:war:"+version)
                .resolveAsFiles();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class,
                        TestUtils.copyToTmpFile(archiveFiles[0],"slareport.war"));
    }

    @Test @OperateOnDeployment("orders-app")
    public void testSLAReport() {
        
        try {
            java.util.Calendar startDateTime=java.util.Calendar.getInstance();
            
            // Wait
            Thread.sleep(2000);
            
            SOAPConnectionFactory factory=SOAPConnectionFactory.newInstance();
            SOAPConnection con=factory.createConnection();
            
            java.net.URL url=new java.net.URL(ORDER_SERVICE_URL);
            
            String mesg="<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
                        "   <soap:Body>"+
                        "       <orders:submitOrder xmlns:orders=\"urn:switchyard-quickstart-demo:orders:1.0\">"+
                        "            <order>"+
                        "                <orderId>1</orderId>"+
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
            
            java.util.Calendar endDateTime=java.util.Calendar.getInstance();
            
            // Is working week?
            boolean f_enableCalendar=false;
            
            if (startDateTime.get(java.util.Calendar.DAY_OF_WEEK) >= java.util.Calendar.MONDAY
                        && startDateTime.get(java.util.Calendar.DAY_OF_WEEK) >= java.util.Calendar.FRIDAY
                        && startDateTime.get(java.util.Calendar.HOUR_OF_DAY) >= 9
                        && startDateTime.get(java.util.Calendar.HOUR_OF_DAY) <= 5) {
                f_enableCalendar = true;
            }

            java.util.Map<String,String> params=new java.util.HashMap<String, String>();
            params.put("maxResponseTime", "400");
            params.put("averagedDuration", "450");
            
            if (f_enableCalendar) {
                params.put("calendar", "Default");
            }
            
            params.put("startDay", ""+startDateTime.get(java.util.Calendar.DAY_OF_MONTH));
            params.put("startMonth", ""+(startDateTime.get(java.util.Calendar.MONTH)+1));
            params.put("startYear", ""+startDateTime.get(java.util.Calendar.YEAR));
            params.put("startHour", ""+startDateTime.get(java.util.Calendar.HOUR_OF_DAY));
            params.put("startMinute", ""+startDateTime.get(java.util.Calendar.MINUTE));
            params.put("startSecond", ""+startDateTime.get(java.util.Calendar.SECOND));
            
            params.put("endDay", ""+endDateTime.get(java.util.Calendar.DAY_OF_MONTH));
            params.put("endMonth", ""+(endDateTime.get(java.util.Calendar.MONTH)+1));
            params.put("endYear", ""+endDateTime.get(java.util.Calendar.YEAR));
            params.put("endHour", ""+endDateTime.get(java.util.Calendar.HOUR_OF_DAY));
            params.put("endMinute", ""+endDateTime.get(java.util.Calendar.MINUTE));
            params.put("endSecond", ""+endDateTime.get(java.util.Calendar.SECOND));
            
            String reportjson = getReport("SLAReport", params);
            
            if (reportjson == null) {
                fail("Report json is null");
            }
            
            System.out.println("REPORT="+reportjson);
            
            Report report=ReportsUtil.deserializeReport(reportjson.getBytes());
            
            if (report == null) {
                fail("Report is null");
            }
            
            if (report.getSections().size() != 1) {
                fail("Expecting 1 section: "+report.getSections().size());
            }
            
            Tabular section=(Tabular)report.getSections().get(0);
            
            if (section.getRows().size() == 0) {
                fail("Expecting rows: "+section.getRows().size());
            }
            
            if (section.getSummary() == null) {
                fail("Summary is null");
            }
            
            if ((section.getSummary().getValues().get(1) instanceof Number) == false) {
                fail("Value is not a number");
            }
                        
            if (((Number)section.getSummary().getValues().get(1)).intValue() == 0) {
                fail("Summary value should not be 0: "+section.getSummary().getValues().get(1));
            }
            
            if (f_enableCalendar) {
                if (!section.getSummary().getProperties().containsKey("ViolationPercentage")) {
                    fail("Property 'ViolationPercentage' not found");
                }
                
                if (((Number)section.getSummary().getProperties().get("ViolationPercentage")).doubleValue() > 0) {
                    fail("Violation percentage should be greater than 0: "+section.getSummary().getProperties().get("ViolationPercentage"));
                }
            }
            
         } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to invoke service: "+e);
        }
    }

    public static String getReport(String name, java.util.Map<String,String> params) throws Exception {
        
        Authenticator.setDefault(new DefaultAuthenticator());
        
        String url="http://localhost:8080/overlord-rtgov/report/generate?report="+name;
        
        for (String key : params.keySet()) {
            url += "&"+key+"="+params.get(key);
        }
        
        System.out.println("URL="+url);
        
        URL getUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
        connection.setRequestMethod("GET");

        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setAllowUserInteraction(false);
        connection.setRequestProperty("Content-Type",
                    "application/json");

        java.io.InputStream is=connection.getInputStream();
        
        byte[] b=new byte[is.available()];
        is.read(b);
        
        String result=new String(b);
        
        is.close();
        
        return (result);
    }
    
    static class DefaultAuthenticator extends Authenticator {

        public PasswordAuthentication getPasswordAuthentication () {
            return new PasswordAuthentication ("admin", "overlord".toCharArray());
        }
    }
}