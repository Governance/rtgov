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
package org.savara.bam.activity.model;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * This class represents information about the context in which the component
 * executes, and in which the activities are generated.
 *
 */
public class Context implements java.io.Externalizable {

    private static final int VERSION = 1;

    private String _principal=null;
    private String _transactionId=null;
    private String _thread=null;
    private String _host=null;
    private String _port=null;
	
    public Context() {
    }
 
    public Context(Context context) {
        _principal = context._principal;
        _transactionId = context._transactionId;
        _thread = context._thread;
        _host = context._host;
        _port = context._port;
    }
	
    public void setPrincipal(String principal) {
        _principal = principal;
    }
    
    public String getPrincipal() {
        return (_principal);
    }
    
    public void setTransaction(String transactionId) {
        _transactionId = transactionId;
    }
    
    public String getTransaction() {
        return (_transactionId);
    }
    
    public void setThread(String thread) {
        _thread = thread;
    }
    
    public String getThread() {
        return (_thread);
    }
    
    public void setHost(String host) {
        _host = host;
    }
    
    public String getHost() {
        return (_host);
    }
    
    public void setPort(String port) {
        _port = port;
    }
    
    public String getPort() {
        return (_port);
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION);
        
        out.writeUTF(_principal);
        out.writeUTF(_transactionId);
        out.writeUTF(_thread);
        out.writeUTF(_host);
        out.writeUTF(_port);
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        in.readInt(); // Consume version, as not required for now
        
        _principal = in.readUTF();
        _transactionId = in.readUTF();
        _thread = in.readUTF();
        _host = in.readUTF();
        _port = in.readUTF();
    }
}
