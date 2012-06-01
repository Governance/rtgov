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
 * This interface represents the Active Collection Manager, responsible
 * for managing the active collection sources, and their associated
 * collections.
 *
 */
public interface ActiveCollectionManager {

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
    
}
