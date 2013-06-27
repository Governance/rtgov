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
import org.overlord.rtgov.activity.model.ActivityTypeId;
import org.overlord.rtgov.analytics.service.InvocationDefinition;
import org.overlord.rtgov.analytics.service.MEPDefinition;

public class MEPDefinitionTest {

    private static final String OPERATION_1 = "op1";
    private static final String INTERFACE_1 = "intf1";
    private static final String OPERATION_2 = "op2";
    private static final String INTERFACE_2 = "intf2";

    @Test
    public void testMergeNoReqResp() {
        
        MEPDefinition mep1=new MEPDefinition() {};
        
        InvocationDefinition id1=new InvocationDefinition();
        id1.setInterface(INTERFACE_1);
        id1.setOperation(OPERATION_1);
        
        mep1.getInvocations().add(id1);
        
        MEPDefinition mep2=new MEPDefinition() {};
        
        InvocationDefinition id2=new InvocationDefinition();
        id2.setInterface(INTERFACE_1);
        id2.setOperation(OPERATION_1);
        
        mep2.getInvocations().add(id2);
        
        InvocationDefinition id3=new InvocationDefinition();
        id3.setInterface(INTERFACE_2);
        id3.setOperation(OPERATION_2);
        
        mep2.getInvocations().add(id3);
        
        mep1.merge(mep2);  
        
        if (mep1.getInvocations().size() != 2) {
            fail("Expecting 2 invocations: "+mep1.getInvocations().size());
        }
        
        if (mep1.getInvocation(INTERFACE_1, OPERATION_1, null) == null) {
            fail("Failed to get st1/op1");
        }
        
        if (mep1.getInvocation(INTERFACE_2, OPERATION_2, null) == null) {
            fail("Failed to get st2/op2");
        }
    }
    
    @Test
    public void testMergeClearReqRespId() {
        
        MEPDefinition mep1=new MEPDefinition() {};
        mep1.setRequestId(new ActivityTypeId());
        mep1.setResponseId(new ActivityTypeId());
        
        MEPDefinition mep2=new MEPDefinition() {};
        
        mep1.merge(mep2);  
        
        if (mep1.getRequestId() != null) {
            fail("Request id should be null");
        }
        
        if (mep1.getResponseId() != null) {
            fail("Response id should be null");
        }
    }
}
