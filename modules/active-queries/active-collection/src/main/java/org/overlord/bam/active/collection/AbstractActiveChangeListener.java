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
package org.overlord.bam.active.collection;

import org.codehaus.jackson.annotate.JsonTypeInfo;

/**
 * This class defines an abstract active change listener. The
 * main purpose of this class is to enable derived active change
 * listener implementations to be used within an ActiveCollectionSource
 * by providing their class information when serialized using json.
 *
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class AbstractActiveChangeListener implements ActiveChangeListener {

    /**
     * This method pre-initializes the active change listener
     * in situations where it needs to be initialized before
     * registration with the collection. This may be required
     * where the registration is performed in a different
     * contextual classloader.
     * 
     * @throws Exception Failed to pre-initialize
     */
    protected void preInit() throws Exception {
    }
    
    /**
     * This method initializes the active change listener.
     * 
     * @throws Exception Failed to initialize
     */
    public void init() throws Exception {
        preInit();
    }
    
    /**
     * This method closes the active change listener.
     * 
     * @throws Exception Failed to close
     */
    public void close() throws Exception {
    }
    
}
