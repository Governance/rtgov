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
package org.overlord.bam.analytics.service;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.overlord.bam.activity.model.ActivityTypeRef;

/**
 * This class represents the occurrence of an SLA violation.
 *
 */
public class Situation implements java.io.Externalizable {

    private static final int VERSION = 1;
    
    /**
     * Critical severity.
     */
    public static final String SEVERITY_CRITICAL="Critical";

    /**
     * High severity.
     */
    public static final String SEVERITY_HIGH="High";
    
    /**
     * Medium severity.
     */
    public static final String SEVERITY_MEDIUM="Medium";
    
    /**
     * Low severity.
     */
    public static final String SEVERITY_LOW="Low";

    private String _type=null;
    private String _subject=null;
    private String _description=null;
    private long _timestamp=0;
    private String _severity=null;
    private java.util.List<ActivityTypeRef> _activityTypeRefs=
                        new java.util.ArrayList<ActivityTypeRef>();

    /**
     * This method sets the situation type.
     * 
     * @param type The situation type
     */
    public void setType(String type) {
        _type = type;
    }
    
    /**
     * This method gets the situation type.
     * 
     * @return The situation type
     */
    public String getType() {
        return (_type);
    }
    
    /**
     * This method sets the subject.
     * 
     * @param subject The subject
     */
    public void setSubject(String subject) {
        _subject = subject;
    }
    
    /**
     * This method gets the subject.
     * 
     * @return The subject
     */
    public String getSubject() {
        return (_subject);
    }
    
    /**
     * This method sets the situation description.
     * 
     * @param description The description
     */
    public void setDescription(String description) {
        _description = description;
    }
    
    /**
     * This method gets the situation description.
     * 
     * @return The description
     */
    public String getDescription() {
        return (_description);
    }
    
    /**
     * This method sets the timestamp.
     * 
     * @param timestamp The timestamp
     */
    public void setTimestamp(long timestamp) {
        _timestamp = timestamp;
    }
    
    /**
     * This method returns the timestamp.
     * 
     * @return The timestamp
     */
    public long getTimestamp() {
        return (_timestamp);
    }
    
    /**
     * This method returns the severity value.
     * 
     * @return The severity
     */
    public String getSeverity() {
        return _severity;
    }

    /**
     * This method sets the severity value.
     * 
     * @param severity The severity value
     */
    public void setSeverity(String severity) {
        _severity = severity;
    }

    /**
     * This method returns the list of activity type references
     * associated with the violation.
     * 
     * @return The related activity type refs
     */
    public java.util.List<ActivityTypeRef> getActivityTypeRefs() {
        return (_activityTypeRefs);
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return ("Situation '"+_description+"' of type="+_type+" subject="
                    +_subject+ " severity=" + _severity + " "+new java.util.Date(_timestamp));
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION);
        
        out.writeObject(_type);
        out.writeObject(_subject);
        out.writeObject(_description);
        out.writeObject(_severity);
        out.writeLong(_timestamp);
        
        out.writeInt(_activityTypeRefs.size());
        for (int i=0; i < _activityTypeRefs.size(); i++) {
            out.writeObject(_activityTypeRefs.get(i));            
        }
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        in.readInt(); // Consume version, as not required for now
        
        _type = (String)in.readObject();
        _subject = (String)in.readObject();
        _description = (String)in.readObject();
        _severity = (String)in.readObject();
        _timestamp = in.readLong();
        
        int len=in.readInt();
        for (int i=0; i < len; i++) {
            _activityTypeRefs.add((ActivityTypeRef)in.readObject());
        }
    }
}
