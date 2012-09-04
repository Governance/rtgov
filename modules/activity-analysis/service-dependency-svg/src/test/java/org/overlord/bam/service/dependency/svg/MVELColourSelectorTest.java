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
package org.overlord.bam.service.dependency.svg;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.bam.analytics.service.InvocationMetric;

public class MVELColourSelectorTest {

    @Test
    public void test() {
        MVELColourSelector selector=new MVELColourSelector();
        
        selector.setScriptLocation("ColourSelector.mvel");
        
        try {
            selector.init();
        } catch (Exception e) {
            fail("Failed to initialize selector: "+e);
        }
        
        InvocationMetric metric=new InvocationMetric();
        metric.setMax(1000);
        metric.setMin(500);
        metric.setAverage(900);
        
        String colour=selector.getColour(null, metric);
        
        if (colour == null) {
            fail("Failed to get colour");
        }
        
        if (!colour.equals("#FF9479")) {
            fail("Didn't get expected colour");
        }
    }

}
