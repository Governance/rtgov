/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-11, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.bam.epn;

import static org.junit.Assert.*;

import java.io.Serializable;

import org.junit.Test;
import org.overlord.bam.epn.AbstractEPNManager;
import org.overlord.bam.epn.EPNContainer;
import org.overlord.bam.epn.EventList;
import org.overlord.bam.epn.Network;
import org.overlord.bam.epn.Node;
import org.overlord.bam.epn.testdata.TestEvent1;
import org.overlord.bam.epn.testdata.TestEvent2;
import org.overlord.bam.epn.testdata.TestEventProcessorA;
import org.overlord.bam.epn.testdata.TestNodeListener;

public class AbstractEPNManagerTest {

    private static final String DUMMY_NETWORK = "DummyNetwork";
    private static final String N1 = "N1";
    private static final String N2 = "N2";
    private static final String N3 = "N3";
    private static final String TEST_NETWORK = "TestNetwork";
    private static final String TEST_SUBJECT1 = "TestSubject1";
    private static final String TEST_SUBJECT2 = "TestSubject2";
    private static final String VER1 = "1";
    private static final String VER2 = "2";

    protected AbstractEPNManager getManager() {
        return(new AbstractEPNManager() {
            
            public void publish(String subject,
                    java.util.List<? extends java.io.Serializable> events) throws Exception {
            }
            
            public EPNContainer getContainer() {
                return null;
            }
        });
    }
    
    @Test
    public void testRegisterNetworkIncorrectRootNodeName() {
        Network net=new Network();
        net.setName(TEST_NETWORK);
        
        // Root should be incorrect, to test exception
        Subscription sub=new Subscription();
        sub.setNodeName(N2);
        sub.setSubject(TEST_SUBJECT1);
        net.getSubscriptions().add(sub);
        
        Node n1=new Node();
        n1.setName(N1);
        n1.setEventProcessor(new TestEventProcessorA());
        net.getNodes().add(n1);
        
        AbstractEPNManager mgr=getManager();
        
        try {
            mgr.register(net);
            
            fail("Network registration should fail due to missing or incorrect root node");
        } catch(Exception e) {
        }
    }

    @Test
    public void testNetworkListenerNotified() {
        Network net=new Network();
        net.setName(TEST_NETWORK);
        
        Node n1=new Node();
        n1.setName(N1);
        n1.setEventProcessor(new TestEventProcessorA());
        net.getNodes().add(n1);
        
        AbstractEPNManager mgr=getManager();
        
        TestNetworkListener l=new TestNetworkListener();
        mgr.addNetworkListener(l);
        
        if (l._registered.size() != 0) {
            fail("No networks should be registered");
        }
        
        if (l._unregistered.size() != 0) {
            fail("No networks should be unregistered");
        }
        
        try {
            mgr.register(net);
        } catch(Exception e) {
            fail("Failed: "+e);
        }
        
        if (l._registered.size() != 1) {
            fail("1 network should be registered: "+l._registered.size());
        }
        
        if (l._unregistered.size() != 0) {
            fail("Still no networks should be unregistered");
        }
        
        try {
            mgr.unregister(net.getName(), net.getVersion());
        } catch(Exception e) {
            fail("Failed: "+e);
        }
        
        if (l._registered.size() != 1) {
            fail("Still 1 network should be registered: "+l._registered.size());
        }
        
        if (l._unregistered.size() != 1) {
            fail("1 network should be unregistered: "+l._unregistered.size());
        }
    }

    @Test
    public void testRegisterNetworkNodeNoEventProcessor() {
        Network net=new Network();
        net.setName(TEST_NETWORK);
        //net.setRootNodeName(N1);
        
        Node n1=new Node();
        n1.setName(N1);
        net.getNodes().add(n1);
        
        AbstractEPNManager mgr=getManager();
        
        try {
            mgr.register(net);
            
            fail("Network registration should fail due to node with missing event processor");
        } catch(Exception e) {
        }
    }

    @Test
    public void testNetworkAndNodeLookup() {
        Network net=new Network();
        net.setName(TEST_NETWORK);
        //net.setRootNodeName(N1);
        
        Node n1=new Node();
        n1.setName(N1);
        n1.setEventProcessor(new TestEventProcessorA());
        net.getNodes().add(n1);
        
        Node n2=new Node();
        n2.setName(N2);
        n2.setEventProcessor(new TestEventProcessorA());
        net.getNodes().add(n2);
        
        Node n3=new Node();
        n3.setName(N3);
        n3.setEventProcessor(new TestEventProcessorA());
        net.getNodes().add(n3);
        
        AbstractEPNManager mgr=getManager();
        
        try {
            mgr.register(net);
        } catch(Exception e) {
            fail("Failed to register network: "+e);
        }
        
        if (mgr.getNetwork(TEST_NETWORK, null) != net) {
            fail("Failed to find test network");
        }
        
        try {
            if (mgr.getNode(TEST_NETWORK, null, N1) != n1) {
                fail("Failed to find node n1");
            }
            if (mgr.getNode(TEST_NETWORK, null, N2) != n2) {
                fail("Failed to find node n2");
            }
            if (mgr.getNode(TEST_NETWORK, null, N3) != n3) {
                fail("Failed to find node n3");
            }
        } catch(Exception e) {
            fail("Failed to find node");
        }
    }

    @Test
    public void testRegisterNodeListenerNotifyProcessed() {
        Network net=new Network();
        net.setName(TEST_NETWORK);
        
        TestEventProcessorA tep=new TestEventProcessorA();
        
        Node n1=new Node();
        n1.setName(N1);
        n1.setEventProcessor(tep);
        n1.getNotifyTypes().add(NotificationType.Processed.name());
        net.getNodes().add(n1);
        
        AbstractEPNManager mgr=getManager();
        
        try {
            mgr.register(net);
            
            TestNodeListener nl=new TestNodeListener();
            
            mgr.addNodeListener(TEST_NETWORK, nl);
            
            TestNodeListener anothernl=new TestNodeListener();
            
            mgr.addNodeListener(DUMMY_NETWORK, anothernl);
            
            TestEvent1 te1=new TestEvent1(2);
            TestEvent2 te2=new TestEvent2(5);
            
            java.util.List<Serializable> elList=new java.util.ArrayList<Serializable>();
            EventList el=new EventList(elList);
            elList.add(te1);
            elList.add(te2);
            
            tep.retry(te2);
            
            EventList retries=mgr.process(net, n1, null, el, 3);
            
            if (retries == null) {
                fail("Retries is null");
            }
            
            if (retries.size() != 1) {
                fail("Retries should have 1 event: "+retries.size());
            }
            
            if (!retries.contains(te2)) {
                fail("Retries did not contain te2");
            }
            
            if (nl.getEntries().size() != 1) {
                fail("Node listener should have 1 processed event: "+nl.getEntries().size());
            }
            
            if (!nl.getEntries().get(0).getEvents().contains(te1)) {
                fail("Processed Event te1 should have been processed");
            }
            
            if (!nl.getEntries().get(0).getNetwork().equals(TEST_NETWORK)) {
                fail("Processed Event network name incorrect");
            }           
            
            if (!nl.getEntries().get(0).getNode().equals(N1)) {
                fail("Processed Event node name incorrect");
            }
            
            if (anothernl.getEntries().size() > 0) {
                fail("Should be no entries in other listener");
            }
            
        } catch(Exception e) {
            e.printStackTrace();
            fail("Failed with exception: "+e);
        }
    }

    @Test
    public void testRegisterNodeListenerDontNotifyProcessed() {
        Network net=new Network();
        net.setName(TEST_NETWORK);
        
        TestEventProcessorA tep=new TestEventProcessorA();
        
        Node n1=new Node();
        n1.setName(N1);
        n1.setEventProcessor(tep);
        n1.getNotifyTypes().add(NotificationType.Results.name());
        net.getNodes().add(n1);
        
        AbstractEPNManager mgr=getManager();
        
        try {
            mgr.register(net);
            
            TestNodeListener nl=new TestNodeListener();
            
            mgr.addNodeListener(TEST_NETWORK, nl);
            
            TestNodeListener anothernl=new TestNodeListener();
            
            mgr.addNodeListener(DUMMY_NETWORK, anothernl);
            
            TestEvent1 te1=new TestEvent1(2);
            TestEvent2 te2=new TestEvent2(5);
            
            java.util.List<Serializable> elList=new java.util.ArrayList<Serializable>();
            EventList el=new EventList(elList);
            elList.add(te1);
            elList.add(te2);
            
            tep.retry(te2);
            
            EventList retries=mgr.process(net, n1, null, el, 3);
            
            if (retries == null) {
                fail("Retries is null");
            }
            
            if (retries.size() != 1) {
                fail("Retries should have 1 event: "+retries.size());
            }
            
            if (!retries.contains(te2)) {
                fail("Retries did not contain te2");
            }
            
            if (nl.getEntries().size() != 0) {
                fail("Node listener should have 0 processed event: "+nl.getEntries().size());
            }
            
            if (anothernl.getEntries().size() > 0) {
                fail("Should be no entries in other listener");
            }
            
        } catch(Exception e) {
            e.printStackTrace();
            fail("Failed with exception: "+e);
        }
    }

    @Test
    public void testRegisterMultipleNetworkVersions() {
        Network net1=new Network();
        net1.setName(TEST_NETWORK);
        net1.setVersion(VER1);
        
        Subscription sub1=new Subscription();
        sub1.setNodeName(N1);
        sub1.setSubject(TEST_SUBJECT1);
        net1.getSubscriptions().add(sub1);
        
        TestEventProcessorA tep=new TestEventProcessorA();
        
        Node n1=new Node();
        n1.setName(N1);
        n1.setEventProcessor(tep);
        net1.getNodes().add(n1);
        
        Network net2=new Network();
        net2.setName(TEST_NETWORK);
        net2.setVersion(VER2);
        
        Subscription sub2=new Subscription();
        sub2.setNodeName(N2);
        sub2.setSubject(TEST_SUBJECT2);
        net2.getSubscriptions().add(sub2);
        
        Node n2=new Node();
        n2.setName(N2);
        n2.setEventProcessor(tep);
        net2.getNodes().add(n2);
        
        AbstractEPNManager mgr=getManager();
        
        try {
            mgr.register(net1);
            
            // Check if network subscribed to subject
            java.util.List<Network> res1=mgr.getNetworksForSubject(TEST_SUBJECT1);
            
            if (res1 == null) {
                fail("Network list is null");
            }
            
            if (res1.size() != 1) {
                fail("Network list should have 1 entry: "+res1.size());
            }
            
            if (res1.get(0) != net1) {
                fail("Network should be net1");
            }
            
            mgr.register(net2);
            
            // Refresh subject list
            res1 = mgr.getNetworksForSubject(TEST_SUBJECT1);
            
            if (res1 != null) {
                fail("List for subject1 should now be null");
            }
            
            // Check if network subscribed to subject
            java.util.List<Network> res2=mgr.getNetworksForSubject(TEST_SUBJECT2);
            
            if (res2 == null) {
                fail("Network list2 is null");
            }
            
            if (res2.size() != 1) {
                fail("Network list2 should have 1 entry: "+res2.size());
            }
            
            if (res2.get(0) != net2) {
                fail("Network should be net2");
            }
            
            // Finally check that unsubscribing the current version will reinstate the old subjects
            mgr.unregister(net2.getName(), net2.getVersion());
            
            res2 = mgr.getNetworksForSubject(TEST_SUBJECT2);
            
            if (res2 != null) {
                fail("List for subject2 should now be null");
            }
            
            res1 = mgr.getNetworksForSubject(TEST_SUBJECT1);
            
            if (res1 == null) {
                fail("List for subject1 should now be available again");
            }
            
            if (res1.size() != 1) {
                fail("Network list1 should have 1 entry again: "+res1.size());
            }
            
            if (res1.get(0) != net1) {
                fail("Network should be net1 again");
            }
        } catch(Exception e) {
            fail("Failed with exception: "+e);
        }
    }

    public class TestNetworkListener implements NetworkListener {
        
        protected java.util.List<Network> _registered=new java.util.ArrayList<Network>();
        protected java.util.List<Network> _unregistered=new java.util.ArrayList<Network>();

        public void registered(Network network) {
            _registered.add(network);
        }

        public void unregistered(Network network) {
            _unregistered.add(network);
        }
        
    }
}
