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
 * This interface represents the Active Collection Manager, responsible
 * for managing the active collection sources, and their associated
 * collections.
 *
 */
public interface ActiveCollectionManager {

    /**
     * This method sets the house keeping interval.
     * 
     * @param interval The interval
     */
    public void setHouseKeepingInterval(long interval);
    
    /**
     * This method gets the house keeping interval.
     * 
     * @return The interval
     */
    public long getHouseKeepingInterval();

    /**
     * This method registers an active collection listener.
     * 
     * @param l The active collection listener
     */
    public void addActiveCollectionListener(ActiveCollectionListener l);
    
    /**
     * This method unregisters an active collection listener.
     * 
     * @param l The active collection listener
     */
    public void removeActiveCollectionListener(ActiveCollectionListener l);
    
    /**
     * This method registers the active collection source with
     * the manager.
     * 
     * @param acs The active collection source
     * @throws Exception Failed to register the active collection source
     */
    public void register(ActiveCollectionSource acs) throws Exception;
    
    /**
     * This method unregisters the active collection source from
     * the manager.
     * 
     * @param acs The active collection source
     * @throws Exception Failed to unregister the active collection source
     */
    public void unregister(ActiveCollectionSource acs) throws Exception;
    
    /**
     * This method returns the active collection associated with the
     * supplied name, or null if not found.
     * 
     * @param name The name
     * @return The active collection, or null if not found
     */
    public ActiveCollection getActiveCollection(String name);
    
    /**
     * This method returns the active collections.
     * 
     * @return The active collections
     */
    public java.util.Collection<ActiveCollection> getActiveCollections();
    
    /**
     * This method derives a local active collection, from the supplied
     * parent active collection, with the supplied predicate to filter
     * results from the parent collection.
     * 
     * @param name The name of the locally maintained collection
     * @param parent The parent active collection
     * @param predicate The predicate used to filter results from the parent
     *                  before they are applied to the child
     * @return The newly created active collection
     */
    public ActiveCollection create(String name, ActiveCollection parent,
                    Predicate predicate);
    
    /**
     * This method explicitly removes the named derived active collection
     * making it unavailable to other clients. Care should be taken
     * not to remove a collection that may be used by other clients
     * in a shared environment. In a shared environment, it should be
     * left to the manager to clear up unused derived collections.
     * 
     * @param name The name of the derived collection to be removed
     */
    public void remove(String name);
    
}
