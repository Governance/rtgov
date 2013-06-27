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
package org.overlord.rtgov.activity.processor;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.overlord.rtgov.activity.processor.mvel.MVELInformationTransformer;

/**
 * This abstract class represents an information transformer.
 *
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type")
@JsonSubTypes({@Type(value=SerializeInformationTransformer.class, name="serialize"),
        @Type(value=MVELInformationTransformer.class, name="mvel") })
public abstract class InformationTransformer {
    
    /**
     * This method initializes the information transformer.
     * 
     * @throws Exception Failed to initialize
     */
    public void init() throws Exception {
    }
    
    /**
     * This method transforms the supplied information and
     * returns the textual representation of the result.
     * 
     * @param information The information
     * @return The result, or null if unable to transform
     */
    public abstract String transform(Object information);
    
    /**
     * This method closes the information transformer.
     * 
     * @throws Exception Failed to close
     */
    public void close() throws Exception {
    }
    
}
