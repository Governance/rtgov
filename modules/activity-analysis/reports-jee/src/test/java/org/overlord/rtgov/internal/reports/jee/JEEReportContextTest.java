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
package org.overlord.rtgov.internal.reports.jee;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Test;
import org.overlord.rtgov.common.util.RTGovPropertiesProvider;
import org.overlord.rtgov.internal.reports.jee.JEEReportContext;
import org.overlord.rtgov.reports.model.Calendar;

public class JEEReportContextTest {

    @Test
    public void testGetCalendar() {
        JEEReportContext context=new JEEReportContext();
        
        TestPropertiesProvider provider=new TestPropertiesProvider();
        context.setPropertiesProvider(provider);
        
        java.net.URL url=JEEReportContextTest.class.getResource("/config/TestCalendar.json");
        
        if (url == null) {
            fail("Failed to find URL for test calendar");
        }
        
        provider.getProperties().put("calendar.Test", url.getFile());
        
        Calendar cal=context.getCalendar("Test", null);
        
        if (cal == null) {
            fail("Failed to get calendar");
        }
        
        if (!cal.getName().equals("Test")) {
            fail("Calendar name is not Test: "+cal.getName());
        }
    }

    public static class TestPropertiesProvider implements RTGovPropertiesProvider {
        
        private java.util.Properties _properties=new java.util.Properties();
        
        public String getProperty(String name) {
            return _properties.getProperty(name);
        }

        public Properties getProperties() {
            return _properties;
        }
        
    }
}
