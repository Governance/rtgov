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
package org.overlord.rtgov.common.util;

/**
 * This interface provides access to Runtime Governance properties.
 *
 */
public interface RTGovPropertiesProvider {

    /**
     * This method returns the property associated with the
     * supplied name.
     * 
     * @param name The property name
     * @return The value, or null if not found
     */
    public String getProperty(String name);
    
    /**
     * This method returns the Runtime Governance properties.
     * 
     * @return The properties, or null if not available
     */
    public java.util.Properties getProperties();
    
}
