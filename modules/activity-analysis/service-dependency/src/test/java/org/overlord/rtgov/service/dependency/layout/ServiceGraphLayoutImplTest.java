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
package org.overlord.rtgov.service.dependency.layout;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.analytics.service.InterfaceDefinition;
import org.overlord.rtgov.analytics.service.InvocationDefinition;
import org.overlord.rtgov.analytics.service.OperationDefinition;
import org.overlord.rtgov.analytics.service.RequestFaultDefinition;
import org.overlord.rtgov.analytics.service.RequestResponseDefinition;
import org.overlord.rtgov.analytics.service.ServiceDefinition;
import org.overlord.rtgov.service.dependency.ServiceDependencyBuilder;
import org.overlord.rtgov.service.dependency.ServiceGraph;
import org.overlord.rtgov.service.dependency.ServiceNode;
import org.overlord.rtgov.service.dependency.layout.ServiceGraphLayout;
import org.overlord.rtgov.service.dependency.layout.ServiceGraphLayoutImpl;

public class ServiceGraphLayoutImplTest {

    private static final String FAULT2 = "Fault2";
    private static final String OP4 = "op4";
    private static final String OP3 = "op3";
    private static final String OP2 = "op2";
    private static final String OP1 = "op1";
    private static final String INTERFACE1 = "intf1";
    private static final String INTERFACE2 = "intf2";
    private static final String INTERFACE3 = "intf3";
    private static final String INTERFACE4 = "intf4";
    private static final String SERVICE_TYPE1 = "st1";
    private static final String SERVICE_TYPE2 = "st2";
    private static final String SERVICE_TYPE3 = "st3";
    private static final String SERVICE_TYPE4 = "st4";

    @Test
    public void testLayoutGraph() {
        ServiceDefinition sd1=new ServiceDefinition();
        sd1.setServiceType(SERVICE_TYPE1);
        
        InterfaceDefinition idef1=new InterfaceDefinition();
        idef1.setInterface(INTERFACE1);
        sd1.getInterfaces().add(idef1);
        
        OperationDefinition op1=new OperationDefinition();
        op1.setName(OP1);
        idef1.getOperations().add(op1);
        
        RequestResponseDefinition rrd1=new RequestResponseDefinition();
        op1.setRequestResponse(rrd1);
        
        InvocationDefinition id1=new InvocationDefinition();
        id1.setInterface(INTERFACE2);
        id1.setOperation(OP2);
        rrd1.getInvocations().add(id1);
        
        InvocationDefinition id1b=new InvocationDefinition();
        id1b.setInterface(INTERFACE4);
        id1b.setOperation(OP4);
        rrd1.getInvocations().add(id1b);
        
        ServiceDefinition sd2=new ServiceDefinition();
        sd2.setServiceType(SERVICE_TYPE2);
        
        InterfaceDefinition idef2=new InterfaceDefinition();
        idef2.setInterface(INTERFACE2);
        sd2.getInterfaces().add(idef2);
        
        OperationDefinition op2=new OperationDefinition();
        op2.setName(OP2);
        idef2.getOperations().add(op2);
        
        RequestResponseDefinition rrd2=new RequestResponseDefinition();
        op2.setRequestResponse(rrd2);
        
        InvocationDefinition id2c=new InvocationDefinition();
        id2c.setInterface(INTERFACE3);
        id2c.setOperation(OP3);
        rrd2.getInvocations().add(id2c);
        
        RequestFaultDefinition rfd2=new RequestFaultDefinition();
        rfd2.setFault(FAULT2);
        op2.getRequestFaults().add(rfd2);
        
        InvocationDefinition id2b=new InvocationDefinition();
        id2b.setInterface(INTERFACE3);
        id2b.setOperation(OP3);
        rfd2.getInvocations().add(id2b);
        
        ServiceDefinition sd3=new ServiceDefinition();
        sd3.setServiceType(SERVICE_TYPE3);
        
        InterfaceDefinition idef3=new InterfaceDefinition();
        idef3.setInterface(INTERFACE3);
        sd3.getInterfaces().add(idef3);
        
        OperationDefinition op3=new OperationDefinition();
        op3.setName(OP3);
        idef3.getOperations().add(op3);
        
        ServiceDefinition sd4=new ServiceDefinition();
        sd4.setServiceType(SERVICE_TYPE4);
        
        InterfaceDefinition idef4=new InterfaceDefinition();
        idef4.setInterface(INTERFACE4);
        sd4.getInterfaces().add(idef4);       
        
        OperationDefinition op4=new OperationDefinition();
        op4.setName(OP4);
        idef4.getOperations().add(op4);
        
        
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
                
        if (sn2y != ServiceGraphLayoutImpl.SERVICE_VERTICAL_PADDING) {
            fail("sn2y incorrect");
        }

        val = (ServiceGraphLayoutImpl.SERVICE_INITIAL_HORIZONTAL_PADDING
                +(2*ServiceGraphLayoutImpl.SERVICE_HORIZONTAL_PADDING)
                +(2*ServiceGraphLayoutImpl.SERVICE_WIDTH));
        
        if (sn3x != val) {
            fail("sn3x incorrect");
        }
        
        if (sn3y != ServiceGraphLayoutImpl.SERVICE_VERTICAL_PADDING) {
            fail("sn3y incorrect");
        }

        val = (ServiceGraphLayoutImpl.SERVICE_INITIAL_HORIZONTAL_PADDING
                +ServiceGraphLayoutImpl.SERVICE_HORIZONTAL_PADDING
                +ServiceGraphLayoutImpl.SERVICE_WIDTH);
        
        if (sn4x != val) {
            fail("sn4x incorrect");
        }
        
        val = (2*ServiceGraphLayoutImpl.SERVICE_VERTICAL_PADDING
                +sn4h);
        
        if (sn4y != val) {
            fail("sn4y incorrect");
        }
    }
}
