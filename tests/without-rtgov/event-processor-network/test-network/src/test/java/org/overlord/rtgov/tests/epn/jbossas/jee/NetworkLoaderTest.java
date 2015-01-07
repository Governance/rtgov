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
package org.overlord.rtgov.tests.epn.jbossas.jee;

import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.overlord.rtgov.epn.Network;
import org.overlord.rtgov.epn.Node;
import org.overlord.rtgov.tests.epn.Child;
import org.overlord.rtgov.tests.epn.NetworkLoader;
import org.overlord.rtgov.tests.epn.Obj1;
import org.overlord.rtgov.tests.epn.Obj2;


import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class NetworkLoaderTest {

    @Inject
    org.overlord.rtgov.epn.EPNManager _epnManager;

    @Deployment
    public static WebArchive createDeployment() {
        String rtgovversion=System.getProperty("rtgov.version");
        String jacksonversion=System.getProperty("jackson.version");

        return ShrinkWrap.create(WebArchive.class)
            .addClass(org.overlord.rtgov.tests.epn.Child.class)
            .addClass(org.overlord.rtgov.tests.epn.ChildPredicate.class)
            .addClass(org.overlord.rtgov.tests.epn.NetworkLoader.class)
            .addClass(org.overlord.rtgov.tests.epn.Obj1.class)
            .addClass(org.overlord.rtgov.tests.epn.Obj2.class)
            .addClass(org.overlord.rtgov.tests.epn.Root.class)
            .addAsResource("networks/TestNetwork.json")
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsManifestResource("rtgov-epn-hornetq-jms.xml")
            .addAsLibraries(
                    Maven.resolver().resolve("org.overlord.rtgov.event-processor-network:epn-core:"+rtgovversion).withoutTransitivity().asSingleFile(),
                    Maven.resolver().resolve("org.overlord.rtgov.event-processor:ep-core:"+rtgovversion).withoutTransitivity().asSingleFile(),
                    Maven.resolver().resolve("org.overlord.rtgov.event-processor-network:epn-container-jee:"+rtgovversion).withoutTransitivity().asSingleFile(),
                    Maven.resolver().resolve("org.overlord.rtgov.common:rtgov-common:"+rtgovversion).withoutTransitivity().asSingleFile(),
                    Maven.resolver().resolve("com.fasterxml.jackson.core:jackson-core:"+jacksonversion).withoutTransitivity().asSingleFile(),
                    Maven.resolver().resolve("com.fasterxml.jackson.core:jackson-databind:"+jacksonversion).withoutTransitivity().asSingleFile()
             );
    }

    @Test
    public void checkEPNManagerAvailable() {
        
        if (_epnManager == null) {
            fail("EPN Manager has not been set");
        }
    }

    @Test
    public void testTransformation() {
        NetworkLoader tl=new NetworkLoader();
        
        Network net=tl.loadNetwork();
        
        java.util.List<java.io.Serializable> events=new java.util.Vector<java.io.Serializable>();
        
        Obj1 o1=new Obj1(5);
        events.add(o1);
        
        try {
            _epnManager.register(net);
        } catch(Exception e) {
            fail("Failed to register network: "+e);
        }
        
        try {
            _epnManager.publish(NetworkLoader.TEST_SUBJECT, events);
            
            Thread.sleep(1000);
            
        } catch(Exception e) {
            fail("Failed to process events: "+e);
        }
        
        // Check that event was transformed into Obj2 at ChildA (due to predicate)
        Node childAnode=net.getNode(NetworkLoader.CHILD_A);
        
        if (childAnode == null) {
            fail("Failed to get child A");
        }
        
        Child childA=(Child)childAnode.getEventProcessor();
        
        if (childA.events().size() != 1) {
            fail("Child A does not have 1 event: "+childA.events().size());
        }
        
        if (!(childA.events().get(0) instanceof Obj2)) {
            fail("Child A event is not correct type");
        }
        
        if (((Obj2)childA.events().get(0)).getValue() != 5) {
            fail("Child A event has wrong value: "+((Obj2)childA.events().get(0)).getValue());
        }
        
        try {
            _epnManager.unregister(NetworkLoader.TEST_NETWORK, null);
        } catch(Exception e) {
            fail("Failed to unregister network: "+e);
        }
    }
    
    @Test
    public void testPredicates() {
        NetworkLoader tl=new NetworkLoader();
        
        Network net=tl.loadNetwork();
        
        java.util.List<java.io.Serializable> events=new java.util.Vector<java.io.Serializable>();
        
        Obj1 o1=new Obj1(15);
        Obj1 o2=new Obj1(5);
        Obj1 o3=new Obj1(12);
        events.add(o1);
        events.add(o2);
        events.add(o3);
        
        try {
            _epnManager.register(net);
        } catch(Exception e) {
            fail("Failed to register network: "+e);
        }
        
        try {
            _epnManager.publish(NetworkLoader.TEST_SUBJECT, events);
            
            Thread.sleep(1000);
            
        } catch(Exception e) {
            fail("Failed to process events: "+e);
        }
        
        Node childAnode=net.getNode(NetworkLoader.CHILD_A);
        Node childBnode=net.getNode(NetworkLoader.CHILD_B);
        
        if (childAnode == null) {
            fail("Failed to get child A");
        }
        
        if (childBnode == null) {
            fail("Failed to get child B");
        }
        
        Child childA=(Child)childAnode.getEventProcessor();
        Child childB=(Child)childBnode.getEventProcessor();
        
        if (childA.events().size() != 1) {
            fail("Child A does not have 1 event: "+childA.events().size());
        }
        
        if (childB.events().size() != 2) {
            fail("Child B does not have 2 events: "+childB.events().size());
        }
        
        if (childA.retries().size() != 0) {
            fail("Child A should have no retries: "+childA.retries().size());
        }
        
        if (childB.retries().size() != 0) {
            fail("Child B should have no retries: "+childB.retries().size());
        }
        
        try {
            _epnManager.unregister(NetworkLoader.TEST_NETWORK, null);
        } catch(Exception e) {
            fail("Failed to unregister network: "+e);
        }
    }

    @Test
    public void testRetries() {
        NetworkLoader tl=new NetworkLoader();
        
        Network net=tl.loadNetwork();
        
        java.util.List<java.io.Serializable> events=new java.util.Vector<java.io.Serializable>();
        
        Obj1 o1=new Obj1(15);
        Obj1 o2=new Obj1(5);
        Obj1 o3=new Obj1(12);
        events.add(o1);
        events.add(o2);
        events.add(o3);
        
        try {
            _epnManager.register(net);
        } catch(Exception e) {
            fail("Failed to register network: "+e);
        }
        
        Node childAnode=net.getNode(NetworkLoader.CHILD_A);
        Node childBnode=net.getNode(NetworkLoader.CHILD_B);
        
        if (childAnode == null) {
            fail("Failed to get child A");
        }
        
        if (childBnode == null) {
            fail("Failed to get child B");
        }
        
        Child childA=(Child)childAnode.getEventProcessor();
        Child childB=(Child)childBnode.getEventProcessor();
        
        Obj2 rej3=new Obj2(o3.getValue());
        childB.reject(rej3);
        
        if (childA.events().size() != 0) {
            fail("Child A does not have 0 event: "+childA.events().size()+" "+childA.events());
        }
        
        if (childB.events().size() != 0) {
            fail("Child B does not have 0 events: "+childB.events().size()+" "+childB.events());
        }
        
        try {
            _epnManager.publish(NetworkLoader.TEST_SUBJECT, events);
            
            Thread.sleep(1000);
            
        } catch(Exception e) {
            fail("Failed to process events: "+e);
        }
        
        if (childA.events().size() != 1) {
            fail("Child A does not have 1 event: "+childA.events().size()+" "+childA.events());
        }
        
        if (childB.events().size() != 2) {
            fail("Child B does not have 2 events: "+childB.events().size()+" "+childB.events());
        }
        
        if (childA.retries().size() != 0) {
            fail("Child A should have no retries: "+childA.retries().size());
        }
        
        if (childB.retries().size() != 1) {
            fail("Child B should have 1 retries: "+childB.retries().size());
        }
        
        if (!childB.retries().contains(rej3)) {
            fail("Child B retry event is wrong");
        }
        
        try {
            _epnManager.unregister(NetworkLoader.TEST_NETWORK, null);
        } catch(Exception e) {
            fail("Failed to unregister network: "+e);
        }
    }
}