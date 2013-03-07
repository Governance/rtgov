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
package org.overlord.rtgov.tests.epn;

import org.overlord.rtgov.ep.Predicate;

/**
 * This class provides the child predicate.
 *
 */
public class ChildPredicate extends Predicate {

    private int _min=0;
    private int _max=0;
    
    /**
     * This method sets the min.
     * 
     * @param min The min
     */
    public void setMin(int min) {
        _min = min;
    }
    
    /**
     * This method gets the min.
     * 
     * @return The min
     */
    public int getMin() {
        return (_min);
    }
    
    /**
     * This method sets the max.
     * 
     * @param max The max
     */
    public void setMax(int max) {
        _max = max;
    }
    
    /**
     * This method gets the max.
     * 
     * @return The max
     */
    public int getMax() {
        return (_max);
    }
    
    @Override
    public boolean evaluate(Object event) {
        return (event instanceof Obj2
                && ((Obj2)event).getValue() >= getMin()
                && ((Obj2)event).getValue() <= getMax());
    }

}
