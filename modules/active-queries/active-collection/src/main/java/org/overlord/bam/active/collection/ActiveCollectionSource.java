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

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;

/**
 * This class defines an Active Collection Source that is
 * responsible for retrieving the data (with optional pre-
 * processing) to be placed within an associated active
 * collection, and then maintaining that information with
 * subsequent updates and eventual removal.
 *
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class ActiveCollectionSource {

    private static final Logger LOG=Logger.getLogger(ActiveCollectionSource.class.getName());
    
    private String _name=null;
    private ActiveCollectionType _type=ActiveCollectionType.List;
    private long _itemExpiration=0;
    private int _maxItems=0;
    private int _highWaterMark=0;
    private ActiveCollection _activeCollection=null;
    private java.util.List<AbstractActiveChangeListener> _listeners=
                    new java.util.ArrayList<AbstractActiveChangeListener>();

    /**
     * This method sets the name of the active collection that
     * this source represents.
     * 
     * @param name The active collection name
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * This method returns the name of the active collection associated
     * with this source.
     * 
     * @return The name
     */
    public String getName() {
        return (_name);
    }

    /**
     * This method returns the active collection type associated
     * with the source.
     * 
     * @return The type
     */
    public ActiveCollectionType getType() {
        return (_type);
    }
    
    /**
     * This method sets the active collection type.
     * 
     * @param type The type
     */
    public void setType(ActiveCollectionType type) {
        _type = type;
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
    public void setItemExpiration(long expire) {
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
    public void setMaxItems(int max) {
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
    public void setHighWaterMark(int highWaterMark) {
        _highWaterMark = highWaterMark;
    }
    
    /**
     * This method returns the Active Collection associated with the
     * source.
     * 
     * @return The active collection
     */
    @JsonIgnore
    public ActiveCollection getActiveCollection() {
        return (_activeCollection);
    }

    /**
     * This method sets the Active Collection associated with the
     * source.
     * 
     * @param ac The active collection
     */
    public void setActiveCollection(ActiveCollection ac) {
        _activeCollection = ac;
    }

    /**
     * This method returns the list of active change listeners to be
     * automatically registered against the active collection associated
     * with this source..
     * 
     * @return The list of active change listeners
     */
    public java.util.List<AbstractActiveChangeListener> getActiveChangeListeners() {
        return (_listeners);
    }

    /**
     * This method returns the list of active change listeners to be
     * automatically registered against the active collection associated
     * with this source..
     * 
     * @param listeners The list of active change listeners
     */
    public void setActiveChangeListeners(java.util.List<AbstractActiveChangeListener> listeners) {
        _listeners = listeners;
    }

    /**
     * This method pre-initializes the active collection source
     * in situations where it needs to be initialized before
     * registration with the manager. This may be required
     * where the registration is performed in a different
     * contextual classloader than the source was loaded.
     * 
     * @throws Exception Failed to pre-initialize
     */
    protected void preInit() throws Exception {
        
    }
    
    /**
     * This method initializes the active collection source.
     * 
     * @throws Exception Failed to initialize source
     */
    public void init() throws Exception {
        
        // If active change listeners defined, then instantiate them
        // and add them to the active collection
        if (_listeners.size() > 0) {
            
            // Check that active collection has been set
            if (_activeCollection == null) {
                throw new Exception("Active collection has not been associated with the '"
                                    +getName()+"' source");
            }
            
            for (AbstractActiveChangeListener l : _listeners) {
                _activeCollection.addActiveChangeListener(l);
            }
        }
    }

    /**
     * This method closes the active collection source.
     * 
     * @throws Exception Failed to close source
     */
    public void close() throws Exception {
        
        // Unregister any pre-defined listeners
        if (_listeners.size() > 0) {
                        
            for (AbstractActiveChangeListener l : _listeners) {
                _activeCollection.removeActiveChangeListener(l);
            }
        }
    }

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
    public void insert(Object key, Object value) {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("insert key="+key+" value="+value+" ac="+_activeCollection);
        }
        _activeCollection.insert(key, value);
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
    public void update(Object key, Object value) {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("update key="+key+" value="+value+" ac="+_activeCollection);
        }
        _activeCollection.update(key, value);        
    }

    /**
     * This method removes the supplied object from the active collection.
     * 
     * @param key The optional key, not required for lists
     * @param value The value
     */
    public void remove(Object key, Object value) {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("remove key="+key+" value="+value+" ac="+_activeCollection);
        }
        _activeCollection.remove(key, value);
    }
}
