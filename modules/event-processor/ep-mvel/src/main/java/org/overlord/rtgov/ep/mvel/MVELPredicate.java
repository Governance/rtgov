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
package org.overlord.rtgov.ep.mvel;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.mvel2.MVEL;
import org.overlord.rtgov.ep.Predicate;

/**
 * This class represents the MVEL implementation of the Event
 * Processor.
 *
 */
public class MVELPredicate extends Predicate {

    private static final Logger LOG=Logger.getLogger(MVELPredicate.class.getName());

    private String _script=null;
    private String _expression=null;
    private Object _compiled=null;

    /**
     * {@inheritDoc}
     */
    public void init() throws Exception {
        
        String expr=_expression;
        
        if (expr == null && _script != null) {
            // Load the script
            java.io.InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(_script);
            
            if (is == null) {
                throw new Exception("Unable to locate MVEL script '"+_script+"'");
            } else {
                byte[] b=new byte[is.available()];
                is.read(b);
                is.close();
                
                expr = new String(b);
            }
        }
        
        if (expr != null) {
            // Compile expression
            _compiled = MVEL.compileExpression(expr);

            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Initialized expression="+expr);
            }
        } else {
            throw new Exception("No expression or script specified");
        }
    }
    
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
     * @param expression The expression
     */
    public void setExpression(String expression) {
        _expression = expression;
    }
    
    /**
     * This method returns the script.
     * 
     * @return The script
     */
    public String getScript() {
        return (_script);
    }
    
    /**
     * This method sets the script.
     * 
     * @param script The script
     */
    public void setScript(String script) {
        _script = script;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean evaluate(Object event) {
        boolean ret=false;
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Evaluate event '"+event
                    +"' on MVEL Predicate "
                    +(getScript() != null ? "script="+getScript()
                                        : "expression="+getExpression())
                    +"]");
        }

        if (_compiled != null) {
            java.util.Map<String,Object> vars=
                    new java.util.HashMap<String, Object>();
            
            vars.put("event", event);

            ret = (Boolean)MVEL.executeExpression(_compiled, vars);
        }

        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Evaluation result="+ret);
        }

        return (ret);
    }

}
