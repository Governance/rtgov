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

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.bam.active.collection.ActiveCollectionSource;
import org.overlord.bam.active.collection.util.ActiveCollectionUtil;

public class ActiveCollectionUtilTest {

    @Test
    public void testSerializeACS() {
        java.util.List<ActiveCollectionSource> list=new java.util.Vector<ActiveCollectionSource>();
        list.add(new TestActiveCollectionSource());
       
        try {
            ActiveCollectionUtil.serialize(list);            
        } catch(Exception e) {
            fail("Failed to serialize: "+e);
        }
    }

    @Test
    public void testDeserializeACS() {
        try {
            java.io.InputStream is=ActiveCollectionUtilTest.class.getResourceAsStream("/jsondata/acs.json");
            
            byte[] b = new byte[is.available()];
            is.read(b);
            
            is.close();
            
            java.util.List<ActiveCollectionSource> acslist=ActiveCollectionUtil.deserialize(b);
            
            if (acslist.size() != 1) {
                fail("List should have 1 source: "+acslist.size());
            }

            if (!acslist.get(0).getName().equals("TestACS")) {
                fail("Name is not correct: "+acslist.get(0).getName());
            }
            
            if (acslist.get(0).getItemExpiration() != 200) {
                fail("Item expiration is not correct: "+acslist.get(0).getItemExpiration());
            }
            
            if (acslist.get(0).getMaxItems() != 1000) {
                fail("Max items is not correct: "+acslist.get(0).getMaxItems());
            }
            
        } catch(Exception e) {
            e.printStackTrace();
            fail("Failed to deserialize: "+e);
        }
    }
    
    public class TestActiveCollectionSource extends ActiveCollectionSource {
        
        public TestActiveCollectionSource() {
            setName("TestACS");
        }
    }
}
