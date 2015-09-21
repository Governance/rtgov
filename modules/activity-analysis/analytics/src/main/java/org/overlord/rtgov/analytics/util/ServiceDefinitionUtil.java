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
package org.overlord.rtgov.analytics.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.ActivityTypeId;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.activity.model.soa.RequestReceived;
import org.overlord.rtgov.activity.model.soa.RequestSent;
import org.overlord.rtgov.activity.model.soa.ResponseReceived;
import org.overlord.rtgov.activity.model.soa.ResponseSent;
import org.overlord.rtgov.activity.util.ActivityUtil;
import org.overlord.rtgov.analytics.service.InterfaceDefinition;
import org.overlord.rtgov.analytics.service.InvocationDefinition;
import org.overlord.rtgov.analytics.service.InvocationMetric;
import org.overlord.rtgov.analytics.service.MEPDefinition;
import org.overlord.rtgov.analytics.service.OperationDefinition;
import org.overlord.rtgov.analytics.service.RequestFaultDefinition;
import org.overlord.rtgov.analytics.service.RequestResponseDefinition;
import org.overlord.rtgov.analytics.service.ServiceDefinition;

/**
 * This class provides utility functions related to the service
 * definition.
 *
 */
public final class ServiceDefinitionUtil {
    
    private static final String PRINCIPAL = "principal";

    private static final Logger LOG=Logger.getLogger(ServiceDefinitionUtil.class.getName());

    private static final ObjectMapper MAPPER=new ObjectMapper();

    static {
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    /**
     * The default constructor.
     */
    private ServiceDefinitionUtil() {
    }

    /**
     * This method serializes a Service Definition into a JSON representation.
     * 
     * @param sdef The service definition
     * @return The JSON serialized representation
     * @throws Exception Failed to serialize
     */
    public static byte[] serializeServiceDefinition(ServiceDefinition sdef) throws Exception {
        byte[] ret=null;
        
        java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
        
        MAPPER.writeValue(baos, sdef);
        
        ret = baos.toByteArray();
        
        baos.close();
        
        return (ret);
    }

    /**
     * This method deserializes a Service Definition from a JSON representation.
     * 
     * @param sdef The JSON representation of the service definition
     * @return The service definition
     * @throws Exception Failed to deserialize
     */
    public static ServiceDefinition deserializeServiceDefinition(byte[] sdef) throws Exception {
        ServiceDefinition ret=null;
        
        java.io.ByteArrayInputStream bais=new java.io.ByteArrayInputStream(sdef);
        
        ret = MAPPER.readValue(bais, ServiceDefinition.class);
        
        bais.close();
        
        return (ret);
    }
    
    /**
     * This method processes the supplied activity unit to derive
     * service definition information.
     * 
     * @param actUnit The activity unit
     * @return The service definitions
     */
    public static java.util.Collection<ServiceDefinition> derive(ActivityUnit actUnit) {
        java.util.Map<String,ServiceDefinition> ret=
                        new java.util.HashMap<String,ServiceDefinition>();
        
        checkForServiceInvoked(ret, actUnit, 0, actUnit.getActivityTypes().size(), null);
        
        if (LOG.isLoggable(Level.FINEST)) {
            String au=null;
            
            try {
                au = new String(ActivityUtil.serializeActivityUnit(actUnit));
            } catch (Exception e) {
                LOG.log(Level.FINEST, "Failed to deserialize activity unit: "+actUnit, e);
            }
            
            LOG.finest("Derive service definitions: ActivityUnit="+au+" ServiceDefinitions="+ret);
        }
        
        return (ret.values());
    }

    /**
     * This method checks for the events associated with the service
     * being invoked.
     * 
     * @param sdefs The service definitions
     * @param actUnit The activity unit
     * @param from The 'from' index
     * @param to The 'to' index
     * @param idef The invocation definition
     */
    protected static void checkForServiceInvoked(java.util.Map<String,ServiceDefinition> sdefs,
                        ActivityUnit actUnit, int from, int to, InvocationDefinition idef) {
        
        // Scan the activity types for a received request
        for (int i=from; i < to; i++) {
            ActivityType at1=actUnit.getActivityTypes().get(i);
            
            if (at1 instanceof RequestReceived) {
                RequestReceived rqr=(RequestReceived)at1;
                
                if (idef != null && idef.getServiceType() == null) {
                    idef.setServiceType(rqr.getServiceType());
                }
                
                if (rqr.getMessageId() != null) {
                    // Locate the matching response sent activity
                    for (int j=i+1; j < to; j++) {
                        ActivityType at2=actUnit.getActivityTypes().get(j);
                        
                        if (at2 instanceof ResponseSent
                                && ((ResponseSent)at2).getReplyToId() != null
                                && ((ResponseSent)at2).getReplyToId().equals(
                                        rqr.getMessageId())) {
                            ResponseSent rps=(ResponseSent)at2;
                            
                            // Process the activities related to this
                            // matched interaction
                            MEPDefinition resp=processServiceInvoked(sdefs, actUnit, rqr, rps);
                            
                            // Check if any invocations are performed in the
                            // scope of this req/resp
                            if (resp != null) {
                                checkForExternalInvocations(sdefs, actUnit, resp, i+1, j);
                            }
                            
                            // Advance 'i' so only checks after the sent
                            // response
                            i = j;
                            
                            // Escape from this loop, as the response has
                            // been found
                            break;
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Check if there are any external service invocations during the scope of
     * the suppled MEP definition.
     * 
     * @param sdefs The service definitions
     * @param actUnit The activity unit
     * @param mep The MEP definition
     * @param from The 'from' index
     * @param to The 'to' index
     */
    protected static void checkForExternalInvocations(java.util.Map<String,ServiceDefinition> sdefs,
            ActivityUnit actUnit, MEPDefinition mep, int from, int to) {
        
        // Scan the activity types for a received request
        for (int i=from; i < to; i++) {
            ActivityType at1=actUnit.getActivityTypes().get(i);
            
            if (at1 instanceof RequestSent) {
                RequestSent rqs=(RequestSent)at1;
                
                if (rqs.getMessageId() != null) {
                    
                    // Locate the matching response received activity
                    for (int j=i+1; j < to; j++) {
                        ActivityType at2=actUnit.getActivityTypes().get(j);
                        
                        if (at2 instanceof ResponseReceived
                                && ((ResponseReceived)at2).getReplyToId() != null
                                && ((ResponseReceived)at2).getReplyToId().equals(
                                        rqs.getMessageId())) {
                            ResponseReceived rpr=(ResponseReceived)at2;
                            
                            // Process the activities related to this
                            // matched interaction
                            InvocationDefinition idef=processExternalInvocation(sdefs, mep, rqs, rpr);
                            
                            // Check if any invocations are performed in the
                            // scope of this req/resp
                            checkForServiceInvoked(sdefs, actUnit, i+1, j, idef);
                            
                            // Advance 'i' so only checks after the received
                            // response
                            i = j;
                            
                            // Escape from this loop, as the response has
                            // been found
                            break;
                        }
                    }
                }
            }
        }
    }
    
    /**
     * This method processes the service invocation.
     * 
     * @param sdefs The service definitions
     * @param actUnit The activity unit
     * @param rqr The request received event
     * @param rps The response sent event
     * @return The response definition associated with the req/resp
     */
    protected static MEPDefinition processServiceInvoked(java.util.Map<String,ServiceDefinition> sdefs,
                ActivityUnit actUnit, RequestReceived rqr, ResponseSent rps) {
        MEPDefinition ret=null;
        
        if (rqr.getServiceType() == null) {
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Can't process service invocation with missing service type and interface '"
                            +rqr.getInterface()+"'");
            }
            return null;
        }
        
        if (rqr.getInterface() == null) {
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Can't process service invocation for service type '"
                            +rqr.getServiceType()+"' due to missing interface");
            }
            return null;
        }
        
        // Get service definition associated with the service type
        ServiceDefinition sd=sdefs.get(rqr.getServiceType());
        
        // If not found, then create
        if (sd == null) {
            sd = new ServiceDefinition();
            sd.setServiceType(rqr.getServiceType());
            sd.setInternal(rqr.getInternal() || rps.getInternal()); // Mark service as internal if either request or response are
            sdefs.put(rqr.getServiceType(), sd);
        }
        
        InterfaceDefinition id=sd.getInterface(rqr.getInterface());
        
        if (id == null) {
            id = new InterfaceDefinition();
            id.setInterface(rqr.getInterface());
            sd.getInterfaces().add(id);
        }
        
        OperationDefinition op=id.getOperation(rqr.getOperation());
        
        if (op == null) {
            op = new OperationDefinition();
            op.setName(rqr.getOperation());
            id.getOperations().add(op);
        }
        
        // Check if normal or fault response
        InvocationMetric metrics=null;
        
        if (rps.getFault() == null || rps.getFault().trim().length() == 0) {
            RequestResponseDefinition nrd=op.getRequestResponse();
            
            if (nrd == null) {
                nrd = new RequestResponseDefinition();
                op.setRequestResponse(nrd);
                
                // Set the request and response ids
                nrd.setRequestId(ActivityTypeId.createId(rqr));
                nrd.setResponseId(ActivityTypeId.createId(rps));                
            }
            
            metrics = nrd.getMetrics();
            
            ret = nrd;
        } else {
            RequestFaultDefinition frd=op.getRequestFault(rps.getFault());
            
            if (frd == null) {
                frd = new RequestFaultDefinition();
                frd.setFault(rps.getFault());
                
                // Set the request and response ids
                frd.setRequestId(ActivityTypeId.createId(rqr));
                frd.setResponseId(ActivityTypeId.createId(rps));
                
                op.getRequestFaults().add(frd);
            }
            
            metrics = frd.getMetrics();
            
            metrics.setFaults(metrics.getFaults()+1);
            
            ret = frd;
        }
        
        // Copy the properties
        if (ret != null) {
            ret.getProperties().putAll(rqr.getProperties());
            ret.getProperties().putAll(rps.getProperties());
            
            // Check if principal defined for either activity
            if (!ret.getProperties().containsKey(PRINCIPAL)) {
                if (rqr.getPrincipal() != null) {
                    ret.getProperties().put(PRINCIPAL, rqr.getPrincipal());
                } else if (rps.getPrincipal() != null) {
                    ret.getProperties().put(PRINCIPAL, rps.getPrincipal());
                }
            }
            
            // Specify the origin information
            if (actUnit != null && actUnit.getOrigin() != null) {
                ret.getProperties().put("host", actUnit.getOrigin().getHost());
                ret.getProperties().put("node", actUnit.getOrigin().getNode());
            }
        }

        // Calculate stats
        long duration=rps.getTimestamp()-rqr.getTimestamp();
        
        metrics.setAverage(((metrics.getAverage() * metrics.getCount())+duration)
                            / (metrics.getCount()+1));
        
        if (metrics.getMin() == 0 || duration < metrics.getMin()) {
            metrics.setMin(duration);
        }

        if (duration > metrics.getMax()) {
            metrics.setMax(duration);
        }

        metrics.setCount(metrics.getCount()+1);
        
        // Store context details
        sd.getContext().addAll(rqr.getContext());
        
        java.util.Iterator<Context> iter=rps.getContext().iterator();
        while (iter.hasNext()) {
            Context c=iter.next();
            
            if (!sd.getContext().contains(c)) {
                sd.getContext().add(c);
            }
        }
        
        return (ret);
    }
    
    /**
     * This method processes the external invocation and associates the
     * details with the supplied MEP definition.
     * 
     * @param sdefs The service definitions
     * @param call The MEP definition
     * @param rqs The request
     * @param rpr The response
     * @return The invocation definition
     */
    protected static InvocationDefinition processExternalInvocation(java.util.Map<String,ServiceDefinition> sdefs,
                MEPDefinition call, RequestSent rqs, ResponseReceived rpr) {
        
        InvocationDefinition idef=call.getInvocation(rqs.getInterface(),
                        rqs.getOperation(), rpr.getFault());
        
        if (idef == null) {
            idef = new InvocationDefinition();
            idef.setInterface(rqs.getInterface());
            idef.setOperation(rqs.getOperation());
            idef.setFault(rpr.getFault());
            idef.setInternal(rqs.getInternal() || rpr.getInternal()); // Mark service as internal if either request or response are
            
            idef.getProperties().putAll(rqs.getProperties());
            idef.getProperties().putAll(rpr.getProperties());

            call.getInvocations().add(idef);
        }
        
        InvocationMetric metrics=idef.getMetrics();

        long duration=rpr.getTimestamp()-rqs.getTimestamp();
        
        metrics.setAverage(((metrics.getAverage() * metrics.getCount())+duration)
                            / (metrics.getCount()+1));
        
        if (metrics.getMin() == 0 || duration < metrics.getMin()) {
            metrics.setMin(duration);
        }

        if (duration > metrics.getMax()) {
            metrics.setMax(duration);
        }

        metrics.setCount(metrics.getCount()+1);
        
        if (idef.getFault() != null) {
            metrics.setFaults(metrics.getFaults()+1);
        }
        
        return (idef);
    }
    
    /**
     * This method merges the supplied service definition snapshots.
     * 
     * @param snapshots The snapshots to merge
     * @return The merged service definitions
     */
    public static java.util.Map<String,ServiceDefinition> mergeSnapshots(
                    java.util.List<java.util.Map<String,ServiceDefinition>> snapshots) {
        return (mergeSnapshots(snapshots, false));
    }
    
    /**
     * This method merges the supplied service definition snapshots.
     * 
     * @param snapshots The snapshots to merge
     * @param retainContexts Whether to retain and merge context information
     * @return The merged service definitions
     */
    public static java.util.Map<String,ServiceDefinition> mergeSnapshots(
                    java.util.List<java.util.Map<String,ServiceDefinition>> snapshots,
                    boolean retainContexts) {
        java.util.Map<String,ServiceDefinition> ret=
                        new java.util.HashMap<String, ServiceDefinition>();
        
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("MERGE: "+snapshots);
        }
        
        java.util.Set<String> keys=new java.util.HashSet<String>();
        
        // Build key set
        for (int i=0; i < snapshots.size(); i++) {
            java.util.Map<String,ServiceDefinition> sds=snapshots.get(i);
            keys.addAll(sds.keySet());
        }
        
        for (String key : keys) {
            ServiceDefinition sd=new ServiceDefinition();
            sd.setServiceType(key);
            
            for (int i=0; i < snapshots.size(); i++) {
                java.util.Map<String,ServiceDefinition> sds=snapshots.get(i);
                if (sds.containsKey(key)) {
                    try {
                        sd.merge(sds.get(key), retainContexts);
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                                "analytics.Messages").getString("ANALYTICS-1"), e);
                    }
                }
            }
            
            ret.put(key, sd);
        }
        
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("MERGED: "+ret);
        }
        
        return (ret);
    }
}
