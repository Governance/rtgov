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
import org.overlord.rtgov.active.collection.ActiveList;
import org.overlord.rtgov.active.collection.QuerySpec;
import org.overlord.rtgov.active.collection.QuerySpec.Style;
import org.overlord.rtgov.active.collection.QuerySpec.Truncate;
import org.overlord.rtgov.active.collection.predicate.MVEL;
import org.overlord.rtgov.active.collection.predicate.Predicate;

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
        
        if (list.getSize() != 3) {
            fail("Should be 3 objects: "+list.getSize());
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

			public boolean evaluate(ActiveCollectionContext context, Object item) {
				return (((TestObject)item).getNumber() % 2 == 0);
			}
        	
        };
        
        ActiveList derived=new ActiveList(TEST_DERIVED_ACTIVE_COLLECTION, list, null, predicate, null);
        
        if (derived.getSize() != 5) {
        	fail("Should be 5 entries in derived: "+derived.getSize());
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

			public boolean evaluate(ActiveCollectionContext context, Object item) {
				return (((TestObject)item).getNumber() % 2 == 0);
			}
        	
        };
        
        ActiveList derived=new ActiveList(TEST_DERIVED_ACTIVE_COLLECTION, list, null, predicate, null);
        
        TestActiveChangeListener l=new TestActiveChangeListener();
        
        derived.addActiveChangeListener(l);
        
        list.insert(null, new TestObject(11));
        list.insert(null, new TestObject(12));
        
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
    public void testCleanupMaxItems() {
        ActiveList list=new ActiveList(TEST_ACTIVE_COLLECTION);
        
        TestActiveChangeListener l=new TestActiveChangeListener();
        list.addActiveChangeListener(l);
        
        list.setMaxItems(10);
        
        for (int i=0; i < 15; i++) {
            list.insert(null, new TestObject(i));
        }
        
        if (list.getSize() != 15) {
            fail("List should have 15 items: "+list.getSize());
        }
        
        list.cleanup();
        
        if (list.getSize() != 10) {
            fail("List should have 10 items: "+list.getSize());
        }
        
        // Check first item is now the TestObject with i=5
        TestObject to=(TestObject)list.iterator().next();
        
        if (to.getNumber() != 5) {
            fail("First item in list should have number 5");
        }
        
        if (l._removedKey.size() != 5) {
            fail("Removed key size should be 5: "+l._removedKey.size());
        }
    }

    @Test
    public void testInsertionExpirationPerformance() {
        //int testSize=10000000; // TODO: causes heap size problems in jenkins - try config changes
        int testSize=10000;
        
        // Run first test without expiration        
        java.util.List<TestObject> sourceList=new java.util.ArrayList<TestObject>();
        
        for (int i=0; i < testSize; i++) {
            sourceList.add(new TestObject(i));
        }
        
        System.gc();
        
        // Run first test without expiration        
        ActiveList list1=new ActiveList(TEST_ACTIVE_COLLECTION, testSize);
        
        long startTime1=System.currentTimeMillis();
        
        for (int i=0; i < testSize; i++) {
            list1.insert(null, sourceList.get(i));
        }
        
        long without=(System.currentTimeMillis()-startTime1);
        
        System.gc();
        
        // Run second test with expiration
        ActiveList list2=new ActiveList(TEST_ACTIVE_COLLECTION, testSize);
        
        list2.setItemExpiration(1000);
        
        long startTime2=System.currentTimeMillis();
        
        for (int i=0; i < testSize; i++) {
            list2.insert(null, sourceList.get(i));
        }
        
        long with=(System.currentTimeMillis()-startTime2);
        
        double diff=(double)with/(double)without;
        
        System.out.println("INSERT PERTFORMANCE:\r\nWithout expiry = "
                            +without+"\r\nWith expiry = "+with
                            +"\r\nDifference factor = "+diff);
        
        // TODO: BAM-23 Add check after optimizing time based insertion
        //if (diff > 4.0) {
        //    fail("Insert performance with expiration set is too slow!");
        //}
    }

    @Test
    public void testCleanupItemExpiration() {
        ActiveList list=new ActiveList(TEST_ACTIVE_COLLECTION);
        
        TestActiveChangeListener l=new TestActiveChangeListener();
        list.addActiveChangeListener(l);
        
        list.setItemExpiration(2000);
        
        for (int i=0; i < 5; i++) {
            list.insert(null, new TestObject(i));
        }
        
        try {
            synchronized (this) {
                wait(3000);
            }
        } catch(Exception e) {
            fail("Failed to wait");
        }
        
        for (int i=0; i < 10; i++) {
            list.insert(null, new TestObject(5+i));
        }
        
        if (list.getSize() != 15) {
            fail("List should have 15 items: "+list.getSize());
        }
        
        list.cleanup();
        
        if (list.getSize() != 10) {
            fail("List should have 10 items: "+list.getSize());
        }
        
        if (l._removedKey.size() != 5) {
            fail("Removed key size should be 5: "+l._removedKey.size());
        }
    }

    @Test
    public void testAllItemsNormal() {
        ActiveList list=new ActiveList(TEST_ACTIVE_COLLECTION);
        
        TestActiveChangeListener l=new TestActiveChangeListener();
        list.addActiveChangeListener(l);
        
        for (int i=0; i < 15; i++) {
            list.insert(null, new TestObject(i));
        }
        
        if (list.getSize() != 15) {
            fail("List should have 15 items: "+list.getSize());
        }

        QuerySpec qs=new QuerySpec();
        qs.setStyle(Style.Normal);
        
        java.util.List<Object> results=list.query(qs);
        
        if (((TestObject)results.get(0)).getNumber() != 0) {
            fail("First item should be obj 0: "+((TestObject)results.get(0)).getNumber());
        }
    }

    @Test
    public void testAllItemsReversed() {
        ActiveList list=new ActiveList(TEST_ACTIVE_COLLECTION);
        
        TestActiveChangeListener l=new TestActiveChangeListener();
        list.addActiveChangeListener(l);
        
        for (int i=0; i < 15; i++) {
            list.insert(null, new TestObject(i));
        }
        
        if (list.getSize() != 15) {
            fail("List should have 15 items: "+list.getSize());
        }

        QuerySpec qs=new QuerySpec();
        qs.setStyle(Style.Reversed);
        
        java.util.List<Object> results=list.query(qs);
        
        if (((TestObject)results.get(0)).getNumber() != 14) {
            fail("First item should be obj 14: "+((TestObject)results.get(0)).getNumber());
        }
    }

    @Test
    public void testMaxItemsGreaterThanSize() {
        ActiveList list=new ActiveList(TEST_ACTIVE_COLLECTION);
        
        TestActiveChangeListener l=new TestActiveChangeListener();
        list.addActiveChangeListener(l);
        
        for (int i=0; i < 15; i++) {
            list.insert(null, new TestObject(i));
        }
        
        if (list.getSize() != 15) {
            fail("List should have 15 items: "+list.getSize());
        }

        QuerySpec qs=new QuerySpec();
        qs.setStyle(Style.Normal);
        qs.setMaxItems(20);
        
        java.util.List<Object> results=list.query(qs);
        
        if (results.size() != list.getSize()) {
            fail("Result size ("+results.size()+
                    ") should be same as list: "+list.getSize());
        }
    }

    @Test
    public void testMaxItemsLessSizeTruncateEnd() {
        ActiveList list=new ActiveList(TEST_ACTIVE_COLLECTION);
        
        TestActiveChangeListener l=new TestActiveChangeListener();
        list.addActiveChangeListener(l);
        
        for (int i=0; i < 15; i++) {
            list.insert(null, new TestObject(i));
        }
        
        if (list.getSize() != 15) {
            fail("List should have 15 items: "+list.getSize());
        }

        QuerySpec qs=new QuerySpec();
        qs.setStyle(Style.Normal);
        qs.setMaxItems(5);
        qs.setTruncate(Truncate.End);
        
        java.util.List<Object> results=list.query(qs);
        
        if (results.size() != qs.getMaxItems()) {
            fail("Result size ("+results.size()+
                    ") should be: "+qs.getMaxItems());
        }

        if (((TestObject)results.get(0)).getNumber() != 0) {
            fail("First item should be obj 0: "+((TestObject)results.get(0)).getNumber());
        }
    }

    @Test
    public void testMaxItemsLessSizeTruncateStart() {
        ActiveList list=new ActiveList(TEST_ACTIVE_COLLECTION);
        
        TestActiveChangeListener l=new TestActiveChangeListener();
        list.addActiveChangeListener(l);
        
        for (int i=0; i < 15; i++) {
            list.insert(null, new TestObject(i));
        }
        
        if (list.getSize() != 15) {
            fail("List should have 15 items: "+list.getSize());
        }

        QuerySpec qs=new QuerySpec();
        qs.setStyle(Style.Normal);
        qs.setMaxItems(5);
        qs.setTruncate(Truncate.Start);
        
        java.util.List<Object> results=list.query(qs);
        
        if (results.size() != qs.getMaxItems()) {
            fail("Result size ("+results.size()+
                    ") should be: "+qs.getMaxItems());
        }

        if (((TestObject)results.get(0)).getNumber() != 10) {
            fail("First item should be obj 10: "+((TestObject)results.get(0)).getNumber());
        }
    }

    @Test
    public void testMaxItemsLessSizeTruncateEndReversed() {
        ActiveList list=new ActiveList(TEST_ACTIVE_COLLECTION);
        
        TestActiveChangeListener l=new TestActiveChangeListener();
        list.addActiveChangeListener(l);
        
        for (int i=0; i < 15; i++) {
            list.insert(null, new TestObject(i));
        }
        
        if (list.getSize() != 15) {
            fail("List should have 15 items: "+list.getSize());
        }

        QuerySpec qs=new QuerySpec();
        qs.setStyle(Style.Reversed);
        qs.setMaxItems(5);
        qs.setTruncate(Truncate.End);
        
        java.util.List<Object> results=list.query(qs);
        
        if (results.size() != qs.getMaxItems()) {
            fail("Result size ("+results.size()+
                    ") should be: "+qs.getMaxItems());
        }

        if (((TestObject)results.get(0)).getNumber() != 4) {
            fail("First item should be obj 4: "+((TestObject)results.get(0)).getNumber());
        }
    }

    @Test
    public void testMaxItemsLessSizeTruncateStartReversed() {
        ActiveList list=new ActiveList(TEST_ACTIVE_COLLECTION);
        
        TestActiveChangeListener l=new TestActiveChangeListener();
        list.addActiveChangeListener(l);
        
        for (int i=0; i < 15; i++) {
            list.insert(null, new TestObject(i));
        }
        
        if (list.getSize() != 15) {
            fail("List should have 15 items: "+list.getSize());
        }

        QuerySpec qs=new QuerySpec();
        qs.setStyle(Style.Reversed);
        qs.setMaxItems(5);
        qs.setTruncate(Truncate.Start);
        
        java.util.List<Object> results=list.query(qs);
        
        if (results.size() != qs.getMaxItems()) {
            fail("Result size ("+results.size()+
                    ") should be: "+qs.getMaxItems());
        }

        if (((TestObject)results.get(0)).getNumber() != 14) {
            fail("First item should be obj 14: "+((TestObject)results.get(0)).getNumber());
        }
    }

    @Test
    public void testDerivedListInactive() {
        
        ActiveList list=new ActiveList(TEST_ACTIVE_COLLECTION);
        list.setCopyOnRead(true);
        
        // Create initial list entries
        for (int i=0; i < 10; i++) {
            list.insert(null, new TestObject(i));
        }
        
        MVEL predicate=new MVEL();
        predicate.setExpression("number % 2 == 0");
        
        java.util.Map<String,Object> props=new java.util.HashMap<String, Object>();
        props.put("active", false);
        
        ActiveList derived=new ActiveList(TEST_DERIVED_ACTIVE_COLLECTION, list, null, predicate, props);
        
        if (derived.getSize() != 5) {
            fail("Should be 5 entries in derived: "+derived.getSize());
        }
        
        // Change predicate
        predicate.setExpression("number < 3");
        
        if (derived.getSize() != 3) {
            fail("NOW Should be 3 entries in derived: "+derived.getSize());
        }
        
        java.util.Iterator<Object> iter=derived.iterator();
        
        TestObject to1=(TestObject)iter.next();
        TestObject to2=(TestObject)iter.next();
        TestObject to3=(TestObject)iter.next();
        
        if (to1.getNumber() != 0) {
            fail("First is not 0: "+to1.getNumber());
        }
        
        if (to2.getNumber() != 1) {
            fail("Second is not 1: "+to2.getNumber());
        }
        
        if (to3.getNumber() != 2) {
            fail("Third is not 2: "+to3.getNumber());
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
