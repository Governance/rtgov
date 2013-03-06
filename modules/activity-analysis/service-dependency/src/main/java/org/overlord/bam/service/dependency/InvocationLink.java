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
    public int hashCode() {
        return (_source.hashCode());
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
