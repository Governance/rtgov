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
package org.overlord.bam.activity.model;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * This class represents information about the origin of the activity
 * events reported.
 *
 */
public class Origin implements java.io.Externalizable {

    private static final int VERSION = 1;

    private String _principal=null;
    private String _thread=null;
    private String _host=null;
    private String _node=null;
    private String _port=null;

    /**
     * The default constructor.
     */
    public Origin() {
    }
 
    /**
     * The copy constructor.
     * 
     * @param origin The origin to copy
     */
    public Origin(Origin origin) {
        _principal = origin._principal;
        _thread = origin._thread;
        _host = origin._host;
        _node = origin._node;
        _port = origin._port;
    }
    
    /**
     * This method sets the principal.
     * 
     * @param principal The principal
     */
    public void setPrincipal(String principal) {
        _principal = principal;
    }
    
    /**
     * This method gets the principal.
     * 
     * @return The principal
     */
    public String getPrincipal() {
        return (_principal);
    }
    
    /**
     * This method sets the thread.
     * 
     * @param thread The thread
     */
    public void setThread(String thread) {
        _thread = thread;
    }
    
    /**
     * This method gets the thread.
     * 
     * @return The thread
     */
    public String getThread() {
        return (_thread);
    }
    
    /**
     * This method sets the host.
     * 
     * @param host The host
     */
    public void setHost(String host) {
        _host = host;
    }
    
    /**
     * This method gets the host.
     * 
     * @return The host
     */
    public String getHost() {
        return (_host);
    }
    
    /**
     * This method sets the name of the node.
     * This name may identify the environment
     * within a clustered configuration.
     * 
     * @param node The node
     */
    public void setNode(String node) {
        _node = node;
    }
    
    /**
     * This method gets the name of the node.
     * This name may identify the environment
     * within a clustered configuration.
     * 
     * @return The node
     */
    public String getNode() {
        return (_node);
    }
    
    /**
     * This method sets the port.
     * 
     * @param port The port
     */
    public void setPort(String port) {
        _port = port;
    }
    
    /**
     * This method gets the port.
     * 
     * @return The port
     */
    public String getPort() {
        return (_port);
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION);
        
        out.writeObject(_principal);
        out.writeObject(_thread);
        out.writeObject(_host);
        out.writeObject(_node);
        out.writeObject(_port);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        in.readInt(); // Consume version, as not required for now
        
        _principal = (String)in.readObject();
        _thread = (String)in.readObject();
        _host = (String)in.readObject();
        _node = (String)in.readObject();
        _port = (String)in.readObject();
    }
}
