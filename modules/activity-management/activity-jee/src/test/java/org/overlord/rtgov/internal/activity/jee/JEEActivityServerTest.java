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
package org.overlord.rtgov.internal.activity.jee;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.activity.model.bpm.ProcessCompleted;
import org.overlord.rtgov.activity.model.bpm.ProcessStarted;
import org.overlord.rtgov.activity.model.soa.RequestReceived;
import org.overlord.rtgov.activity.model.soa.RequestSent;
import org.overlord.rtgov.activity.model.soa.ResponseReceived;
import org.overlord.rtgov.activity.model.soa.ResponseSent;
import org.overlord.rtgov.activity.server.ActivityNotifier;
import org.overlord.rtgov.activity.server.ActivityStore;
import org.overlord.rtgov.activity.server.QuerySpec;

public class JEEActivityServerTest {
    
    private static final String PROC_TYPE = "ProcType";
    private static final String INST_ID = "InstId";
    private static final String RESP_ID = "respId";
    private static final String REQ_ID = "reqId";
    private static final String TEST_ID = "TestID";
    
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
        JEEActivityServer as=new JEEActivityServer();
        
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
        
        if (!ctxs.contains(new Context(Context.Type.Endpoint,PROC_TYPE+":"+INST_ID))) {
            fail("Expecting context Endpoint="+PROC_TYPE+":"+INST_ID);
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

        public List<ActivityType> getActivityTypes(Context context)
                throws Exception {
            // TODO Auto-generated method stub
            return null;
        }

        public List<ActivityType> getActivityTypes(Context context, long from,
                long to) throws Exception {
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
