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

    /**
     * This method returns the list of service definitions that
     * initiate business activity (i.e. have no client services).
     * 
     * @param sds The service definitions
     * @return The list of initial services
     */
    public static java.util.List<ServiceDefinition> getInitialServices(java.util.List<ServiceDefinition> sds) {
        java.util.List<ServiceDefinition> ret=
                new java.util.ArrayList<ServiceDefinition>();
        
        for (ServiceDefinition sd : sds) {
            java.util.List<ServiceDefinition> clients=
                        getServiceClients(sd.getServiceType(), sds);
            
            if (clients.size() == 0) {
                ret.add(sd);
            }
        }
        
        return (ret);
    }
    
    /**
     * This method returns the list of services that are clients to the
     * supplied service type.
     * 
     * @param serviceType The service type
     * @param sds The service definitions
     * @return The list of service definitions that are clients of
     *                  the service type
     */
    public static java.util.List<ServiceDefinition> getServiceClients(String serviceType,
                        java.util.List<ServiceDefinition> sds) {
        java.util.List<ServiceDefinition> ret=
                new java.util.ArrayList<ServiceDefinition>();
        
        for (ServiceDefinition sd : sds) {
            if (!sd.getServiceType().equals(serviceType)) {
                
                for (OperationDefinition opDef : sd.getOperations()) {
                    for (InvocationDefinition invDef :
                                opDef.getRequestResponse().getInvocations()) {
                        if (invDef.getServiceType().equals(serviceType)) {
                            
                            if (!ret.contains(sd)) {
                                ret.add(sd);
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
}
