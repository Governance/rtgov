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
package org.overlord.rtgov.content.epn;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.model.soa.RequestReceived;
import org.overlord.rtgov.activity.model.soa.RequestSent;
import org.overlord.rtgov.activity.model.soa.ResponseReceived;
import org.overlord.rtgov.activity.model.soa.ResponseSent;
import org.overlord.rtgov.analytics.service.InterfaceDefinition;
import org.overlord.rtgov.analytics.service.InvocationDefinition;
import org.overlord.rtgov.analytics.service.InvocationMetric;
import org.overlord.rtgov.analytics.service.OperationDefinition;
import org.overlord.rtgov.analytics.service.ServiceDefinition;
import org.overlord.rtgov.content.epn.ServiceDefinitionProcessor;

public class ServiceDefinitionProcessorTest {

    private static final String OPERATION_1 = "op1";
    private static final String OPERATION_2 = "op2";
    private static final String INTERFACE_1="intf1";
    private static final String INTERFACE_2="intf2";
    private static final String SERVICE_TYPE_1="st1";
    private static final String SERVICE_TYPE_2="st2";

    @Test
    public void testServiceDefinitionsDerivedFromAU() {
        // Create example activity events
        ActivityUnit au=new ActivityUnit();
        
        RequestReceived rqr1=new RequestReceived();
        rqr1.setInterface(INTERFACE_1);
        rqr1.setOperation(OPERATION_1);
        rqr1.setServiceType(SERVICE_TYPE_1);
        rqr1.setMessageId("1");
        rqr1.setTimestamp(10);
        
        au.getActivityTypes().add(rqr1);

        RequestSent rqs2=new RequestSent();
        rqs2.setInterface(INTERFACE_2);
        rqs2.setOperation(OPERATION_2);
        rqs2.setServiceType(SERVICE_TYPE_2);
        rqs2.setMessageId("2");
        rqs2.setTimestamp(15);
        
        au.getActivityTypes().add(rqs2);

        RequestReceived rqr3=new RequestReceived();
        rqr3.setInterface(INTERFACE_2);
        rqr3.setOperation(OPERATION_2);
        rqr3.setServiceType(SERVICE_TYPE_2);
        rqr3.setMessageId("3");
        rqr3.setTimestamp(21);
        
        au.getActivityTypes().add(rqr3);

        ResponseSent rps3=new ResponseSent();
        rps3.setInterface(INTERFACE_2);
        rps3.setOperation(OPERATION_2);
        rps3.setServiceType(SERVICE_TYPE_2);
        rps3.setMessageId("4");
        rps3.setReplyToId("3");
        rps3.setTimestamp(24);
        
        au.getActivityTypes().add(rps3);

        ResponseReceived rpr2=new ResponseReceived();
        rpr2.setInterface(INTERFACE_2);
        rpr2.setOperation(OPERATION_2);
        rpr2.setServiceType(SERVICE_TYPE_2);
        rpr2.setMessageId("5");
        rpr2.setReplyToId("2");
        rpr2.setTimestamp(36);
        
        au.getActivityTypes().add(rpr2);

        ResponseSent rps1=new ResponseSent();
        rps1.setInterface(INTERFACE_1);
        rps1.setOperation(OPERATION_1);
        rps1.setServiceType(SERVICE_TYPE_1);
        rps1.setMessageId("6");
        rps1.setReplyToId("1");
        rps1.setTimestamp(40);
        
        au.getActivityTypes().add(rps1);
        
        // Create service definition
        ServiceDefinitionProcessor processor=new ServiceDefinitionProcessor();
        
        java.io.Serializable result=null;
        
        try {
            result = processor.process(null, au, 1);
        } catch (Exception e) {
            fail("Failed to process activity unit: "+e);
        }
              
        if (result == null) {
            fail("Result is null");
        }
        
        if (!(result instanceof java.util.List)){
            fail("Result is not a list");
        }
        
        @SuppressWarnings("unchecked")
        java.util.List<ServiceDefinition> sdefs=
                        (java.util.List<ServiceDefinition>)result;
        
        if (sdefs.size() != 2) {
            fail("Two definition expected: "+sdefs.size());
        }
        
        ServiceDefinition sdef1=null;
        ServiceDefinition sdef2=null;
        
        for (ServiceDefinition sd : sdefs) {
            if (sd.getServiceType().equals(SERVICE_TYPE_1)) {
                sdef1 = sd;
            } else if (sd.getServiceType().equals(SERVICE_TYPE_2)) {
                sdef2 = sd;
            }
        }
        
        if (sdef1 == null) {
            fail("Service type 1 definition not found");
        }
        
        if (sdef2 == null) {
            fail("Service type 2 definition not found");
        }
        
        if (sdef1.getInterfaces().size() != 1) {
            fail("Only 1 interface expected for def 1: "+sdef1.getInterfaces().size());
        }
        
        if (sdef2.getInterfaces().size() != 1) {
            fail("Only 1 interface expected for def 2: "+sdef2.getInterfaces().size());
        }
        
        InterfaceDefinition idef1=sdef1.getInterfaces().get(0);
        InterfaceDefinition idef2=sdef2.getInterfaces().get(0);
        
        if (!idef1.getInterface().equals(INTERFACE_1)) {
            fail("Service interface 1 incorrect");
        }
        
        if (!idef2.getInterface().equals(INTERFACE_2)) {
            fail("Service interface 2 incorrect");
        }
        
        if (idef1.getOperations().size() != 1) {
            fail("Only 1 operation expected for def 1: "+idef1.getOperations().size());
        }
        
        if (idef2.getOperations().size() != 1) {
            fail("Only 1 operation expected for def 2: "+idef2.getOperations().size());
        }
        
        
        OperationDefinition op1=idef1.getOperation(OPERATION_1);
        
        if (op1 == null) {
            fail("Failed to retrieve op");
        }
        
        if (op1.getRequestResponse() == null) {
            fail("Request/response not found");
        }
        
        if (op1.getRequestFaults().size() > 0) {
            fail("No faults should have occurred");
        }
        
        if (op1.getRequestResponse().getInvocations().size() != 1) {
            fail("One external invocations expected");
        }
        
        InvocationDefinition id1=op1.getRequestResponse().getInvocation(INTERFACE_2,
                                OPERATION_2, null);
        
        if (id1 == null) {
            fail("Failed to get invocation definition 1");
        }
        
        InvocationMetric metrics1=id1.getMetrics();
        
        if (metrics1.getAverage() != 21) {
            fail("Average not 21: "+metrics1.getAverage());
        }
        
        if (metrics1.getMin() != 21) {
            fail("Min not 21: "+metrics1.getMin());
        }
        
        if (metrics1.getMax() != 21) {
            fail("Max not 21: "+metrics1.getMax());
        }
        
        if (metrics1.getCount() != 1) {
            fail("Count not 1: "+metrics1.getCount());
        }
        
        // Check external invoked operation details
        OperationDefinition op2=idef2.getOperation(OPERATION_2);
        
        if (op2 == null) {
            fail("Failed to retrieve op 2");
        }
        
        if (op2.getRequestResponse() == null) {
            fail("Request/response not found");
        }
        
        if (op2.getRequestFaults().size() > 0) {
            fail("No faults should have occurred");
        }
        
        if (op2.getRequestResponse().getInvocations().size() != 0) {
            fail("No external invocations expected");
        }
        
        InvocationMetric metrics2=op2.getRequestResponse().getMetrics();
        
        if (metrics2.getAverage() != 3) {
            fail("Average not 3: "+metrics2.getAverage());
        }
        
        if (metrics2.getMin() != 3) {
            fail("Min not 3: "+metrics2.getMin());
        }
        
        if (metrics2.getMax() != 3) {
            fail("Max not 3: "+metrics2.getMax());
        }
        
        if (metrics2.getCount() != 1) {
            fail("Count not 1: "+metrics2.getCount());
        }

    }

}
