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

import org.overlord.rtgov.activity.processor.InformationProcessor;

/**
 * This class provides utility functions for the information processor
 * model.
 *
 */
public final class InformationProcessorUtil {
    
    private static final ObjectMapper MAPPER=new ObjectMapper();

    private static final TypeReference<java.util.List<InformationProcessor>> INFORMATION_PROCESSOR_LIST=
            new TypeReference<java.util.List<InformationProcessor>>() { };
    
    private static ObjectWriter IPLIST_WRITER=null;

    static {
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        
        IPLIST_WRITER = MAPPER.writerWithType(INFORMATION_PROCESSOR_LIST);
    }
    
    /**
     * Private constructor.
     */
    private InformationProcessorUtil() {
    }
    
    /**
     * This method serializes an InformationProcessor list into a JSON representation.
     * 
     * @param ips The information processor list
     * @return The JSON serialized representation
     * @throws Exception Failed to serialize
     */
    public static byte[] serializeInformationProcessorList(java.util.List<InformationProcessor> ips) throws Exception {
        byte[] ret=null;
        
        java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
        
        IPLIST_WRITER.writeValue(baos, ips);
        
        ret = baos.toByteArray();
        
        baos.close();
        
        return (ret);
    }

    /**
     * This method deserializes an InformationProcessor list from a JSON representation.
     * 
     * @param ips The JSON representation of the information processor list
     * @return The InformationProcessor list
     * @throws Exception Failed to deserialize
     */
    public static java.util.List<InformationProcessor> deserializeInformationProcessorList(byte[] ips) throws Exception {
        java.util.List<InformationProcessor> ret=null;
        
        java.io.ByteArrayInputStream bais=new java.io.ByteArrayInputStream(ips);
        
        ret = MAPPER.readValue(bais, INFORMATION_PROCESSOR_LIST);
        
        bais.close();
        
        return (ret);
    }
}
