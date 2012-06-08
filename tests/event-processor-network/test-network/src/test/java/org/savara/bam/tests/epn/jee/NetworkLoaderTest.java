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
package org.savara.bam.tests.epn.jee;

import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.savara.bam.epn.Network;
import org.savara.bam.epn.Node;
import org.savara.bam.tests.epn.Child;
import org.savara.bam.tests.epn.NetworkLoader;
import org.savara.bam.tests.epn.Obj1;
import org.savara.bam.tests.epn.Obj2;


import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class NetworkLoaderTest {

    @Inject
    org.savara.bam.epn.EPNManager _epnManager;

    @Deployment
    public static WebArchive createDeployment() {
        String version=System.getProperty("bam.version");

        return ShrinkWrap.create(WebArchive.class)
            .addClass(org.savara.bam.tests.epn.Child.class)
            .addClass(org.savara.bam.tests.epn.ChildPredicate.class)
            .addClass(org.savara.bam.tests.epn.NetworkLoader.class)
            .addClass(org.savara.bam.tests.epn.Obj1.class)
            .addClass(org.savara.bam.tests.epn.Obj2.class)
            .addClass(org.savara.bam.tests.epn.Root.class)
            .addAsResource("networks/TestNetwork.json")
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsManifestResource("bam-epn-hornetq-jms.xml")
            .addAsLibraries(
                    DependencyResolvers
                    .use(MavenDependencyResolver.class)
                    .artifacts("org.overlord.bam.event-processor-network:epn-core:"+version,
                            "org.overlord.bam.event-processor-network:epn-container-jee:"+version)
                    .resolveAsFiles());
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
        Node childAnode=net.getNodes().get(NetworkLoader.CHILD_A);
        
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
        
        Node childAnode=net.getNodes().get(NetworkLoader.CHILD_A);
        Node childBnode=net.getNodes().get(NetworkLoader.CHILD_B);
        
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
        
        Node childAnode=net.getNodes().get(NetworkLoader.CHILD_A);
        Node childBnode=net.getNodes().get(NetworkLoader.CHILD_B);
        
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