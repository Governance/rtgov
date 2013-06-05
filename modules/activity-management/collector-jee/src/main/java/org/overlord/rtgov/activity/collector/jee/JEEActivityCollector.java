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
package org.overlord.rtgov.activity.collector.jee;

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
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.overlord.rtgov.activity.collector.AbstractActivityCollector;
import org.overlord.rtgov.activity.collector.ActivityCollector;

/**
 * This class provides a JEE implementation of the activity
 * collector interface.
 *
 */
@Singleton(name="ActivityCollector")
@ApplicationScoped
@Startup
@ConcurrencyManagement(BEAN)
public class JEEActivityCollector extends AbstractActivityCollector
                        implements ActivityCollector {

    private static final Logger LOG=Logger.getLogger(JEEActivityCollector.class.getName());

    private static final String OBJECT_NAME_DOMAIN = "overlord.rtgov.collector";    
    private static final String OBJECT_NAME_COLLECTOR = OBJECT_NAME_DOMAIN+":name=ActivityCollector";
    private static final String OBJECT_NAME_LOGGER = OBJECT_NAME_DOMAIN+":name=ActivityLogger";
    
    /**
     * The initialize method.
     */
    @PostConstruct
    public void init() {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
            
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Register the ActivityCollector MBean["
                            +OBJECT_NAME_COLLECTOR+"]: "+this);
            }
            
            ObjectName objname1=new ObjectName(OBJECT_NAME_COLLECTOR);            
            mbs.registerMBean(this, objname1);

            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Register the ActivityUnitLogger MBean["
                            +OBJECT_NAME_LOGGER+"]: "+getActivityUnitLogger());
            }
            
            ObjectName objname2=new ObjectName(OBJECT_NAME_LOGGER);            
            mbs.registerMBean(getActivityUnitLogger(), objname2);

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
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 

            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Unregister the ActivityCollector MBean["
                            +OBJECT_NAME_COLLECTOR+"]: "+this);
            }

            ObjectName objname1=new ObjectName(OBJECT_NAME_COLLECTOR);            
            mbs.unregisterMBean(objname1);
            
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Unregister the ActivityUnitLogger MBean["
                            +OBJECT_NAME_LOGGER+"]: "+getActivityUnitLogger());
            }

            ObjectName objname2=new ObjectName(OBJECT_NAME_LOGGER);            
            mbs.unregisterMBean(objname2);
            
        } catch (Throwable t) {
            if (LOG.isLoggable(Level.FINER)) {
                LOG.log(Level.FINER, java.util.PropertyResourceBundle.getBundle(
                    "collector-jee.Messages").getString("COLLECTOR-JEE-2"), t);
            }
        }
    }
}
