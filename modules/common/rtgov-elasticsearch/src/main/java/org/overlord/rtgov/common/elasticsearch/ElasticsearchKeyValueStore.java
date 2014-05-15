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

import org.overlord.rtgov.common.service.KeyValueStore;

/**
 * ElasticSearch implementation of the KeyValueStore.
 */
public class ElasticsearchKeyValueStore extends KeyValueStore {

    private ElasticsearchClient _client;

    /**
     * Default constructor.
     */
    public ElasticsearchKeyValueStore() {
        _client = new ElasticsearchClient();
    }

    /**
     * This method returns the schedule.
     *
     * @return The schedule
     */
    public long getSchedule() {
        return _client.getSchedule();
    }

    /**
     * This method sets the schedule.
     *
     * @param schedule the schedule
     */
    public void setSchedule(long schedule) {
        _client.setSchedule(schedule);
    }

    /**
     * This method returns the index.
     *
     * @return The index
     */
    public String getIndex() {
        return _client.getIndex();
    }

    /**
     * This method sets the index.
     *
     * @param index The index
     */
    public void setIndex(String index) {
        _client.setIndex(index);
    }

    /**
     * This method returns the index.
     *
     * @return The index
     */
    public String getType() {
        return _client.getType();
    }

    /**
     * This method sets the type.
     *
     * @param type The type
     */
    public void setType(String type) {
        _client.setType(type);
    }

    /**
     * This method sets the hosts.
     *
     * @return The hosts
     */
    public String getHosts() {
        return _client.getHosts();
    }

    /**
     * This method returns the hosts.
     *
     * @param hosts The hosts
     */
    public void setHosts(String hosts) {
        _client.setHosts(hosts);
    }

    /**
     * This method returns the _bulkSize.
     *
     * @return Returns _bulkSize
     */
    public int getBulkSize() {
        return _client.getBulkSize();
    }

    /**
     * This method sets the _bulkSize.
     *
     * @param bulkSize The bulkSize
     */
    public void setBulkSize(int bulkSize) {
        _client.setBulkSize(bulkSize);
    }

    /**
     * {@inheritDoc}
     */
    public void init() throws Exception {
        _client.init();
    }

    @Override
    public <V> void add(String id, V document) throws Exception {
        _client.add(id, ElasticsearchClient.convertTypeToJson(document));
    }

    @Override
    public void remove(String id) throws Exception {
        _client.remove(id);
    }

    @Override
    public <V> void update(String id, V document) {
        _client.update(id, ElasticsearchClient.convertTypeToJson(document));
    }

    /**
     * default implementation of getter returns a simple .json document as String.
     *
     * @param id The id
     * @param type The type to be returned
     * @return The value
     * @param <V> The value type
     */
    public <V> V get(String id, Class<V> type) {
        String doc=_client.get(id);
        
        if (doc != null) {
            return ElasticsearchClient.<V>convertJsonToType(doc, type);
        }
        
        return null;
    }

    @Override
    public String toString() {
        return "ElasticsearchKeyValueStore{"+_client+"}";
    }

}
