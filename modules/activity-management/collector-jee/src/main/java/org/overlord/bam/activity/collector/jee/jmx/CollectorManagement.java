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
package org.overlord.bam.activity.collector.jee.jmx;

import static javax.ejb.ConcurrencyManagementType.BEAN;

import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.overlord.bam.activity.collector.ActivityUnitLogger;

/**
 * This class provides the capability to manage the Activity Collector.
 *
 */
@Singleton(name="CollectorManagement")
@ApplicationScoped
@Startup
@ConcurrencyManagement(BEAN)
public class CollectorManagement {
    
    private static final String OBJECT_NAME_DOMAIN = "overlord.bam.collector";    
    private static final String OBJECT_NAME_LOGGER = OBJECT_NAME_DOMAIN+":name=ActivityLogger";
    
    private static final Logger LOG=Logger.getLogger(CollectorManagement.class.getName());
    
    @Inject
    private ActivityUnitLogger _activityLogger=null;
    
    /**
     * The constructor.
     */
    public CollectorManagement() {
    }
    
    /**
     * The initialize method.
     */
    @PostConstruct
    public void init() {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Register the ActivityUnitLogger MBean["
                        +OBJECT_NAME_LOGGER+"]: "+_activityLogger);
        }
        
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
            ObjectName objname=new ObjectName(OBJECT_NAME_LOGGER);
            
            mbs.registerMBean(_activityLogger, objname);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                    "collector-jee.Messages").getString("COLLECTOR-JEE-1"), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @PreDestroy
    public void close() throws Exception {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Unregister the ActivityUnitLogger MBean["
                        +OBJECT_NAME_LOGGER+"]: "+_activityLogger);
        }

        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
            ObjectName objname=new ObjectName(OBJECT_NAME_LOGGER);
            
            mbs.unregisterMBean(objname); 
        } catch (Exception e) {
            LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                    "collector-jee.Messages").getString("COLLECTOR-JEE-2"), e);
        }
    }

}
