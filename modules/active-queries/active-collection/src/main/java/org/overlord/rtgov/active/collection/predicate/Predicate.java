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
package org.overlord.rtgov.active.collection.predicate;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import org.overlord.rtgov.active.collection.ActiveCollectionContext;

/**
 * This interface is used to determine whether a changed item
 * in a parent active collection is relevant to a child collection.
 *
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type")
@JsonSubTypes({@Type(value=MVEL.class)})
public abstract class Predicate {

    /**
     * This method evaluates the supplied item against
     * the predicate.
     * 
     * @param context The context
     * @param item The item
     * @return Whether the predicate is true or false for this item
     */
    public abstract boolean evaluate(ActiveCollectionContext context, Object item);
    
}
