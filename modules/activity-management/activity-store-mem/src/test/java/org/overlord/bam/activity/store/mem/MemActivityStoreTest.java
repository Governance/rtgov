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
package org.overlord.bam.activity.store.mem;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.bam.activity.model.soa.RequestSent;
import org.overlord.bam.activity.server.QuerySpec;

public class MemActivityStoreTest {

    @Test
    public void testEvaluateField() {
        RequestSent activity=new RequestSent();
        
        activity.setOperation("myOp");
        
        if (!MemActivityStore.evaluate(activity,
                new QuerySpec().setFormat("mvel").
                            setExpression("operation == 'myOp'"))) {
            fail("Failed to evaluate operation");
        }
    }

    @Test
    public void testEvaluateProperty() {
        RequestSent activity=new RequestSent();
        
        activity.getProperties().put("testKey", "testValue");
        
        if (!MemActivityStore.evaluate(activity,
                new QuerySpec().setFormat("mvel").
                            setExpression("properties.containsKey('testKey') && "
                                 +"properties.get('testKey').equals('testValue')"))) {
            fail("Failed to evaluate property");
        }
    }

}
