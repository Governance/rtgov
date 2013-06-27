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
package org.overlord.rtgov.common.infinispan.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.common.infinispan.service.InfinispanCacheManager;

public class InfinispanCacheManagerTest {

    private static final String TEST_VALUE = "value";
    private static final String TEST_KEY = "test";

    @Test
    public void testConfigPut() {
        InfinispanCacheManager cm=new InfinispanCacheManager();
        
        try {
            cm.init();
        } catch (Exception e) {
            fail("Failed to initialize infinispan cache manager: "+e);
        }
        
        java.util.Map<Object,Object> cache=cm.getCache("Principals");
        
        if (cache == null) {
            fail("Failed to get 'Principals' cache");
        }
        
        cache.clear();
        
        cache.put(TEST_KEY, TEST_VALUE);
        
        if (!cache.containsKey(TEST_KEY)) {
            fail("Value not found");
        }
        
        Object value=cache.get(TEST_KEY);
        
        if (!value.equals(TEST_VALUE)) {
            fail("Value is incorrect: "+value);
        }
        
        cache.clear();
    }

    @Test
    public void testConfigLockPut() {
        InfinispanCacheManager cm=new InfinispanCacheManager();
        
        try {
            cm.init();
        } catch (Exception e) {
            fail("Failed to initialize infinispan cache manager: "+e);
        }
        
        java.util.Map<Object,Object> cache=cm.getCache("Principals");
        
        if (cache == null) {
            fail("Failed to get 'Principals' cache");
        }
        
        cache.clear();
        
        cm.lock("Principals", TEST_KEY);
        
        cache.put(TEST_KEY, TEST_VALUE);
        
        if (!cache.containsKey(TEST_KEY)) {
            fail("Value not found");
        }
        
        Object value=cache.get(TEST_KEY);
        
        if (!value.equals(TEST_VALUE)) {
            fail("Value is incorrect: "+value);
        }
        
        cache.clear();
    }

    @Test
    public void testTwoCacheManagers() {
        InfinispanCacheManager cm1=new InfinispanCacheManager();
        InfinispanCacheManager cm2=new InfinispanCacheManager();
        
        try {
            cm1.init();
            cm2.init();
        } catch (Exception e) {
            fail("Failed to initialize infinispan cache manager: "+e);
        }
        
        java.util.Map<Object,Object> cache1=cm1.getCache("Principals");
        
        if (cache1 == null) {
            fail("Failed to get 'Principals' cache");
        }
        
        cache1.clear();
        
        cm1.lock("Principals", TEST_KEY);
        
        cache1.put(TEST_KEY, TEST_VALUE);
        
        if (!cache1.containsKey(TEST_KEY)) {
            fail("Value1 not found");
        }
        
        Object value1=cache1.get(TEST_KEY);
        
        if (!value1.equals(TEST_VALUE)) {
            fail("Value1 is incorrect: "+value1);
        }
        
        // Check whether cm2 has access to value
        java.util.Map<Object,Object> cache2=cm2.getCache("Principals");
        
        if (cache2 == null) {
            fail("Failed to get 'Principals' cache");
        }
        
        if (!cache2.containsKey(TEST_KEY)) {
            fail("Value2 not found");
        }
        
        Object value2=cache2.get(TEST_KEY);
        
        if (!value2.equals(TEST_VALUE)) {
            fail("Value2 is incorrect: "+value2);
        }
        
        
        cache1.clear();
    }

}
