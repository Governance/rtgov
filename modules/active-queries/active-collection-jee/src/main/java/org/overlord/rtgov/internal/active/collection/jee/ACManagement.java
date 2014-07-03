/*
 * 2012-3 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.rtgov.internal.active.collection.jee;

import static javax.ejb.ConcurrencyManagementType.BEAN;

import java.lang.management.ManagementFactory;
import java.text.MessageFormat;
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

import org.overlord.rtgov.active.collection.ActiveChangeListener;
import org.overlord.rtgov.active.collection.ActiveCollection;
import org.overlord.rtgov.active.collection.ActiveCollectionListener;
import org.overlord.rtgov.active.collection.ActiveCollectionManager;
import org.overlord.rtgov.active.collection.jmx.JMXNotifier;

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
    
    private static final String OBJECT_NAME_DOMAIN = "overlord.rtgov.collections";    
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
     * This method sets the active collection manager.
     * 
     * @param acm The active collection manager
     */
    public void setActiveCollectionManager(ActiveCollectionManager acm) {
        _acManager = acm;
    }
    
    /**
     * This method returns the active collection manager.
     * 
     * @return The active collection manager
     */
    public ActiveCollectionManager getActiveCollectionManager() {
        return (_acManager);
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
            LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                    "active-collection-jee.Messages").getString("ACTIVE-COLLECTION-JEE-1"), e);
        }
        
        _acManager.addActiveCollectionListener(this);
        
    }

    /**
     * {@inheritDoc}
     */
    @PreDestroy
    public void close() throws Exception {
        LOG.info("Unregister the ACManagement MBean");

        /* BAM-22 - comment out for now, as only an issue on shutdown
        try {
            _acManager.removeActiveCollectionListener(this);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                        "active-collection-jee.Messages").getString("ACTIVE-COLLECTION-JEE-2"), e);
        }
        */
        
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
            ObjectName objname=new ObjectName(OBJECT_NAME_DOMAIN+OBJECT_NAME_MANAGER);
            
            mbs.unregisterMBean(objname); 
        } catch (Exception e) {
            LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                    "active-collection-jee.Messages").getString("ACTIVE-COLLECTION-JEE-3"), e);
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
            LOG.log(Level.SEVERE, MessageFormat.format(
                    java.util.PropertyResourceBundle.getBundle(
                    "active-collection-jee.Messages").getString("ACTIVE-COLLECTION-JEE-4"),
                    ac.getName()), e);
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
            
        } catch (Throwable t) {
            if (LOG.isLoggable(Level.FINER)) {
                LOG.log(Level.FINER, MessageFormat.format(
                    java.util.PropertyResourceBundle.getBundle(
                    "active-collection.Messages").getString("ACTIVE-COLLECTION-JEE-5"),
                    ac.getName()), t);
            }
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
