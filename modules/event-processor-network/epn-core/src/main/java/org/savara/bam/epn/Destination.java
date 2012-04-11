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

/**
 * This interface represents a destination for sending a
 * list of events.
 *
 */
public class Destination {
    
    private String _network=null;
    private String _node=null;

    /**
     * This is the default constructor.
     */
    public Destination() {
    }
    
    /**
     * This constructor initializes the network and node names.
     * 
     * @param network The network name
     * @param node The node name
     */
    public Destination(String network, String node) {
        _network = network;
        _node = node;
    }
    
    /**
     * This method returns the network destination.
     * 
     * @return The network
     */
    public String getNetwork() {
        return (_network);
    }
    
    /**
     * This method sets the network destination.
     * 
     * @param network The network
     */
    public void setNetwork(String network) {
        _network = network;
    }

    /**
     * This method returns the node destination.
     * 
     * @return The node
     */
    public String getNode() {
        return (_node);
    }
    
    /**
     * This method sets the node destination.
     * 
     * @param node The node
     */
    public void setNode(String node) {
        _node = node;
    }

}
