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
package org.overlord.rtgov.call.trace;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.activity.model.Context.Type;
import org.overlord.rtgov.activity.model.bpm.ProcessCompleted;
import org.overlord.rtgov.activity.model.bpm.ProcessCompleted.Status;
import org.overlord.rtgov.activity.model.bpm.ProcessStarted;
import org.overlord.rtgov.activity.model.soa.RequestReceived;
import org.overlord.rtgov.activity.model.soa.RequestSent;
import org.overlord.rtgov.activity.model.soa.ResponseReceived;
import org.overlord.rtgov.activity.model.soa.ResponseSent;
import org.overlord.rtgov.activity.server.impl.ActivityServerImpl;
import org.overlord.rtgov.activity.store.mem.MemActivityStore;
import org.overlord.rtgov.call.trace.CallTraceServiceImpl;
import org.overlord.rtgov.call.trace.CallTraceServiceImpl.CTState;
import org.overlord.rtgov.call.trace.model.CallTrace;
import org.overlord.rtgov.call.trace.util.CallTraceUtil;
import org.overlord.rtgov.call.trace.util.CallTraceUtilTest;

public class CallTraceServiceImplTest {

    protected CallTraceServiceImpl getCallTraceService() {
        CallTraceServiceImpl ctp=new CallTraceServiceImpl();
        
        MemActivityStore memas=new MemActivityStore();
        ActivityServerImpl as=new ActivityServerImpl();
        
        as.setActivityStore(memas);
        
        ctp.setActivityServer(as);
        
        return (ctp);
    }
        
    @Test
    public void testIncludeRelatedAUByContext() {
        CallTraceServiceImpl ctp=getCallTraceService();
        
        CTState state=new CTState();
        
        ActivityUnit au1=new ActivityUnit();
        ActivityUnit au2=new ActivityUnit();
        
        RequestSent rs1=new RequestSent();
        RequestSent rs2=new RequestSent();
        
        au1.getActivityTypes().add(rs1);
        au2.getActivityTypes().add(rs2);
        
        Context c1=new Context();
        c1.setValue("1");
        rs1.getContext().add(c1);
        
        Context c2=new Context();
        c2.setValue("1");
        rs2.getContext().add(c2);
        
        java.util.List<ActivityUnit> aus=new java.util.ArrayList<ActivityUnit>();
        aus.add(au1);
        aus.add(au2);
        
        try {
            ctp.getActivityServer().store(aus);
        } catch (Exception e) {
            fail("Failed to store activities: "+e);
        }
        
        Context query=new Context();
        query.setValue("1");
        
        ctp.loadActivityUnits(state, query);
        
        if (state.getActivityUnits().size() != 2) {
            fail("Expecting 2 activity units: "+state.getActivityUnits().size());
        }
        
        if (!state.getActivityUnits().contains(au1)) {
            fail("AU1 not in list");
        }
        
        if (!state.getActivityUnits().contains(au2)) {
            fail("AU2 not in list");
        }
    }

    @Test
    public void testIncludeRelatedAUByIndirectContext() {
        CallTraceServiceImpl ctp=getCallTraceService();
        
        CTState state=new CTState();
        
        ActivityUnit au1=new ActivityUnit();
        ActivityUnit au2=new ActivityUnit();
        
        RequestSent rs1=new RequestSent();
        RequestSent rs2=new RequestSent();
        
        au1.getActivityTypes().add(rs1);
        au2.getActivityTypes().add(rs2);
        
        Context c1=new Context();
        c1.setValue("1");
        rs1.getContext().add(c1);
        
        Context c1link=new Context();
        c1link.setValue("2");
        rs1.getContext().add(c1link);
        
        Context c2=new Context();
        c2.setValue("2");
        rs2.getContext().add(c2);
        
        java.util.List<ActivityUnit> aus=new java.util.ArrayList<ActivityUnit>();
        aus.add(au1);
        aus.add(au2);
        
        try {
            ctp.getActivityServer().store(aus);
        } catch (Exception e) {
            fail("Failed to store activities: "+e);
        }
        
        Context query=new Context();
        query.setValue("1");
        
        ctp.loadActivityUnits(state, query);
        
        if (state.getActivityUnits().size() != 2) {
            fail("Expecting 2 activity units: "+state.getActivityUnits().size());
        }
        
        if (!state.getActivityUnits().contains(au1)) {
            fail("AU1 not in list");
        }
        
        if (!state.getActivityUnits().contains(au2)) {
            fail("AU2 not in list");
        }
    }

    @Test
    public void testDoNotLoadExistingCorrelation() {
        CallTraceServiceImpl ctp=getCallTraceService();
        
        CTState state=new CTState();
        
        ActivityUnit au1=new ActivityUnit();
        ActivityUnit au2=new ActivityUnit();
        
        RequestSent rs1=new RequestSent();
        RequestSent rs2=new RequestSent();
        
        au1.getActivityTypes().add(rs1);
        au2.getActivityTypes().add(rs2);
        
        Context c1=new Context();
        c1.setValue("1");
        rs1.getContext().add(c1);
        
        Context c1link=new Context();
        c1link.setValue("2");
        rs1.getContext().add(c1link);
        
        Context c2=new Context();
        c2.setValue("2");
        rs2.getContext().add(c2);
        
        java.util.List<ActivityUnit> aus=new java.util.ArrayList<ActivityUnit>();
        aus.add(au1);
        aus.add(au2);
        
        try {
            ctp.getActivityServer().store(aus);
        } catch (Exception e) {
            fail("Failed to store activities: "+e);
        }
        
        // Mark correlation value '2' as already initialized
        Context existingContext=new Context();
        existingContext.setValue("2");
        
        state.initialized(existingContext);
        
        Context query=new Context();
        query.setValue("1");
        
        ctp.loadActivityUnits(state, query);
        
        if (state.getActivityUnits().size() != 1) {
            fail("Expecting 1 activity unit: "+state.getActivityUnits().size());
        }
        
        if (!state.getActivityUnits().contains(au1)) {
            fail("AU1 should be in state");
        }
    }

    @Test
    public void testSortActivityUnits1() {
        CTState state=new CTState();
        
        ActivityUnit au1=new ActivityUnit();
        ActivityUnit au2=new ActivityUnit();
        
        RequestSent rs1=new RequestSent();
        RequestSent rs2=new RequestSent();
        
        rs1.setTimestamp(System.currentTimeMillis());
        rs2.setTimestamp(System.currentTimeMillis()+20);
        
        au1.getActivityTypes().add(rs1);
        au2.getActivityTypes().add(rs2);
        
        state.getActivityUnits().add(au1);
        state.getActivityUnits().add(au2);
        
        state.sortActivityUnitsByTime();
        
        if (state.getActivityUnits().get(0) != au1) {
            fail("Expecting au1 at pos 0");
        }
        
        if (state.getActivityUnits().get(1) != au2) {
            fail("Expecting au2 at pos 1");
        }
    }

    @Test
    public void testSortActivityUnits2() {
        CTState state=new CTState();
        
        ActivityUnit au1=new ActivityUnit();
        ActivityUnit au2=new ActivityUnit();
        
        RequestSent rs1=new RequestSent();
        RequestSent rs2=new RequestSent();
        
        rs1.setTimestamp(System.currentTimeMillis());
        rs2.setTimestamp(System.currentTimeMillis()+20);
        
        au1.getActivityTypes().add(rs1);
        au2.getActivityTypes().add(rs2);
        
        state.getActivityUnits().add(au2);
        state.getActivityUnits().add(au1);
        
        state.sortActivityUnitsByTime();
        
        if (state.getActivityUnits().get(0) != au1) {
            fail("Expecting au1 at pos 0");
        }
        
        if (state.getActivityUnits().get(1) != au2) {
            fail("Expecting au2 at pos 1");
        }
    }
    
    @Test
    public void testCursorAllActivityTypes() {
        ActivityUnit au1=new ActivityUnit();
        
        RequestSent rs1=new RequestSent();
        RequestReceived rr2=new RequestReceived();
        
        au1.getActivityTypes().add(rs1);
        au1.getActivityTypes().add(rr2);
        
        CallTraceServiceImpl.ActivityUnitCursor cursor=
                    new CallTraceServiceImpl.ActivityUnitCursor(au1);
        
        if (cursor.getActivityTypes().size() != 2) {
            fail("Expecting 2 events: "+cursor.getActivityTypes().size());
        }
    }
    
    @Test
    public void testCursorRemainingActivityTypes() {
        ActivityUnit au1=new ActivityUnit();
        
        RequestSent rs1=new RequestSent();
        RequestReceived rr2=new RequestReceived();
        
        au1.getActivityTypes().add(rs1);
        au1.getActivityTypes().add(rr2);
        
        CallTraceServiceImpl.ActivityUnitCursor cursor=
                    new CallTraceServiceImpl.ActivityUnitCursor(au1);
        
        cursor.next();
        
        if (cursor.getActivityTypes().size() != 1) {
            fail("Expecting 1 events: "+cursor.getActivityTypes().size());
        }
    }
    
    @Test
    public void testCursorPeek() {
        ActivityUnit au1=new ActivityUnit();
        
        RequestSent rs1=new RequestSent();
        RequestReceived rr2=new RequestReceived();
        
        au1.getActivityTypes().add(rs1);
        au1.getActivityTypes().add(rr2);
        
        CallTraceServiceImpl.ActivityUnitCursor cursor=
                    new CallTraceServiceImpl.ActivityUnitCursor(au1);
        
        if (cursor.peek() != rs1) {
            fail("Peek should return rs1");
        }
    }
    
    @Test
    public void testCursorNextThenPeek() {
        ActivityUnit au1=new ActivityUnit();
        
        RequestSent rs1=new RequestSent();
        RequestReceived rr2=new RequestReceived();
        
        au1.getActivityTypes().add(rs1);
        au1.getActivityTypes().add(rr2);
        
        CallTraceServiceImpl.ActivityUnitCursor cursor=
                    new CallTraceServiceImpl.ActivityUnitCursor(au1);
        
        if (cursor.next() != rs1) {
            fail("Next should return rs1");
        }
        
        if (cursor.peek() != rr2) {
            fail("Peek should return rr2");
        }
    }
    
    @Test
    public void testCursorNextWhenEmpty() {
        ActivityUnit au1=new ActivityUnit();
        
        RequestSent rs1=new RequestSent();
        RequestReceived rr2=new RequestReceived();
        
        au1.getActivityTypes().add(rs1);
        au1.getActivityTypes().add(rr2);
        
        CallTraceServiceImpl.ActivityUnitCursor cursor=
                    new CallTraceServiceImpl.ActivityUnitCursor(au1);
        
        if (cursor.next() != rs1) {
            fail("Next should return rs1");
        }
        
        if (cursor.next() != rr2) {
            fail("Peek should return rr2");
        }
        
        if (cursor.next() != null) {
            fail("Cursor should now be empty");
        }
    }
    
    @Test
    public void testTopLevelAUs1() {
        ActivityUnit au1=new ActivityUnit();
        
        RequestReceived rr1=new RequestReceived();
        rr1.setServiceType("st1");
        rr1.setOperation("op1");
        
        au1.getActivityTypes().add(rr1);
        
        ActivityUnit au2=new ActivityUnit();
        
        RequestReceived rr2=new RequestReceived();
        rr2.setServiceType("st2");
        rr2.setOperation("op2");
        
        au2.getActivityTypes().add(rr2);
        
        CTState state=new CTState();
        state.add(au1);
        state.add(au2);
        
        java.util.List<ActivityUnit> tl=CallTraceServiceImpl.getTopLevelAUs(state);
        
        if (tl.size() != 2) {
            fail("Should be two top level aus: "+tl.size());
        }
    }
    
    @Test
    public void testTopLevelAUs2() {
        ActivityUnit au1=new ActivityUnit();
        
        RequestReceived rr1=new RequestReceived();
        rr1.setServiceType("st1");
        rr1.setOperation("op1");
        
        au1.getActivityTypes().add(rr1);
        
        ActivityUnit au2=new ActivityUnit();
        
        RequestReceived rr2=new RequestReceived();
        rr2.setServiceType("st2");
        rr2.setOperation("op2");
        
        au2.getActivityTypes().add(rr2);
        
        ActivityUnit au3=new ActivityUnit();
        
        RequestSent rs2=new RequestSent();
        rs2.setServiceType("st2");
        rs2.setOperation("op2");
        
        au3.getActivityTypes().add(rs2);
        
        CTState state=new CTState();
        state.add(au1);
        state.add(au2);
        state.add(au3);
        
        java.util.List<ActivityUnit> tl=CallTraceServiceImpl.getTopLevelAUs(state);
        
        if (tl.size() != 1) {
            fail("Should be 1 top level aus: "+tl.size());
        }
        
        if (tl.get(0) != au1) {
            fail("Should be au1");
        }
    }
    
    @Test
    public void testProcessAUSingleUnit2Service() {
        
        ActivityUnit au1=new ActivityUnit();
        au1.setId("au1");
        
        RequestReceived a1=new RequestReceived();
        a1.setServiceType("st1");
        a1.setOperation("op1");
        a1.setTimestamp(0);
        
        au1.getActivityTypes().add(a1);
        
        ProcessStarted p1=new ProcessStarted();
        p1.setProcessType("proc1");
        p1.setVersion("1");
        p1.setInstanceId("456");
        p1.setTimestamp(10);
        
        au1.getActivityTypes().add(p1);
        
        RequestSent a2=new RequestSent();
        a2.setServiceType("st2");
        a2.setOperation("op2");
        a2.setTimestamp(30);
        
        au1.getActivityTypes().add(a2);
        
        RequestReceived a3=new RequestReceived();
        a3.setServiceType("st2");
        a3.setOperation("op2");
        a3.setTimestamp(37);
        
        au1.getActivityTypes().add(a3);
        
        ProcessStarted p2=new ProcessStarted();
        p2.setProcessType("proc2");
        p2.setVersion("2");
        p2.setInstanceId("123");
        p2.setTimestamp(48);
        
        au1.getActivityTypes().add(p2);
        
        ProcessCompleted p3=new ProcessCompleted();
        p3.setInstanceId("123");
        p3.setStatus(Status.Success);
        p3.setTimestamp(57);
        
        au1.getActivityTypes().add(p3);
        
        ResponseSent a4=new ResponseSent();
        a4.setServiceType("st2");
        a4.setOperation("op2");
        a4.setTimestamp(59);
        
        au1.getActivityTypes().add(a4);
        
        ResponseReceived a5=new ResponseReceived();
        a5.setServiceType("st2");
        a5.setOperation("op2");
        a5.setTimestamp(67);
        
        au1.getActivityTypes().add(a5);
        
        ProcessCompleted p4=new ProcessCompleted();
        p4.setInstanceId("456");
        p4.setStatus(Status.Fail);
        p4.setTimestamp(83);
        
        au1.getActivityTypes().add(p4);
        
        ResponseSent a6=new ResponseSent();
        a6.setServiceType("st1");
        a6.setOperation("op1");
        a6.setTimestamp(88);
        
        au1.getActivityTypes().add(a6);
        
        CTState state=new CTState();
        au1.init();
        state.add(au1);
        
        CallTrace ct=CallTraceServiceImpl.processAUs(state);
        
        compare(ct, "SingleUnit2Service", "CallTrace1");
    }    
    
    @Test
    public void testProcessAUSeparateUnits2Service() {
        
        ActivityUnit au1=new ActivityUnit();
        au1.setId("au1");
        
        ActivityUnit au2=new ActivityUnit();
        au2.setId("au2");
        
        RequestReceived a1=new RequestReceived();
        a1.setServiceType("st1");
        a1.setOperation("op1");
        a1.setTimestamp(0);
        
        au1.getActivityTypes().add(a1);
        
        ProcessStarted p1=new ProcessStarted();
        p1.setProcessType("proc1");
        p1.setVersion("1");
        p1.setInstanceId("456");
        p1.setTimestamp(10);
        
        au1.getActivityTypes().add(p1);
        
        RequestSent a2=new RequestSent();
        a2.setServiceType("st2");
        a2.setOperation("op2");
        a2.setTimestamp(30);
        
        au1.getActivityTypes().add(a2);
        
        RequestReceived a3=new RequestReceived();
        a3.setServiceType("st2");
        a3.setOperation("op2");
        a3.setTimestamp(37);
        
        au2.getActivityTypes().add(a3);
        
        ProcessStarted p2=new ProcessStarted();
        p2.setProcessType("proc2");
        p2.setVersion("2");
        p2.setInstanceId("123");
        p2.setTimestamp(48);
        
        au2.getActivityTypes().add(p2);
        
        ProcessCompleted p3=new ProcessCompleted();
        p3.setInstanceId("123");
        p3.setStatus(Status.Success);
        p3.setTimestamp(57);
        
        au2.getActivityTypes().add(p3);
        
        ResponseSent a4=new ResponseSent();
        a4.setServiceType("st2");
        a4.setOperation("op2");
        a4.setTimestamp(59);
        
        au2.getActivityTypes().add(a4);
        
        ResponseReceived a5=new ResponseReceived();
        a5.setServiceType("st2");
        a5.setOperation("op2");
        a5.setTimestamp(67);
        
        au1.getActivityTypes().add(a5);
        
        ProcessCompleted p4=new ProcessCompleted();
        p4.setInstanceId("456");
        p4.setStatus(Status.Fail);
        p4.setTimestamp(83);
        
        au1.getActivityTypes().add(p4);
        
        ResponseSent a6=new ResponseSent();
        a6.setServiceType("st1");
        a6.setOperation("op1");
        a6.setTimestamp(88);
        
        au1.getActivityTypes().add(a6);
        
        CTState state=new CTState();
        au1.init();
        state.add(au1);
        au2.init();
        state.add(au2);
        
        CallTrace ct=CallTraceServiceImpl.processAUs(state);
        
        compare(ct, "SeparateUnits2Service", "CallTrace1");
    }
    
    @Test
    public void testProcessAUSeparateUnits2Service2() {
        
        ActivityUnit au1=new ActivityUnit();
        au1.setId("au1");
        
        ActivityUnit au2=new ActivityUnit();
        au2.setId("au2");
        
        ActivityUnit au3=new ActivityUnit();
        au3.setId("au3");
        
        RequestReceived a1=new RequestReceived();
        a1.setServiceType("st1");
        a1.setOperation("op1");
        a1.setTimestamp(0);
        
        au1.getActivityTypes().add(a1);
        
        ProcessStarted p1=new ProcessStarted();
        p1.setProcessType("proc1");
        p1.setVersion("1");
        p1.setInstanceId("456");
        p1.setTimestamp(10);
        
        au1.getActivityTypes().add(p1);
        
        RequestSent a2=new RequestSent();
        a2.setServiceType("st2");
        a2.setOperation("op2");
        a2.setTimestamp(30);
        
        au1.getActivityTypes().add(a2);
        
        RequestReceived a3=new RequestReceived();
        a3.setServiceType("st2");
        a3.setOperation("op2");
        a3.setTimestamp(37);
        
        au2.getActivityTypes().add(a3);
        
        ProcessStarted p2=new ProcessStarted();
        p2.setProcessType("proc2");
        p2.setVersion("2");
        p2.setInstanceId("123");
        p2.setTimestamp(48);
        
        au2.getActivityTypes().add(p2);
        
        ProcessCompleted p3=new ProcessCompleted();
        p3.setInstanceId("123");
        p3.setStatus(Status.Success);
        p3.setTimestamp(57);
        
        au3.getActivityTypes().add(p3);
        
        ResponseSent a4=new ResponseSent();
        a4.setServiceType("st2");
        a4.setOperation("op2");
        a4.setTimestamp(59);
        
        au3.getActivityTypes().add(a4);
        
        ResponseReceived a5=new ResponseReceived();
        a5.setServiceType("st2");
        a5.setOperation("op2");
        a5.setTimestamp(67);
        
        au1.getActivityTypes().add(a5);
        
        ProcessCompleted p4=new ProcessCompleted();
        p4.setInstanceId("456");
        p4.setStatus(Status.Fail);
        p4.setTimestamp(83);
        
        au1.getActivityTypes().add(p4);
        
        ResponseSent a6=new ResponseSent();
        a6.setServiceType("st1");
        a6.setOperation("op1");
        a6.setTimestamp(88);
        
        au1.getActivityTypes().add(a6);
        
        CTState state=new CTState();
        au1.init();
        state.add(au1);
        au2.init();
        state.add(au2);
        au3.init();
        state.add(au3);
        
        CallTrace ct=CallTraceServiceImpl.processAUs(state);
        
        compare(ct, "SeparateUnits2Service2", "CallTrace1");
    }

    @Test
    public void testProcessAUSingleUnit2ServiceFault() {
        
        ActivityUnit au1=new ActivityUnit();
        au1.setId("au1");
        
        RequestReceived a1=new RequestReceived();
        a1.setServiceType("st1");
        a1.setOperation("op1");
        a1.setTimestamp(0);
        
        au1.getActivityTypes().add(a1);
        
        ProcessStarted p1=new ProcessStarted();
        p1.setProcessType("proc1");
        p1.setVersion("1");
        p1.setInstanceId("456");
        p1.setTimestamp(10);
        
        au1.getActivityTypes().add(p1);
        
        RequestSent a2=new RequestSent();
        a2.setServiceType("st2");
        a2.setOperation("op2");
        a2.setTimestamp(30);
        
        au1.getActivityTypes().add(a2);
        
        RequestReceived a3=new RequestReceived();
        a3.setServiceType("st2");
        a3.setOperation("op2");
        a3.setTimestamp(37);
        
        au1.getActivityTypes().add(a3);
        
        ProcessStarted p2=new ProcessStarted();
        p2.setProcessType("proc2");
        p2.setVersion("2");
        p2.setInstanceId("123");
        p2.setTimestamp(48);
        
        au1.getActivityTypes().add(p2);
        
        ProcessCompleted p3=new ProcessCompleted();
        p3.setInstanceId("123");
        p3.setStatus(Status.Success);
        p3.setTimestamp(57);
        
        au1.getActivityTypes().add(p3);
        
        ResponseSent a4=new ResponseSent();
        a4.setServiceType("st2");
        a4.setOperation("op2");
        a4.setFault("Failed");
        a4.setTimestamp(59);
        
        au1.getActivityTypes().add(a4);
        
        ResponseReceived a5=new ResponseReceived();
        a5.setServiceType("st2");
        a5.setOperation("op2");
        a5.setFault("Failed");
        a5.setTimestamp(67);
        
        au1.getActivityTypes().add(a5);
        
        ProcessCompleted p4=new ProcessCompleted();
        p4.setInstanceId("456");
        p4.setStatus(Status.Fail);
        p4.setTimestamp(83);
        
        au1.getActivityTypes().add(p4);
        
        ResponseSent a6=new ResponseSent();
        a6.setServiceType("st1");
        a6.setOperation("op1");
        a6.setTimestamp(88);
        
        au1.getActivityTypes().add(a6);
        
        CTState state=new CTState();
        au1.init();
        state.add(au1);
        
        CallTrace ct=CallTraceServiceImpl.processAUs(state);
        
        compare(ct, "SingleUnit2ServiceFault", "CallTrace2");
    }    
    
    @Test
    public void testProcessAUSeparateUnits2ServiceOneWayWithSentResp() {
        
        ActivityUnit au1=new ActivityUnit();
        au1.setId("au1");
        
        ActivityUnit au2=new ActivityUnit();
        au2.setId("au2");
        
        ActivityUnit au3=new ActivityUnit();
        au3.setId("au3");
        
        RequestReceived a1=new RequestReceived();
        a1.setServiceType("st1");
        a1.setOperation("op1");
        a1.setTimestamp(0);
        
        au1.getActivityTypes().add(a1);
        
        ProcessStarted p1=new ProcessStarted();
        p1.setProcessType("proc1");
        p1.setVersion("1");
        p1.setInstanceId("456");
        p1.setTimestamp(10);
        
        au1.getActivityTypes().add(p1);
        
        RequestSent a2=new RequestSent();
        a2.setServiceType("st2");
        a2.setOperation("op2");
        a2.setTimestamp(30);
        
        au1.getActivityTypes().add(a2);
        
        RequestReceived a3=new RequestReceived();
        a3.setServiceType("st2");
        a3.setOperation("op2");
        a3.setTimestamp(37);
        
        au2.getActivityTypes().add(a3);
        
        ProcessStarted p2=new ProcessStarted();
        p2.setProcessType("proc2");
        p2.setVersion("2");
        p2.setInstanceId("123");
        p2.setTimestamp(48);
        
        au2.getActivityTypes().add(p2);
        
        ProcessCompleted p3=new ProcessCompleted();
        p3.setInstanceId("123");
        p3.setStatus(Status.Success);
        p3.setTimestamp(57);
        
        au3.getActivityTypes().add(p3);
        
        ResponseSent a4=new ResponseSent();
        a4.setServiceType("st2");
        a4.setOperation("op2");
        a4.setTimestamp(59);
        
        au3.getActivityTypes().add(a4);
 
        // NO RESPONSE RECEIVED
        
        ProcessCompleted p4=new ProcessCompleted();
        p4.setInstanceId("456");
        p4.setStatus(Status.Fail);
        p4.setTimestamp(83);
        
        au1.getActivityTypes().add(p4);
        
        ResponseSent a6=new ResponseSent();
        a6.setServiceType("st1");
        a6.setOperation("op1");
        a6.setTimestamp(88);
        
        au1.getActivityTypes().add(a6);
        
        CTState state=new CTState();
        au1.init();
        state.add(au1);
        au2.init();
        state.add(au2);
        au3.init();
        state.add(au3);
        
        CallTrace ct=CallTraceServiceImpl.processAUs(state);
        
        compare(ct, "testProcessAUSeparateUnits2ServiceOneWayWithSentResp", "CallTrace3");
    }

    
    @Test
    public void testProcessAUSeparateUnits2ServiceOneWayNoResp() {
        
        ActivityUnit au1=new ActivityUnit();
        au1.setId("au1");
        
        ActivityUnit au2=new ActivityUnit();
        au2.setId("au2");
        
        ActivityUnit au3=new ActivityUnit();
        au3.setId("au3");
        
        RequestReceived a1=new RequestReceived();
        a1.setServiceType("st1");
        a1.setOperation("op1");
        a1.setTimestamp(0);
        
        au1.getActivityTypes().add(a1);
        
        ProcessStarted p1=new ProcessStarted();
        p1.setProcessType("proc1");
        p1.setVersion("1");
        p1.setInstanceId("456");
        p1.setTimestamp(10);
        
        au1.getActivityTypes().add(p1);
        
        RequestSent a2=new RequestSent();
        a2.setServiceType("st2");
        a2.setOperation("op2");
        a2.setTimestamp(30);
        
        au1.getActivityTypes().add(a2);
        
        RequestReceived a3=new RequestReceived();
        a3.setServiceType("st2");
        a3.setOperation("op2");
        a3.setTimestamp(37);
        
        au2.getActivityTypes().add(a3);
        
        ProcessStarted p2=new ProcessStarted();
        p2.setProcessType("proc2");
        p2.setVersion("2");
        p2.setInstanceId("123");
        p2.setTimestamp(48);
        
        au2.getActivityTypes().add(p2);
        
        ProcessCompleted p3=new ProcessCompleted();
        p3.setInstanceId("123");
        p3.setStatus(Status.Success);
        p3.setTimestamp(57);
        
        au3.getActivityTypes().add(p3);
        
        // NO RESPONSE SENT OR RECEIVED
        
        ProcessCompleted p4=new ProcessCompleted();
        p4.setInstanceId("456");
        p4.setStatus(Status.Fail);
        p4.setTimestamp(83);
        
        au1.getActivityTypes().add(p4);
        
        ResponseSent a6=new ResponseSent();
        a6.setServiceType("st1");
        a6.setOperation("op1");
        a6.setTimestamp(88);
        
        au1.getActivityTypes().add(a6);
        
        CTState state=new CTState();
        au1.init();
        state.add(au1);
        au2.init();
        state.add(au2);
        au3.init();
        state.add(au3);
        
        CallTrace ct=CallTraceServiceImpl.processAUs(state);
        
        compare(ct, "testProcessAUSeparateUnits2ServiceOneWayNoResp", "CallTrace3");
    }

    @Test
    public void testProcessAUSOAAndBPMInterleaved() {
        
        try {
            ActivityUnit au1=new ActivityUnit();
            au1.setId("au1");
           
            ActivityUnit au2=new ActivityUnit();
            au2.setId("au2");
           
            ActivityUnit au3=new ActivityUnit();
            au3.setId("au3");
           
            RequestReceived a1=new RequestReceived();
            a1.setServiceType("st1");
            a1.setOperation("op1");
            a1.setMessageId("m0");
            a1.setTimestamp(0);
            a1.getContext().add(new Context(Type.Conversation, "1"));
           
            au1.getActivityTypes().add(a1);
           
            ProcessStarted p1=new ProcessStarted();
            p1.setProcessType("proc1");
            p1.setVersion("1");
            p1.setInstanceId("456");
            p1.setTimestamp(10);
           
            au1.getActivityTypes().add(p1);
           
            RequestSent a2=new RequestSent();
            a2.setServiceType("st2");
            a2.setOperation("op2");
            a2.setMessageId("m1");
            a2.setTimestamp(30);
            a2.getContext().add(new Context(Type.Conversation, "1"));
           
            au1.getActivityTypes().add(a2);
           
            RequestReceived a3=new RequestReceived();
            a3.setServiceType("st2");
            a3.setOperation("op2");
            a3.setMessageId("m1");
            a3.setTimestamp(37);
            a3.getContext().add(new Context(Type.Conversation, "1"));
        
            au2.getActivityTypes().add(a3);
           
            ProcessStarted p2=new ProcessStarted();
            p2.setProcessType("proc2");
            p2.setVersion("2");
            p2.setInstanceId("123");
            p2.setTimestamp(48);
           
            au2.getActivityTypes().add(p2);
           
            ProcessCompleted p3=new ProcessCompleted();
            p3.setInstanceId("123");
            p3.setStatus(Status.Success);
            p3.setTimestamp(57);
           
            au3.getActivityTypes().add(p3);

            ResponseSent a4=new ResponseSent();
            a4.setServiceType("st2");
            a4.setOperation("op2");
            a4.setMessageId("m2");
            a4.setReplyToId("m1");
            a4.setTimestamp(59);
            a4.getContext().add(new Context(Type.Conversation, "1"));
        
            au3.getActivityTypes().add(a4);
           
            ResponseReceived a5=new ResponseReceived();
            a5.setServiceType("st2");
            a5.setOperation("op2");
            a5.setMessageId("m2");
            a5.setReplyToId("m1");
            a5.setTimestamp(67);
            a5.getContext().add(new Context(Type.Conversation, "1"));
        
            au1.getActivityTypes().add(a5);
           
            ProcessCompleted p4=new ProcessCompleted();
            p4.setInstanceId("456");
            p4.setStatus(Status.Fail);
            p4.setTimestamp(83);

            au1.getActivityTypes().add(p4);
           
            ResponseSent a6=new ResponseSent();
            a6.setServiceType("st1");
            a6.setOperation("op1");
            a4.setMessageId("m3");
            a4.setReplyToId("m0");
            a6.setTimestamp(88);
            a6.getContext().add(new Context(Type.Conversation, "1"));
        
            au1.getActivityTypes().add(a6);
           
            au1.init();
            au2.init();
            au3.init();
           
            java.util.List<ActivityUnit> activities=new java.util.ArrayList<ActivityUnit>();
            activities.add(au1);
            activities.add(au2);
            activities.add(au3);
           
            CTState state=new CTState();
            au1.init();
            state.add(au1);
            au2.init();
            state.add(au2);
            au3.init();
            state.add(au3);
            
            CallTrace ct=CallTraceServiceImpl.processAUs(state);
            
            compare(ct, "testProcessAUSOAAndBPMInterleaved", "CallTrace4");

        } catch(Exception e) {
            e.printStackTrace();
            fail("Failed to access activity server: "+e);
        }
    }

    protected void compare(CallTrace ct, String testname, String filename) {
        
        try {
            byte[] b=CallTraceUtil.serializeCallTrace(ct);
            
            if (b == null) {
                fail("null returned");
            }
            
            System.out.println(testname+": "+new String(b));
            
            java.io.InputStream is=
                    CallTraceUtilTest.class.getResourceAsStream(
                            "/calltraces/"+filename+".json");
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
