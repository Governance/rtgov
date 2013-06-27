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
