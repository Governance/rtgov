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

import java.util.logging.Logger;

import org.overlord.rtgov.analytics.Situation;
import org.overlord.rtgov.analytics.service.InvocationDefinition;
import org.overlord.rtgov.analytics.service.OperationDefinition;
import org.overlord.rtgov.analytics.service.RequestFaultDefinition;
import org.overlord.rtgov.analytics.service.ServiceDefinition;
import org.overlord.rtgov.analytics.service.OperationImplDefinition;

/**
 * This class builds a service view representing the
 * dependency between a list of service definitions.
 *
 */
public final class ServiceDependencyBuilder {
    
    private static final Logger LOG=Logger.getLogger(ServiceDependencyBuilder.class.getName());

    /**
     * The default constructor.
     */
    private ServiceDependencyBuilder() {
    }
    
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
                        getServiceClients(sd.getInterface(), sds);
            
            if (clients.size() == 0) {
                ret.add(sd);
            }
        }
        
        return (ret);
    }
    
    /**
     * This method returns the set of services that are clients to the
     * supplied service interface.
     * 
     * @param intf The interface
     * @param sds The service definitions
     * @return The set of service definitions that are clients of
     *                  the service type
     */
    public static java.util.Set<ServiceDefinition> getServiceClients(String intf,
                        java.util.Collection<ServiceDefinition> sds) {
        java.util.Set<ServiceDefinition> ret=
                new java.util.HashSet<ServiceDefinition>();
        
        for (ServiceDefinition sd : sds) {
            if (!sd.getInterface().equals(intf)) {
                
                for (OperationDefinition opDef : sd.getOperations()) {
                    
                	for (OperationImplDefinition stod : opDef.getImplementations()) {
                		
	                    if (stod.getRequestResponse() != null) {
	                        for (InvocationDefinition invDef
	                                    : stod.getRequestResponse().getInvocations()) {
	                            if (invDef.getInterface().equals(intf)) {
	                                
	                                if (!ret.contains(sd)) {
	                                    ret.add(sd);
	                                }
	                            }
	                        }
	                    }
	                    
	                    for (RequestFaultDefinition rfd : stod.getRequestFaults()) {
	                        for (InvocationDefinition invDef
	                                    : rfd.getInvocations()) {
	                            if (invDef.getInterface().equals(intf)) {
	                                
	                                if (!ret.contains(sd)) {
	                                    ret.add(sd);
	                                }
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
     * @param sits The situations
     * @return The service graph
     */
    public static ServiceGraph buildGraph(java.util.Set<ServiceDefinition> sds,
                        java.util.List<Situation> sits) {
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
                
                String subject=sn.getService().getInterface()
                        +"/"+op.getName();
                
                if (sits != null) {
                    for (Situation s : sits) {
                        if (s.getSubject() != null
                        		&& (s.getSubject().equals(subject)
                        		|| s.getSubject().startsWith(subject+"/"))) {
                            opn.getSituations().add(s);
                        }
                    }
                }
                
                sn.getOperations().add(opn);
            }
            
            sn.getProperties().put(ServiceNode.INITIAL_NODE, initialNodes.contains(sd));
            
            if (sits != null) {
                for (Situation s : sits) {
                    if (s.getSubject() != null && s.getSubject().equals(
                                sn.getService().getInterface())) {
                        sn.getSituations().add(s);
                    }
                }
            }
            
            ret.getServiceNodes().add(sn);
        }
        
        // Initialize invocation links between operations
        for (ServiceDefinition sd : sds) {
            
            for (OperationDefinition op : sd.getOperations()) {
                ServiceNode sn=ret.getServiceNode(sd.getInterface());
                OperationNode opn=sn.getOperation(op.getName());
                
                for (OperationImplDefinition stod : op.getImplementations()) {
	                if (stod.getRequestResponse() != null) {
	                    linkOperationNodes(ret, sn, opn,
	                    		stod.getRequestResponse().getInvocations());
	                }
	                
	                for (RequestFaultDefinition rfd : stod.getRequestFaults()) {
	                    linkOperationNodes(ret, sn, opn,
	                            rfd.getInvocations());
	                }
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
            ServiceNode tsn=sg.getServiceNode(id.getInterface());
            
            if (tsn != null) {
                UsageLink ul=new UsageLink();
                
                ul.setSource(sn);
                ul.setTarget(tsn);
                ul.getInvocations().add(id);
                
                if (!sg.getUsageLinks().contains(ul)) {
                    sg.getUsageLinks().add(ul);
                } else {
                    LOG.fine("Usage link between source '"+sn
                            +"' and target '"+tsn
                            +"' has already been defined: "+ul);
                    
                    // Copy invocation definitions to existing link
                    for (UsageLink existing : sg.getUsageLinks()) {
                        
                        if (existing.equals(ul)) {
                            existing.getInvocations().addAll(ul.getInvocations());
                            break;
                        }
                    }
                }

                // Find target for invocation link
                OperationNode topn=tsn.getOperation(id.getOperation());
                
                if (topn != null) {
                    InvocationLink il=new InvocationLink();
                    
                    il.setSource(opn);
                    il.setTarget(topn);
                    il.getInvocations().add(id);
                    
                    if (!sg.getInvocationLinks().contains(il)) {
                        sg.getInvocationLinks().add(il);
                    } else {
                        LOG.fine("Link between source '"+opn
                                +"' and target '"+topn
                                +"' has already been defined: "+il);
                        
                        // Copy invocation definitions to existing link
                        for (InvocationLink existing : sg.getInvocationLinks()) {
                            
                            if (existing.equals(il)) {
                                existing.getInvocations().addAll(il.getInvocations());
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
