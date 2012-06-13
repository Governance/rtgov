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

import org.junit.Test;
import org.overlord.bam.active.collection.ActiveChangeListener;
import org.overlord.bam.active.collection.ActiveList;

public class ActiveListTest {

    private static final String TEST_ACTIVE_COLLECTION = "TestActiveCollection";
    private static final String TEST_DERIVED_ACTIVE_COLLECTION = "TestDerivedActiveCollection";

    @Test
    public void testInsertFirstObject() {
        ActiveList list=new ActiveList(TEST_ACTIVE_COLLECTION);
        
        TestActiveChangeListener l=new TestActiveChangeListener();
        list.addActiveChangeListener(l);
        
        list.insert(null, new TestObject(1));
        
        java.util.Iterator<Object> iter=list.iterator();
        
        if (!iter.hasNext()) {
            fail("Should be item in iterator");
        }
        
        TestObject obj=(TestObject)iter.next();
        
        if (iter.hasNext()) {
            fail("Should NOT be item in iterator");
        }
        
        if (obj.getNumber() != 1) {
            fail("Incorrect test object");
        }
        
        if (l._insertedKey.size() != 1) {
            fail("Should be 1 inserted key: "+l._insertedKey.size());
        }
        
        if (l._insertedKey.get(0) != null) {
            fail("Key should be null (i.e. inserted end of list): "+l._insertedKey.get(0));
        }
        
        if (l._insertedValue.size() != 1) {
            fail("Should be 1 inserted value: "+l._insertedValue.size());
        }
        
        if (((TestObject)l._insertedValue.get(0)).getNumber() != 1) {
            fail("Value should be test object 1: "+l._insertedValue.get(0));
        }      
    }

    @Test
    public void testInsertObjectBetweenOthers() {
        ActiveList list=new ActiveList(TEST_ACTIVE_COLLECTION);
        
        TestActiveChangeListener l=new TestActiveChangeListener();
        list.addActiveChangeListener(l);
        
        list.insert(null, new TestObject(1));
        list.insert(null, new TestObject(2));
        
        list.insert(1, new TestObject(3));
        
        if (list.size() != 3) {
            fail("Should be 3 objects: "+list.size());
        }
        
        java.util.Iterator<Object> iter=list.iterator();
        
        TestObject obj1=(TestObject)iter.next();
        TestObject obj2=(TestObject)iter.next();
        TestObject obj3=(TestObject)iter.next();
        
        if (obj1.getNumber() != 1) {
            fail("Obj1 is not correct");
        }
        
        if (obj2.getNumber() != 3) {
            fail("Obj2 is not correct");
        }
        
        if (obj3.getNumber() != 2) {
            fail("Obj3 is not correct");
        }
        
        if (l._insertedKey.size() != 3) {
            fail("Should be 3 inserted keys: "+l._insertedKey.size());
        }
        
        if (l._insertedKey.get(0) != null) {
            fail("Key0 should be null (i.e. inserted end of list): "+l._insertedKey.get(0));
        }
        
        if (l._insertedKey.get(1) != null) {
            fail("Key1 should be null (i.e. inserted end of list): "+l._insertedKey.get(1));
        }
        
        if (!l._insertedKey.get(2).equals(1)) {
            fail("Key2 should be index 1: "+l._insertedKey.get(2));
        }
        
        if (l._insertedValue.size() != 3) {
            fail("Should be 3 inserted value: "+l._insertedValue.size());
        }
        
        if (((TestObject)l._insertedValue.get(0)).getNumber() != 1) {
            fail("Value1 should be test object 1: "+l._insertedValue.get(0));
        }      
        
        if (((TestObject)l._insertedValue.get(1)).getNumber() != 2) {
            fail("Value2 should be test object 2: "+l._insertedValue.get(1));
        }      
        
        if (((TestObject)l._insertedValue.get(2)).getNumber() != 3) {
            fail("Value3 should be test object 3: "+l._insertedValue.get(2));
        }      
    }

    @Test
    public void testCopyOnReadFalse() {
        
        ActiveList list=new ActiveList(TEST_ACTIVE_COLLECTION);
        list.setCopyOnRead(false);
        
        long startTime=System.currentTimeMillis();

        // Create list entries
        for (int i=0; i < 1000000; i++) {
            list.insert(null, new TestObject(i));
        }
        
        long writeTime=System.currentTimeMillis()-startTime;

        startTime=System.currentTimeMillis();
        int num=0;
        
        for (Object obj : list) {
            // Do something
            num = ((TestObject)obj).getNumber();
        }
        
        long readTime=System.currentTimeMillis()-startTime;
        
        System.out.println("==> NO COPY ON READ: "+readTime+"ms - (initial write time: "+writeTime+"ms)");
        
        if (num != 1000000-1) {
            fail("Final number value was unexpected");
        }
        
        try {
            for (Object obj : list) {
                TestObject to=(TestObject)obj;
                
                if (to.getNumber() == 50000) {
                    // Apply a change to see if throws exception
                    list.insert(null, new TestObject(1000000));
                }
            }
            fail("Expecting it to throw concurrent mod exception");
        } catch (java.util.ConcurrentModificationException cme) {
            // Ignore
        }
    }
    
    @Test
    public void testCopyOnReadTrue() {
        
        ActiveList list=new ActiveList(TEST_ACTIVE_COLLECTION);
        list.setCopyOnRead(true);
        
        long startTime=System.currentTimeMillis();

        // Create list entries
        for (int i=0; i < 1000000; i++) {
            list.insert(null, new TestObject(i));
        }
        
        long writeTime=System.currentTimeMillis()-startTime;

        startTime=System.currentTimeMillis();
        int num=0;
        
        for (Object obj : list) {
            // Do something
            num = ((TestObject)obj).getNumber();
        }
        
        long readTime=System.currentTimeMillis()-startTime;
        
        System.out.println("==> COPY ON READ: "+readTime+"ms - (initial write time: "+writeTime+"ms)");
        
        if (num != 1000000-1) {
            fail("Final number value was unexpected");
        }
        
        try {
            for (Object obj : list) {
                TestObject to=(TestObject)obj;
                
                if (to.getNumber() == 50000) {
                    // Apply a change to see if throws exception
                    list.insert(null, new TestObject(1000000));
                }
            }
        } catch (java.util.ConcurrentModificationException cme) {
            fail("Not expecting it to throw concurrent mod exception");
        }
    }
    
    @Test
    public void testDerivedList() {
        
        ActiveList list=new ActiveList(TEST_ACTIVE_COLLECTION);
        list.setCopyOnRead(true);
        
        // Create initial list entries
        for (int i=0; i < 10; i++) {
            list.insert(null, new TestObject(i));
        }
        
        Predicate predicate=new Predicate() {

			public boolean evaluate(Object item) {
				return (((TestObject)item).getNumber() % 2 == 0);
			}
        	
        };
        
        ActiveList derived=new ActiveList(TEST_DERIVED_ACTIVE_COLLECTION, list, predicate);
        
        if (derived.size() != 5) {
        	fail("Should be 5 entries in derived: "+derived.size());
        }
    }
    
    @Test
    public void testDerivedListWithInsert() {
        
        ActiveList list=new ActiveList(TEST_ACTIVE_COLLECTION);
        list.setCopyOnRead(true);
        
        // Create initial list entries
        for (int i=0; i < 10; i++) {
            list.insert(null, new TestObject(i));
        }
        
        Predicate predicate=new Predicate() {

			public boolean evaluate(Object item) {
				return (((TestObject)item).getNumber() % 2 == 0);
			}
        	
        };
        
        ActiveList derived=new ActiveList(TEST_DERIVED_ACTIVE_COLLECTION, list, predicate);
        
        TestActiveChangeListener l=new TestActiveChangeListener();
        
        derived.addActiveChangeListener(l);
        
        list.insert(null, new TestObject(11));
        list.insert(null, new TestObject(12));
        
        if (derived.size() != 6) {
        	fail("Derived list should have 6 items: "+derived.size());
        }
        
        if (l._insertedValue.size() != 1) {
        	fail("Listener should have 1 value: "+l._insertedValue.size());
        }
        
        if (((TestObject)l._insertedValue.get(0)).getNumber() != 12) {
        	fail("Expecting test object 12: "+l._insertedValue.get(0));
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
