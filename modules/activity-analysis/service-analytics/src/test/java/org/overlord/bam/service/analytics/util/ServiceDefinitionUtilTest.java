/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-11, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.bam.service.analytics.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.bam.activity.model.ActivityUnit;
import org.overlord.bam.activity.model.soa.RequestReceived;
import org.overlord.bam.activity.model.soa.RequestSent;
import org.overlord.bam.activity.model.soa.ResponseReceived;
import org.overlord.bam.activity.model.soa.ResponseSent;
import org.overlord.bam.service.analytics.InvocationDefinition;
import org.overlord.bam.service.analytics.InvocationMetric;
import org.overlord.bam.service.analytics.OperationDefinition;
import org.overlord.bam.service.analytics.RequestFaultDefinition;
import org.overlord.bam.service.analytics.RequestResponseDefinition;
import org.overlord.bam.service.analytics.ServiceDefinition;
import org.overlord.bam.service.analytics.util.ServiceDefinitionUtil;

public class ServiceDefinitionUtilTest {

    private static final String FAULT_1 = "fault1";
    private static final String OPERATION_1 = "op1";
    private static final String OPERATION_2 = "op2";
    private static final String SERVICE_TYPE_1="ST1";
    private static final String SERVICE_TYPE_2="ST2";

    @Test
    public void testSerializeServiceDefiniton() {
        
        ServiceDefinition st1=new ServiceDefinition();
        st1.setServiceType("st1");
        
        OperationDefinition op1=new OperationDefinition();
        st1.getOperations().add(op1);
        
        op1.setName("op1");
        
        RequestResponseDefinition nrd1=new RequestResponseDefinition();
        nrd1.getMetrics().setCount(10);
        nrd1.getMetrics().setAverage(1000);
        nrd1.getMetrics().setMin(500);
        nrd1.getMetrics().setMax(1500);
        nrd1.getMetrics().setCountChange(+5);
        nrd1.getMetrics().setAverageChange(+2);
        nrd1.getMetrics().setMinChange(-5);
        nrd1.getMetrics().setMaxChange(+20);
        
        op1.setRequestResponse(nrd1);
        
        RequestFaultDefinition frd1=new RequestFaultDefinition();
        frd1.setFault("fault1");
        
        frd1.getMetrics().setCount(20);
        frd1.getMetrics().setAverage(2000);
        frd1.getMetrics().setMin(1500);
        frd1.getMetrics().setMax(2500);
        frd1.getMetrics().setCountChange(-10);
        frd1.getMetrics().setAverageChange(+6);
        frd1.getMetrics().setMinChange(0);
        frd1.getMetrics().setMaxChange(+10);
        
        op1.getRequestFaults().add(frd1);
        
        try {
            byte[] b = ServiceDefinitionUtil.serializeServiceDefinition(st1);
            
            System.out.println("SERVICE DEFINITION: "+new String(b));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to serialize");
        }
    }
    
    @Test
    public void testDeserializeServiceDefiniton() {
        
        ServiceDefinition st1=new ServiceDefinition();
        st1.setServiceType("st1");
        
        OperationDefinition op1=new OperationDefinition();
        st1.getOperations().add(op1);
        
        op1.setName("op1");
        
        RequestResponseDefinition nrd1=new RequestResponseDefinition();
        nrd1.getMetrics().setCount(10);
        nrd1.getMetrics().setAverage(1000);
        nrd1.getMetrics().setMin(500);
        nrd1.getMetrics().setMax(1500);
        nrd1.getMetrics().setCountChange(+5);
        nrd1.getMetrics().setAverageChange(+2);
        nrd1.getMetrics().setMinChange(-5);
        nrd1.getMetrics().setMaxChange(+20);
        
        op1.setRequestResponse(nrd1);
        
        RequestFaultDefinition frd1=new RequestFaultDefinition();
        frd1.setFault("fault1");
        
        frd1.getMetrics().setCount(20);
        frd1.getMetrics().setAverage(2000);
        frd1.getMetrics().setMin(1500);
        frd1.getMetrics().setMax(2500);
        frd1.getMetrics().setCountChange(-10);
        frd1.getMetrics().setAverageChange(+6);
        frd1.getMetrics().setMinChange(0);
        frd1.getMetrics().setMaxChange(+10);
        
        op1.getRequestFaults().add(frd1);
        
        byte[] b=null;
        
        try {
            b = ServiceDefinitionUtil.serializeServiceDefinition(st1);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to serialize");
        }
       
        ServiceDefinition result=null;
        
        try {
            result = ServiceDefinitionUtil.deserializeServiceDefinition(b);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to serialize");
        }

        if (result == null) {
            fail("Failed to deserialize service definition");
        }
        
        if (!result.getServiceType().equals(st1.getServiceType())) {
            fail("Service type mismatch");
        }
        
        if (result.getOperations().size() != 1) {
            fail("Expecting 1 operation: "+result.getOperations().size());
        }
        
        OperationDefinition opresult=result.getOperations().get(0);
        
        if (!opresult.getName().equals(op1.getName())) {
            fail("Operation mismatch");
        }
    }

    @Test
    public void testSingleServiceInvokedNormal() {
        
        // Create example activity events
        ActivityUnit au=new ActivityUnit();
        
        RequestReceived rqr1=new RequestReceived();
        rqr1.setServiceType(SERVICE_TYPE_1);
        rqr1.setOperation(OPERATION_1);
        rqr1.setMessageId("1");
        rqr1.setTimestamp(10);
        
        au.getActivityTypes().add(rqr1);

        ResponseSent rps1=new ResponseSent();
        rps1.setServiceType(SERVICE_TYPE_1);
        rps1.setOperation(OPERATION_1);
        rps1.setMessageId("2");
        rps1.setReplyToId("1");
        rps1.setTimestamp(20);
        
        au.getActivityTypes().add(rps1);
        
        // Create service definition
        java.util.Collection<ServiceDefinition> sdefs=
                        ServiceDefinitionUtil.derive(au);
        
        if (sdefs.size() != 1) {
            fail("One definition expected: "+sdefs.size());
        }
        
        ServiceDefinition sdef=sdefs.iterator().next();
        
        if (!sdef.getServiceType().equals(SERVICE_TYPE_1)) {
            fail("Service type incorrect");
        }
        
        if (sdef.getOperations().size() != 1) {
            fail("Only 1 operation expected: "+sdef.getOperations().size());
        }
        
        OperationDefinition op=sdef.getOperation(OPERATION_1);
        
        if (op == null) {
            fail("Failed to retrieve op");
        }
        
        if (op.getRequestResponse() == null) {
            fail("Request/response not found");
        }
        
        if (op.getRequestFaults().size() > 0) {
            fail("No faults should have occurred");
        }
        
        if (op.getRequestResponse().getInvocations().size() > 0) {
            fail("No external invocations expected");
        }
        
        InvocationMetric metrics=op.getRequestResponse().getMetrics();
        
        if (metrics.getAverage() != 10) {
            fail("Average not 10: "+metrics.getAverage());
        }
        
        if (metrics.getMin() != 10) {
            fail("Min not 10: "+metrics.getMin());
        }
        
        if (metrics.getMax() != 10) {
            fail("Max not 10: "+metrics.getMax());
        }
        
        if (metrics.getCount() != 1) {
            fail("Count not 1: "+metrics.getCount());
        }
    }

    @Test
    public void testDoubleServiceInvokedNormal() {
        
        // Create example activity events
        ActivityUnit au=new ActivityUnit();
        
        RequestReceived rqr1=new RequestReceived();
        rqr1.setServiceType(SERVICE_TYPE_1);
        rqr1.setOperation(OPERATION_1);
        rqr1.setMessageId("1");
        rqr1.setTimestamp(10);
        
        au.getActivityTypes().add(rqr1);

        ResponseSent rps1=new ResponseSent();
        rps1.setServiceType(SERVICE_TYPE_1);
        rps1.setOperation(OPERATION_1);
        rps1.setMessageId("2");
        rps1.setReplyToId("1");
        rps1.setTimestamp(20);
        
        au.getActivityTypes().add(rps1);
        
        RequestReceived rqr2=new RequestReceived();
        rqr2.setServiceType(SERVICE_TYPE_1);
        rqr2.setOperation(OPERATION_1);
        rqr2.setMessageId("3");
        rqr2.setTimestamp(30);
        
        au.getActivityTypes().add(rqr2);

        ResponseSent rps2=new ResponseSent();
        rps2.setServiceType(SERVICE_TYPE_1);
        rps2.setOperation(OPERATION_1);
        rps2.setMessageId("4");
        rps2.setReplyToId("3");
        rps2.setTimestamp(50);
        
        au.getActivityTypes().add(rps2);
        
        // Create service definition
        java.util.Collection<ServiceDefinition> sdefs=
                ServiceDefinitionUtil.derive(au);
        
        if (sdefs.size() != 1) {
            fail("One definition expected: "+sdefs.size());
        }
        
        ServiceDefinition sdef=sdefs.iterator().next();
        
        if (!sdef.getServiceType().equals(SERVICE_TYPE_1)) {
            fail("Service type incorrect");
        }
        
        if (sdef.getOperations().size() != 1) {
            fail("Only 1 operation expected: "+sdef.getOperations().size());
        }
        
        OperationDefinition op=sdef.getOperation(OPERATION_1);
        
        if (op == null) {
            fail("Failed to retrieve op");
        }
        
        if (op.getRequestResponse() == null) {
            fail("Request/response not found");
        }
        
        if (op.getRequestFaults().size() > 0) {
            fail("No faults should have occurred");
        }
        
        if (op.getRequestResponse().getInvocations().size() > 0) {
            fail("No external invocations expected");
        }
        
        InvocationMetric metrics=op.getRequestResponse().getMetrics();
        
        if (metrics.getAverage() != 15) {
            fail("Average not 15: "+metrics.getAverage());
        }
        
        if (metrics.getMin() != 10) {
            fail("Min not 10: "+metrics.getMin());
        }
        
        if (metrics.getMax() != 20) {
            fail("Max not 20: "+metrics.getMax());
        }
        
        if (metrics.getCount() != 2) {
            fail("Count not 2: "+metrics.getCount());
        }
    }

    @Test
    public void testSingleServiceInvokedFault() {
        
        // Create example activity events
        ActivityUnit au=new ActivityUnit();
        
        RequestReceived rqr1=new RequestReceived();
        rqr1.setServiceType(SERVICE_TYPE_1);
        rqr1.setOperation(OPERATION_1);
        rqr1.setFault(FAULT_1);
        rqr1.setMessageId("1");
        rqr1.setTimestamp(10);
        
        au.getActivityTypes().add(rqr1);

        ResponseSent rps1=new ResponseSent();
        rps1.setServiceType(SERVICE_TYPE_1);
        rps1.setOperation(OPERATION_1);
        rps1.setFault(FAULT_1);
        rps1.setMessageId("2");
        rps1.setReplyToId("1");
        rps1.setTimestamp(20);
        
        au.getActivityTypes().add(rps1);
        
        // Create service definition
        java.util.Collection<ServiceDefinition> sdefs=
                ServiceDefinitionUtil.derive(au);
        
        if (sdefs.size() != 1) {
            fail("One definition expected: "+sdefs.size());
        }
        
        ServiceDefinition sdef=sdefs.iterator().next();
        
        if (!sdef.getServiceType().equals(SERVICE_TYPE_1)) {
            fail("Service type incorrect");
        }
        
        if (sdef.getOperations().size() != 1) {
            fail("Only 1 operation expected: "+sdef.getOperations().size());
        }
        
        OperationDefinition op=sdef.getOperation(OPERATION_1);
        
        if (op == null) {
            fail("Failed to retrieve op");
        }
        
        if (op.getRequestResponse() != null) {
            fail("Request/response should be null");
        }
        
        if (op.getRequestFaults().size() != 1) {
            fail("One fault should have occurred");
        }
        
        RequestFaultDefinition rfd=op.getRequestFault(FAULT_1);
        
        if (rfd == null) {
            fail("Failed to retrieve fault");
        }
        
        if (rfd.getInvocations().size() > 0) {
            fail("No external invocations expected");
        }
        
        InvocationMetric metrics=rfd.getMetrics();
        
        if (metrics.getAverage() != 10) {
            fail("Average not 10: "+metrics.getAverage());
        }
        
        if (metrics.getMin() != 10) {
            fail("Min not 10: "+metrics.getMin());
        }
        
        if (metrics.getMax() != 10) {
            fail("Max not 10: "+metrics.getMax());
        }
        
        if (metrics.getCount() != 1) {
            fail("Count not 1: "+metrics.getCount());
        }
    }

    @Test
    public void testServiceInvokedWithExternalInvocations() {
        
        // Create example activity events
        ActivityUnit au=new ActivityUnit();
        
        RequestReceived rqr1=new RequestReceived();
        rqr1.setServiceType(SERVICE_TYPE_1);
        rqr1.setOperation(OPERATION_1);
        rqr1.setMessageId("1");
        rqr1.setTimestamp(10);
        
        au.getActivityTypes().add(rqr1);

        RequestSent rqs2=new RequestSent();
        rqs2.setServiceType(SERVICE_TYPE_2);
        rqs2.setOperation(OPERATION_2);
        rqs2.setMessageId("2");
        rqs2.setTimestamp(15);
        
        au.getActivityTypes().add(rqs2);

        ResponseReceived rpr2=new ResponseReceived();
        rpr2.setServiceType(SERVICE_TYPE_2);
        rpr2.setOperation(OPERATION_2);
        rpr2.setMessageId("3");
        rpr2.setReplyToId("2");
        rpr2.setTimestamp(21);
        
        au.getActivityTypes().add(rpr2);

        RequestSent rqs3=new RequestSent();
        rqs3.setServiceType(SERVICE_TYPE_2);
        rqs3.setOperation(OPERATION_2);
        rqs3.setMessageId("4");
        rqs3.setTimestamp(24);
        
        au.getActivityTypes().add(rqs3);

        ResponseReceived rpr3=new ResponseReceived();
        rpr3.setServiceType(SERVICE_TYPE_2);
        rpr3.setOperation(OPERATION_2);
        rpr3.setMessageId("5");
        rpr3.setReplyToId("4");
        rpr3.setTimestamp(36);
        
        au.getActivityTypes().add(rpr3);

        ResponseSent rps1=new ResponseSent();
        rps1.setServiceType(SERVICE_TYPE_1);
        rps1.setOperation(OPERATION_1);
        rps1.setMessageId("6");
        rps1.setReplyToId("1");
        rps1.setTimestamp(40);
        
        au.getActivityTypes().add(rps1);
        
        // Create service definition
        java.util.Collection<ServiceDefinition> sdefs=
                ServiceDefinitionUtil.derive(au);
        
        if (sdefs.size() != 1) {
            fail("One definition expected: "+sdefs.size());
        }
        
        ServiceDefinition sdef=sdefs.iterator().next();
        
        if (!sdef.getServiceType().equals(SERVICE_TYPE_1)) {
            fail("Service type incorrect");
        }
        
        if (sdef.getOperations().size() != 1) {
            fail("Only 1 operation expected: "+sdef.getOperations().size());
        }
        
        OperationDefinition op=sdef.getOperation(OPERATION_1);
        
        if (op == null) {
            fail("Failed to retrieve op");
        }
        
        if (op.getRequestResponse() == null) {
            fail("Request/response not found");
        }
        
        if (op.getRequestFaults().size() > 0) {
            fail("No faults should have occurred");
        }
        
        if (op.getRequestResponse().getInvocations().size() != 1) {
            fail("One external invocations expected");
        }
        
        InvocationDefinition id=op.getRequestResponse().getInvocation(SERVICE_TYPE_2,
                                OPERATION_2, null);
        
        if (id == null) {
            fail("Failed to get invocation definition");
        }
        
        InvocationMetric metrics=id.getMetrics();
        
        if (metrics.getAverage() != 9) {
            fail("Average not 9: "+metrics.getAverage());
        }
        
        if (metrics.getMin() != 6) {
            fail("Min not 6: "+metrics.getMin());
        }
        
        if (metrics.getMax() != 12) {
            fail("Max not 12: "+metrics.getMax());
        }
        
        if (metrics.getCount() != 2) {
            fail("Count not 2: "+metrics.getCount());
        }
    }

    @Test
    public void testServiceInvokedWithExternalInvocationsAndOtherService() {
        
        // Create example activity events
        ActivityUnit au=new ActivityUnit();
        
        RequestReceived rqr1=new RequestReceived();
        rqr1.setServiceType(SERVICE_TYPE_1);
        rqr1.setOperation(OPERATION_1);
        rqr1.setMessageId("1");
        rqr1.setTimestamp(10);
        
        au.getActivityTypes().add(rqr1);

        RequestSent rqs2=new RequestSent();
        rqs2.setServiceType(SERVICE_TYPE_2);
        rqs2.setOperation(OPERATION_2);
        rqs2.setMessageId("2");
        rqs2.setTimestamp(15);
        
        au.getActivityTypes().add(rqs2);

        RequestReceived rqr3=new RequestReceived();
        rqr3.setServiceType(SERVICE_TYPE_2);
        rqr3.setOperation(OPERATION_2);
        rqr3.setMessageId("3");
        rqr3.setTimestamp(21);
        
        au.getActivityTypes().add(rqr3);

        ResponseSent rps3=new ResponseSent();
        rps3.setServiceType(SERVICE_TYPE_2);
        rps3.setOperation(OPERATION_2);
        rps3.setMessageId("4");
        rps3.setReplyToId("3");
        rps3.setTimestamp(24);
        
        au.getActivityTypes().add(rps3);

        ResponseReceived rpr2=new ResponseReceived();
        rpr2.setServiceType(SERVICE_TYPE_2);
        rpr2.setOperation(OPERATION_2);
        rpr2.setMessageId("5");
        rpr2.setReplyToId("2");
        rpr2.setTimestamp(36);
        
        au.getActivityTypes().add(rpr2);

        ResponseSent rps1=new ResponseSent();
        rps1.setServiceType(SERVICE_TYPE_1);
        rps1.setOperation(OPERATION_1);
        rps1.setMessageId("6");
        rps1.setReplyToId("1");
        rps1.setTimestamp(40);
        
        au.getActivityTypes().add(rps1);
        
        // Create service definition
        java.util.Collection<ServiceDefinition> sdefs=
                ServiceDefinitionUtil.derive(au);
        
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
        
        if (!sdef1.getServiceType().equals(SERVICE_TYPE_1)) {
            fail("Service type 1 incorrect");
        }
        
        if (!sdef2.getServiceType().equals(SERVICE_TYPE_2)) {
            fail("Service type 2 incorrect");
        }
        
        if (sdef1.getOperations().size() != 1) {
            fail("Only 1 operation expected for def 1: "+sdef1.getOperations().size());
        }
        
        if (sdef2.getOperations().size() != 1) {
            fail("Only 1 operation expected for def 2: "+sdef2.getOperations().size());
        }
        
        OperationDefinition op1=sdef1.getOperation(OPERATION_1);
        
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
        
        InvocationDefinition id1=op1.getRequestResponse().getInvocation(SERVICE_TYPE_2,
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
        OperationDefinition op2=sdef2.getOperation(OPERATION_2);
        
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
