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
        
        OperationDefinition op1=new OperationDefinition();
        op1.setName(OPERATION_1);
        
        ServiceDefinition sd2=new ServiceDefinition();
        
        sd2.setServiceType(SERVICE_TYPE_1);
        
        OperationDefinition op2=new OperationDefinition();
        op2.setName(OPERATION_1);
        sd2.getOperations().add(op2);
        
        OperationDefinition op3=new OperationDefinition();
        op3.setName(OPERATION_3);
        sd2.getOperations().add(op3);
       
        try {
            sd1.merge(sd2, false);  
        } catch (Exception e) {
            fail("Failed to merge: "+e);
        }
        
        if (sd1.getContext().size() != 0) {
            fail("Should be 0 context: "+sd1.getContext().size());
        }
        
        if (sd1.getOperations().size() != 2) {
            fail("Should be two ops: "+sd1.getOperations().size());
        }
        
        if (sd1.getOperation(OPERATION_1) == null) {
            fail("Failed to get op1");
        }
        
        if (sd1.getOperation(OPERATION_3) == null) {
            fail("Failed to get op3");
        }
    }

    @Test
    public void testMergeWithoutContext() {
        
        ServiceDefinition sd1=new ServiceDefinition();
        
        sd1.setServiceType(SERVICE_TYPE_1);
        
        OperationDefinition op1=new OperationDefinition();
        op1.setName(OPERATION_1);
        
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
}
