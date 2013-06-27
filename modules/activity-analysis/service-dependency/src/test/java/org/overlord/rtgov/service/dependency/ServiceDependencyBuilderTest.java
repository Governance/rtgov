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

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.analytics.service.InvocationDefinition;
import org.overlord.rtgov.analytics.service.InvocationMetric;
import org.overlord.rtgov.analytics.service.OperationDefinition;
import org.overlord.rtgov.analytics.service.RequestFaultDefinition;
import org.overlord.rtgov.analytics.service.RequestResponseDefinition;
import org.overlord.rtgov.analytics.service.ServiceDefinition;
import org.overlord.rtgov.analytics.service.OperationImplDefinition;
import org.overlord.rtgov.analytics.situation.Situation;
import org.overlord.rtgov.service.dependency.InvocationLink;
import org.overlord.rtgov.service.dependency.OperationNode;
import org.overlord.rtgov.service.dependency.ServiceDependencyBuilder;
import org.overlord.rtgov.service.dependency.ServiceGraph;
import org.overlord.rtgov.service.dependency.ServiceNode;
import org.overlord.rtgov.service.dependency.UsageLink;

public class ServiceDependencyBuilderTest {

    private static final String OP3 = "op3";
    private static final String OP2 = "op2";
    private static final String OP1 = "op1";
    private static final String INTERFACE1 = "intf1";
    private static final String INTERFACE2 = "intf2";
    private static final String INTERFACE3 = "intf3";
    private static final String SERVICE_TYPE2 = "serviceType2";
    private static final String FAULT2 = "fault2";

    @Test
    public void testInitialServices() {
        ServiceDefinition sd1=new ServiceDefinition();
        sd1.setInterface(INTERFACE1);
        
        OperationDefinition op1=new OperationDefinition();
        sd1.getOperations().add(op1);
        
        OperationImplDefinition stod1=new OperationImplDefinition();
        op1.getImplementations().add(stod1);
        
        RequestResponseDefinition rrd1=new RequestResponseDefinition();
        stod1.setRequestResponse(rrd1);
        
        InvocationDefinition id1=new InvocationDefinition();
        id1.setInterface(INTERFACE3);
        rrd1.getInvocations().add(id1);
        
        ServiceDefinition sd2=new ServiceDefinition();
        sd2.setInterface(INTERFACE2);
        
        OperationDefinition op2=new OperationDefinition();
        sd2.getOperations().add(op2);
        
        OperationImplDefinition stod2=new OperationImplDefinition();
        op2.getImplementations().add(stod2);
        
        RequestResponseDefinition rrd2=new RequestResponseDefinition();
        stod2.setRequestResponse(rrd2);
        
        InvocationDefinition id2=new InvocationDefinition();
        id2.setInterface(INTERFACE3);
        rrd2.getInvocations().add(id2);
        
        ServiceDefinition sd3=new ServiceDefinition();
        sd3.setInterface(INTERFACE3);
        
        
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
        sd1.setInterface(INTERFACE1);
        
        OperationDefinition op1=new OperationDefinition();
        sd1.getOperations().add(op1);
        
        OperationImplDefinition stod1=new OperationImplDefinition();
        op1.getImplementations().add(stod1);
        
        RequestResponseDefinition rrd1=new RequestResponseDefinition();
        stod1.setRequestResponse(rrd1);
        
        InvocationDefinition id1=new InvocationDefinition();
        id1.setInterface(INTERFACE3);
        rrd1.getInvocations().add(id1);
        
        ServiceDefinition sd2=new ServiceDefinition();
        sd2.setInterface(INTERFACE2);
        
        OperationDefinition op2=new OperationDefinition();
        sd2.getOperations().add(op2);
        
        OperationImplDefinition stod2=new OperationImplDefinition();
        op2.getImplementations().add(stod2);
        
        RequestFaultDefinition rrd2=new RequestFaultDefinition();
        stod2.getRequestFaults().add(rrd2);
        
        InvocationDefinition id2=new InvocationDefinition();
        id2.setInterface(INTERFACE3);
        rrd2.getInvocations().add(id2);
        
        ServiceDefinition sd3=new ServiceDefinition();
        sd3.setInterface(INTERFACE3);
        
        
        java.util.Set<ServiceDefinition> sds=new java.util.HashSet<ServiceDefinition>();
        sds.add(sd1);
        sds.add(sd2);
        sds.add(sd3);
        
        java.util.Set<ServiceDefinition> results=
                ServiceDependencyBuilder.getServiceClients(INTERFACE3, sds);
        
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
        sd1.setInterface(INTERFACE1);
        
        OperationDefinition op1=new OperationDefinition();
        op1.setName(OP1);
        sd1.getOperations().add(op1);
        
        OperationImplDefinition stod1=new OperationImplDefinition();
        op1.getImplementations().add(stod1);
        
        RequestResponseDefinition rrd1=new RequestResponseDefinition();
        stod1.setRequestResponse(rrd1);
        
        InvocationDefinition id1=new InvocationDefinition();
        id1.setInterface(INTERFACE2);
        id1.setOperation(OP2);
        rrd1.getInvocations().add(id1);
        
        ServiceDefinition sd2=new ServiceDefinition();
        sd2.setInterface(INTERFACE2);
        
        OperationDefinition op2=new OperationDefinition();
        op2.setName(OP2);
        sd2.getOperations().add(op2);
        
        OperationImplDefinition stod2=new OperationImplDefinition();
        op2.getImplementations().add(stod2);
        
        RequestResponseDefinition rrd2=new RequestResponseDefinition();
        stod2.setRequestResponse(rrd2);
        
        InvocationDefinition id2a=new InvocationDefinition();
        id2a.setInterface(INTERFACE1);
        id2a.setOperation(OP1);
        rrd2.getInvocations().add(id2a);
        
        InvocationDefinition id2c=new InvocationDefinition();
        id2c.setInterface(INTERFACE3);
        id2c.setOperation(OP3);
        rrd2.getInvocations().add(id2c);
        
        RequestFaultDefinition rfd2=new RequestFaultDefinition();
        stod2.getRequestFaults().add(rfd2);
        
        InvocationDefinition id2b=new InvocationDefinition();
        id2b.setInterface(INTERFACE3);
        id2b.setOperation(OP3);
        rfd2.getInvocations().add(id2b);
        
        ServiceDefinition sd3=new ServiceDefinition();
        sd3.setInterface(INTERFACE3);
        
        OperationDefinition op3=new OperationDefinition();
        op3.setName(OP3);
        sd3.getOperations().add(op3);
        
        
        java.util.Set<ServiceDefinition> sds=new java.util.HashSet<ServiceDefinition>();
        sds.add(sd1);
        sds.add(sd2);
        sds.add(sd3);
        
        java.util.List<Situation> sits=new java.util.ArrayList<Situation>();
        
        Situation sit1=new Situation();
        sit1.setSeverity(Situation.Severity.Critical);
        sit1.setSubject(Situation.createSubject(INTERFACE1));
        sit1.setType("SLA Violation");
        sit1.setDescription("Service exceeded SLA");
        sits.add(sit1);
        
        Situation sit2=new Situation();
        sit2.setSeverity(Situation.Severity.High);
        sit2.setSubject(Situation.createSubject(INTERFACE1));
        sit2.setType("SLA Warning");
        sit2.setDescription("Service close to violating SLA");
        sits.add(sit2);
        
        Situation sit3=new Situation();
        sit3.setSeverity(Situation.Severity.High);
        sit3.setSubject(Situation.createSubject(INTERFACE2, 
        		OP2, SERVICE_TYPE2, FAULT2));
        sit3.setType("SLA Violation");
        sit3.setDescription("Service exceeded SLA");
        sits.add(sit3);
        
        ServiceGraph result=
                ServiceDependencyBuilder.buildGraph(sds, sits);
        
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
        
        ServiceNode sn1=result.getServiceNode(INTERFACE1);
        OperationNode opn1=sn1.getOperation(OP1);
        ServiceNode sn2=result.getServiceNode(INTERFACE2);
        OperationNode opn2=sn2.getOperation(OP2);
        ServiceNode sn3=result.getServiceNode(INTERFACE3);
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
        
        if (sn1.getSituations().size() != 2) {
            fail("sn1 does not have 2 situation: "+sn1.getSituations().size());
        }
        
        if (!sn1.getSituations().contains(sit1)) {
            fail("sn1 situation does not include sit1");
        }
        
        if (!sn1.getSituations().contains(sit2)) {
            fail("sn1 situation does not include sit2");
        }
        
        if (sn2.getSituations().size() != 0) {
            fail("sn2 should not have any situations: "+sn2.getSituations().size());
        }
        
        if (opn2.getSituations().size() != 1) {
            fail("Opn2 does not have 1 situation: "+opn2.getSituations().size());
        }
        
        if (opn2.getSituations().get(0) != sit3) {
            fail("Opn2 situation not sit3");
        }
    }
    
    @Test
    public void testMergedMetrics() {
        InvocationDefinition id1=new InvocationDefinition();
        InvocationDefinition id2=new InvocationDefinition();
        
        InvocationMetric im1=id1.getMetrics();
        im1.setCount(1);
        
        InvocationMetric im2=id2.getMetrics();
        im2.setCount(2);
        
        java.util.List<InvocationDefinition> ids=new java.util.ArrayList<InvocationDefinition>();
        ids.add(id1);
        ids.add(id2);
        
        InvocationMetric imresult=ServiceDependencyBuilder.getMergedMetrics(ids);
        
        if (imresult == null) {
            fail("No merged metric returned");
        }
        
        if (imresult.getCount() != 3) {
            fail("Expecting count of 3: "+imresult.getCount());
        }
    }
}
