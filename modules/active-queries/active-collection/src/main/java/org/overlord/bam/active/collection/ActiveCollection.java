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

import org.overlord.bam.active.collection.predicate.Predicate;

/**
 * This class provides the base Active Collection implementation.
 *
 */
public abstract class ActiveCollection implements ActiveCollectionMBean {

    private String _name=null;
    private java.util.List<ActiveChangeListener> _listeners=
                    new java.util.ArrayList<ActiveChangeListener>();
    private ActiveCollectionAdapter _adapter=null;
    private long _itemExpiration=0;
    private int _maxItems=0;
    private int _highWaterMark=0;
    private boolean _highWaterMarkWarningIssued=false;
    
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
     * @param name The name
     * @param itemExpiration Item expiration time, or 0 if not relevant
     * @param maxItems Max number of items, or 0 if not relevant
     * @param highWaterMark Generate warning if number of items exceed high water mark
     */
    public ActiveCollection(String name, long itemExpiration, int maxItems,
                            int highWaterMark) {
        _name = name;
        _itemExpiration = itemExpiration;
        _maxItems = maxItems;
        _highWaterMark = highWaterMark;
    }
    
    /**
     * This constructor initializes the active collection as a derived
     * version of the supplied collection, that applies the supplied predicate.
     * 
     * @param name The name
     * @param parent The parent collection
     * @param predicate The predicate
     */
    protected ActiveCollection(String name, ActiveCollection parent, Predicate predicate) {
        this(name);
        
        _adapter = new ActiveCollectionAdapter(parent, predicate);
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
    protected abstract void insert(Object key, Object value);
    
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
    protected abstract void update(Object key, Object value);
    
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
    protected abstract void remove(Object key, Object value);
    
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
     * @param predicate The predicate
     * @return The derived collection
     */
    protected abstract ActiveCollection derive(String name, Predicate predicate);
    
    /**
     * This class provides a bridge between the parent and derived active
     * collections.
     *
     */
    public class ActiveCollectionAdapter implements ActiveChangeListener {
        
        private ActiveCollection _parent=null; // Strong ref to ensure not garbage collected
                                                // while child still needs it
        private Predicate _predicate=null;
        
        /**
         * This constructor initializes the fields within the adapter.
         * 
         * @param parent The parent active collection
         * @param predicate The predicate used to filter changes applied to the
         *                          active collection
         */
        public ActiveCollectionAdapter(ActiveCollection parent,
                            Predicate predicate) {
            _parent = parent;
            _predicate = predicate;
            
            // Register to receive change notifications from parent
            // active collection
            _parent.addActiveChangeListener(this);
        }
        
        /**
         * {@inheritDoc}
         */
        public void inserted(Object key, Object value) { 
            if (_predicate.evaluate(value)) {
                insert(key, value);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void updated(Object key, Object value) {
            if (_predicate.evaluate(value)) {
                update(key, value);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void removed(Object key, Object value) {
            if (_predicate.evaluate(value)) {
                remove(key, value);
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
