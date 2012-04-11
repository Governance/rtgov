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
package org.savara.bam.activity.server.epn;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.savara.bam.activity.model.Activity;
import org.savara.bam.activity.server.spi.ActivityNotifier;
import org.savara.bam.epn.EPNManager;
import org.savara.bam.epn.internal.EventList;

/**
 * This class provides a bridge between the Activity Server, where
 * activity events are reported, and the Event Processor Network
 * which will be used to provide additional processing and analysis
 * of the activity events.
 *
 */
public class EPNActivityNotifier implements ActivityNotifier {

    private static final String NETWORK_NAME = "activities";

    private static final Logger LOG=Logger.getLogger(EPNActivityNotifier.class.getName());
    
    @Inject
    private EPNManager _epnManager=null;
    
    /**
     * This method initializes the Activity Server to EPN bridge.
     */
    @PostConstruct
    public void init() {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Initialize Activity Server to EPN bridge");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void notify(List<Activity> activities) throws Exception {
        _epnManager.enqueue(NETWORK_NAME, new EventList(activities));
    }

    /**
     * This method closes the Activity Server to EPN bridge.
     */
    @PreDestroy
    public void close() {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Initialize Activity Server to EPN (via JMS) bridge");
        }
    }
}
