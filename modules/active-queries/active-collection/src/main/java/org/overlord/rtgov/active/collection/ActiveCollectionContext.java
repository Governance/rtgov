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
package org.overlord.rtgov.active.collection;


/**
 * This interface represents the context used by the active collections,
 * e.g. to help predicates evaluate objects being applied to the active collections.
 *
 */
public interface ActiveCollectionContext {

    /**
     * This method returns the named active list.
     * 
     * @param name The name of the active list
     * @return The active list, or null if not found
     */
    public ActiveList getList(String name);
    
    /**
     * This method returns the named active map.
     * 
     * @param name The name of the active map
     * @return The active map, or null if not found
     */
    public ActiveMap getMap(String name);
    
}
