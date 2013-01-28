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
package org.overlord.bam.active.collection;

import static org.junit.Assert.*;

import java.io.Serializable;

import org.junit.Test;
import org.overlord.bam.active.collection.ActiveList;

public class ActiveCollectionSourceTest {

    private static final String T_OBJ3 = "TObj3";
    private static final String T_OBJ2 = "TObj2";
    private static final String T_OBJ1 = "TObj1";
    private static final String TEST_ACTIVE_LIST = "TestActiveList";

    @Test
    public void testActiveChangeListenerFailConfig() {
        
        ActiveCollectionSource acs=new ActiveCollectionSource();
        
        acs.getActiveChangeListeners().add(new TestActiveChangeListener());
        
        acs.setFactory(new ActiveCollectionFactory() {
            @Override
            public ActiveCollection createActiveCollection(ActiveCollectionSource acs) {
                return (null);
            }
        });
        
        try {
            acs.init();
            
        } catch (Exception e) {
            fail("Should not have failed init: "+e);
        }
        
        // Need to pre-empt creation if lazy instantiation
        if (acs.getActiveCollection() != null) {
            fail("Active collection should be null");
        }
    
    }
    
    @Test
    public void testActiveChangeListenerRegistered() {
        
        ActiveCollectionSource acs=new ActiveCollectionSource();
        
        acs.getActiveChangeListeners().add(new TestActiveChangeListener());

        ActiveList list=new ActiveList("TestList");
        
        acs.setActiveCollection(list);
       
        if (list.getActiveChangeListeners().size() != 0) {
            fail("Should be 0 listeners: "+list.getActiveChangeListeners().size());
        }

        try {
            acs.init();       
        } catch (Exception e) {
            fail("Failed to initialize the active collection source: "+e);
        }
        
        if (list.getActiveChangeListeners().size() != 1) {
            fail("Should be 1 listener: "+list.getActiveChangeListeners().size());
        }
        
        try {
            acs.close();       
        } catch (Exception e) {
            fail("Failed to close the active collection source: "+e);
        }
        
        if (list.getActiveChangeListeners().size() != 0) {
            fail("Should be 0 listeners again (after close): "+list.getActiveChangeListeners().size());
        }
    }
    
    @Test
    public void testGroupBy() {
        ActiveCollectionSource acs=new ActiveCollectionSource();
        
        acs.setActiveCollection(new ActiveList(TEST_ACTIVE_LIST));
        acs.setName(TEST_ACTIVE_LIST);
        
        acs.setType(ActiveCollectionType.List);
        
        acs.setAggregationDuration(1000);
        acs.setGroupBy("name");
        
        try {
            acs.init();
        } catch(Exception e) {
            fail("Failed to initialize active collection source: "+e);
        }
        
        java.util.List<Serializable> eventList=new java.util.ArrayList<Serializable>();
        eventList.add(new TestObject(T_OBJ1, 11));
        eventList.add(new TestObject(T_OBJ2, 21));
        eventList.add(new TestObject(T_OBJ3, 31));
        eventList.add(new TestObject(T_OBJ1, 12));
        eventList.add(new TestObject(T_OBJ2, 22));
        eventList.add(new TestObject(T_OBJ1, 13));
        eventList.add(new TestObject(T_OBJ1, 14));
        eventList.add(new TestObject(T_OBJ2, 23));
        
        for (Serializable event : eventList) {
            acs.aggregateEvent(event);
        }
        
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
        ActiveCollectionSource acs=new ActiveCollectionSource();
        
        acs.setActiveCollection(new ActiveList(TEST_ACTIVE_LIST));
        acs.setName(TEST_ACTIVE_LIST);
        
        acs.setType(ActiveCollectionType.List);
        
        acs.setAggregationDuration(1000);
        acs.setGroupBy("name");
        
        acs.setAggregationScript("scripts/Aggregate.mvel");
        
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
    
    @Test
    public void testMaintainInsert() {
        ActiveCollectionSource acs=new ActiveCollectionSource();
        
        acs.setActiveCollection(new ActiveList(TEST_ACTIVE_LIST));
        acs.setName(TEST_ACTIVE_LIST);
        
        TestActiveChangeListener l=new TestActiveChangeListener();
        
        acs.getActiveCollection().addActiveChangeListener(l);
        
        acs.setType(ActiveCollectionType.List);
        
        acs.setMaintenanceScript("scripts/Maintain.mvel");
        
        try {
            acs.init();
        } catch(Exception e) {
            fail("Failed to initialize active collection source: "+e);
        }
        
        acs.maintainEntry(null, new TestObject(T_OBJ1, 50));
        
        if (acs.getActiveCollection().getSize() != 1) {
            fail("Active collection should have 1 entry: "+acs.getActiveCollection().getSize());
        }
        
        TestObject to=(TestObject)((ActiveList)acs.getActiveCollection()).iterator().next();
        
        if (to.getAvg() != 50) {
            fail("Avg was not 50: "+to.getAvg());
        }
        
        if (!to.getName().equals(T_OBJ1)) {
            fail("Name should be "+T_OBJ1+": "+to.getName());
        }
        
        if (l._insertedValue.size() != 1) {
            fail("Expecting 1 inserted value");
        }
    }
    
    @Test
    public void testMaintainUpdate() {
        ActiveCollectionSource acs=new ActiveCollectionSource();
        
        acs.setActiveCollection(new ActiveList(TEST_ACTIVE_LIST));
        acs.setName(TEST_ACTIVE_LIST);
        
        TestObject updateObject=new TestObject();
        
        acs.getActiveCollection().insert(null, updateObject);

        TestActiveChangeListener l=new TestActiveChangeListener();
        
        acs.getActiveCollection().addActiveChangeListener(l);
        
        acs.setType(ActiveCollectionType.List);
        
        acs.setMaintenanceScript("scripts/Maintain.mvel");
        
        try {
            acs.init();
        } catch(Exception e) {
            fail("Failed to initialize active collection source: "+e);
        }
        
        acs.maintainEntry(null, new TestObject(T_OBJ1, 50));
        
        if (acs.getActiveCollection().getSize() != 1) {
            fail("Active collection should have 1 entry: "+acs.getActiveCollection().getSize());
        }
        
        TestObject to=(TestObject)((ActiveList)acs.getActiveCollection()).iterator().next();
        
        if (to.getAvg() != 50) {
            fail("Avg was not 50: "+to.getAvg());
        }
        
        if (!to.getName().equals(T_OBJ1)) {
            fail("Name should be "+T_OBJ1+": "+to.getName());
        }
        
        if (l._updatedValue.size() != 1) {
            fail("Expecting 1 updated value");
        }
        
        if (!updateObject.getName().equals("UPDATED")) {
            fail("Object not updated");
        }
    }
    
    @Test
    public void testMaintainRemove() {
        ActiveCollectionSource acs=new ActiveCollectionSource();
        
        acs.setActiveCollection(new ActiveList(TEST_ACTIVE_LIST));
        acs.setName(TEST_ACTIVE_LIST);
        
        TestObject to1=new TestObject(T_OBJ1, 50);        
        acs.getActiveCollection().insert(null, to1);

        TestObject to2=new TestObject(T_OBJ2, 60);        
        acs.getActiveCollection().insert(null, to2);

        TestActiveChangeListener l=new TestActiveChangeListener();
        
        acs.getActiveCollection().addActiveChangeListener(l);
        
        acs.setType(ActiveCollectionType.List);
        
        acs.setMaintenanceScript("scripts/Maintain.mvel");
        
        try {
            acs.init();
        } catch(Exception e) {
            fail("Failed to initialize active collection source: "+e);
        }
        
        acs.maintainEntry(null, new TestObject(T_OBJ3, 70));
        
        if (acs.getActiveCollection().getSize() != 1) {
            fail("Active collection should have 1 entries: "+acs.getActiveCollection().getSize());
        }
        
        TestObject to=(TestObject)((ActiveList)acs.getActiveCollection()).iterator().next();
        
        if (to.getAvg() != 60) {
            fail("Avg was not 60: "+to.getAvg());
        }
        
        if (!to.getName().equals(T_OBJ2)) {
            fail("Name should be "+T_OBJ2+": "+to.getName());
        }
        
        if (l._removedValue.size() != 1) {
            fail("Expecting 1 removed value");
        }
    }
    
    @Test
    public void testScheduled() {
        ActiveCollectionSource acs=new ActiveCollectionSource();
        
        acs.setActiveCollection(new ActiveList(TEST_ACTIVE_LIST));
        acs.setName(TEST_ACTIVE_LIST);
        acs.setType(ActiveCollectionType.List);
        
        acs.setScheduledScript("scripts/Scheduled.mvel");
        acs.setScheduledInterval(5000);
        
        acs.getVariables().put("counter", 0);
        
        try {
            acs.init();
        } catch(Exception e) {
            fail("Failed to initialize active collection source: "+e);
        }
        
        try {
            synchronized (this) {
                wait(7000);
            }
        } catch (Exception e) {
            fail("Failed to wait: "+e);
        }
        
        if (((Integer)acs.getVariables().get("counter")).intValue() != 2) {
            fail("Counter should be 2: "+((Integer)
                    acs.getVariables().get("counter")).intValue());
        }
        
        try {
            acs.close();
        } catch(Exception e) {
            fail("Failed to close: "+e);
        }
    }
    
    public static class TestActiveChangeListener extends AbstractActiveChangeListener {
        
        protected java.util.List<Object> _insertedKey=new java.util.ArrayList<Object>();
        protected java.util.List<Object> _insertedValue=new java.util.ArrayList<Object>();
        protected java.util.List<Object> _upatedKey=new java.util.ArrayList<Object>();
        protected java.util.List<Object> _updatedValue=new java.util.ArrayList<Object>();
        protected java.util.List<Object> _removedKey=new java.util.ArrayList<Object>();
        protected java.util.List<Object> _removedValue=new java.util.ArrayList<Object>();        

        public void inserted(Object key, Object value) {
            _insertedKey.add(key);
            _insertedValue.add(value);
        }

        public void updated(Object key, Object value) {
            _upatedKey.add(key);
            _updatedValue.add(value);
        }

        public void removed(Object key, Object value) {
            _removedKey.add(key);
            _removedValue.add(value);
        }
        
    }
}
    
