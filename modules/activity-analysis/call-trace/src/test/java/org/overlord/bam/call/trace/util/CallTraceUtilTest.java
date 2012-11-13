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
package org.overlord.bam.call.trace.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.bam.call.trace.model.Call;
import org.overlord.bam.call.trace.model.Task;
import org.overlord.bam.call.trace.model.TraceNode;

public class CallTraceUtilTest {

    @Test
    public void testJSONCompareActivityUnit() {
        Call node=new Call();
        
        node.setComponent("TestComponent");
        node.setOperation("TestOperation");
        node.setFault("TestFault");
        node.setRequest("<request/>");
        node.setResponse("<response/>");
        node.setDuration(56);
        node.setPercentage(100);
        node.setRequestLatency(1);
        node.setResponseLatency(2);
        
        // Add tasks
        Task t1=new Task();
        t1.setDescription("This is task 1");
        t1.setDuration(50);
        t1.setPercentage(25);
        
        node.getTasks().add(t1);
        
        Call c1=new Call();
        
        c1.setComponent("OtherComponent");
        c1.setOperation("OtherOp");
        c1.setDuration(100);
        c1.setPercentage(50);
        c1.setRequest("<req2/>");
        
        node.getTasks().add(c1);
        
        Task t2=new Task();
        t2.setDescription("This is task 2");
        t2.setDuration(50);
        t2.setPercentage(25);
        
        node.getTasks().add(t2);
        
        Task t3=new Task();
        t3.setDescription("This is task 3");
        t3.setDuration(100);
        t3.setPercentage(100);
        
        c1.getTasks().add(t3);
        
        
        
        try {
            byte[] b=CallTraceUtil.serializeCallTrace(node);
            
            if (b == null) {
                fail("null returned");
            }
            
            System.out.println(""+new String(b));
            
            java.io.InputStream is=CallTraceUtilTest.class.getResourceAsStream("/json/calltrace.json");
            byte[] inb2=new byte[is.available()];
            is.read(inb2);
            is.close();
            
            TraceNode node2=CallTraceUtil.deserializeCallTrace(inb2);
            
            byte[] b2=CallTraceUtil.serializeCallTrace(node2);            
            
            String s1=new String(b);
            String s2=new String(b2);
            
            if (!s1.equals(s2)) {
                fail("JSON is different: created="+s1+" stored="+s2);
            }

        } catch(Exception e) {
            e.printStackTrace();
            fail("Failed to serialize: "+e);
        }
    }
  
}
