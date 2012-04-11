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
package org.savara.bam.activity.server;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * This class represents an activity query.
 *
 */
public class ActivityQuery implements java.io.Externalizable {

    private static final int VERSION = 1;
    private long _fromTimestamp=0;
    private long _toTimestamp=0;
    
    /**
     * This is the default constructor.
     */
    public ActivityQuery() {
    }
    
    /**
     * This method sets the 'from' timestamp. If
     * set to 0, then query will start from the
     * oldest activity event.
     * 
     * @param from The 'from' timetamp
     */
    public void setFromTimestamp(long from) {
        _fromTimestamp = from;
    }
    
    /**
     * This method gets the 'from' timestamp. If
     * value is 0, then query will start from the
     * oldest activity event.
     * 
     * @return The 'from' timetamp
     */
    public long getFromTimestamp() {
        return (_fromTimestamp);
    }

    /**
     * This method sets the 'to' timestamp. If
     * set to 0, then query will end with the
     * newest activity event.
     * 
     * @param to The 'to' timetamp
     */
    public void setToTimestamp(long to) {
        _toTimestamp = to;
    }
    
    /**
     * This method gets the 'to' timestamp. If
     * value is 0, then query will start with the
     * newest activity event.
     * 
     * @return The 'to' timetamp
     */
    public long getToTimestamp() {
        return (_toTimestamp);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION);
        
        out.writeLong(_fromTimestamp);
        out.writeLong(_toTimestamp);
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        in.readInt(); // Consume version number - not required at the moment
        
        _fromTimestamp = in.readLong();
        _toTimestamp = in.readLong();
    }
}
