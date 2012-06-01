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
    
}
