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
package org.overlord.rtgov.analytics.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.analytics.service.InvocationMetric;

public class InvocationMetricTest {

    @Test
    public void testMerge() {

        InvocationMetric im1=new InvocationMetric();
        
        im1.setAverage(10);
        im1.setCount(1);
        im1.setFaults(1);
        
        im1.setMin(10);
        im1.setMax(10);
        
        InvocationMetric im2=new InvocationMetric();
        
        im2.setAverage(50);
        im2.setCount(3);
        im2.setFaults(1);
        im2.setMin(10);
        im2.setMax(70);
        
        im1.merge(im2);
        
        if (im1.getCount() != 4) {
            fail("Count should be 4: "+im1.getCount());
        }
        
        if (im1.getFaults() != 2) {
            fail("Faults should be 2: "+im1.getFaults());
        }
        
        if (im1.getAverage() != 40) {
            fail("Average should be 40: "+im1.getAverage());
        }
        
        if (im1.getMin() != 10) {
            fail("Min should be 10: "+im1.getMin());
        }
        
        if (im1.getMax() != 70) {
            fail("Max should be 70: "+im1.getMax());
        }
    }

}
