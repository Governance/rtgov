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
package org.overlord.rtgov.service.dependency.layout;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.analytics.service.InvocationDefinition;
import org.overlord.rtgov.analytics.service.OperationDefinition;
import org.overlord.rtgov.analytics.service.RequestFaultDefinition;
import org.overlord.rtgov.analytics.service.RequestResponseDefinition;
import org.overlord.rtgov.analytics.service.ServiceDefinition;
import org.overlord.rtgov.service.dependency.ServiceDependencyBuilder;
import org.overlord.rtgov.service.dependency.ServiceGraph;
import org.overlord.rtgov.service.dependency.ServiceNode;

public class ServiceGraphLayoutImplTest {

    private static final String FAULT2 = "Fault2";
    private static final String OP4 = "op4";
    private static final String OP3 = "op3";
    private static final String OP2 = "op2";
    private static final String OP1 = "op1";
    private static final String SERVICE_TYPE1 = "serviceType1";
    private static final String SERVICE_TYPE2 = "serviceType2";
    private static final String SERVICE_TYPE3 = "serviceType3";
    private static final String SERVICE_TYPE4 = "serviceType4";

    @Test
    public void testLayoutGraph() {
        ServiceDefinition sd1=new ServiceDefinition();
        sd1.setServiceType(SERVICE_TYPE1);
        
        OperationDefinition op1=new OperationDefinition();
        op1.setName(OP1);
        sd1.getOperations().add(op1);
        
        RequestResponseDefinition rrd1=new RequestResponseDefinition();
        op1.setRequestResponse(rrd1);
        
        InvocationDefinition id1=new InvocationDefinition();
        id1.setServiceType(SERVICE_TYPE2);
        id1.setOperation(OP2);
        rrd1.getInvocations().add(id1);
        
        InvocationDefinition id1b=new InvocationDefinition();
        id1b.setServiceType(SERVICE_TYPE4);
        id1b.setOperation(OP4);
        rrd1.getInvocations().add(id1b);
        
        ServiceDefinition sd2=new ServiceDefinition();
        sd2.setServiceType(SERVICE_TYPE2);
        
        OperationDefinition op2=new OperationDefinition();
        op2.setName(OP2);
        sd2.getOperations().add(op2);
        
        RequestResponseDefinition rrd2=new RequestResponseDefinition();
        op2.setRequestResponse(rrd2);
        
        InvocationDefinition id2c=new InvocationDefinition();
        id2c.setServiceType(SERVICE_TYPE3);
        id2c.setOperation(OP3);
        rrd2.getInvocations().add(id2c);
        
        RequestFaultDefinition rfd2=new RequestFaultDefinition();
        rfd2.setFault(FAULT2);
        op2.getRequestFaults().add(rfd2);
        
        InvocationDefinition id2b=new InvocationDefinition();
        id2b.setServiceType(SERVICE_TYPE3);
        id2b.setOperation(OP3);
        rfd2.getInvocations().add(id2b);
        
        ServiceDefinition sd3=new ServiceDefinition();
        sd3.setServiceType(SERVICE_TYPE3);
        
        OperationDefinition op3=new OperationDefinition();
        op3.setName(OP3);
        sd3.getOperations().add(op3);
        
        ServiceDefinition sd4=new ServiceDefinition();
        sd4.setServiceType(SERVICE_TYPE4);
        
        OperationDefinition op4=new OperationDefinition();
        op4.setName(OP4);
        sd4.getOperations().add(op4);
        
        
        java.util.Set<ServiceDefinition> sds=new java.util.HashSet<ServiceDefinition>();
        sds.add(sd1);
        sds.add(sd2);
        sds.add(sd3);
        sds.add(sd4);
        
        ServiceGraph graph=
                ServiceDependencyBuilder.buildGraph(sds, null);
        
        if (graph == null) {
            fail("Graph is null");
        }
        
        ServiceGraphLayoutImpl layout=new ServiceGraphLayoutImpl();
        
        layout.layout(graph);
        
        // Check some of the dimensions
        ServiceNode sn1=graph.getServiceNode(sd1.getServiceType());
        ServiceNode sn2=graph.getServiceNode(sd2.getServiceType());
        ServiceNode sn3=graph.getServiceNode(sd3.getServiceType());
        ServiceNode sn4=graph.getServiceNode(sd4.getServiceType());
        
        int sn1x=(Integer)sn1.getProperties().get(ServiceGraphLayout.X_POSITION);
        int sn1y=(Integer)sn1.getProperties().get(ServiceGraphLayout.Y_POSITION);
        
        int sn2x=(Integer)sn2.getProperties().get(ServiceGraphLayout.X_POSITION);
        int sn2y=(Integer)sn2.getProperties().get(ServiceGraphLayout.Y_POSITION);
        
        int sn3x=(Integer)sn3.getProperties().get(ServiceGraphLayout.X_POSITION);
        int sn3y=(Integer)sn3.getProperties().get(ServiceGraphLayout.Y_POSITION);
        
        int sn4x=(Integer)sn4.getProperties().get(ServiceGraphLayout.X_POSITION);
        int sn4y=(Integer)sn4.getProperties().get(ServiceGraphLayout.Y_POSITION);
        int sn4h=(Integer)sn4.getProperties().get(ServiceGraphLayout.HEIGHT);
        
        if (sn1x != ServiceGraphLayoutImpl.SERVICE_INITIAL_HORIZONTAL_PADDING) {
            fail("sn1x incorrect");
        }
        
        if (sn1y != ServiceGraphLayoutImpl.SERVICE_VERTICAL_PADDING) {
            fail("sn1y incorrect");
        }
        
        int val=(ServiceGraphLayoutImpl.SERVICE_INITIAL_HORIZONTAL_PADDING
                +ServiceGraphLayoutImpl.SERVICE_HORIZONTAL_PADDING
                +ServiceGraphLayoutImpl.SERVICE_WIDTH);
        
         if (sn2x != val) {
            fail("sn2x incorrect");
        }
        
        val = (2*ServiceGraphLayoutImpl.SERVICE_VERTICAL_PADDING
                 +sn4h);
         
        if (sn2y != val) {
            fail("sn2y incorrect");
        }

        val = (ServiceGraphLayoutImpl.SERVICE_INITIAL_HORIZONTAL_PADDING
                +(2*ServiceGraphLayoutImpl.SERVICE_HORIZONTAL_PADDING)
                +(2*ServiceGraphLayoutImpl.SERVICE_WIDTH));
        
        if (sn3x != val) {
            fail("sn3x incorrect");
        }
        
        val = (2*ServiceGraphLayoutImpl.SERVICE_VERTICAL_PADDING
                +sn4h);
        
       if (sn3y != val) {
           fail("sn3y incorrect");
       }

        val = (ServiceGraphLayoutImpl.SERVICE_INITIAL_HORIZONTAL_PADDING
                +ServiceGraphLayoutImpl.SERVICE_HORIZONTAL_PADDING
                +ServiceGraphLayoutImpl.SERVICE_WIDTH);
        
        if (sn4x != val) {
            fail("sn4x incorrect");
        }
        
        if (sn4y != ServiceGraphLayoutImpl.SERVICE_VERTICAL_PADDING) {
            fail("sn4y incorrect");
        }
     }
}
