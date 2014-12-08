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

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.transaction.Synchronization;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.model.Origin;
import org.overlord.rtgov.activity.processor.InformationProcessorManager;
import org.overlord.rtgov.activity.validator.ActivityValidatorManager;
import org.overlord.rtgov.common.registry.ServiceClose;
import org.overlord.rtgov.common.registry.ServiceInit;
import org.overlord.rtgov.common.registry.ServiceListener;
import org.overlord.rtgov.common.registry.ServiceRegistryUtil;
import org.overlord.rtgov.common.util.RTGovProperties;

/**
 * This class provides an abstract implementation of the activity
 * collector interface.
 *
 */
public abstract class AbstractActivityCollector implements ActivityCollector, AbstractActivityCollectorMBean {

    private static final Logger LOG=Logger.getLogger(AbstractActivityCollector.class.getName());
    
    private static final boolean DEFAULT_COLLECTION_ENABLED=true;
    
    private Boolean _enabled;
    
    private CollectorContext _collectorContext=null;
    
    private ActivityUnitLogger _activityLogger=null;
    
    private InformationProcessorManager _infoProcessorManager=null;
    
    private ActivityValidatorManager _activityValidatorManager=null;
    
    private java.lang.ThreadLocal<ActivityUnit> _activityUnit=new java.lang.ThreadLocal<ActivityUnit>();
    
    /**
     * The default constructor.
     */
    public AbstractActivityCollector() {
        _enabled = RTGovProperties.getPropertyAsBoolean("ActivityCollector.enabled", DEFAULT_COLLECTION_ENABLED);
    }
    
    /**
     * Initialize the activity collector.
     */
    @ServiceInit
    public void init() {
        if (_infoProcessorManager == null) {
            ServiceRegistryUtil.addServiceListener(InformationProcessorManager.class, new ServiceListener<InformationProcessorManager>() {
    
                @Override
                public void registered(InformationProcessorManager service) {
                    setInformationProcessorManager(service);
                }
    
                @Override
                public void unregistered(InformationProcessorManager service) {
                    setInformationProcessorManager(null);
                }
                
            });
        }
        
        if (_activityValidatorManager == null) {
            ServiceRegistryUtil.addServiceListener(ActivityValidatorManager.class, new ServiceListener<ActivityValidatorManager>() {
    
                @Override
                public void registered(ActivityValidatorManager service) {
                    setActivityValidatorManager(service);
                }
    
                @Override
                public void unregistered(ActivityValidatorManager service) {
                    setActivityValidatorManager(null);
                }
                
            });
        }
        
        if (_collectorContext == null) {
            ServiceRegistryUtil.addServiceListener(CollectorContext.class, new ServiceListener<CollectorContext>() {
    
                @Override
                public void registered(CollectorContext service) {
                    setCollectorContext(service);
                }
    
                @Override
                public void unregistered(CollectorContext service) {
                    setCollectorContext(null);
                }
                
            });
        }

        if (_activityLogger == null) {
            ServiceRegistryUtil.addServiceListener(ActivityUnitLogger.class, new ServiceListener<ActivityUnitLogger>() {
    
                @Override
                public void registered(ActivityUnitLogger service) {
                    setActivityUnitLogger(service);
                }
    
                @Override
                public void unregistered(ActivityUnitLogger service) {
                    setActivityUnitLogger(null);
                }
                
            });
        }
    }
    
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
     * This method indicates whether activity collection is
     * currently enabled.
     * 
     * @return Whether collection is enabled
     */
    public boolean isCollectionEnabled() {
        return (_enabled == null ? DEFAULT_COLLECTION_ENABLED : _enabled);
    }
    
    /**
     * This method identifies whether the collection process should be
     * enabled.
     * 
     * @return Whether enabled
     */
    public boolean getCollectionEnabled() {
        return (isCollectionEnabled());
    }
    
    /**
     * This method sets whether the collection process should be
     * enabled.
     * 
     * @param enabled Whether enabled
     */
    public void setCollectionEnabled(boolean enabled) {
        _enabled = enabled;
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
     * This method gets the information processor manager.
     * 
     * @return The information processor manager
     */
    public InformationProcessorManager getInformationProcessorManager() {
        return (_infoProcessorManager);
    }
    
    /**
     * This method sets the information processor manager.
     * 
     * @param ipm The information processor manager
     */
    public void setInformationProcessorManager(InformationProcessorManager ipm) {
        _infoProcessorManager = ipm;
    }
    
    /**
     * This method gets the activity validator manager.
     * 
     * @return The activity validator manager
     */
    public ActivityValidatorManager getActivityValidatorManager() {
        return (_activityValidatorManager);
    }
    
    /**
     * This method sets the activity validator manager.
     * 
     * @param aim The activity validator manager
     */
    public void setActivityValidatorManager(ActivityValidatorManager aim) {
        _activityValidatorManager = aim;
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
        if (!isCollectionEnabled()) {
            return;
        }
        
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
        origin.setThread(Thread.currentThread().getName());
        
        ret.setOrigin(origin);
        
        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    public void endScope() {
        if (!isCollectionEnabled()) {
            return;
        }
        
        ActivityUnit au=_activityUnit.get();

        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("End scope for: "+au);
        }
        
        if (au != null) {
            _activityLogger.log(au);

            _activityUnit.remove();
        } else {
            LOG.severe(java.util.PropertyResourceBundle.getBundle(
                    "activity.Messages").getString("ACTIVITY-1"));
        }
    }

    /**
     * {@inheritDoc}
     */
    public String processInformation(String processor, String type, Object info,
                java.util.Map<String, Object> headers, ActivityType actType) {
        
        if (_infoProcessorManager != null) {
            return (_infoProcessorManager.process(processor, type, info, headers, actType));
        } else if (LOG.isLoggable(Level.WARNING)) {
            LOG.warning("Information processor manager not specified: "
                    +"unable to process type '"+type+"' info: "+info);
        }
        
        return (null);
    }

    /**
     * {@inheritDoc}
     */
    public void validate(ActivityType actType) throws Exception {
        
        // Check if activity is of interest to validators
        if (_activityValidatorManager != null) {
            _activityValidatorManager.validate(actType);
        }
    }
    
     /**
     * {@inheritDoc}
     */
    public void record(ActivityType actType) {
        if (!isCollectionEnabled()) {
            return;
        }

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
                    LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                            "activity.Messages").getString("ACTIVITY-2"), e);
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
        
        au.getActivityTypes().add(actType);
        
        if (!transactional) {
            _activityLogger.log(au);
        }
    }

    /**
     * This method closes the activity collector.
     */
    @ServiceClose
    public void close() {
    }
}
