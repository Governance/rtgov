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
package org.overlord.bam.active.collection.infinispan;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;

import org.overlord.bam.active.collection.ActiveCollection;
import org.overlord.bam.active.collection.ActiveCollectionFactory;
import org.overlord.bam.active.collection.ActiveCollectionSource;
import org.overlord.bam.active.collection.ActiveCollectionType;
import org.overlord.bam.common.util.InfinispanUtil;


/**
 * This class provides the infinispan implementation of the ActiveCollectionFactory.
 *
 */
public class InfinispanActiveCollectionFactory extends ActiveCollectionFactory {
    
    private static final Logger LOG=Logger.getLogger(InfinispanActiveCollectionFactory.class.getName());

    private String _cache=null;
    private String _jndiName=null;
    private String _config=null;
    
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
     * This method returns the cache configuration.
     * 
     * @return The cache configuration filename
     */
    public String getConfig() {
        return (_config);
    }
    
    /**
     * This method sets the cache configuration filename.
     * 
     * @param config The cache configuration
     */
    public void setConfig(String config) {
        _config = config;
    }
    
    /**
     * This method returns the cache name.
     * 
     * @return The cache name
     */
    public String getCache() {
        return (_cache);
    }
    
    /**
     * This method sets the cache name.
     * 
     * @param cache The cache name
     */
    public void setCache(String cache) {
        _cache = cache;
    }
    
    /**
     * {@inheritDoc}
     */
    public ActiveCollection createActiveCollection(ActiveCollectionSource acs) {
        ActiveCollection ret=null;
        
        if (acs.getType() == ActiveCollectionType.Map) {
            
            // Obtain the infinspan cache
            org.infinispan.manager.CacheContainer cacheContainer=null;

            if (_jndiName != null) {
                try {
                    InitialContext ctx=new InitialContext();
                    
                    cacheContainer = (org.infinispan.manager.CacheContainer)
                            ctx.lookup(_jndiName);
                } catch (Exception e) {
                    LOG.log(Level.WARNING, MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                            "active-collection-infinispan.Messages").getString("ACTIVE-COLLECTION-INFINISPAN-1"),
                            _jndiName), e);
                }
            } else {
                try {
                    cacheContainer = InfinispanUtil.getCacheContainer();
                    
                } catch (Exception e) {
                    LOG.log(Level.WARNING, java.util.PropertyResourceBundle.getBundle(
                            "active-collection-infinispan.Messages").getString("ACTIVE-COLLECTION-INFINISPAN-2"), e);
                }
            }
            
            if (cacheContainer != null) {
                java.util.Map<Object,Object> ac=cacheContainer.getCache(_cache);
                                
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Infinispan cache [jndiName="+_jndiName+" config="
                                +_config+" name="+_cache+"] = "+ac);
                }
 
                ret = new InfinispanActiveMap(acs, ac);
            }
            
        }
        
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Infinispan ActiveMap = "+ret);
        }

        return (ret);
    }
    
}
