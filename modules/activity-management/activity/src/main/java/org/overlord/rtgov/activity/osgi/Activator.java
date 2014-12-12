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
package org.overlord.rtgov.activity.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.overlord.commons.services.ServiceRegistryUtil;
import org.overlord.rtgov.activity.server.ActivityStore;
import org.overlord.rtgov.activity.server.ActivityStoreFactory;

/**
 * This class provides the activator capability for the analytics bundle.
 *
 */
public class Activator implements BundleActivator {
    
    private org.overlord.commons.services.ServiceListener<ActivityStore> _listener;
    
    /**
     * {@inheritDoc}
     */
    public void start(final BundleContext context) throws Exception {
        _listener = new org.overlord.commons.services.ServiceListener<ActivityStore>() {

            @Override
            public void registered(ActivityStore service) {
                //ActivityStoreFactory.initialize(service);
            }

            @Override
            public void unregistered(ActivityStore service) {
                ActivityStoreFactory.clear();
            }
            
        };
        
        ServiceRegistryUtil.addServiceListener(ActivityStore.class, _listener);        
    }
    
    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) throws Exception {
        if (_listener != null) {
            ServiceRegistryUtil.removeServiceListener(_listener);
        }
    }

}
