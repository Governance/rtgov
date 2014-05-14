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
     * This method initializes the activity store. If a class name has been
     * defined in configuration, then the supplied class must match the type,
     * otherwise it will be ignored. This method is primarily used for testing,
     * or where classloading is not possible (i.e. OSGi).
     * 
     * @param store The store
     */
    public static synchronized void initialize(ActivityStore store) {
        // Only initialize if no instance available
        if (_instance != null) {
            return;
        }
        
        String clsName=(String)RTGovProperties.getProperties().get(ACTIVITY_STORE_CLASS);
        
        // Verify the instance is of the correct class
        if (clsName == null || store.getClass().getName().equals(clsName)) {
            
            if (LOG.isLoggable(Level.FINER)) {
                LOG.finer("Initialize activity store instance="+_instance);
            }
            
            _instance = store;
            
        } else if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("Ignoring activity store initialization due to incorrect type ["
                            +store.getClass().getName()+"], expecting ["
                            +clsName+"]");
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
