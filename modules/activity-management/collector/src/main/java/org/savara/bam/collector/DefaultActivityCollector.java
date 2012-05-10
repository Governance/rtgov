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
package org.savara.bam.collector;

import static javax.ejb.ConcurrencyManagementType.BEAN;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.transaction.Synchronization;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.savara.bam.activity.model.ActivityUnit;
import org.savara.bam.activity.model.ActivityType;
import org.savara.bam.activity.model.Origin;
import org.savara.bam.collector.spi.ActivityLogger;
import org.savara.bam.collector.spi.CollectorContext;

/**
 * This class provides a default implementation of the activity
 * collector interface.
 *
 */
@Singleton(name="ActivityCollector")
@ConcurrencyManagement(BEAN)
public class DefaultActivityCollector implements ActivityCollector {

    private static final Logger LOG=Logger.getLogger(DefaultActivityCollector.class.getName());
    
    @Inject
    private CollectorContext _collectorContext=null;
    
    @Inject
    private ActivityLogger _activityLogger=null;
    
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
    public void setActivityLogger(ActivityLogger activityLogger) {
        _activityLogger = activityLogger;
    }
    
    /**
     * This method gets the activity logger.
     * 
     * @return The activity logger
     */
    public ActivityLogger getActivityLogger() {
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
    public boolean startScope() {
        ActivityUnit au=_activityUnit.get();
        
        if (au == null) {
            startScope(createActivityUnit());
            return true;
        }
        
        return false;
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
            LOG.finest("End scope for ActivityUnit="+au);
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
        
        if (!transactional) {
            _activityLogger.log(au);
        }
    }

}
