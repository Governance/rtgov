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
package org.overlord.rtgov.ep.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="RTGOV_TEST_EVENT")
public class TestEvent implements java.io.Serializable {

	private static final long serialVersionUID = 7232940306592053980L;

	private String _id=null;
    private String _description=null;

    /**
     * This method sets the id.
     * 
     * @param id The id
     */
    public void setId(String id) {
        _id = id;
    }
    
    /**
     * This method gets the id.
     * 
     * @return The id
     */
    @Id
    public String getId() {
        return (_id);
    }
    
    /**
     * This method sets the description.
     * 
     * @param description The description
     */
    public void setDescription(String description) {
    	_description = description;
    }
    
    /**
     * This method gets the description.
     * 
     * @return The description
     */
    public String getDescription() {
        return (_description);
    }
    
}
