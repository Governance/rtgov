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
package org.overlord.bam.activity.server.jee;

import static org.junit.Assert.*;

import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;
import org.overlord.bam.activity.model.ActivityType;
import org.overlord.bam.activity.model.ActivityUnit;
import org.overlord.bam.activity.model.bpm.ProcessCompleted;
import org.overlord.bam.activity.model.bpm.ProcessStarted;
import org.overlord.bam.activity.model.soa.RequestReceived;
import org.overlord.bam.activity.model.soa.RequestSent;
import org.overlord.bam.activity.model.soa.ResponseReceived;
import org.overlord.bam.activity.model.soa.ResponseSent;
import org.overlord.bam.activity.server.ActivityNotifier;
import org.overlord.bam.activity.server.ActivityStore;
import org.overlord.bam.activity.server.QuerySpec;
import org.overlord.bam.activity.server.jee.JEEActivityServer;

public class JEEActivityServerTest {
    
    private static final String PROC_TYPE = "ProcType";
    private static final String INST_ID = "InstId";
    private static final String RESP_ID = "respId";
    private static final String REQ_ID = "reqId";
    private static final String TEST_ID = "TestID";
    private static final Logger LOG=Logger.getLogger(JEEActivityServerTest.class.getName());

    @Test
    public void testStoreAndNotify() {
        JEEActivityServer as=new JEEActivityServer();
        
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
        JEEActivityServer as=new JEEActivityServer();
        
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
        JEEActivityServer as=new JEEActivityServer();
        
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
    public void testProcessActivityUnitHandleDuplicates() {
        JEEActivityServer as=new JEEActivityServer();
        as.setHandleDuplicateIds(true);
        
        ActivityUnit au=new ActivityUnit();
        
        for (int i=0; i < 10000; i++) {
            RequestSent rqs=new RequestSent();
            rqs.setMessageId("req"+i);
            au.getActivityTypes().add(rqs);
            
            RequestReceived rqr=new RequestReceived();
            rqr.setMessageId(rqs.getMessageId());
            au.getActivityTypes().add(rqr);
            
            ResponseSent rps=new ResponseSent();
            rps.setMessageId("resp"+i);
            rps.setReplyToId(rqs.getMessageId());
            au.getActivityTypes().add(rps);
            
            ResponseReceived rpr=new ResponseReceived();
            rpr.setMessageId(rps.getMessageId());
            rpr.setReplyToId(rqs.getMessageId());
            au.getActivityTypes().add(rpr);
        }
        
        long startTime=System.currentTimeMillis();
        
        as.processActivityUnit(au);
        
        long result=(System.currentTimeMillis()-startTime);
        
        LOG.info("PERFORMANCE RESULT: Processing "+au.getActivityTypes().size()+
                " activity types, within a single activity unit, with duplicate ids handled: TIME="+result+"ms");
    }
    
    
    @Test
    public void testProcessActivityUnitNotHandleDuplicates() {
        JEEActivityServer as=new JEEActivityServer();
        as.setHandleDuplicateIds(false);
        
        ActivityUnit au=new ActivityUnit();
        
        for (int i=0; i < 10000; i++) {
            RequestSent rqs=new RequestSent();
            rqs.setMessageId("req"+i);
            au.getActivityTypes().add(rqs);
            
            RequestReceived rqr=new RequestReceived();
            rqr.setMessageId(rqs.getMessageId());
            au.getActivityTypes().add(rqr);
            
            ResponseSent rps=new ResponseSent();
            rps.setMessageId("resp"+i);
            rps.setReplyToId(rqs.getMessageId());
            au.getActivityTypes().add(rps);
            
            ResponseReceived rpr=new ResponseReceived();
            rpr.setMessageId(rps.getMessageId());
            rpr.setReplyToId(rqs.getMessageId());
            au.getActivityTypes().add(rpr);
        }
        
        long startTime=System.currentTimeMillis();
        
        as.processActivityUnit(au);
        
        long result=(System.currentTimeMillis()-startTime);
        
        LOG.info("PERFORMANCE RESULT: Processing "+au.getActivityTypes().size()+
                " activity types, within a single activity unit, with duplicate ids NOT handled: TIME="+result+"ms");
    }
    
    @Test
    public void testProcessActivityIdAssignedIfMissing() {
        JEEActivityServer as=new JEEActivityServer();
        
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
        JEEActivityServer as=new JEEActivityServer();
        
        ActivityUnit au=new ActivityUnit();
        au.setId(TEST_ID);
 
        as.processActivityUnit(au);
        
        if (!au.getId().equals(TEST_ID)) {
            fail("ActivityUnit id has changed: "+au.getId());
        }
    }
        
    @Test
    public void testProcessActivityAssignActivityUnitIdToTypesAndIndex() {
        JEEActivityServer as=new JEEActivityServer();
        
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
            if (!at.getActivityUnitId().equals(au.getId())) {
                fail("Activity type's unit id invalid: "+at.getActivityUnitId());
            }
            if (at.getActivityUnitIndex() != pos) {
                fail("Activity type's unit index ("+pos+") incorrect: "+at.getActivityUnitIndex());
            }
            pos++;
        }
    }
    
    @Test
    public void testProcessActivityExtractIdWithDuplicates() {
        JEEActivityServer as=new JEEActivityServer();
        as.setHandleDuplicateIds(false);
        
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
        pc.setProcessType(PROC_TYPE);
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

        if (au.getContext().size() != 6) {
            fail("Unexpected number of context entries (6): "+au.getContext().size());
        }
        
        if (!au.getContext().get(0).getValue().equals(REQ_ID)) {
            fail("Expecting context 0 to be "+REQ_ID+": "+au.getContext().get(0).getValue());
        }
        
        if (!au.getContext().get(1).getValue().equals(REQ_ID)) {
            fail("Expecting context 1 to be "+REQ_ID+": "+au.getContext().get(1).getValue());
        }
        
        if (!au.getContext().get(2).getValue().equals(INST_ID)) {
            fail("Expecting context 2 to be "+INST_ID+": "+au.getContext().get(2).getValue());
        }
        
        if (!au.getContext().get(3).getValue().equals(INST_ID)) {
            fail("Expecting context 3 to be "+INST_ID+": "+au.getContext().get(3).getValue());
        }
        
        if (!au.getContext().get(4).getValue().equals(RESP_ID)) {
            fail("Expecting context 4 to be "+RESP_ID+": "+au.getContext().get(4).getValue());
        }
        
        if (!au.getContext().get(5).getValue().equals(RESP_ID)) {
            fail("Expecting context 5 to be "+RESP_ID+": "+au.getContext().get(5).getValue());
        }
    }
    
    @Test
    public void testProcessActivityExtractIdWithNoDuplicates() {
        JEEActivityServer as=new JEEActivityServer();
        as.setHandleDuplicateIds(true);
        
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
        pc.setProcessType(PROC_TYPE);
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

        if (au.getContext().size() != 3) {
            fail("Unexpected number of context entries (3): "+au.getContext().size());
        }
        
        if (!au.getContext().get(0).getValue().equals(REQ_ID)) {
            fail("Expecting context 0 to be "+REQ_ID+": "+au.getContext().get(0).getValue());
        }
        
        if (!au.getContext().get(1).getValue().equals(INST_ID)) {
            fail("Expecting context 1 to be "+INST_ID+": "+au.getContext().get(1).getValue());
        }
        
        if (!au.getContext().get(2).getValue().equals(RESP_ID)) {
            fail("Expecting context 2 to be "+RESP_ID+": "+au.getContext().get(2).getValue());
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

        public List<ActivityUnit> query(QuerySpec query) throws Exception {
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
