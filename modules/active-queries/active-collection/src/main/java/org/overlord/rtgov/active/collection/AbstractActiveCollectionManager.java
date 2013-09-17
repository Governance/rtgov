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
package org.overlord.rtgov.active.collection;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.overlord.rtgov.active.collection.predicate.Predicate;
import org.overlord.rtgov.common.util.RTGovConfig;

/**
 * This class provides the abstract base implementation of the ActiveCollectionManager
 * interface. This class provides a general implementation that can be used by
 * derived implementations in different environments.
 *
 */
public abstract class AbstractActiveCollectionManager implements ActiveCollectionManager {
    
    private static final Logger LOG=Logger.getLogger(AbstractActiveCollectionManager.class.getName());

    private static final long HOUSE_KEEPING_INTERVAL = 10000;

    private java.util.Map<String, ActiveCollection> _activeCollections=
            new java.util.HashMap<String, ActiveCollection>();
    private java.util.Map<String, ActiveCollectionSource> _activeCollectionSources=
            new java.util.HashMap<String, ActiveCollectionSource>();
    private java.util.Map<String, java.lang.ref.SoftReference<ActiveCollection>> _derivedActiveCollections=
            new java.util.HashMap<String, java.lang.ref.SoftReference<ActiveCollection>>();
    private java.util.Map<String, ActiveCollection> _derivedActiveCollectionsRetain=
            new java.util.HashMap<String, ActiveCollection>();
    private java.util.List<ActiveCollectionListener> _activeCollectionListeners=
                new java.util.ArrayList<ActiveCollectionListener>();
    
    @Inject @RTGovConfig
    private Long _houseKeepingInterval=HOUSE_KEEPING_INTERVAL;
    
    private HouseKeeper _houseKeeper=null;
    private ActiveCollectionContext _context=new DefaultActiveCollectionContext(this);
    
    /**
     * The default constructor.
     */
    public AbstractActiveCollectionManager() {
        ActiveCollectionManagerAccessor.setActiveCollectionManager(this);
    }
    
    /**
     * This method initializes the Active Collection Manager.
     */
    public void init() {
        _houseKeeper = new HouseKeeper();
    }
    
    /**
     * This method closes the Active Collection Manager.
     */
    public void close() {
        if (_houseKeeper != null) {
            _houseKeeper.cancel();
        }
    }
    
    /**
     * This method sets the house keeping interval.
     * 
     * @param interval The interval
     */
    public void setHouseKeepingInterval(long interval) {
        _houseKeepingInterval = interval;
    }
    
    /**
     * This method gets the house keeping interval.
     * 
     * @return The interval
     */
    public long getHouseKeepingInterval() {
        if (_houseKeepingInterval == null) {
            return (HOUSE_KEEPING_INTERVAL);
        }
        return (_houseKeepingInterval);
    }
    
    /**
     * {@inheritDoc}
     */
    public void register(ActiveCollectionSource acs) throws Exception {
        ActiveCollection ac=null;
        
        // Check whether active collection for name has already been created
        synchronized (_activeCollectionSources) {
            if (_activeCollectionSources.containsKey(acs.getName())) {
                throw new IllegalArgumentException("Active collection source already exists for '"
                        +acs.getName()+"'");
            }
            
            // Initialize the active collection source
            acs.init(_context);
            
            _activeCollectionSources.put(acs.getName(), acs);
            
            LOG.info("Registered active collection source '"+acs.getName()+"'");            
        }
        
        // If not lazy instantiation
        if (!acs.getLazy()) {
            synchronized (_activeCollections) {
                ac = acs.getActiveCollection();

                _activeCollections.put(acs.getName(), ac);
                
                // Check for derived collections
                for (ActiveCollection dac : acs.getDerivedActiveCollections()) {
                    _derivedActiveCollectionsRetain.put(dac.getName(), dac);
                }

                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Registered active collection '"+acs.getName()+"' immediately");
                }
            }
        }
        
        if (ac != null) {
            synchronized (_activeCollectionListeners) {
                for (int i=0; i < _activeCollectionListeners.size(); i++) {
                    _activeCollectionListeners.get(i).registered(ac);
                }
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void unregister(ActiveCollectionSource acs) throws Exception {
        ActiveCollection ac=null;
        
        synchronized (_activeCollections) {
            ac = _activeCollections.remove(acs.getName());
        }
        
        if (ac != null) {
            // Active collection needs to be unregistered before closing
            // the active collection source, as the source unregisters
            // any active change listeners associated with the collection
            synchronized (_activeCollectionListeners) {
                for (int i=0; i < _activeCollectionListeners.size(); i++) {
                    _activeCollectionListeners.get(i).unregistered(ac);
                }
            }
        }
        
        synchronized (_activeCollectionSources) {
            if (!_activeCollectionSources.containsKey(acs.getName())) {
                throw new IllegalArgumentException("Active collection '"
                        +acs.getName()+"' is not registered");
            }
            
            // Close the active collection source
            acs.close();
          
            acs.setActiveCollection(null);
            
            _activeCollectionSources.remove(acs.getName());
            
            // Remove derived collections
            for (ActiveCollection dac : acs.getDerivedActiveCollections()) {
                _derivedActiveCollectionsRetain.remove(dac.getName());
            }

            LOG.info("Unregistered active collection for source '"+acs.getName()+"'");
        }
    }

    /**
     * {@inheritDoc}
     */
    public ActiveCollection getActiveCollection(String name) {
        ActiveCollection ret=_activeCollections.get(name);
        
        if (ret == null) {
            // Check if active collection source exists
            if (_activeCollectionSources.containsKey(name)) {
                synchronized (_activeCollections) {
                    // Check again, to make sure active collection wasn't
                    // created in another thread outside sync block
                    if (_activeCollections.containsKey(name)) {
                        ret = _activeCollections.get(name);
                    } else {
                        ActiveCollectionSource acs=_activeCollectionSources.get(name);
                        ret = acs.getActiveCollection();
                        
                        if (ret != null) {
                            _activeCollections.put(acs.getName(), ret);
                            
                            // Check for derived collections
                            for (ActiveCollection dac : acs.getDerivedActiveCollections()) {
                                _derivedActiveCollectionsRetain.put(dac.getName(), dac);
                            }

                           // Register listeners
                            synchronized (_activeCollectionListeners) {
                                for (int i=0; i < _activeCollectionListeners.size(); i++) {
                                    _activeCollectionListeners.get(i).registered(ret);
                                }
                            }
                        }
                    }
                }
            } else {
                
                // Check if a retained derived active collection
                ret = _derivedActiveCollectionsRetain.get(name);
                
                if (ret == null) {
                    // Check if a derived active collection
                    java.lang.ref.SoftReference<ActiveCollection> ref=_derivedActiveCollections.get(name);
                    
                    if (ref != null) {
                        ret = ref.get();
                    }
                }
            }
        }
        
        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    public java.util.Collection<ActiveCollection> getActiveCollections() {
        return (_activeCollections.values());
    }

    /**
     * {@inheritDoc}
     */
    public ActiveCollection create(String name, ActiveCollection parent, Predicate predicate,
                            java.util.Map<String,Object> properties) {
        ActiveCollection ret=null;
        
        synchronized (_derivedActiveCollections) {
            // Check if collection already exists
            java.lang.ref.SoftReference<ActiveCollection> ref=_derivedActiveCollections.get(name);
            
            if (ref != null) {
                ret = ref.get();
                
                if (ret == null) {                    
                    _derivedActiveCollections.remove(name);
                    
                    if (LOG.isLoggable(Level.FINER)) {
                        LOG.finer("Removing soft reference to active collection '"+name+"'");
                    }
                }
            }
            
            if (ret == null) {
                ret = parent.derive(name, _context, predicate, properties);
                
                _derivedActiveCollections.put(name, new java.lang.ref.SoftReference<ActiveCollection>(ret));
                
                if (LOG.isLoggable(Level.FINER)) {
                    LOG.finer("Derived active collection '"+name+"' with predicate: "+predicate);
                }
            }
        }
        
        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    public void remove(String name) {
        synchronized (_derivedActiveCollections) {
            _derivedActiveCollections.remove(name);
            
            if (LOG.isLoggable(Level.FINER)) {
                LOG.finer("Removed derived active collection '"+name+"'");
            }
        }
    }
    
    /**
     * This method performs the cleanup task on the top level
     * active collections.
     */
    protected void cleanup() {
               
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Running active collection cleanup ....");
        }
        
        synchronized (_activeCollectionSources) {
            for (ActiveCollectionSource acs : _activeCollectionSources.values()) {
                
                if (acs.hasActiveCollection()) {
                    ActiveCollection ac=acs.getActiveCollection();
                    
                    try {
                        ac.cleanup();
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, MessageFormat.format(
                                java.util.PropertyResourceBundle.getBundle(
                                "active-collection.Messages").getString("ACTIVE-COLLECTION-3"),
                                ac.getName()), e);
                    }
                    
                    // Check whether the high water mark has been breached
                    if (ac.getHighWaterMark() > 0) {
                        
                        if (ac.getHighWaterMarkWarningIssued()) {
                            
                            if (ac.getSize() < ac.getHighWaterMark()) {
                                // TODO: Currently log message, but should also
                                // report via MBean when implemented
                                LOG.info("Active collection '"+ac.getName()
                                        +"' has returned below its high water mark ("
                                        +ac.getHighWaterMark()+")");
    
                                // Reset warning indicator
                                ac.setHighWaterMarkWarningIssued(false);
                            }
                        } else if (ac.getSize() > ac.getHighWaterMark()) {
                            
                            // Issue warning
                            // TODO: Currently log message, but should also
                            // report via MBean when implemented
                            LOG.warning("Active collection '"+ac.getName()
                                    +"' has exceeded its high water mark ("
                                    +ac.getHighWaterMark()+")");
                            
                            ac.setHighWaterMarkWarningIssued(true);
                        }
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addActiveCollectionListener(ActiveCollectionListener l) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Register active collection listener="+l);
        }
        
        synchronized (_activeCollectionListeners) {
            _activeCollectionListeners.add(l);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeActiveCollectionListener(ActiveCollectionListener l) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Unregister active collection listener="+l);
        }
        
        synchronized (_activeCollectionListeners) {
            _activeCollectionListeners.remove(l);
        }
    }

    /**
     * This class implements the housekeeping functionality to
     * cleanup the top level active collections periodically.
     *
     */
    public class HouseKeeper extends java.util.TimerTask {

        private java.util.Timer _timer=new java.util.Timer();
        
        /**
         * This is the constructor.
         */
        public HouseKeeper() {
            _timer.scheduleAtFixedRate(this, getHouseKeepingInterval(), getHouseKeepingInterval());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            cleanup();
        }
    }

}
