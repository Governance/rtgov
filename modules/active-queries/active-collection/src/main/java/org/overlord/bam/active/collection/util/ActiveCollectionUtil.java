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
package org.overlord.rtgov.active.collection.util;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.type.TypeReference;
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
        SerializationConfig config=MAPPER.getSerializationConfig().with(SerializationConfig.Feature.INDENT_OUTPUT);
        
        MAPPER.setSerializationConfig(config);
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
