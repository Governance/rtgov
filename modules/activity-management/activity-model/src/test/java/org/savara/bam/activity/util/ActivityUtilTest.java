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
package org.savara.bam.activity.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.savara.bam.activity.model.ActivityUnit;
import org.savara.bam.activity.model.Context;
import org.savara.bam.activity.model.ContextType;
import org.savara.bam.activity.model.Origin;
import org.savara.bam.activity.model.soa.RequestSent;
import org.savara.bam.activity.model.soa.ResponseReceived;
import org.savara.bam.activity.util.ActivityUtil;

public class ActivityUtilTest {

    public ActivityUnit createTestActivityUnit(String id) {
        ActivityUnit act=new ActivityUnit();
        
        act.setId(id);

        Origin origin=new Origin();
        origin.setHost("MyHost");
        origin.setPort("1010");
        origin.setPrincipal("Me");
        origin.setThread("MyThread");
        origin.setTransaction("MyTxn");
        act.setOrigin(origin);

        Context c1=new Context();
        c1.setType(ContextType.Identifier);
        c1.setName("OrderId");
        c1.setValue("12345");
        act.getContext().add(c1);
        
        Context c2=new Context();
        c2.setType(ContextType.InstanceId);
        c2.setName("procId");
        c2.setValue("abc123");
        act.getContext().add(c2);
        
        RequestSent me1=new RequestSent();
        me1.setTimestamp(1000);
        me1.setContent("<tns:Order xmlns:tns=\"http://www.savara.org\" amount=\"100\" />");
        me1.setMessageType("{http://message}Order");
        me1.setOperation("myOp");
        me1.setServiceType("{http://service}OrderService");
        me1.setCorrelation("corr1");
        
        act.getActivityTypes().add(me1);
        
        ResponseReceived me2=new ResponseReceived();
        me2.setTimestamp(2000);
        me2.setContent("<tns:Confirmation xmlns:tns=\"http://www.savara.org\" amount=\"100\" />");
        me2.setFault("MyFault");
        me2.setMessageType("{http://message}Confirmation");
        me2.setOperation("myOp");
        me2.setServiceType("{http://service}OrderService");
        me2.setCorrelation("corr1");
        
        act.getActivityTypes().add(me2);
        
        return (act);
    }
    
    @Test
    public void testJSONSerialize() {
        ActivityUnit act=createTestActivityUnit("TestId");
        
        try {
            byte[] b=ActivityUtil.serialize(act);
            
            java.io.InputStream is=ActivityUtilTest.class.getResourceAsStream("/json/activity.json");
            byte[] b2=new byte[is.available()];
            is.read(b2);
            is.close();
            
            String s1=new String(b);
            String s2=new String(b2);
            
            if (!s1.equals(s2)) {
                fail("Test json is different: generated="+s1+" expected="+s2);
            }
        } catch(Exception e) {
            e.printStackTrace();
            fail("Failed to serialize: "+e);
        }
    }
  
    @Test
    public void testJSONDeserialize() {
        
        try {
            java.io.InputStream is=ActivityUtilTest.class.getResourceAsStream("/json/activity.json");

            byte[] b=new byte[is.available()];
            is.read(b);
            
            ActivityUnit act2=ActivityUtil.deserialize(b);
            
            // Serialize
            byte[] b2=ActivityUtil.serialize(act2);
            
            String s1=new String(b);
            String s2=new String(b2);
            
            if (!s1.equals(s2)) {
                fail("Test json is different: generated="+s1+" expected="+s2);
            }
            
        } catch(Exception e) {
            e.printStackTrace();
            fail("Failed to deserialize: "+e);
        }
    }
    
    @Test
    public void testJSONSerializeList() {
        ActivityUnit act1=createTestActivityUnit("TestId1");
        ActivityUnit act2=createTestActivityUnit("TestId2");
        
        try {
            java.util.List<ActivityUnit> acts=new java.util.Vector<ActivityUnit>();
            acts.add(act1);
            acts.add(act2);
            
            byte[] b1=ActivityUtil.serializeList(acts);
            
            java.io.InputStream is=ActivityUtilTest.class.getResourceAsStream("/json/activityList.json");
            byte[] b2=new byte[is.available()];
            is.read(b2);
            is.close();
            
            String s1=new String(b1);
            String s2=new String(b2);
            
            if (!s1.equals(s2)) {
                fail("Test json is different: generated="+s1+" expected="+s2);
            }
        } catch(Exception e) {
            e.printStackTrace();
            fail("Failed to serialize: "+e);
        }
    }
  
    @Test
    public void testJSONDeserializeList() {
        
        try {
            java.io.InputStream is=ActivityUtilTest.class.getResourceAsStream("/json/activityList.json");

            byte[] b=new byte[is.available()];
            is.read(b);
            
            java.util.List<ActivityUnit> act2=ActivityUtil.deserializeList(b);
            
            if (act2.size() != 2) {
               fail("Should be two activities");
            }
            
            if (!(act2.get(0) instanceof ActivityUnit)) {
                fail("First element is not an activity");
            }
            
            if (!(act2.get(1) instanceof ActivityUnit)) {
                fail("Second element is not an activity");
            }
            
            // Serialize
            byte[] b2=ActivityUtil.serializeList(act2);
            
            String s1=new String(b);
            String s2=new String(b2);
            
            if (!s1.equals(s2)) {
                fail("Test json is different: generated="+s1+" expected="+s2);
            }
            
        } catch(Exception e) {
            e.printStackTrace();
            fail("Failed to deserialize: "+e);
        }
    }
    
}
