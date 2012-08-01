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
package org.overlord.bam.content.epn;

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
import org.overlord.bam.service.analytics.ServiceDefinition;

public class ServiceDefinitionProcessorTest {

    private static final String OPERATION_1 = "op1";
    private static final String OPERATION_2 = "op2";
    private static final String SERVICE_TYPE_1="ST1";
    private static final String SERVICE_TYPE_2="ST2";

    @Test
    public void testServiceDefinitionsDerivedFromAU() {
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
