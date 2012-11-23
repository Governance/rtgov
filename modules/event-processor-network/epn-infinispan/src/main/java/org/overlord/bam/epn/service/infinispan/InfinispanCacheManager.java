/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-11, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.bam.epn.service.infinispan;

import java.text.MessageFormat;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;

import org.infinispan.Cache;
import org.infinispan.transaction.TransactionMode;
import org.infinispan.manager.CacheContainer;
import org.overlord.bam.epn.service.CacheManager;

/**
 * This class represents the MVEL implementation of the Event
 * Processor.
 *
 */
public class InfinispanCacheManager extends CacheManager {

    private static final Logger LOG=Logger.getLogger(InfinispanCacheManager.class.getName());

    private String _jndiName=null;
    
    private CacheContainer _cacheContainer=null;
    
    /**
     * This method sets the JNDI name for the container resource.
     * 
     * @param jndiName The JNDI name for the container resource
     */
    public void setJNDIName(String jndiName) {
        _jndiName = jndiName;
    }
    
    /**
     * This method returns the JNDI name used to obtain
     * the container resource.
     * 
     * @return The JNDI name for the container resource
     */
    public String getJNDIName() {
        return (_jndiName);
    }
    
    /**
     * This method returns the cache container for the current thread.
     * 
     * @return The cache container
     */
    protected CacheContainer getCacheContainer() {
        if (_cacheContainer == null) {
            if (_jndiName != null) {
                try {
                    InitialContext ctx=new InitialContext();
                    
                    _cacheContainer = (org.infinispan.manager.CacheContainer)
                            ctx.lookup(_jndiName);
                    
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                            "epn-infinispan.Messages").getString("EPN-INFINISPAN-1"),
                            _jndiName), e);
                }
            } else {
                try {
                    _cacheContainer = org.overlord.bam.common.util.InfinispanUtil.getCacheContainer();
                    
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                            "epn-infinispan.Messages").getString("EPN-INFINISPAN-2"), e);
                }
            }
        }
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Returning cache container [jndi="+_jndiName+"] = "+_cacheContainer);
        }
        
        return (_cacheContainer);
    }
    
    /**
     * {@inheritDoc}
     */
    public <K,V> Map<K,V> getCache(String name) {
        CacheContainer container=getCacheContainer();
        
        if (container == null) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Requested cache '"+name
                        +"', but no cache container ("+_jndiName+")");
            }
            return (null);
        }
        
        Map<K,V> ret=container.<K,V>getCache(name);
        
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Returning cache '"+name
                    +"' = "+ret);
        }

        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    public boolean lock(String cacheName, Object key) {

        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("About to lock: "+cacheName+" key="+key);
        }
        
        CacheContainer container=getCacheContainer();
        
        if (container != null) {
            Cache<Object,Object> cache=container.getCache(cacheName);
            
            if (cache != null) {
                
                // Check if cache is transactional
                if (cache.getAdvancedCache().getCacheConfiguration().
                            transaction().transactionMode() !=
                                TransactionMode.TRANSACTIONAL) {
                    if (LOG.isLoggable(Level.FINEST)) {
                        LOG.finest("Not transactional, so returning true");
                    }

                    return true;
                }
                
                boolean ret=cache.getAdvancedCache().lock(key);
                
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("Lock '"+cacheName
                        +"' key '"+key+"' = "+ret);
                }

                return (ret);
                
            } else if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Cannot lock cache '"+cacheName
                        +"' key '"+key+"' as cache does not exist");
            }
        } else if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Cannot lock cache '"+cacheName
                    +"' key '"+key+"' as no container");
        }
        
        return false;
    }

}
