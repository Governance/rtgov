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
package org.overlord.bam.tests.actmgmt.jbossas.actsvrrest;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.inject.Inject;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.overlord.bam.activity.model.ActivityType;
import org.overlord.bam.activity.model.ActivityUnit;
import org.overlord.bam.activity.model.soa.RequestSent;
import org.overlord.bam.activity.server.QuerySpec;
import org.overlord.bam.activity.store.mem.MemActivityStore;
import org.overlord.bam.activity.util.ActivityUtil;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class ActivityServerRESTTest {
    
    private static final String MVEL_FORMAT = "mvel";
    private static final ObjectMapper MAPPER=new ObjectMapper();

    static {
        SerializationConfig config=MAPPER.getSerializationConfig().with(SerializationConfig.Feature.INDENT_OUTPUT);
        
        MAPPER.setSerializationConfig(config);
    }

    @Inject
    org.overlord.bam.activity.server.ActivityStore _activityStore=null;

    @Deployment
    public static WebArchive createDeployment() {
        String version=System.getProperty("bam.version");
        
        return ShrinkWrap.create(WebArchive.class, "overlord-bam.war")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .setWebXML("web.xml")
            .addAsLibraries(
                    DependencyResolvers
                    .use(MavenDependencyResolver.class)
                    .artifacts("org.overlord.bam.activity-management:activity-server-impl:"+version,
                            "org.overlord.bam.activity-management:activity-store-mem:"+version,
                            "org.overlord.bam.activity-management:activity-server-rests:"+version)
                    .resolveAsFiles());
    }
    
    @Test
    public void queryAll() {
        
        if (_activityStore == null) {
            fail("Activity Store has not been initialized");
        } else if (_activityStore instanceof MemActivityStore) {
            ((MemActivityStore)_activityStore).clear();
        } else {
            fail("Activity Store unexpected type");
        }
        
        try {

            // Check if any activity types already exist
            java.util.List<ActivityType> result=_activityStore.query(
                    new QuerySpec().setFormat(MVEL_FORMAT).setExpression("true"));
            
            if (result == null) {
                fail("Initial query result is null");
            }
            
            if (result.size() != 0) {
                fail("Initial query result should be empty");
            }

            // Add two entries
            ActivityUnit au1=new ActivityUnit();
            au1.setId("au1");
            
            RequestSent rs1=new RequestSent();
            rs1.setOperation("op1");
            au1.getActivityTypes().add(rs1);
            
            ActivityUnit au2=new ActivityUnit();
            au2.setId("au2");
            
            RequestSent rs2=new RequestSent();
            rs2.setOperation("op2");
            au2.getActivityTypes().add(rs2);
            
            java.util.List<ActivityUnit> aulist=new java.util.ArrayList<ActivityUnit>();
            aulist.add(au1);
            aulist.add(au2);
            
            _activityStore.store(aulist);
            
            // Query via REST interface
            QuerySpec query=new QuerySpec().setFormat(MVEL_FORMAT).setExpression("true");
            
            URL queryUrl = new URL("http://localhost:8080/overlord-bam/activity/query");
            
            HttpURLConnection connection = (HttpURLConnection) queryUrl.openConnection();
            connection.setRequestMethod("POST");

            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);
            connection.setRequestProperty("Content-Type",
                        "application/json");

            java.io.OutputStream os=connection.getOutputStream();
            
            byte[] b=ActivityUtil.serializeQuerySpec(query);
            os.write(b);
            
            os.flush();
            os.close();
            
            java.io.InputStream is=connection.getInputStream();

            b = new byte[is.available()];
            is.read(b);
            
            System.out.println(">>>> JSON="+new String(b));
            result = ActivityUtil.deserializeActivityTypeList(b);
            
            is.close();
            
            System.out.println("RETRIEVED RESULTS="+result);
           
            if (result == null) {
                fail("Follow-up query result is null");
            }
            
            if (result.size() != 2) {
                fail("Follow-up query result should have 2 items: "+result.size());
            }

        } catch(Exception e) {
            e.printStackTrace();
            fail("Failed to access activity server: "+e);
        }
    }

    @Test
    public void getActivityUnitId() {
        
        if (_activityStore == null) {
            fail("Activity Store has not been initialized");
        } else if (_activityStore instanceof MemActivityStore) {
            ((MemActivityStore)_activityStore).clear();
        } else {
            fail("Activity Store unexpected type");
        }
        
        try {

            java.util.List<ActivityType> atypes=_activityStore.query(
                    new QuerySpec().setFormat(MVEL_FORMAT).setExpression("true"));
            
            if (atypes == null) {
                fail("Initial query result is null");
            }
            
            if (atypes.size() != 0) {
                fail("Initial query result should be empty");
            }

            // Add two entries
            ActivityUnit au1=new ActivityUnit();
            au1.setId("au1");
            
            RequestSent rs1=new RequestSent();
            rs1.setOperation("op1");
            au1.getActivityTypes().add(rs1);
            
            ActivityUnit au2=new ActivityUnit();
            au2.setId("au2");
            
            RequestSent rs2=new RequestSent();
            rs2.setOperation("op2");
            au2.getActivityTypes().add(rs2);
            
            java.util.List<ActivityUnit> aulist=new java.util.ArrayList<ActivityUnit>();
            aulist.add(au1);
            aulist.add(au2);
            
            _activityStore.store(aulist);
            
            // Query via REST interface
            URL getUrl = new URL("http://localhost:8080/overlord-bam/activity/unit?id="+au2.getId());
            HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
            connection.setRequestMethod("GET");
    
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);
            connection.setRequestProperty("Content-Type",
                        "application/json");
    
            java.io.InputStream is=connection.getInputStream();
           
            byte[] b=new byte[is.available()];
            is.read(b);
            
            is.close();
            
            ActivityUnit auresult=ActivityUtil.deserializeActivityUnit(b);
            
            System.out.println("RETRIEVED RESULTS="+auresult);
            
            if (auresult == null) {
                fail("Follow-up query result is null");
            }
            
            if (!auresult.getId().equals("au2")) {
                fail("Incorrect activity unit");
            }

        } catch(Exception e) {
            fail("Failed to access activity server: "+e);
        }
    }

    @Test
    public void queryActivityTypes() {
        
        if (_activityStore == null) {
            fail("Activity Store has not been initialized");
        } else if (_activityStore instanceof MemActivityStore) {
            ((MemActivityStore)_activityStore).clear();
        } else {
            fail("Activity Store unexpected type");
        }
        
        try {

            java.util.List<ActivityType> result=_activityStore.query(
                    new QuerySpec().setFormat(MVEL_FORMAT).setExpression("true"));
            
            if (result == null) {
                fail("Initial query result is null");
            }
            
            if (result.size() != 0) {
                fail("Initial query result should be empty");
            }

            // Add two entries
            ActivityUnit au1=new ActivityUnit();
            au1.setId("au1");
            
            RequestSent rs1=new RequestSent();
            rs1.setOperation("op1");
            au1.getActivityTypes().add(rs1);
            
            ActivityUnit au2=new ActivityUnit();
            au2.setId("au2");
            
            RequestSent rs2=new RequestSent();
            rs2.setOperation("op2");
            au2.getActivityTypes().add(rs2);
            
            java.util.List<ActivityUnit> aulist=new java.util.ArrayList<ActivityUnit>();
            aulist.add(au1);
            aulist.add(au2);
            
            _activityStore.store(aulist);
            
            // Query via REST interface
            URL getUrl = new URL("http://localhost:8080/overlord-bam/activity/query");
            HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
            connection.setRequestMethod("POST");
    
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);
            connection.setRequestProperty("Content-Type",
                        "application/json");
    
            java.io.OutputStream os=connection.getOutputStream();
            
            QuerySpec qs=new QuerySpec().setFormat(MVEL_FORMAT).setExpression("true");
            
            byte[] b=ActivityUtil.serializeQuerySpec(qs);
            os.write(b);
            
            os.flush();
            os.close();
            
            java.io.InputStream is=connection.getInputStream();

            b = new byte[is.available()];
            is.read(b);
            
            is.close();
            
            result = ActivityUtil.deserializeActivityTypeList(b);
            
            System.out.println("RETRIEVED RESULTS="+result);
            
            if (result == null) {
                fail("Follow-up query result is null");
            }
            
            if (result.size() != 2) {
                fail("Follow-up query result should have 2 items: "+result.size());
            }
            
            if (!((RequestSent)result.get(0)).getOperation().equals("op1")) {
                fail("Incorrect activity unit 1");
            }

            if (!((RequestSent)result.get(1)).getOperation().equals("op2")) {
                fail("Incorrect activity unit 2");
            }

        } catch(Exception e) {
            e.printStackTrace();
            fail("Failed to access activity server: "+e);
        }
    }

    @Test
    public void storeAndQuery() {
        
        if (_activityStore == null) {
            fail("Activity Store has not been initialized");
        } else if (_activityStore instanceof MemActivityStore) {
            ((MemActivityStore)_activityStore).clear();
        } else {
            fail("Activity Store unexpected type");
        }
        
        try {

            java.util.List<ActivityType> result=_activityStore.query(
                            new QuerySpec().setFormat(MVEL_FORMAT).setExpression("true"));
            
            if (result == null) {
                fail("Initial query result is null");
            }
            
            if (result.size() != 0) {
                fail("Initial query result should be 0: "+result.size());
            }
            
            URL getUrl = new URL("http://localhost:8080/overlord-bam/activity/store");
            HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
            connection.setRequestMethod("POST");
            
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);
            connection.setRequestProperty("Content-Type",
                        "application/json");
    
            java.io.OutputStream os=connection.getOutputStream();
            
            ActivityUnit au1=new ActivityUnit();
            au1.setId("au1");
            
            RequestSent rs1=new RequestSent();
            rs1.setOperation("op1");
            au1.getActivityTypes().add(rs1);
            
            ActivityUnit au2=new ActivityUnit();
            au2.setId("au2");
            
            RequestSent rs2=new RequestSent();
            rs2.setOperation("op2");
            au2.getActivityTypes().add(rs2);
            
            java.util.List<ActivityUnit> aulist=new java.util.ArrayList<ActivityUnit>();
            aulist.add(au1);
            aulist.add(au2);
            
            byte[] aus=ActivityUtil.serializeActivityUnitList(aulist);
            
            os.write(aus);
            
            os.flush();
            os.close();
            
            java.io.InputStream is=connection.getInputStream();
            
            byte[] b=new byte[is.available()];
            
            is.read(b);
            
            is.close();
            
            System.out.println("RESULT="+new String(b));
            
            // Re-issue query
            result = _activityStore.query(
                    new QuerySpec().setFormat(MVEL_FORMAT).setExpression("true"));
            
            if (result == null) {
                fail("Follow-up query result is null");
            }
            
            if (result.size() != 2) {
                fail("Follow-up query result should have 2 items: "+result.size());
            }

        } catch(Exception e) {
            fail("Failed to access activity server: "+e);
        }
    }
}