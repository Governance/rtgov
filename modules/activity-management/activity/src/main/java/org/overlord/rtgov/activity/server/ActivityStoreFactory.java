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
package org.overlord.rtgov.activity.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.overlord.commons.services.ServiceRegistryUtil;
import org.overlord.rtgov.common.util.RTGovProperties;

/**
 * This class represents a CDI factory for obtaining an activity store implementation.
 *
 */
public final class ActivityStoreFactory {

    private static final Logger LOG=Logger.getLogger(ActivityStoreFactory.class.getName());
    
    /**
     * Property defining the activity store implementation class.
     */
    public static final String ACTIVITY_STORE_CLASS="ActivityStore.class";
    
    private static ActivityStore _instance;
    
    /**
     * Private constructor.
     */
    private ActivityStoreFactory() {
    }
    
    /**
     * This method resets the factory.
     */
    public static void clear() {
        _instance = null;
    }
    
    /**
     * This method returns an instance of the ActivityStore interface.
     * 
     * @return The activity store
     */
    public static ActivityStore getActivityStore() {
        if (_instance == null) {
            java.util.Set<ActivityStore> services=ServiceRegistryUtil.getServices(ActivityStore.class);
            String clsName=(String)RTGovProperties.getProperties().get(ACTIVITY_STORE_CLASS);
            
            for (ActivityStore as : services) {
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("Checking activity store impl="+as);
                }
                if (as.getClass().getName().equals(clsName)) {                    
                    // Only overwrite if instance not set
                    if (_instance == null) {
                        _instance = as;                        
                        if (LOG.isLoggable(Level.FINEST)) {
                            LOG.finest("Found activity store impl="+as);
                        }
                    }
                    break;
                }
            }
        }
        
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("Activity store instance="+_instance);
        }
        
        return (_instance);
    }
}
