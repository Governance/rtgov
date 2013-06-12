/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-13, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.rtgov.situation.manager.jmx;

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

import org.overlord.rtgov.analytics.situation.IgnoreSubject;
import org.overlord.rtgov.situation.manager.SituationManager;

/**
 * This class represents the management component for the situation
 * manager.
 *
 */
@Singleton(name="SituationManager")
@ApplicationScoped
@Startup
@ConcurrencyManagement(BEAN)
public class SituationMgr extends javax.management.NotificationBroadcasterSupport
                                implements SituationMgrMBean {

    private static final String OBJECT_NAME_DOMAIN = "overlord.rtgov.services";    
    private static final String OBJECT_NAME_MANAGER = ":name=SituationManager";

    private static final Logger LOG=Logger.getLogger(SituationMgr.class.getName());
    
    @Inject
    private SituationManager _situationManager=null;
    
    /**
     * The initialize method.
     */
    @PostConstruct
    public void init() {
        LOG.info("Register the Situation Manager MBean: "+_situationManager);

        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
            ObjectName objname=new ObjectName(OBJECT_NAME_DOMAIN+OBJECT_NAME_MANAGER);
            
            mbs.registerMBean(this, objname); 
        } catch (Exception e) {
            LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                    "situation-manager.Messages").getString("SITUATION-MANAGER-7"), e);
        }
    }
        
    /**
     * {@inheritDoc}
     */
    @PreDestroy
    public void close() throws Exception {
        LOG.info("Unregister the Situation Manager MBean");

        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
            ObjectName objname=new ObjectName(OBJECT_NAME_DOMAIN+OBJECT_NAME_MANAGER);
            
            mbs.unregisterMBean(objname); 
        } catch (Exception e) {
            LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                    "situation-manager.Messages").getString("SITUATION-MANAGER-8"), e);
        }
    }
    
    /**
     * This method sets the situation manager.
     * 
     * @param sm The situation manager
     */
    public void setSituationManager(SituationManager sm) {
        _situationManager = sm;
    }

    /**
     * This method returns the situation manager.
     * 
     * @return The situation manager
     */
    public SituationManager getSituationManager() {
        return (_situationManager);
    }

    /**
     * {@inheritDoc}
     */
    public void ignore(String subject, String reason) throws Exception {
        if (_situationManager != null) {
            IgnoreSubject is=new IgnoreSubject();
            is.setSubject(subject);
            is.setReason(reason);
            is.setTimestamp(System.currentTimeMillis());
            
            _situationManager.ignore(is);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void observe(String subject) throws Exception {
        if (_situationManager != null) {
            _situationManager.observe(subject, null);
        }
    }
    
}
