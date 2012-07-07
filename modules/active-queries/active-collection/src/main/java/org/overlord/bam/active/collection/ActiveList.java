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
     * This constructor initializes the active list.
     * 
     * @param name The name
     * @param itemExpiration Item expiration time, or 0 if not relevant
     * @param maxItems Max number of items, or 0 if not relevant
     * @param highWaterMark Generate warning if number of items exceed high water mark
     */
    public ActiveList(String name, long itemExpiration, int maxItems,
                            int highWaterMark) {
        super(name, itemExpiration, maxItems, highWaterMark);
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
     * @param predicate The predicate
     */
    protected ActiveList(String name, ActiveCollection parent, Predicate predicate) {
        super(name, parent, predicate);
        
        // Filter the parent list items, to determine which pass the predicate
        for (Object value : (ActiveList)parent) {
            if (predicate.evaluate(value)) {
                insert(null, value);
            }
        }
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
        synchronized (_list) {
            return (_list.size());
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void insert(Object key, Object value) {
        
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
                LOG.severe("Unknown key type '"+key+"' - should be integer");
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
        synchronized (_list) {
            if (key != null) {
                if (key instanceof Integer) {
                    _list.set((Integer)key, value);
                    
                    if (getItemExpiration() > 0) {
                        _listTimestamps.set((Integer)key, System.currentTimeMillis());
                    }
                    
                    updated(key, value);
                } else {
                    LOG.severe("Unknown key '"+key+"' for update");
                }
            } else {
                // Can only assume that value maintains its own identity
                int index=_list.indexOf(value);
                
                if (index == -1) {
                    // Can't find updated entry, so log error
                    LOG.severe("Unable to find list entry for value in list '"+getName()+"': "+value);
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected void remove(Object key, Object value) {
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
                LOG.severe("Unable to remove value from list '"+getName()+"': "+value);
            }
            _readCopy = null;
        }
        
        if (pos != -1) {
            removed(pos, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Object> iterator() {
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
    }

    /**
     * {@inheritDoc}
     */
    protected ActiveCollection derive(String name, Predicate predicate) {
        return (new ActiveList(name, this, predicate));
    }
    
    /**
     * {@inheritDoc}
     */
    protected void cleanup() {
        
        // TODO: Provide some separate cleanup policy class to enable
        // different policies to be used. For now just have a simple
        // directly implemented mechanism.
        
        if (getMaxItems() > 0) {
            
            synchronized (_list) {
                int num=getSize()-getMaxItems();
                
                if (num > 0) {
                    for (int i=getSize()-1; i >= getMaxItems(); i--) {
                        // TODO: Could do bulk remove and then
                        // send notifications all at once???
                        remove(i, null);
                    }
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
                        remove(i, null);
                    }
                }
            }
        }
    }

}
