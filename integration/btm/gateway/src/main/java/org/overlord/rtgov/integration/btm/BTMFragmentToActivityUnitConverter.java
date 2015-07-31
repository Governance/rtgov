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
import java.util.concurrent.atomic.AtomicInteger;

import org.hawkular.btm.api.model.btxn.BusinessTransaction;
import org.hawkular.btm.api.model.btxn.ContainerNode;
import org.hawkular.btm.api.model.btxn.Content;
import org.hawkular.btm.api.model.btxn.CorrelationIdentifier;
import org.hawkular.btm.api.model.btxn.Node;
import org.hawkular.btm.api.model.btxn.Service;
import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.activity.model.Context.Type;
import org.overlord.rtgov.activity.model.Origin;
import org.overlord.rtgov.activity.model.soa.RequestReceived;
import org.overlord.rtgov.activity.model.soa.RequestSent;
import org.overlord.rtgov.activity.model.soa.ResponseReceived;
import org.overlord.rtgov.activity.model.soa.ResponseSent;

/**
 * 
 * @author gbrown
 *
 */
public class BTMFragmentToActivityUnitConverter {

    private static AtomicInteger counter=new AtomicInteger();

    /**
     * Convert the business transactions to activity units.
     *
     * @param btxns The business txns
     * @return The activity units
     */
    public List<ActivityUnit> convert(List<BusinessTransaction> btxns) {
        List<ActivityUnit> ret=new ArrayList<ActivityUnit>();
        
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
        if (node instanceof Service) {
            processRequest((Service)node, reqId, au, btxn);
        }
        
        if (node instanceof ContainerNode) {
            processNodes(btxn, ((ContainerNode)node).getNodes(), au);
        }
        
        // Create post activity event
        if (node instanceof Service) {
            processResponse((Service)node, reqId, respId, au, btxn);
        }
    }
    
    protected void processResponse(Service service, String reqId, String respId, ActivityUnit au, BusinessTransaction btxn) {
        String intf=service.getDetails().get("interface");
        boolean f_internal=false;

        if (intf == null) {
            intf = service.getUri();
        }
        if (service.getDetails().containsKey("internal")) {
            f_internal = service.getDetails().get("internal").equalsIgnoreCase("true");
        }
        
        ResponseSent rs=new ResponseSent();            
        rs.setServiceType(service.getUri());
        rs.setOperation(service.getOperation());
        long completedTime=service.completedTime();
        rs.setTimestamp(completedTime);
        rs.setInternal(f_internal);
        rs.setInterface(intf);
        rs.setReplyToId(reqId);

        convertCorrelationInformation(service, rs, respId);

        if (service.getResponse() != null) {
            if (service.getResponse().getHeaders() != null
                    && !service.getResponse().getHeaders().isEmpty()) {
                convertHeaders(service, rs);
            }
            if (!service.getResponse().getContent().isEmpty()) {
                Content c=service.getResponse().getContent().values().iterator().next();
                rs.setContent(c.getValue());

                if (c.getType() != null) {
                    rs.setMessageType(c.getType());
                }
            }
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

        if (f_internal) {
            ResponseReceived rr=new ResponseReceived();                
            rr.setServiceType(service.getUri());
            rr.setOperation(service.getOperation());
            rr.setTimestamp(completedTime);
            rr.setInternal(f_internal);
            rr.setInterface(intf);
            rr.setReplyToId(reqId);

            convertCorrelationInformation(service, rr, respId);

            if (service.getResponse() != null) {
                if (service.getResponse().getHeaders() != null
                        && !service.getResponse().getHeaders().isEmpty()) {
                    convertHeaders(service, rr);
                }
                if (!service.getResponse().getContent().isEmpty()) {
                    Content c=service.getResponse().getContent().values().iterator().next();
                    rr.setContent(c.getValue());

                    if (c.getType() != null) {
                        rr.setMessageType(c.getType());
                    }
                }
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

    protected void processRequest(Service service, String reqId, ActivityUnit au, BusinessTransaction btxn) {
        String intf=service.getDetails().get("interface");
        boolean f_internal=false;

        if (intf == null) {
            intf = service.getUri();
        }
        if (service.getDetails().containsKey("internal")) {
            f_internal = service.getDetails().get("internal").equalsIgnoreCase("true");
        }
        
        if (f_internal) {
            RequestSent rs=new RequestSent();
            rs.setServiceType(service.getUri());
            rs.setOperation(service.getOperation());
            rs.setTimestamp(service.getStartTime());
            rs.setInternal(f_internal);
            rs.setInterface(intf);

            convertCorrelationInformation(service, rs, reqId);

            if (service.getRequest() != null) {
                if (service.getRequest().getHeaders() != null
                        && !service.getRequest().getHeaders().isEmpty()) {
                    convertHeaders(service, rs);
                }
                if (!service.getRequest().getContent().isEmpty()) {
                    Content c=service.getRequest().getContent().values().iterator().next();
                    rs.setContent(c.getValue());
                    if (c.getType() != null) {
                        rs.setMessageType(c.getType());
                    }
                }
            }
            rs.getProperties().putAll(btxn.getProperties());
            rs.setUnitId(au.getId());
            rs.setUnitIndex(au.getActivityTypes().size());
            au.getActivityTypes().add(rs);
        }

        RequestReceived rr=new RequestReceived();
        rr.setServiceType(service.getUri());
        rr.setOperation(service.getOperation());            
        rr.setTimestamp(service.getStartTime());
        rr.setInternal(f_internal);
        rr.setInterface(intf);

        convertCorrelationInformation(service, rr, reqId);

        if (service.getRequest() != null) {
            if (service.getRequest().getHeaders() != null
                    && !service.getRequest().getHeaders().isEmpty()) {
                convertHeaders(service, rr);
            }
            if (!service.getRequest().getContent().isEmpty()) {
                Content c=service.getRequest().getContent().values().iterator().next();
                rr.setContent(c.getValue());
                if (c.getType() != null) {
                    rr.setMessageType(c.getType());
                }
            }
        }
        rr.getProperties().putAll(btxn.getProperties());
        rr.setUnitId(au.getId());
        rr.setUnitIndex(au.getActivityTypes().size());
        au.getActivityTypes().add(rr);
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
        
        activity.getContext().add(new Context(Type.Message, mesgId));
    }
    
    protected void convertHeaders(Service service, ActivityType activity) {
        for (String key : service.getRequest().getHeaders().keySet()) {
            activity.getProperties().put(key, service.getRequest().getHeaders().get(key));
            activity.getProperties().put("_header_format_"+key, "text");
            
            if (key.equals("org.switchyard.exchangeGatewayName")) {
                String value=service.getRequest().getHeaders().get(key);
                String[] parts=value.split("_");
                if (parts.length >= 3) {
                    activity.getProperties().put("gateway", parts[2]);
                }
            }
        }
    }
}
