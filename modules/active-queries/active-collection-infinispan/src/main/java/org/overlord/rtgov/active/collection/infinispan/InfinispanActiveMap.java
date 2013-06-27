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

import java.util.Map;

import org.infinispan.Cache;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;
import org.overlord.rtgov.active.collection.ActiveCollectionSource;
import org.overlord.rtgov.active.collection.ActiveMap;

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
         * This method handles entry created events.
         * 
         * @param evt The event
         */
        @CacheEntryCreated
        public void entryCreated(CacheEntryCreatedEvent<Object,Object> evt) {
            if (!evt.isPre()) {
                inserted(evt.getKey(), evt.getCache().get(evt.getKey()));
            }
        }

        /**
         * This method handles entry modified events.
         * 
         * @param evt The event
         */
        @CacheEntryModified
        public void entryModified(CacheEntryModifiedEvent<Object,Object> evt) {
            if (!evt.isPre()) {
                updated(evt.getKey(), evt.getValue());
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
