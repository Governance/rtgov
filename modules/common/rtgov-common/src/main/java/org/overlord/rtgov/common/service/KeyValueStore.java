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
package org.overlord.rtgov.common.service;

/**
 * This abstract class represents a key/value store.
 *
 */
public abstract class KeyValueStore extends Service {

    /**
     * This method adds a value, associated with the id.
     *
     * @param id The id
     * @param document The value
     * @param <V> The value type
     * @throws Exception Failed to add document
     */
    public abstract <V> void add(String id, V document) throws Exception;

    /**
     * This method removes the value associated with the supplied
     * id.
     *
     * @param id The id
     */
    public abstract void remove(String id);

    /**
     * This method updates the value associated with the supplied
     * id.
     *
     * @param id The id
     * @param document The updated value
     * @param <V> The value type
     */
    public abstract <V> void update(String id, V document);

    /**
     * This method returns the value associated with the supplied
     * id.
     *
     * @param id The id
     * @param <V> The value type
     * @return The value, or null if not found
     */
    public abstract <V> V get(String id);
}
