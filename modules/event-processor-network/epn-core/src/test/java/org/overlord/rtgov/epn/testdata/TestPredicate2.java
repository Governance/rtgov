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
package org.overlord.rtgov.epn.testdata;

import org.overlord.rtgov.ep.Predicate;

public class TestPredicate2 extends Predicate {

    private String _someProperty=null;
    
    public String getSomeProperty() {
        return(_someProperty);
    }
    
    public void setSomeProperty(String prop) {
        _someProperty = prop;
    }
    
    public boolean evaluate(Object arg0) {
        if (arg0 instanceof TestEvent2) {
            TestEvent2 te=(TestEvent2)arg0;
            
            return te.getValue() >= 20;
         }
        
        return false;
    }

}
