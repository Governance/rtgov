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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.overlord.rtgov.analytics.situation.store.SituationStore;
import org.overlord.rtgov.analytics.situation.store.SituationStoreFactory;

/**
 * This class provides the activator capability for the analytics bundle.
 *
 */
public class Activator implements BundleActivator {
    
    private static final Logger LOG=Logger.getLogger(Activator.class.getName());

    /**
     * {@inheritDoc}
     */
    public void start(final BundleContext context) throws Exception {
        ServiceReference actStoreRef=context.getServiceReference(SituationStore.class.getName());
        
        if (actStoreRef != null) {
            register(context, actStoreRef);
        } else {        
            ServiceListener sl = new ServiceListener() {
                public void serviceChanged(ServiceEvent ev) {
                    ServiceReference sr = ev.getServiceReference();
                    switch(ev.getType()) {
                    case ServiceEvent.REGISTERED:
                        register(context, sr);
                        break;
                    default:
                        break;
                    }
                }           
            };
            
            String filter = "(objectclass=" + SituationStore.class.getName() + ")";
            try {
                context.addServiceListener(sl, filter);
            } catch (InvalidSyntaxException e) { 
                LOG.log(Level.SEVERE, "Failed to add service listener for situation store", e);
            }
        }
    }
    
    /**
     * This method registers the situation store associated with the
     * supplied service reference.
     * 
     * @param context The context
     * @param actStoreRef The service ref
     */
    protected void register(final BundleContext context, ServiceReference actStoreRef) {
        SituationStore sitStore=(SituationStore)context.getService(actStoreRef);            
        SituationStoreFactory.initialize(sitStore);
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) throws Exception {
    }

}
