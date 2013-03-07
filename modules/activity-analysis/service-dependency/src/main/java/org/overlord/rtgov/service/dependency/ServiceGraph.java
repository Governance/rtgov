/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-12, Red Hat Middleware LLC, and others contributors as indicated
 * by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
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
     * the supplied service type.
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
     * This method returns the service invocation links.
     * 
     * @return The service invocation links
     */
    public java.util.Set<InvocationLink> getInvocationLinks() {
        return (_invocationLinks);
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
     * This method returns the properties associated with
     * the graph.
     * 
     * @return The properties
     */
    public java.util.Map<String,Object> getProperties() {
        return (_properties);
    }
}
