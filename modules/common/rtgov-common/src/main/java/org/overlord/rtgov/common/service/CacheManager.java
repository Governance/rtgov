/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-11, Red Hat Middleware LLC, and others contributors as indicated
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
