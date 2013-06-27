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

import org.junit.Test;
import org.overlord.rtgov.active.collection.ActiveChangeListener;
import org.overlord.rtgov.active.collection.ActiveMap;
import org.overlord.rtgov.active.collection.QuerySpec;
import org.overlord.rtgov.active.collection.predicate.MVEL;
import org.overlord.rtgov.active.collection.predicate.Predicate;

public class ActiveMapTest {

    private static final String TEST_ACTIVE_COLLECTION = "TestActiveCollection";
    private static final String TEST_DERIVED_ACTIVE_COLLECTION = "TestDerivedActiveCollection";

    @Test
    public void testInsertFirstObject() {
        ActiveMap map=new ActiveMap(TEST_ACTIVE_COLLECTION);
        
        TestActiveChangeListener l=new TestActiveChangeListener();
        map.addActiveChangeListener(l);
        
        map.insert("1", new TestObject(1));
        
        java.util.Iterator<Object> iter=map.iterator();
        
        if (!iter.hasNext()) {
            fail("Should be item in iterator");
        }
        
        ActiveMap.Entry entry=(ActiveMap.Entry)iter.next();
        
        if (iter.hasNext()) {
            fail("Should NOT be item in iterator");
        }
        
        if (!entry.getKey().equals("1")) {
            fail("Invalid key");
        }
        
        TestObject obj=(TestObject)entry.getValue();
        
        if (obj.getNumber() != 1) {
            fail("Incorrect test object");
        }
        
        if (l._insertedKey.size() != 1) {
            fail("Should be 1 inserted key: "+l._insertedKey.size());
        }
        
        if (!l._insertedKey.get(0).equals("1")) {
            fail("Key should be '1': "+l._insertedKey.get(0));
        }
        
        if (l._insertedValue.size() != 1) {
            fail("Should be 1 inserted value: "+l._insertedValue.size());
        }
        
        if (((TestObject)l._insertedValue.get(0)).getNumber() != 1) {
            fail("Value should be test object 1: "+l._insertedValue.get(0));
        }      
    }

    @Test
    public void testUpdateFirstObject() {
        ActiveMap map=new ActiveMap(TEST_ACTIVE_COLLECTION);
        
        TestActiveChangeListener l=new TestActiveChangeListener();
        map.addActiveChangeListener(l);
        
        map.insert("1", new TestObject(1));
        
        map.update("1", new TestObject(2));
        
        java.util.Iterator<Object> iter=map.iterator();
        
        if (!iter.hasNext()) {
            fail("Should be item in iterator");
        }
        
        ActiveMap.Entry entry=(ActiveMap.Entry)iter.next();
        
        if (iter.hasNext()) {
            fail("Should NOT be item in iterator");
        }
        
        if (!entry.getKey().equals("1")) {
            fail("Invalid key");
        }
        
        TestObject obj=(TestObject)entry.getValue();
        
        if (obj.getNumber() != 2) {
            fail("Incorrect test object");
        }
        
        if (l._updatedKey.size() != 1) {
            fail("Should be 1 updated key: "+l._updatedKey.size());
        }
        
        if (!l._updatedKey.get(0).equals("1")) {
            fail("Key should be '1': "+l._updatedKey.get(0));
        }
        
        if (l._updatedValue.size() != 1) {
            fail("Should be 1 updated value: "+l._updatedValue.size());
        }
        
        if (((TestObject)l._updatedValue.get(0)).getNumber() != 2) {
            fail("Value should be test object 2: "+l._updatedValue.get(0));
        }      
    }

    @Test
    public void testRemoveFirstObject() {
        ActiveMap map=new ActiveMap(TEST_ACTIVE_COLLECTION);
        
        TestActiveChangeListener l=new TestActiveChangeListener();
        map.addActiveChangeListener(l);
        
        map.insert("1", new TestObject(1));
        
        map.remove("1", null);
        
        java.util.Iterator<Object> iter=map.iterator();
        
        if (iter.hasNext()) {
            fail("Should NOT be item in iterator");
        }
        
        if (l._removedKey.size() != 1) {
            fail("Should be 1 removed key: "+l._removedKey.size());
        }
        
        if (!l._removedKey.get(0).equals("1")) {
            fail("Key should be '1': "+l._removedKey.get(0));
        }
        
        if (l._removedValue.size() != 1) {
            fail("Should be 1 removed value: "+l._removedValue.size());
        }
        
        if (((TestObject)l._removedValue.get(0)).getNumber() != 1) {
            fail("Value should be test object 1: "+l._removedValue.get(0));
        }      
    }

    @Test
    public void testDerivedMap() {
        
        ActiveMap map=new ActiveMap(TEST_ACTIVE_COLLECTION);
        
        // Create initial list entries
        for (int i=0; i < 10; i++) {
            map.insert(""+i, new TestObject(i));
        }
        
        Predicate predicate=new Predicate() {

            public boolean evaluate(ActiveCollectionContext context, Object item) {
                return (((TestObject)item).getNumber() % 2 == 0);
            }
            
        };
        
        ActiveMap derived=new ActiveMap(TEST_DERIVED_ACTIVE_COLLECTION, map, null, predicate, null);
        
        if (derived.getSize() != 5) {
            fail("Should be 5 entries in derived: "+derived.getSize());
        }
    }
    
    @Test
    public void testDerivedMapWithInsert() {
        
        ActiveMap map=new ActiveMap(TEST_ACTIVE_COLLECTION);
        
        // Create initial list entries
        for (int i=0; i < 10; i++) {
            map.insert(""+i, new TestObject(i));
        }
        
        Predicate predicate=new Predicate() {

			public boolean evaluate(ActiveCollectionContext context, Object item) {
				return (((TestObject)item).getNumber() % 2 == 0);
			}
        	
        };
        
        ActiveMap derived=new ActiveMap(TEST_DERIVED_ACTIVE_COLLECTION, map, null, predicate, null);
        
        TestActiveChangeListener l=new TestActiveChangeListener();
        
        derived.addActiveChangeListener(l);
        
        map.insert("10", new TestObject(11));
        map.insert("11", new TestObject(12));
        
        if (derived.getSize() != 6) {
        	fail("Derived list should have 6 items: "+derived.getSize());
        }
        
        if (l._insertedValue.size() != 1) {
        	fail("Listener should have 1 value: "+l._insertedValue.size());
        }
        
        if (((TestObject)l._insertedValue.get(0)).getNumber() != 12) {
        	fail("Expecting test object 12: "+l._insertedValue.get(0));
        }
    }

    @Test
    public void testAllItemsNormal() {
        ActiveMap map=new ActiveMap(TEST_ACTIVE_COLLECTION);
        
        TestActiveChangeListener l=new TestActiveChangeListener();
        map.addActiveChangeListener(l);
        
        int total=0;
        
        for (int i=0; i < 15; i++) {
            map.insert(""+i, new TestObject(i));
            
            total += i;
        }
        
        if (map.getSize() != 15) {
            fail("Map should have 15 items: "+map.getSize());
        }

        QuerySpec qs=new QuerySpec();
        
        java.util.List<Object> results=map.query(qs);
        
        if (results.size() != 15) {
            fail("Results should be 15: "+results.size());
        }
        
        int resultTotal=0;
        
        for (Object res : results) {
            ActiveMap.Entry entry=(ActiveMap.Entry)res;
            
            resultTotal += ((TestObject)entry.getValue()).getNumber();
        }
        
        if (total != resultTotal) {
            fail("Totals don't match");
        }
    }

    @Test
    public void testDerivedMapInactive() {
        
        ActiveMap map=new ActiveMap(TEST_ACTIVE_COLLECTION);
        
        // Create initial list entries
        for (int i=0; i < 10; i++) {
            map.insert(""+i, new TestObject(i));
        }
        
        MVEL predicate=new MVEL();
        predicate.setExpression("number % 2 == 0");
        
        java.util.Map<String,Object> props=new java.util.HashMap<String, Object>();
        props.put("active", false);
        
        ActiveMap derived=new ActiveMap(TEST_DERIVED_ACTIVE_COLLECTION, map, null, predicate, props);
        
        if (derived.getSize() != 5) {
            fail("Should be 5 entries in derived: "+derived.getSize());
        }
        
        if (!derived.containsKey("4")) {
            fail("Should have key '4'");
        }
        
        if (!derived.containsValue(new TestObject(4))) {
            fail("Should have value 4");
        }
        
        // Change predicate
        predicate.setExpression("number < 3");
        
        if (derived.getSize() != 3) {
            fail("NOW Should be 3 entries in derived: "+derived.getSize());
        }
        
        if (!derived.containsKey("0")) {
            fail("Should have key '0'");
        }
        
        if (!derived.containsKey("1")) {
            fail("Should have key '1'");
        }
        
        if (!derived.containsKey("2")) {
            fail("Should have key '2'");
        }
        
        if (derived.containsKey("3")) {
            fail("Should NOT have key '3'");
        }
        
        if (derived.containsKey("4")) {
            fail("Should NOT have key '4'");
        }
        
        if (!derived.containsValue(new TestObject(0))) {
            fail("Should have value 0");
        }
        
        if (!derived.containsValue(new TestObject(1))) {
            fail("Should have value 1");
        }
        
        if (!derived.containsValue(new TestObject(2))) {
            fail("Should have value 2");
        }
        
        if (derived.containsValue(new TestObject(3))) {
            fail("Should NOT have value 3");
        }
        
        if (derived.containsValue(new TestObject(4))) {
            fail("Should NOT have value 4");
        }
    }
    
    public static class TestObject {
        
        private int _number=0;
        
        public TestObject(int num) {
            _number = num;
        }
        
        public int getNumber() {
            return (_number);
        }
        
        public int hashCode() {
            return (_number);
        }
        
        public boolean equals(Object obj) {
            boolean ret=false;
            
            if (obj instanceof TestObject) {
                ret = (_number == ((TestObject)obj).getNumber());
            }
            
            return (ret);
        }
        
        public String toString() {
            return ("TestObject["+_number+"]");
        }
    }
    
    public static class TestActiveChangeListener implements ActiveChangeListener {
        
        protected java.util.List<Object> _insertedKey=new java.util.ArrayList<Object>();
        protected java.util.List<Object> _insertedValue=new java.util.ArrayList<Object>();
        protected java.util.List<Object> _updatedKey=new java.util.ArrayList<Object>();
        protected java.util.List<Object> _updatedValue=new java.util.ArrayList<Object>();
        protected java.util.List<Object> _removedKey=new java.util.ArrayList<Object>();
        protected java.util.List<Object> _removedValue=new java.util.ArrayList<Object>();        

        public void inserted(Object key, Object value) {
            _insertedKey.add(key);
            _insertedValue.add(value);
        }

        public void updated(Object key, Object value) {
            _updatedKey.add(key);
            _updatedValue.add(value);
        }

        public void removed(Object key, Object value) {
            _removedKey.add(key);
            _removedValue.add(value);
        }
        
    }
}
