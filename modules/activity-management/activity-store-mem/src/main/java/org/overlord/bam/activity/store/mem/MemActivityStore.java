/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-11, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.bam.activity.store.mem;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Singleton;

import org.overlord.bam.activity.model.ActivityUnit;
import org.overlord.bam.activity.server.ActivityStore;
import org.overlord.bam.activity.server.QuerySpec;

/**
 * This class provides the in-memory implementation of the Activity Store.
 *
 */
@Singleton
public class MemActivityStore implements ActivityStore {
    
    private static final Logger LOG=Logger.getLogger(MemActivityStore.class.getName());

    private List<ActivityUnit> _activities=new java.util.ArrayList<ActivityUnit>();
    
    /**
     * This method clears the activity store.
     */
    public void clear() {
        _activities.clear();
    }
    
    /**
     * {@inheritDoc}
     */
    public void store(List<ActivityUnit> activities) throws Exception {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Store ("+this+") = "+activities);
        }
        
        _activities.addAll(activities);
    }

    /**
     * {@inheritDoc}
     */
    public List<ActivityUnit> query(QuerySpec query) throws Exception {
        List<ActivityUnit> ret=query.evaluate(_activities);
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Query ("+this+") "+query+" = "+ret);
        }
        
        return (ret);
    }

}
