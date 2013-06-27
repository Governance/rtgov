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
