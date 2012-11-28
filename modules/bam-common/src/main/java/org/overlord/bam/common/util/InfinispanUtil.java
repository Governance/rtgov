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
package org.overlord.bam.common.util;

import org.infinispan.manager.CacheContainer;
import org.infinispan.manager.DefaultCacheManager;

/**
 * This class provides utility functions for working with Infinispan.
 *
 */
public final class InfinispanUtil {

    private static final String INFINISPAN_CONFIG = "bam-infinispan.xml";
    
    private static CacheContainer _cacheContainer=null;

    /**
     * Private constructor.
     */
    private InfinispanUtil() {
    }
    
    /**
     * This method returns the cache container.
     * 
     * @return The cache container
     * @throws Exception Failed to get cache container
     */
    public static CacheContainer getCacheContainer() throws Exception {
        if (_cacheContainer == null) {
            _cacheContainer = new DefaultCacheManager(INFINISPAN_CONFIG);
        }
        return (_cacheContainer);
    }
}
