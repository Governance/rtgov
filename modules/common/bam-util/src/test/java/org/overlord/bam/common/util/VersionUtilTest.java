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
package org.overlord.bam.common.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class VersionUtilTest {

    @Test
    public void testIsNewerVersionNumeric() {
        if (!VersionUtil.isNewerVersion("1", "2")) {
            fail("Wrong result 1");
        }
        
        if (!VersionUtil.isNewerVersion(""+System.currentTimeMillis(),
                         ""+System.currentTimeMillis()+2000)) {
            fail("Wrong result 2");
        }
    }
    
    @Test
    public void testIsNewerVersionLexical() {
        if (!VersionUtil.isNewerVersion("1.1.0.Final", "1.1.1.Final")) {
            fail("Wrong result 1");
        }
        
        if (!VersionUtil.isNewerVersion("1.1.0.Final", "1.10.0.Final")) {
            fail("Wrong result 2");
        }
        
        if (!VersionUtil.isNewerVersion("1.2.0.Final", "1.10.0.Final")) {
            fail("Wrong result 3");
        }
        
        if (!VersionUtil.isNewerVersion("1.2.0.Final", "2.1.0.Final")) {
            fail("Wrong result 4");
        }
        
        if (!VersionUtil.isNewerVersion("1.2.0.Final", "1.2.0.GA")) {
            fail("Wrong result 5");
        }
    }
}
