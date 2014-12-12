/*
 * 2012-4 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.overlord.rtgov.activity.validator.loader.osgi;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.overlord.commons.services.ServiceRegistryUtil;
import org.overlord.rtgov.activity.util.ActivityValidatorUtil;
import org.overlord.rtgov.activity.validator.ActivityValidator;
import org.overlord.rtgov.activity.validator.ActivityValidatorManager;

/**
 * This class provides the capability to load Activity Validators from a
 * defined file.
 *
 */
public class AVActivator implements BundleActivator {
    
    private static final Logger LOG=Logger.getLogger(AVActivator.class.getName());
    
    private static final String AV_JSON = "/av.json";
    
    private java.util.List<ActivityValidator> _activityValidators=null;
    
    private org.overlord.commons.services.ServiceListener<ActivityValidatorManager> _listener;

    /**
     * {@inheritDoc}
     */
    public void start(final BundleContext context) throws Exception {
        _listener = new org.overlord.commons.services.ServiceListener<ActivityValidatorManager>() {

            @Override
            public void registered(ActivityValidatorManager service) {
                registerActivityValidator(service);
            }

            @Override
            public void unregistered(ActivityValidatorManager service) {
                unregisterActivityValidator(service);
            }
        };
        
        ServiceRegistryUtil.addServiceListener(ActivityValidatorManager.class, _listener);
    }
    
    /**
     * This method registers the activity validator with the manager.
     * 
     * @param avManager Activity validator manager
     */
    protected void registerActivityValidator(ActivityValidatorManager avManager) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Register ActivityValidatorManager");
        }
        
        try {
            java.io.InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(AV_JSON);
            
            if (is == null) {
                LOG.severe(java.util.PropertyResourceBundle.getBundle(
                        "av-loader-osgi.Messages").getString("AV-LOADER-OSGI-1"));
            } else {
                byte[] b=new byte[is.available()];
                is.read(b);
                is.close();
                
                _activityValidators = ActivityValidatorUtil.deserializeActivityValidatorList(b);
                
                if (_activityValidators == null) {
                    LOG.severe(java.util.PropertyResourceBundle.getBundle(
                            "av-loader-osgi.Messages").getString("AV-LOADER-OSGI-2"));
                } else {
                    for (ActivityValidator ai : _activityValidators) {
                        ai.init();

                        avManager.register(ai);
                    }
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                    "av-loader-osgi.Messages").getString("AV-LOADER-OSGI-3"), e);
        }
    }

    /**
     * This method unregisters the activity validator.
     * 
     * @param avManager The activity validator manager
     */
    protected void unregisterActivityValidator(ActivityValidatorManager avManager) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Unregister ActivityValidatorManager");
        }
        
        if (avManager != null && _activityValidators != null) {
            try {
                for (ActivityValidator ai : _activityValidators) {
                    avManager.unregister(ai);
                }
                
                _activityValidators = null;
            } catch (Exception e) {
                LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                        "av-loader-osgi.Messages").getString("AV-LOADER-OSGI-4"), e);
            }
        }
    }

     /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) throws Exception {
        if (_listener != null) {
            ActivityValidatorManager acManager=ServiceRegistryUtil.getSingleService(ActivityValidatorManager.class);
            
            if (acManager != null) {
                unregisterActivityValidator(acManager);
            }
        }
        
        ServiceRegistryUtil.removeServiceListener(_listener);
        _listener = null;
    }       
}
