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
package org.overlord.bam.epn.jee.jmx;

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

import org.overlord.bam.epn.EPNManager;
import org.overlord.bam.epn.Network;
import org.overlord.bam.epn.NetworkListener;

/**
 * This class provides the capability to manage the EPN Manager.
 *
 */
@Singleton(name="EPNManagement")
@ApplicationScoped
@Startup
@ConcurrencyManagement(BEAN)
public class EPNManagement extends javax.management.NotificationBroadcasterSupport
                        implements EPNManagementMBean, NetworkListener {
    
    private static final String OBJECT_NAME_DOMAIN = "overlord.bam.networks";    
    private static final String OBJECT_NAME_MANAGER = ":name=EPNManager";
    
    private static final Logger LOG=Logger.getLogger(EPNManagement.class.getName());
    
    @Inject
    private EPNManager _epnManager;
    
    private int _numOfNetworks=0;

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
        LOG.info("Register the EPNManagement MBean: "+_epnManager);
        
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
            ObjectName objname=new ObjectName(OBJECT_NAME_DOMAIN+OBJECT_NAME_MANAGER);
            
            mbs.registerMBean(this, objname); 
        } catch (Exception e) {
            LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                    "epn-container-jee.Messages").getString("EPN-CONTAINER-JEE-1"), e);
        }
        
        _epnManager.addNetworkListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @PreDestroy
    public void close() throws Exception {
        LOG.info("Unregister the EPNManagement MBean");

        /* BAM-22 - comment out for now, as only an issue on shutdown
        try {
            _epnManager.removeNetworkListener(this);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to unregister network listener", e);
        }
        */
        
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
            ObjectName objname=new ObjectName(OBJECT_NAME_DOMAIN+OBJECT_NAME_MANAGER);
            
            mbs.unregisterMBean(objname); 
        } catch (Exception e) {
            LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                    "epn-container-jee.Messages").getString("EPN-CONTAINER-JEE-2"), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void registered(Network network) {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
            
            mbs.registerMBean(network, getObjectName(network)); 
        } catch (Exception e) {
            LOG.log(Level.SEVERE, MessageFormat.format(
                    java.util.PropertyResourceBundle.getBundle(
                    "epn-container-jee.Messages").getString("EPN-CONTAINER-JEE-3"),
                    network.getName(), network.getVersion()), e);
        }   
        
        _numOfNetworks++;
    }

    /**
     * This method creates the MBean object name for the supplied
     * network.
     * 
     * @param network The network
     * @return The object name
     * @throws Exception Failed to create object name
     */
    protected ObjectName getObjectName(Network network) throws Exception {
        return (new ObjectName(OBJECT_NAME_DOMAIN+":name="
                +network.getName()+",version="+network.getVersion()));
    }
    
    /**
     * {@inheritDoc}
     */
    public void unregistered(Network network) {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
            
            mbs.unregisterMBean(getObjectName(network)); 
        } catch (Exception e) {
            LOG.log(Level.SEVERE, MessageFormat.format(
                    java.util.PropertyResourceBundle.getBundle(
                    "epn-container-jee.Messages").getString("EPN-CONTAINER-JEE-4"),
                    network.getName(), network.getVersion()), e);
        }
        
        _numOfNetworks--;
    }

    /**
     * {@inheritDoc}
     */
    public int getNumberOfNetworks() {
        return (_numOfNetworks);
    }
}
