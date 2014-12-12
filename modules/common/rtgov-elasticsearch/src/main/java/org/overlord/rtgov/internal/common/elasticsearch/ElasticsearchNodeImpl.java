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
package org.overlord.rtgov.internal.common.elasticsearch;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.overlord.commons.services.ServiceClose;
import org.overlord.commons.services.ServiceInit;
import org.overlord.rtgov.common.elasticsearch.ElasticsearchNode;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This singleton class represents an Elasticsearch node.
 * 
 * User: imk@redhat.com
 * Date: 21/06/14
 * Time: 23:41
 */
public final class ElasticsearchNodeImpl implements ElasticsearchNode {
    
    private static final Logger LOG=Logger.getLogger(ElasticsearchNodeImpl.class.getName());

    private Client _client;
    private Node _node;
    
    /**
     * The default constructor.
     */
    public ElasticsearchNodeImpl()  {
    }
    
    /**
     * This method initializes the elasticsearch node.
     */
    @ServiceInit
    public void init() {
    }
    
    /**
     * This method initializes the node.
     */
    protected void initNode() {
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

            _node.start(); 
            _client = _node.client();
        } finally {
            Thread.currentThread().setContextClassLoader(cl);
        }
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Initialized Elasticsearch node="+_node+" client="+_client);
        }
    }
    
    /**
     * This method closes the elasticsearch node.
     */
    @ServiceClose
    public void close() {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Close Elasticsearch node="+_node+" client="+_client);
        }

        if (_client != null) {
            _client.close();
            _client = null;
        }
        
        if (_node != null) {
            _node.stop();
            _node = null;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public synchronized Client getClient() {
        if (_client == null) {
            initNode();
        }
        return _client;
    }
}
