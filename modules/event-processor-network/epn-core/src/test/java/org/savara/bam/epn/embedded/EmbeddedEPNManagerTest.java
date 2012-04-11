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
package org.savara.bam.epn.embedded;

import static org.junit.Assert.*;

import org.junit.Test;
import org.savara.bam.epn.Destination;
import org.savara.bam.epn.Network;
import org.savara.bam.epn.Node;
import org.savara.bam.epn.internal.EventList;
import org.savara.bam.epn.testdata.TestEvent1;
import org.savara.bam.epn.testdata.TestEvent2;
import org.savara.bam.epn.testdata.TestEventProcessorA;
import org.savara.bam.epn.testdata.TestEventProcessorB;
import org.savara.bam.epn.testdata.TestEventProcessorC;
import org.savara.bam.epn.testdata.TestPredicate1;
import org.savara.bam.epn.testdata.TestPredicate2;

public class EmbeddedEPNManagerTest {

    private static final int MAX_RETRIES = 4;
    private static final String N1 = "N1";
    private static final String N2 = "N2";
    private static final String TEST_NETWORK = "TestNetwork";

    @Test
    public void testEventTransformedForChild() {
        Network net=new Network();
        net.setName(TEST_NETWORK);
        net.setRootNodeName(N1);
        net.setTimestamp(0);
        
        Node n1=new Node();
        n1.setPredicate(new TestPredicate1());
        n1.setEventProcessor(new TestEventProcessorB());
        n1.getDestinations().add(new Destination(TEST_NETWORK, N2));
        net.getNodes().put(N1, n1);
        
        Node n2=new Node();
        n2.setPredicate(new TestPredicate2());
        
        TestEventProcessorA tea=new TestEventProcessorA();
        n2.setEventProcessor(tea);
        net.getNodes().put(N2, n2);
        
        EmbeddedEPNManager mgr=new EmbeddedEPNManager();
        
        try {
            mgr.register(net);
            
            EventList events=new EventList();
            
            TestEvent1 te1=new TestEvent1(25);
            events.add(te1);
            
            mgr.enqueue(TEST_NETWORK, events);
            
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
            fail("Test failed: "+e);
        }
    }

    @Test
    public void testEventRetries() {
        Network net=new Network();
        net.setName(TEST_NETWORK);
        net.setRootNodeName(N1);
        net.setTimestamp(0);
        
        Node n1=new Node();
        n1.setPredicate(new TestPredicate1());
        n1.setEventProcessor(new TestEventProcessorB());
        n1.getDestinations().add(new Destination(TEST_NETWORK, N2));
        net.getNodes().put(N1, n1);
        
        Node n2=new Node();
        n2.setPredicate(new TestPredicate2());
        n2.setMaxRetries(MAX_RETRIES);
        
        TestEventProcessorC tea=new TestEventProcessorC();
        n2.setEventProcessor(tea);
        net.getNodes().put(N2, n2);
        
        EmbeddedEPNManager mgr=new EmbeddedEPNManager();
        
        try {
            mgr.register(net);
            
            EventList events=new EventList();
            
            TestEvent1 te1=new TestEvent1(25);
            events.add(te1);
            
            mgr.enqueue(TEST_NETWORK, events);
            
            // Need to delay awaiting processing via other threads
            Thread.sleep(1000);
            
            if (tea._retryCount != MAX_RETRIES+1) {
                fail("Expecting "+(MAX_RETRIES+1)+" retries: "+tea._retryCount);
            }
        } catch(Exception e) {
            fail("Test failed: "+e);
        }
    }
}
