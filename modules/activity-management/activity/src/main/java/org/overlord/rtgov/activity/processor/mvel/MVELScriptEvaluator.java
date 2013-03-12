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
import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.processor.ScriptEvaluator;

/**
 * This class represents a MVEL based script evaluator.
 *
 */
public class MVELScriptEvaluator extends ScriptEvaluator {

    private Object _compiledExpression=null;
    
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
    public void evaluate(Object information, ActivityType activityType) {
        java.util.Map<String,Object> vars=new java.util.HashMap<String, Object>();
        vars.put("information", information);
        vars.put("activity", activityType);
        
        MVEL.executeExpression(_compiledExpression, vars);        
    }
    
}
