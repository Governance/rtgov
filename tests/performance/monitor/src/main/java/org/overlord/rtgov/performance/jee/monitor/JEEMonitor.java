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
package org.overlord.rtgov.performance.jee.monitor;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.naming.InitialContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.overlord.rtgov.active.collection.ActiveChangeListener;
import org.overlord.rtgov.active.collection.ActiveCollectionManager;
import org.overlord.rtgov.active.collection.ActiveList;

/**
 * This is the custom event monitor that receives node notifications
 * from the EPN, and makes the events available via a REST API.
 *
 */
@Path("/monitor")
@ApplicationScoped
public class JEEMonitor implements ActiveChangeListener {

    private static final String RESPONSE_TIMES_PROCESSED = "ResponseTimesProcessed";

    private static final Logger LOG=Logger.getLogger(JEEMonitor.class.getName());
    
    private static final String ACM_MANAGER = "java:global/overlord-rtgov/ActiveCollectionManager";

    private ActiveCollectionManager _acmManager=null;
    private ActiveList _responseTimesProcessed=null;
    private long _testStart=0;
    private long _firstInsert=0;
    private long _lastInsert=0;
    
    /**
     * This is the default constructor.
     */
    public JEEMonitor() {
        
        try {
            InitialContext ctx=new InitialContext();
            
            _acmManager = (ActiveCollectionManager)ctx.lookup(ACM_MANAGER);

            _responseTimesProcessed = (ActiveList)
                        _acmManager.getActiveCollection(RESPONSE_TIMES_PROCESSED);
            _responseTimesProcessed.addActiveChangeListener(this);
            
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to initialize active collection manager", e);
        }

    }
    
    /**
     * This method starts the monitor.
     * 
     * @return The response
     */
    @GET
    @Path("/start")
    @Produces("application/json")
    public Response start() {
    	_testStart = System.currentTimeMillis();
    	
    	if (LOG.isLoggable(Level.FINE)) {
    		LOG.fine("PerformanceTest: Monitor started="+_testStart);
    	}
    	
    	return Response.status(200).entity("test started").build();
    }

    /**
     * This method returns the latency.
     * 
     * @return The latency
     */
    @GET
    @Path("/latency")
    @Produces("application/json")
    public long getLatency() {
    	if (_firstInsert == 0 || (System.currentTimeMillis()-_lastInsert) < 5000) {
    		return (0);
    	}
    	
    	if (LOG.isLoggable(Level.FINE)) {
    		LOG.fine("PerformanceTest: Monitor latency from start to first event="+(_firstInsert - _testStart));
    	}
    	
    	return (_firstInsert - _testStart);
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
    	if (System.currentTimeMillis()-_lastInsert < 5000) {
    		return (0);
    	}
    	
    	if (LOG.isLoggable(Level.FINE)) {
    		LOG.fine("PerformanceTest: Monitor duration from first to last event="+(_lastInsert - _firstInsert));
    	}
    	
    	return (_lastInsert - _firstInsert);
    }

    public void inserted(Object key, Object value) {
		_lastInsert = System.currentTimeMillis();
		
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("PerformanceTest: Inserted="+key+" value="+value);
        }
        
		if (_firstInsert == 0) {
			_firstInsert = _lastInsert;
			
	        if (LOG.isLoggable(Level.FINE)) {
	            LOG.fine("PerformanceTest: Monitor first event received="+_firstInsert);
	        }
		}
	}

	public void updated(Object key, Object value) {
		
	}

	public void removed(Object key, Object value) {
		
	}

}
