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
