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
package org.overlord.rtgov.internal.activity.server.jee;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.activity.server.ActivityNotifier;
import org.overlord.rtgov.activity.server.ActivityServer;
import org.overlord.rtgov.activity.server.ActivityStore;
import org.overlord.rtgov.activity.server.ActivityStoreFactory;
import org.overlord.rtgov.activity.server.QuerySpec;

/**
 * This class represents the JEE implementation of the activity server.
 *
 */
@Singleton
public class JEEActivityServer implements ActivityServer {

    private static final Logger LOG=Logger.getLogger(JEEActivityServer.class.getName());
    
    @Resource
    private UserTransaction _tx;
        
    private ActivityStore _store;
    
    private java.util.List<ActivityNotifier> _notifiers=new java.util.Vector<ActivityNotifier>();
    
    private @Inject @Any Instance<ActivityNotifier> _injectedNotifiers=null;
    
    /**
     * Initialize the activity server implementation.
     */
    @PostConstruct
    public void init() {
        
        if (_store == null) {
            _store = ActivityStoreFactory.getActivityStore();
        }
        
        if (_injectedNotifiers != null) {
            for (ActivityNotifier notifier : _injectedNotifiers) {

                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Injecting activity notifier="+notifier);
                }
                
                _notifiers.add(notifier);
            }
        }       
    }
    
    /**
     * This method will return the current activity store. If not
     * explicitly defined, it will obtain it from a centrally configured
     * factory.
     * 
     * @return The activity store
     */
    protected ActivityStore retrieveActivityStore() {
        if (_store == null) {
            _store = ActivityStoreFactory.getActivityStore();
        }
        return (_store);
    }
    
    /**
     * Close the activity server implementation.
     */
    @PreDestroy
    public void close() {
        if (_injectedNotifiers != null) {
            for (ActivityNotifier notifier : _injectedNotifiers) {
                _notifiers.remove(notifier);
            }
        }       
    }
    
    /**
     * This method sets the activity store.
     * 
     * @param store The activity store
     */
    public void setActivityStore(ActivityStore store) {
        _store = store;
    }
    
    /**
     * This method gets the activity store.
     * 
     * @return The activity store
     */
    public ActivityStore getActivityStore() {
        return (_store);
    }
    
    /**
     * This method gets the list of activity notifiers.
     * 
     * @return The activity notifiers
     */
    public java.util.List<ActivityNotifier> getActivityNotifiers() {
        return (_notifiers);
    }
    
    /**
     * This method sets the list of activity notifiers.
     * 
     * @param notifiers The activity notifiers
     */
    public void setActivityNotifiers(java.util.List<ActivityNotifier> notifiers) {
        _notifiers = notifiers;
    }
    
    /**
     * This method starts the transaction.
     * 
     * @return Whether the transaction has been started
     * @throws Exception Failed to start txn
     */
    protected boolean startTxn() throws Exception {
        boolean ret=false;

        if (_tx != null && _tx.getStatus() == Status.STATUS_NO_TRANSACTION) {
            _tx.begin();

            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Started transaction");
            }
            
            ret = true;
        }
        
        return (ret);
    }
    
    /**
     * This method commits the transaction.
     * 
     * @throws Exception Failed to commit
     */
    protected void commitTxn() throws Exception {
        try {
            _tx.commit();
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Committed transaction");
            }
        } catch (Exception e) {
            _tx.rollback();
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Rolling back transaction: exception="+e);
            }
            
            throw e;
        }
    }
    
    /**
     * This method rolls back the transaction.
     * 
     * @throws Exception Failed to rollback
     */
    protected void rollbackTxn() throws Exception {
        try {
            _tx.rollback();
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Rolled back transaction");
            }
        } catch (Exception e) {
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Failed to roll back transaction: exception="+e);
            }
            
            throw e;
        }
    }
    
    /**
     * This method stores the supplied activity events.
     * 
     * @param activities The activity events
     * @throws Exception Failed to store the activities
     */
    public void store(java.util.List<ActivityUnit> activities) throws Exception {        

        ActivityStore actStore=retrieveActivityStore();
        
        if (actStore == null) {
            throw new Exception("Activity Store is unavailable");
        }
        
        boolean f_txnStarted=startTxn();

        try {
            // Process activity units to establish consistent id info
            for (int i=0; i < activities.size(); i++) {
                processActivityUnit(activities.get(i));
            }
            
            // Store the activities
            actStore.store(activities);
             
            // Inform registered notifiers
            for (int i=0; i < _notifiers.size(); i++) {
                _notifiers.get(i).notify(activities);
            }
            
            if (f_txnStarted) {
                commitTxn();
            }
        } catch (Exception e) {
            if (f_txnStarted) {
                rollbackTxn();
            }            
            
            throw e;
        }
    }
    
    /**
     * This method processes the activity unit to establish consistent
     * context and id information across the activity unit and contained
     * activity types.
     * 
     * @param au The activity unit
     */
    protected void processActivityUnit(ActivityUnit au) {
        
        // Check that activity unit has an id - and if not, create a
        // globally unique id
        if (au.getId() == null) {
            au.setId(createUniqueId());
        }

        au.init();
    }
    
    /**
     * This method creates a globally unique id for an activity unit.
     * 
     * @return The globally unique id
     */
    protected String createUniqueId() {
        return (UUID.randomUUID().toString());
    }
    
    /**
     * {@inheritDoc}
     */
    public ActivityUnit getActivityUnit(String id) throws Exception {
        
        ActivityStore actStore=retrieveActivityStore();
        
        if (actStore == null) {
            throw new Exception("Activity Store is unavailable");
        }
        
        boolean f_txnStarted=startTxn();

        ActivityUnit ret=null;
        
        try {
            ret = actStore.getActivityUnit(id);
            
            if (f_txnStarted) {
                commitTxn();
            }
        } catch (Exception e) {
            if (f_txnStarted) {
                rollbackTxn();
            }
            
            throw e;
        }

        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    public java.util.List<ActivityType> query(QuerySpec query) throws Exception {
        
        ActivityStore actStore=retrieveActivityStore();
        
        if (actStore == null) {
            throw new Exception("Activity Store is unavailable");
        }
        
        boolean f_txnStarted=startTxn();

        java.util.List<ActivityType> ret=null;
        
        try {
            ret = actStore.query(query);        
            
            if (f_txnStarted) {
                commitTxn();
            }
        } catch (Exception e) {
            if (f_txnStarted) {
                rollbackTxn();
            }
            
            throw e;
        }
        
        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    public List<ActivityType> getActivityTypes(Context context) throws Exception {
        
        ActivityStore actStore=retrieveActivityStore();
        
        if (actStore == null) {
            throw new Exception("Activity Store is unavailable");
        }
        
        boolean f_txnStarted=startTxn();
        List<ActivityType> ret=null;
        
        try {
            ret = actStore.getActivityTypes(context);
            
            if (f_txnStarted) {
                commitTxn();
            }
        } catch (Exception e) {
            if (f_txnStarted) {
                rollbackTxn();
            }
            
            throw e;
        }
        
        return (ret);
    }
    
    /**
     * {@inheritDoc}
     */
    public List<ActivityType> getActivityTypes(Context context, long from, long to) throws Exception {
        
        ActivityStore actStore=retrieveActivityStore();
        
        if (actStore == null) {
            throw new Exception("Activity Store is unavailable");
        }
        
        boolean f_txnStarted=startTxn();
        List<ActivityType> ret=null;
        
        try {
            ret = actStore.getActivityTypes(context, from, to);
            
            if (f_txnStarted) {
                commitTxn();
            }
        } catch (Exception e) {
            if (f_txnStarted) {
                rollbackTxn();
            }

            throw e;
        }
        
        return (ret);
    }
    
}
