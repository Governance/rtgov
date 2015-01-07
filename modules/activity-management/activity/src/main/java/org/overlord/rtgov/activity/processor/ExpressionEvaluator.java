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
package org.overlord.rtgov.activity.processor;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
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
