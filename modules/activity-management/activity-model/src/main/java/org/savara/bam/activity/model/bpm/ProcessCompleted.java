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
package org.savara.bam.activity.model.bpm;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * This activity type represents a process completed event.
 *
 */
public class ProcessCompleted extends BPMActivityType implements java.io.Externalizable {

    private static final int VERSION = 1;

    /**
     * The status of the process upon completion.
     *
     */
    public enum Status {
        /** Process completed succcesfully. **/
        Success,
        /** Process failed. **/
        Fail
    }
    
    private Status _status=Status.Success;
    
    /**
     * The default constructor.
     */
    public ProcessCompleted() {
    }
    
    /**
     * The copy constructor.
     * 
     * @param ba The bpm activity to copy
     */
    public ProcessCompleted(ProcessCompleted ba) {
     }
    
    /**
     * This method sets the status.
     * 
     * @param status The status
     */
    public void setStatus(Status status) {
        _status = status;
    }
    
    /**
     * This method gets the status.
     * 
     * @return The status
     */
    public Status getStatus() {
        return (_status);
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION);
        
        out.writeObject(_status);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        in.readInt(); // Consume version, as not required for now
        
        _status = (Status)in.readObject();
    }
}
