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
package org.overlord.rtgov.activity.processor.mvel;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.activity.processor.mvel.MVELInformationTransformer;

public class MVELInformationTransformerTest {
    
    @Test
    public void testTransform() {
        MVELInformationTransformer transformer=new MVELInformationTransformer();
        
        transformer.setExpression("information.value");
        
        try {
            transformer.init();
        } catch (Exception e) {
            fail("Evaluator should have initialized: "+e);
        }
        
        TestInfo info=new TestInfo();
        
        String value=transformer.transform(info);
        
        if (value == null) {
            fail("Null value returned");
        }
        
        if (!value.equals(info.value)) {
        	fail("Value mismatch: "+value+" not equal "+info.value);
        }
    }

    @Test
    public void testEvaluateBadExpression() {
    	MVELInformationTransformer evaluator=new MVELInformationTransformer();
        
        evaluator.setExpression("X X");
        
        try {
            evaluator.init();
            fail("Transformer should NOT have initialized");
        } catch (Exception e) {
        }
    }

    public class TestInfo {
    	public String value="Hello";
    }
}
