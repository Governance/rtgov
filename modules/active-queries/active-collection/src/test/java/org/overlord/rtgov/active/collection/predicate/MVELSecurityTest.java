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
import org.overlord.rtgov.active.collection.ActiveCollectionContext;
import org.overlord.rtgov.active.collection.ActiveList;
import org.overlord.rtgov.active.collection.ActiveMap;

public class MVELSecurityTest {

    @org.junit.BeforeClass
    public static void init() {
        java.net.URL url=ClassLoader.getSystemResource("security/security.policy");
        
        System.setProperty("java.security.policy", url.getFile());
        
        // Create a security manager
        System.setSecurityManager(new SecurityManager());        
    }
    
    @org.junit.AfterClass
    public static void close() {
        System.setSecurityManager(null);        
    }
    
    @Test
    public void testPredicatePropertyAccessor() {
        
        MVEL predicate=new MVEL();
        
        predicate.setExpression("serviceType == \"OrderService\" && operation == \"buy\"");
        
        TestObject to=new TestObject();
        
        to.setServiceType("OrderService");
        to.setOperation("buy");
        
        try {
            predicate.evaluate(null, to);
            
        } catch (Exception e) {            
            e.printStackTrace();
            fail("Should NOT have failed");
        }        
    }
    
    @Test
    public void testPredicateContextAndMethodInvoke() {
        
        MVEL predicate=new MVEL();
        
        predicate.setExpression("map = context.getMap(\"Map\"); if (map == null) { return false; } return !map.containsKey(serviceType);");
        
        ActiveCollectionContext context=new ActiveCollectionContext() {

            public ActiveList getList(String name) {
                return null;
            }

            public ActiveMap getMap(String name) {
                return new ActiveMap("Map");
            }
            
        };
        
        TestObject to=new TestObject();
        to.setServiceType("TestService");
        
        try {
            predicate.evaluate(context, to);
            
        } catch (Exception e) {            
            e.printStackTrace();
            fail("Should NOT have failed");
        }        
    }
    
    @Test
    public void testPredicateExit() {
        
        MVEL predicate=new MVEL();
        
        predicate.setExpression("System.exit(0)");
        
        try {
            predicate.evaluate(null, "TestValue");
            
            fail("Should have failed due to security exception");
            
        } catch (Exception e) {
            //e.printStackTrace();
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
