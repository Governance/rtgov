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
package org.overlord.bam.analytics;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.overlord.bam.activity.model.ActivityTypeId;

/**
 * This class represents the occurrence of an SLA violation.
 *
 */
public class Situation implements java.io.Externalizable {

    private static final int VERSION = 1;
    
    /**
     * This enumeration type represents the severity of
     * the situation.
     *
     */
    public enum Severity {
        
        /**
         * Low severity.
         */
        Low,
        
        /**
         * Medium severity.
         */
        Medium,
        
        /**
         * High severity.
         */
        High,
       
        /**
         * Critical severity.
         */
        Critical
    }
    
    private String _type=null;
    private String _subject=null;
    private String _description=null;
    private long _timestamp=0;
    private Severity _severity=null;
    private java.util.List<ActivityTypeId> _activityTypeIds=
                        new java.util.ArrayList<ActivityTypeId>();

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
    public Severity getSeverity() {
        return _severity;
    }

    /**
     * This method sets the severity value.
     * 
     * @param severity The severity value
     */
    public void setSeverity(Severity severity) {
        _severity = severity;
    }

    /**
     * This method returns the list of activity type ids
     * associated with the violation.
     * 
     * @return The related activity type ids
     */
    public java.util.List<ActivityTypeId> getActivityTypeIds() {
        return (_activityTypeIds);
    }
    
    /**
     * This method returns the highest severity associated with the supplied
     * list of situations.
     * 
     * @param sits The list of situations
     * @return The highest severity, or null if no situations provided
     */
    public static Severity getHighestSeverity(java.util.List<Situation> sits) {
        Severity ret=null;
        
        for (Situation s : sits) {
            
            if (ret == null) {
                ret = s.getSeverity();
            } else if (s.getSeverity().ordinal() > ret.ordinal()) {
                ret = s.getSeverity();
            }
        }
        
        return (ret);
    }
    
    /**
     * This method filters the list of situations based on severity.
     * 
     * @param severity The severity
     * @param sits The list of situations
     * @return The situations for the supplied severity
     */
    public static java.util.List<Situation> getSituationsForSeverity(Severity severity,
                        java.util.List<Situation> sits) {
        java.util.List<Situation> ret=new java.util.ArrayList<Situation>();
        
        for (Situation s : sits) {
            if (s.getSeverity() == severity) {
                ret.add(s);
            }
        }
        
        return (ret);
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
        
        out.writeInt(_activityTypeIds.size());
        for (int i=0; i < _activityTypeIds.size(); i++) {
            out.writeObject(_activityTypeIds.get(i));            
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
        _severity = (Severity)in.readObject();
        _timestamp = in.readLong();
        
        int len=in.readInt();
        for (int i=0; i < len; i++) {
            _activityTypeIds.add((ActivityTypeId)in.readObject());
        }
    }
}
