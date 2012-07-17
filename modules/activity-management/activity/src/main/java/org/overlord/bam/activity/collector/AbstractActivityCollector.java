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
package org.overlord.bam.activity.collector;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.transaction.Synchronization;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.overlord.bam.activity.model.ActivityType;
import org.overlord.bam.activity.model.ActivityUnit;
import org.overlord.bam.activity.model.Context;
import org.overlord.bam.activity.model.Origin;

/**
 * This class provides an abstract implementation of the activity
 * collector interface.
 *
 */
public class AbstractActivityCollector implements ActivityCollector {

    private static final Logger LOG=Logger.getLogger(AbstractActivityCollector.class.getName());
    
    @Inject
    private CollectorContext _collectorContext=null;
    
    @Inject
    private ActivityUnitLogger _activityLogger=null;
    
    private java.lang.ThreadLocal<ActivityUnit> _activityUnit=new java.lang.ThreadLocal<ActivityUnit>();
    
    /**
     * This method sets the collector context.
     * 
     * @param cc The collector context
     */
    public void setCollectorContext(CollectorContext cc) {
        _collectorContext = cc;
    }
    
    /**
     * This method gets the collector context.
     * 
     * @return The collector context
     */
    public CollectorContext getCollectorContext() {
        return (_collectorContext);
    }
    
    /**
     * This method sets the activity logger.
     * 
     * @param activityLogger The activity logger
     */
    public void setActivityUnitLogger(ActivityUnitLogger activityLogger) {
        _activityLogger = activityLogger;
    }
    
    /**
     * This method gets the activity logger.
     * 
     * @return The activity logger
     */
    public ActivityUnitLogger getActivityUnitLogger() {
        return (_activityLogger);
    }
    
    /**
     * This method generates a unique transaction id.
     * 
     * @return The transaction id
     */
    protected String createTransactionId() {
        return (UUID.randomUUID().toString());
    }
    
    /**
     * This method returns the current date/time.
     * 
     * @return The timestamp
     */
    protected long getTimestamp() {
        return (System.currentTimeMillis());
    }
    
    /**
     * {@inheritDoc}
     */
    public void startScope() {
        ActivityUnit au=_activityUnit.get();
        
        // Currently only starts a scope if none exists. However
        // in the future may wish to support nested scopes.
        if (au == null) {
            startScope(createActivityUnit());
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isScopeActive() {
        return (_activityUnit.get() != null);
    }
    
    /**
     * This method starts the scope associated with the supplied activity unit.
     * 
     * @param au The activity unit
     */
    protected void startScope(ActivityUnit au) {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Start scope");
        }

        _activityUnit.set(au);
    }
    
    /**
     * This method creates a new activity unit and
     * initializes its origin based on the template
     * supplied by the OriginInitializer, if
     * available.
     * 
     * @return The new activity unit
     */
    protected ActivityUnit createActivityUnit() {
        ActivityUnit ret=new ActivityUnit();
        
        Origin origin=new Origin();
        origin.setHost(_collectorContext.getHost());
        origin.setNode(_collectorContext.getNode());
        origin.setPort(_collectorContext.getPort());
        origin.setPrincipal(_collectorContext.getPrincipal());
        origin.setThread(Thread.currentThread().getName());
        
        ret.setOrigin(origin);
        
        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    public void endScope() {
        ActivityUnit au=_activityUnit.get();

        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("End scope for: "+au);
        }
        
        if (au != null) {
            _activityLogger.log(au);

            _activityUnit.remove();
        } else {
            LOG.severe("End scope called but no ActivityUnit available");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void record(ActivityType actType) {
        record(actType, null);
    }
        
    /**
     * {@inheritDoc}
     */
    public void record(ActivityType actType, java.util.List<Context> contexts) {
        
        ActivityUnit au=_activityUnit.get();
        
        // Check if need to create a single event activity unit outside of transaction scope
        boolean transactional=true;
        
        if (au == null) {
            au = createActivityUnit();
            
            TransactionManager tm=_collectorContext.getTransactionManager();
            
            if (tm != null) {
                try {
                    Transaction txn=tm.getTransaction();
                    
                    if (txn != null) {
                        txn.registerSynchronization(
                            new Synchronization() {
                                public void afterCompletion(int arg0) {
                                    endScope();
                                }
    
                                public void beforeCompletion() {
                                }                           
                            });
                    
                        startScope(au);
                        
                    } else {
                        if (LOG.isLoggable(Level.FINEST)) {
                            LOG.finest("No transaction available");
                        }
                        transactional = false;
                    }
                    
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Failed to register synchronization with transaction", e);
                    transactional = false;
                }
            } else {
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("No transaction manager available");
                }
                transactional = false;
            }
        }
        
        // Set/override timestamp
        actType.setTimestamp(getTimestamp());
        
        // TODO: Need to determine how best to collect context
        // information
        
        // TODO: Need to also consider how to deal with filtering
        // mechanism
        
        au.getActivityTypes().add(actType);
        
        // Check if context information has been provided
        if (contexts != null && contexts.size() > 0) {
            
            for (int i=0; i < contexts.size(); i++) {
                Context c=contexts.get(i);
                
                if (!au.getContext().contains(c)) {
                    au.getContext().add(c);
                }
            }
        }
        
        if (!transactional) {
            _activityLogger.log(au);
        }
    }

}
