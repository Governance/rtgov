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
package org.overlord.rtgov.analytics.situation;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.ActivityTypeId;
import org.overlord.rtgov.activity.model.Context;

/**
 * This class represents the occurrence of an SLA violation.
 *
 */
@Entity
@Table(name="RTGOV_SITUATIONS")
public class Situation implements java.io.Externalizable {

    private static final int VERSION = 1;
    
    private String _id=UUID.randomUUID().toString();
    
    /**
     * The property name used to defined the resubmitted situation id.
     */
    public static final String RESUBMITTED_SITUATION_ID = ActivityType.RTGOV_PROPERTY_PREFIX+"resubmittedSituationId";

    /**
     * The separator character between parts of the subject.
     */
    public static final char SUBJECT_SEPARATOR_CHAR = '|';

    /**
     * The regular expression to isolate the subject separator
     */
    private static final String SUBJECT_SEPARATOR_REGEX = "\\|";

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
    private java.util.Set<ActivityTypeId> _activityTypeIds=
                        new java.util.LinkedHashSet<ActivityTypeId>();
    private java.util.Map<String,String> _situationProperties=new java.util.HashMap<String,String>();
    private java.util.Set<Context> _contexts=
                        new java.util.LinkedHashSet<Context>();

    /**
     * This method returns the id.
     * 
     * @return The id
     */
    @Id
    public String getId() {
        return (_id);
    }
    
    /**
     * This method sets the id.
     * 
     * @param id The id
     */
    public void setId(String id) {
        _id = id;
    }
    
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
    @Column(name="situationType")
    public String getType() {
        return (_type);
    }
    
    /**
     * This method constructs a subject based on a variable
     * number of string parts.
     * 
     * @param parts The parts
     * @return The subject
     */
    public static String createSubject(String... parts) {
        String ret=null;
        
        for (String part : parts) {
            if (ret == null) {
                ret = (part == null ? "" : part);
            } else {
                ret += SUBJECT_SEPARATOR_CHAR + (part == null ? "" : part);
            }
        }
        
        // Check for trailing separators
        if (ret != null && ret.length() > 0) {
            int i=ret.length()-1;
            
            while (i >= 0 && ret.charAt(i) == SUBJECT_SEPARATOR_CHAR) {
                i--;
            }
            
            if (i != ret.length()-1) {
                ret = ret.substring(0, i+1);
            }
        }
        
        return (ret);
    }
    
    /**
     * This method returns the subject as its constituent parts.
     * 
     * @return The subject parts
     */
    public String[] subjectAsParts() {
        String[] ret=null;
        
        if (_subject != null) {
            ret = _subject.split(SUBJECT_SEPARATOR_REGEX);
        } else {
            ret = new String[0];
        }
        
        return (ret);
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
    @Column(length=10240)
    @Lob
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
    @Column(name="tstamp")
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
     * This method sets the list of activity type ids.
     * 
     * @param activityTypeIds The list of activity type ids
     */
    public void setActivityTypeIds(java.util.Set<ActivityTypeId> activityTypeIds) {
        _activityTypeIds = activityTypeIds;
    }
    
    /**
     * This method returns the list of activity type ids
     * associated with the violation.
     * 
     * @return The related activity type ids
     */
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name="RTGOV_SITUATION_ACTIVITY_TYPES")
    public java.util.Set<ActivityTypeId> getActivityTypeIds() {
        return (_activityTypeIds);
    }
    
    /**
     * This method sets the properties.
     * 
     * @param props The properties
     */
    public void setSituationProperties(java.util.Map<String,String> props) {
        _situationProperties = props;
    }

    /**
     * This method gets the properties.
     * 
     * @return The properties
     */
    @ElementCollection(targetClass=String.class,fetch=FetchType.EAGER)
    @MapKeyColumn(name="name")
    @Column(name="value")
    @CollectionTable(name="RTGOV_SITUATION_PROPERTIES",joinColumns={
            @JoinColumn(name="id",referencedColumnName="id")})
    @JsonProperty("properties")
    public java.util.Map<String,String> getSituationProperties() {
        return (_situationProperties);
    }
    
    /**
     * This method sets the properties.
     * 
     * @param props The properties
     * 
     * @deprecated Use {@link #setSituationProperties(java.util.Map)}.
     */
    @Deprecated
    public void setProperties(java.util.Map<String,String> props) {
        _situationProperties = props;
    }

    /**
     * This method gets the properties.
     * 
     * @return The properties
     * 
     * @deprecated Use {@link #getSituationProperties()}.
     */
    @Deprecated
    @Transient
    @JsonIgnore
    public java.util.Map<String,String> getProperties() {
        return (_situationProperties);
    }

    /**
     * This method sets the context.
     * 
     * @param context The context
     */
    public void setContext(java.util.Set<Context> context) {
        _contexts = context;
    }
    
    /**
     * This method gets the context.
     * 
     * @return The context
     */
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name="RTGOV_SITUATION_CONTEXT")
    public java.util.Set<Context> getContext() {
        return (_contexts);
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
    public boolean equals(Object obj) {
        if (obj instanceof Situation) {
            return (_id.equals(((Situation)obj).getId()));
        }
        
        return (false);
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return (_id.hashCode());
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
        
        out.writeObject(_id);
        out.writeObject(_type);
        out.writeObject(_subject);
        out.writeObject(_description);
        out.writeObject(_severity);
        out.writeLong(_timestamp);
        
        out.writeInt(_activityTypeIds.size());
        java.util.Iterator<ActivityTypeId> iter=_activityTypeIds.iterator();
        while (iter.hasNext()) {
            out.writeObject(iter.next());
        }    
        
        out.writeObject(_situationProperties);    
        
        out.writeInt(_contexts.size());
        java.util.Iterator<Context> iter2=_contexts.iterator();
        while (iter2.hasNext()) {
            out.writeObject(iter2.next());
        }    
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        in.readInt(); // Consume version, as not required for now
        
        _id = (String)in.readObject();
        _type = (String)in.readObject();
        _subject = (String)in.readObject();
        _description = (String)in.readObject();
        _severity = (Severity)in.readObject();
        _timestamp = in.readLong();
        
        int len=in.readInt();
        for (int i=0; i < len; i++) {
            _activityTypeIds.add((ActivityTypeId)in.readObject());
        }
        
        _situationProperties = (java.util.Map<String, String>)in.readObject();

        len = in.readInt();
        for (int i=0; i < len; i++) {
            _contexts.add((Context)in.readObject());
        }
    }
}
