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
package org.overlord.bam.analytics.service.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.bam.analytics.service.RequestFaultDefinition;
import org.overlord.bam.analytics.service.RequestResponseDefinition;
import org.overlord.bam.analytics.service.OperationDefinition;
import org.overlord.bam.analytics.service.ServiceDefinition;

public class ServiceDefinitionUtilTest {

    @Test
    public void testSerializeServiceDefiniton() {
        
        ServiceDefinition st1=new ServiceDefinition();
        st1.setServiceType("st1");
        
        OperationDefinition op1=new OperationDefinition();
        st1.getOperations().add(op1);
        
        op1.setOperation("op1");
        
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
        
        op1.setOperation("op1");
        
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
        
        if (!opresult.getOperation().equals(op1.getOperation())) {
            fail("Operation mismatch");
        }
    }
}
