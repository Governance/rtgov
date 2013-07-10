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
package org.overlord.rtgov.active.collection.embedded;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.overlord.rtgov.active.collection.ActiveCollectionManager;
import org.overlord.rtgov.active.collection.AbstractActiveCollectionManager;


/**
 * This class provides the embedded implementation of the ActiveCollectionManager
 * interface.
 *
 */
public class EmbeddedActiveCollectionManager extends AbstractActiveCollectionManager
                    implements ActiveCollectionManager {
    
    /**
     * This method initializes the Active Collection Manager.
     */
    @PostConstruct
    public void init() {
       super.init();
    }
    
    /**
     * This method closes the Active Collection Manager.
     */
    @PreDestroy
    public void close() {
        super.close();
    }
    
}
