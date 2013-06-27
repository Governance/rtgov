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
package org.overlord.rtgov.reports.util;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;
import org.overlord.rtgov.reports.ReportDefinition;
import org.overlord.rtgov.reports.model.Calendar;

public class ReportsUtilTest {

    @Test
    @Ignore
    public void testDeserializeReportDefinition() {
        String content="{ \"title\": \"My Report\" }";
        
        try {
            ReportDefinition defn=ReportsUtil.deserializeReportDefinition(content.getBytes());
            
            if (defn == null) {
                fail("No definition returned");
            }
            
            if (defn.getName() == null) {
                fail("Title not set");
            }
            
            if (!defn.getName().equals("My Report")) {
                fail("Title not expected: "+defn.getName());
            }
        } catch (Exception e) {
            fail("Failed to deserialize: "+e);
        }
    }

    @Test
    public void testDeserializeCalendar() {
        String content="{ \"name\": \"MyCalendar\", \"timeZone\": \"GMT+01:00\", " +
        		"\"monday\": { \"startHour\": 9, \"endHour\": 17, \"endMinute\": 30 }, " +
        		"\"excludedDays\": [{ \"day\": 25, \"month\":12, \"reason\":\"Christmas\" }] }";
        
        try {
            Calendar cal=ReportsUtil.deserializeCalendar(content.getBytes());
            
            if (cal == null) {
                fail("Calendar was null");
            }
            
            if (!cal.getName().equals("MyCalendar")) {
                fail("Name incorrect: "+cal.getName());
            }
            
            if (cal.getTimeZone() == null) {
                fail("Timezone not set");
            }
            
            if (!cal.getTimeZone().getID().equals("GMT+01:00")) {
                fail("Timezone id incorrect: "+cal.getTimeZone().getID());
            }
            
            if (cal.getMonday() == null) {
                fail("Monday working day not defined");
            }
            
            if (cal.getMonday().getStartHour() != 9) {
                fail("Monday start hour not 9: "+cal.getMonday().getStartHour());
            }
            
            if (cal.getMonday().getStartMinute() != 0) {
                fail("Monday start minute not 0: "+cal.getMonday().getStartMinute());
            }
            
            if (cal.getMonday().getEndHour() != 17) {
                fail("Monday end hour not 17: "+cal.getMonday().getEndHour());
            }

            if (cal.getMonday().getEndMinute() != 30) {
                fail("Monday end minute not 30: "+cal.getMonday().getEndMinute());
            }
            
            if (cal.getExcludedDays().size() != 1) {
                fail("Expecting single excluded day: "+cal.getExcludedDays().size());
            }

            if (cal.getExcludedDays().get(0).getDay() != 25) {
                fail("Excluded day should be 25: "+cal.getExcludedDays().get(0).getDay());
            }

            if (cal.getExcludedDays().get(0).getMonth() != 12) {
                fail("Excluded month should be 12: "+cal.getExcludedDays().get(0).getMonth());
            }

            if (cal.getExcludedDays().get(0).getYear() != 0) {
                fail("Excluded year should be 0: "+cal.getExcludedDays().get(0).getYear());
            }

            if (!cal.getExcludedDays().get(0).getReason().equals("Christmas")) {
                fail("Excluded reason incorrect: "+cal.getExcludedDays().get(0).getReason());
            }
        } catch (Exception e) {
            fail("Failed to deserialize: "+e);
        }
    }

}
