/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-13, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.rtgov.activity.util;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.TypeReference;
import org.overlord.rtgov.activity.interceptor.ActivityInterceptor;

/**
 * This class provides utility functions for the activity interceptors.
 *
 */
public final class ActivityInterceptorUtil {
    
    private static final ObjectMapper MAPPER=new ObjectMapper();

    private static final TypeReference<java.util.List<ActivityInterceptor>> ACTIVITY_INTERCEPTOR_LIST=
            new TypeReference<java.util.List<ActivityInterceptor>>() { };
    
    private static ObjectWriter AILIST_WRITER=null;

    static {
        SerializationConfig config=MAPPER.getSerializationConfig()
                .withSerializationInclusion(JsonSerialize.Inclusion.NON_NULL)
                .withSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
        
        MAPPER.setSerializationConfig(config);
        
        AILIST_WRITER = MAPPER.writerWithType(ACTIVITY_INTERCEPTOR_LIST);
    }
    
    /**
     * Private constructor.
     */
    private ActivityInterceptorUtil() {
    }
    
    /**
     * This method serializes an ActivityInterceptor list into a JSON representation.
     * 
     * @param ais The ActivityInterceptor list
     * @return The JSON serialized representation
     * @throws Exception Failed to serialize
     */
    public static byte[] serializeActivityInterceptorList(java.util.List<ActivityInterceptor> ais) throws Exception {
        byte[] ret=null;
        
        java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
        
        AILIST_WRITER.writeValue(baos, ais);
        
        ret = baos.toByteArray();
        
        baos.close();
        
        return (ret);
    }

    /**
     * This method deserializes an ActivityInterceptor list from a JSON representation.
     * 
     * @param ips The JSON representation of the ActivityInterceptor list
     * @return The ActivityInterceptor list
     * @throws Exception Failed to deserialize
     */
    public static java.util.List<ActivityInterceptor> deserializeActivityInterceptorList(byte[] ais) throws Exception {
        java.util.List<ActivityInterceptor> ret=null;
        
        java.io.ByteArrayInputStream bais=new java.io.ByteArrayInputStream(ais);
        
        ret = MAPPER.readValue(bais, ACTIVITY_INTERCEPTOR_LIST);
        
        bais.close();
        
        return (ret);
    }
}
