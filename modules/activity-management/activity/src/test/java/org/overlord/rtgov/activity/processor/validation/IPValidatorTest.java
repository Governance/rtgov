/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-13, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.rtgov.activity.processor.validation;

import static org.junit.Assert.*;

import java.text.MessageFormat;

import org.junit.Test;
import org.overlord.rtgov.activity.processor.InformationProcessor;
import org.overlord.rtgov.activity.processor.TypeProcessor;
import org.overlord.rtgov.activity.processor.TypeProcessor.ContextEvaluator;
import org.overlord.rtgov.activity.processor.TypeProcessor.PropertyEvaluator;

public class IPValidatorTest {
	
	@Test
	public void testValidateInformationProcessor() {
		InformationProcessor ip=new InformationProcessor();
		ip.setName("network");
		ip.setVersion("version");
		
		TestListener l=new TestListener();
		
		if (!IPValidator.validateInformationProcessor(ip, l)) {
			fail("Should be valid");
		}
		
		String[] expected=new String[] {
		};
		
		validateIssues(expected, l.getIssues());
	}
	
	@Test
	public void testValidateEmptyInformationProcessor() {
		InformationProcessor ip=new InformationProcessor();
		
		TestListener l=new TestListener();
		
		if (IPValidator.validateInformationProcessor(ip, l)) {
			fail("Should be invalid");
		}
		
		String[] expected=new String[] {
				MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
	                    "activity.Messages").getString("ACTIVITY-14"),
	                    "Information Processor", "name"),
                MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
	                    "activity.Messages").getString("ACTIVITY-14"),
	                    "Information Processor", "version")
		};
		
		validateIssues(expected, l.getIssues());
	}

    @Test
    public void testValidateContextEvaluator() {
        InformationProcessor ip=new InformationProcessor();
        ip.setName("network");
        ip.setVersion("version");
        
        TypeProcessor tp=new TypeProcessor();
        ip.getTypeProcessors().put("tp", tp);
        
        ContextEvaluator ce=new ContextEvaluator();
        tp.getContexts().add(ce);
        
        ce.setTimeframe(100);
        
        TestListener l=new TestListener();
        
        if (IPValidator.validateContextEvaluators(ip, tp, l)) {
            fail("Should be invalid");
        }
        
        String[] expected=new String[] {
                java.util.PropertyResourceBundle.getBundle(
                        "activity.Messages").getString("ACTIVITY-15"),
                java.util.PropertyResourceBundle.getBundle(
                        "activity.Messages").getString("ACTIVITY-16")
        };
        
        validateIssues(expected, l.getIssues());
    }

    @Test
    public void testValidatePropertyEvaluator() {
        InformationProcessor ip=new InformationProcessor();
        ip.setName("network");
        ip.setVersion("version");
        
        TypeProcessor tp=new TypeProcessor();
        ip.getTypeProcessors().put("tp", tp);
        
        PropertyEvaluator pe=new PropertyEvaluator();
        tp.getProperties().add(pe);
        
        TestListener l=new TestListener();
        
        if (IPValidator.validatePropertyEvaluators(ip, tp, l)) {
            fail("Should be invalid");
        }
        
        String[] expected=new String[] {
                MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                        "activity.Messages").getString("ACTIVITY-14"),
                        "Property Evaluator", "name"),
                java.util.PropertyResourceBundle.getBundle(
                        "activity.Messages").getString("ACTIVITY-17")
        };
        
        validateIssues(expected, l.getIssues());
    }
    
	protected void validateIssues(String[] expected, java.util.List<String> issues) {
		String errmsg="";
		
		for (String exp : expected) {
			if (!issues.remove(exp)) {
				errmsg += "    -> Expected issue missing: "+exp+"\r\n";
			}
		}
		
		for (String issue : issues) {
			errmsg += "    -> Unxpected issue: "+issue+"\r\n";
		}
		
		if (errmsg.length() > 0) {
			fail("Errors detected:\r\n"+errmsg);
		}
	}

	public class TestListener implements IPValidationListener {

		private java.util.List<String> _issues=new java.util.Vector<String>();
		
		public void error(InformationProcessor ip, Object target, String issue) {
			_issues.add(issue);
		}
		
		public java.util.List<String> getIssues() {
			return (_issues);
		}
	}
}
