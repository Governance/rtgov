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
package org.overlord.bam.active.collection.jee;

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
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.InitialContext;

import org.overlord.bam.active.collection.ActiveChangeListener;
import org.overlord.bam.active.collection.ActiveCollection;
import org.overlord.bam.active.collection.ActiveCollectionListener;
import org.overlord.bam.active.collection.ActiveCollectionManager;
import org.overlord.bam.active.collection.jmx.JMXNotifier;

/**
 * This class provides the capability to manage the Active Collection Manager.
 *
 */
@Singleton(name="ACManagement")
@ApplicationScoped
@Startup
@ConcurrencyManagement(BEAN)
public class ACManagement extends javax.management.NotificationBroadcasterSupport
                        implements ACManagementMBean, ActiveCollectionListener {
    
    private static final String OBJECT_NAME_DOMAIN = "overlord.bam.collections";    
    private static final String OBJECT_NAME_MANAGER = ":name=CollectionManager";
    
    private static final Logger LOG=Logger.getLogger(ACManagement.class.getName());
    
    @Inject
    private ActiveCollectionManager _acManager;
    
    /**
     * The constructor.
     */
    public ACManagement() {
    }
    
    /**
     * The initialize method.
     */
    @PostConstruct
    public void init() {
        LOG.info("Register the ACManagement MBean: "+_acManager);
        
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
            ObjectName objname=new ObjectName(OBJECT_NAME_DOMAIN+OBJECT_NAME_MANAGER);
            
            mbs.registerMBean(this, objname); 
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to register MBean for ACManagement", e);
        }
        
        _acManager.addActiveCollectionListener(this);
        
    }

    /**
     * {@inheritDoc}
     */
    @PreDestroy
    public void close() throws Exception {
        LOG.info("Unregister the ACManagement MBean");

        try {
            BeanManager bm=InitialContext.doLookup("java:comp/BeanManager");
            
            java.util.Set<Bean<?>> beans=bm.getBeans(ActiveCollectionManager.class);
            
            if (beans.size() > 0) {
                _acManager.removeActiveCollectionListener(this);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to unregister active collection listener", e);
        }
        
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
            ObjectName objname=new ObjectName(OBJECT_NAME_DOMAIN+OBJECT_NAME_MANAGER);
            
            mbs.unregisterMBean(objname); 
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to unregister MBean for ACManagement", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void registered(ActiveCollection ac) {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
            
            mbs.registerMBean(ac, getObjectName(ac)); 
            
            // Check whether the active collection has a JMXNotifier
            // registered as a listener
            for (ActiveChangeListener l : ac.getActiveChangeListeners()) {
                if (l instanceof JMXNotifier) {
                    mbs.registerMBean(l, new ObjectName(((JMXNotifier)l).getObjectName()));
                }
            }
            
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to register MBean for active collection '"
                        +ac.getName()+"'", e);
        }   
    }

    /**
     * This method creates the MBean object name for the supplied
     * active collection.
     * 
     * @param ac The active collection
     * @return The object name
     * @throws Exception Failed to create object name
     */
    protected ObjectName getObjectName(ActiveCollection ac) throws Exception {
        return (new ObjectName(OBJECT_NAME_DOMAIN+":name="+ac.getName()));
    }
    
    /**
     * {@inheritDoc}
     */
    public void unregistered(ActiveCollection ac) {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
            
            mbs.unregisterMBean(getObjectName(ac)); 

            // Check whether the active collection has a JMXNotifier
            // registered as a listener
            for (ActiveChangeListener l : ac.getActiveChangeListeners()) {
                if (l instanceof JMXNotifier) {
                    mbs.unregisterMBean(new ObjectName(((JMXNotifier)l).getObjectName()));
                }
            }
            
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to unregister MBean for active collection '"
                        +ac.getName()+"'", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setHouseKeepingInterval(long interval) {
        _acManager.setHouseKeepingInterval(interval);
    }

    /**
     * {@inheritDoc}
     */
    public long getHouseKeepingInterval() {
        return (_acManager.getHouseKeepingInterval());
    }
}
