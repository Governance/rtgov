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
package org.savara.bam.epn.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.savara.bam.epn.Destination;
import org.savara.bam.epn.Network;
import org.savara.bam.epn.Node;
import org.savara.bam.epn.testdata.TestEventProcessor1;
import org.savara.bam.epn.testdata.TestEventProcessor2;
import org.savara.bam.epn.testdata.TestEventProcessor3;
import org.savara.bam.epn.testdata.TestPredicate1;
import org.savara.bam.epn.testdata.TestPredicate2;
import org.savara.bam.epn.util.NetworkUtil;
//import org.savara.sam.epn.testdata.TestEvent;

public class NetworkUtilTest {

    @Test
    public void testSerializeEPN() {
        Network epn=new Network();
        
        epn.setName("Test");
        epn.setTimestamp(123456);
        epn.setRootNodeName("N0");
        
        // Event destinations
        Destination ed1=new Destination();
        ed1.setNetwork("Test");
        ed1.setNode("N1");
        
        Destination ed2=new Destination();
        ed2.setNetwork("Test");
        ed2.setNode("N2");
        
        // Node 0
        Node n0=new Node();
        epn.getNodes().put("N0", n0);
        
        n0.getDestinations().add(ed1);
        n0.getDestinations().add(ed2);
        n0.setEventProcessor(new TestEventProcessor1());
        n0.setPredicate(new TestPredicate1());    
        
        // Node 1
        Node n1=new Node();
        epn.getNodes().put("N1", n1);
        
        TestEventProcessor2 ep2=new TestEventProcessor2();
        n1.setEventProcessor(ep2);
        TestPredicate2 tp2=new TestPredicate2();
        tp2.setSomeProperty("TestProperty");
        n1.setPredicate(tp2);
        
        // Node 2
        Node n2=new Node();
        epn.getNodes().put("N2", n2);
        
        TestEventProcessor3 ep3=new TestEventProcessor3();
        n2.setEventProcessor(ep3);
        
        try {
            NetworkUtil.serialize(epn);            
        } catch(Exception e) {
            fail("Failed to serialize: "+e);
        }
    }

    @Test
    public void testDeserializeEPN() {
        try {
            java.io.InputStream is=NetworkUtilTest.class.getResourceAsStream("/jsondata/TestNetwork1.json");
            
            byte[] b = new byte[is.available()];
            is.read(b);
            
            is.close();
            
            Network network=NetworkUtil.deserialize(b);
            
            if (network.getNodes().size() != 3) {
                fail("Number of nodes not 3: "+network.getNodes().size());
            }
            
            Node n1=network.getNodes().get("N0");
            Node n2=network.getNodes().get("N1");
            Node n3=network.getNodes().get("N2");
            
            if (n1.getPredicate() == null) {
                fail("Predicate 1 should not be null");
            }
            
            if (!(n1.getPredicate() instanceof TestPredicate1)) {
                fail("Predicate 1 not correct class");
            }
            
            if (n1.getEventProcessor() == null) {
                fail("Event Processor 1 should not be null");
            }
            
            if (!(n1.getEventProcessor() instanceof TestEventProcessor1)) {
                fail("Event Processor 1 not correct class");
            }
            
            if (n2.getPredicate() == null) {
                fail("Predicate 2 should not be null");
            }
            
            if (!(n2.getPredicate() instanceof TestPredicate2)) {
                fail("Predicate 2 not correct class");
            }
            
            if (n2.getEventProcessor() == null) {
                fail("Event Processor 2 should not be null");
            }
            
            if (!(n2.getEventProcessor() instanceof TestEventProcessor2)) {
                fail("Event Processor 2 not correct class");
            }
            
            if (n3.getPredicate() != null) {
                fail("Predicate 3 should be null");
            }
            
            if (n3.getEventProcessor() == null) {
                fail("Event Processor 3 should not be null");
            }
            
            if (!(n3.getEventProcessor() instanceof TestEventProcessor3)) {
                fail("Event Processor 3 not correct class");
            }
            
        } catch(Exception e) {
            e.printStackTrace();
            fail("Failed to deserialize: "+e);
        }

    }
}
