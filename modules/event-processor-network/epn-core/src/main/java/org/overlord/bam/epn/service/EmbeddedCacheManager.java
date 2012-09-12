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
package org.overlord.bam.epn.service;

import java.util.Map;

/**
 * This class defines an in-memory Cache Manager service for
 * use by an event processor.
 *
 */
public class EmbeddedCacheManager extends CacheManager {
    
    private java.util.Map<String, java.util.Map<?,?>> _caches=
            new java.util.HashMap<String, java.util.Map<?,?>>();
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <K,V> Map<K,V> getCache(String name) {
        Map<K,V> ret=null;
        
        synchronized (_caches) {
            if (_caches.containsKey(name)) {
                ret = (Map<K,V>)_caches.get(name);
            } else {
                ret = new java.util.HashMap<K,V>();
                
                _caches.put(name, ret);
            }
        }
        
        return (ret);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean lock(String cacheName, Object key) {
        return (true);
    }

}
