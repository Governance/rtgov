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
 * This interface can be used to listen for active collections
 * being registered with the manager.
 *
 */
public interface ActiveCollectionListener {

    /**
     * This method notifies the listener that an active collection
     * has been registered.
     * 
     * @param ac The active collection
     */
    public void registered(ActiveCollection ac);
    
    /**
     * This method notifies the listener that an active collection
     * has been unregistered.
     * 
     * @param ac The active collection
     */
    public void unregistered(ActiveCollection ac);
    
}
