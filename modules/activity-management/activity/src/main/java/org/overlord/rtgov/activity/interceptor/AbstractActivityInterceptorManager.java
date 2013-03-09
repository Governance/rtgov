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
package org.overlord.rtgov.activity.interceptor;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.common.util.VersionUtil;

/**
 * This class manages a set of ActivityInterceptor
 * implementations.
 *
 */
public abstract class AbstractActivityInterceptorManager implements ActivityInterceptorManager {
    
    private static final Logger LOG=Logger.getLogger(AbstractActivityInterceptorManager.class.getName());

    private java.util.Map<String,ActivityInterceptor> _activityInterceptorIndex=
            new java.util.HashMap<String,ActivityInterceptor>();
    private java.util.List<ActivityInterceptor> _activityInterceptors=
                new java.util.ArrayList<ActivityInterceptor>();
    
    /**
     * The default constructor.
     */
    public AbstractActivityInterceptorManager() {
    }
    
    /**
     * {@inheritDoc}
     */
    public void register(ActivityInterceptor ai) throws Exception {
        
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Register: activity interceptor name="
                        +ai.getName()+" version="
                        +ai.getVersion()+" ai="+ai);
        }
        
        // Initialize the activity interceptor
        ai.init();
        
        synchronized (_activityInterceptors) {
            boolean f_add=false;
            
            // Check if activity interceptor for same name already exists
            ActivityInterceptor existing=_activityInterceptorIndex.get(ai.getName());
            
            if (existing != null) {
                
                // Check whether newer version
                if (VersionUtil.isNewerVersion(existing.getVersion(),
                        ai.getVersion())) {
                    
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine("Replace existing activity interceptor version="
                                    +existing.getVersion());
                    }
                    
                    // Unregister old version
                    unregister(existing);
                    
                    // Add new version
                    f_add = true;                  
                } else {
                                      
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine("Newer version '"+existing.getVersion()
                                +"' already registered");
                    }
                }
            } else {
                f_add = true;
            }
            
            if (f_add) {
            	_activityInterceptorIndex.put(ai.getName(), ai);
            	_activityInterceptors.add(ai);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public ActivityInterceptor getActivityInterceptor(String name) {
        synchronized (_activityInterceptorIndex) {
            return (_activityInterceptorIndex.get(name));
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void process(ActivityType actType) throws Exception {        
        synchronized (_activityInterceptorIndex) {            
            for (int i=0; i < _activityInterceptors.size(); i++) {
            	_activityInterceptors.get(i).process(actType);
            }
        }        
    }
    
    /**
     * {@inheritDoc}
     */
    public void unregister(ActivityInterceptor ai) throws Exception {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Unregister: activity interceptor name="
                        +ai.getName()+" version="
                        +ai.getVersion()+" ai="+ai);
        }
        
        synchronized (_activityInterceptorIndex) {
            
            if (_activityInterceptors.contains(ai)) {
                ActivityInterceptor removed=
                		_activityInterceptorIndex.remove(ai.getName());
                _activityInterceptors.remove(removed);
                
            } else if (_activityInterceptorIndex.containsKey(ai.getName())) {
                
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Another version of activity interceptor name="
                            +ai.getName()+" is currently registered: existing version ="
                            +_activityInterceptorIndex.get(ai.getName()).getVersion());
                }
            }
        }
    }
    
}
