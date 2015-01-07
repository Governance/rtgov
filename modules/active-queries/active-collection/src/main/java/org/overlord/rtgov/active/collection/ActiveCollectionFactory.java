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
 * This class represents a factory for creating active collections.
 *
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class ActiveCollectionFactory {

    /**
     * This is the default factory.
     */
    public static final ActiveCollectionFactory DEFAULT_FACTORY=new ActiveCollectionFactory();
    
    /**
     * This method creates an active collection based on the supplied source.
     * 
     * @param acs The source
     * @return The active collection, or null if unable to create
     */
    public ActiveCollection createActiveCollection(ActiveCollectionSource acs) {
        ActiveCollection ret=null;
        
        if (acs.getType() == ActiveCollectionType.List) {
            ret = new ActiveList(acs);
        } else if (acs.getType() == ActiveCollectionType.Map) {
            ret = new ActiveMap(acs);
        }

        return (ret);
    }
    
}
