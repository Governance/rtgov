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

/**
 * This interface represents an Elasticsearch node.
 * 
 * User: imk@redhat.com
 * Date: 21/06/14
 * Time: 23:41
 */
public interface ElasticsearchNode  {
    
    /**
     * This method returns the client for communicating with the Elasticsearch
     * node.
     * 
     * @return Gets the client
     */
    public Client getClient();

}
