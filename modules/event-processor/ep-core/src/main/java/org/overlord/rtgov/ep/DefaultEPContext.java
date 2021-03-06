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
package org.overlord.rtgov.ep;

import org.overlord.rtgov.common.service.Service;

/**
 * This class provides services to the EventProcessor
 * implementations that process the events.
 *
 */
public class DefaultEPContext extends org.overlord.rtgov.internal.ep.DefaultEPContext {

    /**
     * The default constructor.
     */
    public DefaultEPContext() {
    }
    
    /**
     * This constructor initializes the service map.
     * 
     * @param services The map of services available
     */
    public DefaultEPContext(java.util.Map<String,Service> services) {
        super(services);
    }
    
}
