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
