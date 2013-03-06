/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-11, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.rtgov.epn.mvel;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.mvel2.MVEL;
import org.overlord.rtgov.epn.Predicate;

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
