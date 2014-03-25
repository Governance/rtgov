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
 * This class represents the invocation link between a
 * source and target service operation. The invocation
 * encompasses both normal and fault responses.
 *
 */
public class InvocationLink {

    private java.util.List<InvocationDefinition> _invocations=
                    new java.util.ArrayList<InvocationDefinition>();
    private OperationNode _source=null;
    private OperationNode _target=null;
    
    /**
     * This is the default constructor.
     */
    public InvocationLink() {
    }
    
    /**
     * This constructor sets the source and target operation nodes.
     * 
     * @param source The source operation node
     * @param target The target operation node
     */
    public InvocationLink(OperationNode source, OperationNode target) {
        _source = source;
        _target = target;
    }
    
    /**
     * This method returns the invocation definitions for
     * the operation being invoked. The list will include
     * invocation details for normal and fault responses.
     * 
     * @return The invocation definitions
     */
    public java.util.List<InvocationDefinition> getInvocations() {
        return (_invocations);
    }
    
    /**
     * This method returns the source operation node.
     * 
     * @return The source operation node
     */
    public OperationNode getSource() {
        return (_source);
    }
    
    /**
     * This method sets the source operation node.
     * 
     * @param node The source operation node
     */
    public void setSource(OperationNode node) {
        _source = node;
    }
    
    /**
     * This method returns the target operation node.
     * 
     * @return The target operation node
     */
    public OperationNode getTarget() {
        return (_target);
    }
    
    /**
     * This method sets the target operation node.
     * 
     * @param node The target operation node
     */
    public void setTarget(OperationNode node) {
        _target = node;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj instanceof InvocationLink) {
            InvocationLink il=(InvocationLink)obj;
            
            return (il.getSource() == _source && il.getTarget() == _target);
        }
        
        return (false);
    }
}
