/*
 * 2012-3 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.rtgov.activity.model.app;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.persistence.Entity;

import org.overlord.rtgov.activity.model.ActivityType;

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
