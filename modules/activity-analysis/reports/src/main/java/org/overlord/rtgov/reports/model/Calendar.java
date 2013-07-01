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

/**
 * This class represents a calendar.
 *
 */
public class Calendar {
    
    /**
     * This constant represents the name of the default calendar.
     */
    public static final String DEFAULT="Default";

    private String _name=null;
    private java.util.TimeZone _timezone=null;
    private WorkingDay _monday=null;
    private WorkingDay _tuesday=null;
    private WorkingDay _wednesday=null;
    private WorkingDay _thursday=null;
    private WorkingDay _friday=null;
    private WorkingDay _saturday=null;
    private WorkingDay _sunday=null;
    
    private java.util.List<ExcludedDay> _excludedDays=
                    new java.util.ArrayList<Calendar.ExcludedDay>();
    
    /**
     * This method returns the name of the calendar.
     * 
     * @return The name
     */
    public String getName() {
        return (_name);
    }
    
    /**
     * This method sets the name of the calendar.
     * 
     * @param name The name
     * @return The calendar
     */
    public Calendar setName(String name) {
        _name = name;
        return (this);
    }
    
    /**
     * This method returns the optional timezone of the
     * calendar. If not defined, then must be supplied before
     * being used.
     * 
     * @return The timezone
     */
    public java.util.TimeZone getTimeZone() {
        return (_timezone);
    }
    
    /**
     * This method sets the timezone.
     * 
     * @param timezone The timezone
     * @return The calendar
     */
    public Calendar setTimeZone(java.util.TimeZone timezone) {
        _timezone = timezone;
        return (this);
    }
    
    /**
     * This method returns the working day for Monday.
     * 
     * @return The working day, or null if a non-working day
     */
    public WorkingDay getMonday() {
        return (_monday);
    }
    
    /**
     * This method sets the working day for Monday.
     * 
     * @param wd The working day, or null if a non-working day
     * @return The calendar
     */
    public Calendar setMonday(WorkingDay wd) {
        _monday = wd;
        return (this);
    }
    
    /**
     * This method returns the working day for Tuesday.
     * 
     * @return The working day, or null if a non-working day
     */
    public WorkingDay getTuesday() {
        return (_tuesday);
    }
    
    /**
     * This method sets the working day for Tuesday.
     * 
     * @param wd The working day, or null if a non-working day
     * @return The calendar
     */
    public Calendar setTuesday(WorkingDay wd) {
        _tuesday = wd;
        return (this);
    }
    
    /**
     * This method returns the working day for Wednesday.
     * 
     * @return The working day, or null if a non-working day
     */
    public WorkingDay getWednesday() {
        return (_wednesday);
    }
    
    /**
     * This method sets the working day for Wednesday.
     * 
     * @param wd The working day, or null if a non-working day
     * @return The calendar
     */
    public Calendar setWednesday(WorkingDay wd) {
        _wednesday = wd;
        return (this);
    }
    
    /**
     * This method returns the working day for Thursday.
     * 
     * @return The working day, or null if a non-working day
     */
    public WorkingDay getThursday() {
        return (_thursday);
    }
    
    /**
     * This method sets the working day for Thursday.
     * 
     * @param wd The working day, or null if a non-working day
     * @return The calendar
     */
    public Calendar setThursday(WorkingDay wd) {
        _thursday = wd;
        return (this);
    }
    
    /**
     * This method returns the working day for Friday.
     * 
     * @return The working day, or null if a non-working day
     */
    public WorkingDay getFriday() {
        return (_friday);
    }
    
    /**
     * This method sets the working day for Friday.
     * 
     * @param wd The working day, or null if a non-working day
     * @return The calendar
     */
    public Calendar setFriday(WorkingDay wd) {
        _friday = wd;
        return (this);
    }
    
    /**
     * This method returns the working day for Saturday.
     * 
     * @return The working day, or null if a non-working day
     */
    public WorkingDay getSaturday() {
        return (_saturday);
    }
    
    /**
     * This method sets the working day for Saturday.
     * 
     * @param wd The working day, or null if a non-working day
     * @return The calendar
     */
    public Calendar setSaturday(WorkingDay wd) {
        _saturday = wd;
        return (this);
    }
    
    /**
     * This method returns the working day for Sunday.
     * 
     * @return The working day, or null if a non-working day
     */
    public WorkingDay getSunday() {
        return (_sunday);
    }
    
    /**
     * This method sets the working day for Sunday.
     * 
     * @param wd The working day, or null if a non-working day
     * @return The calendar
     */
    public Calendar setSunday(WorkingDay wd) {
        _sunday = wd;
        return (this);
    }
    
    /**
     * This method returns the list of excluded days.
     * 
     * @return The excluded days
     */
    public java.util.List<ExcludedDay> getExcludedDays() {
        return (_excludedDays);
    }
    
    /**
     * This method sets the list of excluded days.
     * 
     * @param days The excluded days
     * @return The calendar
     */
    public Calendar setExcludedDays(java.util.List<ExcludedDay> days) {
        _excludedDays = days;
        return (this);
    }
    
    /**
     * This method initializes the calendar.
     * 
     * @throws Exception Failed to initialize
     */
    public void init() throws Exception {
        
        if (getTimeZone() == null) {
            throw new Exception("TimeZone has not been set");
        }
    }
    
    /**
     * This method returns the working day associated with the
     * supplied calendar.
     * 
     * @param cal The calendar
     * @return The working day, or null if not defined
     */
    protected WorkingDay findWorkingDay(java.util.Calendar cal) {
        int day=cal.get(java.util.Calendar.DAY_OF_WEEK);
        WorkingDay wd=null;
        
        switch (day) {
        case java.util.Calendar.SUNDAY:
            wd = getSunday();
            break;
        case java.util.Calendar.MONDAY:
            wd = getMonday();
            break;
        case java.util.Calendar.TUESDAY:
            wd = getTuesday();
            break;
        case java.util.Calendar.WEDNESDAY:
            wd = getWednesday();
            break;
        case java.util.Calendar.THURSDAY:
            wd = getThursday();
            break;
        case java.util.Calendar.FRIDAY:
            wd = getFriday();
            break;
        case java.util.Calendar.SATURDAY:
            wd = getSaturday();
            break;
        }
        
        return (wd);
    }
    
    /**
     * This method creates a calendar from the supplied date
     * time information and optionally configured timezone.
     * 
     * @param dateTime The date/time info
     * @return The calendar
     */
    protected java.util.Calendar createCalendar(long dateTime) {
        java.util.Calendar cal=java.util.Calendar.getInstance();
        
        if (getTimeZone() != null) {
            cal.setTimeZone(getTimeZone());
        }
        
        cal.setTimeInMillis(dateTime);
        
        return (cal);
    }
    
    /**
     * This method determines whether the supplied date/time
     * is part of the working day represented by this calendar.
     * 
     * @param dateTime The date/time to check
     * @return Whether the date/time is associated with a working day/time
     */
    public boolean isWorkingDateTime(long dateTime) {
        boolean ret=false;
        
        java.util.Calendar cal=createCalendar(dateTime);
        
        // Check if working day has been defined for day
        WorkingDay wd=findWorkingDay(cal);
        
        if (wd != null) {
            int dttime=(cal.get(java.util.Calendar.HOUR_OF_DAY) * 60)+cal.get(java.util.Calendar.MINUTE);
            int wdstarttime=(wd.getStartHour() * 60)+wd.getStartMinute();
            int wdendtime=(wd.getEndHour() * 60)+wd.getEndMinute();
            
            if (dttime >= wdstarttime && dttime < wdendtime) {
                ret = !isExcluded(cal);               
            }
        }
        
        return (ret);
    }
    
    /**
     * This method checks whether the supplied calendar is
     * associated with an excluded day.
     * 
     * @param cal The calendar
     * @return Whether associated with an excluded day
     */
    protected boolean isExcluded(java.util.Calendar cal) {
        boolean ret=false;
        
        // Check if on an excluded day
        // NOTE: Impl could be optimized with a hash set based
        // on the date, then just check if contained
        for (ExcludedDay ed : getExcludedDays()) {
            if (ed.isExcluded(cal)) {
                ret = true;
                break;
            }
        }

        return (ret);
    }
    
    /**
     * This method calculates the number of milliseconds of working time
     * during the supplied timestamps.
     * 
     * @param from The from timestamp
     * @param to The to timestamp
     * @return The duration in milliseconds
     */
    public long getWorkingDuration(long from, long to) {
        long ret=0;
        
        if (to > from) {
            java.util.Calendar fromDateTime=createCalendar(from);
            
            do {
                WorkingDay wd=findWorkingDay(fromDateTime);
                
                if (wd != null) {
                    // Check if excluded day
                    if (!isExcluded(fromDateTime)) {
                        // Check if need to move start time based on working day
                        if (wd.getStartHour() > fromDateTime.get(java.util.Calendar.HOUR_OF_DAY)) {
                            fromDateTime.set(java.util.Calendar.HOUR_OF_DAY, wd.getStartHour());
                            fromDateTime.set(java.util.Calendar.MINUTE, wd.getStartMinute());
                        } else if (wd.getStartHour() == fromDateTime.get(java.util.Calendar.HOUR_OF_DAY)
                                && wd.getStartMinute() > fromDateTime.get(java.util.Calendar.MINUTE)) {
                            fromDateTime.set(java.util.Calendar.MINUTE, wd.getStartMinute());
                        }
                        
                        long startOfDay=fromDateTime.getTimeInMillis();
                        
                        // Determine end time
                        fromDateTime.set(java.util.Calendar.HOUR_OF_DAY, wd.getEndHour());
                        fromDateTime.set(java.util.Calendar.MINUTE, wd.getEndMinute());
                        
                        long endOfDay=fromDateTime.getTimeInMillis();
                        
                        if (endOfDay > to) {
                            endOfDay = to;
                        }
                        
                        ret += (endOfDay - startOfDay);
                    }
                }    
                
                // Move the 'from' date/time to the start of the next day
                fromDateTime = createCalendar(fromDateTime.getTimeInMillis() + (24 * 60 * 60 * 1000));
                fromDateTime.set(java.util.Calendar.HOUR_OF_DAY, 0);
                fromDateTime.set(java.util.Calendar.MINUTE, 0);
                
            } while (fromDateTime.getTimeInMillis() < to);
        }
        
        return (ret);
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return ("Calendar[name="+_name+" timezone="+_timezone+"]");
    }
    
    /**
     * This class represents the start and end time associated
     * with a working day.
     *
     */
    public static class WorkingDay {
        private int _startHour=0;
        private int _startMinute=0;
        private int _endHour=0;
        private int _endMinute=0;
        
        /**
         * This method returns the start hour.
         * 
         * @return The start hour
         */
        public int getStartHour() {
            return (_startHour);
        }
        
        /**
         * This method sets the start hour.
         * 
         * @param val The start hour
         * @return The working day
         */
        public WorkingDay setStartHour(int val) {
            _startHour = val;
            return (this);
        }
        
        /**
         * This method returns the start minute.
         * 
         * @return The start minute
         */
        public int getStartMinute() {
            return (_startMinute);
        }
        
        /**
         * This method sets the start minute.
         * 
         * @param val The start minute
         * @return The working day
         */
        public WorkingDay setStartMinute(int val) {
            _startMinute = val;
            return (this);
        }
        
        /**
         * This method returns the end hour.
         * 
         * @return The end hour
         */
        public int getEndHour() {
            return (_endHour);
        }
        
        /**
         * This method sets the end hour.
         * 
         * @param val The end hour
         * @return The working day
         */
        public WorkingDay setEndHour(int val) {
            _endHour = val;
            return (this);
        }
        
        /**
         * This method returns the end minute.
         * 
         * @return The end minute
         */
        public int getEndMinute() {
            return (_endMinute);
        }
        
        /**
         * This method sets the end minute.
         * 
         * @param val The end minute
         * @return The working day
         */
        public WorkingDay setEndMinute(int val) {
            _endMinute = val;
            return (this);
        }
    }
    
    /**
     * This class represents a day excluded from the 
     * working calendar.
     *
     */
    public static class ExcludedDay {
        
        private int _day=0;
        private int _month=0;
        private int _year=0;
        
        private String _reason=null;
        
        /**
         * This method returns the day.
         * 
         * @return The day
         */
        public int getDay() {
            return (_day);
        }
        
        /**
         * This method sets the day.
         * 
         * @param val The day
         * @return The excluded day
         */
        public ExcludedDay setDay(int val) {
            _day = val;
            return (this);
        }
        
        /**
         * This method returns the month. January is
         * represented by 1, through to December which is
         * represented by 12.
         * 
         * @return The month
         */
        public int getMonth() {
            return (_month);
        }
        
        /**
         * This method sets the month. January is
         * represented by 1, through to December which is
         * represented by 12.
         * 
         * @param val The month
         * @return The excluded day
         */
        public ExcludedDay setMonth(int val) {
            _month = val;
            return (this);
        }
        
        /**
         * This method returns the year. If not defined,
         * then this represents an annually repeating
         * exclusion.
         * 
         * @return The year
         */
        public int getYear() {
            return (_year);
        }
        
        /**
         * This method sets the year.
         * 
         * @param val The year
         * @return The excluded day
         */
        public ExcludedDay setYear(int val) {
            _year = val;
            return (this);
        }
        
        /**
         * This method returns the reason for exclusion.
         * 
         * @return The reason
         */
        public String getReason() {
            return (_reason);
        }
        
        /**
         * This method sets the reason for exclusion.
         * 
         * @param val The reason
         * @return The excluded day
         */
        public ExcludedDay setReason(String val) {
            _reason = val;
            return (this);
        }
        
        /**
         * This method determines whether the supplied calendar
         * is associated with the excluded day.
         * 
         * @param cal The calendar
         * @return Whether associated with this excluded day
         */
        protected boolean isExcluded(java.util.Calendar cal) {
            boolean ret=false;
            
            if (cal.get(java.util.Calendar.DAY_OF_MONTH) == getDay()
                    && cal.get(java.util.Calendar.MONTH) == getMonth()-1
                    && (getYear() == 0 || cal.get(java.util.Calendar.YEAR) == getYear())) {
                ret = true;
            }
            return (ret);
        }
    }
}
