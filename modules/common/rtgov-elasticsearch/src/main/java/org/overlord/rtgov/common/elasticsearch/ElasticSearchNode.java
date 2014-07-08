package org.overlord.rtgov.common.elasticsearch;
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

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.overlord.rtgov.common.util.RTGovProperties;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Simple singleton.
 * User: imk@redhat.com
 * Date: 21/06/14
 * Time: 23:41
 */
//@Singleton
public enum ElasticSearchNode  {
    /**
     * Elastic search node. configurable via rtgov-es.properties
     */
    NODE;


    /**
     * location of elasticsearch property file
     */
    private static final String ELASTICSEARCH_CONFIG = "elasticsearch.config";

    /**
     * DEFAULT configuration of elastic search bundled with the deployment
     */
    private static final String ELASTICSEARCH_CONFIG_DEFAULT = "rtgov-es.properties";


    /**
     *
     *
     */
    private Client _client;
    /**
     *
     */
    private Node _node;

    private String getPropertyFileName() throws FileNotFoundException {
        String filelocation = RTGovProperties.getProperty(ELASTICSEARCH_CONFIG, ELASTICSEARCH_CONFIG_DEFAULT);
        File io = new File(filelocation);
        /**
         * check if passed property was loaded
         */
        if (io.exists() && !io.isDirectory()) {
            return filelocation;
        } else {
            Logger.getLogger(ElasticSearchNode.class.getName()).warning("Cannot start ES server. Could not locate elasticsearch config file[" + ELASTICSEARCH_CONFIG + " = " + filelocation + "], trying default location " + ELASTICSEARCH_CONFIG_DEFAULT + "");
            // return the default file bundled.

                return ELASTICSEARCH_CONFIG_DEFAULT;

        }


    }

    //  @PostConstruct
    private ElasticSearchNode()  {
        /**
         * quick fix for integration tests. if hosts property set to "embedded" then a local node is start.
         * maven dependencies need to be defined correctly for this to work
         */
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String property =
                System.getProperty(ELASTICSEARCH_CONFIG);


        try {
            String filelocation = getPropertyFileName();
            // Need to use the classloader for Elasticsearch to pick up the property files when
            // running in an OSGi environment
            Thread.currentThread().setContextClassLoader(TransportClient.class.getClassLoader());
            // partaking in a cluster.
            Properties defaultProps = new Properties();

            defaultProps  = new ElasticSearchPropertyProvider().getProperties();


            _node = NodeBuilder.nodeBuilder()
                    .settings(
                            ImmutableSettings.settingsBuilder()
                                    .put(defaultProps)
                    ).node();

            _client = _node.client();

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Could not start ES server node, "+e.getMessage(), e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not start ES server node, "+e.getMessage(), e);
        } catch (Throwable e) {
            System.out.println("bsdbdfsbfds");
        } finally {
            Thread.currentThread().setContextClassLoader(cl);
        }


    }

    /**
     * @return gets the client
     */
    public Client getClient() {

        return _client;
    }
}
