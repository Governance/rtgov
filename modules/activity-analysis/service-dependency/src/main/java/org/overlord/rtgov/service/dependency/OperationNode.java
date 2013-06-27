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
package org.overlord.rtgov.service.dependency;

import org.overlord.rtgov.analytics.service.OperationDefinition;
import org.overlord.rtgov.analytics.service.ServiceDefinition;
import org.overlord.rtgov.analytics.situation.Situation;

/**
 * This class represents a node in the graph/tree
 * associated with a service operation.
 *
 */
public class OperationNode {

    private ServiceDefinition _service=null;
    private OperationDefinition _operation=null;
    private java.util.Properties _properties=new java.util.Properties();
    private java.util.List<Situation> _situations=new java.util.ArrayList<Situation>();
    
    /**
     * The default constructor.
     */
    public OperationNode() {
    }
    
    /**
     * This method returns the service definition.
     * 
     * @return The service definition
     */
    public ServiceDefinition getService() {
        return (_service);
    }
    
    /**
     * This method sets the service definition.
     * 
     * @param sd The service definition
     */
    public void setService(ServiceDefinition sd) {
        _service = sd;
    }

    /**
     * This method returns the operation.
     * 
     * @return The operation
     */
    public OperationDefinition getOperation() {
        return (_operation);
    }
    
    /**
     * This method sets the operation.
     * 
     * @param op The operation
     */
    public void setOperation(OperationDefinition op) {
        _operation = op;
    }
    
    /**
     * This method returns the properties associated with
     * the node.
     * 
     * @return The properties
     */
    public java.util.Properties getProperties() {
        return (_properties);
    }
    
    /**
     * This method returns the situations associated with
     * the node.
     * 
     * @return The situations
     */
    public java.util.List<Situation> getSituations() {
        return (_situations);
    }
}
