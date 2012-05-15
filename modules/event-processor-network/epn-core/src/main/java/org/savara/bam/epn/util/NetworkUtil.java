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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.savara.bam.epn.Network;

/**
 * This class provides utility functions for the EPN
 * model.
 *
 */
public final class NetworkUtil {
    
    private static final Logger LOG=Logger.getLogger(NetworkUtil.class.getName());
    
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

    /**
     * This method determines whether the second version is newer
     * than the first.
     * 
     * @param ver1 The first version
     * @param ver2 The second version
     * @return Determines whether the second version is newer than the first
     */
    public static boolean isNewerVersion(String ver1, String ver2) {
        boolean ret=false;
        
        // Check if versions are numeric
        try {
            long v1=Long.parseLong(ver1);
            long v2=Long.parseLong(ver2);
            
            ret = (v2 > v1);
            
        } catch (Exception e) {
            
            // Check if dot-notation
            if (ver1.indexOf('.') != -1 && ver2.indexOf('.') != -1) {
                String[] v1=ver1.split("\\.");
                String[] v2=ver2.split("\\.");
                
                try {
                    int cur=0;
                    
                    while (!ret && cur < v1.length && cur < v2.length) {
                        try {
                            long p1=Long.parseLong(v1[cur]);
                            long p2=Long.parseLong(v2[cur]);
                            
                            if (p2 < p1) {
                                break;
                            }
                            
                            ret = (p2 > p1);
                        } catch (Exception e2) {
                            // Assume not numeric, so check lexically
                            ret = v2[cur].compareTo(v1[cur]) > 0;
                        }
                        
                        cur++;
                    }
                    
                } catch (Exception e2) {
                    if (LOG.isLoggable(Level.FINEST)) {
                        LOG.log(Level.FINEST, "Skipping version check for '"
                                    +ver1+"' against '"+ver2+"'", e);
                    }
                }
            } else {          
                // Check lexically
                ret = (ver2.compareTo(ver1) > 0);
            }
        }
        
        return (ret);
    }
}
