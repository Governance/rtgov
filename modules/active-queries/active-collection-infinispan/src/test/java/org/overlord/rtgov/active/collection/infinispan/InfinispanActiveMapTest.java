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
package org.overlord.rtgov.active.collection.infinispan;

import static org.junit.Assert.*;

import org.infinispan.Cache;
import org.infinispan.manager.CacheContainer;
import org.junit.Test;
import org.overlord.rtgov.active.collection.ActiveChangeListener;
import org.overlord.rtgov.active.collection.ActiveCollectionSource;
import org.overlord.rtgov.active.collection.infinispan.InfinispanActiveMap;
import org.overlord.rtgov.active.collection.infinispan.InfinispanActiveMapTest.TestChange.TestChangeType;

public class InfinispanActiveMapTest {

    @Test
    public void testNotifications() {
        
        CacheContainer cc=org.overlord.rtgov.common.infinispan.InfinispanManager.getCacheContainer(null);
        
        if (cc == null) {
            fail("Failed to get default cache container");
        }
        
        Cache<Object,Object> cache=cc.getCache("Test");
        
        ActiveCollectionSource acs=new ActiveCollectionSource();
        acs.setName("Test");
        
        InfinispanActiveMap iam=new InfinispanActiveMap(acs, cache);
        
        TestListener l=new TestListener();
        
        iam.addActiveChangeListener(l);
        
        cache.put("t1", "v1");
        cache.put("t2", "v2");
        cache.put("t1", "v1.1");        
        cache.remove("t2");
        
        // TODO: RTGOV-174 - need to deal with additional update notifications of inserting
        // a new entity, especially as they currently occur before the associated insert.
        if (l._changes.size() != 6) {
            fail("Expecting 6 notifications: "+l._changes.size());
        }
        
        if (l._changes.get(1)._type != TestChangeType.Insert) {
            fail("1 should be insert: "+l._changes.get(0)._type);
        }
        
        if (!l._changes.get(1)._key.equals("t1")) {
            fail("1 key incorrect");
        }
        
        if (!l._changes.get(1)._value.equals("v1")) {
            fail("1 value incorrect");
        }
        
        if (l._changes.get(3)._type != TestChangeType.Insert) {
            fail("3 should be insert");
        }
        
        if (!l._changes.get(3)._key.equals("t2")) {
            fail("3 key incorrect");
        }
        
        if (!l._changes.get(3)._value.equals("v2")) {
            fail("3 value incorrect");
        }
        
        if (l._changes.get(4)._type != TestChangeType.Update) {
            fail("4 should be Update: "+l._changes.get(2)._type);
        }
        
        if (!l._changes.get(4)._key.equals("t1")) {
            fail("4 key incorrect");
        }
        
        if (!l._changes.get(4)._value.equals("v1.1")) {
            fail("4 value incorrect");
        }
        
        if (l._changes.get(5)._type != TestChangeType.Remove) {
            fail("5 should be Remove");
        }

        if (!l._changes.get(5)._key.equals("t2")) {
            fail("5 key incorrect");
        }
        
        if (!l._changes.get(5)._value.equals("v2")) {
            fail("5 value incorrect");
        }
        
    }
    
    public static class TestChange {
        public enum TestChangeType {
            Insert,
            Update,
            Remove
        }
        
        public TestChangeType _type=null;
        public Object _key=null;
        public Object _value=null;
    }

    public static class TestListener implements ActiveChangeListener {
        
        private java.util.List<TestChange> _changes=new java.util.Vector<TestChange>();
        
        public void inserted(Object key, Object value) {
            TestChange tc=new TestChange();
            tc._type = TestChangeType.Insert;
            tc._key = key;
            tc._value = value;
            _changes.add(tc);
        }

        public void updated(Object key, Object value) {
            TestChange tc=new TestChange();
            tc._type = TestChangeType.Update;
            tc._key = key;
            tc._value = value;
            _changes.add(tc);
        }

        public void removed(Object key, Object value) {
            TestChange tc=new TestChange();
            tc._type = TestChangeType.Remove;
            tc._key = key;
            tc._value = value;
            _changes.add(tc);
        }
        
    }
}
