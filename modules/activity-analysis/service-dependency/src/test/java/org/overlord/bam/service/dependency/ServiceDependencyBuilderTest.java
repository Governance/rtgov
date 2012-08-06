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

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.bam.analytics.service.InvocationDefinition;
import org.overlord.bam.analytics.service.OperationDefinition;
import org.overlord.bam.analytics.service.RequestFaultDefinition;
import org.overlord.bam.analytics.service.RequestResponseDefinition;
import org.overlord.bam.analytics.service.ServiceDefinition;

public class ServiceDependencyBuilderTest {

    private static final String OP3 = "op3";
    private static final String OP2 = "op2";
    private static final String OP1 = "op1";
    private static final String SERVICE_TYPE1 = "serviceType1";
    private static final String SERVICE_TYPE2 = "serviceType2";
    private static final String SERVICE_TYPE3 = "serviceType3";

    @Test
    public void testInitialServices() {
        ServiceDefinition sd1=new ServiceDefinition();
        sd1.setServiceType(SERVICE_TYPE1);
        
        OperationDefinition op1=new OperationDefinition();
        sd1.getOperations().add(op1);
        
        RequestResponseDefinition rrd1=new RequestResponseDefinition();
        op1.setRequestResponse(rrd1);
        
        InvocationDefinition id1=new InvocationDefinition();
        id1.setServiceType(SERVICE_TYPE3);
        rrd1.getInvocations().add(id1);
        
        ServiceDefinition sd2=new ServiceDefinition();
        sd2.setServiceType(SERVICE_TYPE2);
        
        OperationDefinition op2=new OperationDefinition();
        sd2.getOperations().add(op2);
        
        RequestResponseDefinition rrd2=new RequestResponseDefinition();
        op2.setRequestResponse(rrd2);
        
        InvocationDefinition id2=new InvocationDefinition();
        id2.setServiceType(SERVICE_TYPE3);
        rrd2.getInvocations().add(id2);
        
        ServiceDefinition sd3=new ServiceDefinition();
        sd3.setServiceType(SERVICE_TYPE3);
        
        
        java.util.Set<ServiceDefinition> sds=new java.util.HashSet<ServiceDefinition>();
        sds.add(sd1);
        sds.add(sd2);
        sds.add(sd3);
        
        java.util.Set<ServiceDefinition> results=
                        ServiceDependencyBuilder.getInitialServices(sds);
        
        if (results == null) {
            fail("Results null");
        }
        
        if (results.size() != 2) {
            fail("Should be 2 results: "+results.size());
        }
        
        if (!results.contains(sd1)) {
            fail("SD1 not in results");
        }
       
        if (!results.contains(sd2)) {
            fail("SD2 not in results");
        }
    }

    @Test
    public void testServiceClients() {
        ServiceDefinition sd1=new ServiceDefinition();
        sd1.setServiceType(SERVICE_TYPE1);
        
        OperationDefinition op1=new OperationDefinition();
        sd1.getOperations().add(op1);
        
        RequestResponseDefinition rrd1=new RequestResponseDefinition();
        op1.setRequestResponse(rrd1);
        
        InvocationDefinition id1=new InvocationDefinition();
        id1.setServiceType(SERVICE_TYPE3);
        rrd1.getInvocations().add(id1);
        
        ServiceDefinition sd2=new ServiceDefinition();
        sd2.setServiceType(SERVICE_TYPE2);
        
        OperationDefinition op2=new OperationDefinition();
        sd2.getOperations().add(op2);
        
        RequestFaultDefinition rrd2=new RequestFaultDefinition();
        op2.getRequestFaults().add(rrd2);
        
        InvocationDefinition id2=new InvocationDefinition();
        id2.setServiceType(SERVICE_TYPE3);
        rrd2.getInvocations().add(id2);
        
        ServiceDefinition sd3=new ServiceDefinition();
        sd3.setServiceType(SERVICE_TYPE3);
        
        
        java.util.Set<ServiceDefinition> sds=new java.util.HashSet<ServiceDefinition>();
        sds.add(sd1);
        sds.add(sd2);
        sds.add(sd3);
        
        java.util.Set<ServiceDefinition> results=
                ServiceDependencyBuilder.getServiceClients(SERVICE_TYPE3, sds);
        
        if (results == null) {
            fail("Results null");
        }
        
        if (results.size() != 2) {
            fail("Should be 2 results: "+results.size());
        }
        
        if (!results.contains(sd1)) {
            fail("SD1 not in results");
        }
       
        if (!results.contains(sd2)) {
            fail("SD2 not in results");
        }
    }

    @Test
    public void testBuildGraph() {
        ServiceDefinition sd1=new ServiceDefinition();
        sd1.setServiceType(SERVICE_TYPE1);
        
        OperationDefinition op1=new OperationDefinition();
        op1.setName(OP1);
        sd1.getOperations().add(op1);
        
        RequestResponseDefinition rrd1=new RequestResponseDefinition();
        op1.setRequestResponse(rrd1);
        
        InvocationDefinition id1=new InvocationDefinition();
        id1.setServiceType(SERVICE_TYPE2);
        id1.setOperation(OP2);
        rrd1.getInvocations().add(id1);
        
        ServiceDefinition sd2=new ServiceDefinition();
        sd2.setServiceType(SERVICE_TYPE2);
        
        OperationDefinition op2=new OperationDefinition();
        op2.setName(OP2);
        sd2.getOperations().add(op2);
        
        RequestResponseDefinition rrd2=new RequestResponseDefinition();
        op2.setRequestResponse(rrd2);
        
        InvocationDefinition id2a=new InvocationDefinition();
        id2a.setServiceType(SERVICE_TYPE1);
        id2a.setOperation(OP1);
        rrd2.getInvocations().add(id2a);
        
        InvocationDefinition id2c=new InvocationDefinition();
        id2c.setServiceType(SERVICE_TYPE3);
        id2c.setOperation(OP3);
        rrd2.getInvocations().add(id2c);
        
        RequestFaultDefinition rfd2=new RequestFaultDefinition();
        op2.getRequestFaults().add(rfd2);
        
        InvocationDefinition id2b=new InvocationDefinition();
        id2b.setServiceType(SERVICE_TYPE3);
        id2b.setOperation(OP3);
        rfd2.getInvocations().add(id2b);
        
        ServiceDefinition sd3=new ServiceDefinition();
        sd3.setServiceType(SERVICE_TYPE3);
        
        OperationDefinition op3=new OperationDefinition();
        op3.setName(OP3);
        sd3.getOperations().add(op3);
        
        
        java.util.Set<ServiceDefinition> sds=new java.util.HashSet<ServiceDefinition>();
        sds.add(sd1);
        sds.add(sd2);
        sds.add(sd3);
        
        ServiceGraph result=
                ServiceDependencyBuilder.buildGraph(sds);
        
        if (result == null) {
            fail("Result null");
        }
        
        if (result.getServiceNodes().size() != 3) {
            fail("Expecting 3 nodes: "+result.getServiceNodes().size());
        }
        
        // Should only be 3 links, although 4 invocations, as
        // two are between the same source/target operation nodes
        if (result.getUsageLinks().size() != 3) {
            fail("Expecting 3 usage links: "+result.getUsageLinks().size());
        }
        
        // Should only be 3 links, although 4 invocations, as
        // two are between the same source/target operation nodes
        if (result.getInvocationLinks().size() != 3) {
            fail("Expecting 3 invocation links: "+result.getInvocationLinks().size());
        }
        
        ServiceNode sn1=result.getServiceNode(SERVICE_TYPE1);
        OperationNode opn1=sn1.getOperation(OP1);
        ServiceNode sn2=result.getServiceNode(SERVICE_TYPE2);
        OperationNode opn2=sn2.getOperation(OP2);
        ServiceNode sn3=result.getServiceNode(SERVICE_TYPE3);
        OperationNode opn3=sn3.getOperation(OP3);
        
        if (!result.getUsageLinks().contains(new UsageLink(sn1, sn2))) {
            fail("UsageLink from s1 to s2 not present");
        }
        
        if (!result.getUsageLinks().contains(new UsageLink(sn2, sn1))) {
            fail("UsageLink from s2 to s1 not present");
        }
        
        if (!result.getUsageLinks().contains(new UsageLink(sn2, sn3))) {
            fail("UsageLink from s2 to s3 not present");
        }
        
        if (result.getUsageLinks().contains(new UsageLink(sn3, sn2))) {
            fail("UsageLink from s3 to s2 should not be present");
        }
        
        int idcount=0;
        
        for (UsageLink ul : result.getUsageLinks()) {
            idcount += ul.getInvocations().size();
        }
        
        if (idcount != 4) {
            fail("Expecting 4 invocation definitions: "+idcount);
        }
        
        
        if (!result.getInvocationLinks().contains(new InvocationLink(opn1, opn2))) {
            fail("Link from op1 to op2 not present");
        }
        
        if (!result.getInvocationLinks().contains(new InvocationLink(opn2, opn1))) {
            fail("Link from op2 to op1 not present");
        }
        
        if (!result.getInvocationLinks().contains(new InvocationLink(opn2, opn3))) {
            fail("Link from op2 to op3 not present");
        }
        
        if (result.getInvocationLinks().contains(new InvocationLink(opn3, opn2))) {
            fail("Link from op3 to op2 should not be present");
        }
        
        idcount = 0;
        
        for (InvocationLink il : result.getInvocationLinks()) {
            idcount += il.getInvocations().size();
        }
        
        if (idcount != 4) {
            fail("Expecting 4 invocation definitions: "+idcount);
        }
    }
}
