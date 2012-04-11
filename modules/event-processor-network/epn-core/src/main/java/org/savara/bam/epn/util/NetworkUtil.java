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
package org.savara.bam.epn.util;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.savara.bam.epn.Network;

/**
 * This class provides utility functions for the EPN
 * model.
 *
 */
public class NetworkUtil {
    
    private static final ObjectMapper MAPPER=new ObjectMapper();
    
    static {
        MAPPER.getSerializationConfig().set(SerializationConfig.Feature.INDENT_OUTPUT, true);
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
        
        return(ret);
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
        
        return(ret);
    }

}
