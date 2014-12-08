/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.rtgov.analytics.situation.store.elasticsearch;

import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.overlord.rtgov.analytics.situation.Situation;
import org.overlord.rtgov.analytics.situation.store.SituationStore;
import org.overlord.rtgov.analytics.situation.store.SituationsQuery;
import org.overlord.rtgov.analytics.situation.store.AbstractSituationStore;
import org.overlord.rtgov.analytics.situation.store.ResolutionState;
import org.overlord.rtgov.analytics.util.SituationUtil;
import org.overlord.rtgov.common.elasticsearch.ElasticsearchClient;
import org.overlord.rtgov.common.registry.ServiceClose;
import org.overlord.rtgov.common.registry.ServiceInit;
import org.overlord.rtgov.common.util.RTGovProperties;

/**
 * This class provides the Elastcsearch based implementation of the SituationsStore
 * interface.
 * 
 */
public class ElasticsearchSituationStore extends AbstractSituationStore implements SituationStore {

    private static final String RESOLUTION_STATE_UNRESOLVED = "unresolved";

    private static final Logger LOG = Logger.getLogger(ElasticsearchSituationStore.class.getName());

    private static String SITUATIONSTORE_UNIT_INDEX = "SituationStore.Elasticsearch.index";
    private static String SITUATIONSTORE_UNIT_TYPE = "SituationStore.Elasticsearch.type";
    private static String SITUATIONSTORE_RESPONSE_SIZE = "SituationStore.Elasticsearch.responseSize";
    private static String SITUATIONSTORE_TIMEOUT = "SituationStore.Elasticsearch.timeout";

    private static final int PROPERTY_VALUE_MAX_LENGTH = 250;

    private static int DEFAULT_RESPONSE_SIZE = 100000;
    private static long DEFAULT_TIMEOUT = 10000L;
    
    private int _responseSize;
    private long _timeout;
    
    private ElasticsearchClient _client=new ElasticsearchClient();
    
    /**
     * Constructor.
     */
    public ElasticsearchSituationStore() {
    }
    
    /**
     * Initialize the situation store.
     */
    @ServiceInit
    public void init() {
        _client.setIndex(RTGovProperties.getProperty(SITUATIONSTORE_UNIT_INDEX, "rtgov"));
        _client.setType(RTGovProperties.getProperty(SITUATIONSTORE_UNIT_TYPE, "situation"));
        
        _responseSize = RTGovProperties.getPropertyAsInteger(SITUATIONSTORE_RESPONSE_SIZE, DEFAULT_RESPONSE_SIZE);
        _timeout = RTGovProperties.getPropertyAsLong(SITUATIONSTORE_TIMEOUT, DEFAULT_TIMEOUT);
        
        try {
            _client.init();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, java.util.PropertyResourceBundle
                        .getBundle("situation-store-elasticsearch.Messages").getString("SITUATION-STORE-ELASTICSEARCH-1"), e);
        }
    }
    
    /**
     * This method sets the response size.
     * 
     * @param size The size
     */
    protected void setResponseSize(int size) {
        _responseSize = size;
    }

    /**
     * This method sets the response size.
     * 
     * @param size The size
     */
    protected int getResponseSize() {
        return (_responseSize);
    }

    /**
     * {@inheritDoc}
     */
    public void store(final Situation situation) throws Exception {
        
        if (_client != null) {
            _client.add(situation.getId(), ElasticsearchClient.convertTypeToJson(situation));
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Situation getSituation(final String id) {
        Situation ret=null;
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Get situation: "+id); //$NON-NLS-1$
        }

        if (_client != null) {
            String json=_client.get(id);
            
            if (json != null) {
                try {
                    ret = SituationUtil.deserializeSituation(json.getBytes());
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, java.util.PropertyResourceBundle
                            .getBundle("situation-store-elasticsearch.Messages").getString("SITUATION-STORE-ELASTICSEARCH-2"), e);
                }
            }
        }

        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Situation="+ret); //$NON-NLS-1$
        }

        return (ret);
    }
    
    /**
     * {@inheritDoc}
     */
    public List<Situation> getSituations(final SituationsQuery sitQuery) {        
        List<Situation> situations = new java.util.ArrayList<Situation>();
        
        SearchResponse response=_client.getElasticsearchClient().prepareSearch(_client.getIndex())
                        .setTypes(_client.getType())
                        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        .setTimeout(TimeValue.timeValueMillis(_timeout))
                        .setSize(_responseSize)
                        .setQuery(getQueryBuilder(sitQuery))
                        .execute().actionGet();
        
        long num=response.getHits().getTotalHits();
        
        if (num > _responseSize) {
            num = _responseSize;
        }
        
        for (int i=0; i < num; i++) {
            SearchHit hit=response.getHits().getAt(i);
            
            try {
                situations.add(SituationUtil.deserializeSituation(hit.getSourceAsString().getBytes()));
            } catch (Exception e) {
                LOG.log(Level.SEVERE, java.util.PropertyResourceBundle
                        .getBundle("situation-store-elasticsearch.Messages").getString("SITUATION-STORE-ELASTICSEARCH-2"), e);
            }
        }
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Situations="+situations); //$NON-NLS-1$
        }
        
        return (situations);
    }
    
    protected QueryBuilder getQueryBuilder(SituationsQuery sitQuery) {
        QueryBuilder qb=QueryBuilders.matchAllQuery();
        FilterBuilder filter=null;
        
        if (sitQuery != null) {
            BoolQueryBuilder bool=QueryBuilders.boolQuery();
            
            if (!isNullOrEmpty(sitQuery.getResolutionState())) {
                if (sitQuery.getResolutionState().equalsIgnoreCase(RESOLUTION_STATE_UNRESOLVED)) {
                    filter = FilterBuilders.missingFilter("properties."+SituationStore.RESOLUTION_STATE_PROPERTY);
                } else {
                    bool.must(QueryBuilders.matchQuery("properties."+SituationStore.RESOLUTION_STATE_PROPERTY, sitQuery.getResolutionState()));
                }
            }
            
            if (sitQuery.getProperties() != null && !sitQuery.getProperties().isEmpty()) {
                Set<Entry<Object,Object>> entrySet = sitQuery.getProperties().entrySet();
                for (Entry<Object, Object> entry : entrySet) {
                    Object key = entry.getKey();
                    Object value = entry.getValue();
                    if (value instanceof String) {
                        bool.must(QueryBuilders.fuzzyLikeThisFieldQuery("properties."+key).likeText((String)value));
                    } else {
                        bool.must(QueryBuilders.matchQuery("properties."+key, value));
                    }
                }
            }
            
            if (!isNullOrEmpty(sitQuery.getDescription())) {
                bool.must(QueryBuilders.fuzzyLikeThisFieldQuery("description").likeText(sitQuery.getDescription()));
            }
            
            if (!isNullOrEmpty(sitQuery.getSubject())) {
                bool.must(QueryBuilders.fuzzyLikeThisFieldQuery("subject").likeText(sitQuery.getSubject()));
            }
            
            if (!isNullOrEmpty(sitQuery.getType())) {
                bool.must(QueryBuilders.fuzzyLikeThisFieldQuery("type").likeText(sitQuery.getType()));
            }
            
            if (sitQuery.getSeverity() != null) {
                bool.must(QueryBuilders.matchQuery("severity", sitQuery.getSeverity().name()));
            }
            
            if (sitQuery.getFromTimestamp() > 0 || sitQuery.getToTimestamp() > 0) {
                long from=(sitQuery.getFromTimestamp() > 0 ? sitQuery.getFromTimestamp() : 0);
                long to=(sitQuery.getToTimestamp() > 0 ? sitQuery.getToTimestamp() : System.currentTimeMillis()+2000);
                
                bool.must(QueryBuilders.rangeQuery("timestamp").from(from).to(to));
            }
            
            if (bool.hasClauses()) {
                qb = bool;
            }
        }
        
        if (filter != null) {
            return (QueryBuilders.filteredQuery(qb, filter));
        }
        
        return (qb);
    }
    
    /**
     * Check if the supplied string is null or empty (after removing
     * whitespaces).
     * 
     * @param str The string to test
     * @return Whether the supplied string is null or empty
     */
    protected boolean isNullOrEmpty(String str) {
        return (str == null || str.trim().length() == 0);
    }

    /**
     * {@inheritDoc}
     */
    public void assignSituation(final String situationId, final String userName) {
        Situation sit=getSituation(situationId);
        
        if (sit != null) {
            doAssignSituation(sit, userName);
            
            // Save the updated situation
            _client.update(situationId, ElasticsearchClient.convertTypeToJson(sit));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void unassignSituation(final String situationId) {
        Situation sit=getSituation(situationId);
        
        if (sit != null) {
            doUnassignSituation(sit);
            
            // Save the updated situation
            _client.update(situationId, ElasticsearchClient.convertTypeToJson(sit));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void updateResolutionState(final String situationId, final ResolutionState resolutionState) {
        Situation sit=getSituation(situationId);
        
        if (sit != null) {
            doUpdateResolutionState(sit, resolutionState);
            
            // Save the updated situation
            _client.update(situationId, ElasticsearchClient.convertTypeToJson(sit));
        }
    }

    @Override
    public void recordSuccessfulResubmit(final String situationId, final String userName) {
        Situation sit=getSituation(situationId);
        
        if (sit != null) {
            doRecordSuccessfulResubmit(sit, userName);
            
            // Save the updated situation
            _client.update(situationId, ElasticsearchClient.convertTypeToJson(sit));
        }
    }

    @Override
    public void recordResubmitFailure(final String situationId, final String errorMessage, final String userName) {
        Situation sit=getSituation(situationId);
        
        if (sit != null) {
            String message = (errorMessage == null ? "" : errorMessage);
            if (message.length() > PROPERTY_VALUE_MAX_LENGTH) {
                message = message.substring(0, PROPERTY_VALUE_MAX_LENGTH);
            }
            doRecordResubmitFailure(sit, message, userName);
            
            // Save the updated situation
            _client.update(situationId, ElasticsearchClient.convertTypeToJson(sit));
        }
    }

    /**
     * This method deletes the supplied situation.
     * 
     * @param situation The situation
     */
    protected void doDelete(final Situation situation) {
        if (_client != null) {
            try {
                _client.remove(situation.getId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * This method returns the elasticsearch client.
     * 
     * @return The client
     */
    protected ElasticsearchClient getClient() {
        return (_client);
    }
    
    /**
     * Close the situation store.
     */
    @ServiceClose
    public void close() {
        if (_client != null) {
            _client.close();
            _client = null;
        }
    }
}
