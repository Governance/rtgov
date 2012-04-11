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
import org.savara.bam.activity.model.Activity;
import org.savara.bam.activity.model.Component;
import org.savara.bam.activity.model.Context;
import org.savara.bam.activity.model.MessageExchange;
import org.savara.bam.activity.model.MessageExchange.Direction;
import org.savara.bam.activity.model.MessageExchange.InvocationType;
import org.savara.bam.activity.util.ActivityUtil;

public class ActivityUtilTest {

    public Activity createTestActivity(String id) {
        Activity act=new Activity();
        
        act.setId(id);
        act.setTimestamp(1000);
        
        Context context=new Context();
        context.setHost("MyHost");
        context.setPort("1010");
        context.setPrincipal("Me");
        context.setThread("MyThread");
        context.setTransaction("MyTxn");
        
        act.setContext(context);
        
        Component component=new Component();
        component.setProcessDefinition("MyProcess");
        component.setProcessInstance("MyInstance");
        component.setService("MyService");
        component.setTask("MyTask");
        
        act.setComponent(component);
        
        MessageExchange me=new MessageExchange();
        me.setContent("<tns:Order xmlns:tns=\"http://www.savara.org\" amount=\"100\" />");
        me.setCorrelation("CorId");
        me.setDirection(Direction.Outbound);
        me.setFault("MyFault");
        me.setInvocationType(InvocationType.Request);
        me.setMessageType("{http://message}type");
        me.setOperation("myOp");
        me.setServiceType("{http://service}type");
        
        act.setActivityType(me);
        
        return (act);
    }
    
    @Test
    public void testSerialize() {
        Activity act=createTestActivity("TestId");
        
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
    public void testDeserialize() {
        
        try {
            java.io.InputStream is=ActivityUtilTest.class.getResourceAsStream("/json/activity.json");

            byte[] b=new byte[is.available()];
            is.read(b);
            
            Activity act2=ActivityUtil.deserialize(b);
            
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
    public void testSerializeList() {
        Activity act1=createTestActivity("TestId1");
        Activity act2=createTestActivity("TestId2");
        
        try {
            java.util.List<Activity> acts=new java.util.Vector<Activity>();
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
    public void testDeserializeList() {
        
        try {
            java.io.InputStream is=ActivityUtilTest.class.getResourceAsStream("/json/activityList.json");

            byte[] b=new byte[is.available()];
            is.read(b);
            
            java.util.List<Activity> act2=ActivityUtil.deserializeList(b);
            
            if (act2.size() != 2) {
               fail("Should be two activities");
            }
            
            if (!(act2.get(0) instanceof Activity)) {
                fail("First element is not an activity");
            }
            
            if (!(act2.get(1) instanceof Activity)) {
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
