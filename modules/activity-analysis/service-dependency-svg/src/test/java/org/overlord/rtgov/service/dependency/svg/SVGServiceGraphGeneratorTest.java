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
package org.overlord.rtgov.service.dependency.svg;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.analytics.service.InterfaceDefinition;
import org.overlord.rtgov.analytics.service.InvocationDefinition;
import org.overlord.rtgov.analytics.service.OperationDefinition;
import org.overlord.rtgov.analytics.service.RequestFaultDefinition;
import org.overlord.rtgov.analytics.service.RequestResponseDefinition;
import org.overlord.rtgov.analytics.service.ServiceDefinition;
import org.overlord.rtgov.analytics.situation.Situation;
import org.overlord.rtgov.service.dependency.ServiceDependencyBuilder;
import org.overlord.rtgov.service.dependency.ServiceGraph;
import org.overlord.rtgov.service.dependency.layout.ServiceGraphLayoutImpl;
import org.overlord.rtgov.service.dependency.presentation.Severity;
import org.overlord.rtgov.service.dependency.svg.SVGServiceGraphGenerator;

public class SVGServiceGraphGeneratorTest {

    private static final String FAULT2 = "Fault2";
    private static final String OP5 = "op5";
    private static final String OP4 = "op4";
    private static final String OP3 = "op3";
    private static final String OP2 = "op2";
    private static final String OP1 = "op1";
    private static final String INTERFACE1 = "intf1";
    private static final String INTERFACE2 = "intf2";
    private static final String INTERFACE3 = "intf3";
    private static final String INTERFACE4 = "intf4";
    private static final String SERVICE_TYPE1 = "serviceType1";
    private static final String SERVICE_TYPE2 = "serviceType2";
    private static final String SERVICE_TYPE3 = "serviceType3";
    private static final String SERVICE_TYPE4 = "serviceType4";

    //@Test
    public void testLoadMainTemplate() {
        if (new SVGServiceGraphGenerator().loadTemplate("main") == null) {
            fail("Failed to load SVG template");
        }
    }
    
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
        
        id1.getMetrics().setCount(5);
        id1.getMetrics().setFaults(3);
        id1.getMetrics().setAverage(200);
        id1.getMetrics().setMax(220);
        id1.getMetrics().setMin(140);
        
        InvocationDefinition id1b=new InvocationDefinition();
        id1b.setInterface(INTERFACE4);
        id1b.setOperation(OP4);
        rrd1.getInvocations().add(id1b);
        
        id1b.getMetrics().setCount(8);
        id1b.getMetrics().setFaults(2);
        id1b.getMetrics().setAverage(250);
        id1b.getMetrics().setMax(255);
        id1b.getMetrics().setMin(140);
        
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
        
        rrd2.getMetrics().setCount(2);
        rrd2.getMetrics().setAverage(100);
        rrd2.getMetrics().setMax(200);
        rrd2.getMetrics().setMin(20);
        
        InvocationDefinition id2c=new InvocationDefinition();
        id2c.setInterface(INTERFACE3);
        id2c.setOperation(OP3);
        rrd2.getInvocations().add(id2c);
        
        id2c.getMetrics().setCount(2);
        id2c.getMetrics().setAverage(100);
        id2c.getMetrics().setMax(200);
        id2c.getMetrics().setMin(20);
        
        RequestFaultDefinition rfd2=new RequestFaultDefinition();
        rfd2.setFault(FAULT2);
        op2.getRequestFaults().add(rfd2);
        
        rfd2.getMetrics().setCount(3);
        rfd2.getMetrics().setFaults(3);
        rfd2.getMetrics().setAverage(80);
        rfd2.getMetrics().setMax(180);
        rfd2.getMetrics().setMin(50);

        InvocationDefinition id2b=new InvocationDefinition();
        id2b.setInterface(INTERFACE3);
        id2b.setOperation(OP3);
        rfd2.getInvocations().add(id2b);
        
        id2b.getMetrics().setCount(1);
        id2b.getMetrics().setAverage(90);
        id2b.getMetrics().setMax(90);
        id2b.getMetrics().setMin(90);
        
        ServiceDefinition sd3=new ServiceDefinition();
        sd3.setServiceType(SERVICE_TYPE3);
        
        InterfaceDefinition idef3=new InterfaceDefinition();
        idef3.setInterface(INTERFACE3);
        sd3.getInterfaces().add(idef3);
        
        OperationDefinition op3=new OperationDefinition();
        op3.setName(OP3);
        idef3.getOperations().add(op3);
        
        op3.getMetrics().setCount(3);
        op3.getMetrics().setAverage(80);
        op3.getMetrics().setMax(180);
        op3.getMetrics().setMin(50);
                
        ServiceDefinition sd4=new ServiceDefinition();
        sd4.setServiceType(SERVICE_TYPE4);

        InterfaceDefinition idef4=new InterfaceDefinition();
        idef4.setInterface(INTERFACE4);
        sd4.getInterfaces().add(idef4);
        
        OperationDefinition op4=new OperationDefinition();
        op4.setName(OP4);
        idef4.getOperations().add(op4);
        
        OperationDefinition op5=new OperationDefinition();
        op5.setName(OP5);
        idef4.getOperations().add(op5);
                
        java.util.Set<ServiceDefinition> sds=new java.util.HashSet<ServiceDefinition>();
        sds.add(sd1);
        sds.add(sd2);
        sds.add(sd3);
        sds.add(sd4);
        
        java.util.List<Situation> sits=new java.util.ArrayList<Situation>();
        
        Situation sit1=new Situation();
        sit1.setSeverity(Situation.Severity.Critical);
        sit1.setSubject(SERVICE_TYPE1);
        sit1.setType("SLA Violation");
        sit1.setDescription("Service exceeded SLA");
        sits.add(sit1);
        
        Situation sit2=new Situation();
        sit2.setSeverity(Situation.Severity.High);
        sit2.setSubject(SERVICE_TYPE1);
        sit2.setType("SLA Warning");
        sit2.setDescription("Service close to violating SLA");
        sits.add(sit2);
        
        Situation sit3=new Situation();
        sit3.setSeverity(Situation.Severity.High);
        sit3.setSubject(SERVICE_TYPE2+"/"+OP2+"/"+FAULT2);
        sit3.setType("SLA Violation");
        sit3.setDescription("Service exceeded SLA");
        sits.add(sit3);
        
        Situation sit4=new Situation();
        sit4.setSeverity(Situation.Severity.Low);
        sit4.setSubject(SERVICE_TYPE4+"/"+OP4);
        sit4.setType("SLA Violation");
        sit4.setDescription("Service exceeded SLA");
        sits.add(sit4);
        
        Situation sit5=new Situation();
        sit5.setSeverity(Situation.Severity.Medium);
        sit5.setSubject(SERVICE_TYPE4+"/"+OP5);
        sit5.setType("SLA Violation");
        sit5.setDescription("Service exceeded SLA");
        sits.add(sit5);
        
        ServiceGraph graph=
                ServiceDependencyBuilder.buildGraph(sds, sits);
        
        if (graph == null) {
            fail("Graph is null");
        }
        
        graph.setDescription("Graph description");
        
        ServiceGraphLayoutImpl layout=new ServiceGraphLayoutImpl();
        
        layout.layout(graph);
        
        // Check some of the dimensions
        SVGServiceGraphGenerator generator=new SVGServiceGraphGenerator();
        
        java.io.ByteArrayOutputStream os=new java.io.ByteArrayOutputStream();
        
        try {
            generator.generate(graph, 0, os);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to generate: "+e);
        }
        
        try {
            os.close();
        } catch (Exception e) {
            fail("Failed to close: "+e);
        }
        
        String svg=new String(os.toByteArray());
        
        if (!svg.contains("<svg")) {
            fail("Not a SVG document");
        }
        
        System.out.println(svg);
    }

    @Test
    public void testEmptyServiceDescriptionLayoutGraph() {
        java.util.Set<ServiceDefinition> sds=new java.util.HashSet<ServiceDefinition>();
        
        java.util.List<Situation> sits=new java.util.ArrayList<Situation>();
        
        ServiceGraph graph=
                ServiceDependencyBuilder.buildGraph(sds, sits);
        
        if (graph == null) {
            fail("Graph is null");
        }
        
        graph.setDescription("Graph description");
        
        ServiceGraphLayoutImpl layout=new ServiceGraphLayoutImpl();
        
        layout.layout(graph);
        
        // Check some of the dimensions
        SVGServiceGraphGenerator generator=new SVGServiceGraphGenerator();
        
        java.io.ByteArrayOutputStream os=new java.io.ByteArrayOutputStream();
        
        try {
            generator.generate(graph, 0, os);
        } catch (Exception e) {
            fail("Failed to generate: "+e);
        }
        
        try {
            os.close();
        } catch (Exception e) {
            fail("Failed to close: "+e);
        }
        
        String svg=new String(os.toByteArray());
        
        if (!svg.contains("<svg")) {
            fail("Not a SVG document");
        }
        
        System.out.println(svg);
    }

    @Test
    public void testAverageSeverity() {
        Severity[] severities={ Severity.Normal, Severity.Critical };
        Severity avg=SVGServiceGraphGenerator.getAverageSeverity(severities);
        
        if (avg == null) {
            fail("Average is null");
        }
        
        if (avg != Severity.Warning) {
            fail("Average should be Warning: "+avg);
        }
    }
}
