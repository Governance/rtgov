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
package org.overlord.bam.activity.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.bam.activity.model.ActivityUnit;
import org.overlord.bam.activity.model.Context;
import org.overlord.bam.activity.model.Origin;
import org.overlord.bam.activity.model.soa.RequestSent;
import org.overlord.bam.activity.model.soa.ResponseReceived;
import org.overlord.bam.activity.util.ActivityUtil;

public class ActivityUtilTest {

    public ActivityUnit createTestActivityUnit(String id) {
        ActivityUnit act=new ActivityUnit();
        
        act.setId(id);

        Origin origin=new Origin();
        origin.setHost("MyHost");
        origin.setNode("MyNode");
        origin.setPort("1010");
        origin.setPrincipal("Me");
        origin.setThread("MyThread");
        act.setOrigin(origin);

        RequestSent me1=new RequestSent();
        me1.setTimestamp(1000);
        me1.setContent("<tns:Order xmlns:tns=\"http://www.savara.org\" amount=\"100\" />");
        me1.setMessageType("{http://message}Order");
        me1.setOperation("myOp");
        me1.setServiceType("{http://service}OrderService");
        me1.setMessageId("corr1");
        me1.getProperties().put("customer", "Fred");
        me1.getProperties().put("trader", "Joe");
        
        Context c1=new Context();
        c1.setType(Context.Type.Conversation);
        c1.setValue("12345");
        me1.getContext().add(c1);
        
        act.getActivityTypes().add(me1);
        
        ResponseReceived me2=new ResponseReceived();
        me2.setTimestamp(2000);
        me2.setContent("<tns:Confirmation xmlns:tns=\"http://www.savara.org\" amount=\"100\" />");
        me2.setFault("MyFault");
        me2.setMessageType("{http://message}Confirmation");
        me2.setOperation("myOp");
        me2.setServiceType("{http://service}OrderService");
        me2.setMessageId("corr1");
        me2.getProperties().put("customer", "Fred");
        me2.getProperties().put("trader", "Joe");
        
        Context c2=new Context();
        c2.setType(Context.Type.Endpoint);
        c2.setValue("abc123");
        me2.getContext().add(c2);
        
        act.getActivityTypes().add(me2);
        
        return (act);
    }
    
    @Test
    public void testJSONCompareActivityUnit() {
        ActivityUnit act=createTestActivityUnit("TestId");
        
        try {
            byte[] b=ActivityUtil.serializeActivityUnit(act);
            
            if (b == null) {
                fail("null returned");
            }
            
            java.io.InputStream is=ActivityUtilTest.class.getResourceAsStream("/json/activity.json");
            byte[] inb2=new byte[is.available()];
            is.read(inb2);
            is.close();
            
            ActivityUnit act2=ActivityUtil.deserializeActivityUnit(inb2);
            
            byte[] b2=ActivityUtil.serializeActivityUnit(act2);            
            
            String s1=new String(b);
            String s2=new String(b2);
            
            if (!s1.equals(s2)) {
                fail("JSON is different: created="+s1+" stored="+s2);
            }

        } catch(Exception e) {
            e.printStackTrace();
            fail("Failed to serialize: "+e);
        }
    }
  
    @Test
    public void testJSONCompareActivityUnitList() {
        ActivityUnit act1=createTestActivityUnit("TestId1");
        ActivityUnit act2=createTestActivityUnit("TestId2");
        
        try {
            java.util.List<ActivityUnit> acts=new java.util.Vector<ActivityUnit>();
            acts.add(act1);
            acts.add(act2);
            
            byte[] b=ActivityUtil.serializeActivityUnitList(acts);
            
            if (b == null) {
                fail("null returned");
            }
            
            java.io.InputStream is=ActivityUtilTest.class.getResourceAsStream("/json/activityList.json");
            byte[] inb2=new byte[is.available()];
            is.read(inb2);
            is.close();
            
            java.util.List<ActivityUnit> acts2=ActivityUtil.deserializeActivityUnitList(inb2);
            
            byte[] b2=ActivityUtil.serializeActivityUnitList(acts2);            
            
            String s1=new String(b);
            String s2=new String(b2);
            
            if (!s1.equals(s2)) {
                fail("JSON is different: created="+s1+" stored="+s2);
            }

        } catch(Exception e) {
            e.printStackTrace();
            fail("Failed to serialize: "+e);
        }
    }
  
    @Test
    public void testJSONBinarySerialization() {
        ActivityUnit act1=createTestActivityUnit("TestId");
        
        try {
            java.io.ByteArrayOutputStream os=new java.io.ByteArrayOutputStream();
            java.io.ObjectOutputStream oos=new java.io.ObjectOutputStream(os);
            
            oos.writeObject(act1);
            
            oos.close();
            os.close();
            
            java.io.ByteArrayInputStream is=new java.io.ByteArrayInputStream(os.toByteArray());
            java.io.ObjectInputStream ois=new java.io.ObjectInputStream(is);
            
            ActivityUnit act2=(ActivityUnit)ois.readObject();
            
            ois.close();
            is.close();
            
            String s1=new String(ActivityUtil.serializeActivityUnit(act1));
            String s2=new String(ActivityUtil.serializeActivityUnit(act2));

            if (!s1.equals(s2)) {
                fail("Representations are different: s1="+s1+" s2="+s2);
            }
        } catch(Exception e) {
            e.printStackTrace();
            fail("Failed to serialize: "+e);
        }
    }
  
}
