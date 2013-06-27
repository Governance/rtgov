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
package org.overlord.rtgov.active.collection.predicate;

import java.text.MessageFormat;
import java.util.logging.Logger;

import org.overlord.rtgov.active.collection.ActiveCollectionContext;

/**
 * This class provides an MVEL implementation of the
 * predicate interface.
 *
 */
public class MVEL extends Predicate {

    private static final Logger LOG=Logger.getLogger(MVEL.class.getName());
    
    private String _expression=null;
    private java.io.Serializable _expressionCompiled=null;
    private boolean _initialized=false;

    /**
     * This is the default constructor for the MVEL predicate.
     */
    public MVEL() {
    }
    
    /**
     * This constructor initializes the expression for the MVEL
     * predicate.
     * 
     * @param expr The predicate
     */
    public MVEL(String expr) {
        setExpression(expr);
    }
    
    /**
     * This method sets the expression.
     * 
     * @param expr The expression
     */
    public void setExpression(String expr) {
        _expression = expr;
        
        // Reset state
        _initialized = false;
        _expressionCompiled = null;
    }
    
    /**
     * This method gets the expression.
     * 
     * @return The expression
     */
    public String getExpression() {
        return (_expression);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean evaluate(ActiveCollectionContext context, Object item) {
        boolean ret=false;
        
        if (!_initialized) {
            if (_expression != null) {
                _expressionCompiled = org.mvel2.MVEL.compileExpression(_expression);
            } else {
                _expressionCompiled = null;
            }

            _initialized = true;
        }
        
        if (_expressionCompiled != null) {
            java.util.Map<String,Object> vars=new java.util.HashMap<String,Object>();
            vars.put("context", context);
            
            Object result=org.mvel2.MVEL.executeExpression(_expressionCompiled, item, vars);
            
            if (result instanceof Boolean) {
                ret = ((Boolean)result).booleanValue();
            } else {
                LOG.severe(MessageFormat.format(
                        java.util.PropertyResourceBundle.getBundle(
                        "active-collection.Messages").getString("ACTIVE-COLLECTION-2"),
                        _expression, result, item));
            }
        }
        
        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return ("MVEL["+_expression+"]");
    }
}
