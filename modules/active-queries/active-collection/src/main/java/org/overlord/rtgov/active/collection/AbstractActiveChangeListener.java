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
package org.overlord.rtgov.active.collection;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * This class defines an abstract active change listener. The
 * main purpose of this class is to enable derived active change
 * listener implementations to be used within an ActiveCollectionSource
 * by providing their class information when serialized using json.
 *
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class AbstractActiveChangeListener implements ActiveChangeListener {

    /**
     * This method pre-initializes the active change listener
     * in situations where it needs to be initialized before
     * registration with the collection. This may be required
     * where the registration is performed in a different
     * contextual classloader.
     * 
     * @throws Exception Failed to pre-initialize
     */
    protected void preInit() throws Exception {
    }
    
    /**
     * This method initializes the active change listener.
     * 
     * @throws Exception Failed to initialize
     */
    public void init() throws Exception {
        preInit();
    }
    
    /**
     * This method closes the active change listener.
     * 
     * @throws Exception Failed to close
     */
    public void close() throws Exception {
    }
    
}
