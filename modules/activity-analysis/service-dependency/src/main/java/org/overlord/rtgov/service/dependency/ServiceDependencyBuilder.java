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

import java.util.logging.Logger;

import org.overlord.rtgov.analytics.service.InterfaceDefinition;
import org.overlord.rtgov.analytics.service.InvocationDefinition;
import org.overlord.rtgov.analytics.service.InvocationMetric;
import org.overlord.rtgov.analytics.service.OperationDefinition;
import org.overlord.rtgov.analytics.service.RequestFaultDefinition;
import org.overlord.rtgov.analytics.service.ServiceDefinition;
import org.overlord.rtgov.analytics.situation.Situation;

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
                        getServiceClients(sd, sds);
            
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
     * @param mainsd The service definition
     * @param sds The other service definitions to check
     * @return The set of service definitions that are clients of
     *                  the service definition
     */
    public static java.util.Set<ServiceDefinition> getServiceClients(ServiceDefinition mainsd,
                        java.util.Collection<ServiceDefinition> sds) {
        java.util.Set<ServiceDefinition> ret=
                new java.util.HashSet<ServiceDefinition>();
        
        for (ServiceDefinition sd : sds) {
            if (!sd.getServiceType().equals(mainsd.getServiceType())) {
                
                for (InterfaceDefinition iDef : sd.getInterfaces()) {
                
                    for (OperationDefinition opDef : iDef.getOperations()) {
                    
                        if (opDef.getRequestResponse() != null) {
                            for (InvocationDefinition invDef
                                        : opDef.getRequestResponse().getInvocations()) {
                                if (mainsd.getInterface(invDef.getInterface()) != null) {
                                    
                                    if (!ret.contains(sd)) {
                                        ret.add(sd);
                                    }
                                }
                            }
                        }
                        
                        for (RequestFaultDefinition rfd : opDef.getRequestFaults()) {
                            for (InvocationDefinition invDef
                                        : rfd.getInvocations()) {
                                if (mainsd.getInterface(invDef.getInterface()) != null) {
                                    
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
     * definitions. If specified, the result will focus on a nominated service,
     * only showing its direct client services, and the services it directly
     * or indirectly calls.
     * 
     * @param sds The service definitions
     * @param sits The situations
     * @param focusServiceType The optional service type to focus on
     * @return The service graph
     */
    public static ServiceGraph buildGraph(java.util.Set<ServiceDefinition> sds,
                        java.util.List<Situation> sits, String focusServiceType) {
        ServiceGraph ret=buildGraph(sds, sits);
        
        if (focusServiceType != null) {
            filter(ret, focusServiceType);
        }
        
        return (ret);
    }
    
    /**
     * This method filters out any service node that is not a client, or
     * direct/indirect service provider, to the supplied service type.
     * 
     * @param sg
     * @param focusServiceType
     */
    protected static void filter(ServiceGraph sg, String focusServiceType) {
        ServiceNode focus=sg.getServiceNode(focusServiceType);
        
        // If focus service node is not found, then ignore and return the
        // complete graph
        if (focus != null) {
            
            // For each service, that is not the focus service, determine if either
            // a client or direct/indirect service.
            java.util.Set<ServiceNode> serviceProviders=new java.util.HashSet<ServiceNode>();
            
            java.util.Iterator<ServiceNode> serviceNodeIter=sg.getServiceNodes().iterator();
            
            while (serviceNodeIter.hasNext()) {
                ServiceNode node=serviceNodeIter.next();
            
                // If node is the focus, or it has already been identified as a service
                // provider, then just continue
                if (node == focus || serviceProviders.contains(node)) {
                    continue;
                }
                
                // Check if service node is a direct client of the focus service
                if (sg.getUsageLink(node, focus) != null) {
                    continue;
                }
                
                java.util.Set<ServiceNode> visited=new java.util.HashSet<ServiceNode>();
                
                if (!isServiceProvider(sg, node, focus, visited)) {
                    // Remove invocation links associated with the node
                    java.util.Iterator<InvocationLink> ilIter=sg.getInvocationLinks().iterator();
                    
                    while (ilIter.hasNext()) {
                        InvocationLink il=ilIter.next();
                       
                        if (node.getOperations().contains(il.getSource())
                                || node.getOperations().contains(il.getTarget())) {
                            ilIter.remove();
                        }
                    }
                    
                    // Remove usage links associated with the node
                    java.util.Iterator<UsageLink> ulIter=sg.getUsageLinks().iterator();
                    
                    while (ulIter.hasNext()) {
                        UsageLink ul=ulIter.next();
                       
                        if (ul.getSource() == node
                                || ul.getTarget() == node) {
                            ulIter.remove();
                        }
                    }
                    
                    // Remove node from graph
                    serviceNodeIter.remove();
                } else {
                    serviceProviders.addAll(visited);
                }
            }
        }
    }
    
    /**
     * This method determines whether the supplied 
     * 
     * @param sg The service graph
     * @param node The node of interest
     * @param focus The focus service
     * @param visited The list of nodes already visited
     * @return Whether the node is a direct or indirect service provider of the focus service
     */
    protected static boolean isServiceProvider(ServiceGraph sg, ServiceNode node, ServiceNode focus,
                                    java.util.Set<ServiceNode> visited) {
        if (visited.contains(node)) {
            return (false);
        }
        
        visited.add(node);
        
        if (sg.getUsageLink(focus, node) != null) {
            return (true);
        }
        
        for (UsageLink ul : sg.getUsageLinks()) {
            if (ul.getTarget() == node) {
                if (isServiceProvider(sg, ul.getSource(), focus, visited)) {
                    return (true);
                }
            }
        }
        
        return (false);
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
            
            for (InterfaceDefinition idef : sd.getInterfaces()) {
                for (OperationDefinition op : idef.getOperations()) {
                    OperationNode opn=new OperationNode();
                    
                    opn.setService(sd);
                    opn.setOperation(op);
                    
                    if (sits != null) {
                        for (Situation s : sits) {
                            String[] parts=s.subjectAsParts();
                            if (parts.length > 1 && parts[0].equals(
                                        sn.getService().getServiceType())
                                        && parts[1].equals(op.getName())) {
                                opn.getSituations().add(s);
                            }
                        }
                    }
                    
                    sn.getOperations().add(opn);
                }
            }
            
            sn.getProperties().put(ServiceNode.INITIAL_NODE, initialNodes.contains(sd));
            
            if (sits != null) {
                // Associate situations specific to the interface just with the service node
                for (Situation s : sits) {
                    String[] parts=s.subjectAsParts();
                    if (parts.length == 1 && parts[0].equals(
                                sn.getService().getServiceType())) {
                        sn.getSituations().add(s);
                    }
                }
            }
            
            ret.getServiceNodes().add(sn);
        }
        
        // Initialize invocation links between operations
        for (ServiceDefinition sd : sds) {            
            ServiceNode sn=ret.getServiceNode(sd.getServiceType());
            
            for (InterfaceDefinition idef : sd.getInterfaces()) {
                for (OperationDefinition op : idef.getOperations()) {
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
            ServiceNode tsn=null;
            
            // Workaround to support FSW6.0 with RTGov 1, which did not have
            // the getServiceType() method.
            try {
                if (id.getServiceType() != null) {
                    tsn = sg.getServiceNode(id.getServiceType());
                }
            } catch (Throwable t) {
                // Ignore
                if (LOG.isLoggable(java.util.logging.Level.FINEST)) {
                    LOG.log(java.util.logging.Level.FINEST, "Failed to get service type (assume running in FSW6.0)", t);
                }
            }
            
            if (tsn == null && id.getInterface() != null) {
                tsn = sg.getServiceNodeForInterface(id.getInterface());
            }
            
            if (tsn != null) {
                UsageLink existingul=sg.getUsageLink(sn, tsn);
                
                if (existingul == null) {
                    UsageLink ul=new UsageLink();
                    
                    ul.setSource(sn);
                    ul.setTarget(tsn);
                    ul.getInvocations().add(id);
                    
                    sg.getUsageLinks().add(ul);
                    
                } else {
                    LOG.fine("Usage link between source '"+sn
                            +"' and target '"+tsn
                            +"' has already been defined: "+existingul);
                    
                    // Copy invocation definitions to existing link
                    existingul.getInvocations().add(id);
                }

                // Find target for invocation link
                OperationNode topn=tsn.getOperation(id.getOperation());
                
                if (topn != null) {
                    InvocationLink existingil=sg.getInvocationLink(opn, topn);
                    
                    if (existingil == null) {
                        InvocationLink il=new InvocationLink();
                        
                        il.setSource(opn);
                        il.setTarget(topn);
                        il.getInvocations().add(id);
                        
                        sg.getInvocationLinks().add(il);
                    } else {
                        LOG.fine("Link between source '"+opn
                                +"' and target '"+topn
                                +"' has already been defined: "+existingil);
                        
                        // Copy invocation definitions to existing link
                        existingil.getInvocations().add(id);
                    }
                }                
            }
        }
    }

    /**
     * This method returns a merged invocation metric value associated with the supplied
     * invocation definitions.
     * 
     * @param invocations The invocation details
     * @return The merged metrics
     */
    public static InvocationMetric getMergedMetrics(java.util.List<InvocationDefinition> invocations) {
        InvocationMetric ret=new InvocationMetric();
        
        for (InvocationDefinition id : invocations) {
            ret.merge(id.getMetrics());
        }
        
        return (ret);
    }
    
}
