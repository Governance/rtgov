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
import org.overlord.rtgov.activity.util.ActivityValidatorUtil;
import org.overlord.rtgov.activity.validator.ActivityValidator;
import org.overlord.rtgov.activity.validator.ActivityValidatorManager;
import org.overlord.rtgov.activity.validator.ActivityValidatorManagerAccessor;

/**
 * This class provides the capability to load Activity Validators from a
 * defined file.
 *
 */
public class AVActivator implements BundleActivator {
    
    private static final Logger LOG=Logger.getLogger(AVActivator.class.getName());
    
    private static final String AV_JSON = "/av.json";
    
    private ActivityValidatorManager _avManager=null;

    private java.util.List<ActivityValidator> _activityValidators=null;
    
    /**
     * {@inheritDoc}
     */
    public void start(final BundleContext context) throws Exception {
        
        _avManager = ActivityValidatorManagerAccessor.getActivityValidatorManager();
        
        if (_avManager == null) {
            LOG.severe(java.util.PropertyResourceBundle.getBundle(
                "av-loader-osgi.Messages").getString("AV-LOADER-OSGI-5"));
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

                        _avManager.register(ai);
                    }
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                    "av-loader-osgi.Messages").getString("AV-LOADER-OSGI-3"), e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) throws Exception {
        
        if (_avManager != null && _activityValidators != null) {
            try {
                for (ActivityValidator ai : _activityValidators) {
                    _avManager.unregister(ai);
                }
            } catch (Throwable t) {
                if (LOG.isLoggable(Level.FINER)) {
                    LOG.log(Level.FINER, java.util.PropertyResourceBundle.getBundle(
                        "av-loader-osgi.Messages").getString("AV-LOADER-OSGI-4"), t);
                }
            }
        }
    }       
}
