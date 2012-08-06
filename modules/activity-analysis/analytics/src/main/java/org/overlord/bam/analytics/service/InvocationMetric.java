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
package org.overlord.bam.analytics.service;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * This class represents invocation metric information, defining
 * the number of invocations, avg/min/max response times, and
 * percentage change values for each.
 *
 */
public class InvocationMetric implements java.io.Externalizable {

    private static final int VERSION = 1;

    private int _count=0;
    private int _countChange=0;
    private long _avg=0;
    private int _avgChange=0;
    private long _max=0;
    private int _maxChange=0;
    private long _min=0;
    private int _minChange=0;

    /**
     * The default constructor.
     */
    public InvocationMetric() {    
    }
    
    /**
     * This constructor initializes the invocation metric as an
     * aggregation of a supplied list of metrics.
     * 
     * @param metrics The metrics to aggregate
     */
    public InvocationMetric(java.util.List<InvocationMetric> metrics) { 
        
        if (metrics.size() > 0) {
            for (InvocationMetric m : metrics) {
                
                if (m.getCount() > 0) {
                    _count += m.getCount();
                    _countChange += (m.getCountChange() * m.getCount());
                    
                    _avg += (m.getAverage() * m.getCount());
                    _avgChange += (m.getAverageChange() * m.getCount());
                    
                    if (m.getMax() > _max) {
                        _max = m.getMax();
                    }
                    _maxChange += (m.getMaxChange() * m.getCount());
                    
                    if (_min == 0 || m.getMin() < _min) {
                        _min = m.getMin();
                    }
                    _minChange += (m.getMinChange() * m.getCount());
                }
            }
            
            if (_count != 0) {
                _countChange /= _count;
    
                _avg /= _count;
                _avgChange /= _count;
                
                _minChange /= _count;
                _maxChange /= _count;
            }
        }
    }
    
    /**
     * This method sets the count.
     * 
     * @param count The count
     */
    public void setCount(int count) {
        _count = count;
    }
    
    /**
     * This method returns the count.
     * 
     * @return The count
     */
    public int getCount() {
        return (_count);
    }
    
    /**
     * This method sets the average duration.
     * 
     * @param time The average duration
     */
    public void setAverage(long time) {
        _avg = time;
    }
    
    /**
     * This method returns the average duration.
     * 
     * @return The average duration
     */
    public long getAverage() {
        return (_avg);
    }
    
    /**
     * This method sets the maximum duration.
     * 
     * @param time The maximum duration
     */
    public void setMax(long time) {
        _max = time;
    }
    
    /**
     * This method returns the maximum duration.
     * 
     * @return The maximum duration
     */
    public long getMax() {
        return (_max);
    }
    
    /**
     * This method sets the minimum duration.
     * 
     * @param time The minimum duration
     */
    public void setMin(long time) {
        _min = time;
    }
    
    /**
     * This method returns the minimum duration.
     * 
     * @return The minimum duration
     */
    public long getMin() {
        return (_min);
    }
    
    /**
     * This method sets the count change (if applicable).
     * 
     * @param change The count change percentage
     */
    public void setCountChange(int change) {
        _countChange = change;
    }
    
    /**
     * This method returns the count change (if applicable).
     * 
     * @return The count change percentage
     */
    public int getCountChange() {
        return (_countChange);
    }
    
    /**
     * This method sets the average duration change (if applicable).
     * 
     * @param change The average duration change percentage
     */
    public void setAverageChange(int change) {
        _avgChange = change;
    }
    
    /**
     * This method returns the average duration change (if applicable).
     * 
     * @return The average duration change percentage
     */
    public int getAverageChange() {
        return (_avgChange);
    }
    
    /**
     * This method sets the maximum duration change (if applicable).
     * 
     * @param change The maximum duration change percentage
     */
    public void setMaxChange(int change) {
        _maxChange = change;
    }
    
    /**
     * This method returns the maximum duration change (if applicable).
     * 
     * @return The maximum duration change percentage
     */
    public int getMaxChange() {
        return (_maxChange);
    }
    
    /**
     * This method sets the minimum duration change (if applicable).
     * 
     * @param change The minimum duration change percentage
     */
    public void setMinChange(int change) {
        _minChange = change;
    }
    
    /**
     * This method returns the minimum duration change (if applicable).
     * 
     * @return The minimum duration change percentage
     */
    public int getMinChange() {
        return (_minChange);
    }
    
    /**
     * This method merges the supplied invocation metric
     * information.
     * 
     * @param metric The invocation metrics to merge
     */
    public void merge(InvocationMetric metric) {
        
        if (metric.getCount() > 0) {
            int myCount=getCount();
            int mergeCount=metric.getCount();
            
            setCount(myCount + mergeCount);
            
            setCountChange(((getCountChange() * myCount)
                    + (metric.getCountChange() * mergeCount))
                    / getCount());
            
            setAverage(((getAverage() * myCount)
                        + (metric.getAverage() * mergeCount))
                        / getCount());
            setAverageChange(((getAverageChange() * myCount)
                    + (metric.getAverageChange() * mergeCount))
                    / getCount());
            
            if (getMin() == 0 || metric.getMin() < getMin()) {
                setMin(metric.getMin());
            }
            setMinChange(((getMinChange() * myCount)
                    + (metric.getMinChange() * mergeCount))
                    / getCount());
            
            if (metric.getMax() > getMax()) {
                setMax(metric.getMax());
            }
            setMaxChange(((getMaxChange() * myCount)
                    + (metric.getMaxChange() * mergeCount))
                    / getCount());
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return ("InvocationMetric[count="+_count+"("+_countChange+"%) average="+_avg
                +"("+_avgChange+"%) min="+_min+"("+_minChange
                +"%) max="+_max+"("+_maxChange+"%)]");
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION);
        
        out.writeInt(_count);
        out.writeInt(_countChange);
        out.writeLong(_avg);
        out.writeInt(_avgChange);
        out.writeLong(_max);
        out.writeInt(_maxChange);
        out.writeLong(_min);
        out.writeInt(_minChange);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        in.readInt(); // Consume version, as not required for now
        
        _count = in.readInt();
        _countChange = in.readInt();
        _avg = in.readLong();
        _avgChange = in.readInt();
        _max = in.readLong();
        _maxChange = in.readInt();
        _min = in.readLong();
        _minChange = in.readInt();
    }
}
