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
package org.overlord.rtgov.epn.embedded;

import static org.junit.Assert.*;

import java.io.Serializable;

import org.junit.Test;
import org.overlord.rtgov.epn.EventList;
import org.overlord.rtgov.epn.Network;
import org.overlord.rtgov.epn.Node;
import org.overlord.rtgov.epn.Subscription;
import org.overlord.rtgov.epn.embedded.EmbeddedEPNManager;
import org.overlord.rtgov.epn.embedded.EmbeddedEPNManager.EmbeddedChannel;

public class EmbeddedEPNManagerTest {

    private static final int MAX_RETRIES = 4;
    private static final String N1 = "N1";
    private static final String N2 = "N2";
    private static final String TEST_NETWORK = "TestNetwork";
    private static final String TEST_SUBJECT = "TestSubject";
    private static final String TEST_NETWORK2 = "TestNetwork2";
    private static final String TEST_SUBJECT2 = "TestSubject2";
    private static final String VER1 = "1";
    private static final String VER2 = "2";    

    @Test
    public void testEventTransformedForChild() {
        Network net=new Network();
        net.setName(TEST_NETWORK);
        net.setVersion(null);
        
        Subscription sub=new Subscription();
        sub.setSubject(TEST_SUBJECT);
        sub.setNodeName(N1);
        net.getSubscriptions().add(sub);
        
        Node n1=new Node();
        n1.setName(N1);
        n1.setPredicate(new TestPredicate1());
        n1.setEventProcessor(new TestEventProcessorB());
        net.getNodes().add(n1);
        
        Node n2=new Node();
        n2.setPredicate(new TestPredicate2());
        
        TestEventProcessorA tea=new TestEventProcessorA();
        n2.setName(N2);
        n2.setEventProcessor(tea);
        n2.getSourceNodes().add(N1);
        net.getNodes().add( n2);
        
        EmbeddedEPNManager mgr=new EmbeddedEPNManager();
        
        try {
            mgr.register(net);
            
            java.util.List<Serializable> eventsList=new java.util.ArrayList<Serializable>();
            EventList events=new EventList(eventsList);
            
            TestEvent1 te1=new TestEvent1(25);
            eventsList.add(te1);
            
            mgr.publish(TEST_SUBJECT, events);
            
            // Need to delay awaiting processing via other threads
            Thread.sleep(1000);
            
            if (tea.getEvents().size() != 1) {
                fail("Expecting 1 event to be processed: "+tea.getEvents().size());
            }
            
            if (tea.getEvents().get(0) instanceof TestEvent2) {
                TestEvent2 te2=(TestEvent2)tea.getEvents().get(0);
                
                if (te2.getValue() != 25) {
                    fail("Value is not correct: "+te2.getValue());
                }
            } else {
                fail("Incorrect event type: "+tea.getEvents().get(0).getClass());
            }
        } catch(Exception e) {
            e.printStackTrace();
            fail("Test failed: "+e);
        }
    }

    @Test
    public void testEventRetries() {
        Network net=new Network();
        net.setName(TEST_NETWORK);
        net.setVersion(null);
        
        Subscription sub=new Subscription();
        sub.setSubject(TEST_SUBJECT);
        sub.setNodeName(N1);
        net.getSubscriptions().add(sub);
        
        Node n1=new Node();
        n1.setName(N1);
        n1.setPredicate(new TestPredicate1());
        n1.setEventProcessor(new TestEventProcessorB());
        net.getNodes().add(n1);
        
        Node n2=new Node();
        n2.setName(N2);
        n2.setPredicate(new TestPredicate2());
        n2.setMaxRetries(MAX_RETRIES);
        n2.getSourceNodes().add(N1);
        
        TestEventProcessorC tea=new TestEventProcessorC();
        n2.setEventProcessor(tea);
        net.getNodes().add(n2);
        
        EmbeddedEPNManager mgr=new EmbeddedEPNManager();
        
        try {
            mgr.register(net);
            
            java.util.List<Serializable> eventsList=new java.util.ArrayList<Serializable>();
            EventList events=new EventList(eventsList);
            
            TestEvent1 te1=new TestEvent1(25);
            eventsList.add(te1);
            
            mgr.publish(TEST_SUBJECT, events);
            
            // Need to delay awaiting processing via other threads
            Thread.sleep(1000);
            
            if (tea._retryCount != MAX_RETRIES+1) {
                fail("Expecting "+(MAX_RETRIES+1)+" retries: "+tea._retryCount);
            }
        } catch(Exception e) {
            fail("Test failed: "+e);
        }
    }

    @Test
    public void testMultipleEntryPointsForSameSubject() {
        Network net1=new Network();
        net1.setName(TEST_NETWORK);
        net1.setVersion(null);
        
        Subscription sub1=new Subscription();
        sub1.setSubject(TEST_SUBJECT);
        sub1.setNodeName(N1);
        net1.getSubscriptions().add(sub1);
        
        Node n1=new Node();
        n1.setName(N1);
        n1.setPredicate(new TestPredicate1());
        n1.setEventProcessor(new TestEventProcessorB());
        net1.getNodes().add(n1);
        
        Network net2=new Network();
        net2.setName(TEST_NETWORK2);
        net2.setVersion(null);
        
        Subscription sub2=new Subscription();
        sub2.setSubject(TEST_SUBJECT);
        sub2.setNodeName(N2);
        net2.getSubscriptions().add(sub2);
        
        Node n2=new Node();
        n2.setName(N2);
        n2.setPredicate(new TestPredicate2());
        n2.setMaxRetries(MAX_RETRIES);
        
        TestEventProcessorC tea=new TestEventProcessorC();
        n2.setEventProcessor(tea);
        net2.getNodes().add(n2);
        
        EmbeddedEPNManager mgr=new EmbeddedEPNManager();
        
        try {
            mgr.register(net1);
            
            java.util.List<EmbeddedChannel> ch1=mgr.getEntryPoints().get(TEST_SUBJECT);
            
            if (ch1 == null) {
                fail("Failed to get channels for test subject");
            } else if (ch1.size() != 1) {
                fail("Number of channels for test subject should be 1: "+ch1.size());
            }
            
            mgr.register(net2);
            
            if (ch1.size() != 2) {
                fail("Number of channels for test subject should now be 2: "+ch1.size());
            }
            
        } catch(Exception e) {
            fail("Test failed: "+e);
        }
    }

    @Test
    public void testEntryPointsForDifferentSubjects() {
        Network net1=new Network();
        net1.setName(TEST_NETWORK);
        net1.setVersion(null);
        
        Subscription sub1=new Subscription();
        sub1.setSubject(TEST_SUBJECT);
        sub1.setNodeName(N1);
        net1.getSubscriptions().add(sub1);
        
        Node n1=new Node();
        n1.setName(N1);
        n1.setPredicate(new TestPredicate1());
        n1.setEventProcessor(new TestEventProcessorB());
        net1.getNodes().add(n1);
        
        Network net2=new Network();
        net2.setName(TEST_NETWORK2);
        net2.setVersion(null);
        
        Subscription sub2=new Subscription();
        sub2.setSubject(TEST_SUBJECT2);
        sub2.setNodeName(N2);
        net2.getSubscriptions().add(sub2);
        
        Node n2=new Node();
        n2.setName(N2);
        n2.setPredicate(new TestPredicate2());
        n2.setMaxRetries(MAX_RETRIES);
        
        TestEventProcessorC tea=new TestEventProcessorC();
        n2.setEventProcessor(tea);
        net2.getNodes().add(n2);
        
        EmbeddedEPNManager mgr=new EmbeddedEPNManager();
        
        try {
            mgr.register(net1);
            
            mgr.register(net2);
            
            java.util.List<EmbeddedChannel> ch1=mgr.getEntryPoints().get(TEST_SUBJECT);
            
            if (ch1 == null) {
                fail("Failed to get channels for test subject");
            } else if (ch1.size() != 1) {
                fail("Number of channels for test subject should be 1: "+ch1.size());
            }
            
            java.util.List<EmbeddedChannel> ch2=mgr.getEntryPoints().get(TEST_SUBJECT2);
            
            if (ch2 == null) {
                fail("Failed to get channels for test subject2");
            } else if (ch2.size() != 1) {
                fail("Number of channels for test subject2 should be 1: "+ch2.size());
            }
            
            
        } catch(Exception e) {
            fail("Test failed: "+e);
        }
    }
    
    @Test
    public void testMultipleEntryPointsForSameSubjectRemoved() {
        Network net1=new Network();
        net1.setName(TEST_NETWORK);
        net1.setVersion(null);
        
        Subscription sub1=new Subscription();
        sub1.setSubject(TEST_SUBJECT);
        sub1.setNodeName(N1);
        net1.getSubscriptions().add(sub1);
        
        Node n1=new Node();
        n1.setName(N1);
        n1.setPredicate(new TestPredicate1());
        n1.setEventProcessor(new TestEventProcessorB());
        net1.getNodes().add(n1);
        
        Network net2=new Network();
        net2.setName(TEST_NETWORK2);
        net2.setVersion(null);
        
        Subscription sub2=new Subscription();
        sub2.setSubject(TEST_SUBJECT);
        sub2.setNodeName(N2);
        net2.getSubscriptions().add(sub2);
        
        Node n2=new Node();
        n2.setName(N2);
        n2.setPredicate(new TestPredicate2());
        n2.setMaxRetries(MAX_RETRIES);
        
        TestEventProcessorC tea=new TestEventProcessorC();
        n2.setEventProcessor(tea);
        net2.getNodes().add(n2);
        
        EmbeddedEPNManager mgr=new EmbeddedEPNManager();
        
        try {
            mgr.register(net1);
            
            mgr.register(net2);
            
            mgr.unregister(TEST_NETWORK, null);
            
            java.util.List<EmbeddedChannel> ch1=mgr.getEntryPoints().get(TEST_SUBJECT);
            
            if (ch1 == null) {
                fail("Failed to get channels for test subject");
            } else if (ch1.size() != 1) {
                fail("Number of channels for test subject should be 1: "+ch1.size());
            }
            
            mgr.unregister(TEST_NETWORK2, null);
            
            java.util.List<EmbeddedChannel> ch2=mgr.getEntryPoints().get(TEST_SUBJECT);
            
            if (ch2 != null) {
                fail("No channels should exist for test subject");
            }
            
        } catch(Exception e) {
            fail("Test failed: "+e);
        }
    }

    @Test
    public void testMultipleVersionEntryPoints1() {
        Network net1=new Network();
        net1.setName(TEST_NETWORK);
        net1.setVersion(VER1);
        
        Subscription sub1=new Subscription();
        sub1.setSubject(TEST_SUBJECT);
        sub1.setNodeName(N1);
        net1.getSubscriptions().add(sub1);
        
        Node n1=new Node();
        n1.setName(N1);
        n1.setPredicate(new TestPredicate1());
        n1.setEventProcessor(new TestEventProcessorB());
        net1.getNodes().add(n1);
        
        Network net2=new Network();
        net2.setName(TEST_NETWORK);
        net2.setVersion(VER2);
        
        Subscription sub2=new Subscription();
        sub2.setSubject(TEST_SUBJECT);
        sub2.setNodeName(N2);
        net2.getSubscriptions().add(sub2);
        
        Node n2=new Node();
        n2.setName(N2);
        n2.setPredicate(new TestPredicate2());
        n2.setMaxRetries(MAX_RETRIES);
        
        TestEventProcessorC tea=new TestEventProcessorC();
        n2.setEventProcessor(tea);
        net2.getNodes().add(n2);
        
        EmbeddedEPNManager mgr=new EmbeddedEPNManager();
        
        try {
            mgr.register(net1);
            
            java.util.List<EmbeddedChannel> ch1=mgr.getEntryPoints().get(TEST_SUBJECT);
            
            if (ch1 == null) {
                fail("Failed to get channels for test subject");
            } else if (ch1.size() != 1) {
                fail("Number of channels for test subject should be 1: "+ch1.size());
            }
            
            if (!ch1.get(0).getNodeName().equals(N1)) {
                fail("Expected N1");
            }
            
            mgr.register(net2);
            
            java.util.List<EmbeddedChannel> ch2=mgr.getEntryPoints().get(TEST_SUBJECT);
            
            if (ch2 == null) {
                fail("Failed to get channels(2) for test subject");
            } else if (ch2.size() != 1) {
                fail("Number of channels(2) for test subject should be 1: "+ch2.size());
            }
            
            if (!ch2.get(0).getNodeName().equals(N2)) {
                fail("Expected N2");
            }
            
            // This should revert the entry point back to N1
            mgr.unregister(TEST_NETWORK, null);

            java.util.List<EmbeddedChannel> ch3=mgr.getEntryPoints().get(TEST_SUBJECT);
            
            if (ch3 == null) {
                fail("Failed to get channels(3) for test subject");
            } else if (ch3.size() != 1) {
                fail("Number of channels(3) for test subject should be 1: "+ch3.size());
            }
            
            if (!ch3.get(0).getNodeName().equals(N1)) {
                fail("Expected N1 again");
            }
            
            // This should revert the entry point back to N2
            mgr.unregister(TEST_NETWORK, null);

            java.util.List<EmbeddedChannel> ch4=mgr.getEntryPoints().get(TEST_SUBJECT);
            
            if (ch4 != null) {
                fail("Should be no channels for subject");
            }
            
        } catch(Exception e) {
            fail("Test failed: "+e);
        }
    }

    @Test
    public void testMultipleVersionEntryPoints2() {
        Network net1=new Network();
        net1.setName(TEST_NETWORK);
        net1.setVersion(VER1);
        
        Subscription sub1=new Subscription();
        sub1.setSubject(TEST_SUBJECT);
        sub1.setNodeName(N1);
        net1.getSubscriptions().add(sub1);
        
        Node n1=new Node();
        n1.setName(N1);
        n1.setPredicate(new TestPredicate1());
        n1.setEventProcessor(new TestEventProcessorB());
        net1.getNodes().add(n1);
        
        Network net2=new Network();
        net2.setName(TEST_NETWORK);
        net2.setVersion(VER2);
        
        Subscription sub2=new Subscription();
        sub2.setSubject(TEST_SUBJECT);
        sub2.setNodeName(N2);
        net2.getSubscriptions().add(sub2);
        
        Node n2=new Node();
        n2.setName(N2);
        n2.setPredicate(new TestPredicate2());
        n2.setMaxRetries(MAX_RETRIES);
        
        TestEventProcessorC tea=new TestEventProcessorC();
        n2.setEventProcessor(tea);
        net2.getNodes().add(n2);
        
        EmbeddedEPNManager mgr=new EmbeddedEPNManager();
        
        try {
            mgr.register(net1);
            
            java.util.List<EmbeddedChannel> ch1=mgr.getEntryPoints().get(TEST_SUBJECT);
            
            if (ch1 == null) {
                fail("Failed to get channels for test subject");
            } else if (ch1.size() != 1) {
                fail("Number of channels for test subject should be 1: "+ch1.size());
            }
            
            if (!ch1.get(0).getNodeName().equals(N1)) {
                fail("Expected N1");
            }
            
            mgr.register(net2);
            
            java.util.List<EmbeddedChannel> ch2=mgr.getEntryPoints().get(TEST_SUBJECT);
            
            if (ch2 == null) {
                fail("Failed to get channels(2) for test subject");
            } else if (ch2.size() != 1) {
                fail("Number of channels(2) for test subject should be 1: "+ch2.size());
            }
            
            if (!ch2.get(0).getNodeName().equals(N2)) {
                fail("Expected N2");
            }
            
            // This should NOT revert the entry point back to N1
            mgr.unregister(TEST_NETWORK, VER1);

            java.util.List<EmbeddedChannel> ch3=mgr.getEntryPoints().get(TEST_SUBJECT);
            
            if (ch3 == null) {
                fail("Failed to get channels(3) for test subject");
            } else if (ch3.size() != 1) {
                fail("Number of channels(3) for test subject should be 1: "+ch3.size());
            }
            
            if (!ch3.get(0).getNodeName().equals(N2)) {
                fail("Expected N2 still");
            }
            
        } catch(Exception e) {
            fail("Test failed: "+e);
        }
    }
}
