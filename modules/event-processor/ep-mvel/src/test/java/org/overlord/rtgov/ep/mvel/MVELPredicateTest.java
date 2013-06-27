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

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.ep.mvel.MVELPredicate;

public class MVELPredicateTest {

    @Test
    public void testEvaluateScript() {
        MVELPredicate pred=new MVELPredicate();
        
        pred.setScript("script/Evaluate.mvel");
        
        try {
            pred.init();
        } catch (Exception e) {
            fail("Failed to initialize: "+e);
        }
        
        try {
            if (!pred.evaluate("Test")) {
                fail("Evaluation of string failed");
            }
        } catch (Exception e) {
            fail("Failed to process: "+e);
        }
        
        try {
            if (pred.evaluate(5)) {
                fail("Evaluation of integer failed");
            }
        } catch (Exception e) {
            fail("Failed to process: "+e);
        }
    }

    @Test
    public void testEvaluateExpression() {
        MVELPredicate pred=new MVELPredicate();
        
        pred.setExpression("event instanceof java.lang.String");
        
        try {
            pred.init();
        } catch (Exception e) {
            fail("Failed to initialize: "+e);
        }
        
        try {
            if (!pred.evaluate("Test")) {
                fail("Evaluation of string failed");
            }
        } catch (Exception e) {
            fail("Failed to process: "+e);
        }
        
        try {
            if (pred.evaluate(5)) {
                fail("Evaluation of integer failed");
            }
        } catch (Exception e) {
            fail("Failed to process: "+e);
        }
    }
}
