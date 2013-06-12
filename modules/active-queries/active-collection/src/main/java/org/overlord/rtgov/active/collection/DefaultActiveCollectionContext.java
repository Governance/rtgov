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
 * This class represents the default implementation of the
 * active collection context interface.
 *
 */
public class DefaultActiveCollectionContext implements ActiveCollectionContext {

    private ActiveCollectionManager _activeCollectionManager=null;
    
    /**
     * The constructor initialized with the active collection manager.
     * 
     * @param acm The active collection manager
     */
    public DefaultActiveCollectionContext(ActiveCollectionManager acm) {
        _activeCollectionManager = acm;
    }
    
    /**
     * {@inheritDoc}
     */
    public ActiveList getList(String name) {
        return ((ActiveList)_activeCollectionManager.getActiveCollection(name));
    }
    
    /**
     * {@inheritDoc}
     */
    public ActiveMap getMap(String name) {
        return ((ActiveMap)_activeCollectionManager.getActiveCollection(name));
    }
    
}
