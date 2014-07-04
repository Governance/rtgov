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
package org.overlord.rtgov.activity.store.elasticsearch;

import org.elasticsearch.action.admin.indices.refresh.RefreshRequestBuilder;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.activity.server.ActivityStore;
import org.overlord.rtgov.activity.server.QuerySpec;
import org.overlord.rtgov.activity.util.ActivityUtil;
import org.overlord.rtgov.common.elasticsearch.ElasticsearchClient;
import org.overlord.rtgov.common.util.RTGovProperties;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides the Elasticsearch implementation of the activityStore
 * CRUD operations are provided by  ElasticSearchKeyValueStore.
 * User: imk@redhat.com
 * Date: 20/04/14
 * Time: 23:32
 */
@SuppressWarnings("deprecation")
public class ElasticsearchActivityStore implements ActivityStore {
    private static final Logger LOG = Logger.getLogger(ElasticsearchActivityStore.class.getName());

    private static String ACTIVITYSTORE_UNIT_INDEX = "ActivityStore.Elasticsearch.index";
    private static String ACTIVITYSTORE_UNIT_TYPE = "ActivityStore.Elasticsearch.type";
    private static String ACTIVITYSTORE_RESPONSE_SIZE = "ActivityStore.Elasticsearch.responseSize";
    
    private static int DEFAULT_RESPONSE_SIZE = 100000;
    
    private int _responseSize;
    
    private ElasticsearchClient _client=new ElasticsearchClient();

    /**
     * The default constructor.
     */
    public ElasticsearchActivityStore() {
        _client.setIndex(RTGovProperties.getProperty(ACTIVITYSTORE_UNIT_INDEX, "rtgov"));
        _client.setType(RTGovProperties.getProperty(ACTIVITYSTORE_UNIT_TYPE, "activity"));
        
        _responseSize = RTGovProperties.getPropertyAsInteger(ACTIVITYSTORE_RESPONSE_SIZE, DEFAULT_RESPONSE_SIZE);
        
        try {
            _client.init();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * This method persists the activity unit in the Elasticsearch repository.
     * 
     * @param id The id
     * @param activityUnit The activity unit
     * @throws Exception Failed to persist
     */
    protected void persist(BulkRequestBuilder localBulkRequestBuilder, String id, ActivityUnit activityUnit) throws Exception {
        List<ActivityType> activityTypes = activityUnit.getActivityTypes();
        
        // Temporarily clear the list of activities, while the activity unit part is stored
        activityUnit.setActivityTypes(Collections.<ActivityType>emptyList());            
        localBulkRequestBuilder.add(_client.getElasticsearchClient().prepareIndex(_client.getIndex(), 
                    _client.getType(), id).setSource(ElasticsearchClient.convertTypeToJson(activityUnit)));
        activityUnit.setActivityTypes(activityTypes);            

        // Persist activity types
        for (int i = 0; i < activityTypes.size(); i++) {
            ActivityType activityType = activityTypes.get(i);
            localBulkRequestBuilder.add(_client.getElasticsearchClient().prepareIndex(_client.getIndex(), 
                    _client.getType() + "type", id + "-" + i).setParent(id).setSource(
                            ElasticsearchClient.convertTypeToJson(activityType)));
        }
    }

    /**
     * @param activities The list of activity events to store
     * @throws Exception if any activities cannot be stored
     */
    public void store(List<ActivityUnit> activities) throws Exception {
        BulkRequestBuilder localBulkRequestBuilder = _client.getElasticsearchClient().prepareBulk();

        for (int i=0; i < activities.size(); i++) {
            ActivityUnit activityUnit=activities.get(i);
            persist(localBulkRequestBuilder, activityUnit.getId(), activityUnit);
        }

        BulkResponse bulkItemResponses = localBulkRequestBuilder.execute().actionGet();

        if (bulkItemResponses.hasFailures()) {
            LOG.severe(" Bulk Documents{" + activities.size() + "} could not be created for index [" + _client.getIndex() + "/" + _client.getType() + "/");

            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("FAILED MESSAGES. " + bulkItemResponses.buildFailureMessage());
            }
            throw new Exception(" Bulk Documents{" + activities.size() + "} could not be created for index [" + _client.getIndex() + "/" + _client.getType() + "/ \n  " + bulkItemResponses.buildFailureMessage());
        } else {
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Success storing " + activities.size() + " items to  [" + _client.getIndex() + "/" + _client.getType() + "]");
            }
        }
    }

    /**
     * @param id The activity unit id
     * @return ActivityUnit as activity unit or null.
     * @throws Exception when activity unit cannot be got from elastic search.
     */
    public ActivityUnit getActivityUnit(String id) throws Exception {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Get activity unit for id["+id+"]");
        }
       
        if (id == null) {
            return null;

        } else {
            String jsonDoc = _client.get(id);
            if (jsonDoc != null) {
                ActivityUnit ret=ElasticsearchClient.<ActivityUnit>convertJsonToType(jsonDoc, ActivityUnit.class);
                
                // Retrieve the activity types associated with the activity unit
                SearchResponse response=_client.getElasticsearchClient().prepareSearch(_client.getIndex())
                        .setTypes(_client.getType()+"type")
                        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        .setSize(_responseSize)
                        .setQuery(QueryBuilders.matchQuery("unitId", id))
                        .execute().actionGet();
        
                // Using iterator instead of using index, as caused out of range exception,
                // so not sure if results are unstable
                for (SearchHit hit : response.getHits()) {
                    ret.getActivityTypes().add(ElasticsearchClient.convertJsonToType(hit.getSourceAsString(),
                                            ActivityType.class));
                }
                
                if (ret.getActivityTypes().size() > 0) {
                    // Sort the entries
                    Collections.<ActivityType>sort(ret.getActivityTypes(), new Comparator<ActivityType>() {
                        public int compare(ActivityType at1, ActivityType at2) {
                            return at1.getUnitIndex()-at2.getUnitIndex();
                        }                        
                    });
                }
                
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("Return reconstructed activity unit for id["+id+"]="+ActivityUtil.objectToJSONString(ret));
                }
               
                return ret;
            } else {
                return null;
            }
        }
    }

    /**
     * @param context The context value
     * @return List of activityTypes
     * @throws Exception in the event of a failure
     */
    public List<ActivityType> getActivityTypes(Context context) throws Exception {
        
        if (context == null) {
            throw new Exception(java.util.PropertyResourceBundle.getBundle(
                            "activity-store-elasticsearch.Messages").getString("ACTIVITY-STORE-ELASTICSEARCH-4"));
        }
        
        RefreshRequestBuilder refreshRequestBuilder = _client.getElasticsearchClient().admin().indices().prepareRefresh(_client.getIndex());
        _client.getElasticsearchClient().admin().indices().refresh(refreshRequestBuilder.request()).actionGet();
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("getActivityTypes=" + context);
        }

        QueryBuilder b2 = QueryBuilders.nestedQuery("context",
                QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchQuery("context.value",
                                context.getValue())).must(QueryBuilders.matchQuery("context.type", context.getType()))
        );

        SearchResponse response = _client.getElasticsearchClient().prepareSearch(
                _client.getIndex()).setTypes(_client.getType() + "type")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setSize(_responseSize)
                .setQuery(b2).execute().actionGet();
        
        if (response.isTimedOut()) {
            throw new Exception(MessageFormat.format(
                    java.util.PropertyResourceBundle.getBundle(
                            "activity-store-elasticsearch.Messages").getString("ACTIVITY-STORE-ELASTICSEARCH-3"),
                    _client.getIndex(), _client.getType(), b2.toString()
            ));
        }
        List<ActivityType> list = new ArrayList<ActivityType>();
        for (SearchHit searchHitFields : response.getHits()) {
            list.add(ElasticsearchClient.<ActivityType>convertJsonToType(searchHitFields.getSourceAsString(),
                                            ActivityType.class));
        }
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Returning activity list for context '"+context+"': "
                        +new String(ActivityUtil.serializeActivityTypeList(list)));
        }
        
        return list;
    }

    /**
     * @param context The optional context value
     * @param from    The 'from' timestamp
     * @param to      The 'to' timestamp
     * @return List of actvitiyTypes
     * @throws Exception in the event of timeout.
     */
    public List<ActivityType> getActivityTypes(Context context, long from, long to) throws Exception {
        
        // If default time range, then use the alternate method based on querying just the context
        if (from == 0 && to == 0) {
            return getActivityTypes(context);
        }
        
        RefreshRequestBuilder refreshRequestBuilder = _client.getElasticsearchClient().admin().indices().prepareRefresh(_client.getIndex());
        _client.getElasticsearchClient().admin().indices().refresh(refreshRequestBuilder.request()).actionGet();
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("getActivityTypes=" + context);
        }

        if (to == 0) {
            to = System.currentTimeMillis();
        }
        
        BoolQueryBuilder b2 = QueryBuilders.boolQuery()
                .must(QueryBuilders.rangeQuery("timestamp").from(from).to(to));
        
        if (context != null) {
                b2 = b2.must(
                        QueryBuilders.nestedQuery("context",               // Path
                                QueryBuilders.boolQuery()
                                        .must(QueryBuilders.matchQuery("context.value", context.getValue()))
                                        .must(QueryBuilders.matchQuery("context.type", context.getType()))
                        )
                );
        }

        SearchResponse response = _client.getElasticsearchClient().prepareSearch(
                _client.getIndex()).setTypes(_client.getType() + "type")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setSize(_responseSize)
                .setQuery(b2).execute().actionGet();
        if (response.isTimedOut()) {
            throw new Exception(MessageFormat.format(
                    java.util.PropertyResourceBundle.getBundle(
                            "activity-store-elasticsearch.Messages").getString("ACTIVITY-STORE-ELASTICSEARCH-3"),
                    _client.getIndex(), _client.getType(), b2.toString()
            ));
        }
        List<ActivityType> list = new ArrayList<ActivityType>();
        for (SearchHit searchHitFields : response.getHits()) {
            list.add(ElasticsearchClient.<ActivityType>convertJsonToType(searchHitFields.getSourceAsString(),
                                ActivityType.class));
        }
        return list;
    }

    /**
     * Elasticsearch query DSL. further language reference can be found under
     * http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/query-dsl.html#query-dsl
     * No validation is performed against query and should format be incorrect api simply fails fast and returns a exception
     *
     * @param query The query. Of type elastic search Query DSL
     * @return null
     * @throws Exception UnsupportedOperationException
     */
    @Deprecated
    public List<ActivityType> query(QuerySpec query) throws Exception {
        throw new UnsupportedOperationException("Query method not support by Elasticsearch Actvitystore");
    }
    
    /**
     * This method returns the client.
     * 
     * @return The client
     */
    protected ElasticsearchClient getClient() {
        return (_client);
    }
}
