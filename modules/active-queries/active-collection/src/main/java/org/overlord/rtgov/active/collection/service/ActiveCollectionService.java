/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-13, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.rtgov.active.collection.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.overlord.rtgov.active.collection.ActiveCollection;
import org.overlord.rtgov.active.collection.ActiveCollectionManager;
import org.overlord.rtgov.active.collection.ActiveCollectionManagerAccessor;
import org.overlord.rtgov.active.collection.ActiveList;
import org.overlord.rtgov.active.collection.ActiveMap;
import org.overlord.rtgov.common.service.Service;

/**
 * This class provides the RTGov (Active Collection) implementation of the
 * Service abstract class.
 *
 */
public class ActiveCollectionService extends Service {
    
    private static final Logger LOG=Logger.getLogger(ActiveCollectionService.class.getName());

    private static ActiveCollectionManager _activeCollectionManager;
    
    private boolean _initialized=false;
    
    /**
     * {@inheritDoc}
     */
    public void init() throws Exception {
        super.init();
        
        if (_activeCollectionManager == null) {
            _activeCollectionManager = ActiveCollectionManagerAccessor.getActiveCollectionManager();
        }
    }
    
    /**
     * This method sets the active collection manager.
     * 
     * @param acm The active collection manager
     */
    protected void setActiveCollectionManager(ActiveCollectionManager acm) {
        _activeCollectionManager = acm;
    }
    
    /**
     * This method returns the named active collection.
     * 
     * @param name The collection name
     * @return The active collection
     */
    protected ActiveCollection getActiveCollection(String name) {
        if (!_initialized) {
            try {
                init();
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Failed to initialize active collection for retrieve list '"+name+"'", e);
            }
        }
        
        if (_activeCollectionManager != null) {
            return (_activeCollectionManager.getActiveCollection(name));
        }
        
        return null;
    }

    /**
     * This method returns the active list associated with the
     * supplied name.
     * 
     * @param name The list name
     * @return The active list, or null if not found
     */
    public ActiveList getList(String name) {
        return ((ActiveList)getActiveCollection(name));
    }

    /**
     * This method returns the active map associated with the
     * supplied name.
     * 
     * @param name The map name
     * @return The active map, or null if not found
     */
    public ActiveMap getMap(String name) {
        return ((ActiveMap)getActiveCollection(name));
    }

}
