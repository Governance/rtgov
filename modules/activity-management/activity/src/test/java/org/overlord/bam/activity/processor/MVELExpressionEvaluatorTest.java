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
package org.overlord.rtgov.activity.processor;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.activity.model.soa.RequestReceived;

public class MVELExpressionEvaluatorTest {

    private static final String TEST_MESSAGE_ID = "TestMID";

    @Test
    public void testEvaluate() {
        MVELExpressionEvaluator evaluator=new MVELExpressionEvaluator();
        
        RequestReceived rr=new RequestReceived();
        rr.setMessageId(TEST_MESSAGE_ID);
        
        evaluator.setExpression("messageId");
        
        try {
            evaluator.init();
        } catch (Exception e) {
            fail("Evaluator should have initialized: "+e);
        }
        
        String result=evaluator.evaluate(rr);
        
        if (result == null) {
            fail("Result is null");
        }
        
        if (!result.equals(TEST_MESSAGE_ID)) {
            fail("Unexpected result: "+result);
        }
    }

    @Test
    public void testEvaluateBadExpression() {
        MVELExpressionEvaluator evaluator=new MVELExpressionEvaluator();
        
        evaluator.setExpression("X X");
        
        try {
            evaluator.init();
            fail("Evaluator should NOT have initialized");
        } catch (Exception e) {
        }
    }

}
