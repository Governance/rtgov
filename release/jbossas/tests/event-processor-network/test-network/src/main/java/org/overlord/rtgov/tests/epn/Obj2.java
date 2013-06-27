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

import java.io.Serializable;

/**
 * This is object 2 class.
 *
 */
public class Obj2 implements Serializable {

    private static final long serialVersionUID = -3530824983315376813L;

    private int _value=0;
    
    /**
     * The constructor.
     * 
     * @param val The value
     */
    public Obj2(int val) {
        _value = val;
    }
    
    /**
     * This method returns the value.
     * 
     * @return The value
     */
    public int getValue() {
        return (_value);
    }
    
    @Override
    public int hashCode() {
        return (_value);
    }
    
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Obj2
                && ((Obj2)obj).getValue() == _value);
    }
    
    @Override
    public String toString() {
        return ("Obj2["+_value+"]");
    }
}
