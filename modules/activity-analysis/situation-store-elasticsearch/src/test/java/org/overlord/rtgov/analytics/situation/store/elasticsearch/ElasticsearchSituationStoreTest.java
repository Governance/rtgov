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
package org.overlord.rtgov.analytics.situation.store.elasticsearch;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.NodeBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.overlord.rtgov.analytics.situation.Situation;
import org.overlord.rtgov.analytics.situation.Situation.Severity;
import org.overlord.rtgov.analytics.situation.store.ResolutionState;
import org.overlord.rtgov.analytics.situation.store.SituationStore;
import org.overlord.rtgov.analytics.situation.store.SituationsQuery;
import org.overlord.rtgov.common.util.RTGovProperties;
import org.overlord.rtgov.common.util.RTGovPropertiesProvider;

import com.google.common.base.Strings;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.overlord.rtgov.analytics.situation.store.ResolutionState.IN_PROGRESS;

public class ElasticsearchSituationStoreTest {
    private static final String TEST_HOST = "theworld";
    private static final String SITUATION_ID_1 = "Situation_id_1";
    private static final String SITUATION_ID_2 = "Situation_id_2";
    private static final String SITUATION_ID_3 = "Situation_id_3";
    
    private static ElasticsearchSituationStore elasticsearchSituationStore;

    /**
     * elastic search index to test against
     */
    private static String index = "rtgovtest";
    
    /**
     * elastich search host
     */
    private static String host = "localhost";
    
    /**
     * elasticsearch port
     */
    private static int port = 9300;
    
    /**
     * elasticsearch type to test
     */
    private static String type = "situation";

    public static class TestPropertiesProvider implements RTGovPropertiesProvider {

        private java.util.Properties _properties = new java.util.Properties();

        public TestPropertiesProvider() {
            _properties = new Properties();
            _properties.setProperty("Elasticsearch.hosts", host + ":" + 9300);
            //_properties.setProperty("Elasticsearch.hosts", host);
            _properties.setProperty("Elasticsearch.schedule", "3000");
            _properties.setProperty("SituationStore.Elasticsearch.type", type);
            _properties.setProperty("SituationStore.Elasticsearch.index", index);


        }

        public String getProperty(String name) {
            return _properties.getProperty(name);
        }

        public Properties getProperties() {
            return _properties;
        }

    }

    /**
     * tear down test index again.
     * @throws Exception
     */
    @AfterClass
    public static void tearDown() throws Exception {
        Client c = new TransportClient();
        if(host.equals("embedded"))
           c= NodeBuilder.nodeBuilder().local(true).node().client();
        else{

            c = new TransportClient().addTransportAddress(new InetSocketTransportAddress(host, port));

        }
        c.admin().indices().prepareDelete(index).execute().actionGet();
    }

    /**
     * tear down after test.^
     * @throws Exception
     */
    @BeforeClass
    public static void initialiseStore() throws Exception {
        TestPropertiesProvider provider = new TestPropertiesProvider();
        Client c = new TransportClient();
        if(host.equals("embedded"))
            c= NodeBuilder.nodeBuilder().local(true).node().client();
        else{

            c = new TransportClient().addTransportAddress(new InetSocketTransportAddress(host, port));

        }
        // remove index.
        if (c.admin().indices().prepareExists(index).execute().actionGet().isExists()) {

            c.admin().indices().prepareDelete(index).execute().actionGet();
        }
        
        RTGovProperties.setPropertiesProvider(provider);

        elasticsearchSituationStore = new ElasticsearchSituationStore();

    }
    
    @org.junit.Before
    public void removeSituations() {
        elasticsearchSituationStore.delete(new SituationsQuery());
    }

    @Test
    public void testStoreAndGetSituation() {
        try {
            Situation s1=new Situation();
            s1.setId(SITUATION_ID_1);
            s1.setTimestamp(System.currentTimeMillis());
            elasticsearchSituationStore.store(s1);
        } catch (Exception e) {

            fail("Could not store situation " + e);
        }
        try {
            Situation s1 = elasticsearchSituationStore.getSituation(SITUATION_ID_1);
            if (s1 != null) {
                if (!s1.getId().equals(SITUATION_ID_1))
                    fail("Situation id mismatch");

            } else
                fail("Situation is null");

        } catch (Exception e) {
            fail("Failed to get situation: " + e);

        }
        
        try {
            elasticsearchSituationStore.getClient().remove(SITUATION_ID_1);
        } catch (Exception e) {
            fail("Could not remove situation" + e);
        }
    }

    @Test
    public void testQueryAllSituations() {
        try {
            Situation s1=new Situation();
            s1.setId(SITUATION_ID_1);
            s1.setTimestamp(System.currentTimeMillis());
            elasticsearchSituationStore.store(s1);

            Situation s2=new Situation();
            s2.setId(SITUATION_ID_2);
            s2.setTimestamp(System.currentTimeMillis()+100);
            elasticsearchSituationStore.store(s2);
            
            // Need to delay to allow situations to be index, and therefore become searchable
            synchronized (this) {
                wait(2000);                
            }
        } catch (Exception e) {

            fail("Could not store situation " + e);
        }
        
        try {
            java.util.List<Situation> sits = elasticsearchSituationStore.getSituations(null);
            if (sits != null) {
                if (sits.size() != 2) {
                    fail("Expecting 2 situations: "+sits.size());
                }
                
                if (!sits.get(0).getId().equals(SITUATION_ID_1)) {
                    fail("Expecting entry 1 to have id '"+SITUATION_ID_1+"', but got: "+sits.get(0).getId());
                }
                
                if (!sits.get(1).getId().equals(SITUATION_ID_2)) {
                    fail("Expecting entry 2 to have id '"+SITUATION_ID_2+"', but got: "+sits.get(1).getId());
                }
            } else {
                fail("Situations list is null");
            }
        } catch (Exception e) {
            fail("Failed to get situation: " + e);

        }
        
        try {
            elasticsearchSituationStore.getClient().remove(SITUATION_ID_1);
            elasticsearchSituationStore.getClient().remove(SITUATION_ID_2);
        } catch (Exception e) {
            fail("Could not remove situation" + e);
        }
    }

    @Test
    public void testQuerySituationsResolutionStateResolved() {
        try {
            Situation s1=new Situation();
            s1.setId(SITUATION_ID_1);
            s1.setTimestamp(System.currentTimeMillis());
            elasticsearchSituationStore.store(s1);

            Situation s2=new Situation();
            s2.setId(SITUATION_ID_2);
            s2.setTimestamp(System.currentTimeMillis()+100);
            s2.getSituationProperties().put(SituationStore.RESOLUTION_STATE_PROPERTY, ResolutionState.RESOLVED.name());
            elasticsearchSituationStore.store(s2);
            
            // Need to delay to allow situations to be index, and therefore become searchable
            synchronized (this) {
                wait(2000);                
            }
        } catch (Exception e) {

            fail("Could not store situation " + e);
        }
        
        try {
            SituationsQuery query=new SituationsQuery();
            query.setResolutionState(ResolutionState.RESOLVED.name());
            
            java.util.List<Situation> sits = elasticsearchSituationStore.getSituations(query);
            if (sits != null) {
                if (sits.size() != 1) {
                    fail("Expecting 1 situations: "+sits.size());
                }
                
                if (!sits.get(0).getId().equals(SITUATION_ID_2)) {
                    fail("Expecting entry 1 to have id '"+SITUATION_ID_2+"', but got: "+sits.get(0).getId());
                }
           } else {
                fail("Situations list is null");
            }
        } catch (Exception e) {
            fail("Failed to get situation: " + e);

        }
        
        try {
            elasticsearchSituationStore.getClient().remove(SITUATION_ID_1);
            elasticsearchSituationStore.getClient().remove(SITUATION_ID_2);
        } catch (Exception e) {
            fail("Could not remove situation" + e);
        }
    }

    @Test
    public void testQuerySituationsHost() {
        try {
            Situation s1=new Situation();
            s1.setId(SITUATION_ID_1);
            s1.setTimestamp(System.currentTimeMillis());
            elasticsearchSituationStore.store(s1);

            Situation s2=new Situation();
            s2.setId(SITUATION_ID_2);
            s2.setTimestamp(System.currentTimeMillis()+100);
            s2.getSituationProperties().put(SituationStore.HOST_PROPERTY, TEST_HOST);
            elasticsearchSituationStore.store(s2);
            
            // Need to delay to allow situations to be index, and therefore become searchable
            synchronized (this) {
                wait(2000);                
            }
        } catch (Exception e) {

            fail("Could not store situation " + e);
        }
        
        try {
            SituationsQuery query=new SituationsQuery();
            query.setHost(TEST_HOST);
            
            java.util.List<Situation> sits = elasticsearchSituationStore.getSituations(query);
            if (sits != null) {
                if (sits.size() != 1) {
                    fail("Expecting 1 situations: "+sits.size());
                }
                
                if (!sits.get(0).getId().equals(SITUATION_ID_2)) {
                    fail("Expecting entry 1 to have id '"+SITUATION_ID_2+"', but got: "+sits.get(0).getId());
                }
           } else {
                fail("Situations list is null");
            }
        } catch (Exception e) {
            fail("Failed to get situation: " + e);

        }
        
        try {
            elasticsearchSituationStore.getClient().remove(SITUATION_ID_1);
            elasticsearchSituationStore.getClient().remove(SITUATION_ID_2);
        } catch (Exception e) {
            fail("Could not remove situation" + e);
        }
    }

    @Test
    public void testQuerySituationsResolvedANDHost() {
        try {
            Situation s1=new Situation();
            s1.setId(SITUATION_ID_1);
            s1.setTimestamp(System.currentTimeMillis());
            s1.getSituationProperties().put(SituationStore.RESOLUTION_STATE_PROPERTY, ResolutionState.RESOLVED.name());
            elasticsearchSituationStore.store(s1);

            Situation s2=new Situation();
            s2.setId(SITUATION_ID_2);
            s2.setTimestamp(System.currentTimeMillis()+100);
            s2.getSituationProperties().put(SituationStore.HOST_PROPERTY, TEST_HOST);
            s2.getSituationProperties().put(SituationStore.RESOLUTION_STATE_PROPERTY, ResolutionState.RESOLVED.name());
            elasticsearchSituationStore.store(s2);
            
            Situation s3=new Situation();
            s3.setId(SITUATION_ID_3);
            s3.setTimestamp(System.currentTimeMillis()+200);
            s3.getSituationProperties().put(SituationStore.HOST_PROPERTY, TEST_HOST);
            elasticsearchSituationStore.store(s3);
            
            // Need to delay to allow situations to be index, and therefore become searchable
            synchronized (this) {
                wait(2000);                
            }
        } catch (Exception e) {

            fail("Could not store situation " + e);
        }
        
        try {
            SituationsQuery query=new SituationsQuery();
            query.setHost(TEST_HOST);
            query.setResolutionState(ResolutionState.RESOLVED.name());
           
            java.util.List<Situation> sits = elasticsearchSituationStore.getSituations(query);
            if (sits != null) {
                if (sits.size() != 1) {
                    fail("Expecting 1 situations: "+sits.size());
                }
                
                if (!sits.get(0).getId().equals(SITUATION_ID_2)) {
                    fail("Expecting entry 1 to have id '"+SITUATION_ID_2+"', but got: "+sits.get(0).getId());
                }
           } else {
                fail("Situations list is null");
            }
        } catch (Exception e) {
            fail("Failed to get situation: " + e);

        }
        
        try {
            elasticsearchSituationStore.getClient().remove(SITUATION_ID_1);
            elasticsearchSituationStore.getClient().remove(SITUATION_ID_2);
            elasticsearchSituationStore.getClient().remove(SITUATION_ID_3);
        } catch (Exception e) {
            fail("Could not remove situation" + e);
        }
    }
    
    @Test
    public void testQuerySituationsDescription() {
        try {
            Situation s1=new Situation();
            s1.setId(SITUATION_ID_1);
            s1.setTimestamp(System.currentTimeMillis());
            elasticsearchSituationStore.store(s1);

            Situation s2=new Situation();
            s2.setId(SITUATION_ID_2);
            s2.setTimestamp(System.currentTimeMillis()+100);
            s2.setDescription("An error occurred");
            elasticsearchSituationStore.store(s2);
            
            Situation s3=new Situation();
            s3.setId(SITUATION_ID_3);
            s3.setTimestamp(System.currentTimeMillis()+200);
            s3.setDescription("Have a nice day");
            elasticsearchSituationStore.store(s3);
            
            // Need to delay to allow situations to be index, and therefore become searchable
            synchronized (this) {
                wait(2000);                
            }
        } catch (Exception e) {

            fail("Could not store situation " + e);
        }
        
        try {
            SituationsQuery query=new SituationsQuery();
            query.setDescription("error");
            
            java.util.List<Situation> sits = elasticsearchSituationStore.getSituations(query);
            if (sits != null) {
                if (sits.size() != 1) {
                    fail("Expecting 1 situations: "+sits.size());
                }
                
                if (!sits.get(0).getId().equals(SITUATION_ID_2)) {
                    fail("Expecting entry 1 to have id '"+SITUATION_ID_2+"', but got: "+sits.get(0).getId());
                }
           } else {
                fail("Situations list is null");
            }
        } catch (Exception e) {
            fail("Failed to get situation: " + e);

        }
        
        try {
            elasticsearchSituationStore.getClient().remove(SITUATION_ID_1);
            elasticsearchSituationStore.getClient().remove(SITUATION_ID_2);
            elasticsearchSituationStore.getClient().remove(SITUATION_ID_3);
        } catch (Exception e) {
            fail("Could not remove situation" + e);
        }
    }

    @Test
    public void testQuerySituationsSubjectLike() {
        try {
            Situation s1=new Situation();
            s1.setId(SITUATION_ID_1);
            s1.setTimestamp(System.currentTimeMillis());
            elasticsearchSituationStore.store(s1);

            Situation s2=new Situation();
            s2.setId(SITUATION_ID_2);
            s2.setTimestamp(System.currentTimeMillis()+100);
            
            // NOTE: 'Like' only appears to work on whole words, so if OrderService is the subject
            // then a search on Order will not find it.
            s2.setSubject("Order Service");
            elasticsearchSituationStore.store(s2);
            
            Situation s3=new Situation();
            s3.setId(SITUATION_ID_3);
            s3.setTimestamp(System.currentTimeMillis()+200);
            s3.setSubject("InventoryService");
            elasticsearchSituationStore.store(s3);
            
            // Need to delay to allow situations to be index, and therefore become searchable
            synchronized (this) {
                wait(2000);                
            }
        } catch (Exception e) {

            fail("Could not store situation " + e);
        }
        
        try {
            SituationsQuery query=new SituationsQuery();
            query.setSubject("Order");
            
            java.util.List<Situation> sits = elasticsearchSituationStore.getSituations(query);
            if (sits != null) {
                if (sits.size() != 1) {
                    fail("Expecting 1 situations: "+sits.size());
                }
                
                if (!sits.get(0).getId().equals(SITUATION_ID_2)) {
                    fail("Expecting entry 1 to have id '"+SITUATION_ID_2+"', but got: "+sits.get(0).getId());
                }
           } else {
                fail("Situations list is null");
            }
        } catch (Exception e) {
            fail("Failed to get situation: " + e);

        }
        
        try {
            elasticsearchSituationStore.getClient().remove(SITUATION_ID_1);
            elasticsearchSituationStore.getClient().remove(SITUATION_ID_2);
            elasticsearchSituationStore.getClient().remove(SITUATION_ID_3);
        } catch (Exception e) {
            fail("Could not remove situation" + e);
        }
    }

    @Test
    public void testQuerySituationsSubjectExact() {
        try {
            Situation s1=new Situation();
            s1.setId(SITUATION_ID_1);
            s1.setTimestamp(System.currentTimeMillis());
            elasticsearchSituationStore.store(s1);

            Situation s2=new Situation();
            s2.setId(SITUATION_ID_2);
            s2.setTimestamp(System.currentTimeMillis()+100);
            s2.setSubject("OrderService");
            elasticsearchSituationStore.store(s2);
            
            Situation s3=new Situation();
            s3.setId(SITUATION_ID_3);
            s3.setTimestamp(System.currentTimeMillis()+200);
            s3.setSubject("InventoryService");
            elasticsearchSituationStore.store(s3);
            
            // Need to delay to allow situations to be index, and therefore become searchable
            synchronized (this) {
                wait(2000);                
            }
        } catch (Exception e) {

            fail("Could not store situation " + e);
        }
        
        try {
            SituationsQuery query=new SituationsQuery();
            query.setSubject("OrderService");
            
            java.util.List<Situation> sits = elasticsearchSituationStore.getSituations(query);
            if (sits != null) {
                if (sits.size() != 1) {
                    fail("Expecting 1 situations: "+sits.size());
                }
                
                if (!sits.get(0).getId().equals(SITUATION_ID_2)) {
                    fail("Expecting entry 1 to have id '"+SITUATION_ID_2+"', but got: "+sits.get(0).getId());
                }
           } else {
                fail("Situations list is null");
            }
        } catch (Exception e) {
            fail("Failed to get situation: " + e);

        }
        
        try {
            elasticsearchSituationStore.getClient().remove(SITUATION_ID_1);
            elasticsearchSituationStore.getClient().remove(SITUATION_ID_2);
            elasticsearchSituationStore.getClient().remove(SITUATION_ID_3);
        } catch (Exception e) {
            fail("Could not remove situation" + e);
        }
    }

    @Test
    public void testQuerySituationsType() {
        try {
            Situation s1=new Situation();
            s1.setId(SITUATION_ID_1);
            s1.setTimestamp(System.currentTimeMillis());
            elasticsearchSituationStore.store(s1);

            Situation s2=new Situation();
            s2.setId(SITUATION_ID_2);
            s2.setTimestamp(System.currentTimeMillis()+100);
            s2.setType("SLA Violation");
            elasticsearchSituationStore.store(s2);
            
            Situation s3=new Situation();
            s3.setId(SITUATION_ID_3);
            s3.setTimestamp(System.currentTimeMillis()+200);
            s3.setType("Exception");
            elasticsearchSituationStore.store(s3);
            
            // Need to delay to allow situations to be index, and therefore become searchable
            synchronized (this) {
                wait(2000);                
            }
        } catch (Exception e) {

            fail("Could not store situation " + e);
        }
        
        try {
            SituationsQuery query=new SituationsQuery();
            query.setType("SLA");
            
            java.util.List<Situation> sits = elasticsearchSituationStore.getSituations(query);
            if (sits != null) {
                if (sits.size() != 1) {
                    fail("Expecting 1 situations: "+sits.size());
                }
                
                if (!sits.get(0).getId().equals(SITUATION_ID_2)) {
                    fail("Expecting entry 1 to have id '"+SITUATION_ID_2+"', but got: "+sits.get(0).getId());
                }
           } else {
                fail("Situations list is null");
            }
        } catch (Exception e) {
            fail("Failed to get situation: " + e);

        }
        
        try {
            elasticsearchSituationStore.getClient().remove(SITUATION_ID_1);
            elasticsearchSituationStore.getClient().remove(SITUATION_ID_2);
            elasticsearchSituationStore.getClient().remove(SITUATION_ID_3);
        } catch (Exception e) {
            fail("Could not remove situation" + e);
        }
    }

    @Test
    public void testQuerySituationsSeverityHigh() {
        try {
            Situation s1=new Situation();
            s1.setId(SITUATION_ID_1);
            s1.setTimestamp(System.currentTimeMillis());
            elasticsearchSituationStore.store(s1);

            Situation s2=new Situation();
            s2.setId(SITUATION_ID_2);
            s2.setTimestamp(System.currentTimeMillis()+100);
            s2.setSeverity(Severity.High);
            elasticsearchSituationStore.store(s2);
            
            // Need to delay to allow situations to be index, and therefore become searchable
            synchronized (this) {
                wait(2000);                
            }
        } catch (Exception e) {

            fail("Could not store situation " + e);
        }
        
        try {
            SituationsQuery query=new SituationsQuery();
            query.setSeverity(Severity.High);
            
            java.util.List<Situation> sits = elasticsearchSituationStore.getSituations(query);
            if (sits != null) {
                if (sits.size() != 1) {
                    fail("Expecting 1 situations: "+sits.size());
                }
                
                if (!sits.get(0).getId().equals(SITUATION_ID_2)) {
                    fail("Expecting entry 1 to have id '"+SITUATION_ID_2+"', but got: "+sits.get(0).getId());
                }
           } else {
                fail("Situations list is null");
            }
        } catch (Exception e) {
            fail("Failed to get situation: " + e);

        }
        
        try {
            elasticsearchSituationStore.getClient().remove(SITUATION_ID_1);
            elasticsearchSituationStore.getClient().remove(SITUATION_ID_2);
        } catch (Exception e) {
            fail("Could not remove situation" + e);
        }
    }

    @Test
    public void testQuerySituationsTimestampFrom() {
        long from=0;
        
        try {
            Situation s1=new Situation();
            s1.setId(SITUATION_ID_1);
            s1.setTimestamp(System.currentTimeMillis()-10000);
            elasticsearchSituationStore.store(s1);

            Situation s2=new Situation();
            s2.setId(SITUATION_ID_2);
            s2.setTimestamp(System.currentTimeMillis()-5000);
            elasticsearchSituationStore.store(s2);
            
            from = s2.getTimestamp();
            
            // Need to delay to allow situations to be index, and therefore become searchable
            synchronized (this) {
                wait(2000);                
            }
        } catch (Exception e) {

            fail("Could not store situation " + e);
        }
        
        try {
            SituationsQuery query=new SituationsQuery();
            query.setFromTimestamp(from);
            
            java.util.List<Situation> sits = elasticsearchSituationStore.getSituations(query);
            if (sits != null) {
                if (sits.size() != 1) {
                    fail("Expecting 1 situations: "+sits.size());
                }
                
                if (!sits.get(0).getId().equals(SITUATION_ID_2)) {
                    fail("Expecting entry 1 to have id '"+SITUATION_ID_2+"', but got: "+sits.get(0).getId());
                }
           } else {
                fail("Situations list is null");
            }
        } catch (Exception e) {
            fail("Failed to get situation: " + e);

        }
        
        try {
            elasticsearchSituationStore.getClient().remove(SITUATION_ID_1);
            elasticsearchSituationStore.getClient().remove(SITUATION_ID_2);
        } catch (Exception e) {
            fail("Could not remove situation" + e);
        }
    }

    @Test
    public void testQuerySituationsTimestampTo() {
        long to=0;
        
        try {
            Situation s1=new Situation();
            s1.setId(SITUATION_ID_1);
            s1.setTimestamp(System.currentTimeMillis()-10000);
            elasticsearchSituationStore.store(s1);

            Situation s2=new Situation();
            s2.setId(SITUATION_ID_2);
            s2.setTimestamp(System.currentTimeMillis()-5000);
            elasticsearchSituationStore.store(s2);
            
            to = s1.getTimestamp();
            
            // Need to delay to allow situations to be index, and therefore become searchable
            synchronized (this) {
                wait(2000);                
            }
        } catch (Exception e) {

            fail("Could not store situation " + e);
        }
        
        try {
            SituationsQuery query=new SituationsQuery();
            query.setToTimestamp(to);
            
            java.util.List<Situation> sits = elasticsearchSituationStore.getSituations(query);
            if (sits != null) {
                if (sits.size() != 1) {
                    fail("Expecting 1 situations: "+sits.size());
                }
                
                if (!sits.get(0).getId().equals(SITUATION_ID_1)) {
                    fail("Expecting entry 1 to have id '"+SITUATION_ID_1+"', but got: "+sits.get(0).getId());
                }
           } else {
                fail("Situations list is null");
            }
        } catch (Exception e) {
            fail("Failed to get situation: " + e);

        }
        
        try {
            elasticsearchSituationStore.getClient().remove(SITUATION_ID_1);
            elasticsearchSituationStore.getClient().remove(SITUATION_ID_2);
        } catch (Exception e) {
            fail("Could not remove situation" + e);
        }
    }

    @Test
    public void testQuerySituationsTimestampFromTo() {
        long from=0;
        long to=0;
        
        try {
            Situation s1=new Situation();
            s1.setId(SITUATION_ID_1);
            s1.setTimestamp(System.currentTimeMillis()-10000);
            elasticsearchSituationStore.store(s1);

            Situation s2=new Situation();
            s2.setId(SITUATION_ID_2);
            s2.setTimestamp(System.currentTimeMillis()-5000);
            elasticsearchSituationStore.store(s2);
            
            Situation s3=new Situation();
            s3.setId(SITUATION_ID_3);
            s3.setTimestamp(System.currentTimeMillis()-1000);
            elasticsearchSituationStore.store(s3);
            
            from = s1.getTimestamp()+100;
            to = s3.getTimestamp()-100;
            
            // Need to delay to allow situations to be index, and therefore become searchable
            synchronized (this) {
                wait(2000);                
            }
        } catch (Exception e) {

            fail("Could not store situation " + e);
        }
        
        try {
            SituationsQuery query=new SituationsQuery();
            query.setFromTimestamp(from);
            query.setToTimestamp(to);
            
            java.util.List<Situation> sits = elasticsearchSituationStore.getSituations(query);
            if (sits != null) {
                if (sits.size() != 1) {
                    fail("Expecting 1 situations: "+sits.size());
                }
                
                if (!sits.get(0).getId().equals(SITUATION_ID_2)) {
                    fail("Expecting entry 1 to have id '"+SITUATION_ID_2+"', but got: "+sits.get(0).getId());
                }
           } else {
                fail("Situations list is null");
            }
        } catch (Exception e) {
            fail("Failed to get situation: " + e);

        }
        
        try {
            elasticsearchSituationStore.getClient().remove(SITUATION_ID_1);
            elasticsearchSituationStore.getClient().remove(SITUATION_ID_2);
            elasticsearchSituationStore.getClient().remove(SITUATION_ID_3);
        } catch (Exception e) {
            fail("Could not remove situation" + e);
        }
    }

    @Test
    public void assignSituation() throws Exception {
        Situation situation = new Situation();
        situation.setId("assignSituation");
        situation.setTimestamp(System.currentTimeMillis());
        elasticsearchSituationStore.store(situation);
        
        Situation reload = elasticsearchSituationStore.getSituation(situation.getId());
        
        assertEquals(situation.getId(), reload.getId());
        assertFalse(reload.getSituationProperties().containsKey(SituationStore.ASSIGNED_TO_PROPERTY));
        assertFalse(reload.getSituationProperties().containsKey(SituationStore.RESOLUTION_STATE_PROPERTY));
        
        elasticsearchSituationStore.assignSituation(situation.getId(), "junit");
        
        reload = elasticsearchSituationStore.getSituation(situation.getId());
        
        assertEquals("junit",reload.getSituationProperties().get(SituationStore.ASSIGNED_TO_PROPERTY));
        
        elasticsearchSituationStore.getClient().remove(situation.getId());
    }

    @Test
    public void closeSituationAndRemoveAssignment() throws Exception {
        Situation situation = new Situation();
        situation.setId("closeSituationAndRemoveAssignment");
        situation.setTimestamp(System.currentTimeMillis());
        elasticsearchSituationStore.store(situation);
        
        elasticsearchSituationStore.assignSituation(situation.getId(), "junit");
        
        Situation reload = elasticsearchSituationStore.getSituation(situation.getId());
        
        assertEquals("junit",reload.getSituationProperties().get("assignedTo"));
        
        elasticsearchSituationStore.closeSituation(situation.getId());
        
        reload = elasticsearchSituationStore.getSituation(situation.getId());
        
        assertFalse(reload.getSituationProperties().containsKey("assignedTo"));
    }
    
    @Test
    public void deleteSituation() throws Exception {
        Situation situation = new Situation();
        situation.setId("deleteSituation");
        situation.setDescription("deleteSituation");
        situation.setTimestamp(System.currentTimeMillis());
        situation.setSituationProperties(Collections.singletonMap("1", "1"));

        elasticsearchSituationStore.store(situation);
        
        // Changes are not atomic, so need to delay to ensure the search index is updated
        try {
            synchronized(this) {
                wait(2000);
            }
        } catch (Exception e) {
            fail("Failed to wait");
        }
        
        SituationsQuery situationQuery = new SituationsQuery();
        situationQuery.setDescription(situation.getDescription());
        elasticsearchSituationStore.delete(situationQuery);
        
        // Changes are not atomic, so need to delay to ensure the search index is updated
        try {
            synchronized(this) {
                wait(2000);
            }
        } catch (Exception e) {
            fail("Failed to wait");
        }

        List<Situation> situations = elasticsearchSituationStore.getSituations(situationQuery);
        
        assertTrue(situations.isEmpty());
    }
    
    @Test
    public void closeSituationResetOpenResolution() throws Exception {
        Situation situation = new Situation();
        situation.setId("closeSituationResetOpenResolution");
        situation.setTimestamp(System.currentTimeMillis());
        elasticsearchSituationStore.store(situation);
        
        elasticsearchSituationStore.assignSituation(situation.getId(), "junit");
        elasticsearchSituationStore.updateResolutionState(situation.getId(),IN_PROGRESS);
        
        Situation reload = elasticsearchSituationStore.getSituation(situation.getId());
        
        assertEquals("junit",reload.getSituationProperties().get(SituationStore.ASSIGNED_TO_PROPERTY));
        
        elasticsearchSituationStore.closeSituation(situation.getId());
        
        reload = elasticsearchSituationStore.getSituation(situation.getId());
        
        assertFalse(reload.getSituationProperties().containsKey(SituationStore.RESOLUTION_STATE_PROPERTY));
        assertFalse(reload.getSituationProperties().containsKey(SituationStore.ASSIGNED_TO_PROPERTY));
    }
    
    @Test
    public void updateResolutionState() throws Exception {
        Situation situation = new Situation();
        situation.setId("updateResolutionState");
        situation.setTimestamp(System.currentTimeMillis());
        elasticsearchSituationStore.store(situation);
        
        Situation reload = elasticsearchSituationStore.getSituation(situation.getId());
        
        assertFalse(reload.getSituationProperties().containsKey(SituationStore.RESOLUTION_STATE_PROPERTY));
        
        elasticsearchSituationStore.updateResolutionState(situation.getId(),ResolutionState.IN_PROGRESS);
        
        reload = elasticsearchSituationStore.getSituation(situation.getId());
        
        assertEquals(ResolutionState.IN_PROGRESS.name(), reload.getSituationProperties().get(SituationStore.RESOLUTION_STATE_PROPERTY));
    }
    
    @Test
    public void recordResubmit() throws Exception {
        Situation situation = new Situation();
        situation.setId("recordResubmit");
        situation.setTimestamp(System.currentTimeMillis());
        elasticsearchSituationStore.store(situation);
        
        elasticsearchSituationStore.recordSuccessfulResubmit(situation.getId(), "recordResubmit");
        
        Situation reload = elasticsearchSituationStore.getSituation(situation.getId());
        
        assertEquals("recordResubmit", reload.getSituationProperties().get(SituationStore.RESUBMIT_BY_PROPERTY));
        assertEquals(SituationStore.RESUBMIT_RESULT_SUCCESS, reload.getSituationProperties().get(SituationStore.RESUBMIT_RESULT_PROPERTY));
        assertTrue(reload.getSituationProperties().containsKey(SituationStore.RESUBMIT_AT_PROPERTY));
        assertFalse(reload.getSituationProperties().containsKey(SituationStore.RESUBMIT_ERROR_MESSAGE));
    }

    @Test
    public void recordResubmitFailure() throws Exception {
        String name="recordResubmitFailure";
        
        Situation situation = new Situation();
        situation.setId(name);
        situation.setTimestamp(System.currentTimeMillis());
        elasticsearchSituationStore.store(situation);
        elasticsearchSituationStore.recordResubmitFailure(situation.getId(), name, name);
        Situation reload = elasticsearchSituationStore.getSituation(situation.getId());
        assertEquals(name, reload.getSituationProperties().get(SituationStore.RESUBMIT_BY_PROPERTY));
        assertEquals(name, reload.getSituationProperties().get(SituationStore.RESUBMIT_ERROR_MESSAGE));
        assertTrue(reload.getSituationProperties().containsKey(SituationStore.RESUBMIT_AT_PROPERTY));
        assertEquals(SituationStore.RESUBMIT_RESULT_ERROR,
                reload.getSituationProperties().get(SituationStore.RESUBMIT_RESULT_PROPERTY));
    }

    @Test
    public void recordResubmitErrorMessageMaxLength() throws Exception {
        String name="recordResubmitErrorMessageMaxLength";
        
        Situation situation = new Situation();
        situation.setId(name);
        situation.setTimestamp(System.currentTimeMillis());
        elasticsearchSituationStore.store(situation);
        
        elasticsearchSituationStore.recordResubmitFailure(situation.getId(),
                Strings.padEnd(name, 10000, '*'), name);
        
        Situation reload = elasticsearchSituationStore.getSituation(situation.getId());
        
        assertEquals(name, reload.getSituationProperties().get(SituationStore.RESUBMIT_BY_PROPERTY));
        
        String errorMessage = reload.getSituationProperties().get(SituationStore.RESUBMIT_ERROR_MESSAGE);
        
        assertEquals(Strings.padEnd(name, 250, '*'), errorMessage);
        assertTrue(reload.getSituationProperties().containsKey(SituationStore.RESUBMIT_AT_PROPERTY));
        assertEquals(SituationStore.RESUBMIT_RESULT_ERROR,
                reload.getSituationProperties().get(SituationStore.RESUBMIT_RESULT_PROPERTY));
    }
}
