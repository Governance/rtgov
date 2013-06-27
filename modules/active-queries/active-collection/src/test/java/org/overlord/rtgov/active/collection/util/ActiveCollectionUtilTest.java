/*
 * 2012-3 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.rtgov.active.collection.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.active.collection.ActiveCollectionSource;
import org.overlord.rtgov.active.collection.QuerySpec;
import org.overlord.rtgov.active.collection.QuerySpec.Style;
import org.overlord.rtgov.active.collection.QuerySpec.Truncate;
import org.overlord.rtgov.active.collection.predicate.MVEL;
import org.overlord.rtgov.active.collection.util.ActiveCollectionUtil;

public class ActiveCollectionUtilTest {

    @Test
    public void testSerializeACS() {
        ActiveCollectionSource acs=new ActiveCollectionSource();
        acs.setName("TestACS");
        acs.getProperties().put("testProp", "testValue");
        
        java.util.List<ActiveCollectionSource> list=new java.util.Vector<ActiveCollectionSource>();
        list.add(acs);
       
        try {
            byte[] b=ActiveCollectionUtil.serializeACS(list);
            
            System.out.println("Serialized ACS="+new String(b));
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
}
