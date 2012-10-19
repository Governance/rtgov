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
package org.overlord.bam.activity.util;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.TypeReference;
import org.overlord.bam.activity.model.ActivityType;
import org.overlord.bam.activity.model.ActivityUnit;
import org.overlord.bam.activity.server.QuerySpec;

/**
 * This class provides utility functions for the activity
 * model.
 *
 */
public final class ActivityUtil {
    
    private static final ObjectMapper MAPPER=new ObjectMapper();

    private static final TypeReference<java.util.List<ActivityUnit>> ACTIVITY_UNIT_LIST=
            new TypeReference<java.util.List<ActivityUnit>>() { };
    private static final TypeReference<java.util.List<ActivityType>> ACTIVITY_TYPE_LIST=
            new TypeReference<java.util.List<ActivityType>>() { };
    
    private static ObjectWriter ATLIST_WRITER=null;

    static {
        SerializationConfig config=MAPPER.getSerializationConfig()
                .withSerializationInclusion(JsonSerialize.Inclusion.NON_NULL)
                .withSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
        
        MAPPER.setSerializationConfig(config);
        
        ATLIST_WRITER = MAPPER.writerWithType(ACTIVITY_TYPE_LIST);
    }
    
    /**
     * Private constructor.
     */
    private ActivityUtil() {
    }
    
    /**
     * This method serializes an Activity event into a JSON representation.
     * 
     * @param act The activity
     * @return The JSON serialized representation
     * @throws Exception Failed to serialize
     */
    public static byte[] serializeActivityUnit(ActivityUnit act) throws Exception {
        byte[] ret=null;
        
        java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
        
        MAPPER.writeValue(baos, act);
        
        ret = baos.toByteArray();
        
        baos.close();
        
        return (ret);
    }

    /**
     * This method serializes a Query Spec into a JSON representation.
     * 
     * @param qs The query spec
     * @return The JSON serialized representation
     * @throws Exception Failed to serialize
     */
    public static byte[] serializeQuerySpec(QuerySpec qs) throws Exception {
        byte[] ret=null;
        
        java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
        
        MAPPER.writeValue(baos, qs);
        
        ret = baos.toByteArray();
        
        baos.close();
        
        return (ret);
    }

    /**
     * This method serializes an object into a JSON string representation.
     * 
     * @param obj The object
     * @return The JSON serialized string representation
     * @throws Exception Failed to serialize
     */
    public static String objectToJSONString(Object obj) throws Exception {
        String ret=null;
        
        java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
        
        MAPPER.writeValue(baos, obj);
        
        ret = new String(baos.toByteArray());
        
        baos.close();
        
        return (ret);
    }

    /**
     * This method serializes an ActivityUnit  list into a JSON representation.
     * 
     * @param activities The activity unit list
     * @return The JSON serialized representation
     * @throws Exception Failed to serialize
     */
    public static byte[] serializeActivityUnitList(java.util.List<ActivityUnit> activities) throws Exception {
        byte[] ret=null;
        
        java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
        
        MAPPER.writeValue(baos, activities);
        
        ret = baos.toByteArray();
        
        baos.close();
        
        return (ret);
    }

    /**
     * This method serializes an ActivityType event list into a JSON representation.
     * 
     * @param activities The activity type list
     * @return The JSON serialized representation
     * @throws Exception Failed to serialize
     */
    public static byte[] serializeActivityTypeList(java.util.List<ActivityType> activities) throws Exception {
        byte[] ret=null;
        
        java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
        
        ATLIST_WRITER.writeValue(baos, activities);
        
        ret = baos.toByteArray();
        
        baos.close();
        
        return (ret);
    }

    /**
     * This method deserializes an Activity event from a JSON representation.
     * 
     * @param act The JSON representation of the activity
     * @return The Activity event
     * @throws Exception Failed to deserialize
     */
    public static ActivityUnit deserializeActivityUnit(byte[] act) throws Exception {
        ActivityUnit ret=null;
        
        java.io.ByteArrayInputStream bais=new java.io.ByteArrayInputStream(act);
        
        ret = MAPPER.readValue(bais, ActivityUnit.class);
        
        bais.close();
        
        return (ret);
    }

    /**
     * This method deserializes a Query Spec from a JSON representation.
     * 
     * @param qs The JSON representation of the query spec
     * @return The query spec
     * @throws Exception Failed to deserialize
     */
    public static QuerySpec deserializeQuerySpec(byte[] qs) throws Exception {
        QuerySpec ret=null;
        
        java.io.ByteArrayInputStream bais=new java.io.ByteArrayInputStream(qs);
        
        ret = MAPPER.readValue(bais, QuerySpec.class);
        
        bais.close();
        
        return (ret);
    }

    /**
     * This method deserializes an Activity Unit list from a JSON representation.
     * 
     * @param act The JSON representation of the activity
     * @return The ActivityUnit event list
     * @throws Exception Failed to deserialize
     */
    public static java.util.List<ActivityUnit> deserializeActivityUnitList(byte[] act) throws Exception {
        java.util.List<ActivityUnit> ret=null;
        
        java.io.ByteArrayInputStream bais=new java.io.ByteArrayInputStream(act);
        
        ret = MAPPER.readValue(bais, ACTIVITY_UNIT_LIST);
        
        bais.close();
        
        return (ret);
    }

    /**
     * This method deserializes an ActivityType event list from a JSON representation.
     * 
     * @param act The JSON representation of the activity
     * @return The ActivityType event list
     * @throws Exception Failed to deserialize
     */
    public static java.util.List<ActivityType> deserializeActivityTypeList(byte[] act) throws Exception {
        java.util.List<ActivityType> ret=null;
        
        java.io.ByteArrayInputStream bais=new java.io.ByteArrayInputStream(act);
        
        ret = MAPPER.readValue(bais, ACTIVITY_TYPE_LIST);
        
        bais.close();
        
        return (ret);
    }
}
