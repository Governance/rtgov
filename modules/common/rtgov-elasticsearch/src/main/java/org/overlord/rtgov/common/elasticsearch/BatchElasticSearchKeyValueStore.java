/*
 * 2012-4 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.overlord.rtgov.common.elasticsearch;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * .
 * User: imk@redhat.com
 * Date: 23/03/14
 * Time: 23:27
 * ES impl for batch operations. it could be that constantly strwaming data to es maybe suboptimal. i
 * Todo, implement a batch es integration that would push data to es every minute or so instead of realtime
 */
public class BatchElasticSearchKeyValueStore extends ElasticSearchKeyValueStore {
    private static final Logger LOG = Logger.getLogger(ElasticSearchKeyValueStore.class.getName());

    @Override
    public <V> void add(String id, V document) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Adding " + document.getClass().toString() + ". for id " + id);
        }

    }

    @Override
    public void remove(String id) {
        throw new UnsupportedOperationException("KeyValueStore. Remove not implemented.");
    }

    @Override
    public <V> void update(String id, V document) {
        throw new UnsupportedOperationException("KeyValueStore. Update not implemented.");

    }

    @Override
    public <V> V get(String id) {
        throw new UnsupportedOperationException("KeyValueStore. Get not implemented.");

    }
}
