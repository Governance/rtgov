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
import org.overlord.rtgov.activity.model.soa.ResponseSent;

public class MVELScriptEvaluatorTest {
    
    private static final String TEST_FAULT="TestFault";

    @Test
    public void testEvaluate() {
        MVELScriptEvaluator evaluator=new MVELScriptEvaluator();
        
        ResponseSent rs=new ResponseSent();
        
        evaluator.setExpression("activity.fault = \""+TEST_FAULT+"\"");
        
        try {
            evaluator.init();
        } catch (Exception e) {
            fail("Evaluator should have initialized: "+e);
        }
        
        evaluator.evaluate("", rs);
        
        if (!rs.getFault().equals(TEST_FAULT)) {
            fail("Unexpected fault: "+rs.getFault());
        }
    }

    @Test
    public void testEvaluateBadExpression() {
        MVELScriptEvaluator evaluator=new MVELScriptEvaluator();
        
        evaluator.setExpression("X X");
        
        try {
            evaluator.init();
            fail("Evaluator should NOT have initialized");
        } catch (Exception e) {
        }
    }

}
