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
package org.overlord.rtgov.tests.epn;

import org.overlord.rtgov.ep.Predicate;

/**
 * This class provides the child predicate.
 *
 */
public class ChildPredicate extends Predicate {

    private int _min=0;
    private int _max=0;
    
    /**
     * This method sets the min.
     * 
     * @param min The min
     */
    public void setMin(int min) {
        _min = min;
    }
    
    /**
     * This method gets the min.
     * 
     * @return The min
     */
    public int getMin() {
        return (_min);
    }
    
    /**
     * This method sets the max.
     * 
     * @param max The max
     */
    public void setMax(int max) {
        _max = max;
    }
    
    /**
     * This method gets the max.
     * 
     * @return The max
     */
    public int getMax() {
        return (_max);
    }
    
    @Override
    public boolean evaluate(Object event) {
        return (event instanceof Obj2
                && ((Obj2)event).getValue() >= getMin()
                && ((Obj2)event).getValue() <= getMax());
    }

}
