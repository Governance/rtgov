/*
 * 2012-5 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.overlord.rtgov.integration.btm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hawkular.btm.api.model.btxn.BusinessTransaction;
import org.hawkular.btm.api.model.btxn.Consumer;
import org.hawkular.btm.api.model.btxn.ContainerNode;
import org.hawkular.btm.api.model.btxn.Content;
import org.hawkular.btm.api.model.btxn.CorrelationIdentifier;
import org.hawkular.btm.api.model.btxn.InteractionNode;
import org.hawkular.btm.api.model.btxn.Node;
import org.hawkular.btm.api.model.btxn.Producer;
import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.activity.model.Context.Type;
import org.overlord.rtgov.activity.model.Origin;
import org.overlord.rtgov.activity.model.soa.RequestReceived;
import org.overlord.rtgov.activity.model.soa.RequestSent;
import org.overlord.rtgov.activity.model.soa.ResponseReceived;
import org.overlord.rtgov.activity.model.soa.ResponseSent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author gbrown
 *
 */
public class BTMFragmentToActivityUnitConverter {

    private static final String BTM_SERVICE_TYPE = "btm_serviceType";
    private static final String BTM_SERVICE_OPERATION = "btm_serviceOperation";

    private static AtomicInteger counter=new AtomicInteger();

    private static ObjectMapper mapper=new ObjectMapper();
    
    private static final Logger LOG=Logger.getLogger(BTMFragmentToActivityUnitConverter.class.getName());
    
    /**
     * Convert the business transactions to activity units.
     *
     * @param btxns The business txns
     * @return The activity units
     */
    public List<ActivityUnit> convert(List<BusinessTransaction> btxns) {
        List<ActivityUnit> ret=new ArrayList<ActivityUnit>();
        
        if (LOG.isLoggable(Level.FINEST)) {
            try {
                LOG.finest("Convert business transactions: btxns="+mapper.writeValueAsString(btxns));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        for (BusinessTransaction btxn : btxns) {
            ActivityUnit au=new ActivityUnit();
            au.setId(btxn.getId());
            
            processNodes(btxn, btxn.getNodes(), au);
            
            if (!au.getActivityTypes().isEmpty()) {
                
                Origin origin = new Origin();
                origin.setHost(btxn.getHostName());
                au.setOrigin(origin);

                ret.add(au);
            }
        }

        if (LOG.isLoggable(Level.FINEST)) {
            try {
                LOG.finest("To activity units: ret="+mapper.writeValueAsString(ret));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        return ret;
    }
    
    protected void processNodes(BusinessTransaction btxn, List<Node> nodes, ActivityUnit au) {
        for (int i=0; i < nodes.size(); i++) {
            processNode(btxn, nodes.get(i), au);
        }
    }
    
    protected void processNode(BusinessTransaction btxn, Node node, ActivityUnit au) {
        int index=counter.getAndIncrement();
        String reqId=btxn.getId()+"_req"+index;
        String respId=btxn.getId()+"_resp"+index;

        // Create pre activity event
        if ((node instanceof Consumer || node instanceof Producer)
                && node.getDetails().containsKey(BTM_SERVICE_TYPE)) {
            processRequest((InteractionNode)node, reqId, au, btxn);
        }
        
        if (node instanceof ContainerNode) {
            processNodes(btxn, ((ContainerNode)node).getNodes(), au);
        }
        
        // Create post activity event
        if ((node instanceof Consumer || node instanceof Producer)
                && node.getDetails().containsKey(BTM_SERVICE_TYPE)) {
            processResponse((InteractionNode)node, reqId, respId, au, btxn);
        }
    }
    
    protected void processRequest(InteractionNode service, String reqId, ActivityUnit au, BusinessTransaction btxn) {
        String intf=service.getDetails().get("interface");

        if (intf == null) {
            intf = service.getUri();
        }

        long startTime=btxn.getStartTime();
        
        long topNodeBaseTime=btxn.getNodes().get(0).getBaseTime();
        long diffms=TimeUnit.MILLISECONDS.convert(service.getBaseTime() - topNodeBaseTime, TimeUnit.NANOSECONDS);
        
        if (service instanceof Producer) {
            RequestSent rs=new RequestSent();
            
            rs.setServiceType(service.getDetails().get(BTM_SERVICE_TYPE));
            rs.setOperation(service.getDetails().get(BTM_SERVICE_OPERATION));
            
            rs.setTimestamp(startTime + diffms);
            rs.setInterface(intf);

            convertCorrelationInformation(service, rs, reqId);

            if (service.getIn() != null) {
                if (service.getIn().getHeaders() != null
                        && !service.getIn().getHeaders().isEmpty()) {
                    convertHeaders(service, rs);
                }
                if (!service.getIn().getContent().isEmpty()) {
                    Content c=service.getIn().getContent().values().iterator().next();
                    rs.setContent(c.getValue());
                    if (c.getType() != null) {
                        rs.setMessageType(c.getType());
                    }
                }
            }
                        
            if (service instanceof Producer) {
                rs.getProperties().put("gateway", ((Producer)service).getEndpointType());
            }

            rs.getProperties().putAll(btxn.getProperties());
            rs.setUnitId(au.getId());
            rs.setUnitIndex(au.getActivityTypes().size());
            au.getActivityTypes().add(rs);
        }

        if (service instanceof Consumer) {
            RequestReceived rr=new RequestReceived();
    
            rr.setServiceType(service.getDetails().get(BTM_SERVICE_TYPE));
            rr.setOperation(service.getDetails().get(BTM_SERVICE_OPERATION));
    
            rr.setTimestamp(startTime + diffms);
            rr.setInterface(intf);
    
            convertCorrelationInformation(service, rr, reqId);
    
            if (service.getIn() != null) {
                if (service.getIn().getHeaders() != null
                        && !service.getIn().getHeaders().isEmpty()) {
                    convertHeaders(service, rr);
                }
                if (!service.getIn().getContent().isEmpty()) {
                    Content c=service.getIn().getContent().values().iterator().next();
                    rr.setContent(c.getValue());
                    if (c.getType() != null) {
                        rr.setMessageType(c.getType());
                    }
                }
            }

            if (service instanceof Consumer) {
                rr.getProperties().put("gateway", ((Consumer)service).getEndpointType());
            }

            rr.getProperties().putAll(btxn.getProperties());
            rr.setUnitId(au.getId());
            rr.setUnitIndex(au.getActivityTypes().size());
            au.getActivityTypes().add(rr);
        }
    }
    
    protected void processResponse(InteractionNode service, String reqId, String respId, ActivityUnit au, BusinessTransaction btxn) {
        String intf=service.getDetails().get("interface");

        if (intf == null) {
            intf = service.getUri();
        }
        
        long startTime=btxn.getStartTime();
        
        long topNodeBaseTime=btxn.getNodes().get(0).getBaseTime();
        long diffms=TimeUnit.MILLISECONDS.convert((service.getBaseTime() + service.getDuration() - topNodeBaseTime),
                                    TimeUnit.NANOSECONDS);
        
        if (service instanceof Consumer) {
            ResponseSent rs=new ResponseSent();            
    
            rs.setServiceType(service.getDetails().get(BTM_SERVICE_TYPE));
            rs.setOperation(service.getDetails().get(BTM_SERVICE_OPERATION));
    
            rs.setTimestamp(startTime + diffms);
            rs.setInterface(intf);
            rs.setReplyToId(reqId);
    
            convertCorrelationInformation(service, rs, respId);
    
            if (service.getOut() != null) {
                if (service.getOut().getHeaders() != null
                        && !service.getOut().getHeaders().isEmpty()) {
                    convertHeaders(service, rs);
                }
                if (!service.getOut().getContent().isEmpty()) {
                    Content c=service.getOut().getContent().values().iterator().next();
                    rs.setContent(c.getValue());
    
                    if (c.getType() != null) {
                        rs.setMessageType(c.getType());
                    }
                }
            }
    
            if (service instanceof Consumer) {
                rs.getProperties().put("gateway", ((Consumer)service).getEndpointType());
            }

            rs.getProperties().putAll(btxn.getProperties());
            rs.setUnitId(au.getId());
            rs.setUnitIndex(au.getActivityTypes().size());
            
            if (service.getFault() != null) {
                rs.setFault(service.getFault());
                if (service.getFaultDescription() != null) {
                    rs.setContent(service.getFaultDescription());
                }
            }
    
            au.getActivityTypes().add(rs);
        }

        if (service instanceof Producer) {
            ResponseReceived rr=new ResponseReceived();                
            
            rr.setServiceType(service.getDetails().get(BTM_SERVICE_TYPE));
            rr.setOperation(service.getDetails().get(BTM_SERVICE_OPERATION));
            
            rr.setTimestamp(startTime + diffms);
            rr.setInterface(intf);
            rr.setReplyToId(reqId);

            convertCorrelationInformation(service, rr, respId);

            if (service.getOut() != null) {
                if (service.getOut().getHeaders() != null
                        && !service.getOut().getHeaders().isEmpty()) {
                    convertHeaders(service, rr);
                }
                if (!service.getOut().getContent().isEmpty()) {
                    Content c=service.getOut().getContent().values().iterator().next();
                    rr.setContent(c.getValue());

                    if (c.getType() != null) {
                        rr.setMessageType(c.getType());
                    }
                }
            }

            if (service instanceof Producer) {
                rr.getProperties().put("gateway", ((Producer)service).getEndpointType());
            }

            rr.getProperties().putAll(btxn.getProperties());
            rr.setUnitId(au.getId());
            rr.setUnitIndex(au.getActivityTypes().size());

            if (service.getFault() != null) {
                rr.setFault(service.getFault());
                if (service.getFaultDescription() != null) {
                    rr.setContent(service.getFaultDescription());
                }
            }

            au.getActivityTypes().add(rr);
        }
    }

    /**
     * This method converts the BTM correlation ids into activity context information.
     *
     * @param node The node
     * @param activity The activity
     * @param mesgId The message id
     */
    protected void convertCorrelationInformation(Node node, ActivityType activity,
                                String mesgId) {
        
        activity.getContext().add(new Context(Type.Message, mesgId));

        for (CorrelationIdentifier ci : node.getCorrelationIds()) {
            Context c=new Context();
            switch (ci.getScope()) {
            case Global:
                c.setType(Type.Conversation);
                break;
            case Interaction:
                c.setType(Type.Message);
                break;
            case Local:
                c.setType(Type.Endpoint);
                break;
            default:
                break;
            }
            c.setValue(ci.getValue());
            
            activity.getContext().add(c);
        }
    }
    
    protected void convertHeaders(InteractionNode service, ActivityType activity) {
        for (String key : service.getIn().getHeaders().keySet()) {
            activity.getProperties().put(key, service.getIn().getHeaders().get(key));
            activity.getProperties().put("_header_format_"+key, "text");
        }
    }
}
