/*
 * 2012-3 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
