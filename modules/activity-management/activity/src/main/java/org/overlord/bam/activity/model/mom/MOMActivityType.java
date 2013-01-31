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
package org.overlord.bam.activity.model.mom;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.persistence.Entity;

import org.overlord.bam.activity.model.common.MessageExchange;

/**
 * This activity type represents a MOM activity type.
 *
 */
@Entity
public abstract class MOMActivityType extends MessageExchange implements java.io.Externalizable {

    private static final int VERSION = 1;

    private String _destination=null;

    /**
     * The default constructor.
     */
    public MOMActivityType() {
    }
    
    /**
     * The copy constructor.
     * 
     * @param mat The activity type to copy
     */
    public MOMActivityType(MOMActivityType mat) {
        super(mat);
        _destination = mat._destination;
    }
    
    /**
     * This method sets the destination.
     * 
     * @param destination The destination
     */
    public void setDestination(String destination) {
        _destination = destination;
    }
    
    /**
     * This method gets the destination.
     * 
     * @return The destination
     */
    public String getDestination() {
        return (_destination);
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        
        out.writeInt(VERSION);
        
        out.writeObject(_destination);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        super.readExternal(in);
        
        in.readInt(); // Consume version, as not required for now
        
        _destination = (String)in.readObject();
    }
}
