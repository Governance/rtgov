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
package org.overlord.rtgov.jee;

import org.overlord.rtgov.active.collection.ActiveList;
import org.overlord.rtgov.active.collection.ActiveMap;

/**
 * This interface provides access to active collections.
 *
 */
public interface CollectionManager {

    /**
     * This method returns the active list associated with the
     * supplied name.
     * 
     * @param name The name
     * @return The list, or null if not found
     */
    public ActiveList getList(String name);
    
    /**
     * This method returns the active map associated with the
     * supplied name.
     * 
     * @param name The name
     * @return The map, or null if not found
     */
    public ActiveMap getMap(String name);
    
}
