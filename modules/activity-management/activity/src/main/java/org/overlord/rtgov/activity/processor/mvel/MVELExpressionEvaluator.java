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
package org.overlord.rtgov.activity.processor.mvel;

import org.mvel2.MVEL;
import org.overlord.rtgov.activity.processor.ExpressionEvaluator;

/**
 * This class represents a MVEL based expression evaluator.
 *
 */
public class MVELExpressionEvaluator extends ExpressionEvaluator {

    private Object _compiledExpression=null;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void init() throws Exception {
        super.init();
        
        // Initialize the mvel expression
        _compiledExpression = MVEL.compileExpression(getExpression());
    }
    
    /**
     * {@inheritDoc}
     */
    public String evaluate(Object information) {
        Object result=MVEL.executeExpression(_compiledExpression, information);
        
        if (result != null) {
            return (result.toString());
        }
        
        return (null);
    }
    
}
