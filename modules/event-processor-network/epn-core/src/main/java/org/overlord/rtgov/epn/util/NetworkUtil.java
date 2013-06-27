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
package org.overlord.rtgov.epn.util;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.overlord.rtgov.epn.Network;

/**
 * This class provides utility functions for the EPN
 * model.
 *
 */
public final class NetworkUtil {
    
    private static final ObjectMapper MAPPER=new ObjectMapper();
    
    static {
        SerializationConfig config=MAPPER.getSerializationConfig().with(SerializationConfig.Feature.INDENT_OUTPUT);
        
        MAPPER.setSerializationConfig(config);
    }

    /**
     * Private constructor.
     */
    private NetworkUtil() {
    }
    
    /**
     * This method serializes an Event Processor Network into a JSON representation.
     * 
     * @param epn The Event Processor Network
     * @return The JSON serialized representation
     * @throws Exception Failed to serialize
     */
    public static byte[] serialize(Network epn) throws Exception {
        byte[] ret=null;
        
        java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
        
        MAPPER.writeValue(baos, epn);
        
        ret = baos.toByteArray();
        
        baos.close();
        
        return (ret);
    }

    /**
     * This method deserializes an Event Processor Network from a JSON representation.
     * 
     * @param epn The JSON representation of the Event Processor Network
     * @return The Event Processor Network
     * @throws Exception Failed to deserialize
     */
    public static Network deserialize(byte[] epn) throws Exception {
        Network ret=null;
        
        java.io.ByteArrayInputStream bais=new java.io.ByteArrayInputStream(epn);
        
        ret = MAPPER.readValue(bais, Network.class);
        
        bais.close();
        
        return (ret);
    }

}
