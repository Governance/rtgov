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
package org.overlord.rtgov.call.trace;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.activity.server.impl.ActivityServerImpl;
import org.overlord.rtgov.activity.store.mem.MemActivityStore;
import org.overlord.rtgov.activity.util.ActivityUtil;
import org.overlord.rtgov.call.trace.CallTraceServiceImpl;
import org.overlord.rtgov.call.trace.model.Call;
import org.overlord.rtgov.call.trace.model.CallTrace;
import org.overlord.rtgov.call.trace.model.TraceNode;
import org.overlord.rtgov.call.trace.util.CallTraceUtil;
import org.overlord.rtgov.call.trace.util.CallTraceUtilTest;

public class CallTraceServiceImplActivitiesTest {
	
	private static final String[] IGNORE_PROPERTIES={"client-host","client-node","server-host","server-node"};

    protected CallTraceServiceImpl getCallTraceService() {
        CallTraceServiceImpl ctp=new CallTraceServiceImpl();
        
        MemActivityStore memas=new MemActivityStore();
        ActivityServerImpl as=new ActivityServerImpl();
        
        as.setActivityStore(memas);
        
        ctp.setActivityServer(as);
        
        return (ctp);
    }
        
    @Test
    public void testActivityUnits1() {        
        Context query=new Context();
        query.setValue("1");
        
        testCallTrace("ActivityUnits1", query, null);        
    }
    
    @Test
    public void testActivityUnits2() {        
        Context query=new Context();
        query.setValue("1");
        
        testCallTrace("ActivityUnits2", query, null);        
    }
    
    @Test
    public void testActivityUnits3Message() {        
        Context query=new Context();
        query.setType(Context.Type.Message);
        query.setValue("ID-gbrown-redhat-47657-1369065371888-0-1");
        
        // Initial identifier is the message id
        testCallTrace("ActivityUnits3", query, "Message");        
    }
    
    @Test
    public void testActivityUnits3Endpoint() {        
        Context query=new Context();
        query.setType(Context.Type.Endpoint);
        query.setValue("1");
        
        // Initial identifier is the message id
        testCallTrace("ActivityUnits3", query, "Endpoint");        
    }
    
    protected void testCallTrace(String testName, Context query, String suffix) {
        CallTraceServiceImpl ctp=getCallTraceService();
        
        try {
            java.io.InputStream is=
                    CallTraceUtilTest.class.getResourceAsStream(
                            "/activities/"+testName+".json");
            byte[] b=new byte[is.available()];
            is.read(b);
            is.close();
            
            ctp.getActivityServer().store(ActivityUtil.deserializeActivityUnitList(b));
            
        } catch (Exception e) {
            fail("Failed to load activity units and store in server: "+e);
        }
        
        try {
            CallTrace ct=ctp.createCallTrace(query);
            
            for (TraceNode node : ct.getTasks()) {
                preProcessProperties(node);            		
            }
            
            compare(ct, testName, (suffix == null ? "" : suffix));
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to create call trace: "+e);
        }
    }
    
    protected void preProcessProperties(TraceNode node) {
    	for (String prop : IGNORE_PROPERTIES) {
    		if (node.getProperties().containsKey(prop)) {
    			node.getProperties().put(prop, "<ignore>");
    		}
    	}
    	
    	if (node instanceof Call) {
    		for (TraceNode tn : ((Call)node).getTasks()) {
    			preProcessProperties(tn);
    		}
    	}
    }

    protected void compare(CallTrace ct, String testname, String suffix) {
        
        try {
            byte[] b=CallTraceUtil.serializeCallTrace(ct);
            
            if (b == null) {
                fail("null returned");
            }
            
            System.out.println(testname+suffix+": "+new String(b));
            
            java.io.InputStream is=
                    CallTraceUtilTest.class.getResourceAsStream(
                            "/calltraces/CallTrace"+testname+suffix+".json");
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
