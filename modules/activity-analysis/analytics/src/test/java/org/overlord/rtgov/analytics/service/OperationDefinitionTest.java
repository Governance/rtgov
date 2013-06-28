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
import org.overlord.rtgov.analytics.service.OperationDefinition;
import org.overlord.rtgov.analytics.service.RequestFaultDefinition;
import org.overlord.rtgov.analytics.service.RequestResponseDefinition;

public class OperationDefinitionTest {

    private static final String FAULT_1 = "fault1";
    private static final String FAULT_2 = "fault2";

    @Test
    public void testMergeNoReqResp() {
        
        OperationDefinition od1=new OperationDefinition();
        
        OperationDefinition od2=new OperationDefinition();
        
        RequestResponseDefinition rr1=new RequestResponseDefinition();
        od2.setRequestResponse(rr1);
        
        od1.merge(od2);  
        
        if (od1.getRequestResponse() == null) {
            fail("Failed to req/resp");
        }
    }

    @Test
    public void testMergeMissingReqFault() {
        
        OperationDefinition od1=new OperationDefinition();
        
        RequestResponseDefinition rr1=new RequestResponseDefinition();
        od1.setRequestResponse(rr1);

        RequestFaultDefinition rf1=new RequestFaultDefinition();
        rf1.setFault(FAULT_1);
        od1.getRequestFaults().add(rf1);

        OperationDefinition od2=new OperationDefinition();
        
        RequestFaultDefinition rf2=new RequestFaultDefinition();
        rf2.setFault(FAULT_2);
        od2.getRequestFaults().add(rf2);
        
        od1.merge(od2);  
        
        if (od1.getRequestResponse() == null) {
            fail("Failed to req/resp");
        }
        
        if (od1.getRequestFaults().size() != 2) {
            fail("Expecting 2 faults: "+od1.getRequestFaults().size());
        }
        
        if (od1.getRequestFault(FAULT_1) == null) {
            fail("Failed to get fault 1");
        }
        
        if (od1.getRequestFault(FAULT_2) == null) {
            fail("Failed to get fault 2");
        }
    }
}
