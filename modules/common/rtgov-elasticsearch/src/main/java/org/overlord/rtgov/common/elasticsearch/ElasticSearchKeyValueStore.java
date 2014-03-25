package org.overlord.rtgov.common.elasticsearch;

import org.apache.commons.io.IOUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.overlord.rtgov.common.service.KeyValueStore;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: imk@redhat.com
 * Date: 23/03/14
 * Time: 22:03
 */
public class ElasticSearchKeyValueStore extends KeyValueStore {
    protected Client client;

    protected String index = null;
    protected String type = null;
    //todo . Make configurable Elastic Search url. eg a list would be desired. currently not a list because of legacy rest version i had

    //todo change host and port to be a list of connections as opposed to a single connection.
    protected String host = null;
    protected int port = -1;
    /**
     * settings for the index this store is related to
     */
    public static final String SETTINGS = "settings";
    /**
     * type mappings for the index this store is related to
     */
    public static final String MAPPINGS = "mappings";
    private static final Logger LOG = Logger.getLogger(ElasticSearchKeyValueStore.class.getName());

    @Override
    public String toString() {
        return "AbstractElasticRepo{" +
                "index='" + index + '\'' +
                ", type='" + type + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                '}';
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index.toLowerCase();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        //
        this.type = type.toLowerCase();
    }

    public ElasticSearchKeyValueStore() {

    }

    public void init() throws Exception {

        if (host == null)
            throw new IllegalArgumentException("Host property not set ");
        if (port < 0)
            throw new IllegalArgumentException("Port property not set ");
        if (index == null)
            throw new IllegalArgumentException("Index property not set ");
        if (type == null)
            throw new IllegalArgumentException("Type property not set ");


        client = new TransportClient()
                .addTransportAddress(new InetSocketTransportAddress(host, port));
        InputStream s = Thread.currentThread().getContextClassLoader().getResourceAsStream(index + ".json");
        if (s == null) {
            throw new FileNotFoundException("Could not locate " + index + ".json mapping file. Mapping file require to start elasticsearch store service");
        }
        String jsonDefaultUserIndex = IOUtils.toString(s);
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("index Mapping settings " + index + ".json  [" + jsonDefaultUserIndex + "]");
        }
        //  String jsonDefaultUserIndex = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(index + ".json"));
        Map<String, Object> dataMap = XContentFactory.xContent(jsonDefaultUserIndex).createParser(jsonDefaultUserIndex).mapAndClose();

        if (prepareIndex((Map<String, Object>) dataMap.get(SETTINGS))) {
            System.out.println("");//  prepareIndex()
        }
        prepareMapping((Map<String, Object>) dataMap.get(MAPPINGS));

    }

    /**
     * @param defaultMappings
     * @return true if the mapping was successful
     */
    private boolean prepareMapping(Map<String, Object> defaultMappings) {

        // only prepare the mapping for the configured repo type
        Map<String, Object> mapping = (Map<String, Object>) defaultMappings.get(type);
        if (mapping == null) {
            throw new RuntimeException("type mapping not defined");
        }
        PutMappingRequestBuilder putMappingRequestBuilder = client.admin().indices().preparePutMapping().setIndices(index);
        putMappingRequestBuilder.setType(type);
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
        IndicesExistsResponse res = client.admin().indices().prepareExists(index).execute().actionGet();
        boolean created = false;
        if (!res.isExists()) {
            CreateIndexRequestBuilder req = client.admin().indices().prepareCreate(index);
            req.setSettings(defaultSettings);
            created = req.execute().actionGet().isAcknowledged();
            if (!created)
                throw new RuntimeException("Could not create index");
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
