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
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.server.QuerySpec;
import org.overlord.rtgov.activity.util.ActivityUtil;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class JBossASActivityServerServiceTest {

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
            
            if (acts.size() != 12) {
                fail("Expecting 12 activity events: "+acts.size());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to invoke service: "+e);
        }
    }

    public static java.util.List<ActivityType> getActivityEvents() throws Exception {
        
        Authenticator.setDefault(new DefaultAuthenticator());
        
        QuerySpec query=new QuerySpec().setFormat("jpql").setExpression("SELECT at FROM ActivityType at");
        
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

        b = new byte[is.available()];
        is.read(b);
        
        System.out.println(">>>> JSON="+new String(b));
        
        return (ActivityUtil.deserializeActivityTypeList(b));
     }
    
    static class DefaultAuthenticator extends Authenticator {

        public PasswordAuthentication getPasswordAuthentication () {
            return new PasswordAuthentication ("admin", "overlord".toCharArray());
        }
    }
}