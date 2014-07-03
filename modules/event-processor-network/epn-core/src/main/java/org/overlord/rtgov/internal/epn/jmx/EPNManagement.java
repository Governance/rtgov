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
package org.overlord.rtgov.internal.epn.jmx;

import java.lang.management.ManagementFactory;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.overlord.rtgov.epn.EPNManager;
import org.overlord.rtgov.epn.Network;
import org.overlord.rtgov.epn.NetworkListener;

/**
 * This class provides the capability to manage the EPN Manager.
 *
 */
public class EPNManagement extends javax.management.NotificationBroadcasterSupport
                        implements EPNManagementMBean, NetworkListener {
    
    private static final String OBJECT_NAME_DOMAIN = "overlord.rtgov.networks";    
    private static final String OBJECT_NAME_MANAGER = ":name=EPNManager";
    
    private static final Logger LOG=Logger.getLogger(EPNManagement.class.getName());
    
    private EPNManager _epnManager;
    
    private int _numOfNetworks=0;

    /**
     * The constructor.
     * 
     * @param epnManager The EPN Manager
     */
    public EPNManagement(EPNManager epnManager) {
        _epnManager = epnManager;
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
                    "epn-core.Messages").getString("EPN-CORE-15"), e);
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
                    "epn-container-jee.Messages").getString("EPN-CORE-16"), e);
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
                    "epn-container-jee.Messages").getString("EPN-CORE-17"),
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
        } catch (Throwable t) {
            if (LOG.isLoggable(Level.FINER)) {
                LOG.log(Level.FINER, MessageFormat.format(
                    java.util.PropertyResourceBundle.getBundle(
                    "epn-container-jee.Messages").getString("EPN-CORE-18"),
                    network.getName(), network.getVersion()), t);
            }
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
