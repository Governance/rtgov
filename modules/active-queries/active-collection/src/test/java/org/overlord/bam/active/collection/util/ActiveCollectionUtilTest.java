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
import org.overlord.bam.active.collection.QuerySpec;
import org.overlord.bam.active.collection.QuerySpec.Style;
import org.overlord.bam.active.collection.QuerySpec.Truncate;
import org.overlord.bam.active.collection.predicate.MVEL;
import org.overlord.bam.active.collection.util.ActiveCollectionUtil;

public class ActiveCollectionUtilTest {

    @Test
    public void testSerializeACS() {
        java.util.List<ActiveCollectionSource> list=new java.util.Vector<ActiveCollectionSource>();
        list.add(new TestActiveCollectionSource());
       
        try {
            ActiveCollectionUtil.serializeACS(list);            
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
            
            java.util.List<ActiveCollectionSource> acslist=ActiveCollectionUtil.deserializeACS(b);
            
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
    
    @Test
    public void testSerializeQuerySpec() {
        QuerySpec qs=new QuerySpec();
        qs.setCollection("OrderService");
        qs.setParent("ServiceResponseTime");
        qs.setPredicate(new MVEL(
                "serviceType == \"{urn:switchyard-quickstart-demo:orders:0.1.0}OrderService\" && "
                +"operation == \"submitOrder\""));
        qs.setMaxItems(5000);
        qs.setTruncate(Truncate.End);
        qs.setStyle(Style.Reversed);
        
        try {
            System.out.println("QUERY SPEC="
                        +new String(ActiveCollectionUtil.serializeQuerySpec(qs)));            
        } catch(Exception e) {
            fail("Failed to serialize: "+e);
        }
    }

    @Test
    public void testDeserializeQuerySpec() {
        QuerySpec qs=new QuerySpec();
        qs.setCollection("OrderService");
        qs.setParent("ServiceResponseTime");
        qs.setPredicate(new MVEL(
                "serviceType == \"{urn:switchyard-quickstart-demo:orders:0.1.0}OrderService\" && "
                +"operation == \"submitOrder\""));
        qs.setMaxItems(5000);
        qs.setTruncate(Truncate.End);
        qs.setStyle(Style.Reversed);
        
        try {
           byte[] b=ActiveCollectionUtil.serializeQuerySpec(qs);
           
           QuerySpec result=ActiveCollectionUtil.deserializeQuerySpec(b);
           
           if (result == null) {
               fail("Query spec was not deserialized");
           }
           
           if (!result.getCollection().equals(qs.getCollection())) {
               fail("Collection not the same");
           }
           
           if (!result.getParent().equals(qs.getParent())) {
               fail("Parent not the same");
           }
           
           if (!result.getPredicate().getClass().getName().equals(
                       qs.getPredicate().getClass().getName())) {
               fail("Predicate classes not the same");
           }
           
           if (result.getMaxItems() != qs.getMaxItems()) {
               fail("Max Items not the same");
           }
           
           if (result.getTruncate() != qs.getTruncate()) {
               fail("Truncate not the same");
           }
           
           if (result.getStyle() != qs.getStyle()) {
               fail("Style not the same");
           }
           
        } catch(Exception e) {
            fail("Failed to deserialize: "+e);
        }
    }

    public class TestActiveCollectionSource extends ActiveCollectionSource {
        
        public TestActiveCollectionSource() {
            setName("TestACS");
        }
    }
}
