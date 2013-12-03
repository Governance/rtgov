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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.overlord.rtgov.active.collection.predicate.Predicate;

/**
 * This interface represents an active map.
 *
 */
public class ActiveMap extends ActiveCollection
            implements java.lang.Iterable<Object>, java.util.Map<Object,Object> {
    
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
     * This constructor initializes the active collection.
     * 
     * @param acs The Active Collection source
     */
    public ActiveMap(ActiveCollectionSource acs) {
        super(acs);
    }
    
    /**
     * This constructor initializes the active map.
     * 
     * @param acs The Active Collection source
     * @param map The map
     */
    public ActiveMap(ActiveCollectionSource acs, java.util.Map<Object,Object> map) {
        super(acs);
        
        _map = map;
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
     * @param context The context
     * @param predicate The predicate
     * @param properties The optional properties
     */
    protected ActiveMap(String name, ActiveCollection parent, ActiveCollectionContext context,
                            Predicate predicate, java.util.Map<String,Object> properties) {
        super(name, parent, context, predicate, properties);
        
        // Filter the parent map items, to determine which pass the predicate
        for (Object obj : (ActiveMap)parent) {
            Entry entry=(Entry)obj;
            
            if (predicate.evaluate(context, entry.getValue())) {
                insert(entry.getKey(), entry.getValue());
            }
        }
    }
    
    /**
     * This method derives the content.
     * 
     * @return The derived content
     */
    protected java.util.Map<Object,Object> deriveContent() {
        java.util.Map<Object,Object> ret=new java.util.HashMap<Object,Object>();
        ActiveCollection parent=getParent();
        Predicate pred=getPredicate();
        ActiveCollectionContext context=getContext();
        
        if (parent != null && pred != null) {
            for (Object obj : (ActiveMap)parent) {
                Entry entry=(Entry)obj;
                
                if (pred.evaluate(context, entry.getValue())) {
                   ret.put(entry.getKey(), entry.getValue());
                }
            }
        }
        
        return (ret);
    }
    
    /**
     * This method returns the map.
     * 
     * @return The map
     */
    protected java.util.Map<Object,Object> getMap() {
        return (_map);
    }
    
    /**
     * {@inheritDoc}
     */
    public int getSize() {
        if (!isDerived() || isActive()) {
            synchronized (_map) {
                return (_map.size());
            }
        } else {
            java.util.Map<Object,Object> derived=deriveContent();
            return (derived.size());
        }
    }
    
    /**
     * This method returns the value associated with the supplied
     * key.
     * 
     * @param key The key
     * @return The value, or null if none associated with the key
     */
    public Object get(Object key) {
        if (!isDerived() || isActive()) {
            synchronized (_map) {
                return (_map.get(key));
            }
        } else {
            ActiveMap parent=(ActiveMap)getParent();
            Object ret=parent.get(key);
            
            if (getPredicate().evaluate(getContext(), ret)) {
                return (ret);
            }
            
            return (null);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void insert(Object key, Object value) {
        
        if (!isDerived() || isActive()) {
            synchronized (_map) {
                if (key != null) {
                    _map.put(key, value);
                    
                    if (getItemExpiration() > 0) {
                        _mapTimestamps.put(key, System.currentTimeMillis());
                    }
                } else {
                    LOG.severe(java.util.PropertyResourceBundle.getBundle(
                            "active-collection.Messages").getString("ACTIVE-COLLECTION-11"));
                }
                
                _readCopy = null;
            }
            
            inserted(key, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void update(Object key, Object value) {
        if (!isDerived() || isActive()) {
            synchronized (_map) {
                if (key != null) {
                    _map.put(key, value);
                    
                    if (getItemExpiration() > 0) {
                        _mapTimestamps.put(key, System.currentTimeMillis());
                    }
                } else {
                    LOG.severe(java.util.PropertyResourceBundle.getBundle(
                            "active-collection.Messages").getString("ACTIVE-COLLECTION-11"));
                }
                
                _readCopy = null;
            }
            
            updated(key, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void remove(Object key, Object value) {
        
        if (!isDerived() || isActive()) {
            synchronized (_map) {
                if (key != null) {
                    value = _map.remove(key);
                    
                    if (getItemExpiration() > 0) {
                        _mapTimestamps.remove(key);
                    }
                } else {
                    LOG.severe(java.util.PropertyResourceBundle.getBundle(
                            "active-collection.Messages").getString("ACTIVE-COLLECTION-11"));
                }
                
                _readCopy = null;
            }
                      
            removed(key, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Object> iterator() {
        if (!isDerived() || isActive()) {
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
        } else {
            java.util.Map<Object,Object> derived=deriveContent();
            java.util.List<Object> list=new java.util.ArrayList<Object>();
            
            for (Object key : derived.keySet()) {
                Object value=derived.get(key);
                list.add(new Entry(key, value));
            }
            
            return (list.iterator());
        }
    }

    /**
     * {@inheritDoc}
     */
    protected ActiveCollection derive(String name, ActiveCollectionContext context,
                    Predicate predicate, java.util.Map<String,Object> properties) {
        return (new ActiveMap(name, this, context, predicate, properties));
    }
    
    /**
     * {@inheritDoc}
     */
    public java.util.List<Object> query(QuerySpec qs) {
        java.util.List<Object> ret=new java.util.ArrayList<Object>();
        
        // TODO: Currently does not support any of the query
        // spec constraints
        
        for (Object obj : this) {
            ret.add(obj);
        }
        
        return (ret);
    }
    
    /**
     * {@inheritDoc}
     */
    protected void cleanup() {
        
        if (!isDerived() || isActive()) {
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
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        if (!isDerived() || isActive()) {
            synchronized (_map) {
                return (_map.isEmpty());
            }
        } else {
            java.util.Map<Object,Object> derived=deriveContent();
            return (derived.isEmpty());
        }
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return (getSize());
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsKey(Object key) {
        if (!isDerived() || isActive()) {
            synchronized (_map) {
                return (_map.containsKey(key));
            }
        } else {
            java.util.Map<Object,Object> derived=deriveContent();
            return (derived.containsKey(key));
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean containsValue(Object value) {
        if (!isDerived() || isActive()) {
            synchronized (_map) {            
                return (_map.containsValue(value));
            }
        } else {
            java.util.Map<Object,Object> derived=deriveContent();
            return (derived.containsValue(value));
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object put(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void putAll(Map<? extends Object, ? extends Object> m) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * {@inheritDoc}
     */
    public void clear() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Set<Object> keySet() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Collection<Object> values() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Set<java.util.Map.Entry<Object, Object>> entrySet() {
        throw new UnsupportedOperationException();
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
