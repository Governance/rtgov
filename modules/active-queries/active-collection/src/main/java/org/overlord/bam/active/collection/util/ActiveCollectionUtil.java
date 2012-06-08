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
package org.overlord.bam.active.collection.util;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.overlord.bam.active.collection.ActiveCollectionSource;

/**
 * This class provides utility functions for the Active Collection
 * model.
 *
 */
public final class ActiveCollectionUtil {
    
    private static final ObjectMapper MAPPER=new ObjectMapper();
    
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
     * This method serializes an Active Collection Source
     * into a JSON representation.
     * 
     * @param acs The active collection source
     * @return The JSON serialized representation
     * @throws Exception Failed to serialize
     */
    public static byte[] serialize(ActiveCollectionSource acs) throws Exception {
        byte[] ret=null;
        
        java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
        
        MAPPER.writeValue(baos, acs);
        
        ret = baos.toByteArray();
        
        baos.close();
        
        return (ret);
    }

    /**
     * This method deserializes an Active Collection Source from a JSON representation.
     * 
     * @param acs The JSON representation of the Active Collection Source
     * @return The Active Collection Source
     * @throws Exception Failed to deserialize
     */
    public static ActiveCollectionSource deserialize(byte[] acs) throws Exception {
        ActiveCollectionSource ret=null;
        
        java.io.ByteArrayInputStream bais=new java.io.ByteArrayInputStream(acs);
        
        ret = MAPPER.readValue(bais, ActiveCollectionSource.class);
        
        bais.close();
        
        return (ret);
    }

}
