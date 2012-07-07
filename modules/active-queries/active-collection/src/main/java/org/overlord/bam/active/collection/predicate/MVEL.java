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
package org.overlord.bam.active.collection.predicate;

import java.util.logging.Logger;

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
    public boolean evaluate(Object item) {
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
            Object result=org.mvel2.MVEL.executeExpression(_expressionCompiled, item);
            
            if (result instanceof Boolean) {
                ret = ((Boolean)result).booleanValue();
            } else {
                LOG.severe("Expression '"+_expression
                        +"' returned non-boolean result '"+result
                        +"' for item: "+item);
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
