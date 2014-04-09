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

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.overlord.rtgov.common.service.KeyValueStore;
import org.overlord.rtgov.common.util.RTGovProperties;

import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ElasticSearch implementation of the KeyValueStore.
 */
public class ElasticSearchKeyValueStore extends KeyValueStore {

    protected static final ObjectMapper MAPPER=new ObjectMapper();

    private Client _client;
    
    private String _index = null;
    private String _type = null;
    private String _hosts = null;

    static {
        SerializationConfig config=MAPPER.getSerializationConfig()
                .withSerializationInclusion(JsonSerialize.Inclusion.NON_NULL)
                .withSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
        
        MAPPER.setSerializationConfig(config);
    }

    /**
     * Settings for the index this store is related to.
     */
    public static final String SETTINGS = "settings";

    /**
     * Type mappings for the index this store is related to.
     */
    public static final String MAPPINGS = "mappings";

    /**
     * The default settings.
     */
    public static final String DEFAULT_SETTING = "_default_";

    private static final Logger LOG = Logger.getLogger(ElasticSearchKeyValueStore.class.getName());

    /**
     * Default constructor.
     */
    public ElasticSearchKeyValueStore() {

    }

    /**
     * This method returns the index.
     *
     * @return The index
     */
    public String getIndex() {
        return _index;
    }

    /**
     * This method sets the index.
     *
     * @param index The index
     */
    public void setIndex(String index) {
        this._index = index.toLowerCase();
    }

    /**
     * This method returns the index.
     *
     * @return The index
     */
    public String getType() {
        return _type;
    }

    /**
     * This method sets the type.
     *
     * @param type The type
     */
    public void setType(String type) {
        //
        this._type = type.toLowerCase();
    }

    /**
     * This method sets the hosts.
     *
     * @return The hosts
     */
    public String getHosts() {
        return _hosts;
    }

    /**
     * This method returns the hosts.
     *
     * @param hosts The hosts
     */
    public void setHosts(String hosts) {
        this._hosts = hosts;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void init() throws Exception {

        if (_hosts == null) {
            throw new IllegalArgumentException("Hosts property not set ");
        }

        if (_index == null) {
            throw new IllegalArgumentException("Index property not set ");
        }

        if (_type == null) {
            throw new IllegalArgumentException("Type property not set ");
        }

        determineHostsAsProperty();
        String[] hostsArray = _hosts.split(",");
        TransportClient c = new TransportClient();
        for (String aHostsArray : hostsArray) {
            String s = aHostsArray.trim();
            String[] host = s.split(":");
            LOG.info(" Connecting to elasticsearch host. [" + host[0] + ":" + host[1] + "]");
            c = c.addTransportAddress(new InetSocketTransportAddress(host[0], new Integer(host[1])));
        }
        _client = c;
        InputStream s = Thread.currentThread().getContextClassLoader().getResourceAsStream(_index + "-mapping.json");
        if (s != null) {

            String jsonDefaultUserIndex = IOUtils.toString(s);
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("index Mapping settings " + _index + ".json  [" + jsonDefaultUserIndex + "]");
            }
            //  String jsonDefaultUserIndex = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(index + ".json"));
            Map<String, Object> dataMap = XContentFactory.xContent(jsonDefaultUserIndex).createParser(jsonDefaultUserIndex).mapAndClose();

            if (prepareIndex((Map<String, Object>) dataMap.get(SETTINGS))) {
                LOG.info("Index initialized");
            } else {
                LOG.info("Index already initialized. Doing nothing.");
            }

            prepareMapping((Map<String, Object>) dataMap.get(MAPPINGS));
        } else {
            LOG.warning("Could not locate " + _index + "-mapping.json  index mapping file. Mapping file require to start elasticsearch store service");
            //throw new FileNotFoundException("Could not locate " + index + ".json mapping file. Mapping file require to start elasticsearch store service");
        }
    }

    /**
     * @param defaultMappings
     * @return true if the mapping was successful
     */
    private boolean prepareMapping(Map<String, Object> defaultMappings) {

        // only prepare the mapping for the configured repo type
        @SuppressWarnings("unchecked")
        Map<String, Object> mapping = (Map<String, Object>) defaultMappings.get(_type);
        if (mapping == null) {
            throw new RuntimeException("type mapping not defined");
        }
        PutMappingRequestBuilder putMappingRequestBuilder = _client.admin().indices().preparePutMapping().setIndices(_index);
        putMappingRequestBuilder.setType(_type);
        putMappingRequestBuilder.setSource(mapping);
        return putMappingRequestBuilder.execute().actionGet().isAcknowledged();
        /**   for (Map.Entry<String, Object> mapping : defaultMappings.entrySet()) {
         PutMappingRequestBuilder putMappingRequestBuilder = client.admin().indices().preparePutMapping().setIndices(index);
         putMappingRequestBuilder.setType(mapping.getKey());
         putMappingRequestBuilder.setSource((Map<String, Object>) mapping.getValue());
         assertTrue(putMappingRequestBuilder.execute().actionGet().isAcknowledged());

         } **/

    }

    /**
     * Check if index is created. if not it will created it
     *
     * @return returns true if it just created the index. False if the index already existed
     */
    private boolean prepareIndex(Map<String, Object> defaultSettings) {
        IndicesExistsResponse res = _client.admin().indices().prepareExists(_index).execute().actionGet();
        boolean created = false;
        if (!res.isExists()) {
            CreateIndexRequestBuilder req = _client.admin().indices().prepareCreate(_index);
            req.setSettings(defaultSettings);
            created = req.execute().actionGet().isAcknowledged();
            if (!created) {
                throw new RuntimeException("Could not create index [" + _index + "]");
            }
            // todo, possible to update a mapping. it is not possible to change a mapping as this is related to the data that already exists in the index. but possible to add mapping data that does not already set
            //
            // DeleteIndexRequestBuilder delIdx = client.admin().indices().prepareDelete(index);
            //delIdx.execute().actionGet();
            //  UpdateSettingsRequestBuilder updateSettingsRequestBuilder = client.admin().indices().prepareUpdateSettings(index);
            //  updateSettingsRequestBuilder.setSettings(defaultSettings);
            //  updateSettingsRequestBuilder.execute().actionGet().isAcknowledged();
        }

        // just update settings

        return created;

    }

    @Override
    public <V> void add(String id, V document) throws Exception {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Adding " + document.getClass().toString() + ". for id " + id);
        }
        try {
            IndexResponse indexResponse = _client.prepareIndex(_index, _type, id).setSource(convertTypeToJson(document)).execute().actionGet();
            if (!indexResponse.isCreated()) {
                LOG.fine(" Document could not be created for index [" + _index + "/" + _type + "/" + id + "]");
                throw new Exception("Document could not be created for index [" + _index + "/" + _type + "/" + id + "]");
            }
            LOG.fine(" Document successfully created for index [" + _index + "/" + _type + "/" + id + "]");

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "[/" + _index + "/" + _type + "] Could not store  json document from Type [" + document.getClass().getName() + "] ");
            throw new Exception("[/" + _index + "/" + _type + "] Could not store  json document from Type [" + document.getClass().getName() + "] ", e);
        } finally {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine(" Todo. Possibly need to close the connection here. ");
            }
            // client.close();
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

    protected <V> String convertTypeToJson(V obj) {
        try {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("[/" + _index.toLowerCase() + "/" + _type + "] Converting to json document from Type [" + obj.getClass().getName() + "] ");
            }
            return MAPPER.writeValueAsString(obj);

        } catch (Exception e) {
            throw new RuntimeException("Failed to convert from object to json String [class:" + obj.getClass().getName() + "]", e);
        }
    }

    @Override
    public String toString() {
        return "AbstractElasticRepo{"
                + "index='" + _index + '\''
                + ", type='" + _type + '\''
                + ", hosts='" + _hosts + '\''
                + '}';
    }

    /**
     * sets hosts if the _hosts propertey is determined to be a property placeholder
     * Throws IllegalArgumentException argument exception when nothing found
     */
    private void determineHostsAsProperty() {
        if (_hosts.startsWith("${") && _hosts.endsWith("}")) {
            String _hostsProperty = _hosts.substring(2, _hosts.length() - 1);
            _hosts = RTGovProperties.getProperty(_hostsProperty);
            if (_hosts == null) {
                throw new IllegalArgumentException("Could not find property " + _hostsProperty + " in Rtgov.properties");
            }
        }

    }
}
