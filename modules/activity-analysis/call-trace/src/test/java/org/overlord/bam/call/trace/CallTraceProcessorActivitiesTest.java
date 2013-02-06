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
package org.overlord.bam.call.trace;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.bam.activity.server.impl.ActivityServerImpl;
import org.overlord.bam.activity.store.mem.MemActivityStore;
import org.overlord.bam.activity.util.ActivityUtil;
import org.overlord.bam.call.trace.model.CallTrace;
import org.overlord.bam.call.trace.util.CallTraceUtil;
import org.overlord.bam.call.trace.util.CallTraceUtilTest;

public class CallTraceProcessorActivitiesTest {

    protected CallTraceProcessor getCallTraceProcessor() {
        CallTraceProcessor ctp=new CallTraceProcessor();
        
        MemActivityStore memas=new MemActivityStore();
        ActivityServerImpl as=new ActivityServerImpl();
        
        as.setActivityStore(memas);
        
        ctp.setActivityServer(as);
        
        return (ctp);
    }
        
    @Test
    public void testActivityUnits1() {        
        testCallTrace("ActivityUnits1", "1");        
    }
    
    @Test
    public void testActivityUnits2() {        
        testCallTrace("ActivityUnits2", "1");        
    }
    
    protected void testCallTrace(String testName, String identifier) {
        CallTraceProcessor ctp=getCallTraceProcessor();
        
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
            CallTrace ct=ctp.createCallTrace(identifier);
            
            compare(ct, testName);
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to create call trace: "+e);
        }
    }


    protected void compare(CallTrace ct, String testname) {
        
        try {
            byte[] b=CallTraceUtil.serializeCallTrace(ct);
            
            if (b == null) {
                fail("null returned");
            }
            
            System.out.println(testname+": "+new String(b));
            
            java.io.InputStream is=
                    CallTraceUtilTest.class.getResourceAsStream(
                            "/calltraces/CallTrace"+testname+".json");
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
