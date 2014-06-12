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
package org.overlord.rtgov.ui.tests.services.switchyard;

import java.util.Collections;
import java.util.Comparator;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.overlord.rtgov.ui.client.model.QName;
import org.overlord.rtgov.ui.client.model.ReferenceBean;
import org.overlord.rtgov.ui.client.model.ReferenceSummaryBean;
import org.overlord.rtgov.ui.client.model.ServiceBean;
import org.overlord.rtgov.ui.client.model.ServiceSummaryBean;
import org.overlord.rtgov.ui.client.model.ServicesFilterBean;
import org.overlord.rtgov.ui.provider.ServicesProvider;
import org.overlord.rtgov.ui.provider.switchyard.SwitchYardServicesProvider;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class SwitchYardServicesProviderTest {
	
	@Inject
	private ServicesProvider _provider;
	
	// Application names
	private static final QName ORDER_APP=new QName("urn:switchyard-quickstart-demo:multiapp:0.1.0", "orders");
	private static final QName CONSUMER_SERVICE_APP=new QName("urn:switchyard-quickstart-demo:multiapp:0.1.0", "consumer-service");
	
	// Service names
	private static final QName ORDERINPUT_SERVICE_NAME=new QName("urn:switchyard-quickstart-demo:multiapp:0.1.0", "OrderInput");
	private static final QName ORDERSERVICE_SERVICE_NAME=new QName("urn:switchyard-quickstart-demo:multiapp:0.1.0", "OrderService");
	
	// Reference name
	private static final QName ORDERWEBSERVICE_REF_NAME=new QName("urn:switchyard-quickstart-demo:multiapp:0.1.0","OrderWebService");
    
	@Deployment(name="rtgov-ui-test", order=0)
    public static WebArchive createDeployment1() {
        String rtgovuiversion=System.getProperty("rtgov-ui.version");
        
        return ShrinkWrap.create(WebArchive.class, "rtgov-ui-test.war")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsManifestResource("META-INF/jboss-deployment-structure.xml", "jboss-deployment-structure.xml")
            // Required for RTGOV-351 .addAsLibraries(Maven.resolver().resolve("org.jboss.remotingjmx:remoting-jmx:1.1.2.Final").withTransitivity().asFile())
            .addAsLibraries(Maven.resolver().resolve("org.overlord.rtgov.active-queries:active-collection:"+rtgovuiversion).withTransitivity().asFile())
            .addAsLibraries(Maven.resolver().resolve("org.overlord.rtgov.ui:rtgov-ui-services-switchyard:"+rtgovuiversion).withTransitivity().asFile())
    		.addAsLibraries(Maven.resolver().resolve("org.overlord.rtgov.ui:rtgov-ui-core:"+rtgovuiversion).withTransitivity().asFile());
    }
    
    @Deployment(name="switchyard-demo-multiApp-artifacts", order=2, testable=false)
    public static JavaArchive createDeployment2() {
        String version=System.getProperty("switchyard.version");

        java.io.File archiveFile=Maven.resolver().resolve("org.switchyard.quickstarts.demos:switchyard-demo-multiApp-artifacts:"+version)
                .withoutTransitivity().asSingleFile();
        
        return ShrinkWrap.createFromZipFile(JavaArchive.class,
        					copyToTmpFile(archiveFile, "OrderService.jar"));
    }
   
    @Deployment(name="switchyard-demo-multiApp-order-consumer", order=3, testable=false)
    public static JavaArchive createDeployment3() {
        String version=System.getProperty("switchyard.version");

        java.io.File archiveFile=Maven.resolver().resolve("org.switchyard.quickstarts.demos:switchyard-demo-multiApp-order-consumer:"+version)
                .withoutTransitivity().asSingleFile();
        
        return ShrinkWrap.createFromZipFile(JavaArchive.class, archiveFile);
    }
   
    @Deployment(name="switchyard-demo-multiApp-order-service", order=4, testable=false)
    public static JavaArchive createDeployment4() {
        String version=System.getProperty("switchyard.version");

        java.io.File archiveFile=Maven.resolver().resolve("org.switchyard.quickstarts.demos:switchyard-demo-multiApp-order-service:"+version)
                .withoutTransitivity().asSingleFile();
        
        return ShrinkWrap.createFromZipFile(JavaArchive.class, archiveFile);
    }
    
    /* Required for RTGOV-351
     * However currently this causes the test server to hang after appearing to undeploy all components.
     * Used version 1.1.2.Final of the jmx remoting lib, as 2.0.0.Final was failing to resolve a Beta3
     * dependent on a remoting lib.
    @org.junit.Before
    public void init() {
    	if (_provider instanceof SwitchYardServicesProvider) {
    		((SwitchYardServicesProvider)_provider).setServerJMX("service:jmx:remoting-jmx://localhost:9999");
    	}
    }
    */
   
    @Test @OperateOnDeployment(value="rtgov-ui-test")
    @org.junit.Ignore // RTGOV-444
    public void testGetApplicationNames() {
    	if (_provider == null) {
    		fail("Provider not set");
    	}
    	
        try {
			java.util.List<QName> appNames=_provider.getApplicationNames();
			
			if (appNames.size() != 2) {
				fail("Should be 2 deployed applications: "+appNames.size());
			}
			
			if (!appNames.contains(CONSUMER_SERVICE_APP)) {
				fail("Failed to find app: "+CONSUMER_SERVICE_APP);
			}
			
			if (!appNames.contains(ORDER_APP)) {
				fail("Failed to find app: "+ORDER_APP);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed to get app names: "+e.getMessage());
		}
        
    }
    
    @Test @OperateOnDeployment(value="rtgov-ui-test")
    @org.junit.Ignore // RTGOV-444
    public void testFindServicesNoFilter() {
    	if (_provider == null) {
    		fail("Provider not set");
    	}
    	
        try {
			java.util.List<ServiceSummaryBean> ssbs=_provider.findServices(new ServicesFilterBean());
			
			if (ssbs.size() != 2) {
				fail("Should be 2 services: "+ssbs.size());
			}
			
			Collections.sort(ssbs, new Comparator<ServiceSummaryBean>() {
				public int compare(ServiceSummaryBean o1, ServiceSummaryBean o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
			
			if (!ssbs.get(0).getName().equals(ORDERINPUT_SERVICE_NAME.toString())) {
				fail("Expecting service name '"+ORDERINPUT_SERVICE_NAME+"', but got: "+ssbs.get(0).getName());
			}
			
			if (!ssbs.get(1).getName().equals(ORDERSERVICE_SERVICE_NAME.toString())) {
				fail("Expecting service name '"+ORDERSERVICE_SERVICE_NAME+"', but got: "+ssbs.get(1).getName());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed to find services: "+e.getMessage());
		}
        
    }
        
    @Test @OperateOnDeployment(value="rtgov-ui-test")
    @org.junit.Ignore // RTGOV-444
    public void testFindServicesFilterApp() {
    	if (_provider == null) {
    		fail("Provider not set");
    	}
    	
        try {
        	ServicesFilterBean filter=new ServicesFilterBean();
        	filter.setApplicationName(ORDER_APP.toString());
        	
			java.util.List<ServiceSummaryBean> ssbs=_provider.findServices(filter);
			
			if (ssbs.size() != 1) {
				fail("Should be 1 services: "+ssbs.size());
			}
			
			if (!ssbs.get(0).getName().equals(ORDERSERVICE_SERVICE_NAME.toString())) {
				fail("Expecting service name '"+ORDERSERVICE_SERVICE_NAME+"', but got: "+ssbs.get(0).getName());
			}			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed to find services: "+e.getMessage());
		}
        
    }

    @Test @OperateOnDeployment(value="rtgov-ui-test")
    @org.junit.Ignore // RTGOV-444
    public void testFindServicesFilterService() {
    	if (_provider == null) {
    		fail("Provider not set");
    	}
    	
        try {
        	ServicesFilterBean filter=new ServicesFilterBean();
        	filter.setServiceName(ORDERINPUT_SERVICE_NAME.toString());
        	
			java.util.List<ServiceSummaryBean> ssbs=_provider.findServices(filter);
			
			if (ssbs.size() != 1) {
				fail("Should be 1 services: "+ssbs.size());
			}
			
			if (!ssbs.get(0).getName().equals(ORDERINPUT_SERVICE_NAME.toString())) {
				fail("Expecting service name '"+ORDERINPUT_SERVICE_NAME+"', but got: "+ssbs.get(0).getName());
			}			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed to find services: "+e.getMessage());
		}
        
    }

    @Test @OperateOnDeployment(value="rtgov-ui-test")
    @org.junit.Ignore // RTGOV-444
    public void testGetService() {
    	if (_provider == null) {
    		fail("Provider not set");
    	}
    	
        try {
        	String uuid=SwitchYardServicesProvider.generateId(CONSUMER_SERVICE_APP.toString(), ORDERINPUT_SERVICE_NAME.toString());
        	
			ServiceBean sb=_provider.getService(uuid);
			
			if (sb == null) {
				fail("No service returned");
			}
			
			if (!sb.getApplication().equals(CONSUMER_SERVICE_APP)) {
				fail("Service Application name incorrect: "+sb.getApplication());
			}
			
			if (!sb.getName().equals(ORDERINPUT_SERVICE_NAME)) {
				fail("Service name incorrect: "+sb.getName());
			}
			
			if (sb.getReferences().size() != 1) {
				fail("Expecting 1 reference: "+sb.getReferences().size());
			}
			
			ReferenceSummaryBean rsb=sb.getReferences().get(0);
			
			if (!rsb.getApplication().equals(CONSUMER_SERVICE_APP.toString())) {
				fail("Reference Application name incorrect");
			}
			
			if (!rsb.getName().equals(ORDERWEBSERVICE_REF_NAME.toString())) {
				fail("Expecting reference name '"+ORDERWEBSERVICE_REF_NAME+"', but got: "+rsb.getName());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed to get service: "+e.getMessage());
		}
        
    }

    @Test @OperateOnDeployment(value="rtgov-ui-test")
    @org.junit.Ignore // RTGOV-444
    public void testGetReference() {
    	if (_provider == null) {
    		fail("Provider not set");
    	}
    	
        try {
        	String uuid=SwitchYardServicesProvider.generateId(CONSUMER_SERVICE_APP.toString(), ORDERWEBSERVICE_REF_NAME.toString());
        	
			ReferenceBean rb=_provider.getReference(uuid);
			
			if (rb == null) {
				fail("No reference returned");
			}
			
			if (!rb.getApplication().equals(CONSUMER_SERVICE_APP)) {
				fail("Reference Application name incorrect: "+rb.getApplication());
			}
			
			if (!rb.getName().equals(ORDERWEBSERVICE_REF_NAME)) {
				fail("Reference name incorrect: "+rb.getName());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed to get reference: "+e.getMessage());
		}
        
    }

    public static java.io.File copyToTmpFile(java.io.File source, String filename) {
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
}
