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
package org.overlord.rtgov.call.trace.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.call.trace.model.Call;
import org.overlord.rtgov.call.trace.model.CallTrace;
import org.overlord.rtgov.call.trace.model.Task;
import org.overlord.rtgov.call.trace.util.CallTraceUtil;

public class CallTraceUtilTest {

    @Test
    public void testJSONCompareActivityUnit() {
        Call node=new Call();
        
        node.setComponent("TestComponent");
        node.setInterface("TestInterface");
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
        c1.setInterface("OtherInterface");
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
        
        CallTrace ct=new CallTrace();
        ct.getTasks().add(node);
        
        try {
            byte[] b=CallTraceUtil.serializeCallTrace(ct);
            
            if (b == null) {
                fail("null returned");
            }
            
            System.out.println(""+new String(b));
            
            java.io.InputStream is=CallTraceUtilTest.class.getResourceAsStream("/json/calltrace.json");
            byte[] inb2=new byte[is.available()];
            is.read(inb2);
            is.close();
            
            CallTrace node2=CallTraceUtil.deserializeCallTrace(inb2);
            
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
