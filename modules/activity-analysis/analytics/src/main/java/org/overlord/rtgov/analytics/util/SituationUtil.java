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
package org.overlord.rtgov.analytics.util;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.overlord.rtgov.analytics.situation.IgnoreSubject;
import org.overlord.rtgov.analytics.situation.Situation;

/**
 * This class provides utility functions related to the situation classes.
 *
 */
public final class SituationUtil {
    
    private static final ObjectMapper MAPPER=new ObjectMapper();

    static {
        SerializationConfig config=MAPPER.getSerializationConfig()
                .withSerializationInclusion(JsonSerialize.Inclusion.NON_NULL)
                .withSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
        
        DeserializationConfig config2=MAPPER.getDeserializationConfig()
                .without(Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        
        MAPPER.setSerializationConfig(config);
        MAPPER.setDeserializationConfig(config2);
    }
    
    /**
     * The default constructor.
     */
    private SituationUtil() {
    }

    /**
     * This method serializes a Situation into a JSON representation.
     * 
     * @param situation The situation
     * @return The JSON serialized representation
     * @throws Exception Failed to serialize
     */
    public static byte[] serializeSituation(Situation situation) throws Exception {
        byte[] ret=null;
        
        java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
        
        MAPPER.writeValue(baos, situation);
        
        ret = baos.toByteArray();
        
        baos.close();
        
        return (ret);
    }

    /**
     * This method serializes an IgnoreSubject into a JSON representation.
     * 
     * @param ignore The ignore subject details
     * @return The JSON serialized representation
     * @throws Exception Failed to serialize
     */
    public static byte[] serializeIgnoreSubject(IgnoreSubject ignore) throws Exception {
        byte[] ret=null;
        
        java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
        
        MAPPER.writeValue(baos, ignore);
        
        ret = baos.toByteArray();
        
        baos.close();
        
        return (ret);
    }

    /**
     * This method deserializes a Service Definition from a JSON representation.
     * 
     * @param situation The JSON representation of the situation
     * @return The situation
     * @throws Exception Failed to deserialize
     */
    public static Situation deserializeSituation(byte[] situation) throws Exception {
        Situation ret=null;
        
        java.io.ByteArrayInputStream bais=new java.io.ByteArrayInputStream(situation);
        
        ret = MAPPER.readValue(bais, Situation.class);
        
        bais.close();
        
        return (ret);
    }
    
    /**
     * This method deserializes an Ignore Subject from a JSON representation.
     * 
     * @param ignore The JSON representation of the ignore subject details
     * @return The ignore subject details
     * @throws Exception Failed to deserialize
     */
    public static IgnoreSubject deserializeIgnoreSubject(byte[] ignore) throws Exception {
        IgnoreSubject ret=null;
        
        java.io.ByteArrayInputStream bais=new java.io.ByteArrayInputStream(ignore);
        
        ret = MAPPER.readValue(bais, IgnoreSubject.class);
        
        bais.close();
        
        return (ret);
    }
    
}
