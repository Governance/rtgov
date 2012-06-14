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
package org.overlord.bam.active.collection;

import java.util.logging.Logger;

import org.mvel2.MVEL;

/**
 * This class provides an MVEL implementation of the
 * predicate interface.
 *
 */
public class MVELPredicate implements Predicate {

    private static final Logger LOG=Logger.getLogger(MVELPredicate.class.getName());
    
    private String _expression=null;
    private java.io.Serializable _expressionCompiled=null;

    /**
     * This method sets the expression.
     * 
     * @param expr The expression
     */
    public void setExpression(String expr) {
        _expression = expr;
        
        // Invoke the compilation of the expression
        if (expr != null) {
            _expressionCompiled = MVEL.compileExpression(_expression);
        } else {
            _expressionCompiled = null;
        }
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
        
        if (_expressionCompiled != null) {
            Object result=MVEL.executeExpression(_expressionCompiled, item);
            
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

}
