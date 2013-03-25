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