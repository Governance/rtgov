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

    private static final String OPERATION_3 = "op3";
    private static final String OPERATION_1 = "op1";
    private static final String SERVICE_TYPE_1 = "st1";
    private static final String SERVICE_TYPE_2 = "st2";

    @Test
    public void testMergeIncorrectServiceType() {
        
        ServiceDefinition sd1=new ServiceDefinition();
        
        sd1.setInterface(SERVICE_TYPE_1);
        
        ServiceDefinition sd2=new ServiceDefinition();
        
        sd2.setInterface(SERVICE_TYPE_2);
        
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
        
        sd1.setInterface(SERVICE_TYPE_1);
        
        OperationDefinition op1=new OperationDefinition();
        op1.setName(OPERATION_1);
        sd1.getOperations().add(op1);
        
        ServiceDefinition sd2=new ServiceDefinition();
        
        sd2.setInterface(SERVICE_TYPE_1);
        
        OperationDefinition op2=new OperationDefinition();
        op2.setName(OPERATION_1);
        sd2.getOperations().add(op2);
        
        OperationDefinition op3=new OperationDefinition();
        op3.setName(OPERATION_3);
        sd2.getOperations().add(op3);
       
        
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
        
        if (sd.getOperations().size() != 2) {
            fail("Should be two ops: "+sd1.getOperations().size());
        }
        
        if (sd.getOperation(OPERATION_1) == null) {
            fail("Failed to get op1");
        }
        
        if (sd.getOperation(OPERATION_3) == null) {
            fail("Failed to get op3");
        }
        
        if (sd.getMerged().size() != 2) {
            fail("Expecting 2 merged: "+sd.getMerged().size());
        }
        
        if (sd.getMerged().get(0) != sd1) {
            fail("Merged 0 should be sd1");
        }
        
        if (sd.getMerged().get(1) != sd2) {
            fail("Merged 1 should be sd2");
        }
    }

    @Test
    public void testMergeWithoutContext() {
        
        ServiceDefinition sd1=new ServiceDefinition();
        
        sd1.setInterface(SERVICE_TYPE_1);
        
        OperationDefinition op1=new OperationDefinition();
        op1.setName(OPERATION_1);
        sd1.getOperations().add(op1);
        
        ServiceDefinition sd2=new ServiceDefinition();
        
        sd2.setInterface(SERVICE_TYPE_1);
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
        
        sd1.setInterface(SERVICE_TYPE_1);
        
        ServiceDefinition sd2=new ServiceDefinition();
        
        sd2.setInterface(SERVICE_TYPE_1);
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
        
        sd1.setInterface(SERVICE_TYPE_1);
        
        OperationDefinition op1=new OperationDefinition();
        op1.setName(OPERATION_1);
        sd1.getOperations().add(op1);
        
        OperationImplDefinition opid1=new OperationImplDefinition();
        op1.getImplementations().add(opid1);
        
        RequestResponseDefinition rrd1=new RequestResponseDefinition();
        opid1.setRequestResponse(rrd1);
        rrd1.getMetrics().setCount(1);
        rrd1.getMetrics().setAverage(1250);        
        
        ServiceDefinition sd2=new ServiceDefinition();
        
        sd2.setInterface(SERVICE_TYPE_1);
        
        OperationDefinition op2=new OperationDefinition();
        op2.setName(OPERATION_1);
        sd2.getOperations().add(op2);
        
        OperationImplDefinition opid2=new OperationImplDefinition();
        op2.getImplementations().add(opid2);
        
        RequestResponseDefinition rrd2=new RequestResponseDefinition();
        opid2.setRequestResponse(rrd2);
        rrd2.getMetrics().setCount(1);
        rrd2.getMetrics().setAverage(400);        
        
        OperationDefinition op3=new OperationDefinition();
        op3.setName(OPERATION_3);
        sd2.getOperations().add(op3);
        
        OperationImplDefinition opid3=new OperationImplDefinition();
        op3.getImplementations().add(opid3);
        
        RequestResponseDefinition rrd3=new RequestResponseDefinition();
        opid3.setRequestResponse(rrd3);
        rrd3.getMetrics().setCount(1);
        rrd3.getMetrics().setAverage(700);        
       
        
        ServiceDefinition sd=sd1.shallowCopy();
        
        try {
            sd.merge(sd1, false);  
            sd.merge(sd2, false);  
        } catch (Exception e) {
            fail("Failed to merge: "+e);
        }
        
        if (sd.getOperations().size() != 2) {
            fail("Should be two ops: "+sd1.getOperations().size());
        }
        
        if (sd.getOperation(OPERATION_1) == null) {
            fail("Failed to get op1");
        }
        
        if (sd.getOperation(OPERATION_3) == null) {
            fail("Failed to get op3");
        }
        
        OperationDefinition od=sd.getOperation(OPERATION_1);
        
        if (od.getMerged().size() != 2) {
            fail("Should be two merged ops");
        }
        
        if (od.getImplementations().size() != 1) {
            fail("Should only be 1 op impl defn");
        }
        
        OperationImplDefinition oid=od.getImplementations().get(0);
        
        if (oid.getMerged().size() != 2) {
            fail("Op impl defn should have two merged entries: "+oid.getMerged().size());
        }
        
        RequestResponseDefinition rrd=oid.getRequestResponse();
        
        if (rrd == null) {
            fail("Request/response defn is null");
        }
        
        if (rrd.getMerged().size() != 2) {
            fail("Req/resp defn should have two merged entries: "+rrd.getMerged().size());
        }
        
        if (rrd.getMerged().get(0).getMetrics().getCount() != 1) {
            fail("First rrd count should be 1: "+rrd.getMerged().get(0).getMetrics().getCount());
        }
        
        if (rrd.getMerged().get(0).getMetrics().getAverage() != 1250) {
            fail("First rrd average should be 1250: "+rrd.getMerged().get(0).getMetrics().getAverage());
        }
        
        if (rrd.getMerged().get(1).getMetrics().getCount() != 1) {
            fail("Second rrd count should be 1: "+rrd.getMerged().get(1).getMetrics().getCount());
        }
        
        if (rrd.getMerged().get(1).getMetrics().getAverage() != 400) {
            fail("Second rrd average should be 400: "+rrd.getMerged().get(1).getMetrics().getAverage());
        }
        
        if (rrd.getMetrics().getCount() != 2) {
            fail("Merged count should be 2: "+rrd.getMetrics().getCount());
        }
        
        if (rrd.getMetrics().getAverage() != 825) {
            fail("Merged average should be 825: "+rrd.getMetrics().getAverage());
        }        
    }
}
