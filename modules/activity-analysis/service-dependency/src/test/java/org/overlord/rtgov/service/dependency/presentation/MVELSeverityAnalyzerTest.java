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
package org.overlord.rtgov.service.dependency.presentation;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.analytics.service.InvocationMetric;
import org.overlord.rtgov.service.dependency.presentation.MVELSeverityAnalyzer;
import org.overlord.rtgov.service.dependency.presentation.Severity;

public class MVELSeverityAnalyzerTest {

    @Test
    public void test() {
        MVELSeverityAnalyzer selector=new MVELSeverityAnalyzer();
        
        selector.setScriptLocation("SeverityAnalyzer.mvel");
        
        try {
            selector.init();
        } catch (Exception e) {
            fail("Failed to initialize selector: "+e);
        }
        
        InvocationMetric metric1=new InvocationMetric();
        metric1.setAverage(1000);
        metric1.setCount(1);
        
        InvocationMetric metric2=new InvocationMetric();
        metric2.setAverage(1100);
        metric2.setCount(1);
        
        InvocationMetric metric3=new InvocationMetric();
        metric3.setAverage(900);
        metric3.setCount(1);
        
        InvocationMetric metric4=new InvocationMetric();
        metric4.setAverage(1050);
        metric4.setCount(1);
        
        InvocationMetric metric5=new InvocationMetric();
        metric5.setAverage(1100);
        metric5.setCount(1);
        
        InvocationMetric metric6=new InvocationMetric();
        metric6.setAverage(2000);
        metric6.setCount(1);
        
        java.util.List<InvocationMetric> history=new java.util.ArrayList<InvocationMetric>();
        history.add(metric1);
        history.add(metric2);
        history.add(metric3);
        history.add(metric4);
        history.add(metric5);
        history.add(metric6);

        InvocationMetric summary=new InvocationMetric();
        summary.merge(metric1);
        summary.merge(metric2);
        summary.merge(metric3);
        summary.merge(metric4);
        summary.merge(metric5);
        summary.merge(metric6);
        
        Severity severity=selector.getSeverity(null, summary, history);
        
        if (severity == null) {
            fail("Failed to get severity");
        }
        
        if (!severity.equals(Severity.Warning)) {
            fail("Didn't get expected severity of Warning: "+severity);
        }
    }

}
