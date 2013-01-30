/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-12, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.bam.common.infinispan;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;

import org.infinispan.manager.CacheContainer;
import org.infinispan.manager.DefaultCacheManager;
import org.overlord.bam.common.util.BAMProperties;

/**
 * This class provides utility functions for working with Infinispan.
 *
 */
public final class InfinispanManager {

    private static final String INFINISPAN_CONTAINER = "infinispan.container";

    private static final String INFINISPAN_CONFIG = "bam-infinispan.xml";
    
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
        
        // If container not defined, and default container not initialized,
        // then check if default container name has been defined in the BAM
        // properties
        if (container == null && _cacheContainer == null) {
            container = BAMProperties.getProperty(INFINISPAN_CONTAINER);
            
            // If default container retrieved from bam properties, then
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
                        "bam-infinspan.Messages").getString("BAM-INFINISPAN-1"),
                        container), e);
            }
        } else {
            if (_cacheContainer == null) {
                try {
                    _cacheContainer = new DefaultCacheManager(INFINISPAN_CONFIG);
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                            "bam-infinspan.Messages").getString("BAM-INFINISPAN-2"),
                            container), e);
                }
            }
            
            ret = (CacheContainer)_cacheContainer;
        }
        
        return (ret);
    }
}
