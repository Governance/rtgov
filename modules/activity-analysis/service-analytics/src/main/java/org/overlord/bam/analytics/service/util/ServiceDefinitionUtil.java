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
package org.overlord.bam.analytics.service.util;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.overlord.bam.analytics.service.ServiceDefinition;

/**
 * This class provides utility functions related to the service
 * definition.
 *
 */
public class ServiceDefinitionUtil {

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
     * This method serializes a Service Definition into a JSON representation.
     * 
     * @param sdef The service definition
     * @return The JSON serialized representation
     * @throws Exception Failed to serialize
     */
    public static byte[] serializeServiceDefinition(ServiceDefinition sdef) throws Exception {
        byte[] ret=null;
        
        java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
        
        MAPPER.writeValue(baos, sdef);
        
        ret = baos.toByteArray();
        
        baos.close();
        
        return (ret);
    }

    /**
     * This method deserializes a Service Definition from a JSON representation.
     * 
     * @param sdef The JSON representation of the service definition
     * @return The service definition
     * @throws Exception Failed to deserialize
     */
    public static ServiceDefinition deserializeServiceDefinition(byte[] sdef) throws Exception {
        ServiceDefinition ret=null;
        
        java.io.ByteArrayInputStream bais=new java.io.ByteArrayInputStream(sdef);
        
        ret = MAPPER.readValue(bais, ServiceDefinition.class);
        
        bais.close();
        
        return (ret);
    }
    
}
