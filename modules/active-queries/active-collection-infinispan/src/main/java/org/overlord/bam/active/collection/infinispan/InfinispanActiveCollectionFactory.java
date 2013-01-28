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

import java.util.logging.Level;
import java.util.logging.Logger;

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
    private String _container=null;
    
    /**
     * This method sets the JNDI name for the container resource.
     * 
     * @param jndiName The JNDI name for the container resource
     */
    public void setContainer(String jndiName) {
        _container = jndiName;
    }
    
    /**
     * This method returns the JNDI name used to obtain
     * the container resource.
     * 
     * @return The JNDI name for the container resource
     */
    public String getContainer() {
        return (_container);
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
            org.infinispan.manager.CacheContainer cacheContainer=InfinispanUtil.getCacheContainer(_container);
                
            if (cacheContainer != null) {
                java.util.Map<Object,Object> ac=cacheContainer.getCache(_cache);
                                
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Infinispan cache [container="+_container+" name="+_cache+"] = "+ac);
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
