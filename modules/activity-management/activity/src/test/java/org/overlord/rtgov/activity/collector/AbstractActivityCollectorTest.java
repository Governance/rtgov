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
package org.overlord.rtgov.activity.collector;

import static org.junit.Assert.*;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;

import org.junit.Test;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.activity.model.soa.RequestReceived;
import org.overlord.rtgov.activity.model.soa.RequestSent;
import org.overlord.rtgov.activity.collector.AbstractActivityCollector;
import org.overlord.rtgov.activity.collector.ActivityUnitLogger;
import org.overlord.rtgov.activity.collector.CollectorContext;

public class AbstractActivityCollectorTest {

    @Test
    public void testNoScopeSingleEvent() {
        AbstractActivityCollector ac=new AbstractActivityCollector() {};
        TestActivityLogger al=new TestActivityLogger();
        TestCollectorContext cc=new TestCollectorContext();
        
        ac.setActivityUnitLogger(al);
        ac.setCollectorContext(cc);
        
        RequestSent act=new RequestSent();
        
        try {
        	ac.record(act);
        } catch (Exception e) {
        	fail("Failed to record activity: "+e);
        }
        
        if (al.getActivityUnits().size() != 1) {
            fail("Should be 1 activity unit: "+al.getActivityUnits().size());
        }
        
        if (al.getActivityUnits().get(0).getActivityTypes().size() != 1) {
            fail("Should be 1 activity type: "+al.getActivityUnits().get(0).getActivityTypes().size());
        }
    }
    
    @Test
    public void testAppControlledScopeOnlyStartedOnce() {
        AbstractActivityCollector ac=new AbstractActivityCollector() {};
        TestActivityLogger al=new TestActivityLogger();
        TestCollectorContext cc=new TestCollectorContext();
        
        ac.setActivityUnitLogger(al);
        ac.setCollectorContext(cc);
        
        if (ac.isScopeActive()) {
            fail("Scope should not be active");
        }
        
        // Start scope
        ac.startScope();
        
        if (!ac.isScopeActive()) {
            fail("Scope should be active");
        }
        
        ac.endScope();
        
        if (ac.isScopeActive()) {
            fail("Scope should no longer be active");
        }
        
    }    
    
    @Test
    public void testNestedScope() {
        AbstractActivityCollector ac=new AbstractActivityCollector() {};
        TestActivityLogger al=new TestActivityLogger();
        TestCollectorContext cc=new TestCollectorContext();
        
        ac.setActivityUnitLogger(al);
        ac.setCollectorContext(cc);
        
        if (ac.isScopeActive()) {
            fail("Scope should not be active");
        }
        
        // Start scope
        ac.startScope();
        // Start nested scope
        ac.startScope();
        
        // Check for inner scope
        if (!ac.isScopeActive()) {
            fail("Scope should be active");
        }
        
        // End nested scope
        ac.endScope();

        // Check for outer scope
        if (!ac.isScopeActive()) {
            fail("Scope should be active");
        }

        // End outer scope
        ac.endScope();
        
        if (ac.isScopeActive()) {
            fail("Scope should no longer be active");
        }
        
    }
    
    @Test
    public void testAppControlledScope() {
        AbstractActivityCollector ac=new AbstractActivityCollector() {};
        TestActivityLogger al=new TestActivityLogger();
        TestCollectorContext cc=new TestCollectorContext();
        
        ac.setActivityUnitLogger(al);
        ac.setCollectorContext(cc);
        
        // Start scope
        ac.startScope();
        
        RequestSent req=new RequestSent();
        
        try {
        	ac.record(req);
        } catch (Exception e) {
        	fail("Failed to record activity: "+e);
        }
        
        if (al.getActivityUnits().size() != 0) {
            fail("Should be no activity unit: "+al.getActivityUnits().size());
        }
        
        RequestReceived resp=new RequestReceived();
        
        try {
        	ac.record(resp);
        } catch (Exception e) {
        	fail("Failed to record activity: "+e);
        }
        
        if (al.getActivityUnits().size() != 0) {
            fail("Should still be no activity unit: "+al.getActivityUnits().size());
        }
        
        // End txn
        ac.endScope();
        
        if (al.getActivityUnits().size() != 1) {
            fail("Should be 1 activity unit: "+al.getActivityUnits().size());
        }
        
        if (al.getActivityUnits().get(0).getActivityTypes().size() != 2) {
            fail("Should be 2 activity types: "+al.getActivityUnits().get(0).getActivityTypes().size());
        }
    }

    @Test
    public void testXAControlledScope() {
        AbstractActivityCollector ac=new AbstractActivityCollector() {};
        TestActivityLogger al=new TestActivityLogger();
        TestCollectorContext cc=new TestCollectorContext();
        
        ac.setActivityUnitLogger(al);
        ac.setCollectorContext(cc);
        
        try {
            cc.getTransactionManager().begin();
        } catch (Exception e) {
            fail("Failed to start transaction: "+e);
        }
        
        RequestSent req=new RequestSent();
        
        try {
        	ac.record(req);
        } catch (Exception e) {
        	fail("Failed to record activity: "+e);
        }
        
        if (al.getActivityUnits().size() != 0) {
            fail("Should be no activity unit: "+al.getActivityUnits().size());
        }
        
        RequestReceived resp=new RequestReceived();
        
        try {
        	ac.record(resp);
        } catch (Exception e) {
        	fail("Failed to record activity: "+e);
        }
        
        try {
            cc.getTransactionManager().commit();
        } catch (Exception e) {
            fail("Failed to commit transaction: "+e);
        }
        
        if (al.getActivityUnits().size() != 1) {
            fail("Should be 1 activity unit: "+al.getActivityUnits().size());
        }
        
        if (al.getActivityUnits().get(0).getActivityTypes().size() != 2) {
            fail("Should be 2 activity types: "+al.getActivityUnits().get(0).getActivityTypes().size());
        }
    }

    @Test
    public void testMergeAppProvidedContexts() {
        AbstractActivityCollector ac=new AbstractActivityCollector() {};
        TestActivityLogger al=new TestActivityLogger();
        TestCollectorContext cc=new TestCollectorContext();
        
        ac.setActivityUnitLogger(al);
        ac.setCollectorContext(cc);
        
        Context c1=new Context(Context.Type.Conversation, "value1");
        Context c2=new Context(Context.Type.Message, "value2");
        Context c3=new Context(Context.Type.Endpoint, "value3");
        
        // Start scope
        ac.startScope();
        
        RequestSent req=new RequestSent();
        
        java.util.Set<Context> cl1=new java.util.HashSet<Context>();
        cl1.add(c1);
        cl1.add(c2);
        
        req.setContext(cl1);
        
        try {
        	ac.record(req);
        } catch (Exception e) {
        	fail("Failed to record activity: "+e);
        }
        
        if (al.getActivityUnits().size() != 0) {
            fail("Should be no activity unit: "+al.getActivityUnits().size());
        }
        
        RequestReceived resp=new RequestReceived();
        
        java.util.Set<Context> cl2=new java.util.HashSet<Context>();
        cl2.add(c2);
        cl2.add(c3);
        
        resp.setContext(cl2);
        
        try {
        	ac.record(resp);
        } catch (Exception e) {
        	fail("Failed to record activity: "+e);
        }
        
        if (al.getActivityUnits().size() != 0) {
            fail("Should still be no activity unit: "+al.getActivityUnits().size());
        }
        
        // End txn
        ac.endScope();
        
        if (al.getActivityUnits().size() != 1) {
            fail("Should be 1 activity unit: "+al.getActivityUnits().size());
        }
        
        if (al.getActivityUnits().get(0).contexts().size() != 3) {
            fail("Should be 3 contexts: "+al.getActivityUnits().get(0).contexts().size());
        }
    }

    public class TestCollectorContext implements CollectorContext {
        
        private TransactionManager _txnMgr=new TestTxnManager();

        public String getPrincipal() {
            return null;
        }

        public String getHost() {
            return null;
        }

        public String getNode() {
            return null;
        }

        public String getPort() {
            return null;
        }

        public TransactionManager getTransactionManager() {
            return (_txnMgr);
        }
        
    }

    public class TestActivityLogger implements ActivityUnitLogger {

        private java.util.List<ActivityUnit> _activityUnits=new java.util.Vector<ActivityUnit>();
        
        public java.util.List<ActivityUnit> getActivityUnits() {
            return (_activityUnits);
        }
        
        public void log(ActivityUnit act) {
            _activityUnits.add(act);
        }

        public void init() {
            
        }

        public void close() {
        }
        
    }
    
    public class TestTransaction implements Transaction {

        private Synchronization _sync=null;
        
        public void commit() throws RollbackException, HeuristicMixedException,
                HeuristicRollbackException, SecurityException,
                IllegalStateException, SystemException {
            if (_sync != null) {
                _sync.beforeCompletion();
                _sync.afterCompletion(0);
            }
        }

        public boolean delistResource(XAResource arg0, int arg1)
                throws IllegalStateException, SystemException {
            // TODO Auto-generated method stub
            return false;
        }

        public boolean enlistResource(XAResource arg0)
                throws RollbackException, IllegalStateException,
                SystemException {
            // TODO Auto-generated method stub
            return false;
        }

        public int getStatus() throws SystemException {
            // TODO Auto-generated method stub
            return 0;
        }

        public void registerSynchronization(Synchronization arg0)
                throws RollbackException, IllegalStateException,
                SystemException {
            _sync = arg0;
        }

        public void rollback() throws IllegalStateException, SystemException {
            // TODO Auto-generated method stub
            
        }

        public void setRollbackOnly() throws IllegalStateException,
                SystemException {
            // TODO Auto-generated method stub
            
        }
        
    }
    
    public class TestTxnManager implements TransactionManager {
        
        private Transaction _transaction=null;
        
        public TestTxnManager() {
        }

        public void begin() throws NotSupportedException, SystemException {
            _transaction = new TestTransaction();
        }

        public void commit() throws RollbackException, HeuristicMixedException,
                HeuristicRollbackException, SecurityException,
                IllegalStateException, SystemException {
            _transaction.commit();
            _transaction = null;
        }

        public int getStatus() throws SystemException {
            // TODO Auto-generated method stub
            return 0;
        }

        public Transaction getTransaction() throws SystemException {
            return (_transaction);
        }

        public void resume(Transaction arg0)
                throws InvalidTransactionException, IllegalStateException,
                SystemException {
            // TODO Auto-generated method stub
            
        }

        public void rollback() throws IllegalStateException, SecurityException,
                SystemException {
            // TODO Auto-generated method stub
            
        }

        public void setRollbackOnly() throws IllegalStateException,
                SystemException {
            // TODO Auto-generated method stub
            
        }

        public void setTransactionTimeout(int arg0) throws SystemException {
            // TODO Auto-generated method stub
            
        }

        public Transaction suspend() throws SystemException {
            // TODO Auto-generated method stub
            return null;
        }
        
    }
}
