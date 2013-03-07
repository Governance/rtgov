/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-12, Red Hat Middleware LLC, and others contributors as indicated
 * by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
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
        
        im1.setMin(10);
        im1.setMax(10);
        
        InvocationMetric im2=new InvocationMetric();
        
        im2.setAverage(50);
        im2.setCount(3);
        im2.setMin(10);
        im2.setMax(70);
        
        im1.merge(im2);
        
        if (im1.getCount() != 4) {
            fail("Count should be 4: "+im1.getCount());
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
