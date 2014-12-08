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
package org.overlord.rtgov.analytics.situation.store.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.overlord.rtgov.analytics.situation.store.SituationStore;
import org.overlord.rtgov.analytics.situation.store.SituationStoreFactory;
import org.overlord.rtgov.common.registry.ServiceRegistryUtil;

/**
 * This class provides the activator capability for the analytics bundle.
 *
 */
public class Activator implements BundleActivator {
    
    private org.overlord.rtgov.common.registry.ServiceListener<SituationStore> _listener;
    
    /**
     * {@inheritDoc}
     */
    public void start(final BundleContext context) throws Exception {
        _listener = new org.overlord.rtgov.common.registry.ServiceListener<SituationStore>() {

            @Override
            public void registered(SituationStore service) {
                //SituationStoreFactory.initialize(service);
            }

            @Override
            public void unregistered(SituationStore service) {
                SituationStoreFactory.clear();
            }
            
        };
        
        ServiceRegistryUtil.addServiceListener(SituationStore.class, _listener);
    }
    
    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) throws Exception {
        /*
        if (_listener != null) {
            ServiceRegistryUtil.removeServiceListener(_listener);
        }
        */
    }

}
