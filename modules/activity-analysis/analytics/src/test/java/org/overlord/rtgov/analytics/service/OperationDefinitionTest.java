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
        OperationImplDefinition stod1=new OperationImplDefinition();
        od1.getImplementations().add(stod1);
        
        OperationDefinition od2=new OperationDefinition();
        OperationImplDefinition stod2=new OperationImplDefinition();
        od2.getImplementations().add(stod2);
        
        RequestResponseDefinition rr1=new RequestResponseDefinition();
        stod2.setRequestResponse(rr1);
        
        od1.merge(od2);  
        
        if (stod1.getRequestResponse() == null) {
            fail("Failed to req/resp");
        }
    }

    @Test
    public void testMergeMissingReqFault() {
        
        OperationDefinition od1=new OperationDefinition();
        OperationImplDefinition stod1=new OperationImplDefinition();
        od1.getImplementations().add(stod1);
        
        RequestResponseDefinition rr1=new RequestResponseDefinition();
        stod1.setRequestResponse(rr1);

        RequestFaultDefinition rf1=new RequestFaultDefinition();
        rf1.setFault(FAULT_1);
        stod1.getRequestFaults().add(rf1);

        OperationDefinition od2=new OperationDefinition();
        OperationImplDefinition stod2=new OperationImplDefinition();
        od2.getImplementations().add(stod2);
        
        RequestFaultDefinition rf2=new RequestFaultDefinition();
        rf2.setFault(FAULT_2);
        stod2.getRequestFaults().add(rf2);
        
        od1.merge(od2);  
        
        if (stod1.getRequestResponse() == null) {
            fail("Failed to req/resp");
        }
        
        if (stod1.getRequestFaults().size() != 2) {
            fail("Expecting 2 faults: "+od1.getImplementations().size());
        }
        
        if (stod1.getRequestFault(FAULT_1) == null) {
            fail("Failed to get fault 1");
        }
        
        if (stod1.getRequestFault(FAULT_2) == null) {
            fail("Failed to get fault 2");
        }
    }
}
