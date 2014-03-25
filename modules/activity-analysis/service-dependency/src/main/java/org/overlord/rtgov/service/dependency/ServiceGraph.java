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

/**
 * This class represents the service graph.
 *
 */
public class ServiceGraph {

    private java.util.Set<ServiceNode> _nodes=new java.util.HashSet<ServiceNode>();
    private java.util.Set<InvocationLink> _invocationLinks=new java.util.HashSet<InvocationLink>();
    private java.util.Set<UsageLink> _usageLinks=new java.util.HashSet<UsageLink>();
    private java.util.Map<String,Object> _properties=new java.util.HashMap<String,Object>();
    
    private String _description=null;
 
    /**
     * The default constructor.
     */
    public ServiceGraph() {
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
     */
    public void setDescription(String description) {
        _description = description;
    }
    
    /**
     * This method returns the service nodes.
     * 
     * @return The service nodes
     */
    public java.util.Set<ServiceNode> getServiceNodes() {
        return (_nodes);
    }
    
    /**
     * This method returns the service node associated with
     * the supplied service interface.
     * 
     * @param serviceType The service type
     * @return The service node, or null if not found
     */
    public ServiceNode getServiceNode(String serviceType) {
        ServiceNode ret=null;
        
        for (ServiceNode sn : _nodes) {
            if (sn.getService().getServiceType().equals(serviceType)) {
                ret = sn;
                break;
            }
        }
        
        return (ret);
    }
    
    /**
     * This method returns the service node associated with
     * the supplied service interface.
     * 
     * @param intf The interface
     * @return The service node, or null if not found
     */
    public ServiceNode getServiceNodeForInterface(String intf) {
        ServiceNode ret=null;
        
        for (ServiceNode sn : _nodes) {
            if (sn.getService().getInterface(intf) != null) {
                ret = sn;
                break;
            }
        }
        
        return (ret);
    }
    
    /**
     * This method returns the service invocation links.
     * 
     * @return The service invocation links
     */
    public java.util.Set<InvocationLink> getInvocationLinks() {
        return (_invocationLinks);
    }
    
    /**
     * This method returns the invocation link associated with the supplied
     * source and target operation nodes.
     * 
     * @param source The source
     * @param target The target
     * @return The invocation link, or null if not found
     */
    public InvocationLink getInvocationLink(OperationNode source, OperationNode target) {
        InvocationLink ret=null;
        
        for (InvocationLink eil : getInvocationLinks()) {
            if (eil.getSource() == source &&
                    eil.getTarget() == target) {
                ret = eil;
                break;
            }
        }
        
        return (ret);
    }
    
    /**
     * This method returns the service usage links.
     * 
     * @return The service usage links
     */
    public java.util.Set<UsageLink> getUsageLinks() {
        return (_usageLinks);
    }
    
    /**
     * This method returns the usage link associated with the supplied
     * source and target service nodes.
     * 
     * @param source The source
     * @param target The target
     * @return The usage link, or null if not found
     */
    public UsageLink getUsageLink(ServiceNode source, ServiceNode target) {
        UsageLink ret=null;
        
        for (UsageLink eul : getUsageLinks()) {
            if (eul.getSource() == source &&
                    eul.getTarget() == target) {
                ret = eul;
                break;
            }
        }
        
        return (ret);
    }

    /**
     * This method returns the properties associated with
     * the graph.
     * 
     * @return The properties
     */
    public java.util.Map<String,Object> getProperties() {
        return (_properties);
    }
}
