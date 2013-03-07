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
package org.overlord.rtgov.active.collection;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.active.collection.ActiveChangeListener;
import org.overlord.rtgov.active.collection.ActiveMap;
import org.overlord.rtgov.active.collection.QuerySpec;
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

			public boolean evaluate(Object item) {
				return (((TestObject)item).getNumber() % 2 == 0);
			}
        	
        };
        
        ActiveMap derived=new ActiveMap(TEST_DERIVED_ACTIVE_COLLECTION, map, predicate);
        
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

			public boolean evaluate(Object item) {
				return (((TestObject)item).getNumber() % 2 == 0);
			}
        	
        };
        
        ActiveMap derived=new ActiveMap(TEST_DERIVED_ACTIVE_COLLECTION, map, predicate);
        
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
