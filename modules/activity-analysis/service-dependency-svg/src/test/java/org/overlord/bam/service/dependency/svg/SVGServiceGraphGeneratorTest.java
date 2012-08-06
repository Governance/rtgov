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
package org.overlord.bam.service.dependency.svg;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.bam.service.analytics.InvocationDefinition;
import org.overlord.bam.service.analytics.OperationDefinition;
import org.overlord.bam.service.analytics.RequestFaultDefinition;
import org.overlord.bam.service.analytics.RequestResponseDefinition;
import org.overlord.bam.service.analytics.ServiceDefinition;
import org.overlord.bam.service.dependency.ServiceDependencyBuilder;
import org.overlord.bam.service.dependency.ServiceGraph;
import org.overlord.bam.service.dependency.layout.ServiceGraphLayoutImpl;

public class SVGServiceGraphGeneratorTest {

    private static final String FAULT2 = "Fault2";
    private static final String OP4 = "op4";
    private static final String OP3 = "op3";
    private static final String OP2 = "op2";
    private static final String OP1 = "op1";
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
                ServiceDependencyBuilder.buildGraph(sds);
        
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
            generator.generate(graph, os);
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

}
