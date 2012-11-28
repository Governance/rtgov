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
package org.overlord.bam.call.trace.util;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.overlord.bam.call.trace.model.CallTrace;

/**
 * This class provides utility functions for the call trace
 * model.
 *
 */
public final class CallTraceUtil {
    
    private static final ObjectMapper MAPPER=new ObjectMapper();

    static {
        SerializationConfig config=MAPPER.getSerializationConfig()
                .withSerializationInclusion(JsonSerialize.Inclusion.NON_NULL)
                .withSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
        
        MAPPER.setSerializationConfig(config);
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
        
        MAPPER.writeValue(baos, node);
        
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

}
