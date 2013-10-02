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
package org.overlord.rtgov.tests.actanal.jbossas.calltracerest;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.activity.model.Context.Type;
import org.overlord.rtgov.activity.model.bpm.ProcessCompleted;
import org.overlord.rtgov.activity.model.bpm.ProcessStarted;
import org.overlord.rtgov.activity.model.bpm.ProcessCompleted.Status;
import org.overlord.rtgov.activity.model.soa.RequestReceived;
import org.overlord.rtgov.activity.model.soa.RequestSent;
import org.overlord.rtgov.activity.model.soa.ResponseReceived;
import org.overlord.rtgov.activity.model.soa.ResponseSent;
import org.overlord.rtgov.call.trace.model.CallTrace;
import org.overlord.rtgov.call.trace.util.CallTraceUtil;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class CallTraceRESTTest {
    
    private static final String TRACE1="{\"tasks\":[{\"type\":\"Call\",\"operation\":\"op1\",\"component\":\"st1\",\"tasks\":" +
    		"[{\"type\":\"Task\"," +
            "\"properties\":{\"processType\":\"proc1\",\"instanceId\":\"456\",\"version\":\"1\"}," +
    		"\"description\":\"ProcessStarted instanceId=456 processType=proc1 version=1\"," +
    		"\"duration\":10,\"percentage\":15},{\"type\":\"Call\",\"operation\":\"op2\",\"component\":\"st2\"," +
    		"\"requestLatency\":7,\"responseLatency\":8,\"tasks\":[{\"type\":\"Task\"," +
    		"\"properties\":{\"processType\":\"proc2\",\"instanceId\":\"123\",\"version\":\"2\"}," +
    		"\"description\":\"ProcessStarted " +
    		"instanceId=123 processType=proc2 version=2\",\"duration\":11,\"percentage\":55},{\"type\":\"Task\"," +
    		"\"properties\":{\"instanceId\":\"123\"},"+
    		"\"description\":\"ProcessCompleted instanceId=123\",\"duration\":9,\"percentage\":45}],\"duration\":37," +
    		"\"percentage\":58},{\"type\":\"Task\"," +
    		"\"properties\":{\"instanceId\":\"456\"},"+
    		"\"description\":\"ProcessCompleted instanceId=456\",\"duration\":16," +
    		"\"percentage\":25}],\"duration\":88,\"percentage\":100}]}";
    
    @Inject
    org.overlord.rtgov.activity.server.ActivityStore _activityStore=null;
    
    private boolean _initialized=false;

    @Deployment
    public static WebArchive createDeployment() {
        String rtgovversion=System.getProperty("rtgov.version");
        String jacksonversion=System.getProperty("jackson.version");
        
        return ShrinkWrap.create(WebArchive.class, "overlord-rtgov.war")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .setWebXML("web.xml")
            .addAsLibraries(
                    Maven.resolver().resolve("org.overlord.rtgov.activity-management:activity-server-jee:"+rtgovversion).withoutTransitivity().asSingleFile(),
                    Maven.resolver().resolve("org.overlord.rtgov.activity-management:activity:"+rtgovversion).withoutTransitivity().asSingleFile(),
                    Maven.resolver().resolve("org.overlord.rtgov.activity-management:collector-activity-server:"+rtgovversion).withoutTransitivity().asSingleFile(),
                    Maven.resolver().resolve("org.overlord.rtgov.common:rtgov-common:"+rtgovversion).withoutTransitivity().asSingleFile(),
                    Maven.resolver().resolve("org.overlord.rtgov.integration:rtgov-jbossas:"+rtgovversion).withoutTransitivity().asSingleFile(),
                    Maven.resolver().resolve("org.overlord.rtgov.activity-management:activity-store-mem:"+rtgovversion).withoutTransitivity().asSingleFile(),
                    Maven.resolver().resolve("org.overlord.rtgov.activity-analysis:call-trace:"+rtgovversion).withoutTransitivity().asSingleFile(),
                    Maven.resolver().resolve("org.overlord.rtgov.content.services:call-trace-rests:"+rtgovversion).withoutTransitivity().asSingleFile(),
                    Maven.resolver().resolve("org.codehaus.jackson:jackson-core-asl:"+jacksonversion).withoutTransitivity().asSingleFile(),
                    Maven.resolver().resolve("org.codehaus.jackson:jackson-mapper-asl:"+jacksonversion).withoutTransitivity().asSingleFile()
            );
    }
    
    protected void initActivityStore() {
        
        if (!_initialized) {
            try {
                ActivityUnit au1=new ActivityUnit();
                au1.setId("au1");
               
                ActivityUnit au2=new ActivityUnit();
                au2.setId("au2");
               
                ActivityUnit au3=new ActivityUnit();
                au3.setId("au3");
               
                RequestReceived a1=new RequestReceived();
                a1.setServiceType("st1");
                a1.setOperation("op1");
                a1.setTimestamp(0);
                a1.setMessageId("m1");
                a1.getContext().add(new Context(Type.Conversation, "1"));
               
                au1.getActivityTypes().add(a1);
               
                ProcessStarted p1=new ProcessStarted();
                p1.setProcessType("proc1");
                p1.setVersion("1");
                p1.setInstanceId("456");
                p1.setTimestamp(10);
               
                au1.getActivityTypes().add(p1);
               
                RequestSent a2=new RequestSent();
                a2.setServiceType("st2");
                a2.setOperation("op2");
                a2.setMessageId("m2");
                a2.setTimestamp(30);
                a2.getContext().add(new Context(Type.Conversation, "1"));
               
                au1.getActivityTypes().add(a2);
               
                RequestReceived a3=new RequestReceived();
                a3.setServiceType("st2");
                a3.setOperation("op2");
                a3.setMessageId("m2");
                a3.setTimestamp(37);
                a3.getContext().add(new Context(Type.Conversation, "1"));
            
                au2.getActivityTypes().add(a3);
               
                ProcessStarted p2=new ProcessStarted();
                p2.setProcessType("proc2");
                p2.setVersion("2");
                p2.setInstanceId("123");
                p2.setTimestamp(48);
               
                au2.getActivityTypes().add(p2);
               
                ProcessCompleted p3=new ProcessCompleted();
                p3.setInstanceId("123");
                p3.setStatus(Status.Success);
                p3.setTimestamp(57);
               
                au3.getActivityTypes().add(p3);
    
                ResponseSent a4=new ResponseSent();
                a4.setServiceType("st2");
                a4.setOperation("op2");
                a4.setMessageId("m3");
                a4.setReplyToId("m2");
                a4.setTimestamp(59);
                a4.getContext().add(new Context(Type.Conversation, "1"));
            
                au3.getActivityTypes().add(a4);
               
                ResponseReceived a5=new ResponseReceived();
                a5.setServiceType("st2");
                a5.setOperation("op2");
                a5.setMessageId("m3");
                a5.setReplyToId("m2");
                a5.setTimestamp(67);
                a5.getContext().add(new Context(Type.Conversation, "1"));
            
                au1.getActivityTypes().add(a5);
               
                ProcessCompleted p4=new ProcessCompleted();
                p4.setInstanceId("456");
                p4.setStatus(Status.Fail);
                p4.setTimestamp(83);
    
                au1.getActivityTypes().add(p4);
               
                ResponseSent a6=new ResponseSent();
                a6.setServiceType("st1");
                a6.setOperation("op1");
                a6.setMessageId("m4");
                a6.setReplyToId("m1");
                a6.setTimestamp(88);
                a6.getContext().add(new Context(Type.Conversation, "1"));
            
                au1.getActivityTypes().add(a6);
               
                au1.init();
                au2.init();
                au3.init();
               
                java.util.List<ActivityUnit> activities=new java.util.ArrayList<ActivityUnit>();
                activities.add(au1);
                activities.add(au2);
                activities.add(au3);
               
                _activityStore.store(activities);

                _initialized = true;
                
            } catch(Exception e) {
                e.printStackTrace();
                fail("Failed to initialize activity store: "+e);
            }
        }
    }

    @Test
    public void getCallTraceByConversationId() {
        
        initActivityStore();
        
        try {
            // Query server
            URL getUrl = new URL("http://localhost:8080/overlord-rtgov/call/trace/instance?value=1");
            HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
            connection.setRequestMethod("GET");
            
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);
            connection.setRequestProperty("Content-Type",
                        "application/json");
            
            java.io.InputStream is=connection.getInputStream();
            
            byte[] b = new byte[is.available()];
            is.read(b);
            
            is.close();
            
            CallTrace ct=CallTraceUtil.deserializeCallTrace(b);
            
            compare(ct, "getCallTrace", TRACE1);

        } catch(Exception e) {
            e.printStackTrace();
            fail("Failed to get conversation based call trace: "+e);
        }
    }

    @Test
    public void getCallTraceByMessageId() {
        
        initActivityStore();
        
        try {
            // Query server
            URL getUrl = new URL("http://localhost:8080/overlord-rtgov/call/trace/instance?type=Message&value=m1");
            HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
            connection.setRequestMethod("GET");
            
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);
            connection.setRequestProperty("Content-Type",
                        "application/json");
            
            java.io.InputStream is=connection.getInputStream();
            
            byte[] b = new byte[is.available()];
            is.read(b);
            
            is.close();
            
            CallTrace ct=CallTraceUtil.deserializeCallTrace(b);
            
            compare(ct, "getCallTrace", TRACE1);

        } catch(Exception e) {
            e.printStackTrace();
            fail("Failed to get message based call trace: "+e);
        }
    }

    protected void compare(CallTrace ct, String testname, String trace) {
        
        try {
            byte[] b=CallTraceUtil.serializeCallTrace(ct);
            
            if (b == null) {
                fail("null returned");
            }
            
            System.out.println(testname+": "+new String(b));
            
            CallTrace node2=CallTraceUtil.deserializeCallTrace(trace.getBytes());
            
            byte[] b2=CallTraceUtil.serializeCallTrace(node2);            
            
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
}