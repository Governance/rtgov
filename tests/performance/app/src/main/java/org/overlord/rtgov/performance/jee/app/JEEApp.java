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
package org.overlord.rtgov.performance.jee.app;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.overlord.rtgov.activity.model.soa.RequestReceived;
import org.overlord.rtgov.activity.model.soa.RequestSent;
import org.overlord.rtgov.activity.model.soa.ResponseReceived;
import org.overlord.rtgov.activity.model.soa.ResponseSent;
import org.overlord.rtgov.activity.collector.ActivityCollector;
import org.overlord.rtgov.activity.collector.ActivityCollectorAccessor;

/**
 * REST app for creating activity events related to a virtual
 * business transaction.
 *
 */
@Path("/app")
@ApplicationScoped
public class JEEApp {

    private static final Logger LOG=Logger.getLogger(JEEApp.class.getName());
    
    private ActivityCollector _activityCollector=null;
    
    private long _firstTxn=0;
    private long _lastTxn=0;

    /**
     * This is the default constructor.
     */
    public JEEApp() {
        
        _activityCollector = ActivityCollectorAccessor.getActivityCollector();
            
        if (_activityCollector == null) {
            LOG.severe("Failed to initialize activity collector");
        }
    }
    
    /**
     * This method creates a new business transaction.
     * 
     * @param id The id
     * @return The response
     */
    @GET
    @Path("/create")
    @Produces("application/json")
    public Response create(@QueryParam("id") int id) {
    	    	
        if (_firstTxn == 0) {
        	_firstTxn = System.currentTimeMillis();
        	
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("PerformanceTest: App first transaction created="+_firstTxn);
            }
        }
    	
    	_activityCollector.startScope();
    	
        RequestReceived recvd=new RequestReceived();
        
        recvd.setServiceType("Order");                
        recvd.setOperation("buy");
        recvd.setContent(""+id);
        recvd.setMessageType("order");
        recvd.setMessageId("a"+id);
        
        try {
            _activityCollector.record(recvd);
            
        	synchronized (this) {
        		wait(50);
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        }

        RequestSent sent=new RequestSent();
        
        sent.setServiceType("CreditCheck");   
        sent.setOperation("check");
        sent.setContent(""+id);
        sent.setMessageType("creditCheck");
        sent.setMessageId("b"+id);
        
        try {
            _activityCollector.record(sent);
            
        	synchronized (this) {
        		wait(5);
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        }

        RequestReceived recvd2=new RequestReceived();
        
        recvd2.setServiceType("CreditCheck");   
        recvd2.setOperation("check");
        recvd2.setContent(""+id);
        recvd2.setMessageType("creditCheck");
        recvd2.setMessageId("b"+id);
        
        try {
            _activityCollector.record(recvd2);

        	synchronized (this) {
        		wait(50);
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        }

        ResponseSent sent2=new ResponseSent();
        
        sent2.setServiceType("CreditCheck");                
        sent2.setOperation("check");
        sent2.setContent(""+id);
        sent2.setMessageType("creditCheckResp");
        sent2.setMessageId("c"+id);
        sent2.setReplyToId("b"+id);
        
        try {
            _activityCollector.record(sent2);
            
        	synchronized (this) {
        		wait(5);
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        }

        ResponseReceived recvd3=new ResponseReceived();
        
        recvd3.setServiceType("CreditCheck");                
        recvd3.setOperation("check");
        recvd3.setContent(""+id);
        recvd3.setMessageType("creditCheckResp");
        recvd3.setMessageId("c"+id);
        recvd3.setReplyToId("b"+id);
        
        try {
            _activityCollector.record(recvd3);
            
        	synchronized (this) {
        		wait(50);
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        }

        ResponseSent sent3=new ResponseSent();
        
        sent3.setServiceType("Order");                
        sent3.setOperation("buy");
        sent3.setContent(""+id);
        sent3.setMessageType("orderResp");
        sent3.setMessageId("d"+id);
        sent3.setReplyToId("a"+id);
        
        try {
            _activityCollector.record(sent3);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        _activityCollector.endScope();
        
        _lastTxn = System.currentTimeMillis();
        
    	return Response.status(200).entity("create txn id: " + id).build();
    }

    /**
     * This method returns the duration.
     * 
     * @return The duration
     */
    @GET
    @Path("/duration")
    @Produces("application/json")
    public long getDuration() {
    	if (LOG.isLoggable(Level.FINE)) {
    		LOG.fine("PerformanceTest: App duration between first and last transaction="+(_lastTxn - _firstTxn));
    	}
    	return (_lastTxn - _firstTxn);
    }

}
