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
package org.savara.bam.epn;

import org.codehaus.jackson.annotate.JsonTypeInfo;

/**
 * This class represents the predicate applied to an
 * event being processed.
 *
 * @param <T> The event type
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class Predicate {

    /**
     * This method initializes the predicate.
     * 
     * @param context The container context
     * @throws Exception Failed to initialize the predicate
     */
    public void init(EPNContext context) throws Exception {
    }
    
    /**
     * This method applies the predicate to the supplied
     * event to determine if it should be processed.
     * 
     * @param event The event
     * @return Whether the event should be processed
     */
    public abstract boolean apply(Object event);

    /**
     * This method closes the predicate.
     * 
     * @param context The container context
     * @throws Exception Failed to close the predicate
     */
    protected void close(EPNContext context) throws Exception {
    }
    
}
