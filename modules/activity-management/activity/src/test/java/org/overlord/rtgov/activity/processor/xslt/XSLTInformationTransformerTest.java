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
package org.overlord.rtgov.activity.processor.xslt;

import static org.junit.Assert.*;

import org.junit.Test;

public class XSLTInformationTransformerTest {
    
    @Test
    public void testTransformXmlText() {
        XSLTInformationTransformer transformer=new XSLTInformationTransformer();
        
        transformer.setStyleSheet("xslt/cdcatalog.xsl");
        
        try {
            transformer.init();
        } catch (Exception e) {
            fail("Evaluator should have initialized: "+e);
        }
        
        String xml="<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\r\n"+
				"<catalog>\r\n"+
				"  <cd>\r\n"+
				"    <title>Empire Burlesque</title>\r\n"+
				"    <artist>Bob Dylan</artist>\r\n"+
				"    <country>USA</country>\r\n"+
				"    <company>Columbia</company>\r\n"+
				"    <price>10.90</price>\r\n"+
				"    <year>1985</year>\r\n"+
				"  </cd>\r\n"+
				"</catalog>";
        
        String value=transformer.transform(xml);
        
        if (value == null) {
            fail("Null value returned");
        }
        
        if (!value.startsWith("<html>")) {
        	fail("Expecting html");
        }
    }
    
    @Test
    public void testTransformXmlDOM() {
        XSLTInformationTransformer transformer=new XSLTInformationTransformer();
        
        transformer.setStyleSheet("xslt/cdcatalog.xsl");
        
        try {
            transformer.init();
        } catch (Exception e) {
            fail("Evaluator should have initialized: "+e);
        }
        
        String xml="<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\r\n"+
				"<catalog>\r\n"+
				"  <cd>\r\n"+
				"    <title>Empire Burlesque</title>\r\n"+
				"    <artist>Bob Dylan</artist>\r\n"+
				"    <country>USA</country>\r\n"+
				"    <company>Columbia</company>\r\n"+
				"    <price>10.90</price>\r\n"+
				"    <year>1985</year>\r\n"+
				"  </cd>\r\n"+
				"</catalog>";
        org.w3c.dom.Document doc=null;
        
        try {
	        javax.xml.parsers.DocumentBuilderFactory factory=
	                javax.xml.parsers.DocumentBuilderFactory.newInstance();
	        
	        factory.setNamespaceAware(true);
	        
	        javax.xml.parsers.DocumentBuilder builder=
	                factory.newDocumentBuilder();
	        
	        java.io.InputStream is=
	                new java.io.ByteArrayInputStream(xml.getBytes());
	        
	        doc = builder.parse(is);
	        
	        is.close();
        } catch (Exception e) {
        	fail("Failed to build DOM: "+e);
        }
        
        String value=transformer.transform(doc.getDocumentElement());
        
        if (value == null) {
            fail("Null value returned");
        }
        
        if (!value.startsWith("<html>")) {
        	fail("Expecting html");
        }
    }
}