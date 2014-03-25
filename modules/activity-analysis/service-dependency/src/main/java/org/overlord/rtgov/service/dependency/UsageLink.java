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
package org.overlord.rtgov.service.dependency;

import org.overlord.rtgov.analytics.service.InvocationDefinition;

/**
 * This class represents the usage link between two service
 * nodes.
 *
 */
public class UsageLink {

    private java.util.List<InvocationDefinition> _invocations=
                    new java.util.ArrayList<InvocationDefinition>();
    private ServiceNode _source=null;
    private ServiceNode _target=null;
    
    /**
     * This is the default constructor.
     */
    public UsageLink() {
    }
    
    /**
     * This constructor sets the source and target service nodes.
     * 
     * @param source The source service node
     * @param target The target service node
     */
    public UsageLink(ServiceNode source, ServiceNode target) {
        _source = source;
        _target = target;
    }
    
    /**
     * This method returns the invocation definitions for
     * the service being used. The list will include
     * invocation details for normal and fault responses.
     * 
     * @return The invocation definitions
     */
    public java.util.List<InvocationDefinition> getInvocations() {
        return (_invocations);
    }
    
    /**
     * This method returns the source service node.
     * 
     * @return The source service node
     */
    public ServiceNode getSource() {
        return (_source);
    }
    
    /**
     * This method sets the source service node.
     * 
     * @param node The source service node
     */
    public void setSource(ServiceNode node) {
        _source = node;
    }
    
    /**
     * This method returns the target service node.
     * 
     * @return The target service node
     */
    public ServiceNode getTarget() {
        return (_target);
    }
    
    /**
     * This method sets the target service node.
     * 
     * @param node The target service node
     */
    public void setTarget(ServiceNode node) {
        _target = node;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj instanceof UsageLink) {
            UsageLink il=(UsageLink)obj;
            
            return (il.getSource() == _source && il.getTarget() == _target);
        }
        
        return (false);
    }
}
