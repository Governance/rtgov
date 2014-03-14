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
package org.overlord.rtgov.reports.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class CalendarTest {

    @Test
    public void testIsWorkingDateTime() {
        
        Calendar cal=new Calendar();
        
        Calendar.WorkingDay monday=new Calendar.WorkingDay();
        monday.setStartHour(9);
        monday.setEndHour(17);
        monday.setEndMinute(30);
        
        cal.setMonday(monday);
        
        // Test on a Sunday
        java.util.Calendar tc1=java.util.Calendar.getInstance();
        tc1.set(java.util.Calendar.DAY_OF_MONTH, 23);
        tc1.set(java.util.Calendar.MONTH, java.util.Calendar.JUNE);
        tc1.set(java.util.Calendar.YEAR, 2013);
        tc1.set(java.util.Calendar.HOUR_OF_DAY, 11);
        tc1.set(java.util.Calendar.MINUTE, 30);
        tc1.set(java.util.Calendar.MILLISECOND,0);
        
        if (cal.isWorkingDateTime(tc1.getTimeInMillis())) {            
            fail("Sunday is not a working day");          
        }
        
        // Test on a Monday
        java.util.Calendar tc2=java.util.Calendar.getInstance();
        tc2.set(java.util.Calendar.DAY_OF_MONTH, 24);
        tc2.set(java.util.Calendar.MONTH, java.util.Calendar.JUNE);
        tc2.set(java.util.Calendar.YEAR, 2013);
        tc2.set(java.util.Calendar.HOUR_OF_DAY, 11);
        tc2.set(java.util.Calendar.MINUTE, 30); 
        tc2.set(java.util.Calendar.MILLISECOND,0);
       
        if (!cal.isWorkingDateTime(tc2.getTimeInMillis())) {            
            fail("Monday is a working day");          
        }
        
        // Test on a Monday out of hours
        java.util.Calendar tc3=java.util.Calendar.getInstance();
        tc3.set(java.util.Calendar.DAY_OF_MONTH, 24);
        tc3.set(java.util.Calendar.MONTH, java.util.Calendar.JUNE);
        tc3.set(java.util.Calendar.YEAR, 2013);
        tc3.set(java.util.Calendar.HOUR_OF_DAY, 17);
        tc3.set(java.util.Calendar.MINUTE, 31);
        tc3.set(java.util.Calendar.MILLISECOND,0);
        
        if (cal.isWorkingDateTime(tc3.getTimeInMillis())) {            
            fail("Monday is a working day, but should be out of hours");          
        }
    }

    @Test
    public void testIsWorkingDateTimeWithExclusion() {
        
        Calendar cal=new Calendar();
        
        Calendar.WorkingDay wednesday=new Calendar.WorkingDay();
        wednesday.setStartHour(9);
        wednesday.setEndHour(17);
        wednesday.setEndMinute(30);
        
        cal.setWednesday(wednesday);
        
        Calendar.ExcludedDay excluded=new Calendar.ExcludedDay();
        excluded.setDay(25);
        excluded.setMonth(12);
        excluded.setReason("Christmas Day");
        
        cal.getExcludedDays().add(excluded);
        
        // Test on a normal Wednesday
        java.util.Calendar tc1=java.util.Calendar.getInstance();
        tc1.set(java.util.Calendar.DAY_OF_MONTH, 18);
        tc1.set(java.util.Calendar.MONTH, java.util.Calendar.DECEMBER);
        tc1.set(java.util.Calendar.YEAR, 2013);
        tc1.set(java.util.Calendar.HOUR_OF_DAY, 11);
        tc1.set(java.util.Calendar.MINUTE, 30);
        tc1.set(java.util.Calendar.MILLISECOND,0);
        
        if (!cal.isWorkingDateTime(tc1.getTimeInMillis())) {            
            fail("This wednesday is a working day");          
        }
        
        // Test on Christmas
        java.util.Calendar tc2=java.util.Calendar.getInstance();
        tc2.set(java.util.Calendar.DAY_OF_MONTH, 25);
        tc2.set(java.util.Calendar.MONTH, java.util.Calendar.DECEMBER);
        tc2.set(java.util.Calendar.YEAR, 2013);
        tc2.set(java.util.Calendar.HOUR_OF_DAY, 11);
        tc2.set(java.util.Calendar.MINUTE, 30);
        tc1.set(java.util.Calendar.MILLISECOND,0);
        
        if (cal.isWorkingDateTime(tc2.getTimeInMillis())) {            
            fail("Christmas Wednesday is not a working day");          
        }
    }

    @Test
    public void testGetWorkingDurationSameDay() {
        Calendar cal=new Calendar();
        
        Calendar.WorkingDay monday=new Calendar.WorkingDay();
        monday.setStartHour(9);
        monday.setEndHour(17);
        monday.setEndMinute(30);
        
        cal.setMonday(monday);
        
        // Test on a Monday, within business hours
        java.util.Calendar from1=java.util.Calendar.getInstance();
        from1.set(java.util.Calendar.DAY_OF_MONTH, 24);
        from1.set(java.util.Calendar.MONTH, java.util.Calendar.JUNE);
        from1.set(java.util.Calendar.YEAR, 2013);
        from1.set(java.util.Calendar.HOUR_OF_DAY, 11);
        from1.set(java.util.Calendar.MINUTE, 0);
        from1.set(java.util.Calendar.MILLISECOND,0);
	
        java.util.Calendar to1=java.util.Calendar.getInstance();
        to1.set(java.util.Calendar.DAY_OF_MONTH, 24);
        to1.set(java.util.Calendar.MONTH, java.util.Calendar.JUNE);
        to1.set(java.util.Calendar.YEAR, 2013);
        to1.set(java.util.Calendar.HOUR_OF_DAY, 12);
        to1.set(java.util.Calendar.MINUTE, 0);
        to1.set(java.util.Calendar.MILLISECOND,0);
        
        if (cal.getWorkingDuration(from1.getTimeInMillis(), to1.getTimeInMillis()) != 60*60*1000) {            
            fail("Should be an hour: "+cal.getWorkingDuration(from1.getTimeInMillis(), to1.getTimeInMillis()));          
        }
        
        // Test on a Monday, outside business hours
        java.util.Calendar from2=java.util.Calendar.getInstance();
        from2.set(java.util.Calendar.DAY_OF_MONTH, 24);
        from2.set(java.util.Calendar.MONTH, java.util.Calendar.JUNE);
        from2.set(java.util.Calendar.YEAR, 2013);
        from2.set(java.util.Calendar.HOUR_OF_DAY, 6);
        from2.set(java.util.Calendar.MINUTE, 0);
        from2.set(java.util.Calendar.MILLISECOND,0);
        
        java.util.Calendar to2=java.util.Calendar.getInstance();
        to2.set(java.util.Calendar.DAY_OF_MONTH, 24);
        to2.set(java.util.Calendar.MONTH, java.util.Calendar.JUNE);
        to2.set(java.util.Calendar.YEAR, 2013);
        to2.set(java.util.Calendar.HOUR_OF_DAY, 23);
        to2.set(java.util.Calendar.MINUTE, 0);
        to2.set(java.util.Calendar.MILLISECOND,0);
        
        if (cal.getWorkingDuration(from2.getTimeInMillis(), to2.getTimeInMillis()) != 8.5*60*60*1000) {            
            fail("Should be 8.5 hours: "+cal.getWorkingDuration(from2.getTimeInMillis(), to2.getTimeInMillis()));          
        }
     }

    @Test
    public void testGetWorkingDurationAcrossDayBoundary() {
        Calendar cal=new Calendar();
        
        Calendar.WorkingDay monday=new Calendar.WorkingDay();
        monday.setStartHour(9);
        monday.setEndHour(17);
        monday.setEndMinute(30);
        
        cal.setMonday(monday);
        
        Calendar.WorkingDay tuesday=new Calendar.WorkingDay();
        tuesday.setStartHour(9);
        tuesday.setEndHour(17);
        tuesday.setEndMinute(30);
        
        cal.setTuesday(tuesday);
        
        // Test across day boundary
        java.util.Calendar from1=java.util.Calendar.getInstance();
        from1.set(java.util.Calendar.DAY_OF_MONTH, 24);
        from1.set(java.util.Calendar.MONTH, java.util.Calendar.JUNE);
        from1.set(java.util.Calendar.YEAR, 2013);
        from1.set(java.util.Calendar.HOUR_OF_DAY, 16);
        from1.set(java.util.Calendar.MINUTE, 0);
        from1.set(java.util.Calendar.MILLISECOND,0);
        
        java.util.Calendar to1=java.util.Calendar.getInstance();
        to1.set(java.util.Calendar.DAY_OF_MONTH, 25);
        to1.set(java.util.Calendar.MONTH, java.util.Calendar.JUNE);
        to1.set(java.util.Calendar.YEAR, 2013);
        to1.set(java.util.Calendar.HOUR_OF_DAY, 10);
        to1.set(java.util.Calendar.MINUTE, 0);
        to1.set(java.util.Calendar.MILLISECOND,0);
        
        double val=Math.abs(cal.getWorkingDuration(from1.getTimeInMillis(), to1.getTimeInMillis()) - 2.5*60*60*1000);
        
        if (val > 10) {            
            fail("Should be 2.5 hours: "+cal.getWorkingDuration(from1.getTimeInMillis(), to1.getTimeInMillis()));          
        }
    }

    @Test
    public void testGetWorkingDurationAcrossMonthBoundary() {
        Calendar cal=new Calendar();
        
        Calendar.WorkingDay thursday=new Calendar.WorkingDay();
        thursday.setStartHour(9);
        thursday.setEndHour(17);
        thursday.setEndMinute(30);
        
        cal.setThursday(thursday);
        
        Calendar.WorkingDay friday=new Calendar.WorkingDay();
        friday.setStartHour(9);
        friday.setEndHour(17);
        friday.setEndMinute(30);
        
        cal.setFriday(friday);
        
        // Test across day boundary
        java.util.Calendar from1=java.util.Calendar.getInstance();
        from1.set(java.util.Calendar.DAY_OF_MONTH, 28);
        from1.set(java.util.Calendar.MONTH, java.util.Calendar.FEBRUARY);
        from1.set(java.util.Calendar.YEAR, 2013);
        from1.set(java.util.Calendar.HOUR_OF_DAY, 16);
        from1.set(java.util.Calendar.MINUTE, 0);
        from1.set(java.util.Calendar.MILLISECOND,0);
        
        java.util.Calendar to1=java.util.Calendar.getInstance();
        to1.set(java.util.Calendar.DAY_OF_MONTH, 1);
        to1.set(java.util.Calendar.MONTH, java.util.Calendar.MARCH);
        to1.set(java.util.Calendar.YEAR, 2013);
        to1.set(java.util.Calendar.HOUR_OF_DAY, 10);
        to1.set(java.util.Calendar.MINUTE, 0); 
        to1.set(java.util.Calendar.MILLISECOND,0);       

        if (cal.getWorkingDuration(from1.getTimeInMillis(), to1.getTimeInMillis()) != 2.5*60*60*1000) {            
            fail("Should be 2.5 hours: "+cal.getWorkingDuration(from1.getTimeInMillis(), to1.getTimeInMillis()));          
        }
    }

    @Test
    public void testGetWorkingDurationAcrossDayBoundaryWithExclusion() {
        Calendar cal=new Calendar();
        
        Calendar.WorkingDay monday=new Calendar.WorkingDay();
        monday.setStartHour(9);
        monday.setEndHour(17);
        monday.setEndMinute(30);
        
        cal.setMonday(monday);
        
        Calendar.WorkingDay tuesday=new Calendar.WorkingDay();
        tuesday.setStartHour(9);
        tuesday.setEndHour(17);
        tuesday.setEndMinute(30);
        
        cal.setTuesday(tuesday);
        
        Calendar.WorkingDay wednesday=new Calendar.WorkingDay();
        wednesday.setStartHour(9);
        wednesday.setEndHour(17);
        wednesday.setEndMinute(30);
        
        cal.setWednesday(wednesday);
        
        Calendar.ExcludedDay excluded=new Calendar.ExcludedDay();
        excluded.setDay(25);
        excluded.setMonth(6);
        excluded.setYear(2013);
        excluded.setReason("Public holiday");
        
        cal.getExcludedDays().add(excluded);
        
        // Test across day boundary
        java.util.Calendar from1=java.util.Calendar.getInstance();
        from1.set(java.util.Calendar.DAY_OF_MONTH, 24);
        from1.set(java.util.Calendar.MONTH, java.util.Calendar.JUNE);
        from1.set(java.util.Calendar.YEAR, 2013);
        from1.set(java.util.Calendar.HOUR_OF_DAY, 16);
        from1.set(java.util.Calendar.MINUTE, 0);
        from1.set(java.util.Calendar.MILLISECOND,0);
        
        java.util.Calendar to1=java.util.Calendar.getInstance();
        to1.set(java.util.Calendar.DAY_OF_MONTH, 26);
        to1.set(java.util.Calendar.MONTH, java.util.Calendar.JUNE);
        to1.set(java.util.Calendar.YEAR, 2013);
        to1.set(java.util.Calendar.HOUR_OF_DAY, 10);
        to1.set(java.util.Calendar.MINUTE, 0);
        to1.set(java.util.Calendar.MILLISECOND,0);
        
        if (cal.getWorkingDuration(from1.getTimeInMillis(), to1.getTimeInMillis()) != 2.5*60*60*1000) {            
            fail("Should be 2.5 hours: "+cal.getWorkingDuration(from1.getTimeInMillis(), to1.getTimeInMillis()));          
        }
    }

    @Test
    public void testGetWorkingDurationDefaultCalOneYear() {
        Calendar defcal = new Calendar().setName(Calendar.DEFAULT);
        
        // Define working week
        defcal.setMonday(new Calendar.WorkingDay().setStartHour(9).setEndHour(17));
        defcal.setTuesday(new Calendar.WorkingDay().setStartHour(9).setEndHour(17));
        defcal.setWednesday(new Calendar.WorkingDay().setStartHour(9).setEndHour(17));
        defcal.setThursday(new Calendar.WorkingDay().setStartHour(9).setEndHour(17));
        defcal.setFriday(new Calendar.WorkingDay().setStartHour(9).setEndHour(17));
        
        defcal.getExcludedDays().add(new Calendar.ExcludedDay().setDay(25).
                    setMonth(12).setReason("Christmas Day"));

        long duration=defcal.getWorkingDuration(1357065979362L, 1388515579362L);
        System.out.println("DURATION="+duration);
        
    }
}
