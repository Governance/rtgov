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
package org.overlord.rtgov.active.collection.predicate;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.active.collection.predicate.MVEL;

public class MVELTest {

    @Test
    public void testPredicateTrue() {
        MVEL predicate=new MVEL();
        
        predicate.setExpression("serviceType == \"OrderService\" && operation == \"buy\"");
        
        TestObject to=new TestObject();
        
        to.setServiceType("OrderService");
        to.setOperation("buy");
        
        if (!predicate.evaluate(null, to)) {
            fail("Evaluation should be true");
        }
    }

    @Test
    public void testPredicateFalse() {
        MVEL predicate=new MVEL();
        
        predicate.setExpression("serviceType == \"OrderService\" && operation == \"buy\"");
        
        TestObject to=new TestObject();
        
        to.setServiceType("InventoryService");
        to.setOperation("lookup");
        
        if (predicate.evaluate(null, to)) {
            fail("Evaluation should be false");
        }
    }

    @Test
    public void testPredicateANDFalseWithMissingValue() {
        MVEL predicate=new MVEL();
        
        predicate.setExpression("serviceType == \"OrderService\" && operation == \"buy\"");
        
        TestObject to=new TestObject();
        
        to.setServiceType("OrderService");
        
        if (predicate.evaluate(null, to)) {
            fail("Evaluation should be false");
        }
    }

    @Test
    public void testPredicateORTrueWithMissingValue() {
        MVEL predicate=new MVEL();
        
        predicate.setExpression("serviceType == \"OrderService\" || operation == \"buy\"");
        
        TestObject to=new TestObject();
        
        to.setOperation("buy");
        
        if (!predicate.evaluate(null, to)) {
            fail("Evaluation should be true");
        }
    }

    public class TestObject {
        
        private String _serviceType=null;
        private String _operation=null;
        
        public void setServiceType(String type) {
            _serviceType = type;
        }
        
        public String getServiceType() {
            return (_serviceType);
        }
        
        public void setOperation(String op) {
            _operation = op;
        }
        
        public String getOperation() {
            return (_operation);
        }
    }
}
