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
package org.overlord.bam.activity.collector;

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
import org.overlord.bam.activity.model.ActivityUnit;
import org.overlord.bam.activity.model.Context;
import org.overlord.bam.activity.model.soa.RequestReceived;
import org.overlord.bam.activity.model.soa.RequestSent;
import org.overlord.bam.activity.collector.ActivityUnitLogger;
import org.overlord.bam.activity.collector.CollectorContext;

public class AbstractActivityCollectorTest {

    @Test
    public void testNoScopeSingleEvent() {
        AbstractActivityCollector ac=new AbstractActivityCollector() {};
        TestActivityLogger al=new TestActivityLogger();
        TestCollectorContext cc=new TestCollectorContext();
        
        ac.setActivityUnitLogger(al);
        ac.setCollectorContext(cc);
        
        RequestSent act=new RequestSent();
        
        ac.record(act);
        
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
    public void testAppControlledScope() {
        AbstractActivityCollector ac=new AbstractActivityCollector() {};
        TestActivityLogger al=new TestActivityLogger();
        TestCollectorContext cc=new TestCollectorContext();
        
        ac.setActivityUnitLogger(al);
        ac.setCollectorContext(cc);
        
        // Start scope
        ac.startScope();
        
        RequestSent req=new RequestSent();
        
        ac.record(req);
        
        if (al.getActivityUnits().size() != 0) {
            fail("Should be no activity unit: "+al.getActivityUnits().size());
        }
        
        RequestReceived resp=new RequestReceived();
        
        ac.record(resp);
        
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
        
        ac.record(req);
        
        if (al.getActivityUnits().size() != 0) {
            fail("Should be no activity unit: "+al.getActivityUnits().size());
        }
        
        RequestReceived resp=new RequestReceived();
        
        ac.record(resp);
        
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
        
        Context c1=new Context(Context.CONVERSATION_ID, "name1", "value1");
        Context c2=new Context(Context.MESSAGE_ID, "name2", "value2");
        Context c3=new Context(Context.PROPERTY_ID, "name3", "value3");
        
        // Start scope
        ac.startScope();
        
        RequestSent req=new RequestSent();
        
        java.util.List<Context> cl1=new java.util.Vector<Context>();
        cl1.add(c1);
        cl1.add(c2);
        
        req.setContext(cl1);
        
        ac.record(req);
        
        if (al.getActivityUnits().size() != 0) {
            fail("Should be no activity unit: "+al.getActivityUnits().size());
        }
        
        RequestReceived resp=new RequestReceived();
        
        java.util.List<Context> cl2=new java.util.Vector<Context>();
        cl2.add(c2);
        cl2.add(c3);
        
        resp.setContext(cl2);
        
        ac.record(resp);
        
        if (al.getActivityUnits().size() != 0) {
            fail("Should still be no activity unit: "+al.getActivityUnits().size());
        }
        
        // End txn
        ac.endScope();
        
        if (al.getActivityUnits().size() != 1) {
            fail("Should be 1 activity unit: "+al.getActivityUnits().size());
        }
        
        if (al.getActivityUnits().get(0).getAllContexts().size() != 3) {
            fail("Should be 3 contexts: "+al.getActivityUnits().get(0).getAllContexts().size());
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
