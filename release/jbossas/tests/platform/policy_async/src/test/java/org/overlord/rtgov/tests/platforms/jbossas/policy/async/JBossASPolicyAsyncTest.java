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
package org.overlord.rtgov.tests.platforms.jbossas.policy.async;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;

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
public class JBossASPolicyAsyncTest {

    private static final String ORDER_SERVICE_URL = "http://127.0.0.1:8080/demo-orders/OrderService";

    @Deployment(name="overlord-rtgov", order=1)
    public static WebArchive createDeployment1() {
        String version=System.getProperty("rtgov.version");

        java.io.File[] archiveFiles=DependencyResolvers.use(MavenDependencyResolver.class)
                .artifacts("org.overlord.rtgov.release.jbossas:overlord-rtgov:war:"+version)
                .resolveAsFiles();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class,
                TestUtils.copyToTmpFile(archiveFiles[0],"overlord-rtgov.war"));
    }
    
    @Deployment(name="overlord-rtgov-switchyard", order=2)
    public static WebArchive createDeployment2() {
        String version=System.getProperty("rtgov.version");

        java.io.File[] archiveFiles=DependencyResolvers.use(MavenDependencyResolver.class)
                .artifacts("org.overlord.rtgov.release.jbossas:overlord-rtgov-switchyard:war:"+version)
                .resolveAsFiles();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class, archiveFiles[0]);
    }
    
    @Deployment(name="overlord-rtgov-acs", order=3)
    public static WebArchive createDeployment3() {
        String version=System.getProperty("rtgov.version");

        java.io.File[] archiveFiles=DependencyResolvers.use(MavenDependencyResolver.class)
                .artifacts("org.overlord.rtgov.content:overlord-rtgov-acs:war:"+version)
                .resolveAsFiles();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class,
                TestUtils.copyToTmpFile(archiveFiles[0],"overlord-rtgov-acs.war"));
    }
    
    @Deployment(name="overlord-rtgov-epn", order=4)
    public static WebArchive createDeployment4() {
        String version=System.getProperty("rtgov.version");

        java.io.File[] archiveFiles=DependencyResolvers.use(MavenDependencyResolver.class)
                .artifacts("org.overlord.rtgov.content:overlord-rtgov-epn:war:"+version)
                .resolveAsFiles();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class,
                TestUtils.copyToTmpFile(archiveFiles[0],"overlord-rtgov-epn.war"));
    }
    
    @Deployment(name="orders-app", order=5)
    public static WebArchive createDeployment5() {
        String version=System.getProperty("rtgov.version");

        java.io.File[] archiveFiles=DependencyResolvers.use(MavenDependencyResolver.class)
                .artifacts("org.overlord.rtgov.samples.jbossas.ordermgmt:samples-jbossas-ordermgmt-app:war:"+version)
                .resolveAsFiles();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class, archiveFiles[0]);
    }
    
    @Deployment(name="orders-ip", order=6)
    public static WebArchive createDeployment6() {
        String version=System.getProperty("rtgov.version");

        java.io.File[] archiveFiles=DependencyResolvers.use(MavenDependencyResolver.class)
                .artifacts("org.overlord.rtgov.samples.jbossas.ordermgmt:samples-jbossas-ordermgmt-ip:war:"+version)
                .resolveAsFiles();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class, archiveFiles[0]);
    }
    
    @Deployment(name="policy-async", order=7)
    public static WebArchive createDeployment7() {
        String version=System.getProperty("rtgov.version");

        java.io.File[] archiveFiles=DependencyResolvers.use(MavenDependencyResolver.class)
                .artifacts("org.overlord.rtgov.samples.jbossas.policy:samples-jbossas-policy-async:war:"+version)
                .resolveAsFiles();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class, archiveFiles[0]);
    }
    
    @Test @OperateOnDeployment("overlord-rtgov")
    public void testEnforcePolicy() {
        
        try {
            SOAPConnectionFactory factory=SOAPConnectionFactory.newInstance();
            SOAPConnection con=factory.createConnection();
            
            java.net.URL url=new java.net.URL(ORDER_SERVICE_URL);
            
            String mesg1="<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
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
            
            java.io.InputStream is1=new java.io.ByteArrayInputStream(mesg1.getBytes());
            
            SOAPMessage request1=MessageFactory.newInstance().createMessage(null, is1);
            
            is1.close();
            
            SOAPMessage response1=con.call(request1, url);

            java.io.ByteArrayOutputStream baos1=new java.io.ByteArrayOutputStream();
            
            response1.writeTo(baos1);
            
            String resp1=baos1.toString();

            baos1.close();
            
            if (!resp1.contains("<accepted>true</accepted>")) {
                fail("Order was not accepted: "+resp1);
            }
            
            // Wait for events to propagate
            Thread.sleep(4000);
    
            String mesg2="<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
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
        
            java.io.InputStream is2=new java.io.ByteArrayInputStream(mesg2.getBytes());
            
            SOAPMessage request2=MessageFactory.newInstance().createMessage(null, is2);
            
            is2.close();
            
            SOAPMessage response2=con.call(request2, url);
    
            java.io.ByteArrayOutputStream baos2=new java.io.ByteArrayOutputStream();
            
            response2.writeTo(baos2);
            
            String resp2=baos2.toString();
    
            baos2.close();
            
            if (resp2.contains("<accepted>true</accepted>")) {
                fail("Order should not be accepted, due to customer suspension: "+resp2);
            }

            String mesg3="<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
                    "   <soap:Body>"+
                    "       <orders:makePayment xmlns:orders=\"urn:switchyard-quickstart-demo:orders:1.0\">"+
                    "            <payment>"+
                    "                <amount>200</amount>"+
                    "                <customer>Fred</customer>"+
                    "            </payment>"+
                    "        </orders:makePayment>"+
                    "    </soap:Body>"+
                    "</soap:Envelope>";
        
            java.io.InputStream is3=new java.io.ByteArrayInputStream(mesg3.getBytes());
            
            SOAPMessage request3=MessageFactory.newInstance().createMessage(null, is3);
            
            is2.close();
            
            SOAPMessage response3=con.call(request3, url);
    
            java.io.ByteArrayOutputStream baos3=new java.io.ByteArrayOutputStream();
            
            response3.writeTo(baos3);
            
            String resp3=baos3.toString();
    
            baos3.close();
            
            if (!resp3.contains("<receipt>")) {
                fail("Receipt was not returned: "+resp3);
            }
            
            // Wait for events to propagate
            Thread.sleep(4000);
    
            // Repeat first request, which should now be accepted again
            String mesg4="<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
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
        
            java.io.InputStream is4=new java.io.ByteArrayInputStream(mesg4.getBytes());
            
            SOAPMessage request4=MessageFactory.newInstance().createMessage(null, is4);
            
            is4.close();
            
            SOAPMessage response4=con.call(request4, url);
    
            java.io.ByteArrayOutputStream baos4=new java.io.ByteArrayOutputStream();
            
            response4.writeTo(baos4);
            
            String resp4=baos4.toString();
    
            baos4.close();
            
            if (!resp4.contains("<accepted>true</accepted>")) {
                fail("Order was not accepted following unsuspension: "+resp4);
            }
        
        } catch (Exception e) {
            fail("Failed to invoke service: "+e);
        }
    }
}