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
package org.overlord.bam.active.collection.predicate;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.bam.active.collection.predicate.MVEL;

public class MVELTest {

    @Test
    public void testPredicateTrue() {
        MVEL predicate=new MVEL();
        
        predicate.setExpression("serviceType == \"OrderService\" && operation == \"buy\"");
        
        TestObject to=new TestObject();
        
        to.setServiceType("OrderService");
        to.setOperation("buy");
        
        if (!predicate.evaluate(to)) {
            fail("Evaluation should be true");
        }
    }

    @Test
    public void testPredicateFalse() {
        MVEL predicate=new MVEL();
        
        predicate.setExpression("serviceType == \"OrderService\" && operation == \"buy\"");
        
        TestObject to=new TestObject();
        
        to.setServiceType("InventoryService");
        to.setOperation("lookup");
        
        if (predicate.evaluate(to)) {
            fail("Evaluation should be false");
        }
    }

    @Test
    public void testPredicateANDFalseWithMissingValue() {
        MVEL predicate=new MVEL();
        
        predicate.setExpression("serviceType == \"OrderService\" && operation == \"buy\"");
        
        TestObject to=new TestObject();
        
        to.setServiceType("OrderService");
        
        if (predicate.evaluate(to)) {
            fail("Evaluation should be false");
        }
    }

    @Test
    public void testPredicateORTrueWithMissingValue() {
        MVEL predicate=new MVEL();
        
        predicate.setExpression("serviceType == \"OrderService\" || operation == \"buy\"");
        
        TestObject to=new TestObject();
        
        to.setOperation("buy");
        
        if (!predicate.evaluate(to)) {
            fail("Evaluation should be true");
        }
    }

    public class TestObject {
        
        private String _serviceType=null;
        private String _operation=null;
        
        public void setServiceType(String type) {
            _serviceType = type;
        }
        
        public String getServiceType() {
            return (_serviceType);
        }
        
        public void setOperation(String op) {
            _operation = op;
        }
        
        public String getOperation() {
            return (_operation);
        }
    }
}
