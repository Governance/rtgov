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
package org.overlord.bam.active.collection.epn;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.util.List;

import org.junit.Test;
import org.overlord.bam.active.collection.ActiveCollectionType;
import org.overlord.bam.active.collection.ActiveList;
import org.overlord.bam.active.collection.epn.EPNActiveCollectionSource;
import org.overlord.bam.epn.EPNManager;
import org.overlord.bam.epn.EventList;
import org.overlord.bam.epn.Network;
import org.overlord.bam.epn.NetworkListener;
import org.overlord.bam.epn.NodeListener;
import org.overlord.bam.epn.NotificationType;

public class EPNActiveCollectionSourceTest {

    private static final String T_OBJ3 = "TObj3";
    private static final String T_OBJ2 = "TObj2";
    private static final String T_OBJ1 = "TObj1";
    private static final String TEST_NODE1 = "TestNode1";
    private static final String TEST_NODE2 = "TestNode2";
    private static final String TEST_NETWORK = "TestNetwork";
    private static final String TEST_ACTIVE_LIST = "TestActiveList";

    @Test
    public void testNodeAndTypeFiltering() {
        EPNActiveCollectionSource acs=new EPNActiveCollectionSource();
        
        acs.setActiveCollection(new ActiveList(TEST_ACTIVE_LIST));
        acs.setName(TEST_ACTIVE_LIST);
        
        acs.setNetwork(TEST_NETWORK);
        acs.setNode(TEST_NODE1);
        
        acs.setNotifyType(NotificationType.Results);
        
        acs.setType(ActiveCollectionType.List);
        
        TestEPNManager mgr=new TestEPNManager();
        acs.setEPNManager(mgr);
        
        try {
            acs.init();
        } catch(Exception e) {
            fail("Failed to initialize active collection source: "+e);
        }
        
        java.util.List<Serializable> resultList=new java.util.ArrayList<Serializable>();
        
        java.util.List<Serializable> eventList=new java.util.ArrayList<Serializable>();
        eventList.add(new TestObject(T_OBJ1, 1));
        eventList.add(new TestObject(T_OBJ2, 2));
        eventList.add(new TestObject(T_OBJ3, 3));
        
        resultList.addAll(eventList);
        
        EventList events=new EventList(eventList);
        
        mgr.publish(TEST_NODE1, NotificationType.Results, events);
        
        java.util.List<Serializable> eventList2=new java.util.ArrayList<Serializable>();
        eventList2.add(new TestObject("TObj21", 21));
        eventList2.add(new TestObject("TObj22", 22));
        eventList2.add(new TestObject("TObj23", 23));
        
        EventList events2=new EventList(eventList2);
        
        mgr.publish(TEST_NODE2, NotificationType.Results, events2);
        
        java.util.List<Serializable> eventList3=new java.util.ArrayList<Serializable>();
        eventList3.add(new TestObject("TObj31", 31));
        eventList3.add(new TestObject("TObj32", 32));
        eventList3.add(new TestObject("TObj33", 33));

        resultList.addAll(eventList3);

        EventList events3=new EventList(eventList3);
        
        mgr.publish(TEST_NODE1, NotificationType.Results, events3);
        
        java.util.List<Serializable> eventList4=new java.util.ArrayList<Serializable>();
        eventList4.add(new TestObject("TObj41", 41));
        eventList4.add(new TestObject("TObj42", 42));
        eventList4.add(new TestObject("TObj43", 43));
        
        EventList events4=new EventList(eventList4);
        
        mgr.publish(TEST_NODE1, NotificationType.Processed, events4);
        
        // Review results
        ActiveList al=(ActiveList)acs.getActiveCollection();
        
        if (al.getSize() != 6) {
            fail("Should only be 6 events: "+al.getSize());
        }
        
        for (Object obj : al) {
            if (obj.equals(resultList.get(0))) {
                resultList.remove(0);
            } else {
                fail("Failed to match: "+obj+" with "+resultList.get(0));
            }
        }
        
    }

    @Test
    public void testGroupBy() {
        EPNActiveCollectionSource acs=new EPNActiveCollectionSource();
        
        acs.setActiveCollection(new ActiveList(TEST_ACTIVE_LIST));
        acs.setName(TEST_ACTIVE_LIST);
        
        acs.setNetwork(TEST_NETWORK);
        acs.setNode(TEST_NODE1);
        
        acs.setNotifyType(NotificationType.Results);
        
        acs.setType(ActiveCollectionType.List);
        
        acs.setAggregationDuration(1000);
        acs.setGroupBy("name");
        
        TestEPNManager mgr=new TestEPNManager();
        acs.setEPNManager(mgr);
        
        try {
            acs.init();
        } catch(Exception e) {
            fail("Failed to initialize active collection source: "+e);
        }
        
        java.util.List<Serializable> resultList=new java.util.ArrayList<Serializable>();
        
        java.util.List<Serializable> eventList=new java.util.ArrayList<Serializable>();
        eventList.add(new TestObject(T_OBJ1, 11));
        eventList.add(new TestObject(T_OBJ2, 21));
        eventList.add(new TestObject(T_OBJ3, 31));
        eventList.add(new TestObject(T_OBJ1, 12));
        eventList.add(new TestObject(T_OBJ2, 22));
        eventList.add(new TestObject(T_OBJ1, 13));
        eventList.add(new TestObject(T_OBJ1, 14));
        eventList.add(new TestObject(T_OBJ2, 23));
        
        resultList.addAll(eventList);
        
        EventList events=new EventList(eventList);
        
        mgr.publish(TEST_NODE1, NotificationType.Results, events);

        java.util.Map<Object, java.util.List<Object>> groupedEvents=acs.getGroupedEvents();
        
        if (groupedEvents.keySet().size() != 3) {
            fail("Expecting 3 keys: "+groupedEvents.keySet().size());
        }
        
        java.util.List<Object> results1=groupedEvents.get(T_OBJ1);
        
        if (results1 == null) {
            fail("Results for "+T_OBJ1+" not found");
        } else if (results1.size() != 4) {
            fail("Results for "+T_OBJ1+" should have 4 events: "+results1.size());
        }
        
        java.util.List<Object> results2=groupedEvents.get(T_OBJ2);
        
        if (results2 == null) {
            fail("Results for "+T_OBJ2+" not found");
        } else if (results2.size() != 3) {
            fail("Results for "+T_OBJ2+" should have 3 events: "+results2.size());
        }
        
        java.util.List<Object> results3=groupedEvents.get(T_OBJ3);
        
        if (results3 == null) {
            fail("Results for "+T_OBJ3+" not found");
        } else if (results3.size() != 1) {
            fail("Results for "+T_OBJ3+" should have 1 event: "+results3.size());
        }
        
        // Wait for 2 seconds, and check list - it should be clear, indicating
        // aggregator initiated publication of aggregated events
        try {
            synchronized (this) {
                wait(2000);
            }
        } catch(Exception e) {
            fail("Failed to wait: "+e);
        }
        
        if (acs.getGroupedEvents().size() != 0) {
            fail("Aggregated events should have been cleared: "+acs.getGroupedEvents().size());
        }
    }

    @Test
    public void testPublishAggregatedEvents() {
        EPNActiveCollectionSource acs=new EPNActiveCollectionSource();
        
        acs.setActiveCollection(new ActiveList(TEST_ACTIVE_LIST));
        acs.setName(TEST_ACTIVE_LIST);
        
        acs.setNetwork(TEST_NETWORK);
        acs.setNode(TEST_NODE1);
        
        acs.setNotifyType(NotificationType.Results);
        
        acs.setType(ActiveCollectionType.List);
        
        acs.setAggregationDuration(1000);
        acs.setGroupBy("name");
        
        acs.setAggregationScript("scripts/Aggregate.mvel");
        
        TestEPNManager mgr=new TestEPNManager();
        acs.setEPNManager(mgr);
        
        try {
            acs.init();
        } catch(Exception e) {
            fail("Failed to initialize active collection source: "+e);
        }
        
        java.util.List<Object> list1=new java.util.ArrayList<Object>();
        list1.add(new TestObject(T_OBJ1, 50));
        list1.add(new TestObject(T_OBJ1, 60));
        list1.add(new TestObject(T_OBJ1, 40));
        
        acs.getGroupedEvents().put(T_OBJ1, list1);
        
        acs.publishAggregateEvents();
        
        if (acs.getActiveCollection().getSize() != 1) {
            fail("Active collection should have 1 entry: "+acs.getActiveCollection().getSize());
        }
        
        TestObject to=(TestObject)((ActiveList)acs.getActiveCollection()).iterator().next();
        
        if (to.getAvg() != 50) {
            fail("Avg was not 50: "+to.getAvg());
        }
        
        if (to.getMin() != 40) {
            fail("Min was not 40: "+to.getMin());
        }
        
        if (to.getMax() != 60) {
            fail("Max was not 60: "+to.getMax());
        }
        
        if (!to.getName().equals(T_OBJ1)) {
            fail("Name should be "+T_OBJ1+": "+to.getName());
        }
    }
    
    public class TestEPNManager implements EPNManager {
        
        private java.util.List<NodeListener> _nodeListeners=new java.util.ArrayList<NodeListener>();
        private java.util.List<NetworkListener> _networkListeners=new java.util.ArrayList<NetworkListener>();

        public void register(Network network) throws Exception {
        }

        public void unregister(String networkName, String version)
                throws Exception {
        }

        public void addNodeListener(String network, NodeListener l) {
            _nodeListeners.add(l);
        }

        public void removeNodeListener(String network, NodeListener l) {
            _nodeListeners.remove(l);
        }
        
        public void addNetworkListener(NetworkListener l) {
            _networkListeners.add(l);
        }

        public void removeNetworkListener(NetworkListener l) {
            _networkListeners.remove(l);
        }
        
        public void publish(String node, NotificationType type, EventList events) {
            for (NodeListener l : _nodeListeners) {
                l.notify(TEST_NETWORK, null, node, type, events);
            }
        }

        public void publish(String subject, List<? extends Serializable> events)
                throws Exception {
        }

        public void close() throws Exception {
        }
        
    }
}
