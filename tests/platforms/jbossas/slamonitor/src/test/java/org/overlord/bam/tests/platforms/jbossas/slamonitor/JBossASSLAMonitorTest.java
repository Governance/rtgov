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
package org.overlord.bam.tests.platforms.jbossas.slamonitor;

import java.io.Serializable;

import javax.annotation.Resource;
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
import org.overlord.bam.epn.EventList;
import org.overlord.bam.epn.NodeListener;
import org.overlord.bam.epn.NotificationType;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class JBossASSLAMonitorTest {

    private static final String SLA_MONITOR_EPN = "SLAMonitorEPN";
    private static final String SLA_VIOLATIONS = "SLAViolations";
    private static final String RESPONSE_TIMES = "ResponseTimes";
    
    // NOTE: Had to use resource, as injection didn't seem to work when there
    // was multiple deployments, even though the method defined the
    // 'overlord-bam' as the deployment it should operate on.
    @Resource(mappedName="java:global/overlord-bam/EPNManager")
    org.overlord.bam.epn.EPNManager _epnManager;
    
    @Deployment(name="overlord-bam", order=1)
    public static WebArchive createDeployment1() {
        String version=System.getProperty("bam.version");
        String platform=System.getProperty("bam.platform");

        java.io.File[] archiveFiles=DependencyResolvers.use(MavenDependencyResolver.class)
                .artifacts("org.overlord.bam.distribution.jee:overlord-bam:war:"+platform+":"+version)
                .resolveAsFiles();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class,
                TestUtils.copyToTmpFile(archiveFiles[0],"overlord-bam.war"));
    }
    
    @Deployment(name="orders", order=2)
    public static WebArchive createDeployment2() {
        String version=System.getProperty("bam.version");

        java.io.File[] archiveFiles=DependencyResolvers.use(MavenDependencyResolver.class)
                .artifacts("org.overlord.bam.samples.jbossas.slamonitor:samples-jbossas-slamonitor-orders:war:"+version)
                .resolveAsFiles();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class, archiveFiles[0]);
    }
    
    @Deployment(name="epn", order=3)
    public static WebArchive createDeployment3() {
        String version=System.getProperty("bam.version");

        java.io.File[] archiveFiles=DependencyResolvers.use(MavenDependencyResolver.class)
                .artifacts("org.overlord.bam.samples.jbossas.slamonitor:samples-jbossas-slamonitor-epn:war:"+version)
                .resolveAsFiles();
        
        return ShrinkWrap.createFromZipFile(WebArchive.class, archiveFiles[0]);
    }
    
    @Test @OperateOnDeployment("overlord-bam")
    public void testActivityEventsProcessed() {
        
        TestListener tl=new TestListener();
        
        _epnManager.addNodeListener(SLA_MONITOR_EPN, tl);

        try {
            SOAPConnectionFactory factory=SOAPConnectionFactory.newInstance();
            SOAPConnection con=factory.createConnection();
            
            java.net.URL url=new java.net.URL("http://127.0.0.1:18001/demo-orders/OrderService");
            
            String mesg="<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
                        "   <soap:Body>"+
                        "       <orders:submitOrder xmlns:orders=\"urn:switchyard-quickstart-demo:orders:1.0\">"+
                        "            <order>"+
                        "                <orderId>PO-19838-XYZ</orderId>"+
                        "                <itemId>BUTTER</itemId>"+
                        "                <quantity>200</quantity>"+
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
            if (tl.getProcessed(RESPONSE_TIMES).size() != 8) {
                fail("Expecting 8 (response time) processed events, but got: "+tl.getProcessed(RESPONSE_TIMES).size());
            }
           
            if (tl.getResults(RESPONSE_TIMES).size() != 2) {
                fail("Expecting 2 (response time) results events, but got: "+tl.getResults(RESPONSE_TIMES).size());
            }

            if (tl.getProcessed(SLA_VIOLATIONS).size() != 2) {
                fail("Expecting 2 (sla violations) processed events, but got: "+tl.getProcessed(SLA_VIOLATIONS).size());
            }
           
            if (tl.getResults(SLA_VIOLATIONS) != null) {
                fail("Expecting 0 (sla violations) results events, but got: "+tl.getResults(SLA_VIOLATIONS).size());
            }

        } catch (Exception e) {
            fail("Failed to invoke service via SOAP: "+e);
        }
    }
    

    @Test @OperateOnDeployment("overlord-bam")
    public void testActivityEventsResults() {
        
        TestListener tl=new TestListener();
        
        _epnManager.addNodeListener(SLA_MONITOR_EPN, tl);

        try {
            SOAPConnectionFactory factory=SOAPConnectionFactory.newInstance();
            SOAPConnection con=factory.createConnection();
            
            java.net.URL url=new java.net.URL("http://127.0.0.1:18001/demo-orders/OrderService");
            
            String mesg="<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
                        "   <soap:Body>"+
                        "       <orders:submitOrder xmlns:orders=\"urn:switchyard-quickstart-demo:orders:1.0\">"+
                        "            <order>"+
                        "                <orderId>PO-13739-ABC</orderId>"+
                        "                <itemId>JAM</itemId>"+
                        "                <quantity>50</quantity>"+
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
            if (tl.getProcessed(RESPONSE_TIMES).size() != 8) {
                fail("Expecting 8 (response time) processed events, but got: "+tl.getProcessed(RESPONSE_TIMES).size());
            }
           
            if (tl.getResults(RESPONSE_TIMES).size() != 2) {
                fail("Expecting 2 (response time) results events, but got: "+tl.getResults(RESPONSE_TIMES).size());
            }

            if (tl.getProcessed(SLA_VIOLATIONS).size() != 2) {
                fail("Expecting 2 (sla violations) processed events, but got: "+tl.getProcessed(SLA_VIOLATIONS).size());
            }
           
            if (tl.getResults(SLA_VIOLATIONS).size() != 1) {
                fail("Expecting 1 (sla violations) results events, but got: "+tl.getResults(SLA_VIOLATIONS).size());
            }

        } catch (Exception e) {
            fail("Failed to invoke service via SOAP: "+e);
        }
    }
    
    public class TestListener implements NodeListener {
        
        private java.util.Map<String, java.util.List<Serializable>> _processed=
                    new java.util.HashMap<String, java.util.List<Serializable>>();
        private java.util.Map<String, java.util.List<Serializable>> _results=
                    new java.util.HashMap<String, java.util.List<Serializable>>();

        /**
         * {@inheritDoc}
         */
        public void notify(String network, String version, String node,
                NotificationType type, EventList events) {
            if (type == NotificationType.Processed) {
                java.util.List<Serializable> list=_processed.get(node);
                if (list == null) {
                    list = new java.util.ArrayList<Serializable>();
                    _processed.put(node, list);
                }
                for (Serializable event : events) {
                    list.add(event);
                }
            } else if (type == NotificationType.Results) {
                java.util.List<Serializable> list=_results.get(node);
                if (list == null) {
                    list = new java.util.ArrayList<Serializable>();
                    _results.put(node, list);
                }
                for (Serializable event : events) {
                    list.add(event);
                }
            }
        }
        
        public java.util.List<Serializable> getProcessed(String node) {
            return (_processed.get(node));
        }
        
        public java.util.List<Serializable> getResults(String node) {
            return (_results.get(node));
        }
    }
}