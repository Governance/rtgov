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
package org.overlord.rtgov.active.collection.epn;

public class TestObject implements java.io.Serializable {
    
    private static final long serialVersionUID = -7280462159940869489L;

    private int _min=0;
    private int _max=0;
    private int _avg=0;
    
    private String _name=null;
    
    public TestObject() {
    }
    
    public TestObject(String name, int avg) {
        _name = name;
        _avg = avg;
    }
    
    public void setMin(int min) {
        _min = min;
    }
    
    public int getMin() {
        return (_min);
    }
    
    public void setMax(int max) {
        _max = max;
    }
    
    public int getMax() {
        return (_max);
    }
    
    public void setAvg(int avg) {
        _avg = avg;
    }

    public int getAvg() {
        return (_avg);
    }
    
    public void setName(String name) {
        _name = name;
    }
    
    public String getName() {
        return (_name);
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof TestObject) {
            if (((TestObject)obj)._name.equals(_name)) {
                return (true);
            }
        }
        return (false);
    }
}
