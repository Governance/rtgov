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

/**
 * This class provides an abstract base class for ACS loaders.
 *
 */
public abstract class AbstractACSLoader {

    /**
     * This method pre-initializes the Active Collection Source
     * before it is registered with the manager. This is sometimes
     * required if the loader and manager are associated with different
     * contextual classloaders.
     * 
     * @param acs The active collection source to pre-initialize
     * @throws Exception Failed to pre-initialize source
     */
    protected void preInit(ActiveCollectionSource acs) throws Exception {
        acs.preInit();
    }
}
