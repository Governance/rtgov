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
package org.overlord.rtgov.epn.mvel;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.epn.mvel.MVELPredicate;

public class MVELPredicateTest {

    @Test
    public void testEvaluateScript() {
        MVELPredicate pred=new MVELPredicate();
        
        pred.setScript("script/Evaluate.mvel");
        
        try {
            pred.init();
        } catch (Exception e) {
            fail("Failed to initialize: "+e);
        }
        
        try {
            if (!pred.evaluate("Test")) {
                fail("Evaluation of string failed");
            }
        } catch (Exception e) {
            fail("Failed to process: "+e);
        }
        
        try {
            if (pred.evaluate(5)) {
                fail("Evaluation of integer failed");
            }
        } catch (Exception e) {
            fail("Failed to process: "+e);
        }
    }

    @Test
    public void testEvaluateExpression() {
        MVELPredicate pred=new MVELPredicate();
        
        pred.setExpression("event instanceof java.lang.String");
        
        try {
            pred.init();
        } catch (Exception e) {
            fail("Failed to initialize: "+e);
        }
        
        try {
            if (!pred.evaluate("Test")) {
                fail("Evaluation of string failed");
            }
        } catch (Exception e) {
            fail("Failed to process: "+e);
        }
        
        try {
            if (pred.evaluate(5)) {
                fail("Evaluation of integer failed");
            }
        } catch (Exception e) {
            fail("Failed to process: "+e);
        }
    }
}
