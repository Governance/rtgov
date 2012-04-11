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
package org.savara.bam.epn;

import static org.junit.Assert.*;

import org.junit.Test;
import org.savara.bam.epn.internal.EventList;
import org.savara.bam.epn.testdata.TestEvent1;
import org.savara.bam.epn.testdata.TestEvent2;
import org.savara.bam.epn.testdata.TestEventProcessorA;
import org.savara.bam.epn.testdata.TestNodeListener;

public class AbstractEPNManagerTest {

    private static final String N1 = "N1";
    private static final String N2 = "N2";
    private static final String N3 = "N3";
    private static final String TEST_NETWORK = "TestNetwork";

    protected AbstractEPNManager getManager() {
        return(new AbstractEPNManager() {
            
            public void enqueue(String network,
                    java.util.List<java.io.Serializable> events) throws Exception {
            }
            
            public EPNContext getContext() {
                return null;
            }
        });
    }
    
    @Test
    public void testRegisterNetworkIncorrectRootNodeName() {
        Network net=new Network();
        net.setName(TEST_NETWORK);
        
        // Root should be incorrect, to test exception
        net.setRootNodeName(N2);
        
        Node n1=new Node();
        n1.setEventProcessor(new TestEventProcessorA());
        net.getNodes().put(N1, n1);
        
        AbstractEPNManager mgr=getManager();
        
        try {
            mgr.register(net);
            
            fail("Network registration should fail due to missing or incorrect root node");
        } catch(Exception e) {
        }
    }

    @Test
    public void testRegisterNetworkNodeNoEventProcessor() {
        Network net=new Network();
        net.setName(TEST_NETWORK);
        net.setRootNodeName(N1);
        
        Node n1=new Node();
        net.getNodes().put(N1, n1);
        
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
        net.setRootNodeName(N1);
        
        Node n1=new Node();
        n1.setEventProcessor(new TestEventProcessorA());
        net.getNodes().put(N1, n1);
        
        Node n2=new Node();
        n2.setEventProcessor(new TestEventProcessorA());
        net.getNodes().put("N2", n2);
        
        Node n3=new Node();
        n3.setEventProcessor(new TestEventProcessorA());
        net.getNodes().put("N3", n3);
        
        AbstractEPNManager mgr=getManager();
        
        try {
            mgr.register(net);
        } catch(Exception e) {
            fail("Failed to register network: "+e);
        }
        
        if (mgr.getNetwork(TEST_NETWORK) != net) {
            fail("Failed to find test network");
        }
        
        try {
            if (mgr.getNode(TEST_NETWORK, N1) != n1) {
                fail("Failed to find node n1");
            }
            if (mgr.getNode(TEST_NETWORK, N2) != n2) {
                fail("Failed to find node n2");
            }
            if (mgr.getNode(TEST_NETWORK, N3) != n3) {
                fail("Failed to find node n3");
            }
        } catch(Exception e) {
            fail("Failed to find node");
        }
    }

    @Test
    public void testRegisterNodeListener() {
        Network net=new Network();
        net.setName(TEST_NETWORK);
        net.setRootNodeName(N1);
        
        TestEventProcessorA tep=new TestEventProcessorA();
        
        Node n1=new Node();
        n1.setEventProcessor(tep);
        n1.setNotificationEnabled(true);
        net.getNodes().put(N1, n1);
        
        AbstractEPNManager mgr=getManager();
        
        try {
            mgr.register(net);
            
            TestNodeListener nl=new TestNodeListener();
            
            mgr.addNodeListener(nl);
            
            TestEvent1 te1=new TestEvent1(2);
            TestEvent2 te2=new TestEvent2(5);
            
            EventList el=new EventList();
            el.add(te1);
            el.add(te2);
            
            tep.retry(te2);
            
            EventList retries=mgr.process(TEST_NETWORK, N1, n1, null, el, 3);
            
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
            
        } catch(Exception e) {
            fail("Failed with exception: "+e);
        }
    }

}
