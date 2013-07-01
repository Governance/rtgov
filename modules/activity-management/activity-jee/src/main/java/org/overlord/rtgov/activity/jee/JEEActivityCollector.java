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
package org.overlord.rtgov.activity.jee;

import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.overlord.rtgov.activity.collector.AbstractActivityCollector;
import org.overlord.rtgov.activity.collector.ActivityCollector;

/**
 * This class provides a JEE implementation of the activity
 * collector interface.
 *
 */
@Singleton
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
                    "activity-jee.Messages").getString("ACTIVITY-JEE-1"), e);
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
                    "activity-jee.Messages").getString("ACTIVITY-JEE-2"), t);
            }
        }
    }
}
