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
package org.overlord.rtgov.common.util;

/**
 * This interface provides access to Runtime Governance properties.
 *
 */
public interface RTGovPropertiesProvider {

    /**
     * This method returns the property associated with the
     * supplied name.
     * 
     * @param name The property name
     * @return The value, or null if not found
     */
    public String getProperty(String name);
    
    /**
     * This method returns the Runtime Governance properties.
     * 
     * @return The properties, or null if not available
     */
    public java.util.Properties getProperties();
    
}
