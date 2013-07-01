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
package org.overlord.rtgov.analytics.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.analytics.service.OperationDefinition;
import org.overlord.rtgov.analytics.service.ServiceDefinition;

public class ServiceDefinitionTest {

    private static final String INTERFACE_1 = "Interface1";
    private static final String OPERATION_3 = "op3";
    private static final String OPERATION_1 = "op1";
    private static final String SERVICE_TYPE_1 = "st1";
    private static final String SERVICE_TYPE_2 = "st2";

    @Test
    public void testMergeIncorrectServiceType() {
        
        ServiceDefinition sd1=new ServiceDefinition();
        
        sd1.setServiceType(SERVICE_TYPE_1);
        
        ServiceDefinition sd2=new ServiceDefinition();
        
        sd2.setServiceType(SERVICE_TYPE_2);
        
        try {
            sd1.merge(sd2, false);
            
            fail("Should have thrown exception");
        } catch (Exception e) {
            // Should throw exception
        }
    }

    @Test
    public void testMergeOperations() {
        
        ServiceDefinition sd1=new ServiceDefinition();
        
        sd1.setServiceType(SERVICE_TYPE_1);
        
        InterfaceDefinition idef1=new InterfaceDefinition();
        sd1.getInterfaces().add(idef1);
        idef1.setInterface(INTERFACE_1);
        
        OperationDefinition op1=new OperationDefinition();
        op1.setName(OPERATION_1);
        idef1.getOperations().add(op1);
        
        ServiceDefinition sd2=new ServiceDefinition();
        
        sd2.setServiceType(SERVICE_TYPE_1);
        
        InterfaceDefinition idef2=new InterfaceDefinition();
        sd2.getInterfaces().add(idef2);
        idef2.setInterface(INTERFACE_1);
        
        OperationDefinition op2=new OperationDefinition();
        op2.setName(OPERATION_1);
        idef2.getOperations().add(op2);
        
        OperationDefinition op3=new OperationDefinition();
        op3.setName(OPERATION_3);
        idef2.getOperations().add(op3);
       
        
        ServiceDefinition sd=sd1.shallowCopy();
        
        try {
            sd.merge(sd1, false);  
            sd.merge(sd2, false);  
        } catch (Exception e) {
            fail("Failed to merge: "+e);
        }
        
        if (sd.getContext().size() != 0) {
            fail("Should be 0 context: "+sd1.getContext().size());
        }
        
        if (sd.getInterfaces().size() != 1) {
            fail("Expecting 1 interface: "+sd.getInterfaces().size());
        }
        
        InterfaceDefinition id=sd.getInterfaces().get(0);
        
        if (id.getOperations().size() != 2) {
            fail("Should be two ops: "+id.getOperations().size());
        }
        
        if (id.getOperation(OPERATION_1) == null) {
            fail("Failed to get op1");
        }
        
        if (id.getOperation(OPERATION_3) == null) {
            fail("Failed to get op3");
        }
        
        if (sd.getHistory().size() != 2) {
            fail("Expecting 2 merged: "+sd.getHistory().size());
        }
        
        if (sd.getHistory().get(0).equals(sd1.getMetrics())) {
            fail("Merged 0 should be sd1");
        }
        
        if (sd.getHistory().get(1).equals(sd2.getMetrics())) {
            fail("Merged 1 should be sd2");
        }
    }

    @Test
    public void testMergeWithoutContext() {
        
        ServiceDefinition sd1=new ServiceDefinition();
        
        sd1.setServiceType(SERVICE_TYPE_1);
        
        InterfaceDefinition idef1=new InterfaceDefinition();
        sd1.getInterfaces().add(idef1);
        idef1.setInterface(INTERFACE_1);
        
        OperationDefinition op1=new OperationDefinition();
        op1.setName(OPERATION_1);
        idef1.getOperations().add(op1);
        
        ServiceDefinition sd2=new ServiceDefinition();
        
        sd2.setServiceType(SERVICE_TYPE_1);
        sd2.getContext().add(new Context(Context.Type.Conversation, "c2"));
        
        try {
            sd1.merge(sd2, false);  
        } catch (Exception e) {
            fail("Failed to merge: "+e);
        }
        
        if (sd1.getContext().size() != 0) {
            fail("Should be 0 context: "+sd1.getContext().size());
        }
    }

    @Test
    public void testMergeWithContext() {
        
        ServiceDefinition sd1=new ServiceDefinition();
        
        sd1.setServiceType(SERVICE_TYPE_1);
        
        ServiceDefinition sd2=new ServiceDefinition();
        
        sd2.setServiceType(SERVICE_TYPE_1);
        sd2.getContext().add(new Context(Context.Type.Conversation, "c2"));
        
        try {
            sd1.merge(sd2, true);  
        } catch (Exception e) {
            fail("Failed to merge: "+e);
        }
        
        if (sd1.getContext().size() != 1) {
            fail("Should be 1 context: "+sd1.getContext().size());
        }
    }
    
    @Test
    public void testMergeMetrics() {
        
        ServiceDefinition sd1=new ServiceDefinition();
        
        sd1.setServiceType(SERVICE_TYPE_1);
        
        InterfaceDefinition idef1=new InterfaceDefinition();
        sd1.getInterfaces().add(idef1);
        idef1.setInterface(INTERFACE_1);
        
        OperationDefinition op1=new OperationDefinition();
        op1.setName(OPERATION_1);
        idef1.getOperations().add(op1);
        
        RequestResponseDefinition rrd1=new RequestResponseDefinition();
        op1.setRequestResponse(rrd1);
        rrd1.getMetrics().setCount(1);
        rrd1.getMetrics().setAverage(1250);        
        
        ServiceDefinition sd2=new ServiceDefinition();
        
        sd2.setServiceType(SERVICE_TYPE_1);
        
        InterfaceDefinition idef2=new InterfaceDefinition();
        sd2.getInterfaces().add(idef2);
        idef2.setInterface(INTERFACE_1);
        
        OperationDefinition op2=new OperationDefinition();
        op2.setName(OPERATION_1);
        idef2.getOperations().add(op2);
        
        RequestResponseDefinition rrd2=new RequestResponseDefinition();
        op2.setRequestResponse(rrd2);
        rrd2.getMetrics().setCount(1);
        rrd2.getMetrics().setAverage(400);        
        
        OperationDefinition op3=new OperationDefinition();
        op3.setName(OPERATION_3);
        idef2.getOperations().add(op3);
        
        RequestResponseDefinition rrd3=new RequestResponseDefinition();
        op3.setRequestResponse(rrd3);
        rrd3.getMetrics().setCount(1);
        rrd3.getMetrics().setAverage(700);        
       
        
        ServiceDefinition sd=sd1.shallowCopy();
        
        try {
            sd.merge(sd1, false);  
            sd.merge(sd2, false);  
        } catch (Exception e) {
            fail("Failed to merge: "+e);
        }
        
        if (sd.getInterfaces().size() != 1) {
            fail("Expecting 1 interface: "+sd.getInterfaces().size());
        }
        
        InterfaceDefinition idefres=sd.getInterfaces().get(0);
        
        if (idefres.getOperations().size() != 2) {
            fail("Should be two ops: "+idefres.getOperations().size());
        }
        
        if (idefres.getOperation(OPERATION_1) == null) {
            fail("Failed to get op1");
        }
        
        if (idefres.getOperation(OPERATION_3) == null) {
            fail("Failed to get op3");
        }
        
        OperationDefinition od=idefres.getOperation(OPERATION_1);
        
        if (od.getHistory().size() != 2) {
            fail("Should be two merged ops");
        }
        
        RequestResponseDefinition rrd=od.getRequestResponse();
        
        if (rrd == null) {
            fail("Request/response defn is null");
        }
        
        if (rrd.getMetrics().getCount() != 2) {
            fail("Merged count should be 2: "+rrd.getMetrics().getCount());
        }
        
        if (rrd.getMetrics().getAverage() != 825) {
            fail("Merged average should be 825: "+rrd.getMetrics().getAverage());
        }        
    }
}
