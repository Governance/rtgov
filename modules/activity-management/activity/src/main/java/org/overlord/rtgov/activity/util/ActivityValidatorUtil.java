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
package org.overlord.rtgov.activity.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;

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
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        
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
