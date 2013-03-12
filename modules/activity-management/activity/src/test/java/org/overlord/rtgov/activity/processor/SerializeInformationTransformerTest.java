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
package org.overlord.rtgov.activity.processor;

import static org.junit.Assert.*;

import javax.xml.transform.dom.DOMSource;

import org.junit.Test;
import org.overlord.rtgov.activity.util.ActivityUtil;

public class SerializeInformationTransformerTest {

	@Test
	public void testSerializeString() {
		SerializeInformationTransformer transformer=new SerializeInformationTransformer();
		
		String info="Hello";
		
		String result=transformer.transform(info);
		
		if (result == null) {
			fail("Result is null");
		}
		
		if (!result.equals(info)) {
			fail("Result mismatch: "+info+" but got "+result);
		}
	}

	@Test
	public void testSerializeDOM() {
		SerializeInformationTransformer transformer=new SerializeInformationTransformer();
		
		String info="<?xml version=\"1.0\" encoding=\"UTF-8\"?><hello/>";
		org.w3c.dom.Document doc=null;
		
		try {
	        javax.xml.parsers.DocumentBuilderFactory factory=
	                javax.xml.parsers.DocumentBuilderFactory.newInstance();
	        
	        factory.setNamespaceAware(true);
	        
	        javax.xml.parsers.DocumentBuilder builder=
	                factory.newDocumentBuilder();
	        
	        java.io.InputStream is=
	                new java.io.ByteArrayInputStream(info.getBytes());
	        
	        doc = builder.parse(is);

	        is.close();
		} catch (Exception e) {
			fail("Failed to build dom: "+e);
		}
		
		String result=transformer.transform(doc.getDocumentElement());
		
		if (result == null) {
			fail("Result is null");
		}
		
		if (!result.equals(info)) {
			fail("Result mismatch: "+info+" but got "+result);
		}
	}
	
	@Test
	public void testSerializeDOMSource() {
		SerializeInformationTransformer transformer=new SerializeInformationTransformer();
		
		String info="<?xml version=\"1.0\" encoding=\"UTF-8\"?><hello/>";
		org.w3c.dom.Document doc=null;
		
		try {
	        javax.xml.parsers.DocumentBuilderFactory factory=
	                javax.xml.parsers.DocumentBuilderFactory.newInstance();
	        
	        factory.setNamespaceAware(true);
	        
	        javax.xml.parsers.DocumentBuilder builder=
	                factory.newDocumentBuilder();
	        
	        java.io.InputStream is=
	                new java.io.ByteArrayInputStream(info.getBytes());
	        
	        doc = builder.parse(is);

	        is.close();
		} catch (Exception e) {
			fail("Failed to build dom: "+e);
		}
		
		String result=transformer.transform(new DOMSource(doc.getDocumentElement()));
		
		if (result == null) {
			fail("Result is null");
		}
		
		if (!result.equals(info)) {
			fail("Result mismatch: "+info+" but got "+result);
		}
	}
	
	@Test
	public void testSerializeObject() {
		SerializeInformationTransformer transformer=new SerializeInformationTransformer();
		
		TestObject tobj=new TestObject();
		
		String info=null;
		
		try {
			info = ActivityUtil.objectToJSONString(tobj);
		} catch (Exception e) {
			fail("Failed to get json: "+e);
		}
		
		String result=transformer.transform(tobj);
		
		if (result == null) {
			fail("Result is null");
		}
		
		if (!result.equals(info)) {
			fail("Result mismatch: "+info+" but got "+result);
		}
	}
	
	public static class TestObject {
		
		public TestObject() {
		}
		
		public String getName() {
			return ("Hello");
		}
		
		public String getValue() {
			return ("World");
		}
	}
}
