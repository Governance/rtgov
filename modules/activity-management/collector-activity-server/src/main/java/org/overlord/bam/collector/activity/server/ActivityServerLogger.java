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
package org.overlord.bam.collector.activity.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.overlord.bam.activity.model.ActivityUnit;
import org.overlord.bam.activity.server.ActivityServer;
import org.overlord.bam.collector.BatchedActivityLogger;

/**
 * This class provides a bridge between the Collector and Activity Server,
 * where activity events are logged, causing them to be sent to the
 * configured activity server.
 *
 */
public class ActivityServerLogger extends BatchedActivityLogger {

    private static final Logger LOG=Logger.getLogger(ActivityServerLogger.class.getName());
    
    @Inject
    private ActivityServer _activityServer=null;
    
    private java.util.List<ActivityUnit> _activities=null;
    
    /**
     * This method initializes the Activity Server Logger.
     */
    @PostConstruct
    public void init() {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Initialize Logger for Activity Server: "+_activityServer);
        }
        super.init();
    }
    
    /**
     * {@inheritDoc}
     */
    protected void appendActivity(ActivityUnit act) throws Exception {
        if (_activities == null) {
            _activities = new java.util.Vector<ActivityUnit>();
        }
        _activities.add(act);
    }

    /**
     * {@inheritDoc}
     */
    protected void sendMessage() throws Exception {
        if (_activities != null) {
            _activityServer.store(_activities);
            
            // Clear the list
            _activities.clear();
        }
    }

    /**
     * This method closes the Activity Server Logger.
     */
    @PreDestroy
    public void close() {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Close Logger for Activity Server: "+_activityServer);
        }
        super.close();
    }
}
