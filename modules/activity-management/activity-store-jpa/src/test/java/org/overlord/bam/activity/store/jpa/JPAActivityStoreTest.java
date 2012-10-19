/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-12, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.bam.activity.store.jpa;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.bam.activity.model.ActivityType;
import org.overlord.bam.activity.model.ActivityUnit;
import org.overlord.bam.activity.model.Origin;
import org.overlord.bam.activity.model.Context;
import org.overlord.bam.activity.model.soa.RequestSent;
import org.overlord.bam.activity.model.soa.ResponseReceived;
import org.overlord.bam.activity.server.QuerySpec;

public class JPAActivityStoreTest {

    private static final String OVERLORD_BAM_ACTIVITY_ORM = "overlord-bam-activity-orm";

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
        me1.setUnitId(id);
        me1.setUnitIndex(0);
        
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
        me2.setUnitId(id);
        me2.setUnitIndex(1);
        
        me2.setTimestamp(2000);
        me2.setContent("<tns:Confirmation xmlns:tns=\"http://www.savara.org\" amount=\"100\" />");
        me2.setFault("MyFault");
        me2.setMessageType("{http://message}Confirmation");
        me2.setOperation("myOp");
        me2.setServiceType("{http://service}OrderService");
        me2.setMessageId("corr2");
        me2.setReplyToId("corr1");
        me2.getProperties().put("customer", "Fred");
        me2.getProperties().put("manager", "Jane");
        
        Context c2=new Context();
        c2.setType(Context.Type.Endpoint);
        c2.setValue("abc123");
        me2.getContext().add(c2);
        
        act.getActivityTypes().add(me2);
        
        return (act);
    }
    
    @Test
    public void testStoreAndQueryAllORM() {
        testStoreAndQuery(OVERLORD_BAM_ACTIVITY_ORM, new QuerySpec());
    }
    
    @Test
    public void testQueryActivityFieldORM() {        
        java.util.List<ActivityType> results=
                testStoreAndQuery(OVERLORD_BAM_ACTIVITY_ORM,
                      "select evt from ActivityType evt "+
                              "where evt.operation = 'myOp'");
                        //"join evt.properties p "+
                        //"where p.value = 'Joe'");
        //"inner join evt.properties p \n"+
        //"where p.name = 'trader' and p.value = 'Joe'");
        
        System.out.println("RESULTS="+results);
    }

    protected java.util.List<ActivityType> testStoreAndQuery(String emname, QuerySpec qs) {
        java.util.List<ActivityType> results=null;
        
        JPAActivityStore astore=new JPAActivityStore();
        
        astore.setEntityManagerName(emname);
        astore.init();
        
        java.util.List<ActivityUnit> activities=new java.util.ArrayList<ActivityUnit>();
        
        activities.add(createTestActivityUnit("au1"));
        activities.add(createTestActivityUnit("au2"));
        
        try {
            astore.store(activities);
        } catch(Exception e) {
            fail("Failed to store activities: "+e);
        }
        
        try {
            results = astore.query(qs);
            
            System.out.println("RESULTS="+results);
        } catch(Exception e) {
            fail("Failed to query activities: "+e);
        }
        
        astore.close();
        
        return (results);
    }

    protected java.util.List<ActivityType> testStoreAndQuery(String emname, String query) {
        java.util.List<ActivityType> results=null;
        
        JPAActivityStore astore=new JPAActivityStore();
        
        astore.setEntityManagerName(emname);
        astore.init();
        
        java.util.List<ActivityUnit> activities=new java.util.ArrayList<ActivityUnit>();
        
        activities.add(createTestActivityUnit("au1"));
        activities.add(createTestActivityUnit("au2"));
        
        try {
            astore.store(activities);
        } catch(Exception e) {
            fail("Failed to store activities: "+e);
        }
        
        try {
            results = astore.query(query);
            
            System.out.println("RESULTS="+results);
        } catch(Exception e) {
            fail("Failed to query activities: "+e);
        }
        
        astore.close();
        
        return (results);
    }

}
