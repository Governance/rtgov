package org.overlord.rtgov.activity.store.elasticsearch;
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

/**
 * User: imk@redhat.com
 * Date: 22/04/14
 * Time: 22:10
 */

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.NodeBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.activity.model.Origin;
import org.overlord.rtgov.activity.model.soa.RequestSent;
import org.overlord.rtgov.activity.model.soa.ResponseReceived;
import org.overlord.rtgov.activity.server.QuerySpec;
import org.overlord.rtgov.common.util.RTGovProperties;
import org.overlord.rtgov.common.util.RTGovPropertiesProvider;

import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static org.junit.Assert.fail;

public class ElasticsearchActivityStoreTest {
    private static final String ENDPOINT_ID_1 = "abc123";
    private static final String ENDPOINT_ID_2 = "abc456";
    private static final String AU_ID_2 = "au2";
    private static final String AU_ID_1 = "au1";
    private static final String CONV_ID_2 = "2";
    private static final String CONV_ID_1 = "1";
    private static ElasticsearchActivityStore elasticsearchActivityStore;

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
    private static String type = "activity";

    public static class TestPropertiesProvider implements RTGovPropertiesProvider {

        private java.util.Properties _properties = new java.util.Properties();

        public TestPropertiesProvider() {
            _properties = new Properties();
            _properties.setProperty("Elasticsearch.hosts", host + ":" + 9300);
            //_properties.setProperty("Elasticsearch.hosts", host);
            _properties.setProperty("Elasticsearch.schedule", "3000");
            _properties.setProperty("ActivityStore.Elasticsearch.type", type);
            _properties.setProperty("ActivityStore.Elasticsearch.index", index);


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
    public static void initialiseEntityManager() throws Exception {
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

        elasticsearchActivityStore = new ElasticsearchActivityStore();
        elasticsearchActivityStore.setIndex("rtgovtest");
        elasticsearchActivityStore.setType("activity");
        elasticsearchActivityStore.init();


    }


    @Test
    public void testAdd() {

        try {
            //elasticsearchActivityStore.add(_id, createTestActivityUnit(_id, _conversation, ENDPOINT_ID_1, 0));
        } catch (Exception e) {
            fail("Could not store Add activity unit " + e);
            e.printStackTrace();
        }
    }

    public void testStoreAndGetActivityUnit() {

        try {
            elasticsearchActivityStore.add(AU_ID_1, createTestActivityUnit(AU_ID_1, CONV_ID_1, ENDPOINT_ID_1, 0));
        } catch (Exception e) {

            fail("Could not store Add activity unit " + e);
        }
        try {
            ActivityUnit au = elasticsearchActivityStore.getActivityUnit(AU_ID_1);
            if (au != null) {
                if (!au.getId().equals(AU_ID_1))
                    fail("Activity unit reterive does not match activity unit stored. Could not get AU");

            } else
                fail("Activity unit is null. Could not get AU");

        } catch (Exception e) {
            fail("Could not  get activity unit " + e);

        }
        try {
            elasticsearchActivityStore.remove(AU_ID_1);
        } catch (Exception e) {
            fail("Could not remove activity unit " + e);
        }
    }


    /**
     * ****Works*******

     {
     "query" : {
     "nested" : {
     "path" : "activityTypes",
     "query" : {
     "match" : {
     "activityTypes.type" : "RequestSent"
     }
     }
     }
     }}
     */


    /**
     * ****Works*******
     * <p/>
     * {
     * "query" : {
     * "nested" : {
     * "path" : "activityTypes",
     * "query" : {
     * "match" : {
     * "activityTypes.type" : "RequestSent"
     * }
     * }
     * }
     * }}
     *
     * @return
     */
    @Test
    public void testStoreAndQuery() {
        java.util.List<ActivityType> results = null;

        java.util.List<ActivityUnit> activities = new java.util.ArrayList<ActivityUnit>();

        ActivityUnit au1 = createTestActivityUnit(AU_ID_1, CONV_ID_1, ENDPOINT_ID_1, 0);
        ActivityUnit au2 = createTestActivityUnit(AU_ID_2, CONV_ID_2, ENDPOINT_ID_2, 5000);

        activities.add(au1);
        activities.add(au2);

        try {
            elasticsearchActivityStore.store(activities);
        } catch (Exception e) {
             fail("Failed to store activities: " + e.getMessage() + ", ");
        }
        try {
            ActivityUnit au1r = elasticsearchActivityStore.getActivityUnit(AU_ID_1);
            ActivityUnit au2r = elasticsearchActivityStore.getActivityUnit(AU_ID_2);
            if (au1r != null) {
                if (!au1r.getId().equals(AU_ID_1))
                    fail("Activity unit reterive does not match activity unit stored. Could not get AU");

            } else
                fail("Activity unit is null. Could not get AU");
            if (au2r != null) {
                if (!au2r.getId().equals(AU_ID_2))
                    fail("Activity unit reterive does not match activity unit stored. Could not get AU");

            } else
                fail("Activity unit is null. Could not get AU");
        } catch (Exception e) {
            fail("Could not  get activity unit " + e);

        }
        try {
            elasticsearchActivityStore.remove(AU_ID_1);
            elasticsearchActivityStore.remove(AU_ID_2);
        } catch (Exception e) {
            fail("Could not remove activity unit " + e);
        }
    }

    @Test
    public void testGetActivityTypes() {
        java.util.List<ActivityType> results1 = null;
        java.util.List<ActivityType> results2 = null;

        java.util.List<ActivityUnit> activities = new java.util.ArrayList<ActivityUnit>();


        ActivityUnit au1 = createTestActivityUnit("7", "C1", "E1", 0);
        ActivityUnit au2 = createTestActivityUnit("8", "C1", "E1", 5000);

        activities.add(au1);
        activities.add(au2);
        try {
            //store both ATs
            elasticsearchActivityStore.store(activities);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Could not  store activity units " + e);

        }

        Context context1 = new Context();
        context1.setType(Context.Type.Conversation);
        context1.setValue("C1");
        List<ActivityType> listAT = null;
        try {
            results1 = elasticsearchActivityStore.getActivityTypes(context1);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Could not  get activity units " + e);

        }
        Context context2 = new Context();
        context2.setType(Context.Type.Conversation);
        context2.setValue("C1");
        try {
            results2 = elasticsearchActivityStore.getActivityTypes(context2, 2500, 7500);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Could not  get activity units " + e);

        }
        if (results1 == null) {
            fail("Results1 is null");
        }

        if (results1.size() != 2) {
            fail("Expecting 2 results: " + results1.size());
        }

        if (results2 == null) {
            fail("Results2 is null");
        }

        if (results2.size() != 1) {
            fail("Expecting 1 result: " + results2.size());
        }

        if (!results2.get(0).getUnitId().equals("8")) {
            fail("Expecting au 8: " + results2.get(0).getUnitId());
        }
        try {
            elasticsearchActivityStore.remove("7");
            elasticsearchActivityStore.remove("8");
        } catch (Exception e) {
            fail("Could not remove activity unit " + e);
        }
    }

    protected ActivityUnit createTestActivityUnit(String id, String convId, String endpointId, long baseTime) {
        ActivityUnit act = new ActivityUnit();

        act.setId(id);

        Origin origin = new Origin();
        origin.setHost("MyHost");
        origin.setNode("MyNode");
        origin.setPrincipal("Me");
        origin.setThread("MyThread");
        act.setOrigin(origin);

        RequestSent me1 = new RequestSent();
        me1.setUnitId(id);
        me1.setUnitIndex(0);

        me1.setTimestamp(baseTime + 1000);
        me1.setContent("<tns:Order xmlns:tns=\"http://www.savara.org\" amount=\"100\" />");
        me1.setMessageType("{http://message}Order");
        me1.setOperation("myOp");
        me1.setServiceType("{http://service}OrderService");
        me1.setMessageId("corr1");
        me1.getProperties().put("customer", "Fred");
        me1.getProperties().put("trader", "Joe");
        me1.getProperties().put("sss", "Joe");
        me1.getProperties().put("sss", "Joe");
        if(baseTime==0)
            me1.getProperties().put("cccc","ysdasdasdda");
        Context c1 = new Context();
        c1.setType(Context.Type.Conversation);
        c1.setValue(convId);
        me1.getContext().add(c1);

        act.getActivityTypes().add(me1);

        ResponseReceived me2 = new ResponseReceived();
        me2.setUnitId(id);
        me2.setUnitIndex(1);

        me2.setTimestamp(baseTime + 2000);
        me2.setContent("<tns:Confirmation xmlns:tns=\"http://www.savara.org\" amount=\"100\" />");
        me2.setFault("MyFault");
        me2.setMessageType("{http://message}Confirmation");
        me2.setOperation("myOp");
        me2.setServiceType("{http://service}OrderService");
        me2.setMessageId("corr2");
        me2.setReplyToId("corr1");
        me2.getProperties().put("customer", "Fred");
        me2.getProperties().put("manager", "Jane");

        Context c2 = new Context();
        c2.setType(Context.Type.Endpoint);
        c2.setValue(endpointId);
        me2.getContext().add(c2);

        act.getActivityTypes().add(me2);

        return (act);
    }

    /**
     * This method generates a random string.
     *
     * @return The random string
     */
    protected String getRandom() {
        return (UUID.randomUUID().toString());
    }

}
