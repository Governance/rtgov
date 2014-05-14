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
package org.overlord.rtgov.analytics.situation.store;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.overlord.rtgov.common.util.RTGovProperties;

/**
 * This class represents a CDI factory for obtaining a situation store implementation.
 *
 */
public final class SituationStoreFactory {

    private static final Logger LOG=Logger.getLogger(SituationStoreFactory.class.getName());
    
    private static final String SITUATION_STORE_CLASS="SituationStore.class";
    
    private static SituationStore _instance;
    
    /**
     * Private constructor.
     */
    private SituationStoreFactory() {
    }
    
    /**
     * This method will initialize the situation store factory to use the
     * supplied situation store, if not already initialized with a value.
     * 
     * @param sitStore The situation store
     */
    public static synchronized void initialize(SituationStore sitStore) {
        if (_instance == null) {
            if (LOG.isLoggable(Level.FINER)) {
                LOG.finer("Situation store initialized="+sitStore);
            }
            
            _instance = sitStore;
        }
    }
    
    /**
     * This method returns an instance of the SituationStore interface.
     * 
     * @return The situation store
     */
    public static synchronized SituationStore getSituationStore() {
        if (_instance == null) {
            try {
                String clsName=(String)RTGovProperties.getProperties().get(SITUATION_STORE_CLASS);
                
                if (clsName != null) {
                    try {
                        @SuppressWarnings("unchecked")
                        Class<SituationStore> cls=(Class<SituationStore>)
                                Thread.currentThread().getContextClassLoader().loadClass(clsName);
                        
                        _instance = (SituationStore)cls.newInstance();
                    } catch (Throwable t) {
                        LOG.log(Level.FINEST, "Failed to find situation store class '"+clsName+"'", t);
                    }
                }
            } catch (Throwable t) {
                LOG.log(Level.FINEST, "Failed to get situation store class property", t);
            }
        }
        
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("Situation store instance="+_instance);
        }
        
        return (_instance);
    }
}
