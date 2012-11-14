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
import org.overlord.bam.activity.model.ActivityUnit;
import org.overlord.bam.activity.model.Context;
import org.overlord.bam.activity.model.soa.RequestSent;
import org.overlord.bam.activity.server.impl.ActivityServerImpl;
import org.overlord.bam.activity.store.mem.MemActivityStore;
import org.overlord.bam.call.trace.CallTraceProcessor.CTState;

public class CallTraceProcessorTest {

    protected CallTraceProcessor getCallTraceProcessor() {
        CallTraceProcessor ctp=new CallTraceProcessor();
        
        MemActivityStore memas=new MemActivityStore();
        ActivityServerImpl as=new ActivityServerImpl();
        
        as.setActivityStore(memas);
        
        ctp.setActivityServer(as);
        
        return (ctp);
    }
        
    @Test
    public void testIncludeRelatedAUByContext() {
        CallTraceProcessor ctp=getCallTraceProcessor();
        
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
        
        ctp.loadActivityUnits(state, "1");
        
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
        CallTraceProcessor ctp=getCallTraceProcessor();
        
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
        
        ctp.loadActivityUnits(state, "1");
        
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
        CallTraceProcessor ctp=getCallTraceProcessor();
        
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
        state.initialized("2");
        
        ctp.loadActivityUnits(state, "1");
        
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
}
