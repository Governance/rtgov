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
package org.overlord.rtgov.situation.manager;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.overlord.rtgov.analytics.situation.IgnoreSubject;
import org.overlord.rtgov.common.service.CacheManager;

/**
 * This class represents the default implementation of the SituationManager
 * interface.
 *
 */
public class DefaultSituationManager implements SituationManager {
    
    private static final String CACHE_NAME = "IgnoredSituationSubjects";

    private static final Logger LOG=Logger.getLogger(DefaultSituationManager.class.getName());

    @Inject
    private CacheManager _cacheManager=null;
    
    /**
     * This method sets the cache manager.
     * 
     * @param cm The cache manager
     */
    public void setCacheManager(CacheManager cm) {
        _cacheManager = cm;
    }
    
    /**
     * This method returns the cache manager.
     * 
     * @return The cache manager
     */
    public CacheManager getCacheManager() {
        return (_cacheManager);
    }
    
    /**
     * {@inheritDoc}
     */
    public void ignore(IgnoreSubject details) throws Exception {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Ignore subject with details: "+details);
        }
        
        if (_cacheManager != null) {
            java.util.Map<String,IgnoreSubject> map=_cacheManager.getCache(CACHE_NAME);
            
            if (map == null) {
                LOG.log(Level.SEVERE, MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                        "situation-manager.Messages").getString("SITUATION-MANAGER-2"),
                        CACHE_NAME));
                
                throw new Exception(MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                        "situation-manager.Messages").getString("SITUATION-MANAGER-3"),
                        details.getSubject()));
            } else {
                map.put(details.getSubject(), details);
                
                LOG.info(MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                        "situation-manager.Messages").getString("SITUATION-MANAGER-4"),
                        details.getSubject()));
            }
        } else {
            LOG.severe(java.util.PropertyResourceBundle.getBundle(
                    "situation-manager.Messages").getString("SITUATION-MANAGER-1"));
            
            throw new Exception(MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                    "situation-manager.Messages").getString("SITUATION-MANAGER-3"),
                    details.getSubject()));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void observe(String subject, String principal) throws Exception {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Observe subject: "+subject+" "+(principal == null ? ""
                    : "(performed by "+principal+")"));
        }
        
        if (_cacheManager != null) {
            java.util.Map<String,IgnoreSubject> map=_cacheManager.getCache(CACHE_NAME);
            
            if (map == null) {
                LOG.log(Level.SEVERE, MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                        "situation-manager.Messages").getString("SITUATION-MANAGER-2"),
                        CACHE_NAME));
                
                throw new Exception(MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                        "situation-manager.Messages").getString("SITUATION-MANAGER-5"),
                        subject));
            } else {
                if (map.remove(subject) == null) {
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine("Subject '"+subject+"' had not been ignored");
                    }
                }
                
                LOG.info(MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                        "situation-manager.Messages").getString("SITUATION-MANAGER-6"),
                        subject));
            }
        } else {
            LOG.severe(java.util.PropertyResourceBundle.getBundle(
                    "situation-manager.Messages").getString("SITUATION-MANAGER-1"));
            
            throw new Exception(MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                    "situation-manager.Messages").getString("SITUATION-MANAGER-5"),
                    subject));
        }
    }
    
}
