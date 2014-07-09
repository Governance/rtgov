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

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import java.util.Properties;

/**
 * This singleton class represents an Elasticsearch node.
 * 
 * User: imk@redhat.com
 * Date: 21/06/14
 * Time: 23:41
 */
public final class ElasticsearchNode  {

    private Client _client;
    private Node _node;

    private static ElasticsearchNode INSTANCE=null;
    
    /**
     * The default constructor.
     */
    private ElasticsearchNode()  {
        /**
         * quick fix for integration tests. if hosts property set to "embedded" then a local node is start.
         * maven dependencies need to be defined correctly for this to work
         */
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        try {
            // Need to use the classloader for Elasticsearch to pick up the property files when
            // running in an OSGi environment            
            Thread.currentThread().setContextClassLoader(TransportClient.class.getClassLoader());
            
            Properties defaultProps=new ElasticsearchPropertyProvider().getProperties();

            _node = NodeBuilder.nodeBuilder()
                    .settings(ImmutableSettings.settingsBuilder()
                    .put(defaultProps)).node();

            _client = _node.client();
        } finally {
            Thread.currentThread().setContextClassLoader(cl);
        }
    }
    
    /**
     * This method returns the singleton Elasticsearch node.
     * 
     * @return The singleton Elasticsearch node
     */
    public static synchronized ElasticsearchNode getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ElasticsearchNode();
        }
        
        return (INSTANCE);
    }

    /**
     * This method returns the client for communicating with the Elasticsearch
     * node.
     * 
     * @return Gets the client
     */
    public Client getClient() {
        return _client;
    }
}
