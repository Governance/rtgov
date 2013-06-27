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
package org.overlord.rtgov.reports.model;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;

/**
 * This class represents a section within the report.
 *
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type")
@JsonSubTypes({@Type(value=Tabular.class) })
public abstract class Section {
    
    private String _name=null;
    private String _description=null;
    
    /**
     * This method returns the name.
     * 
     * @return The name
     */
    public String getName() {
        return (_name);
    }
    
    /**
     * This method sets the name.
     * 
     * @param name The name
     * @return The section
     */
    public Section setName(String name) {
        _name = name;
        return (this);
    }
    
    /**
     * This method returns the description.
     * 
     * @return The description
     */
    public String getDescription() {
        return (_description);
    }
    
    /**
     * This method sets the description.
     * 
     * @param description The description
     * @return The section
     */
    public Section setDescription(String description) {
        _description = description;
        return (this);
    }
}
