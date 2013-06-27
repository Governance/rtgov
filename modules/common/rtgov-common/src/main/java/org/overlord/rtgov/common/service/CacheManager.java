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
package org.overlord.rtgov.common.service;

import java.util.Map;

/**
 * This abstract class defines a Cache Manager service for
 * use by runtime governance components.
 *
 */
public abstract class CacheManager extends Service {
    
    /**
     * This method returns the cache associated with the
     * supplied name.
     * 
     * @param name The name of the required cache
     * @return The cache, or null if not found
     * 
     * @param <K> The key type
     * @param <V> The value type
     */
    public abstract <K,V> Map<K,V> getCache(String name);
    
    /**
     * This method locks the item, associated with the
     * supplied key, in the named cache.
     * 
     * @param cacheName The name of the cache
     * @param key The key for the item to be locked
     * @return Whether the lock could be applied
     */
    public abstract boolean lock(String cacheName, Object key);

}
