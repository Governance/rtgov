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
package org.overlord.bam.call.trace.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.overlord.bam.activity.server.ActivityServer;
import org.overlord.bam.call.trace.CallTraceProcessor;
import org.overlord.bam.call.trace.model.Call;
import org.overlord.bam.call.trace.model.CallTrace;
import org.overlord.bam.call.trace.model.Task;
import org.overlord.bam.call.trace.model.TraceNode.Status;
import org.overlord.bam.call.trace.util.CallTraceUtil;

/**
 * This class represents the RESTful interface to the call trace server.
 *
 */
@Path("/call/trace")
@ApplicationScoped
public class RESTCallTraceServer {

    private static final Logger LOG=Logger.getLogger(RESTCallTraceServer.class.getName());
    
    private CallTraceProcessor _processor=new CallTraceProcessor();
    
    //@javax.inject.Inject
    private ActivityServer _activityServer=null;

    /**
     * This is the default constructor.
     */
    @SuppressWarnings("unchecked")
    public RESTCallTraceServer() {
        
        try {
            // Need to obtain activity server directly, as inject does not
            // work for REST service, and RESTeasy/CDI integration did not
            // appear to work in AS7. Directly accessing the bean manager
            // should be portable.
            BeanManager bm=InitialContext.doLookup("java:comp/BeanManager");
            
            java.util.Set<Bean<?>> beans=bm.getBeans(ActivityServer.class);
            
            for (Bean<?> b : beans) {                
                CreationalContext<Object> cc=new CreationalContext<Object>() {
                    public void push(Object arg0) {
                    }
                    public void release() {
                    }                   
                };
                
                _activityServer = (ActivityServer)((Bean<Object>)b).create(cc);
                
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Activity server="+_activityServer+" for bean="+b);
                }
                
                if (_activityServer != null) {
                    break;
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                    "call-trace-rests.Messages").getString("CALL-TRACE-RESTS-1"), e);
        }
        
        _processor.setActivityServer(_activityServer);
    }
    
    /**
     * This method returns the call trace for the specified identifier.
     * 
     * @param identifier The identifier
     * @return The call trace for the identifier
     * @throws Exception Failed to obtain call trace
     */
    @GET
    @Path("/instance")
    @Produces("image/svg+xml")
    public String instance(@QueryParam("identifier") String identifier) throws Exception {
        String ret="";
        
        CallTrace ct=getCallTrace(identifier);
        
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
     * This method returns the call trace associated with the supplied
     * correlation.
     * 
     * @param correlation The correlation value
     * @return The call trace, or null if not found
     * @throws Exception Failed to get call trace
     */
    protected CallTrace getCallTrace(String correlation) throws Exception {
        CallTrace ret=null;
        
        if (correlation.equals("test")) {
            ret = createTestCallTrace();
        } else {
            ret = _processor.createCallTrace(correlation);
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
