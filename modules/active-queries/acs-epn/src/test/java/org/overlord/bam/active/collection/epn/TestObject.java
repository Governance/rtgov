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
package org.overlord.bam.active.collection.epn;

public class TestObject implements java.io.Serializable {
    
    private static final long serialVersionUID = -7280462159940869489L;

    private int _min=0;
    private int _max=0;
    private int _avg=0;
    
    private String _name=null;
    
    public TestObject() {
    }
    
    public TestObject(String name, int avg) {
        _name = name;
        _avg = avg;
    }
    
    public void setMin(int min) {
        _min = min;
    }
    
    public int getMin() {
        return (_min);
    }
    
    public void setMax(int max) {
        _max = max;
    }
    
    public int getMax() {
        return (_max);
    }
    
    public void setAvg(int avg) {
        _avg = avg;
    }

    public int getAvg() {
        return (_avg);
    }
    
    public void setName(String name) {
        _name = name;
    }
    
    public String getName() {
        return (_name);
    }
}
