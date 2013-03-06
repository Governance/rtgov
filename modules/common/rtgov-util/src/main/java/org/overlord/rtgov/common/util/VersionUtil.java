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
package org.overlord.rtgov.common.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides utility functions for managing versions.
 *
 */
public final class VersionUtil {
    
    private static final Logger LOG=Logger.getLogger(VersionUtil.class.getName());

    /**
     * Private constructor.
     */
    private VersionUtil() {
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
