/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-13, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.rtgov.jee.reports;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Test;
import org.overlord.rtgov.common.util.RTGovPropertiesProvider;
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
