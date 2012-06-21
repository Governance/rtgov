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
package org.overlord.bam.epn.jmx;

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

import org.overlord.bam.epn.EPNManager;
import org.overlord.bam.epn.NetworkInfo;

/**
 * This class provides the capability to manage the EPN Manager.
 *
 */
@Singleton(name="EPNManagement")
@ApplicationScoped
@Startup
@ConcurrencyManagement(BEAN)
public class EPNManagement extends javax.management.NotificationBroadcasterSupport
                        implements EPNManagementMBean {
    
    private static final String OBJECT_NAME = "bam.epn:name=EPNManager";
    
    private static final Logger LOG=Logger.getLogger(EPNManagement.class.getName());
    
    @Inject
    private EPNManager _epnManager;

    /**
     * The constructor.
     */
    public EPNManagement() {
    }
    
    /**
     * The initialize method.
     */
    @PostConstruct
    public void init() {
        LOG.info("Register the EPNManager MBean: "+_epnManager);
        
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
            ObjectName objname=new ObjectName(OBJECT_NAME);
            
            mbs.registerMBean(this, objname); 
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to register MBean for EPNManager", e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public java.util.List<String> getNetworks() {
        java.util.List<String> ret=new java.util.ArrayList<String>();
        
        for (NetworkInfo ni : _epnManager.getNetworkInfo()) {
            ret.add(ni.toString());
        }
        
        return (ret);
    }


    /**
     * {@inheritDoc}
     */
    @PreDestroy
    public void close() throws Exception {
        LOG.info("Unregister the EPNManager MBean");
        
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
            ObjectName objname=new ObjectName(OBJECT_NAME);
            
            mbs.unregisterMBean(objname); 
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to unregister MBean for EPNManager", e);
        }
    }
}
