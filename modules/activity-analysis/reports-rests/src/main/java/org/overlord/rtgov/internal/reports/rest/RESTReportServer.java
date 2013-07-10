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
package org.overlord.rtgov.internal.reports.rest;

import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.overlord.rtgov.reports.ReportManager;
import org.overlord.rtgov.reports.model.Report;
import org.overlord.rtgov.reports.util.ReportsUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

/**
 * This class represents the RESTful interface to the report server.
 *
 */
@Path("/report")
@ApplicationScoped
public class RESTReportServer {

    private static final Logger LOG=Logger.getLogger(RESTReportServer.class.getName());
    
    private ReportManager _reportManager=null;
    
    /**
     * This is the default constructor.
     */
    @SuppressWarnings("unchecked")
    public RESTReportServer() {
        
        try {
            // Need to obtain report manager directly, as inject does not
            // work for REST service, and RESTeasy/CDI integration did not
            // appear to work in AS7. Directly accessing the bean manager
            // should be portable.
            BeanManager bm=InitialContext.doLookup("java:comp/BeanManager");
            
            java.util.Set<Bean<?>> beans=bm.getBeans(ReportManager.class);
            
            for (Bean<?> b : beans) {                
                CreationalContext<Object> cc=new CreationalContext<Object>() {
                    public void push(Object arg0) {
                    }
                    public void release() {
                    }                   
                };
                
                _reportManager = (ReportManager)((Bean<Object>)b).create(cc);
                
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Report manager="+_reportManager+" for bean="+b);
                }
                
                if (_reportManager != null) {
                    break;
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                    "report-manager-rests.Messages").getString("REPORT-MANAGER-RESTS-1"), e);
        }
    }
    
    /**
     * This method sets the report manager.
     * 
     * @param rm The report manager
     */
    public void setReportManager(ReportManager rm) {
        LOG.info("Set Report Manager="+rm);
        _reportManager = rm;
    }
    
    /**
     * This method generates a report based on the supplied
     * query parameters.
     * 
     * @param info The URI info
     * @return The report
     * @throws Exception Failed to generate the report
     */
    @GET
    @Path("/generate")
    @Produces("application/json")
    public Response generate(@Context UriInfo info) throws Exception {        
        MultivaluedMap<String,String> params=info.getQueryParameters();
        
        if (params.containsKey("report")) {
            String resp="";
            java.util.Map<String, Object> props=new java.util.HashMap<String, Object>();
            
            String reportName=params.getFirst("report");
            
            // Copy over parameters, only passing lists where multiple values have been defined
            for (String key : params.keySet()) {
                java.util.List<String> val=params.get(key);
                
                if (val.size() > 1) {
                    props.put(key, val);
                } else if (val.size() == 1) {
                    props.put(key, val.get(0));
                }
            }
            
            // Create start and end date/time
            long start=buildDateTime("start", params);
            long end=buildDateTime("end", params);
            
            if (start > 0) {
                props.put("start", start);
            }
            
            if (end > 0) {
                props.put("end", end);
            }

            // Generate report
            Report report=_reportManager.generate(reportName, props);
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Generated report="+report);        
            }
            
            if (report != null) {
                byte[] b=ReportsUtil.serializeReport(report);
                
                if (b != null) {
                    resp = new String(b);
                }
            }

            return (Response.ok(resp).build());
        } else {
            return (Response.status(Status.BAD_REQUEST).build());
        }
    }
    
    /**
     * This method calculates the date/time based on supplied parameters.
     * 
     * @param prefix The prefix
     * @param params The parameters
     * @return The date/time or 0 if not found
     * @throws Exception Failed to build date/time
     */
    protected static long buildDateTime(String prefix, MultivaluedMap<String,String> params) throws Exception {
        long ret=0;
        
        // Check if date info is available
        if (params.containsKey(prefix+"Day") && params.containsKey(prefix+"Month")
                        && params.containsKey(prefix+"Year")) {
            java.util.Calendar cal=java.util.Calendar.getInstance();
            
            // Find timezone
            if (params.containsKey("timezone")) {           
                cal.setTimeZone(TimeZone.getTimeZone(params.getFirst("timezone")));
            }
            
            cal.set(java.util.Calendar.DAY_OF_MONTH, Integer.parseInt(params.getFirst(prefix+"Day")));
            cal.set(java.util.Calendar.MONTH, Integer.parseInt(params.getFirst(prefix+"Month"))-1);
            cal.set(java.util.Calendar.YEAR, Integer.parseInt(params.getFirst(prefix+"Year")));
            
            if (params.containsKey(prefix+"Hour")) {
                cal.set(java.util.Calendar.HOUR_OF_DAY, Integer.parseInt(params.getFirst(prefix+"Hour")));
            }
            
            if (params.containsKey(prefix+"Minute")) {
                cal.set(java.util.Calendar.MINUTE, Integer.parseInt(params.getFirst(prefix+"Minute")));                
            }
            
            if (params.containsKey(prefix+"Second")) {
                cal.set(java.util.Calendar.SECOND, Integer.parseInt(params.getFirst(prefix+"Second")));                
            }
            
            ret = cal.getTimeInMillis();
        }
        
        return (ret);
    }
    
}
