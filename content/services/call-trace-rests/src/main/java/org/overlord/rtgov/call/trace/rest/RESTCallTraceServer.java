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
package org.overlord.rtgov.call.trace.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.activity.server.ActivityServer;
import org.overlord.rtgov.call.trace.CallTraceService;
import org.overlord.rtgov.call.trace.model.Call;
import org.overlord.rtgov.call.trace.model.CallTrace;
import org.overlord.rtgov.call.trace.model.Task;
import org.overlord.rtgov.call.trace.model.TraceNode.Status;
import org.overlord.rtgov.call.trace.util.CallTraceUtil;
import org.overlord.rtgov.common.util.BeanResolverUtil;

/**
 * This class represents the RESTful interface to the call trace server.
 *
 */
@Path("/call/trace")
@ApplicationScoped
public class RESTCallTraceServer {

    private static final Logger LOG=Logger.getLogger(RESTCallTraceServer.class.getName());
    
    private CallTraceService _callTraceService=null;
    
    /**
     * This is the default constructor.
     */
    public RESTCallTraceServer() {
    }
    
    /**
     * This method initializes the call trace REST service.
     */
    @PostConstruct
    public void init() {
        // Only access CDI if service not set, to support both OSGi and CDI
        if (_callTraceService == null) {
            _callTraceService = BeanResolverUtil.getBean(CallTraceService.class);

            if (_callTraceService != null && _callTraceService.getActivityServer() == null) {
                _callTraceService.setActivityServer(BeanResolverUtil.getBean(ActivityServer.class));
            }
        }
    }
    
    /**
     * This method returns the call trace for the specified context.
     * 
     * @param type The context type
     * @param value The context value
     * @return The call trace for the context
     * @throws Exception Failed to obtain call trace
     */
    @GET
    @Path("/instance")
    @Produces("application/json")
    public String instance(@DefaultValue("Conversation") @QueryParam("type") String type,
                    @QueryParam("value") String value) throws Exception {
        String ret="";
        
        init();
        
        CallTrace ct=getCallTrace(type, value);
        
        if (ct != null) {
            byte[] b=CallTraceUtil.serializeCallTrace(ct);
            
            if (b != null) {
                ret = new String(b);
            }
        }
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Instance="+ret);        
        }

        return (ret);
    }
    
    /**
     * This method returns the call trace service.
     * 
     * @return The call trace service
     */
    public CallTraceService getCallTraceService() {
        return (_callTraceService);
    }
    
    /**
     * This method sets the call trace service.
     * 
     * @param cts The call trace service
     */
    public void setCallTraceService(CallTraceService cts) {
        _callTraceService = cts;
    }

    /**
     * This method returns the call trace associated with the supplied
     * context.
     * 
     * @param type The context type
     * @param value The context value
     * @return The call trace, or null if not found
     * @throws Exception Failed to get call trace
     */
    protected CallTrace getCallTrace(String type, String value) throws Exception {
        CallTrace ret=null;
        
        if (value.equals("test")) {
            ret = createTestCallTrace();
        } else {
            Context query=new Context();
            
            if (type != null) {
                query.setType(Context.Type.valueOf(type));
            }
            
            query.setValue(value);
            
            ret = _callTraceService.createCallTrace(query);
        }
        
        return (ret);
    }
    
    /**
     * This method returns a test call trace that can be returned
     * from the REST service.
     * 
     * @return The test call trace
     */
    protected CallTrace createTestCallTrace() {
        CallTrace ret=new CallTrace();
        
        Call c0=new Call();
        c0.setComponent("TestService1");
        c0.setInterface("intf1");
        c0.setOperation("op1");
        c0.setRequest("<op1/>");
        c0.setResponse("<op1/>");
        c0.setDuration(2000);
        c0.setStatus(Status.Warning);
        c0.getProperties().put("customer", "Acme Inc");
        c0.getProperties().put("trader", "Fred Bloggs");
        ret.getTasks().add(c0);

        Task t1=new Task();
        t1.setDescription("Assign var1");
        t1.setDuration(100);
        t1.setPercentage(5);
        t1.getProperties().put("name", "var1");
        c0.getTasks().add(t1);
        
        Task t2=new Task();
        t2.setDescription("Evaluate expr1");
        t2.setDuration(100);
        t2.setPercentage(5);
        t2.getProperties().put("expression", "a + b");
        c0.getTasks().add(t2);
        
        Call c3=new Call();
        c3.setComponent("TestService2");
        c3.setInterface("intf2");
        c3.setOperation("op2");
        c3.setRequest("<op2/>");
        c3.setResponse("<op2/>");
        c3.setDuration(700);
        c3.setPercentage(35);
        c3.setRequestLatency(10);
        c3.setResponseLatency(10);
        c3.getProperties().put("customer", "Acme Inc");
        c0.getTasks().add(c3);
        
        Call c4=new Call();
        c4.setComponent("TestService3");
        c4.setInterface("intf3");
        c4.setOperation("op3");
        c4.setRequest("<op3/>");
        c4.setResponse("<op3/>");
        c4.setDuration(700);
        c4.setPercentage(35);
        c4.setRequestLatency(10);
        c4.setResponseLatency(10);
        c4.setFault("TestFault");
        c4.setStatus(Status.Fail);
        c4.getProperties().put("trader", "Fred Bloggs");
        c0.getTasks().add(c4);
        
        Task t5=new Task();
        t5.setDescription("Store var1");
        t5.setDuration(100);
        t5.setPercentage(5);
        c0.getTasks().add(t5);
        
        Task t31=new Task();
        t31.setDescription("Store var31");
        t31.setDuration(680);
        t31.setPercentage(100);
        t31.getProperties().put("name", "var31");
        t31.getProperties().put("value", "xyz");
        c3.getTasks().add(t31);
        
        Task t41=new Task();
        t41.setDescription("Store var41");
        t41.setDuration(340);
        t41.setPercentage(50);
        t41.getProperties().put("name", "var41");
        t41.getProperties().put("value", "abc");
        c4.getTasks().add(t41);
        
        Task t42=new Task();
        t42.setDescription("Store var42");
        t42.setDuration(340);
        t42.setPercentage(50);
        t42.getProperties().put("name", "var42");
        t42.getProperties().put("value", "ghj");
        c4.getTasks().add(t42);
        
        return (ret);
    }
}
