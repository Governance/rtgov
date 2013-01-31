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
package org.overlord.bam.activity.model.app;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.persistence.Entity;

import org.overlord.bam.activity.model.ActivityType;

/**
 * This activity type represents a log message.
 *
 */
@Entity
public class LogMessage extends ActivityType implements java.io.Externalizable {

    private static final int VERSION = 1;

    private String _message=null;
    private Level _level=Level.Information;

    /**
     * The default constructor.
     */
    public LogMessage() {
    }
    
    /**
     * The copy constructor.
     * 
     * @param lm The log message to copy
     */
    public LogMessage(LogMessage lm) {
        super(lm);
        _message = lm._message;
        _level = lm._level;
    }
    
    /**
     * This method sets the message.
     * 
     * @param message The message
     */
    public void setMessage(String message) {
        _message = message;
    }
    
    /**
     * This method gets the message.
     * 
     * @return The message
     */
    public String getMessage() {
        return (_message);
    }
    
    /**
     * This method sets the level.
     * 
     * @param level The level
     */
    public void setLevel(Level level) {
        _level = level;
    }
    
    /**
     * This method gets the level.
     * 
     * @return The level
     */
    public Level getLevel() {
        return (_level);
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        
        out.writeInt(VERSION);
        
        out.writeObject(_message);
        out.writeObject(_level);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        super.readExternal(in);
        
        in.readInt(); // Consume version, as not required for now
        
        _message = (String)in.readObject();
        _level = (Level)in.readObject();
    }
    
    /**
     * This enumerated type represents the level associated with
     * the log message.
     *
     */
    public enum Level {
        
        /**
         * An information message.
         */
        Information,
        
        /**
         * A warning message.
         */
        Warning,
        
        /**
         * An error message.
         */
        Error
        
    }
}
