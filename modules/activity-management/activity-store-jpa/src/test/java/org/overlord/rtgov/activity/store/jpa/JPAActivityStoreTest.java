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
package org.overlord.rtgov.activity.store.jpa;

import static org.junit.Assert.fail;

import java.net.URL;

import org.hibernate.Session;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.activity.model.Context.Type;
import org.overlord.rtgov.activity.model.Origin;
import org.overlord.rtgov.activity.model.soa.RequestSent;
import org.overlord.rtgov.activity.model.soa.ResponseReceived;
import org.overlord.rtgov.activity.server.QuerySpec;
import org.overlord.rtgov.common.jpa.JpaStore;
import org.overlord.rtgov.common.jpa.JpaStore.JpaWork;

public class JPAActivityStoreTest {

    private static final String ENDPOINT_ID_1 = "abc123";
    private static final String ENDPOINT_ID_2 = "abc456";
    private static final String AU_ID_2 = "au2";
    private static final String AU_ID_1 = "au1";
    private static final String CONV_ID_2 = "2";
    private static final String CONV_ID_1 = "1";
    private static final String JPQL_FORMAT = "jpql";
    private static final String MONGODB_FORMAT = "mongodb";
    private static final String OVERLORD_RTGOV_ACTIVITY_ORM = "overlord-rtgov-activity-orm";
    private static final String OVERLORD_RTGOV_ACTIVITY_OGM_M = "overlord-rtgov-activity-ogm-mongodb";
    
    private static JpaStore jpaStore;
    private static JPAActivityStore activityStore;
    
    @BeforeClass
    public static void initialiseEntityManager() throws Exception{
    	final URL configXml = JPAActivityStoreTest.class.getClassLoader().getResource("hibernate-test.cfg.xml");
    	jpaStore = new JpaStore(configXml);
    	activityStore = new JPAActivityStore(jpaStore);
    }
    
    public ActivityUnit createTestActivityUnit(String id, String convId, String endpointId, long baseTime) {
        ActivityUnit act=new ActivityUnit();
        
        act.setId(id);

        Origin origin=new Origin();
        origin.setHost("MyHost");
        origin.setNode("MyNode");
        origin.setPrincipal("Me");
        origin.setThread("MyThread");
        act.setOrigin(origin);

        RequestSent me1=new RequestSent();
        me1.setUnitId(id);
        me1.setUnitIndex(0);
        
        me1.setTimestamp(baseTime+1000);
        me1.setContent("<tns:Order xmlns:tns=\"http://www.savara.org\" amount=\"100\" />");
        me1.setMessageType("{http://message}Order");
        me1.setOperation("myOp");
        me1.setServiceType("{http://service}OrderService");
        me1.setMessageId("corr1");
        me1.getProperties().put("customer", "Fred");
        me1.getProperties().put("trader", "Joe");
        
        Context c1=new Context();
        c1.setType(Context.Type.Conversation);
        c1.setValue(convId);
        me1.getContext().add(c1);
        
        act.getActivityTypes().add(me1);
        
        ResponseReceived me2=new ResponseReceived();
        me2.setUnitId(id);
        me2.setUnitIndex(1);
        
        me2.setTimestamp(baseTime+2000);
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
        c2.setValue(endpointId);
        me2.getContext().add(c2);
        
        act.getActivityTypes().add(me2);
        
        return (act);
    }
    
    protected void checkTableEmpty(final String table) {
    	int rows = jpaStore.withJpa(new JpaWork<Integer>() {
			public Integer perform(Session s) {
				return s.createSQLQuery("SELECT * FROM "+table).list().size();
			}
		});
        
        if (rows != 0) {
            fail("Table '"+table+"' is not empty: "+rows);
        }
    }
    
    protected void checkAllTablesEmpty() {
        checkTableEmpty("RTGOV_ACTIVITY_UNITS");
        checkTableEmpty("RTGOV_ACTIVITIES");
        checkTableEmpty("RTGOV_ACTIVITY_CONTEXT");
        checkTableEmpty("RTGOV_ACTIVITY_PROPERTIES");        
    }
    
    @Test
    public void testQueryUnsupportedFormat() {
        
        try {
            activityStore.query(new QuerySpec()
                        .setFormat("mvel")
                        .setExpression("true"));
            
            fail("Should have returned an exception");
        } catch (IllegalArgumentException iae) {
            System.out.println("EXPECTED EXCEPTION="+iae);
        } catch (Exception e) {
            fail("Unexpected exception: "+e);
        }
    }
    
    @Test
    public void testQueryNotSelect() {
        
        try {
            activityStore.query(new QuerySpec()
                        .setFormat("jpql")
                        .setExpression("update activities"));
            
            fail("Should have returned an exception");
        } catch (IllegalArgumentException iae) {
            System.out.println("EXPECTED EXCEPTION="+iae);
        } catch (Exception e) {
            fail("Unexpected exception: "+e);
        }
    }
    
    @Test
    public void testStoreAndQueryAllORM() {
        
        checkAllTablesEmpty();
        
        java.util.List<ActivityType> results=
            testStoreAndQuery(OVERLORD_RTGOV_ACTIVITY_ORM,
                new QuerySpec()
                    .setFormat(JPQL_FORMAT)
                    .setExpression("SELECT at FROM ActivityType at"));
                
        if (results.size() != 4) {
            fail("Expected 4 entries: "+results.size());
            
        }
        
        System.out.println("RESULTS="+results);
        
        checkAllTablesEmpty();
    }
    
    @Test
    public void testQueryActivityFieldORM() {
    	
        checkAllTablesEmpty();

        java.util.List<ActivityType> results=
            testStoreAndQuery(OVERLORD_RTGOV_ACTIVITY_ORM,
                new QuerySpec()
                    .setFormat(JPQL_FORMAT)
                    .setExpression("SELECT at from ActivityType at "+
                              "WHERE at.operation = 'myOp' " +
                              "AND at.fault = 'MyFault'"));
                        //"join evt.properties p "+
                        //"where p.value = 'Joe'");
        //"inner join evt.properties p \n"+
        //"where p.name = 'trader' and p.value = 'Joe'");
                 
        if (results.size() != 2) {
            fail("Expected 2 entries: "+results.size());
            
        }

        System.out.println("RESULTS="+results);

        checkAllTablesEmpty();
    }

    @Test
    public void testStoreAndGetATsORM() {
        java.util.List<ActivityType> results=null;
                
        java.util.List<ActivityUnit> activities=new java.util.ArrayList<ActivityUnit>();

        checkAllTablesEmpty();
        
        ActivityUnit au1=createTestActivityUnit(AU_ID_1, CONV_ID_1, ENDPOINT_ID_1, 0);
        ActivityUnit au2=createTestActivityUnit(AU_ID_2, CONV_ID_2, ENDPOINT_ID_2, 5000);
        
        activities.add(au1);
        activities.add(au2);
        
        try {
            activityStore.store(activities);
        } catch(Exception e) {
            fail("Failed to store activities: "+e);
        }
        
        try {
            Context query=new Context();
            query.setType(Type.Conversation);
            query.setValue(CONV_ID_1);
            
            results = activityStore.getActivityTypes(query);
        } catch(Exception e) {
            fail("Failed to query activities: "+e);
        } finally {
            try {
                activityStore.remove(au1);
            	activityStore.remove(au2);
           } catch (Exception e) {
                fail("Failed to remove activity units: "+e);
           }

        }
 
        System.out.println("RESULTS="+results);
        
        if (results.size() != 1) {
            fail("Only expecting a single activity event: "+results.size());
        }

        checkAllTablesEmpty();
    }
    
    @Test
    @Ignore("RTGOV-234 - awaiting support for hibernate OGM")
    public void testStoreAndQueryAllOGMM() {
        testStoreAndQuery(OVERLORD_RTGOV_ACTIVITY_OGM_M,
                new QuerySpec().setFormat(MONGODB_FORMAT).
                    setExpression("db.ActivityType.find()"));
    }
    
    @Test
    public void testGetActivityTypesContextConversation() {
        java.util.List<ActivityType> results=null;
        
        java.util.List<ActivityUnit> activities=new java.util.ArrayList<ActivityUnit>();
        
        checkAllTablesEmpty();

        ActivityUnit au1=createTestActivityUnit("3", "3", "3", 0);
        ActivityUnit au2=createTestActivityUnit("4", "4", "4", 5000);
        
        activities.add(au1);
        activities.add(au2);
        
        try {
            activityStore.store(activities);
        } catch(Exception e) {
            fail("Failed to store activities: "+e);
        }
        
        try {
            Context context=new Context();
            context.setType(Type.Conversation);
            context.setValue("3");
            
            results = activityStore.getActivityTypes(context);
        } catch(Exception e) {
            fail("Failed to query activities: "+e);
        } finally {
            try {
                activityStore.remove(au1);
                activityStore.remove(au2);
            } catch (Exception e) {
                fail("Failed to remove activity units: "+e);
            }
        }
        
        if (results == null) {
            fail("Results is null");
        }
        
        if (results.size() != 1) {
            fail("Expecting 1 result: "+results.size());
        }
        
        if (!results.get(0).getUnitId().equals("3")) {
            fail("Expecting au 3: "+results.get(0).getUnitId());
        }

        checkAllTablesEmpty();
    }
    
    @Test
    public void testGetActivityTypesContextEndpoint() {
        java.util.List<ActivityType> results=null;
        
        java.util.List<ActivityUnit> activities=new java.util.ArrayList<ActivityUnit>();
        
        checkAllTablesEmpty();

        ActivityUnit au1=createTestActivityUnit("5", "5", "5", 0);
        ActivityUnit au2=createTestActivityUnit("6", "6", "6", 5000);
        
        activities.add(au1);
        activities.add(au2);
        
        try {
            activityStore.store(activities);
        } catch(Exception e) {
            fail("Failed to store activities: "+e);
        }
        
        try {
            Context context=new Context();
            context.setType(Type.Endpoint);
            context.setValue("6");
            
            results = activityStore.getActivityTypes(context);
        } catch(Exception e) {
            fail("Failed to query activities: "+e);
        } finally {
            try {
                activityStore.remove(au1);
                activityStore.remove(au2);
            } catch (Exception e) {
                fail("Failed to remove activity units: "+e);
            }
        }
        
        if (results == null) {
            fail("Results is null");
        }
        
        if (results.size() != 1) {
            fail("Expecting 1 result: "+results.size());
        }
        
        if (!results.get(0).getUnitId().equals("6")) {
            fail("Expecting au 6: "+results.get(0).getUnitId());
        }

        checkAllTablesEmpty();
    }    
    
    @Test
    public void testGetActivityTypesContextTimeframe() {
        java.util.List<ActivityType> results1=null;
        java.util.List<ActivityType> results2=null;
        
        java.util.List<ActivityUnit> activities=new java.util.ArrayList<ActivityUnit>();
        
        checkAllTablesEmpty();

        ActivityUnit au1=createTestActivityUnit("7", "C1", "E1", 0);
        ActivityUnit au2=createTestActivityUnit("8", "C1", "E1", 5000);
        
        activities.add(au1);
        activities.add(au2);
        
        try {
            activityStore.store(activities);
        } catch(Exception e) {
            fail("Failed to store activities: "+e);
        }
        
        try {
            Context context1=new Context();
            context1.setType(Type.Conversation);
            context1.setValue("C1");

            results1 = activityStore.getActivityTypes(context1);
            
            Context context2=new Context();
            context2.setType(Type.Conversation);
            context2.setValue("C1");

            results2 = activityStore.getActivityTypes(context2, 2500, 7500);
        } catch(Exception e) {
            fail("Failed to query activities: "+e);
        } finally {
            try {
                activityStore.remove(au1);
                activityStore.remove(au2);
            } catch (Exception e) {
                fail("Failed to remove activity units: "+e);
            }
        }
        
        if (results1 == null) {
            fail("Results1 is null");
        }
        
        if (results1.size() != 2) {
            fail("Expecting 2 results: "+results1.size());
        }
        
        if (results2 == null) {
            fail("Results2 is null");
        }
        
        if (results2.size() != 1) {
            fail("Expecting 1 result: "+results2.size());
        }
        
        if (!results2.get(0).getUnitId().equals("8")) {
            fail("Expecting au 8: "+results2.get(0).getUnitId());
        }

        checkAllTablesEmpty();
    }
    
    @Test
    public void testGetActivityTypesNoContextTimeframe() {
        java.util.List<ActivityType> results1=null;
        
        java.util.List<ActivityUnit> activities=new java.util.ArrayList<ActivityUnit>();
        
        checkAllTablesEmpty();

        ActivityUnit au1=createTestActivityUnit("7", "C1", "E1", 0);
        ActivityUnit au2=createTestActivityUnit("8", "C1", "E1", 5000);
        
        activities.add(au1);
        activities.add(au2);
        
        try {
            activityStore.store(activities);
        } catch(Exception e) {
            fail("Failed to store activities: "+e);
        }
        
        try {
            results1 = activityStore.getActivityTypes(null, 2500, 7500);
        } catch(Exception e) {
            fail("Failed to query activities: "+e);
        } finally {
            try {
                activityStore.remove(au1);
                activityStore.remove(au2);
            } catch (Exception e) {
                fail("Failed to remove activity units: "+e);
            }
        }
        
        if (results1 == null) {
            fail("Results1 is null");
        }
        
        if (results1.size() != 2) {
            fail("Expecting 2 results: "+results1.size());
        }

        checkAllTablesEmpty();
    }
    
    
    @Test
    public void testGetActivityTypesNoContextNoTimeframe() {
        java.util.List<ActivityUnit> activities=new java.util.ArrayList<ActivityUnit>();
        
        checkAllTablesEmpty();

        ActivityUnit au1=createTestActivityUnit("7", "C1", "E1", 0);
        ActivityUnit au2=createTestActivityUnit("8", "C1", "E1", 5000);
        
        activities.add(au1);
        activities.add(au2);
        
        try {
            activityStore.store(activities);
        } catch(Exception e) {
            fail("Failed to store activities: "+e);
        }
        
        try {
            activityStore.getActivityTypes(null, 0, 0);
            
            fail("Should have thrown exception");
        } catch(Exception e) {
        } finally {
            try {
                activityStore.remove(au1);
                activityStore.remove(au2);
            } catch (Exception e) {
                fail("Failed to remove activity units: "+e);
            }
        }

        checkAllTablesEmpty();
    }
    
    protected java.util.List<ActivityType> testStoreAndQuery(String emname, QuerySpec qs) {
        java.util.List<ActivityType> results=null;
        
        java.util.List<ActivityUnit> activities=new java.util.ArrayList<ActivityUnit>();
        
        ActivityUnit au1=createTestActivityUnit(AU_ID_1, CONV_ID_1, ENDPOINT_ID_1, 0);
        ActivityUnit au2=createTestActivityUnit(AU_ID_2, CONV_ID_2, ENDPOINT_ID_2, 5000);
        
        activities.add(au1);
        activities.add(au2);
        
        try {
        	activityStore.store(activities);
        } catch(Exception e) {
            fail("Failed to store activities: "+e);
        }
        
        try {
            results = activityStore.query(qs);
        } catch(Exception e) {
            fail("Failed to query activities: "+e);
        } finally {
            try {
            	activityStore.remove(au1);
            	activityStore.remove(au2);
            } catch (Exception e) {
                fail("Failed to remove activity units: "+e);
            }
        }
        
        return (results);
    }
}
