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
package org.overlord.rtgov.jee;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;

import org.overlord.rtgov.active.collection.ActiveCollection;
import org.overlord.rtgov.active.collection.ActiveCollectionManager;
import org.overlord.rtgov.active.collection.ActiveList;
import org.overlord.rtgov.active.collection.ActiveMap;

/**
 * This interface provides access to active collections.
 *
 */
public class DefaultCollectionManager implements CollectionManager {

    private static final Logger LOG=Logger.getLogger(DefaultCollectionManager.class.getName());
    
    private static final String ACTIVE_COLLECTION_MANAGER = "java:global/overlord-rtgov/ActiveCollectionManager";

    private ActiveCollectionManager _activeCollectionManager=null;
    
    private boolean _initialized=false;

    @PostConstruct
    protected void init() {
        if (_activeCollectionManager == null) {
            try {
                InitialContext ctx=new InitialContext();
                
                _activeCollectionManager = (ActiveCollectionManager)ctx.lookup(ACTIVE_COLLECTION_MANAGER);
                
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Failed to initialize active collection manager", e);
            }
        }
        
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("*********** DefaultCollectionManager Initialized with acm="
                        +_activeCollectionManager);
        }
        
        _initialized = true;
    }

    /**
     * This method returns the active list associated with the
     * supplied name.
     * 
     * @param name The name
     * @return The list, or null if not found
     */
    public ActiveList getList(String name) {
        ActiveCollection ac=getCollection(name);
        
        if (ac instanceof ActiveList) {
            return ((ActiveList)ac);
        }
        
        return null;
    }
    
    /**
     * This method returns the active map associated with the
     * supplied name.
     * 
     * @param name The name
     * @return The map, or null if not found
     */
    public ActiveMap getMap(String name) {
        ActiveCollection ac=getCollection(name);
        
        if (ac instanceof ActiveMap) {
            return ((ActiveMap)ac);
        }
        
        return null;
    }
    
    /**
     * This method returns the active collection associated with the
     * supplied name.
     * 
     * @param name The name
     * @return The collection, or null if not found
     */
    protected ActiveCollection getCollection(String name) {
        
        if (!_initialized) {
            init();
        }
        
        if (_activeCollectionManager != null) {
            return (_activeCollectionManager.getActiveCollection(name));
        }
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Active collection manager not initialized");
        }
        
        return null;
    }
    
}
