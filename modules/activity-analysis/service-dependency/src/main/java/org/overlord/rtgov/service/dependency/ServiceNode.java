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

import org.overlord.rtgov.analytics.service.ServiceDefinition;
import org.overlord.rtgov.analytics.situation.Situation;

/**
 * This class represents a node in the service graph.
 *
 */
public class ServiceNode {
    
    /**
     * This boolean property indicates whether the service node
     * is an initial node in the graph.
     */
    public static final String INITIAL_NODE="InitialNode";

    private ServiceDefinition _service=null;
    private java.util.Set<OperationNode> _operations=new java.util.HashSet<OperationNode>();
    private java.util.Map<String,Object> _properties=new java.util.HashMap<String,Object>();
    private java.util.List<Situation> _situations=new java.util.ArrayList<Situation>();
    
    /**
     * The default constructor.
     */
    public ServiceNode() {
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
     * This method returns the operation nodes.
     * 
     * @return The operation nodes
     */
    public java.util.Set<OperationNode> getOperations() {
        return (_operations);
    }
    
    /**
     * This method returns the operation node associated with
     * the supplied name.
     * 
     * @param name The operation name
     * @return The operation node, or null if not found
     */
    public OperationNode getOperation(String name) {
        OperationNode ret=null;
        
        for (OperationNode opn : _operations) {
            if (opn.getOperation().getName().equals(name)) {
                ret = opn;
                break;
            }
        }
        
        return (ret);
    }
    
    /**
     * This method returns the properties associated with
     * the node.
     * 
     * @return The properties
     */
    public java.util.Map<String,Object> getProperties() {
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
