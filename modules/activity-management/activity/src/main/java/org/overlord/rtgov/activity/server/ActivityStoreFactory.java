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

import org.overlord.rtgov.common.util.RTGovProperties;

/**
 * This class represents a CDI factory for obtaining an activity store implementation.
 *
 */
public final class ActivityStoreFactory {

    private static final Logger LOG=Logger.getLogger(ActivityStoreFactory.class.getName());
    
    private static final String ACTIVITY_STORE_CLASS="ActivityStore.class";
    
    private static ActivityStore _instance;
    
    /**
     * Private constructor.
     */
    private ActivityStoreFactory() {
    }
    
    /**
     * This method will initialize the activity store factory to use the
     * supplied activity store, if not already initialized with a value.
     * 
     * @param actStore The activity store
     */
    public static synchronized void initialize(ActivityStore actStore) {
        if (_instance == null) {
            if (LOG.isLoggable(Level.FINER)) {
                LOG.finer("Activity store initialized="+actStore);
            }
            
            _instance = actStore;
        }
    }
    
    /**
     * This method returns an instance of the ActivityStore interface.
     * 
     * @return The activity store
     */
    public static synchronized ActivityStore getActivityStore() {
        if (_instance == null) {
            try {
                String clsName=(String)RTGovProperties.getProperties().get(ACTIVITY_STORE_CLASS);
                
                if (clsName != null) {
                    try {
                        @SuppressWarnings("unchecked")
                        Class<ActivityStore> cls=(Class<ActivityStore>)
                                Thread.currentThread().getContextClassLoader().loadClass(clsName);
                        
                        _instance = (ActivityStore)cls.newInstance();
                    } catch (Throwable t) {
                        LOG.log(Level.FINEST, "Failed to find activity store class '"+clsName+"'", t);
                    }
                }
            } catch (Throwable t) {
                LOG.log(Level.FINEST, "Failed to get activity store class property", t);
            }
        }
        
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("Activity store instance="+_instance);
        }
        
        return (_instance);
    }
}
