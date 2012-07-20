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

import java.util.Iterator;
import java.util.logging.Logger;

import org.overlord.bam.active.collection.predicate.Predicate;

/**
 * This interface represents an active map.
 *
 */
public class ActiveMap extends ActiveCollection implements java.lang.Iterable<Object> {
    
    private static final int INITIAL_CAPACITY = 1000;

    private static final Logger LOG=Logger.getLogger(ActiveMap.class.getName());
    
    private java.util.Map<Object,Object> _map=new java.util.HashMap<Object,Object>(INITIAL_CAPACITY);
    private java.util.Map<Object,Long> _mapTimestamps=new java.util.HashMap<Object,Long>(INITIAL_CAPACITY);
    private java.util.List<Object> _readCopy=null;

    /**
     * This constructor initializes the active map.
     * 
     * @param name The name
     */
    public ActiveMap(String name) {
        super(name);
    }
    
    /**
     * This constructor initializes the active map.
     * 
     * @param name The name
     * @param itemExpiration Item expiration time, or 0 if not relevant
     * @param maxItems Max number of items, or 0 if not relevant
     * @param highWaterMark Generate warning if number of items exceed high water mark
     */
    public ActiveMap(String name, long itemExpiration, int maxItems,
                            int highWaterMark) {
        super(name, itemExpiration, maxItems, highWaterMark);
    }
    
    /**
     * This constructor initializes the active list.
     * 
     * @param name The name
     * @param capacity The initial capacity of the list
     */
    protected ActiveMap(String name, int capacity) {
        super(name);
        
        _map = new java.util.HashMap<Object,Object>(capacity);
        _mapTimestamps = new java.util.HashMap<Object,Long>(capacity);
    }
    
    /**
     * This constructor initializes the active map as a derived
     * version of the supplied collection, that applies the supplied predicate.
     * 
     * @param name The name
     * @param parent The parent collection
     * @param predicate The predicate
     */
    protected ActiveMap(String name, ActiveCollection parent, Predicate predicate) {
        super(name, parent, predicate);
        
        // Filter the parent map items, to determine which pass the predicate
        for (Object obj : (ActiveMap)parent) {
            Entry entry=(Entry)obj;
            
            if (predicate.evaluate(entry.getValue())) {
                insert(entry.getKey(), entry.getValue());
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public int getSize() {
        synchronized (_map) {
            return (_map.size());
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void insert(Object key, Object value) {
        
        synchronized (_map) {
            if (key != null) {
                _map.put(key, value);
                
                if (getItemExpiration() > 0) {
                    _mapTimestamps.put(key, System.currentTimeMillis());
                }
            } else {
                LOG.severe("Key not defined");
            }
            
            _readCopy = null;
        }
        
        inserted(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void update(Object key, Object value) {
        synchronized (_map) {
            if (key != null) {
                _map.put(key, value);
                
                if (getItemExpiration() > 0) {
                    _mapTimestamps.put(key, System.currentTimeMillis());
                }
            } else {
                LOG.severe("Key not defined");
            }
            
            _readCopy = null;
        }
        
        updated(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void remove(Object key, Object value) {
        
        synchronized (_map) {
            if (key != null) {
                value = _map.remove(key);
                
                if (getItemExpiration() > 0) {
                    _mapTimestamps.remove(key);
                }
            } else {
                LOG.severe("Key not defined");
            }
            
            _readCopy = null;
        }
                  
        removed(key, value);
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Object> iterator() {
        synchronized (_map) {
            if (_readCopy == null) {            
                _readCopy = new java.util.ArrayList<Object>();
                
                for (Object key : _map.keySet()) {
                    Object value=_map.get(key);
                    _readCopy.add(new Entry(key, value));
                }
            }
            
            return (_readCopy.iterator());
        }
    }

    /**
     * {@inheritDoc}
     */
    protected ActiveCollection derive(String name, Predicate predicate) {
        return (new ActiveMap(name, this, predicate));
    }
    
    /**
     * {@inheritDoc}
     */
    public java.util.List<Object> query(QuerySpec qs) {
        java.util.List<Object> ret=null;
        
        synchronized (_map) {

            // TODO: Currently does not support any of the query
            // spec constraints
            
            ret = new java.util.ArrayList<Object>();
            
            for (Object obj : this) {
                ret.add(obj);
            }
        }
        
        return (ret);
    }
    
    /**
     * {@inheritDoc}
     */
    protected void cleanup() {
        
        // TODO: Provide some separate cleanup policy class to enable
        // different policies to be used. For now just have a simple
        // directly implemented mechanism.
        
        if (getMaxItems() > 0) {
            
            synchronized (_map) {
                int num=getSize()-getMaxItems();
                
                while (num > 0) {
                    remove(0, null);
                    num--;
                }
            }
        }
        
        if (getItemExpiration() > 0) {
            
            synchronized (_map) {
                // Calculate expiration time
                long expiration = System.currentTimeMillis()-getItemExpiration();
                
                // Work through list backwards to determine if the entry
                // has expired
                for (int i=getSize()-1; i >= 0; i--) {
                    if (_mapTimestamps.get(i) < expiration) {
                        // TODO: Could do bulk remove and then
                        // send notifications all at once???
                        remove(i, null);
                    }
                }
            }
        }
    }

    /**
     * This class represents an entry in the map.
     *
     */
    public class Entry {
        
        private Object _key=null;
        private Object _value=null;
        
        /**
         * This constructor initializes the key/value pair.
         * 
         * @param key The key
         * @param value The value
         */
        public Entry(Object key, Object value) {
            _key = key;
            _value = value;
        }
        
        /**
         * This method sets the key.
         * 
         * @param key The key
         */
        public void setKey(Object key) {
            _key = key;
        }
        
        /**
         * This method gets the key.
         * 
         * @return The key
         */
        public Object getKey() {
            return (_key);
        }
        
        /**
         * This method sets the value.
         * 
         * @param value The value
         */
        public void setValue(Object value) {
            _value = value;
        }
        
        /**
         * This method gets the value.
         * 
         * @return The value
         */
        public Object getValue() {
            return (_value);
        }
    }
}
