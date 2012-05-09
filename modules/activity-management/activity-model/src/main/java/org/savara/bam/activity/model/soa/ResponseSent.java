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
package org.savara.bam.activity.model.soa;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * This activity type represents a sent request.
 *
 */
public class ResponseSent extends RPCActivityType implements java.io.Externalizable {

    private static final int VERSION = 1;

    private String _replyToId=null;

    /**
     * The default constructor.
     */
    public ResponseSent() {
    }
    
    /**
     * The copy constructor.
     * 
     * @param rpc The rpc activity to copy
     */
    public ResponseSent(ResponseSent rpc) {
        _replyToId = rpc._replyToId;
    }
    
    /**
     * This method sets the 'reply to' message id.
     * 
     * @param replyToId The 'reply to' message id
     */
    public void setReplyToId(String replyToId) {
        _replyToId = replyToId;
    }
    
    /**
     * This method gets the 'reply to' message id.
     * 
     * @return The 'reply to' message id
     */
    public String getReplyToId() {
        return (_replyToId);
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return (super.toString()
                +" replyToId="+_replyToId);
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        
        out.writeInt(VERSION);

        out.writeObject(_replyToId);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        super.readExternal(in);
        
        in.readInt(); // Consume version, as not required for now

        _replyToId = (String)in.readObject();
    }
}
