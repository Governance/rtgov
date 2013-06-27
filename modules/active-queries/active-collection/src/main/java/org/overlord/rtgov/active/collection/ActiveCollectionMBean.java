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
 * This interface exposes the attributes and operations required
 * to manage the active collection.
 *
 */
public interface ActiveCollectionMBean {

    /**
     * This method returns the name of the active collection.
     * 
     * @return The name
     */
    public String getName();
    
    /**
     * This method returns the number of elements in the collection.
     * 
     * @return The size of the collection
     */
    public int getSize();
    
    /**
     * This method returns the item expiration duration.
     * 
     * @return The number of milliseconds that the item should remain
     *          in the active collection, or 0 if not relevant
     */
    public long getItemExpiration();

    /**
     * This method returns the maximum number of items that should be
     * contained within the active collection. The default policy will
     * be to remove oldest entry when maximum number is reached.
     * 
     * @return The maximum number of items, or 0 if not relevant
     */
    public int getMaxItems();

    /**
     * This method gets the high water mark, used to indicate
     * when a warning should be issued.
     * 
     * @return The high water mark, or 0 if not relevant
     */
    public int getHighWaterMark();

}
