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
package org.overlord.bam.epn;

/**
 * This class represents information about a network registered with the
 * manager.
 *
 */
public class NetworkInfo {

    private String _name=null;
    private String _version=null;
    private java.util.Date _lastAccessed=null;
    
    /**
     * This constructor initializes the information.
     * 
     * @param network The network
     * @param lastAccessed The last access date/time
     */
    public NetworkInfo(Network network) {
        _name = network.getName();
        _version = network.getVersion();
        
        if (network.lastAccessed() > 0) {
            _lastAccessed = new java.util.Date(network.lastAccessed());
        }
    }
    
    /**
     * This method returns the network name.
     * 
     * @return The network name
     */
    public String getName() {
        return (_name);
    }
    
    /**
     * This method returns the network version.
     * 
     * @return The network version
     */
    public String getVersion() {
        return (_version);
    }
    
    /**
     * This method returns the date/time the network
     * was last accessed to process an event.
     * 
     * @return The last accessed date/time
     */
    public java.util.Date getLastAccessed() {
        return (_lastAccessed);
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return (getName()+"["+(getVersion()==null?"":getVersion())
                +"] last accessed "+(getLastAccessed()==null?"<unknown>":getLastAccessed()));
    }
}
