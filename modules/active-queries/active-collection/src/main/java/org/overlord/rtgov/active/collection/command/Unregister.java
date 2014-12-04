/*
 * 2012-5 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.overlord.rtgov.active.collection.command;

/**
 * This class represents the command to unregister a listener from an
 * active collection.
 *
 */
public class Unregister {

    private String _collection;
    
    /**
     * This method returns the active collection name.
     * 
     * @return The active collection name
     */
    public String getCollection() {
        return (_collection);
    }
    
    /**
     * This method sets the active collection name.
     * 
     * @param name The active collection name
     */
    public void setCollection(String name) {
        _collection = name;
    }

}
