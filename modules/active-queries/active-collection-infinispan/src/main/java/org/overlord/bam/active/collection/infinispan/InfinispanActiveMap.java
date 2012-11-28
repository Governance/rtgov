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
package org.overlord.bam.active.collection.infinispan;

import java.util.Map;

import org.infinispan.Cache;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;
import org.overlord.bam.active.collection.ActiveCollectionSource;
import org.overlord.bam.active.collection.ActiveMap;

/**
 * This class represents an infinispan specific active map
 * implementation.
 *
 */
public class InfinispanActiveMap extends ActiveMap {
    
    private InfinispanCacheListener _listener=new InfinispanCacheListener();

    /**
     * This constructor initializes the active map.
     * 
     * @param acs The Active Collection source
     * @param map The map
     */
    public InfinispanActiveMap(ActiveCollectionSource acs, Map<Object, Object> map) {
        super(acs, map);
        
        // Add listener for change notifications
        if (map instanceof Cache) {
            Cache<Object, Object> cache=(Cache<Object, Object>)map;
            
            cache.addListener(_listener);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    protected void finalize() throws Throwable {
        super.finalize();
        
        if (_listener != null && getMap() instanceof Cache) {
            ((Cache<Object, Object>)getMap()).removeListener(_listener);
        }
    }

    /**
     * This class represents the infinispan cache listener.
     *
     */
    @Listener
    public class InfinispanCacheListener {
        
        /**
         * This method handles entry modified events.
         * 
         * @param evt The event
         */
        @CacheEntryModified
        public void entryModified(CacheEntryModifiedEvent<Object,Object> evt) {
            if (!evt.isPre()) {
                if (getMap().containsKey(evt.getKey())) {
                    updated(evt.getKey(), evt.getValue());
                } else {
                    inserted(evt.getKey(), evt.getValue());
                }
            }
        }
        
        /**
         * This method handles entry modified events.
         * 
         * @param evt The event
         */
        @CacheEntryRemoved
        public void entryRemoved(CacheEntryRemovedEvent<Object,Object> evt) {
            if (evt.isPre()) {
                removed(evt.getKey(), evt.getValue());
            }
        }
    }
}
