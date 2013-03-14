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
package org.overlord.rtgov.activity.processor;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.overlord.rtgov.activity.processor.mvel.MVELExpressionEvaluator;
import org.overlord.rtgov.activity.processor.xpath.XPathExpressionEvaluator;

/**
 * This abstract class represents an expression evaluator.
 *
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type")
@JsonSubTypes({@Type(value=MVELExpressionEvaluator.class, name="mvel"),
    @Type(value=XPathExpressionEvaluator.class, name="xpath") })
public abstract class ExpressionEvaluator {
    
    private String _expression=null;
    
    private boolean _optional=false;
    
    /**
     * This method returns the expression.
     * 
     * @return The expression
     */
    public String getExpression() {
        return (_expression);
    }
    
    /**
     * This method sets the expression.
     * 
     * @param expr The expression
     */
    public void setExpression(String expr) {
        _expression = expr;
    }
    
    /**
     * This method gets whether the value being obtained
     * is optional.
     * 
     * @return Whether the value associated with the expression is optional
     */
    public boolean getOptional() {
    	return (_optional);
    }
    
    /**
     * This method sets whether the value being obtained
     * is optional.
     * 
     * @param optional Whether the value associated with the expression is optional
     */
    public void setOptional(boolean optional) {
    	_optional = optional;
    }

    /**
     * This method initializes the expression evaluator.
     * 
     * @throws Exception Failed to initialize
     */
    public void init() throws Exception {
    }
    
    /**
     * This method evaluates the supplied information to
     * derive and return a result.
     * 
     * @param information The information
     * @return The result, or null if unable to evaluate
     */
    public abstract String evaluate(Object information);
    
    /**
     * This method closes the expression evaluator.
     * 
     * @throws Exception Failed to close
     */
    public void close() throws Exception {
    }
    
}
