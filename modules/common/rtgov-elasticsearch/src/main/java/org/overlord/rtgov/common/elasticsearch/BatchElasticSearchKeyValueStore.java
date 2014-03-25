package org.overlord.rtgov.common.elasticsearch;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: imk@redhat.com
 * Date: 23/03/14
 * Time: 23:27
 * ES impl for batch operations. it could be that constantly strwaming data to es maybe suboptimal. i
 * Todo, implement a batch es integration that would push datza to es every minute or so.
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
    public <V> void get(String id) {
        throw new UnsupportedOperationException("KeyValueStore. Get not implemented.");

    }
}
