/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-13, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.rtgov.activity.processor.mvel;

import org.mvel2.MVEL;
import org.overlord.rtgov.activity.processor.InformationTransformer;

/**
 * This class represents a MVEL based information transformer.
 *
 */
public class MVELInformationTransformer extends InformationTransformer {

    private String _expression=null;
    
    private Object _compiledExpression=null;
    
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
     * {@inheritDoc}
     */
    @Override
    public void init() throws Exception {
        super.init();
        
        // Initialize the mvel script expression
        _compiledExpression = MVEL.compileExpression(getExpression());
    }
    
    /**
     * {@inheritDoc}
     */
    public String transform(Object information) {
        java.util.Map<String,Object> vars=new java.util.HashMap<String, Object>();
        vars.put("information", information);
        
        Object result=MVEL.executeExpression(_compiledExpression, vars);  
        
        if (result instanceof String) {
            return ((String)result);
        }
        
        return (null);
    }
    
}
