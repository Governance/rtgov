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
package org.overlord.rtgov.common.infinispan;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;

import org.infinispan.manager.CacheContainer;
import org.infinispan.manager.DefaultCacheManager;
import org.overlord.rtgov.common.util.RTGovProperties;

/**
 * This class provides utility functions for working with Infinispan.
 *
 */
public final class InfinispanManager {

    private static final String INFINISPAN_CONTAINER = "infinispan.container";
    private static final String INFINISPAN_CONFIG = "infinispan.config";

    // Untyped to avoid classloading issues when CDI scans classes, if Infinispan
    // is not in class path
    private static Object _cacheContainer=null;
    
    private static final Logger LOG=Logger.getLogger(InfinispanManager.class.getName());

    /**
     * Private constructor.
     */
    private InfinispanManager() {
    }
    
    /**
     * This method returns the cache container. If no JNDI name is
     * provided, then the details will be obtained from configuration.
     * 
     * @param container The optional container JNDI name
     * @return The cache container, or null if failed to obtain
     */
    public static synchronized CacheContainer getCacheContainer(String container) {
        CacheContainer ret=null;
        boolean f_initDefault=false;
        String config=null;
        
        // If container not defined, and default container not initialized,
        // then check if default container name has been defined in the RTGov
        // properties
        if (container == null && _cacheContainer == null) {
            container = RTGovProperties.getProperty(INFINISPAN_CONTAINER);
            config = RTGovProperties.getProperty(INFINISPAN_CONFIG);
            
            // If default container retrieved from RTGov properties, then
            // need to save retrieved container reference
            f_initDefault = true;
        }
        
        if (container != null) {
            try {
                InitialContext ctx=new InitialContext();
                
                ret = (org.infinispan.manager.CacheContainer)
                        ctx.lookup(container);
                
                ret.start();
                
                if (f_initDefault) {
                    _cacheContainer = ret;
                }
                
            } catch (Exception e) {
                LOG.log(Level.SEVERE, MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                        "rtgov-infinispan.Messages").getString("RTGOV-INFINISPAN-1"),
                        container), e);
            }
        } else {
            if (_cacheContainer == null) {
                ClassLoader cl=Thread.currentThread().getContextClassLoader();
                try {
                    // TODO: When infinispan updated to load its root resources from
                    // its own classloader, rather than the context classloader, this
                    // can be removed
                    Thread.currentThread().setContextClassLoader(DefaultCacheManager.class.getClassLoader());
                    
                    if (config != null) {
                        _cacheContainer = new DefaultCacheManager(config);
                    } else {
                        _cacheContainer = new DefaultCacheManager();
                    }
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                            "rtgov-infinispan.Messages").getString("RTGOV-INFINISPAN-2"),
                            container), e);
                } finally {
                    Thread.currentThread().setContextClassLoader(cl);
                }
            }
            
            ret = (CacheContainer)_cacheContainer;
        }
        
        return (ret);
    }
}
