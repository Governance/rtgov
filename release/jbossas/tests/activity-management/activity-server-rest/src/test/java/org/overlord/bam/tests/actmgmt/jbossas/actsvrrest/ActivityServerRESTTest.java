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
import org.codehaus.jackson.type.TypeReference;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.overlord.bam.activity.model.ActivityUnit;
import org.overlord.bam.activity.server.QuerySpec;
import org.overlord.bam.activity.store.mem.MemActivityStore;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class ActivityServerRESTTest {
    
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
        
        return ShrinkWrap.create(WebArchive.class, "TestActivityServer.war")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .setWebXML("web.xml")
            .addAsLibraries(
                    DependencyResolvers
                    .use(MavenDependencyResolver.class)
                    .artifacts("org.overlord.bam.activity-management:activity-server-jee:"+version,
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

            java.util.List<ActivityUnit> result=_activityStore.query(new QuerySpec());
            
            if (result == null) {
                fail("Initial query result is null");
            }
            
            if (result.size() != 0) {
                fail("Initial query result should be empty");
            }

            // Add two entries
            ActivityUnit au1=new ActivityUnit();
            au1.setId("au1");
            
            ActivityUnit au2=new ActivityUnit();
            au2.setId("au2");
            
            java.util.List<ActivityUnit> aulist=new java.util.ArrayList<ActivityUnit>();
            aulist.add(au1);
            aulist.add(au2);
            
            _activityStore.store(aulist);
            
            // Query via REST interface
            URL getUrl = new URL("http://localhost:8080/TestActivityServer/server/query");
            HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
            connection.setRequestMethod("POST");
    
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);
            connection.setRequestProperty("Content-Type",
                        "application/json");
    
            java.io.OutputStream os=connection.getOutputStream();
            
            QuerySpec qs=new QuerySpec();
            
            MAPPER.writeValue(os, qs);
            
            os.flush();
            os.close();
            
            java.io.InputStream is=connection.getInputStream();

            result = MAPPER.readValue(is, new TypeReference<java.util.List<ActivityUnit>>() {});
            
            System.out.println("RETRIEVED RESULTS="+result);
            
            is.close();
            
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

    
    @Test
    public void queryActivityUnitId() {
        
        if (_activityStore == null) {
            fail("Activity Store has not been initialized");
        } else if (_activityStore instanceof MemActivityStore) {
            ((MemActivityStore)_activityStore).clear();
        } else {
            fail("Activity Store unexpected type");
        }
        
        try {

            java.util.List<ActivityUnit> result=_activityStore.query(new QuerySpec());
            
            if (result == null) {
                fail("Initial query result is null");
            }
            
            if (result.size() != 0) {
                fail("Initial query result should be empty");
            }

            // Add two entries
            ActivityUnit au1=new ActivityUnit();
            au1.setId("au1");
            
            ActivityUnit au2=new ActivityUnit();
            au2.setId("au2");
            
            java.util.List<ActivityUnit> aulist=new java.util.ArrayList<ActivityUnit>();
            aulist.add(au1);
            aulist.add(au2);
            
            _activityStore.store(aulist);
            
            // Query via REST interface
            URL getUrl = new URL("http://localhost:8080/TestActivityServer/server/query");
            HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
            connection.setRequestMethod("POST");
    
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);
            connection.setRequestProperty("Content-Type",
                        "application/json");
    
            java.io.OutputStream os=connection.getOutputStream();
            
            QuerySpec qs=new QuerySpec().setId("au2");
            
            MAPPER.writeValue(os, qs);
            
            os.flush();
            os.close();
            
            java.io.InputStream is=connection.getInputStream();

            result = MAPPER.readValue(is, new TypeReference<java.util.List<ActivityUnit>>() {});
            
            System.out.println("RETRIEVED RESULTS="+result);
            
            is.close();
            
            if (result == null) {
                fail("Follow-up query result is null");
            }
            
            if (result.size() != 1) {
                fail("Follow-up query result should have 1 items: "+result.size());
            }
            
            if (!result.get(0).getId().equals("au2")) {
                fail("Incorrect activity unit");
            }

        } catch(Exception e) {
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

            java.util.List<ActivityUnit> result=_activityStore.query(new QuerySpec());
            
            if (result == null) {
                fail("Initial query result is null");
            }
            
            if (result.size() != 0) {
                fail("Initial query result should be 0: "+result.size());
            }
            
            URL getUrl = new URL("http://localhost:8080/TestActivityServer/server/store");
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
            
            ActivityUnit au2=new ActivityUnit();
            au2.setId("au2");
            
            java.util.List<ActivityUnit> aulist=new java.util.ArrayList<ActivityUnit>();
            aulist.add(au1);
            aulist.add(au2);
            
            MAPPER.writeValue(os, aulist);
            
            os.flush();
            os.close();
            
            java.io.InputStream is=connection.getInputStream();
            
            byte[] b=new byte[is.available()];
            
            is.read(b);
            
            is.close();
            
            System.out.println("RESULT="+new String(b));
            
            // Re-issue query
            result = _activityStore.query(new QuerySpec());
            
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