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
package org.overlord.rtgov.activity.validator;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.common.util.VersionUtil;

/**
 * This class manages a set of ActivityValidator
 * implementations.
 *
 */
public abstract class AbstractActivityValidatorManager implements ActivityValidatorManager {
    
    private static final Logger LOG=Logger.getLogger(AbstractActivityValidatorManager.class.getName());

    private java.util.Map<String,ActivityValidator> _activityValidatorIndex=
            new java.util.HashMap<String,ActivityValidator>();
    private java.util.List<ActivityValidator> _activityValidators=
                new java.util.ArrayList<ActivityValidator>();
    
    /**
     * The default constructor.
     */
    public AbstractActivityValidatorManager() {
    }
    
    /**
     * {@inheritDoc}
     */
    public void register(ActivityValidator ai) throws Exception {
        
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Register: activity validator name="
                        +ai.getName()+" version="
                        +ai.getVersion()+" ai="+ai);
        }
        
        // Initialize the activity validator
        ai.init();
        
        synchronized (_activityValidators) {
            boolean f_add=false;
            
            // Check if activity validator for same name already exists
            ActivityValidator existing=_activityValidatorIndex.get(ai.getName());
            
            if (existing != null) {
                
                // Check whether newer version
                if (VersionUtil.isNewerVersion(existing.getVersion(),
                        ai.getVersion())) {
                    
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine("Replace existing activity validator version="
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
                _activityValidatorIndex.put(ai.getName(), ai);
                _activityValidators.add(ai);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public ActivityValidator getActivityValidator(String name) {
        synchronized (_activityValidatorIndex) {
            return (_activityValidatorIndex.get(name));
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void validate(ActivityType actType) throws Exception {        
        synchronized (_activityValidatorIndex) {            
            for (int i=0; i < _activityValidators.size(); i++) {
                _activityValidators.get(i).validate(actType);
            }
        }        
    }
    
    /**
     * {@inheritDoc}
     */
    public void unregister(ActivityValidator ai) throws Exception {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Unregister: activity validator name="
                        +ai.getName()+" version="
                        +ai.getVersion()+" ai="+ai);
        }
        
        synchronized (_activityValidatorIndex) {
            
            if (_activityValidators.contains(ai)) {
                ActivityValidator removed=
                        _activityValidatorIndex.remove(ai.getName());
                _activityValidators.remove(removed);
                
            } else if (_activityValidatorIndex.containsKey(ai.getName())) {
                
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Another version of activity validator name="
                            +ai.getName()+" is currently registered: existing version ="
                            +_activityValidatorIndex.get(ai.getName()).getVersion());
                }
            }
        }
    }
    
}
