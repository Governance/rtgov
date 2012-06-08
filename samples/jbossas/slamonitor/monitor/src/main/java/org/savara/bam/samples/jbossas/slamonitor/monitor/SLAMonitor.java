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
package org.savara.bam.samples.jbossas.slamonitor.monitor;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.naming.InitialContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.savara.bam.active.collection.ActiveCollectionManager;
import org.savara.bam.active.collection.ActiveList;
import org.savara.bam.analytics.service.ResponseTime;

/**
 * This is the custom event monitor that receives node notifications
 * from the EPN, and makes the events available via a REST API.
 *
 */
@Path("/monitor")
@ApplicationScoped
public class SLAMonitor {

    private static final String SERVICE_RESPONSE_TIME = "ServiceResponseTime";

    private static final Logger LOG=Logger.getLogger(SLAMonitor.class.getName());
    
    private static final String ACM_MANAGER = "java:global/overlord-bam/ActiveCollectionManager";

    private ActiveCollectionManager _acmManager=null;
    private ActiveList _serviceResponseTime=null;
    
    /**
     * This is the default constructor.
     */
    public SLAMonitor() {
        
        try {
            InitialContext ctx=new InitialContext();
            
            _acmManager = (ActiveCollectionManager)ctx.lookup(ACM_MANAGER);

            _serviceResponseTime = (ActiveList)
                        _acmManager.getActiveCollection(SERVICE_RESPONSE_TIME);
            
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to initialize active collection manager", e);
        }

    }
    
    /**
     * This method returns the list of response times.
     * 
     * @return The response times
     */
    @GET
    @Path("/responseTimes")
    @Produces("application/json")
    public java.util.List<ResponseTime> getResponseTimes() {
        java.util.List<ResponseTime> ret=new java.util.ArrayList<ResponseTime>();

        for (Object obj : _serviceResponseTime) {
            if (obj instanceof ResponseTime) {
                ret.add((ResponseTime)obj);
            }
        }
        
        return (ret);
    }

}
