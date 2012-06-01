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

/**
 * This interface defines an Active Collection Source that is
 * responsible for retrieving the data (with optional pre-
 * processing) to be placed within an associated active
 * collection, and then maintaining that information with
 * subsequent updates and eventual removal.
 *
 */
public interface ActiveCollectionSource {

    /**
     * This method returns the name of the active collection associated
     * with this source.
     * 
     * @return The name
     */
    public String getName();
    
    /**
     * This method returns the active collection type associated
     * with the source.
     * 
     * @return The type
     */
    public ActiveCollectionType getType();
    
    /**
     * This method returns the item expiration duration.
     * 
     * @return The number of milliseconds that the item should remain
     *          in the active collection, or 0 if not relevant
     */
    public long getItemExpiration();
    
    /**
     * This method returns the maximum number of items that should be
     * contained within the active collection. The default policy will
     * be to remove oldest entry when maximum number is reached.
     * 
     * @return The maximum number of items, or 0 if not relevant
     */
    public int getMaxItems();
 
    /**
     * This method returns the Active Collection associated with the
     * source.
     * 
     * @return The active collection
     */
    public ActiveCollection getActiveCollection();
    
    /**
     * This method sets the Active Collection associated with the
     * source.
     * 
     * @param ac The active collection
     */
    public void setActiveCollection(ActiveCollection ac);
    
    /**
     * This method initializes the active collection source.
     * 
     * @throws Exception Failed to initialize source
     */
    public void init() throws Exception;
    
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
    public void insert(Object key, Object value);
    
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
    public void update(Object key, Object value);
    
    /**
     * This method removes the supplied object from the active collection.
     * 
     * @param key The optional key, not required for lists
     * @param value The value
     */
    public void remove(Object key, Object value);
    
    /**
     * This method closes the active collection source.
     * 
     * @throws Exception Failed to close source
     */
    public void close() throws Exception;
    
}
