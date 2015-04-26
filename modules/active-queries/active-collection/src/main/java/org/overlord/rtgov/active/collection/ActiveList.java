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
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

import org.overlord.rtgov.active.collection.QuerySpec.Style;
import org.overlord.rtgov.active.collection.QuerySpec.Truncate;
import org.overlord.rtgov.active.collection.predicate.Predicate;

/**
 * This interface represents an active list.
 *
 */
public class ActiveList extends ActiveCollection implements java.lang.Iterable<Object> {
    
    private static final int INITIAL_CAPACITY = 1000;

    private static final Logger LOG=Logger.getLogger(ActiveList.class.getName());
    
    private java.util.List<Object> _list=new java.util.ArrayList<Object>(INITIAL_CAPACITY);
    private java.util.List<Long> _listTimestamps=new java.util.ArrayList<Long>(INITIAL_CAPACITY);
    private java.util.List<Object> _readCopy=null;
    private boolean _copyOnRead=true;

    /**
     * This constructor initializes the active list.
     * 
     * @param name The name
     */
    public ActiveList(String name) {
        super(name);
    }
    
    /**
     * This constructor initializes the active collection.
     * 
     * @param acs The Active Collection source
     */
    public ActiveList(ActiveCollectionSource acs) {
        super(acs);
    }
    
    /**
     * This constructor initializes the active list.
     * 
     * @param acs The Active Collection source
     * @param list The list
     */
    public ActiveList(ActiveCollectionSource acs, java.util.List<Object> list) {
        super(acs);
        
        _list = list;
    }
    
    /**
     * This constructor initializes the active list.
     * 
     * @param name The name
     * @param capacity The initial capacity of the list
     */
    protected ActiveList(String name, int capacity) {
        super(name);
        
        _list = new java.util.ArrayList<Object>(capacity);
        _listTimestamps = new java.util.ArrayList<Long>(capacity);
    }
    
    /**
     * This constructor initializes the active list as a derived
     * version of the supplied collection, that applies the supplied predicate.
     * 
     * @param name The name
     * @param parent The parent collection
     * @param context The context
     * @param predicate The predicate
     * @param properties The optional properties
     */
    protected ActiveList(String name, ActiveCollection parent, ActiveCollectionContext context,
                            Predicate predicate, java.util.Map<String,Object> properties) {
        super(name, parent, context, predicate, properties);
        
        if (isActive()) {
            // Filter the parent list items, to determine which pass the predicate
            for (Object value : (ActiveList)parent) {
                if (predicate.evaluate(context, value)) {
                    doInsert(null, value);
                }
            }
        }
    }
    
    /**
     * This method derives the content.
     * 
     * @return The derived content
     */
    protected java.util.List<Object> deriveContent() {
        java.util.List<Object> ret=new java.util.ArrayList<Object>();
        ActiveCollection parent=getParent();
        Predicate pred=getPredicate();
        ActiveCollectionContext context=getContext();
        
        if (parent != null && pred != null) {
            for (Object value : (ActiveList)parent) {
                if (pred.evaluate(context, value)) {
                    ret.add(value);
                }
            }
        }
        
        return (ret);
    }
    
    /**
     * This method sets the copy on read flag.
     * 
     * @param b Whether to 'copy on read'
     */
    protected void setCopyOnRead(boolean b) {
        _copyOnRead = b;
    }
    
    /**
     * {@inheritDoc}
     */
    public int getSize() {
        if (!isDerived() || isActive()) {
            synchronized (_list) {
                return (_list.size());
            }
        } else {
            java.util.List<Object> derived=deriveContent();
            return (derived.size());
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doInsert(Object key, Object value) {
        
        // If top level active collection, or a derive collection that is being actively
        // maintained, then apply the insertion
        if (!isDerived() || isActive()) {
            synchronized (_list) {
                if (key == null) {
                    _list.add(value);
                    
                    if (getItemExpiration() > 0) {
                        _listTimestamps.add(System.currentTimeMillis());
                    }
                } else if (key instanceof Integer) {
                    _list.add((Integer)key, value);
                    
                    if (getItemExpiration() > 0) {
                        _listTimestamps.add((Integer)key, System.currentTimeMillis());
                    }
                } else {
                    LOG.severe(MessageFormat.format(
                            java.util.PropertyResourceBundle.getBundle(
                            "active-collection.Messages").getString("ACTIVE-COLLECTION-7"),
                            key));
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
    protected void doUpdate(Object key, Object value) {
        
        // If top level active collection, or a derive collection that is being actively
        // maintained, then apply the update
        if (!isDerived() || isActive()) {
            synchronized (_list) {
                if (key != null) {
                    if (key instanceof Integer) {
                        _list.set((Integer)key, value);
                        
                        if (getItemExpiration() > 0) {
                            _listTimestamps.set((Integer)key, System.currentTimeMillis());
                        }
                        
                        updated(key, value);
                    } else {
                        LOG.severe(MessageFormat.format(
                                java.util.PropertyResourceBundle.getBundle(
                                "active-collection.Messages").getString("ACTIVE-COLLECTION-8"),
                                key));
                    }
                } else {
                    // Can only assume that value maintains its own identity
                    int index=_list.indexOf(value);
                    
                    if (index == -1) {
                        // Can't find updated entry, so log error
                        LOG.severe(MessageFormat.format(
                                java.util.PropertyResourceBundle.getBundle(
                                "active-collection.Messages").getString("ACTIVE-COLLECTION-9"),
                                getName(), value));
                    } else {
                        _list.set(index, value);
                        
                        if (getItemExpiration() > 0) {
                            _listTimestamps.set(index, System.currentTimeMillis());
                        }
                        
                        updated(index, value);
                    }
                }
                
                _readCopy = null;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doRemove(Object key, Object value) {

        // If top level active collection, or a derive collection that is being actively
        // maintained, then apply the deletion
        if (!isDerived() || isActive()) {
            int pos=-1;
            
            synchronized (_list) {
                if (key instanceof Integer) {
                    pos = (Integer)key;
                } else {
                    pos = _list.indexOf(value);
                }
                
                if (pos != -1) {
                    _list.remove(pos);
                    
                    if (getItemExpiration() > 0) {
                        _listTimestamps.remove(pos);
                    }
                } else {
                    LOG.severe(MessageFormat.format(
                            java.util.PropertyResourceBundle.getBundle(
                            "active-collection.Messages").getString("ACTIVE-COLLECTION-10"),
                            getName(), value));
                }
                _readCopy = null;
            }
            
            if (pos != -1) {
                removed(pos, value);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Object> iterator() {
        if (!isDerived() || isActive()) {
            synchronized (_list) {
                if (_readCopy != null) {
                    return (_readCopy.iterator());
                }
                
                if (_copyOnRead) {
                    _readCopy = new java.util.ArrayList<Object>(_list);
                    
                    return (_readCopy.iterator());
                } else {
                    return (_list.iterator());
                }
            }
        } else {
            java.util.List<Object> derived=deriveContent();
            return (derived.iterator());
        }
    }

    /**
     * {@inheritDoc}
     */
    protected ActiveCollection derive(String name, ActiveCollectionContext context,
                        Predicate predicate, java.util.Map<String,Object> properties) {
        return (new ActiveList(name, this, context, predicate, properties));
    }
    
    /**
     * {@inheritDoc}
     */
    public java.util.List<Object> query(QuerySpec qs) {
        java.util.List<Object> ret=null;
        java.util.List<Object> list=_list;
        
        if (isDerived() && !isActive()) {
            list = deriveContent();
        }
        
        synchronized (list) {
            // If no max items set, or list size is smaller than the max
            // size, and the style is normal, then just copy
            if ((qs.getMaxItems() == 0 || qs.getMaxItems() >= list.size())
                               && qs.getStyle() == Style.Normal) {
                ret = new java.util.ArrayList<Object>(list);
            } else {
                int start=0;
                int end=list.size();
                
                if (qs.getMaxItems() > 0 && qs.getMaxItems() <= list.size()) {
                    if (qs.getTruncate() == Truncate.End) {
                        end = qs.getMaxItems();
                    } else {
                        start = list.size()-qs.getMaxItems();
                    }
                }
                
                ret = new java.util.ArrayList<Object>();
                
                if (qs.getStyle() == Style.Reversed) {
                    for (int i=end-1; i >= start; i--) {
                        ret.add(list.get(i));
                    }
                } else {
                    for (int i=start; i < end; i++) {
                        ret.add(list.get(i));
                    }
                }
            }
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
                
                synchronized (_list) {
                    int num=getSize()-getMaxItems();
                    
                    while (num > 0) {
                        doRemove(0, _list.get(0));
                        num--;
                    }
                }
            }
            
            if (getItemExpiration() > 0) {
                
                synchronized (_list) {
                    // Calculate expiration time
                    long expiration = System.currentTimeMillis()-getItemExpiration();
                    
                    // Work through list backwards to determine if the entry
                    // has expired
                    for (int i=getSize()-1; i >= 0; i--) {
                        if (_listTimestamps.get(i) < expiration) {
                            // TODO: Could do bulk remove and then
                            // send notifications all at once???
                            doRemove(i, _list.get(i));
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
            synchronized (_list) {
                return (_list.isEmpty());
            }
        } else {
            java.util.List<Object> derived=deriveContent();
            return (derived.isEmpty());
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(Object o) {
        if (!isDerived() || isActive()) {
            synchronized (_list) {
                return (_list.contains(o));
            }
        } else {
            java.util.List<Object> derived=deriveContent();
            return (derived.contains(o));
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
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public boolean add(Object e) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public boolean addAll(Collection<? extends Object> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        throw new UnsupportedOperationException();
    }

}
