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
package epn.test.jee;

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

import epn.test.Child;
import epn.test.NetworkLoader;
import epn.test.Obj1;
import epn.test.Obj2;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class NetworkLoaderTest {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
            .addClass(epn.test.Child.class)
            .addClass(epn.test.ChildPredicate.class)
            .addClass(epn.test.NetworkLoader.class)
            .addClass(epn.test.Obj1.class)
            .addClass(epn.test.Obj2.class)
            .addClass(epn.test.Root.class)
            .addAsResource("networks/TestNetwork.json")
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsLibraries(
                    DependencyResolvers
                    .use(MavenDependencyResolver.class)
                    .artifacts("org.savara.bam.event-processor-network:epn-core:1.0.0-SNAPSHOT",
                            "org.savara.bam.event-processor-network:epn-container-jms:1.0.0-SNAPSHOT")
                    .resolveAsFiles());
    }

    @Inject
    org.savara.bam.epn.EPNManager _epnManager;

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
            _epnManager.enqueue(NetworkLoader.TEST_NETWORK, events);
            
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
            _epnManager.unregister(NetworkLoader.TEST_NETWORK);
        } catch(Exception e) {
            fail("Failed to unregister network: "+e);
        }
    }
}