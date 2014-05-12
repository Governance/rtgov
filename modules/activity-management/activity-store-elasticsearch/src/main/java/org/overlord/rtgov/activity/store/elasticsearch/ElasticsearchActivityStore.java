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
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.activity.server.ActivityStore;
import org.overlord.rtgov.activity.server.QuerySpec;
import org.overlord.rtgov.common.elasticsearch.ElasticSearchKeyValueStore;
import org.overlord.rtgov.common.util.RTGovProperties;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Singleton;

/**
 * This class provides the Elasticsearch implementation of the activityStore
 * CRUD operations are provided by  ElasticSearchKeyValueStore.
 * User: imk@redhat.com
 * Date: 20/04/14
 * Time: 23:32
 */
@Singleton
public class ElasticsearchActivityStore extends ElasticSearchKeyValueStore implements ActivityStore {
    private static final Logger LOG = Logger.getLogger(ElasticsearchActivityStore.class.getName());

    private static String ACTIVITYSTORE_UNIT_INDEX = "ActivityStore.Elasticsearch.index";
    private static String ACTIVITYSTORE_UNIT_TYPE = "ActivityStore.Elasticsearch.type";

    /**
     * Preset, configures activityunit store index and type from the rtgov properties "ActivityStore.Elasticsearch.index" and "ActivityStore.Elasticsearch.type".
     * If the properties _hosts, index and type are not set then defaults are loaded. these values can be overriden by setting ACTIVITYSTORE_UNIT_INDEX, ACTIVITYSTORE_UNIT_TYPE defaults in RTgov.properties
     *
     * @throws Exception when a connection cannot be established
     */
    @Override
    public void init() throws Exception {
        if (getHosts() == null) {
            setHosts(RTGovProperties.getProperty(ELASTICSEARCH_STORE_HOSTS));
        }
        if (getIndex() == null || getIndex().length() == 0) {
            setIndex(RTGovProperties.getProperty(ACTIVITYSTORE_UNIT_INDEX, "rtgov"));
        }
        if (getType() == null || getIndex().length() == 0) {
            setType(RTGovProperties.getProperty(ACTIVITYSTORE_UNIT_TYPE, "activity"));
        }
        super.init();


    }

    private void splitAU(ActivityUnit au, BulkRequestBuilder bulkRequestBuilder) {

    }

    //todo, override .add implementation

    /**
     * @param id       id to store doucment
     * @param document Activitiytpype
     * @param <V>      document to be saved
     * @throws Exception when document is not of type activityStore
     */
    @Override
    public <V> void add(String id, V document) throws Exception {
        if (document instanceof ActivityUnit) {
            BulkRequestBuilder localBulkRequestBuilder = getClient().prepareBulk();

            ActivityUnit activityUnit = (ActivityUnit) document;
            List<ActivityType> activityTypes = activityUnit.getActivityTypes();
            activityUnit.setActivityTypes(new ArrayList<ActivityType>());
            localBulkRequestBuilder.add(getClient().prepareIndex(getIndex(), getType(), id).setSource(convertTypeToJson(activityUnit)));
            for (int i = 0; i < activityTypes.size(); i++) {
                ActivityType activityType = activityTypes.get(i);
                localBulkRequestBuilder.add(getClient().prepareIndex(getIndex(), getType() + "type", id + "-" + i).setParent(id).setSource(convertTypeToJson(activityType)));
            }

            BulkResponse bulkItemResponses = localBulkRequestBuilder.execute().actionGet();
            if (bulkItemResponses.hasFailures()) {
                LOG.severe(" add Documents{" + id + "} could not be created for index [" + getIndex() + "/" + getType() + "/");

                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("FAILED MESSAGES. " + bulkItemResponses.buildFailureMessage());
                }
                throw new Exception(" add Documents{" + id + "} could not be created for index [" + getIndex() + "/" + getType() + "/ \n  " + bulkItemResponses.buildFailureMessage());
            } else {
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("Success storing " + id + " items to  [" + getIndex() + "/" + getType() + "]");
                }
            }
        } else {
            throw new IllegalArgumentException("Document to be store not of type " + ActivityUnit.class.toString());
        }
    }

    /**
     * @param activities The list of activity events to store
     * @throws Exception if any activities cannot be stored
     */
    public void store(List<ActivityUnit> activities) throws Exception {
        BulkRequestBuilder localBulkRequestBuilder = getClient().prepareBulk();

        for (ActivityUnit activityUnit : activities) {
            List<ActivityType> activityTypes = activityUnit.getActivityTypes();
            activityUnit.setActivityTypes(null);
            localBulkRequestBuilder.add(getClient().prepareIndex(getIndex(), getType(), activityUnit.getId()).setSource(convertTypeToJson(activityUnit)));

            for (ActivityType activityType : activityTypes) {
                localBulkRequestBuilder.add(getClient().prepareIndex(getIndex(), getType() + "type", activityUnit.getId() + "-" + getRandom()).setParent(activityUnit.getId()).setSource(convertTypeToJson(activityType)));

            }

        }
        BulkResponse bulkItemResponses = localBulkRequestBuilder.execute().actionGet();

        if (bulkItemResponses.hasFailures()) {
            LOG.severe(" Bulk Documents{" + activities.size() + "} could not be created for index [" + getIndex() + "/" + getType() + "/");

            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("FAILED MESSAGES. " + bulkItemResponses.buildFailureMessage());
            }
            throw new Exception(" Bulk Documents{" + activities.size() + "} could not be created for index [" + getIndex() + "/" + getType() + "/ \n  " + bulkItemResponses.buildFailureMessage());
        } else {
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Success storing " + activities.size() + " items to  [" + getIndex() + "/" + getType() + "]");
            }
        }


    }

    /**
     * @param id The activity unit id
     * @return ActivityUnit as activity unit or null.
     * @throws Exception when activity unit cannot be got from elastic search.
     */
    public ActivityUnit getActivityUnit(String id) throws Exception {
        if (id == null) {
            return null;

        } else {
            String jsonDoc = get(id);
            if (jsonDoc != null) {
                return MAPPER.readValue(jsonDoc, ActivityUnit.class);
            } else {
                return null;
            }
        }


    }

    /**
     * @param context The context value
     * @return List of activityTypes
     * @throws Exception in the event of timeout
     */
    public List<ActivityType> getActivityTypes(Context context) throws Exception {
        RefreshRequestBuilder refreshRequestBuilder = getClient().admin().indices().prepareRefresh(getIndex());
        RefreshResponse refreshResponse = getClient().admin().indices().refresh(refreshRequestBuilder.request()).actionGet();
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("getActivityTypes=" + context);
        }

        QueryBuilder b2 = QueryBuilders.nestedQuery("context",               // Path
                QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchQuery("context.value", context.getValue())).must(QueryBuilders.matchQuery("context.type", context.getType()))
        );

        SearchResponse response = getClient().prepareSearch(getIndex()).setTypes(getType() + "type").setQuery(b2).execute().actionGet();
        if (response.isTimedOut()) {
            throw new Exception(MessageFormat.format(
                    java.util.PropertyResourceBundle.getBundle(
                            "activity-store-elasticsearch.Messages").getString("ACTIVITY-STORE-ELASTICSEARCH-3"),
                    getIndex(), getType(), b2.toString()
            ));
        }
        List<ActivityType> list = new ArrayList<ActivityType>();
        for (SearchHit searchHitFields : response.getHits()) {
            list.add(MAPPER.readValue(searchHitFields.getSourceAsString(), ActivityType.class));
        }
        return list;
    }

    /**
     * @param context The context value
     * @param from    The 'from' timestamp
     * @param to      The 'to' timestamp
     * @return List of actvitiyTypes
     * @throws Exception in the event of timeout.
     */
    public List<ActivityType> getActivityTypes(Context context, long from, long to) throws Exception {
        RefreshRequestBuilder refreshRequestBuilder = getClient().admin().indices().prepareRefresh(getIndex());
        RefreshResponse refreshResponse = getClient().admin().indices().refresh(refreshRequestBuilder.request()).actionGet();
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("getActivityTypes=" + context);
        }

        QueryBuilder b2 = QueryBuilders.boolQuery()
                .must(
                        QueryBuilders.rangeQuery("timestamp").from(from).to(to))
                .must(
                        QueryBuilders.nestedQuery("context",               // Path
                                QueryBuilders.boolQuery()
                                        .must(QueryBuilders.matchQuery("context.value", context.getValue()))
                                        .must(QueryBuilders.matchQuery("context.type", context.getType()))
                        )
                );

        SearchResponse response = getClient().prepareSearch(getIndex()).setTypes(getType() + "type").setQuery(b2).execute().actionGet();
        if (response.isTimedOut()) {
            throw new Exception(MessageFormat.format(
                    java.util.PropertyResourceBundle.getBundle(
                            "activity-store-elasticsearch.Messages").getString("ACTIVITY-STORE-ELASTICSEARCH-3"),
                    getIndex(), getType(), b2.toString()
            ));
        }
        List<ActivityType> list = new ArrayList<ActivityType>();
        for (SearchHit searchHitFields : response.getHits()) {
            list.add(MAPPER.readValue(searchHitFields.getSourceAsString(), ActivityType.class));
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
}
