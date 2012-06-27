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
package org.overlord.bam.active.collection;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides the default implementation of the ActiveCollectionManager
 * interface.
 *
 */
public class DefaultActiveCollectionManager implements ActiveCollectionManager {
    
    private static final Logger LOG=Logger.getLogger(DefaultActiveCollectionManager.class.getName());

    private java.util.Map<String, ActiveCollection> _activeCollections=
                new java.util.HashMap<String, ActiveCollection>();
    private java.util.Map<String, java.lang.ref.SoftReference<ActiveCollection>> _derivedActiveCollections=
                new java.util.HashMap<String, java.lang.ref.SoftReference<ActiveCollection>>();
    private java.util.List<ActiveCollectionListener> _activeCollectionListeners=
                new java.util.ArrayList<ActiveCollectionListener>();
    private long _houseKeepingInterval=10000;
    private HouseKeeper _houseKeeper=null;
    
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
        return (_houseKeepingInterval);
    }
    
    /**
     * {@inheritDoc}
     */
    public void register(ActiveCollectionSource acs) throws Exception {
        ActiveCollection ac=null;
        
        // Check whether active collection for name has already been created
        synchronized (_activeCollections) {
            if (_activeCollections.containsKey(acs.getName())) {
                throw new IllegalArgumentException("Active collection already exists for '"
                        +acs.getName()+"'");
            }
            
            if (acs.getType() == ActiveCollectionType.List) {
                ActiveList list=new ActiveList(acs.getName(),
                        acs.getItemExpiration(), acs.getMaxItems(), acs.getHighWaterMark());
                
                _activeCollections.put(acs.getName(), list);
                
                acs.setActiveCollection(list);
                
                // Initialize the active collection source
                acs.init();
                
                LOG.info("Registered active collection for source '"+acs.getName()+"'");
                
                ac = list;
                
            } else {
                throw new IllegalArgumentException("Active collection type not currently supported");
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
        
        if (acs.getActiveCollection() != null) {
            // Active collection needs to be unregistered before closing
            // the active collection source, as the source unregisters
            // any active change listeners associated with the collection
            synchronized (_activeCollectionListeners) {
                for (int i=0; i < _activeCollectionListeners.size(); i++) {
                    _activeCollectionListeners.get(i).unregistered(acs.getActiveCollection());
                }
            }
        }
        
        synchronized (_activeCollections) {
            if (!_activeCollections.containsKey(acs.getName())) {
                throw new IllegalArgumentException("Active collection '"
                        +acs.getName()+"' is not registered");
            }
            
            // Close the active collection source
            acs.close();
          
            acs.setActiveCollection(null);
            
            _activeCollections.remove(acs.getName());
            
            LOG.info("Unregistered active collection for source '"+acs.getName()+"'");
        }
    }

    /**
     * {@inheritDoc}
     */
    public ActiveCollection getActiveCollection(String name) {
        ActiveCollection ret=_activeCollections.get(name);
        
        if (ret == null) {
            java.lang.ref.SoftReference<ActiveCollection> ref=_derivedActiveCollections.get(name);
            
            if (ref != null) {
                ret = ref.get();
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
    public ActiveCollection create(String name, ActiveCollection parent,
                    Predicate predicate) {
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
                ret = parent.derive(name, predicate);
                
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
        
        synchronized (_activeCollections) {
            for (ActiveCollection ac : _activeCollections.values()) {
                ac.cleanup();
                
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
