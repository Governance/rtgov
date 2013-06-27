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
package org.overlord.rtgov.activity.processor;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.processor.mvel.MVELScriptEvaluator;

/**
 * This abstract class represents a script evaluator.
 *
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type")
@JsonSubTypes({@Type(value=MVELScriptEvaluator.class, name="mvel") })
public abstract class ScriptEvaluator {
    
    private String _expression=null;
    
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
     * This method initializes the script evaluator.
     * 
     * @throws Exception Failed to initialize
     */
    public void init() throws Exception {
    }
    
    /**
     * This method evaluates the supplied information to
     * initialize the supplied activity type.
     * 
     * @param information The information
     * @param activityType The activity type
     */
    public abstract void evaluate(Object information, ActivityType activityType);
    
    /**
     * This method closes the expression evaluator.
     * 
     * @throws Exception Failed to close
     */
    public void close() throws Exception {
    }
    
}
