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
package org.overlord.rtgov.epn;

/**
 * This interface exposes the attributes and operations to be
 * managed for a Network.
 *
 */
public interface NetworkMBean {

    /**
     * This method returns the name of the network. This can be used
     * to locate the network by name.
     * 
     * @return The name of the network
     */
    public String getName();
    
    /**
     * This method returns the version associated with the network.
     * 
     * @return The version
     */
    public String getVersion();

    /**
     * This method returns the date/time the network was
     * last accessed.
     * 
     * @return When the network was last accessed
     */
    public java.util.Date getLastAccessed();

}
