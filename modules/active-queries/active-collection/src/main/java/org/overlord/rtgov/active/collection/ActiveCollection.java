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

import org.overlord.rtgov.active.collection.predicate.Predicate;

/**
 * This class provides the base Active Collection implementation.
 *
 */
public abstract class ActiveCollection implements ActiveCollectionMBean,
                          java.lang.Iterable<Object> {

    private String _name=null;
    private ActiveCollectionVisibility _visibility=null;
    private java.util.List<ActiveChangeListener> _listeners=
                    new java.util.ArrayList<ActiveChangeListener>();
    private ActiveCollectionAdapter _adapter=null;
    private long _itemExpiration=0;
    private int _maxItems=0;
    private int _highWaterMark=0;
    private boolean _highWaterMarkWarningIssued=false;
    private java.util.Map<String,Object> _properties=null;
    
    /**
     * This constructor initializes the active collection.
     * 
     * @param name The name
     */
    public ActiveCollection(String name) {
        _name = name;
    }
    
    /**
     * This constructor initializes the active collection.
     * 
     * @param acs The Active Collection source
     */
    public ActiveCollection(ActiveCollectionSource acs) {
        _name = acs.getName();
        _visibility = acs.getVisibility();
        _itemExpiration = acs.getItemExpiration();
        _maxItems = acs.getMaxItems();
        _highWaterMark = acs.getHighWaterMark();
        _properties = acs.getProperties();
    }
    
    /**
     * This constructor initializes the active collection as a derived
     * version of the supplied collection, that applies the supplied predicate.
     * 
     * @param name The name
     * @param parent The parent collection
     * @param context The context
     * @param predicate The predicate
     * @param properties The optional properties
     */
    protected ActiveCollection(String name, ActiveCollection parent, ActiveCollectionContext context,
                                Predicate predicate, java.util.Map<String,Object> properties) {
        this(name);
        
        _adapter = new ActiveCollectionAdapter(parent, context, predicate);
        
        _visibility = parent.getVisibility();
        
        _properties = properties;
    }
    
    /**
     * This method returns the name of the active collection.
     * 
     * @return The name
     */
    public String getName() {
        return (_name);
    }

    /**
     * This method returns the parent active collection.
     * 
     * @return The parent active collection, if defined.
     */
    protected ActiveCollection getParent() {
        return (_adapter == null ? null : _adapter.getParent());
    }

    /**
     * This method returns the predicate.
     * 
     * @return The predicate, if defined.
     */
    protected Predicate getPredicate() {
        return (_adapter == null ? null : _adapter.getPredicate());
    }

    /**
     * This method returns the context.
     * 
     * @return The context, if defined.
     */
    protected ActiveCollectionContext getContext() {
        return (_adapter == null ? null : _adapter.getContext());
    }

    /**
     * This method returns the visibility.
     * 
     * @return The visibility
     */
    public ActiveCollectionVisibility getVisibility() {
        return (_visibility);
    }
    
    /**
     * This method sets the visibility.
     * 
     * @param visibility The visibility
     */
    protected void setVisibility(ActiveCollectionVisibility visibility) {
        _visibility = visibility;
    }

    /**
     * This method returns the item expiration duration.
     * 
     * @return The number of milliseconds that the item should remain
     *          in the active collection, or 0 if not relevant
     */
    public long getItemExpiration() {
        return (_itemExpiration);
    }
    
    /**
     * This method sets the item expiration duration.
     * 
     * @param expire The item expiration duration, or zero
     *              for no expiration duration
     */
    protected void setItemExpiration(long expire) {
        _itemExpiration = expire;
    }

    /**
     * This method returns the maximum number of items that should be
     * contained within the active collection. The default policy will
     * be to remove oldest entry when maximum number is reached.
     * 
     * @return The maximum number of items, or 0 if not relevant
     */
    public int getMaxItems() {
        return (_maxItems);
    }

    /**
     * This method sets the maximum number of items
     * that will be in the active collection.
     * 
     * @param max The maximum number of items, or zero
     *              for no limit
     */
    protected void setMaxItems(int max) {
        _maxItems = max;
    }
    
    /**
     * This method gets the high water mark, used to indicate
     * when a warning should be issued.
     * 
     * @return The high water mark, or 0 if not relevant
     */
    public int getHighWaterMark() {
        return (_highWaterMark);
    }

    /**
     * This method sets the high water mark, used to indicate
     * when a warning should be issued.
     * 
     * @param highWaterMark The high water mark
     */
    protected void setHighWaterMark(int highWaterMark) {
        _highWaterMark = highWaterMark;
    }
    
    /**
     * This method determines whether the high water mark
     * warning has been issued.
     * 
     * @return Whether the high water mark has been issued
     */
    protected boolean getHighWaterMarkWarningIssued() {
        return (_highWaterMarkWarningIssued);
    }

    /**
     * This method sets whether the high water mark
     * warning has been issued.
     * 
     * @param issued Whether the high water mark has been issued
     */
    protected void setHighWaterMarkWarningIssued(boolean issued) {
        _highWaterMarkWarningIssued = issued;
    }
    
    /**
     * This method returns the value of a name property.
     * 
     * @param name The name
     * @param def The optional default value, if the property is not defined
     * @return The property, or null if not found
     */
    protected Object getProperty(String name, Object def) {
        Object ret=_properties == null ? null : _properties.get(name);
        
        if (ret == null) {
            ret = def;
        }
        
        return (ret);
    }
    
    /**
     * This method determines whether this is a derived active collection.
     * 
     * @return Whether the collection is derived
     */
    protected boolean isDerived() {
        return (_adapter != null);
    }
    
    /**
     * This property identifies whether the collection is active. If not,
     * then no notifications will be generated, and the content for derived
     * collections will be generated on demand.
     * 
     * @return Whether the derived collection is created "on demand"
     */
    protected boolean isActive() {
        return ((Boolean)getProperty("active", true));
    }
    
    /**
     * This method performs any required cleanup, associated with
     * the active collection, related to the max items and item
     * expiration properties.
     */
    protected abstract void cleanup();
    
    /**
     * This method adds an Active Change Listener to listen
     * for notifications of change to the active collection.
     * 
     * @param l The listener
     */
    public void addActiveChangeListener(ActiveChangeListener l) {
        synchronized (_listeners) {
            _listeners.add(l);
        }
    }
    
    /**
     * This method removes an Active Change Listener.
     * 
     * @param l The listener
     */
    public void removeActiveChangeListener(ActiveChangeListener l) {
        synchronized (_listeners) {
            _listeners.remove(l);
        }
    }
    
    /**
     * This method returns the list of active change listeners.
     * 
     * @return The list of active change listeners
     */
    public java.util.List<ActiveChangeListener> getActiveChangeListeners() {
        return (java.util.Collections.unmodifiableList(_listeners));
    }
    
    /**
     * This method returns the size of the active collection.
     * 
     * @return The size
     */
    public abstract int getSize();
    
    /**
     * This method adds the supplied object to the active collection.
     * If the optional key is provided, it can either be an index
     * if inserting into a particular position in a list (otherwise
     * default is to add to the end of the list), or a specific value
     * intended to be the key for a map.
     * 
     * @param key The optional key
     * @param value The value
     */
    protected abstract void doInsert(Object key, Object value);
    
    /**
     * This method notifies interested listeners that the entry with the
     * supplied key (which may be an index into a list) has been inserted
     * with the supplied value. A 'null' key for a list means the value
     * was added to the end of the list.
     * 
     * @param key The key, or list index, for the inserted value
     * @param value The inserted value
     */
    protected void inserted(Object key, Object value) {
        synchronized (_listeners) {
            if (_listeners.size() > 0) {
                for (ActiveChangeListener l : _listeners) {
                    l.inserted(key, value);
                }
            }
        }
    }
    
    /**
     * This method updates the supplied value within the active collection,
     * based on the supplied key. If the active collection is a list, then
     * the key will be an integer reflecting the index of the element being
     * updated. If the active collection is a map, then the key will be
     * associated with the element to be updated.
     * 
     * @param key The key
     * @param value The value
     */
    protected abstract void doUpdate(Object key, Object value);
    
    /**
     * This method notifies interested listeners that the entry with the
     * supplied key (which may be an index into a list) has been replaced
     * with the supplied value.
     * 
     * @param key The key, or list index, for the replaced value
     * @param value The replacement value
     */
    protected void updated(Object key, Object value) {
        synchronized (_listeners) {
            if (_listeners.size() > 0) {
                for (ActiveChangeListener l : _listeners) {
                    l.updated(key, value);
                }
            }
        }
    }
    
    /**
     * This method removes the supplied object from the active collection.
     * Generally the key should be provided, as this will be the most
     * efficient means to identify the item to be removed. However if
     * not provided, then the value will be used to locate the item.
     * 
     * @param key The optional key
     * @param value The value
     */
    protected abstract void doRemove(Object key, Object value);
    
    /**
     * This method notifies interested listeners that the entry with the
     * supplied key (which may be an index into a list) has been removed
     * having the supplied value.
     * 
     * @param key The key, or list index, for the removed value
     * @param value The removed value
     */
    protected void removed(Object key, Object value) {
        synchronized (_listeners) {
            if (_listeners.size() > 0) {
                for (ActiveChangeListener l : _listeners) {
                    l.removed(key, value);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void finalize() throws Throwable {
        super.finalize();
        
        if (_adapter != null) {
            _adapter.close();
        }
    }
    
    /**
     * This method derives a child active collection from this parent collection,
     * with the specified name, and filtered using the supplied predicate.
     * 
     * @param name The derived collection name
     * @param context The context
     * @param predicate The predicate
     * @param properties The optional properties
     * @return The derived collection
     */
    protected abstract ActiveCollection derive(String name, ActiveCollectionContext context,
                                Predicate predicate, java.util.Map<String,Object> properties);
    
    /**
     * This method queries the active collection, using the supplied spec.
     * 
     * @param qs The query spec
     * @return The query results
     */
    public abstract java.util.List<Object> query(QuerySpec qs);
    
    /**
     * This class provides a bridge between the parent and derived active
     * collections.
     *
     */
    public class ActiveCollectionAdapter implements ActiveChangeListener {
        
        private ActiveCollection _parent=null; // Strong ref to ensure not garbage collected
                                                // while child still needs it
        private ActiveCollectionContext _context=null;
        private Predicate _predicate=null;
        
        /**
         * This constructor initializes the fields within the adapter.
         * 
         * @param parent The parent active collection
         * @param context The context
         * @param predicate The predicate used to filter changes applied to the
         *                          active collection
         */
        public ActiveCollectionAdapter(ActiveCollection parent,
                    ActiveCollectionContext context, Predicate predicate) {
            _parent = parent;
            _context = context;
            _predicate = predicate;
            
            // Register to receive change notifications from parent
            // active collection
            _parent.addActiveChangeListener(this);
        }
        
        /**
         * The parent active collection.
         * 
         * @return The parent
         */
        protected ActiveCollection getParent() {
            return (_parent);
        }
        
        /**
         * The predicate.
         * 
         * @return The predicate
         */
        protected Predicate getPredicate() {
            return (_predicate);
        }
        
        /**
         * The context.
         * 
         * @return The context
         */
        protected ActiveCollectionContext getContext() {
            return (_context);
        }
        
        /**
         * {@inheritDoc}
         */
        public void inserted(Object key, Object value) { 
            if (_predicate.evaluate(_context, value)) {
                doInsert(key, value);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void updated(Object key, Object value) {
            if (_predicate.evaluate(_context, value)) {
                doUpdate(key, value);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void removed(Object key, Object value) {
            if (_predicate.evaluate(_context, value)) {
                doRemove(key, value);
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
