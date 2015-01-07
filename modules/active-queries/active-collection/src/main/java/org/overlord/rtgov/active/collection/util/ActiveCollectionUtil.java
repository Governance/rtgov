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
package org.overlord.rtgov.active.collection.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;

import org.overlord.rtgov.active.collection.ActiveCollectionSource;
import org.overlord.rtgov.active.collection.QuerySpec;

/**
 * This class provides utility functions for the Active Collection
 * model.
 *
 */
public final class ActiveCollectionUtil {
    
    private static final ObjectMapper MAPPER=new ObjectMapper();
    
    private static final TypeReference<java.util.List<ActiveCollectionSource>> TYPEREF=
                        new TypeReference<java.util.List<ActiveCollectionSource>>() { };
    
    static {
        MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Private constructor.
     */
    private ActiveCollectionUtil() {
    }
    
    /**
     * This method serializes a list of Active Collection Sources
     * into a JSON representation.
     * 
     * @param acs The active collection source list
     * @return The JSON serialized representation
     * @throws Exception Failed to serialize
     */
    public static byte[] serializeACS(java.util.List<ActiveCollectionSource> acs) throws Exception {
        byte[] ret=null;
        
        java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
        
        MAPPER.writeValue(baos, acs);
        
        ret = baos.toByteArray();
        
        baos.close();
        
        return (ret);
    }

    /**
     * This method deserializes a list of Active Collection Sources from a JSON representation.
     * 
     * @param acs The JSON representation of the Active Collection Sources
     * @return The Active Collection Source list
     * @throws Exception Failed to deserialize
     */
    @SuppressWarnings("unchecked")
    public static java.util.List<ActiveCollectionSource> deserializeACS(byte[] acs) throws Exception {
        java.util.List<ActiveCollectionSource> ret=null;
        
        java.io.ByteArrayInputStream bais=new java.io.ByteArrayInputStream(acs);
        
        ret = (java.util.List<ActiveCollectionSource>)MAPPER.readValue(bais, TYPEREF);
        
        bais.close();
        
        return (ret);
    }

    /**
     * This method serializes a query specification
     * into a JSON representation.
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
     * This method deserializes a query specification from a JSON representation.
     * 
     * @param qs The JSON representation of the query spec
     * @return The query spec
     * @throws Exception Failed to deserialize
     */
    public static QuerySpec deserializeQuerySpec(byte[] qs) throws Exception {
        QuerySpec ret=null;
        
        java.io.ByteArrayInputStream bais=new java.io.ByteArrayInputStream(qs);
        
        ret = (QuerySpec)MAPPER.readValue(bais, QuerySpec.class);
        
        bais.close();
        
        return (ret);
    }

}
