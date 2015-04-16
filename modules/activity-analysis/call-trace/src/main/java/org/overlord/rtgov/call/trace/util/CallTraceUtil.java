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
package org.overlord.rtgov.call.trace.util;

import java.beans.PropertyDescriptor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.overlord.rtgov.call.trace.model.CallTrace;

/**
 * This class provides utility functions for the call trace
 * model.
 *
 */
public final class CallTraceUtil {
    
    private static final ObjectMapper MAPPER=new ObjectMapper();

    static {
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
    }
    
    /**
     * Private constructor.
     */
    private CallTraceUtil() {
    }
    
    /**
     * This method serializes a call trace into a JSON representation.
     * 
     * @param node The call trace
     * @return The JSON serialized representation
     * @throws Exception Failed to serialize
     */
    public static byte[] serializeCallTrace(CallTrace node) throws Exception {
        byte[] ret=null;
        
        java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
        
        MAPPER.writer().with(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS).writeValue(baos, node);
        
        ret = baos.toByteArray();
        
        baos.close();
        
        return (ret);
    }

    /**
     * This method deserializes a call trace from a JSON representation.
     * 
     * @param node The JSON representation of the call trace
     * @return The call trace
     * @throws Exception Failed to deserialize
     */
    public static CallTrace deserializeCallTrace(byte[] node) throws Exception {
        CallTrace ret=null;
        
        java.io.ByteArrayInputStream bais=new java.io.ByteArrayInputStream(node);
        
        ret = MAPPER.readValue(bais, CallTrace.class);
        
        bais.close();
        
        return (ret);
    }

    /**
     * This method determines whether the supplied property
     * descriptor should be included in the activity type event's
     * description.
     * 
     * @param pd The property descriptor
     * @return Whether the property's description should be included
     */
    public static boolean shouldIncludeProperty(PropertyDescriptor pd) {
        boolean ret=false;
        
        if (pd.getPropertyType().isPrimitive() || pd.getPropertyType() == String.class) {
            
            // Check excluded names
            if (pd.getName().equals("timestamp")
                    || pd.getName().startsWith("unit")) {
                return (false);
            }
            
            ret = true;
        }
                
        return (ret);
    }
    
}
