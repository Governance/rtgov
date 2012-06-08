/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-11, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.bam.epn.testdata;

import org.overlord.bam.epn.Predicate;

public class TestPredicate2 extends Predicate {

    private String _someProperty=null;
    
    public String getSomeProperty() {
        return(_someProperty);
    }
    
    public void setSomeProperty(String prop) {
        _someProperty = prop;
    }
    
    public boolean evaluate(Object arg0) {
        if (arg0 instanceof TestEvent2) {
            TestEvent2 te=(TestEvent2)arg0;
            
            return te.getValue() >= 20;
         }
        
        return false;
    }

}
