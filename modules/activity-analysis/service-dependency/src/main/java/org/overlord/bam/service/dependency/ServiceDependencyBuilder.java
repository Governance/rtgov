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

import java.util.logging.Logger;

import org.overlord.bam.service.analytics.InvocationDefinition;
import org.overlord.bam.service.analytics.OperationDefinition;
import org.overlord.bam.service.analytics.RequestFaultDefinition;
import org.overlord.bam.service.analytics.ServiceDefinition;

/**
 * This class builds a service view representing the
 * dependency between a list of service definitions.
 *
 */
public class ServiceDependencyBuilder {
    
    private static final Logger LOG=Logger.getLogger(ServiceDependencyBuilder.class.getName());

    /**
     * This method returns the set of service definitions that
     * initiate business activity (i.e. have no client services).
     * 
     * @param sds The service definitions
     * @return The set of initial services
     */
    public static java.util.Set<ServiceDefinition> getInitialServices(java.util.Collection<ServiceDefinition> sds) {
        java.util.Set<ServiceDefinition> ret=
                new java.util.HashSet<ServiceDefinition>();
        
        for (ServiceDefinition sd : sds) {
            java.util.Set<ServiceDefinition> clients=
                        getServiceClients(sd.getServiceType(), sds);
            
            if (clients.size() == 0) {
                ret.add(sd);
            }
        }
        
        return (ret);
    }
    
    /**
     * This method returns the set of services that are clients to the
     * supplied service type.
     * 
     * @param serviceType The service type
     * @param sds The service definitions
     * @return The set of service definitions that are clients of
     *                  the service type
     */
    public static java.util.Set<ServiceDefinition> getServiceClients(String serviceType,
                        java.util.Collection<ServiceDefinition> sds) {
        java.util.Set<ServiceDefinition> ret=
                new java.util.HashSet<ServiceDefinition>();
        
        for (ServiceDefinition sd : sds) {
            if (!sd.getServiceType().equals(serviceType)) {
                
                for (OperationDefinition opDef : sd.getOperations()) {
                    
                    if (opDef.getRequestResponse() != null) {
                        for (InvocationDefinition invDef :
                                    opDef.getRequestResponse().getInvocations()) {
                            if (invDef.getServiceType().equals(serviceType)) {
                                
                                if (!ret.contains(sd)) {
                                    ret.add(sd);
                                }
                            }
                        }
                    }
                    
                    for (RequestFaultDefinition rfd : opDef.getRequestFaults()) {
                        for (InvocationDefinition invDef :
                                        rfd.getInvocations()) {
                            if (invDef.getServiceType().equals(serviceType)) {
                                
                                if (!ret.contains(sd)) {
                                    ret.add(sd);
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return (ret);
    }
    
    /**
     * This method builds a service graph from a collection of service
     * definitions.
     * 
     * @param sds The service definitions
     * @return The service graph
     */
    public static ServiceGraph buildGraph(java.util.Set<ServiceDefinition> sds) {
        ServiceGraph ret=new ServiceGraph();
        
        // Get set of initial services
        java.util.Set<ServiceDefinition> initialNodes=
                        getInitialServices(sds);
        
        // Initialize the service nodes
        for (ServiceDefinition sd : sds) {
            ServiceNode sn=new ServiceNode();
            
            sn.setService(sd);
            
            for (OperationDefinition op : sd.getOperations()) {
                OperationNode opn=new OperationNode();
                
                opn.setService(sd);
                opn.setOperation(op);
                
                sn.getOperations().add(opn);
            }
            
            sn.getProperties().put(ServiceNode.INITIAL_NODE, initialNodes.contains(sd));
            
            ret.getNodes().add(sn);
        }
        
        // Initialize invocation links between operations
        for (ServiceDefinition sd : sds) {
            
            for (OperationDefinition op : sd.getOperations()) {
                ServiceNode sn=ret.getNode(sd.getServiceType());
                OperationNode opn=sn.getOperation(op.getName());
                
                if (op.getRequestResponse() != null) {
                    linkOperationNodes(ret, sn, opn,
                            op.getRequestResponse().getInvocations());
                }
                
                for (RequestFaultDefinition rfd : op.getRequestFaults()) {
                    linkOperationNodes(ret, sn, opn,
                            rfd.getInvocations());
                }
            }
        }
        
        return (ret);
    }
    
    /**
     * This method links the source operation node with the invoked
     * operation nodes.
     * 
     * @param sg The service graph
     * @param sn The source service node
     * @param opn The source operation node
     * @param ids The list of invocations
     */
    protected static void linkOperationNodes(ServiceGraph sg, ServiceNode sn,
            OperationNode opn, java.util.List<InvocationDefinition> ids) {
        
        for (InvocationDefinition id : ids) {
            ServiceNode tsn=sg.getNode(id.getServiceType());
            
            if (tsn != null) {
                OperationNode topn=tsn.getOperation(id.getOperation());
                
                if (topn != null) {
                    InvocationLink il=new InvocationLink();
                    
                    il.setSource(opn);
                    il.setTarget(topn);
                    
                    if (!sg.getLinks().contains(il)) {
                        sg.getLinks().add(il);
                    } else {
                        LOG.fine("Link between source '"+opn
                                +"' and target '"+topn
                                +"' has already been defined: "+il);
                    }
                }
            }
        }
    }
}
