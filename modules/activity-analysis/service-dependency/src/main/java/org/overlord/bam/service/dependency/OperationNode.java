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

import org.overlord.bam.analytics.Situation;
import org.overlord.bam.analytics.service.OperationDefinition;
import org.overlord.bam.analytics.service.ServiceDefinition;

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
