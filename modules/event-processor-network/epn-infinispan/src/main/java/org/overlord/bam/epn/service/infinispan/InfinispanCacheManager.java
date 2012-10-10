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
import org.overlord.bam.epn.service.CacheManager;

/**
 * This class represents the MVEL implementation of the Event
 * Processor.
 *
 */
public class InfinispanCacheManager extends CacheManager {

    private static final Logger LOG=Logger.getLogger(InfinispanCacheManager.class.getName());

    private String _container=null;
    
    private org.infinispan.manager.CacheContainer _cacheContainer=null;
    
    /**
     * This method sets the container resource.
     * 
     * @param container The container resource
     */
    public void setContainer(String container) {
        _container = null;
    }
    
    /**
     * This method returns the container resource.
     * 
     * @return The container resource
     */
    public String getContainer() {
        return (_container);
    }
    
    /**
     * {@inheritDoc}
     */
    public void init() throws Exception {
        if (_container == null) {
            throw new Exception("Container has not been defined");
        }
        
        try {
            InitialContext ctx=new InitialContext();
            
            _cacheContainer = (org.infinispan.manager.CacheContainer)
                    ctx.lookup(_container);
            
        } catch (Exception e) {
            LOG.log(Level.SEVERE, MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                    "epn-infinispan.Messages").getString("EPN-INFINISPAN-1"),
                    _container), e);
            
            throw e;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public <K,V> Map<K,V> getCache(String name) {
        if (_cacheContainer == null) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Requested cache '"+name
                        +"', but no cache container ("+_container+")");
            }
            return (null);
        }
        
        Map<K,V> ret=_cacheContainer.<K,V>getCache(name);
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.fine("Returning cache '"+name
                    +"' = "+ret);
        }
        
        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    public boolean lock(String cacheName, Object key) {
        if (_cacheContainer != null) {
            Cache<Object,Object> cache=_cacheContainer.getCache(cacheName);
            
            if (cache != null) {
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
    
    /**
     * {@inheritDoc}
     */
    public void close() throws Exception {
        if (_cacheContainer != null) {
            _cacheContainer.stop();
            _cacheContainer = null;
        }
    }

}
