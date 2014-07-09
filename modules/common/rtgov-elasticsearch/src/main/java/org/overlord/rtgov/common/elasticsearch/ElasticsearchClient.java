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
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequestBuilder;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.overlord.rtgov.common.util.RTGovProperties;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ElasticSearch client.
 */
public class ElasticsearchClient {

    protected static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        SerializationConfig config = MAPPER.getSerializationConfig()
                .withSerializationInclusion(JsonSerialize.Inclusion.NON_NULL)
                .withSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);

        MAPPER.setSerializationConfig(config);
    }

    /**
     * Default Elasticsearch hosts configuration.
     */
    public static final String ELASTICSEARCH_HOSTS = "Elasticsearch.hosts";

    /**
     * Default Elasticsearch schedule configuration.
     */
    public static final String ELASTICSEARCH_SCHEDULE = "Elasticsearch.schedule";

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

    private static final Logger LOG = Logger.getLogger(ElasticsearchClient.class.getName());

    private Client _client;

    private String _index = null;
    private String _type = null;

    private static final String ELASTICSEARCH_HOSTS_DEFAULT="embedded";

    private String _hosts = ELASTICSEARCH_HOSTS_DEFAULT;

    /**
     * bulkRequest. determines how many request should be sent to elastic search in bulk instead of singular requests
     */
    private int _bulkSize = 0;

    private BulkRequestBuilder _bulkRequestBuilder;

    private ScheduledFuture<BulkResponse> _scheduledFuture;

    private ScheduledExecutorService _scheduler;

    private static final long ELASTICSEARCH_SCHEDULE_DEFAULT = 30000;

    /**
     * schedule to persist the items  to elasticsearch.
     * A new schedule is created after a item is added.
     */
    private long _schedule = ELASTICSEARCH_SCHEDULE_DEFAULT;

    private static final Object SYNC = new Object();


    /**
     * Default constructor.
     */
    public ElasticsearchClient() {

        // NOTE: Using rtgov 1.0.0.Final API for RTGovProperties, to enable deployment into
        // FSW6.0

        String hosts = RTGovProperties.getProperty(ELASTICSEARCH_HOSTS);

        if (hosts != null) {
            _hosts = hosts;
        }

        String schedule = RTGovProperties.getProperty(ELASTICSEARCH_SCHEDULE);

        if (schedule != null) {
            try {
                _schedule = Long.parseLong(schedule);
            } catch (Exception e) {
                LOG.log(Level.SEVERE, MessageFormat.format(
                        java.util.PropertyResourceBundle.getBundle(
                                "rtgov-elasticsearch.Messages").getString("RTGOV-ELASTICSEARCH-2"),
                                schedule), e);
                _schedule = ELASTICSEARCH_SCHEDULE_DEFAULT;
            }
        }
    }

    /**
     * This method returns the schedule.
     *
     * @return The schedule
     */
    public long getSchedule() {
        return _schedule;
    }

    /**
     * This method sets the schedule.
     *
     * @param schedule the schedule
     */
    public void setSchedule(long schedule) {
        this._schedule = schedule;
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
     * This method returns the _bulkSize.
     *
     * @return Returns _bulkSize
     */
    public int getBulkSize() {
        return _bulkSize;
    }

    /**
     * This method sets the _bulkSize.
     *
     * @param bulkSize The bulkSize
     */
    public void setBulkSize(int bulkSize) {
        this._bulkSize = bulkSize;
    }

    /**
     * Initialize the client.
     *
     * @throws Exception Failed to initialize the client
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
        if (_bulkSize > 0) {
            _scheduler = Executors.newScheduledThreadPool(1);
        }

        determineHostsAsProperty();

        /**
         * quick fix for integration tests. if hosts property set to "embedded" then a local node is start.
         * maven dependencies need to be defined correctly for this to work
         */
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        try {
            // Need to use the classloader for Elasticsearch to pick up the property files when
            // running in an OSGi environment
            Thread.currentThread().setContextClassLoader(TransportClient.class.getClassLoader());

            if (_hosts.startsWith("embedded")) {
                try {
                    // Obtain the Elasticsearch client
                    _client =  ElasticsearchNode.getInstance().getClient();
                } catch (Throwable e) {
                    LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                            "rtgov-elasticsearch.Messages").getString("RTGOV-ELASTICSEARCH-3"), e);
                }
            } else {
                String[] hostsArray = _hosts.split(",");
                TransportClient c = new TransportClient();
                
                for (String aHostsArray : hostsArray) {
                    String s = aHostsArray.trim();
                    String[] host = s.split(":");
                    
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine(" Connecting to elasticsearch host. [" + host[0] + ":" + host[1] + "]");
                    }
                    
                    c = c.addTransportAddress(new InetSocketTransportAddress(host[0], new Integer(host[1])));
                }
                
                _client = c;
            }
        } finally {
            Thread.currentThread().setContextClassLoader(cl);
        }

        InputStream s = Thread.currentThread().getContextClassLoader().getResourceAsStream(_index + "-mapping.json");
        if (s == null) {
            s = ElasticsearchClient.class.getResourceAsStream("/" + _index + "-mapping.json");
        }

        if (s != null) {
            synchronized (SYNC) {
                String jsonDefaultUserIndex = IOUtils.toString(s);
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Index mapping settings " + _index + ".json  [" + jsonDefaultUserIndex + "]");
                }

                Map<String, Object> dataMap = XContentFactory.xContent(jsonDefaultUserIndex).createParser(jsonDefaultUserIndex).mapAndClose();

                if (prepareIndex((Map<String, Object>) dataMap.get(SETTINGS))) {
                    LOG.info("Index initialized");
                    // refresh index
                    RefreshRequestBuilder refreshRequestBuilder = getElasticsearchClient().admin().indices().prepareRefresh(getIndex());
                    getElasticsearchClient().admin().indices().refresh(refreshRequestBuilder.request()).actionGet();
                } else {
                    LOG.info("Index already initialized. Doing nothing.");
                }

                prepareMapping((Map<String, Object>) dataMap.get(MAPPINGS));
            }
        } else {
            LOG.warning("Could not locate " + _index + "-mapping.json index mapping file. Mapping file require to start elasticsearch store service");
        }
    }

    /**
     * @param defaultMappings
     * @return true if the mapping was successful
     */
    @SuppressWarnings("unchecked")
    private boolean prepareMapping(Map<String, Object> defaultMappings) {
        // only prepare the mapping for the configured repo type
        Set<String> keys = defaultMappings.keySet();
        boolean success = true;

        Map<String, Object> mapping = (Map<String, Object>) defaultMappings.get(_type);
        if (mapping == null) {
            throw new RuntimeException("type mapping not defined");
        }
        PutMappingRequestBuilder putMappingRequestBuilder = _client.admin().indices().preparePutMapping().setIndices(_index);
        putMappingRequestBuilder.setType(_type);
        putMappingRequestBuilder.setSource(mapping);

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("******* Creating elasticsearch mapping for [" + _type + "] *********");
        }
        
        PutMappingResponse resp = putMappingRequestBuilder.execute().actionGet();

        if (resp.isAcknowledged()) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("******* Successful ACK on elasticsearch mapping for [" + _type + "] *********");
            }
            
            /**
             * now determine if any child relationships exist in the mapping
             */
            for (String s : keys) {
                Map<?, ?> childMap = (Map<?,?>) ((Map<?,?>) defaultMappings.get(s)).get("_parent");
                if (childMap != null && childMap.get("type") != null && childMap.get("type").equals(_type)) {

                    PutMappingRequestBuilder putChildMappingRequestBuilder = _client.admin().indices().preparePutMapping().setIndices(_index);
                    putChildMappingRequestBuilder.setType(s);
                    
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine("******* Creating elasticsearch mapping for [parent="
                                    + _type + ", child=" + s + "] *********");
                    }
                    
                    putChildMappingRequestBuilder.setSource((Map<String, Map<?,?>>) defaultMappings.get(s));
                    PutMappingResponse respChild = putChildMappingRequestBuilder.execute().actionGet();
                    if (respChild.isAcknowledged()) {
                        if (LOG.isLoggable(Level.FINE)) {
                            LOG.fine("******* Successful ACK on elasticsearch mapping for [parent="
                                    + _type + ", child" + s + "] *********");
                        }
                    } else {
                        success = false;
                        LOG.warning("******* Child Mapping creation was not acknowledged for elasticsearch mapping [parent=" + _type + ", child=" + s + "] *********");
                    }
                }
            }
        } else {
            success = false;
            LOG.warning("******* Mapping creation was not acknowledged for elasticsearch mapping [" + _type + "] *********");
        }
        return success;
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
        }

        return created;

    }

    /**
     * @param id
     * @param document
     * @param <V>
     */
    protected synchronized <V> void addBulk(String id, String document) {

        if (_bulkRequestBuilder == null) {
            _bulkRequestBuilder = _client.prepareBulk();
        }
        _bulkRequestBuilder.add(_client.prepareIndex(_index, _type, id).setSource(document));

        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest(" Document successfully added bulk item to index [" + _index + "/" + _type + "/" + id + "]");
        }

        /**
         * check if we need to persist now
         */
        if (_bulkRequestBuilder.numberOfActions() >= _bulkSize) {
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("bulk limit reach. storing " + _bulkSize + " items to  [" + _index + "/" + _type + "]");
            }

            /**
             * cancel any scheduler thats running
             */
            if (_scheduledFuture != null) {
                _scheduledFuture.cancel(true);
            }

            storeBulkItems();

        } else {
            /**
             * if we have not exceeded the bulk. we create a task to dump this to elastic search after configured number of seconds
             */
            if (_scheduledFuture == null) {
                _scheduledFuture = _scheduler.schedule(new Callable<BulkResponse>() {
                    public BulkResponse call() throws Exception {
                        if (LOG.isLoggable(Level.FINEST)) {
                            LOG.finest("Executed scheduled persitence of bulk items " + _index + "/" + _type);
                        }
                        return (storeBulkItems());
                    }
                }, _schedule, TimeUnit.MILLISECONDS);
            }

        }
    }

    private synchronized BulkResponse storeBulkItems() {
        BulkResponse bulkItemResponses = _bulkRequestBuilder.execute().actionGet();

        if (bulkItemResponses.hasFailures()) {
            LOG.severe(" Bulk Documents{" + _bulkSize + "} could not be created for index [" + _index + "/" + _type + "/");

            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("FAILED MESSAGES. " + bulkItemResponses.buildFailureMessage());
            }
        } else {
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Success storing " + _bulkSize + " items to  [" + _index + "/" + _type + "]");
            }
        }

        _bulkRequestBuilder = null;
        _scheduledFuture = null;

        return (bulkItemResponses);
    }

    /**
     * This method adds a new document to ElasticSearch.
     *
     * @param id       The id
     * @param document The document
     * @throws Exception Failed to add
     */
    public void add(String id, String document) throws Exception {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest(" Adding to elastich search id=" + id + ", doc=" + document);
        }
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Adding " + document.getClass().toString() + ". for id " + id);
        }
        if (getBulkSize() > 0) {
            addBulk(id, document);
        } else {
            try {
                IndexResponse indexResponse = _client.prepareIndex(_index, _type, id).setSource(document).execute().actionGet();
                if (!indexResponse.isCreated()) {
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine(" Document could not be created for index ["
                                + _index + "/" + _type + "/" + id + "]");
                    }
                    throw new Exception("Document could not be created for index [" + _index + "/" + _type + "/" + id + "]");
                }
                
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine(" Document successfully created for index [" + _index + "/" + _type + "/" + id + "]");
                }

            } catch (Exception e) {
                LOG.log(Level.SEVERE, "[/" + _index + "/" + _type + "] Could not store json document", e);
                throw new Exception("[/" + _index + "/" + _type + "] Could not store json document", e);
            }
        }
    }

    /**
     * This method removes the document with the supplied id.
     *
     * @param id The id
     * @throws Exception Failed to remove document
     */
    public void remove(String id) throws Exception {
        DeleteResponse response = _client.prepareDelete(_index, _type, id).setRouting(id)
                .execute()
                .actionGet();

        if (!response.isFound()) {
            LOG.warning("Unable to find document [" + _index + "/" + _type + "/" + id + "] for removal");
        }
    }

    /**
     * This method updates the supplied document.
     *
     * @param id The id
     * @param document The document
     */
    public void update(String id, String document) {
        try {
            _client.prepareIndex(_index, _type, id).setSource(document).execute().actionGet();
            
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine(" Document successfully updated for index [" + _index + "/" + _type + "/" + id + "]");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "[/" + _index + "/" + _type + "] Could not update json document", e);
            throw new RuntimeException("[/" + _index + "/" + _type + "] Could not update json document", e);
        }
    }

    /**
     * default implementation of getter returns a simple .json document as String.
     *
     * @param id The id.
     * @return Document as string.
     */
    public String get(String id) {
        GetResponse response = _client.prepareGet(getIndex(), getType(), id).setRouting(id)
                .execute()
                .actionGet();
        if (!response.isSourceEmpty()) {
            String responseSourceAsString = response.getSourceAsString();
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("[/" + _index.toLowerCase() + "/" + _type + "] reterived json document from Elasticsearch [" + responseSourceAsString + "] ");
            }
            return responseSourceAsString;
        } else {
            return null;
        }

    }

    /**
     * convert type to String.
     *
     * @param obj The object to convert
     * @return The json document
     */
    public static String convertTypeToJson(Object obj) {
        try {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Converting to json document from Type [" + obj.getClass().getName() + "] ");
            }
            return MAPPER.writeValueAsString(obj);

        } catch (Exception e) {
            throw new RuntimeException("Failed to convert from object to json String [class:" + obj.getClass().getName() + "]", e);
        }
    }

    /**
     * convert type to String.
     *
     * @param json The json document
     * @param type The type of the object to return
     * @param <V>  type The object type
     * @return The converted object
     */
    public static <V> V convertJsonToType(String json, Class<V> type) {
        try {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Converting from json document to Type [" + type.getName() + "] ");
            }
            return MAPPER.readValue(json.getBytes(), type);

        } catch (Exception e) {
            throw new RuntimeException("Failed to convert to object from json String [class:" + type.getName() + "]", e);
        }
    }

    @Override
    public String toString() {
        return "ElasticsearchClient{"
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
                throw new IllegalArgumentException("Could not find property "
                                + _hostsProperty + " in overlord-rtgov.properties");
            }
        }

    }

    /**
     * The Elasticsearch client.
     *
     * @return The client
     */
    public Client getElasticsearchClient() {
        return _client;
    }
}
