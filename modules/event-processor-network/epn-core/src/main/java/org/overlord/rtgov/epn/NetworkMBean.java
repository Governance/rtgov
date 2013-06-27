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
package org.overlord.rtgov.epn;

/**
 * This interface exposes the attributes and operations to be
 * managed for a Network.
 *
 */
public interface NetworkMBean {

    /**
     * This method returns the name of the network. This can be used
     * to locate the network by name.
     * 
     * @return The name of the network
     */
    public String getName();
    
    /**
     * This method returns the version associated with the network.
     * 
     * @return The version
     */
    public String getVersion();

    /**
     * This method returns the date/time the network was
     * last accessed.
     * 
     * @return When the network was last accessed
     */
    public java.util.Date getLastAccessed();

}
