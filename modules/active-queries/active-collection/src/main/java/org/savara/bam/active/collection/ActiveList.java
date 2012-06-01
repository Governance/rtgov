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
package org.savara.bam.active.collection;

import java.util.Iterator;
import java.util.logging.Logger;

/**
 * This interface represents an active list.
 *
 */
public class ActiveList extends ActiveCollection implements java.lang.Iterable<Object> {
    
    private static final Logger LOG=Logger.getLogger(ActiveList.class.getName());
    
    private java.util.List<Object> _list=new java.util.ArrayList<Object>();
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
    public int size() {
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
            } else if (key instanceof Integer) {
                _list.add(((Integer)key).intValue(), value);
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
                    _list.set(((Integer)key).intValue(), value);
                    
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
            pos = _list.indexOf(value);
            
            if (pos != -1) {
                _list.remove(pos);
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

}
