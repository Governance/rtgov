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

import org.codehaus.jackson.annotate.JsonTypeInfo;

/**
 * This class provides the base definition for an Active Collection Source.
 *
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class DefaultActiveCollectionSource implements ActiveCollectionSource {

    private String _name=null;
    private ActiveCollectionType _type=ActiveCollectionType.List;
    private long _itemExpiration=0;
    private int _maxItems=0;
    private ActiveCollection _activeCollection=null;

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
     * {@inheritDoc}
     */
    public String getName() {
        return (_name);
    }

    /**
     * {@inheritDoc}
     */
    public ActiveCollectionType getType() {
        return (_type);
    }
    
    /**
     * This method sets the active collection type.
     * 
     * @param type The type
     */
    public void setActiveCollectionType(ActiveCollectionType type) {
        _type = type;
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    public ActiveCollection getActiveCollection() {
        return (_activeCollection);
    }

    /**
     * {@inheritDoc}
     */
    public void setActiveCollection(ActiveCollection ac) {
        _activeCollection = ac;
    }

    /**
     * {@inheritDoc}
     */
    public void init() throws Exception {
        
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws Exception {
        
    }

    /**
     * {@inheritDoc}
     */
    public void insert(Object key, Object value) {
        _activeCollection.insert(key, value);
    }

    /**
     * {@inheritDoc}
     */
    public void update(Object key, Object value) {
        _activeCollection.update(key, value);
        
    }

    /**
     * {@inheritDoc}
     */
    public void remove(Object key, Object value) {
        _activeCollection.remove(key, value);
    }
}
