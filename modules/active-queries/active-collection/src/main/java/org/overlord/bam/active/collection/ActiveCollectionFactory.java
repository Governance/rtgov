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
 * This class represents a factory for creating active collections.
 *
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class ActiveCollectionFactory {

    /**
     * This is the default factory.
     */
    public static final ActiveCollectionFactory DEFAULT_FACTORY=new ActiveCollectionFactory();
    
    /**
     * This method creates an active collection based on the supplied source.
     * 
     * @param acs The source
     * @return The active collection, or null if unable to create
     */
    public ActiveCollection createActiveCollection(ActiveCollectionSource acs) {
        ActiveCollection ret=null;
        
        if (acs.getType() == ActiveCollectionType.List) {
            ret = new ActiveList(acs.getName(),
                    acs.getItemExpiration(), acs.getMaxItems(), acs.getHighWaterMark());
        } else if (acs.getType() == ActiveCollectionType.Map) {
            ret = new ActiveMap(acs.getName(),
                    acs.getItemExpiration(), acs.getMaxItems(), acs.getHighWaterMark());
        }

        return (ret);
    }
    
}
