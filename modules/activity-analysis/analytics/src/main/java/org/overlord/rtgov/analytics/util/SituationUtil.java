/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-12, Red Hat Middleware LLC, and others contributors as indicated
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
