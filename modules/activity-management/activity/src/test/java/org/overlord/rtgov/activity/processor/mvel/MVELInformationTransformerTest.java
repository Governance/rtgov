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
package org.overlord.rtgov.activity.processor.mvel;

import static org.junit.Assert.*;

import javax.xml.parsers.DocumentBuilderFactory;

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
    public void testTransformNonString1() {
        MVELInformationTransformer transformer=new MVELInformationTransformer();
        
        transformer.setExpression("information.bool");
        
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
        
        if (!value.equals("true")) {
            fail("Value mismatch: "+value+" not equal true");
        }
    }
    
    @Test
    public void testTransformNonString2() {
        MVELInformationTransformer transformer=new MVELInformationTransformer();
        
        transformer.setExpression("information");
        
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
        
        if (!value.equals(info.toString())) {
            fail("Value mismatch: "+value+" not equal "+info.toString());
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
 
    @Test
    public void testTransformDOM() {
        org.w3c.dom.Document doc=null;
        
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            
            org.w3c.dom.Element elem=doc.createElement("test");
            doc.appendChild(elem);
            
            elem.appendChild(doc.createTextNode("This is a test"));
            
        } catch (Exception e) {
            fail("Failed to build DOM: "+e);
        }
        
        MVELInformationTransformer transformer=new MVELInformationTransformer();
        
        transformer.setExpression("import javax.xml.transform.TransformerFactory; "+
                "import javax.xml.transform.Transformer; "+
                "import javax.xml.transform.dom.DOMSource; "+
                "import javax.xml.transform.stream.StreamResult; "+
                "import java.io.ByteArrayOutputStream; "+
                "ByteArrayOutputStream out = new ByteArrayOutputStream();"+
                "DOMSource source = new DOMSource(information);"+
                "StreamResult result = new StreamResult(out);"+
                "TransformerFactory transFactory = TransformerFactory.newInstance();"+
                "Transformer transformer = transFactory.newTransformer();"+
                "transformer.transform(source, result);"+
                "out.close();"+
                "return new String(out.toByteArray());");
        
        try {
            transformer.init();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Evaluator should have initialized: "+e);
        }
        
        String value=transformer.transform(doc);
        
        if (value == null) {
            fail("Null value returned");
        }
        
        if (!value.contains("<test>This is a test</test>")) {
            fail("Value does not contain original document information: "+value);
        }
    }



    public class TestInfo {
    	public String value="Hello";
    	public boolean bool=true;
    	
    	public String toString() {
    	    return ("TestInfo");
    	}
    }
}
