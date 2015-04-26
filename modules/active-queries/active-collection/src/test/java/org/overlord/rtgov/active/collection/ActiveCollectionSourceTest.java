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
package org.overlord.rtgov.active.collection;

import static org.junit.Assert.*;

import java.io.Serializable;

import org.junit.Test;
import org.overlord.rtgov.active.collection.AbstractActiveChangeListener;
import org.overlord.rtgov.active.collection.ActiveCollection;
import org.overlord.rtgov.active.collection.ActiveCollectionFactory;
import org.overlord.rtgov.active.collection.ActiveCollectionSource;
import org.overlord.rtgov.active.collection.ActiveCollectionType;
import org.overlord.rtgov.active.collection.ActiveList;
import org.overlord.rtgov.active.collection.predicate.MVEL;
import org.overlord.rtgov.active.collection.predicate.Predicate;

public class ActiveCollectionSourceTest {

    private static final String T_OBJ3 = "TObj3";
    private static final String T_OBJ2 = "TObj2";
    private static final String T_OBJ1 = "TObj1";
    private static final String TEST_ACTIVE_LIST = "TestActiveList";
    private static final String TEST_OTHER_LIST = "TestOtherList";

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
            acs.init(null);
            
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
            acs.init(null);       
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
            acs.init(null);
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
            acs.init(null);
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
            acs.init(null);
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
        
        acs.getActiveCollection().doInsert(null, updateObject);

        TestActiveChangeListener l=new TestActiveChangeListener();
        
        acs.getActiveCollection().addActiveChangeListener(l);
        
        acs.setType(ActiveCollectionType.List);
        
        acs.setMaintenanceScript("scripts/Maintain.mvel");
        
        try {
            acs.init(null);
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
        acs.getActiveCollection().doInsert(null, to1);

        TestObject to2=new TestObject(T_OBJ2, 60);        
        acs.getActiveCollection().doInsert(null, to2);

        TestActiveChangeListener l=new TestActiveChangeListener();
        
        acs.getActiveCollection().addActiveChangeListener(l);
        
        acs.setType(ActiveCollectionType.List);
        
        acs.setMaintenanceScript("scripts/Maintain.mvel");
        
        try {
            acs.init(null);
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
            acs.init(null);
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
    
    @Test
    public void testDerived() {
        ActiveCollectionSource acs=new ActiveCollectionSource();
        
        acs.setActiveCollection(new ActiveList(TEST_ACTIVE_LIST));
        acs.setName(TEST_ACTIVE_LIST);
        acs.setType(ActiveCollectionType.List);
        
        ActiveCollectionSource.DerivedDefinition dd=new ActiveCollectionSource.DerivedDefinition();
        dd.setName("Derived");
        dd.setPredicate(new Predicate() {
            public boolean evaluate(ActiveCollectionContext context, Object item) {
                return (item == T_OBJ2);
            }           
        });
        acs.getDerived().add(dd);
        
        try {
            acs.init(null);
        } catch(Exception e) {
            fail("Failed to initialize active collection source: "+e);
        }

        if (acs.getDerivedActiveCollections().size() != 1) {
            fail("Expecting a single derived collection: "+acs.getDerivedActiveCollections().size());
        }
        
        ActiveCollection ddac=acs.getDerivedActiveCollections().get(0);
        
        acs.insert(null, T_OBJ1);
        acs.insert(null, T_OBJ2);
        
        if (ddac.getSize() != 1) {
            fail("Derived collection should only have 1 item: "+ddac.getSize());
        }
        
        Object item=ddac.iterator().next();
        
        if (item != T_OBJ2) {
            fail("Item should be '"+T_OBJ2+"': "+item);
        }
        
        try {
            acs.close();
        } catch(Exception e) {
            fail("Failed to close: "+e);
        }
    }
    
    @Test
    public void testDerivedUsingContextList() {
        ActiveCollectionSource acs=new ActiveCollectionSource();
        
        acs.setActiveCollection(new ActiveList(TEST_ACTIVE_LIST));
        acs.setName(TEST_ACTIVE_LIST);
        acs.setType(ActiveCollectionType.List);
        
        MVEL pred=new MVEL();
        pred.setExpression("list = context.getList(\""+TEST_OTHER_LIST+"\"); if (list == null) { return false; } return !list.contains(name);");
        
        ActiveCollectionSource.DerivedDefinition dd=new ActiveCollectionSource.DerivedDefinition();
        dd.setName("Derived");
        dd.setPredicate(pred);
        acs.getDerived().add(dd);
        
        final ActiveList testOtherList=new ActiveList(TEST_OTHER_LIST);
        testOtherList.doInsert(null, T_OBJ2);
        
        final ActiveCollectionContext context=new ActiveCollectionContext() {

            public ActiveList getList(String name) {
                ActiveList ret=TEST_OTHER_LIST.equals(name) ? testOtherList : null;
                return (ret);
            }

            public ActiveMap getMap(String name) {
                return null;
            }
            
        };
        
        try {
            acs.init(context);
        } catch(Exception e) {
            fail("Failed to initialize active collection source: "+e);
        }

        if (acs.getDerivedActiveCollections().size() != 1) {
            fail("Expecting a single derived collection: "+acs.getDerivedActiveCollections().size());
        }
        
        ActiveCollection ddac=acs.getDerivedActiveCollections().get(0);
        
        TestObject to1=new TestObject(T_OBJ1, 1);
        TestObject to2=new TestObject(T_OBJ2, 2);
        TestObject to3=new TestObject(T_OBJ3, 3);
        
        acs.insert(null, to1);
        acs.insert(null, to2);
        acs.insert(null, to3);
        
        if (ddac.getSize() != 2) {
            fail("Derived collection should only have 2 items: "+ddac.getSize());
        }
        
        java.util.Iterator<Object> iter=ddac.iterator();
        
        Object item1=iter.next();
        
        if (item1 != to1) {
            fail("Item1 should be '"+to1.getName()+"': "+item1);
        }
        
        Object item2=iter.next();
        
        if (item2 != to3) {
            fail("Item2 should be '"+to3.getName()+"': "+item2);
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
    
