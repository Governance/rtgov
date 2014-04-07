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
package org.overlord.rtgov.elasticsearch.rest.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;

import org.overlord.rtgov.elasticsearch.rest.ElasticSearchRESTServer;

/**
 * This class represents the activator for the ElasticSearch REST server.
 *
 */
public class ElasticSearchRESTServerActivator implements BundleActivator {
    
    private ElasticSearchRESTServer _elasticSearchServer=null;

    /**
     * {@inheritDoc}
     */
    public void start(final BundleContext context) throws Exception {
        
        ServiceReference sRef = context.getServiceReference(HttpService.class.getName());

        if (sRef != null) {
            HttpService service = (HttpService)context.getService(sRef);
            
            _elasticSearchServer = new ElasticSearchRESTServer();
            
            service.registerServlet("/overlord-rtgov-elasticsearch", _elasticSearchServer, null, null);
        } else {
            throw new Exception("HttpService reference was not found");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) throws Exception {
        
    }
}
