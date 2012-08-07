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
package org.overlord.bam.content.epn;

import java.io.Serializable;

import org.overlord.bam.analytics.service.MEPDefinition;
import org.overlord.bam.analytics.service.OperationDefinition;
import org.overlord.bam.analytics.service.RequestFaultDefinition;
import org.overlord.bam.analytics.service.ResponseTime;
import org.overlord.bam.analytics.service.ServiceDefinition;

/**
 * This class provides an implementation of the EventProcessor
 * interface, used to identify and split out SOA related events
 * for use by subsequent event processor nodes.
 *
 */
public class ServiceResponseTimeProcessor extends org.overlord.bam.epn.EventProcessor {

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Serializable process(String source, Serializable event,
            int retriesLeft) throws Exception {
        Serializable ret=null;
        
        if (event instanceof ServiceDefinition) {
            ret = new java.util.LinkedList<ResponseTime>();

            ServiceDefinition sd=(ServiceDefinition)event;
            
            for (int j=0; j < sd.getOperations().size(); j++) {
                processOperation((java.util.LinkedList<ResponseTime>)ret,
                             sd, sd.getOperations().get(j));
            }
            
            if (((java.util.LinkedList<Serializable>)ret).size() == 0) {
                ret = null;
            }
        }
        
        return (ret);
    }

    /**
     * This method processes the operation definition to extract the
     * response time information.
     * 
     * @param rts The response time list
     * @param sdef The service definition
     * @param opdef The operation definition
     */
    protected void processOperation(java.util.List<ResponseTime> rts,
            ServiceDefinition sdef, OperationDefinition opdef) {
        
        if (opdef.getRequestResponse() != null) {
            processMEP(rts, sdef, opdef, opdef.getRequestResponse());
        }
        
        for (int i=0; i < opdef.getRequestFaults().size(); i++) {
            processMEP(rts, sdef, opdef, opdef.getRequestFaults().get(i));
        }
    }
    
    /**
     * This method processes the MEP definition to extract the
     * response time information.
     * 
     * @param rts The response time list
     * @param sdef The service definition
     * @param opdef The operation definition
     * @param mep The MEP definition
     */
    protected void processMEP(java.util.List<ResponseTime> rts,
            ServiceDefinition sdef, OperationDefinition opdef, MEPDefinition mep) {
        
        ResponseTime rt=new ResponseTime();
        
        rt.setServiceType(sdef.getServiceType());
        rt.setOperation(opdef.getName());
        
        if (mep instanceof RequestFaultDefinition) {
            rt.setFault(((RequestFaultDefinition)mep).getFault());
        }
        
        rt.setAverage(mep.getMetrics().getAverage());
        rt.setMin(mep.getMetrics().getMin());
        rt.setMax(mep.getMetrics().getMax());
        
        rt.setTimestamp(System.currentTimeMillis());
        
        rts.add(rt);
    }
}
