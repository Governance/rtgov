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
package org.overlord.rtgov.epn.service.infinispan;

import static org.junit.Assert.*;

import org.junit.Test;

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
