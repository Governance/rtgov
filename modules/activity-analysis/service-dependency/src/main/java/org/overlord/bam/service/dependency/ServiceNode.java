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
package org.overlord.bam.service.dependency;

import org.overlord.bam.analytics.service.ServiceDefinition;

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
}
