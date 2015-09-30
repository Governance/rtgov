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
package org.overlord.rtgov.common.elasticsearch.test;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.overlord.rtgov.common.elasticsearch.ElasticsearchClient;
import org.overlord.rtgov.common.util.RTGovProperties;
import org.overlord.rtgov.common.util.RTGovPropertiesProvider;

public class RemoteConnectionTest {
    /**
     * elastic cluster name to test against
     */
    private static final String CLUSTER_NAME = "situationStoreTest";

    /**
     * elastic search index to test against
     */
    private static String INDEX = "rtgovtest";
    
    /**
     * elastich search host
     */
    private static String HOST = "127.0.0.1";
    
    /**
     * elasticsearch port
     */
    private static int PORT = 9300;
    
    /**
     * elasticsearch type to test
     */
    private static String TYPE = "situation";

    /**
     * Starts a JVM-local elasticsearch server with non-default cluster name and uses ElasticsearchClient to connect to it
     * @throws Exception
     */
    @Test
    public void testElasticSearchConnection() throws Exception {
        System.setProperty("elasticsearch.config", "elasticsearch.properties");

        Properties serverProperties = new java.util.Properties();
        serverProperties.setProperty("Elasticsearch.hosts", "embedded" + ":" + PORT);
        serverProperties.setProperty("Elasticsearch.schedule", "3000");
        serverProperties.setProperty("SituationStore.Elasticsearch.type", TYPE);
        serverProperties.setProperty("SituationStore.Elasticsearch.index", INDEX);
        serverProperties.setProperty("elasticsearch.config", "elasticsearch.properties");

        TestPropertiesProvider serverProvider = new TestPropertiesProvider(serverProperties);
        
        Properties clientProperties = new java.util.Properties();
        clientProperties.setProperty("Elasticsearch.hosts", HOST + ":" + PORT);
        clientProperties.setProperty("Elasticsearch.schedule", "3000");
        clientProperties.setProperty("Elasticsearch.clusterName", CLUSTER_NAME);

        TestPropertiesProvider clientProvider = new TestPropertiesProvider(clientProperties);
        
        RTGovProperties.setPropertiesProvider(serverProvider);
        ElasticsearchClient serverClient = new ElasticsearchClient();

        serverClient.setIndex(INDEX);
        serverClient.setType(TYPE);
        serverClient.init();

        RTGovProperties.setPropertiesProvider(clientProvider);
        ElasticsearchClient client = new ElasticsearchClient();

        client.setIndex(INDEX);
        client.setType(TYPE);
        client.init();
        
        // Just a simple get to test if the connection works
        Assert.assertNull(client.get("test"));
    }
    
    public static class TestPropertiesProvider implements RTGovPropertiesProvider {

        private java.util.Properties _properties = new java.util.Properties();

        public TestPropertiesProvider(Properties properties) {
            this._properties = properties;
        }

        public String getProperty(String name) {
            return _properties.getProperty(name);
        }

        public Properties getProperties() {
            return _properties;
        }
    }
}