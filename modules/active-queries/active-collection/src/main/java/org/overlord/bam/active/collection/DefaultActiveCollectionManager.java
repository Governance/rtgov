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

import static javax.ejb.ConcurrencyManagementType.BEAN;

import java.lang.ref.WeakReference;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.Singleton;

/**
 * This class provides the default implementation of the ActiveCollectionManager
 * interface.
 *
 */
@Singleton(name="ActiveCollectionManager")
@ConcurrencyManagement(BEAN)
public class DefaultActiveCollectionManager implements ActiveCollectionManager {

    private java.util.Map<String, ActiveCollection> _activeCollections=
                new java.util.HashMap<String, ActiveCollection>();
    private java.util.Map<String, ActiveCollectionAdapter> _derivedActiveCollections=
                new java.util.HashMap<String, ActiveCollectionAdapter>();
    
    /**
     * {@inheritDoc}
     */
    public void register(ActiveCollectionSource acs) throws Exception {
        
        // Check whether active collection for name has already been created
        synchronized (_activeCollections) {
            if (_activeCollections.containsKey(acs.getName())) {
                throw new IllegalArgumentException("Active collection already exists for '"
                        +acs.getName()+"'");
            }
            
            if (acs.getType() == ActiveCollectionType.List) {
                ActiveList list=new ActiveList(acs.getName());
                
                _activeCollections.put(acs.getName(), list);
                
                acs.setActiveCollection(list);
                
                // Initialize the active collection source
                acs.init();
                
            } else {
                throw new IllegalArgumentException("Active collection type not currently supported");
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void unregister(ActiveCollectionSource acs) throws Exception {
        
        synchronized (_activeCollections) {
            if (!_activeCollections.containsKey(acs.getName())) {
                throw new IllegalArgumentException("Active collection '"
                        +acs.getName()+"' is not registered");
            }
            
            // Close the active collection source
            acs.close();
          
            acs.setActiveCollection(null);
            
            _activeCollections.remove(acs.getName());
        }
    }

    /**
     * {@inheritDoc}
     */
    public ActiveCollection getActiveCollection(String name) {
        ActiveCollection ret=_activeCollections.get(name);
        
        if (ret == null) {
            ActiveCollectionAdapter adapter=_derivedActiveCollections.get(name);
            
            if (adapter != null) {
                ret = adapter.getActiveCollection();
            }
        }
        
        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    public ActiveCollection create(String name, ActiveCollection parent,
                    Predicate predicate) throws Exception {
        ActiveCollection ret=null;
        
        synchronized (_derivedActiveCollections) {
            // Check if collection already exists
            ActiveCollectionAdapter adapter=_derivedActiveCollections.get(name);
            
            if (adapter != null) {
                ret = adapter.getActiveCollection();
                
                if (ret == null) {
                    // Remove adapter
                    adapter.close();
                    
                    _derivedActiveCollections.remove(name);
                    
                    adapter = null;
                }
            }
            
            if (adapter == null) {
                adapter = new ActiveCollectionAdapter(name, parent, predicate);
                
                _derivedActiveCollections.put(name, adapter);
                
                ret = adapter.getActiveCollection();
            }
        }
        
        return (ret);
    }

    /**
     * This class provides a bridge between the parent and derived active
     * collections.
     *
     */
    public class ActiveCollectionAdapter implements ActiveChangeListener {
        
        private String _name=null;
        private ActiveCollection _parent=null; // Strong ref to ensure not garbage collected
                                                // while child still needs it
        private Predicate _predicate=null;
        private WeakReference<ActiveCollection> _activeCollection=null;
        
        /**
         * This constructor initializes the fields within the adapter.
         * 
         * @param name The derived active collection name
         * @param parent The parent active collection
         * @param predicate The predicate used to filter changes applied to the
         *                          active collection
         */
        public ActiveCollectionAdapter(String name, ActiveCollection parent,
                            Predicate predicate) {
            _name = name;
            _parent = parent;
            _predicate = predicate;
            _activeCollection = new WeakReference<ActiveCollection>(parent.derive(name, predicate));
            
            // Register to receive change notifications from parent
            // active collection
            _parent.addActiveChangeListener(this);
        }
        
        /**
         * The name of the derived active collection being maintained by
         * this adapter.
         * 
         * @return The name
         */
        public String getName() {
            return (_name);
        }
        
        /**
         * This method returns the active collection associated with the adapter.
         * 
         * @return The active collection
         */
        public ActiveCollection getActiveCollection() {
            return (_activeCollection.get());
        }

        /**
         * {@inheritDoc}
         */
        public void inserted(Object key, Object value) {
            ActiveCollection ac=_activeCollection.get();
            
            if (ac != null && _predicate.evaluate(value)) {
                ac.insert(key, value);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void updated(Object key, Object value) {
            ActiveCollection ac=_activeCollection.get();
            
            if (ac != null && _predicate.evaluate(value)) {
                ac.update(key, value);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void removed(Object key, Object value) {
            ActiveCollection ac=_activeCollection.get();
            
            if (ac != null && _predicate.evaluate(value)) {
                ac.remove(key, value);
            }
        }
        
        /**
         * This method closes the adapter.
         */
        public void close() {
            _parent.removeActiveChangeListener(this);
            _parent = null;
        }
    }
}
