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
package org.overlord.rtgov.service.dependency.layout;

import java.util.Collections;
import java.util.Comparator;

import org.overlord.rtgov.service.dependency.OperationNode;
import org.overlord.rtgov.service.dependency.ServiceGraph;
import org.overlord.rtgov.service.dependency.ServiceNode;
import org.overlord.rtgov.service.dependency.UsageLink;

/**
 * This class represents the default service graph layout
 * algorithm implementation.
 *
 */
public class ServiceGraphLayoutImpl implements ServiceGraphLayout {
    
    /**
     * This definition represents the width of an operation node.
     */
    protected static final int OPERATION_WIDTH=160;
    
    /**
     * This definition represents the height of an operation node.
     */
    protected static final int OPERATION_HEIGHT=20;
    
    /**
     * This definition represents the padding gap around the service inside.
     */
    protected static final int SERVICE_BORDER_PADDING=10;
    
    /**
     * This definition represents the width of a service node.
     */
    protected static final int SERVICE_WIDTH=OPERATION_WIDTH+(2 * SERVICE_BORDER_PADDING);
    
    /**
     * This definition represents the padding for the service node header.
     */
    protected static final int SERVICE_HEADER_PADDING=20;
    
    /**
     * This definition represents the initial horizontal padding for the
     * root service nodes.
     */
    protected static final int SERVICE_INITIAL_HORIZONTAL_PADDING=50;
    
    /**
     * This definition represents the horizontal padding between service nodes.
     */
    protected static final int SERVICE_HORIZONTAL_PADDING=200;
    
    /**
     * This definition represents the vertical padding between service nodes.
     */
    protected static final int SERVICE_VERTICAL_PADDING=50;
    
    private int _height=0;
    private int _width=0;
    
    /**
     * {@inheritDoc}
     */
    public void layout(ServiceGraph sg) {
        
        // Find initial service nodes
        java.util.List<ServiceNode> initialNodes=new java.util.ArrayList<ServiceNode>();
        
        for (ServiceNode sn : sg.getServiceNodes()) {
            if (sn.getProperties().get(ServiceNode.INITIAL_NODE) == Boolean.TRUE) {
                initialNodes.add(sn);
            }
        }
        
        // Sort initial node list
        Collections.sort(initialNodes, new Comparator<ServiceNode>() {
            public int compare(ServiceNode o1, ServiceNode o2) {
                // TODO: May need to sort based on interface's localpart, if a fully qualified name
                return (o1.getService().getServiceType().compareTo(o2.getService().getServiceType()));
            }
        });
        
        // Layout service nodes
        for (ServiceNode sn : initialNodes) {
            layoutService(sg, sn, SERVICE_INITIAL_HORIZONTAL_PADDING,
                    _height+SERVICE_VERTICAL_PADDING);
        }
        
        // Record the overall height and width
        sg.getProperties().put(ServiceGraphLayout.HEIGHT, _height);
        sg.getProperties().put(ServiceGraphLayout.WIDTH, _width);
    }
    
    /**
     * This method determines whether the supplied service node
     * requires layout.
     * 
     * @param sn The service node
     * @return Whether the service node requires layout
     */
    protected boolean requiresLayout(ServiceNode sn) {
        return (sn.getProperties().get(ServiceGraphLayout.X_POSITION) == null);
    }
    
    /**
     * This method performs the layout of the supplied service
     * node.
     * 
     * @param sg The service graph
     * @param sn The service node
     * @param x The x position
     * @param y The y position
     */
    protected void layoutService(ServiceGraph sg, ServiceNode sn, int x, int y) {
        
        // Ensure service has not already been processed
        if (requiresLayout(sn)) {
            
            sn.getProperties().put(ServiceGraphLayout.X_POSITION, x);
            sn.getProperties().put(ServiceGraphLayout.Y_POSITION, y);
            sn.getProperties().put(ServiceGraphLayout.WIDTH, SERVICE_WIDTH);
            sn.getProperties().put(ServiceGraphLayout.HEIGHT,
                    SERVICE_HEADER_PADDING+((OPERATION_HEIGHT
                            + SERVICE_BORDER_PADDING)
                            * sn.getOperations().size()));
            
            int opX=x+SERVICE_BORDER_PADDING;
            int opY=y+SERVICE_HEADER_PADDING;
            
            // Build list of operations
            java.util.List<OperationNode> oplist=new java.util.ArrayList<OperationNode>(sn.getOperations());
            
            // Sort list of operations
            Collections.sort(oplist, new Comparator<OperationNode>() {
                public int compare(OperationNode o1, OperationNode o2) {
                    return (o1.getOperation().getName().compareTo(o2.getOperation().getName()));
                }
            });

            // Layout the operations
            for (OperationNode opn : oplist) {
                opn.getProperties().put(ServiceGraphLayout.X_POSITION, opX);
                opn.getProperties().put(ServiceGraphLayout.Y_POSITION, opY);
                opn.getProperties().put(ServiceGraphLayout.WIDTH, OPERATION_WIDTH);
                opn.getProperties().put(ServiceGraphLayout.HEIGHT, OPERATION_HEIGHT);
                
                opY += OPERATION_HEIGHT + SERVICE_BORDER_PADDING;
            }
            
            // Check if width and/or height needs to be updated
            int maxX=(Integer)sn.getProperties().get(ServiceGraphLayout.X_POSITION)
                    +(Integer)sn.getProperties().get(ServiceGraphLayout.WIDTH);
            
            int maxY=(Integer)sn.getProperties().get(ServiceGraphLayout.Y_POSITION)
                    +(Integer)sn.getProperties().get(ServiceGraphLayout.HEIGHT);
            
            if (maxX > _width) {
                _width = maxX;
            }
            
            if (maxY > _height) {
                _height = maxY;
            }
            
            // Identify the 'used' services to layout
            int newX=maxX + SERVICE_HORIZONTAL_PADDING;
            int newY=y;

            // Identify links to be laid out
            java.util.List<UsageLink> links=new java.util.ArrayList<UsageLink>();
            
            for (UsageLink ul : sg.getUsageLinks()) {
                
                if (ul.getSource() == sn && requiresLayout(ul.getTarget())) {
                    links.add(ul);
                }
            }
            
            // Sort links
            Collections.sort(links, new Comparator<UsageLink>() {
                public int compare(UsageLink o1, UsageLink o2) {
                    // TODO: May need to sort based on interface's localpart, if a fully qualified name
                    return (o1.getTarget().getService().getServiceType().compareTo(
                            o2.getTarget().getService().getServiceType()));
                }
            });
            
            // Layout sorted links
            for (UsageLink ul : links) {
                layoutService(sg, ul.getTarget(), newX, newY);
                
                newY += (Integer)ul.getTarget().getProperties().get(ServiceGraphLayout.HEIGHT)
                        +SERVICE_VERTICAL_PADDING;
            }
        }
    }
}
