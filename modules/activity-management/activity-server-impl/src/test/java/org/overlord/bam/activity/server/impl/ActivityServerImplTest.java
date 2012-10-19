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
package org.overlord.bam.activity.server.impl;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.overlord.bam.activity.model.ActivityType;
import org.overlord.bam.activity.model.ActivityUnit;
import org.overlord.bam.activity.model.Context;
import org.overlord.bam.activity.model.bpm.ProcessCompleted;
import org.overlord.bam.activity.model.bpm.ProcessStarted;
import org.overlord.bam.activity.model.soa.RequestReceived;
import org.overlord.bam.activity.model.soa.RequestSent;
import org.overlord.bam.activity.model.soa.ResponseReceived;
import org.overlord.bam.activity.model.soa.ResponseSent;
import org.overlord.bam.activity.server.ActivityNotifier;
import org.overlord.bam.activity.server.ActivityStore;
import org.overlord.bam.activity.server.QuerySpec;
import org.overlord.bam.activity.server.impl.ActivityServerImpl;

public class ActivityServerImplTest {
    
    private static final String PROC_TYPE = "ProcType";
    private static final String INST_ID = "InstId";
    private static final String RESP_ID = "respId";
    private static final String REQ_ID = "reqId";
    private static final String TEST_ID = "TestID";
    
    @Test
    public void testStoreAndNotify() {
        ActivityServerImpl as=new ActivityServerImpl();
        
        TestStore store=new TestStore();
        TestNotifier notifier1=new TestNotifier();
        TestNotifier notifier2=new TestNotifier();
        
        as.setActivityStore(store);
        as.getActivityNotifiers().add(notifier1);
        as.getActivityNotifiers().add(notifier2);
        
        java.util.List<ActivityUnit> list1=new java.util.Vector<ActivityUnit>();
        list1.add(new ActivityUnit());
        list1.add(new ActivityUnit());
        list1.add(new ActivityUnit());
        
        java.util.List<ActivityUnit> list2=new java.util.Vector<ActivityUnit>();
        list2.add(new ActivityUnit());
        list2.add(new ActivityUnit());
        
        try {
            as.store(list1);
            
            if (store.getActivityEvents().size() != list1.size()) {
                fail("Store does not have correct number of events");
            }
            
            if (notifier1.getActivityEvents().size() != list1.size()) {
                fail("Notifier 1 does not have correct number of events");
            }
            
            if (notifier2.getActivityEvents().size() != list1.size()) {
                fail("Notifier 2 does not have correct number of events");
            }
            
            as.store(list2);
            
            if (store.getActivityEvents().size() != (list1.size()+list2.size())) {
                fail("Store does not have correct number of events");
            }
            
            if (notifier1.getActivityEvents().size() != (list1.size()+list2.size())) {
                fail("Notifier 1 does not have correct number of events");
            }
            
            if (notifier2.getActivityEvents().size() != (list1.size()+list2.size())) {
                fail("Notifier 2 does not have correct number of events");
            }
        } catch(Exception e) {
            e.printStackTrace();
            fail("Failed to store: "+e);
        }
    }

    @Test
    public void testStoreFault() {
        ActivityServerImpl as=new ActivityServerImpl();
        
        TestStore store=new TestStore();
        TestNotifier notifier1=new TestNotifier();
        TestNotifier notifier2=new TestNotifier();
        
        as.setActivityStore(store);
        as.getActivityNotifiers().add(notifier1);
        as.getActivityNotifiers().add(notifier2);
        
        java.util.List<ActivityUnit> list1=new java.util.Vector<ActivityUnit>();
        list1.add(new ActivityUnit());
        list1.add(new ActivityUnit());
        list1.add(new ActivityUnit());
        
        java.util.List<ActivityUnit> list2=new java.util.Vector<ActivityUnit>();
        list2.add(new ActivityUnit());
        list2.add(new ActivityUnit());
        
        try {
            as.store(list1);
            
            if (store.getActivityEvents().size() != list1.size()) {
                fail("Store does not have correct number of events");
            }
            
            if (notifier1.getActivityEvents().size() != list1.size()) {
                fail("Notifier 1 does not have correct number of events");
            }
            
            if (notifier2.getActivityEvents().size() != list1.size()) {
                fail("Notifier 2 does not have correct number of events");
            }
            
            store.fault();

            try {
                as.store(list2);
                
                fail("Should have thrown exception");
            } catch(Exception f) {
                // Ignore
            }
            
        } catch(Exception e) {
            fail("Failed to store: "+e);
        }
    }

    @Test
    public void testNotifierFault() {
        ActivityServerImpl as=new ActivityServerImpl();
        
        TestStore store=new TestStore();
        TestNotifier notifier1=new TestNotifier();
        TestNotifier notifier2=new TestNotifier();
        
        as.setActivityStore(store);
        as.getActivityNotifiers().add(notifier1);
        as.getActivityNotifiers().add(notifier2);
        
        java.util.List<ActivityUnit> list1=new java.util.Vector<ActivityUnit>();
        list1.add(new ActivityUnit());
        list1.add(new ActivityUnit());
        list1.add(new ActivityUnit());
        
        java.util.List<ActivityUnit> list2=new java.util.Vector<ActivityUnit>();
        list2.add(new ActivityUnit());
        list2.add(new ActivityUnit());
        
        try {
            as.store(list1);
            
            if (store.getActivityEvents().size() != list1.size()) {
                fail("Store does not have correct number of events");
            }
            
            if (notifier1.getActivityEvents().size() != list1.size()) {
                fail("Notifier 1 does not have correct number of events");
            }
            
            if (notifier2.getActivityEvents().size() != list1.size()) {
                fail("Notifier 2 does not have correct number of events");
            }
            
            notifier2.fault();

            try {
                as.store(list2);
                
                fail("Should have thrown exception");
            } catch(Exception f) {
                // Ignore
            }
            
        } catch(Exception e) {
            fail("Failed to store: "+e);
        }
    }
    
    @Test
    public void testProcessActivityIdAssignedIfMissing() {
        ActivityServerImpl as=new ActivityServerImpl();
        
        ActivityUnit au=new ActivityUnit();
 
        if (au.getId() != null) {
            fail("ActivityUnit id should be null");
        }
        
        as.processActivityUnit(au);
        
        if (au.getId() == null) {
            fail("ActivityUnit id should NOT be null");
        }
    }
        
    @Test
    public void testProcessActivityIdPreservedIfExists() {
        ActivityServerImpl as=new ActivityServerImpl();
        
        ActivityUnit au=new ActivityUnit();
        au.setId(TEST_ID);
 
        as.processActivityUnit(au);
        
        if (!au.getId().equals(TEST_ID)) {
            fail("ActivityUnit id has changed: "+au.getId());
        }
    }
        
    @Test
    public void testProcessActivityAssignActivityUnitIdToTypesAndIndex() {
        ActivityServerImpl as=new ActivityServerImpl();
        
        ActivityUnit au=new ActivityUnit();
        au.setId(TEST_ID);
        
        RequestSent rqs=new RequestSent();
        rqs.setMessageId(REQ_ID);
        au.getActivityTypes().add(rqs);
        
        RequestReceived rqr=new RequestReceived();
        rqr.setMessageId(rqs.getMessageId());
        au.getActivityTypes().add(rqr);
        
        ResponseSent rps=new ResponseSent();
        rps.setMessageId(RESP_ID);
        rps.setReplyToId(rqs.getMessageId());
        au.getActivityTypes().add(rps);
        
        ResponseReceived rpr=new ResponseReceived();
        rpr.setMessageId(rps.getMessageId());
        rpr.setReplyToId(rqs.getMessageId());
        au.getActivityTypes().add(rpr);
        
        as.processActivityUnit(au);
        
        int pos=0;
        for (ActivityType at : au.getActivityTypes()) {
            if (!at.getUnitId().equals(au.getId())) {
                fail("Activity type's unit id invalid: "+at.getUnitId());
            }
            if (at.getUnitIndex() != pos) {
                fail("Activity type's unit index ("+pos+") incorrect: "+at.getUnitIndex());
            }
            pos++;
        }
    }
    
    @Test
    public void testProcessActivityExtractIdWithDuplicates() {
        ActivityServerImpl as=new ActivityServerImpl();
        
        ActivityUnit au=new ActivityUnit();
        au.setId(TEST_ID);
        
        RequestSent rqs=new RequestSent();
        rqs.setMessageId(REQ_ID);
        au.getActivityTypes().add(rqs);
        
        RequestReceived rqr=new RequestReceived();
        rqr.setMessageId(rqs.getMessageId());
        au.getActivityTypes().add(rqr);
        
        ProcessStarted ps=new ProcessStarted();
        ps.setInstanceId(INST_ID);
        ps.setProcessType(PROC_TYPE);
        au.getActivityTypes().add(ps);
        
        ProcessCompleted pc=new ProcessCompleted();
        pc.setInstanceId(INST_ID);
        au.getActivityTypes().add(pc);
        
        ResponseSent rps=new ResponseSent();
        rps.setMessageId(RESP_ID);
        rps.setReplyToId(rqs.getMessageId());
        au.getActivityTypes().add(rps);
        
        ResponseReceived rpr=new ResponseReceived();
        rpr.setMessageId(rps.getMessageId());
        rpr.setReplyToId(rqs.getMessageId());
        au.getActivityTypes().add(rpr);
        
        as.processActivityUnit(au);

        java.util.Set<Context> ctxs=au.contexts();
        
        if (ctxs.size() != 3) {
            fail("Unexpected number of context entries (3): "+ctxs.size());
        }
        
        if (!ctxs.contains(new Context(Context.Type.Message,REQ_ID))) {
            fail("Expecting context Message="+REQ_ID);
        }
        
        if (!ctxs.contains(new Context(Context.Type.Message,RESP_ID))) {
            fail("Expecting context Message="+RESP_ID);
        }
        
        if (!ctxs.contains(new Context(Context.Type.Endpoint,INST_ID))) {
            fail("Expecting context Endpoint="+INST_ID);
        }
    }
    
    public class TestStore implements ActivityStore {

        private List<ActivityUnit> _store=new java.util.Vector<ActivityUnit>();
        private boolean _fault=false;
        
        public void fault() {
            _fault = true;
        }
        
        public void store(List<ActivityUnit> activities) throws Exception {
            if (_fault) {
                throw new Exception("Fault in notifier");
            }
            _store.addAll(activities);
        }

        public ActivityUnit getActivityUnit(String id) throws Exception {
            // TODO Auto-generated method stub
            return null;
        }

        public List<ActivityType> query(QuerySpec query) throws Exception {
            // TODO Auto-generated method stub
            return null;
        }
        
        public List<ActivityUnit> getActivityEvents() {
            return (_store);
        }
    }

    public class TestNotifier implements ActivityNotifier {

        private List<ActivityUnit> _store=new java.util.Vector<ActivityUnit>();
        private boolean _fault=false;
        
        public List<ActivityUnit> getActivityEvents() {
            return (_store);
        }

        public void fault() {
            _fault = true;
        }
        
        public void notify(List<ActivityUnit> activities) throws Exception {
            if (_fault) {
                throw new Exception("Fault in notifier");
            }
            _store.addAll(activities);
        }
    }
}
