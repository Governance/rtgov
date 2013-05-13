/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-13, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.rtgov.activity.util;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.TypeReference;
import org.overlord.rtgov.activity.validator.ActivityValidator;

/**
 * This class provides utility functions for the activity validators.
 *
 */
public final class ActivityValidatorUtil {
    
    private static final ObjectMapper MAPPER=new ObjectMapper();

    private static final TypeReference<java.util.List<ActivityValidator>> ACTIVITY_VALIDATOR_LIST=
            new TypeReference<java.util.List<ActivityValidator>>() { };
    
    private static ObjectWriter AILIST_WRITER=null;

    static {
        SerializationConfig config=MAPPER.getSerializationConfig()
                .withSerializationInclusion(JsonSerialize.Inclusion.NON_NULL)
                .withSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
        
        MAPPER.setSerializationConfig(config);
        
        AILIST_WRITER = MAPPER.writerWithType(ACTIVITY_VALIDATOR_LIST);
    }
    
    /**
     * Private constructor.
     */
    private ActivityValidatorUtil() {
    }
    
    /**
     * This method serializes an ActivityValidator list into a JSON representation.
     * 
     * @param ais The ActivityValidator list
     * @return The JSON serialized representation
     * @throws Exception Failed to serialize
     */
    public static byte[] serializeActivityValidatorList(java.util.List<ActivityValidator> ais) throws Exception {
        byte[] ret=null;
        
        java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
        
        AILIST_WRITER.writeValue(baos, ais);
        
        ret = baos.toByteArray();
        
        baos.close();
        
        return (ret);
    }

    /**
     * This method deserializes an ActivityValidator list from a JSON representation.
     * 
     * @param ais The JSON representation of the ActivityValidator list
     * @return The ActivityValidator list
     * @throws Exception Failed to deserialize
     */
    public static java.util.List<ActivityValidator> deserializeActivityValidatorList(byte[] ais) throws Exception {
        java.util.List<ActivityValidator> ret=null;
        
        java.io.ByteArrayInputStream bais=new java.io.ByteArrayInputStream(ais);
        
        ret = MAPPER.readValue(bais, ACTIVITY_VALIDATOR_LIST);
        
        bais.close();
        
        return (ret);
    }
}
