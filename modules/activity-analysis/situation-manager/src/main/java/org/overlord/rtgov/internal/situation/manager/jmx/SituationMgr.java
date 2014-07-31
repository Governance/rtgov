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
package org.overlord.rtgov.internal.situation.manager.jmx;

import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.overlord.rtgov.analytics.situation.IgnoreSubject;
import org.overlord.rtgov.situation.manager.SituationManager;

/**
 * This class represents the management component for the situation
 * manager.
 *
 */
@Deprecated
public class SituationMgr implements SituationMgrMBean {

    private static final String OBJECT_NAME_DOMAIN = "overlord.rtgov.services";    
    private static final String OBJECT_NAME_MANAGER = ":name=SituationManager";

    private static final Logger LOG=Logger.getLogger(SituationMgr.class.getName());
    
    private SituationManager _situationManager=null;
    
    /**
     * Constructor.
     * 
     * @param sm The situation manager
     */
    public SituationMgr(SituationManager sm) {
        _situationManager = sm;
    }
    
    /**
     * The initialize method.
     */
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
