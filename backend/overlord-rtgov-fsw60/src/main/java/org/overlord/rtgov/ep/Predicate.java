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
package org.overlord.rtgov.ep;

import org.codehaus.jackson.annotate.JsonTypeInfo;

/**
 * This class represents the predicate applied to an
 * event being processed.
 *
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class Predicate {

    /**
     * This method initializes the predicate.
     * 
     * @throws Exception Failed to initialize the predicate
     */
    public void init() throws Exception {
    }
    
    /**
     * This method evaluates the predicate against the supplied
     * event to determine if it should be processed.
     * 
     * @param event The event
     * @return Whether the event should be processed
     */
    public abstract boolean evaluate(Object event);

}
