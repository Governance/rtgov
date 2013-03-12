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
package org.overlord.rtgov.activity.processor.xpath;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jaxen.SimpleNamespaceContext;
import org.overlord.rtgov.activity.processor.ExpressionEvaluator;

/**
 * This class represents a XPath based expression evaluator.
 *
 */
public class XPathExpressionEvaluator extends ExpressionEvaluator {
    
    private static final Logger LOG=Logger.getLogger(XPathExpressionEvaluator.class.getName());
    
    private org.jaxen.dom.DOMXPath _domXPath=null;
    private org.jaxen.javabean.JavaBeanXPath _beanXPath=null;
    
    private java.util.Map<String,String> _namespaces=new java.util.HashMap<String,String>();

    /**
     * This method sets the map of prefixes to namespaces.
     * 
     * @param namespaces The namespaces
     */
    public void setNamespaces(java.util.Map<String,String> namespaces) {
        _namespaces = namespaces;
    }
    
    /**
     * This method gets the map of prefixes to namespaces.
     * 
     * @return The namespaces
     */
    public java.util.Map<String,String> getNamespaces() {
        return (_namespaces);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void init() throws Exception {
        super.init();
        
        // Initialize the expression
        _domXPath = new org.jaxen.dom.DOMXPath(getExpression());
        _beanXPath = new org.jaxen.javabean.JavaBeanXPath(getExpression());
        
        if (_namespaces != null) {
            _domXPath.setNamespaceContext(new SimpleNamespaceContext(_namespaces));
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public String evaluate(Object information) {
        String ret=null;
        
        try {
        	boolean reparse=false;
        	
        	if (information instanceof javax.xml.transform.dom.DOMSource) {
        		information = ((javax.xml.transform.dom.DOMSource)information).getNode();
        		
        		if (LOG.isLoggable(Level.FINEST)) {
        			LOG.finest("Extracted node from DOMSource: "+information);
        		}
        		
        		// RTGOV-141 - workaround to overcome xpath evaluation issue
        		reparse = true;
        	}

            if (information instanceof String) {
                // Convert to DOM
                javax.xml.parsers.DocumentBuilderFactory factory=
                        javax.xml.parsers.DocumentBuilderFactory.newInstance();
                
                factory.setNamespaceAware(true);
                
                javax.xml.parsers.DocumentBuilder builder=
                        factory.newDocumentBuilder();
                
                java.io.InputStream is=
                        new java.io.ByteArrayInputStream(((String)information).getBytes());
                
                org.w3c.dom.Document doc=builder.parse(is);
                
                is.close();
                
                ret = _domXPath.stringValueOf(doc.getDocumentElement());
                
            } else if (information instanceof org.w3c.dom.Node) {
            	org.w3c.dom.Node node=(org.w3c.dom.Node)information;
            	
            	// Check if namespace aware
            	if (reparse || !isNamespaceAware(node)) {
            		
            		if (LOG.isLoggable(Level.FINEST)) {
            			LOG.finest("Converting non-namespace-aware node: "+node);
            		}
            		
            		java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
            		
            		javax.xml.transform.dom.DOMSource source=
            				new javax.xml.transform.dom.DOMSource(node);
            		javax.xml.transform.stream.StreamResult result=
            				new javax.xml.transform.stream.StreamResult(baos);
            		
            		javax.xml.transform.Transformer transformer=
            				javax.xml.transform.TransformerFactory.newInstance().newTransformer();
    	        		
            		transformer.transform(source, result);
            		
                    javax.xml.parsers.DocumentBuilderFactory factory=
                            javax.xml.parsers.DocumentBuilderFactory.newInstance();
                    
                    factory.setNamespaceAware(true);
                    
                    javax.xml.parsers.DocumentBuilder builder=
                            factory.newDocumentBuilder();
                    
                    java.io.InputStream is=
                            new java.io.ByteArrayInputStream(baos.toByteArray());
                    
                    org.w3c.dom.Document doc=builder.parse(is);
                    
                    is.close();
                    
                    information = doc.getDocumentElement();

            		if (LOG.isLoggable(Level.FINEST)) {
            			LOG.finest("Converted node: "+information);
            		}            		
            	}
            	
                ret = _domXPath.stringValueOf(information);
                
            } else {
                
                ret = _beanXPath.stringValueOf(information);
            }
            
            if (ret != null && ret.length() == 0) {
                ret = null;
            }
            
        } catch (Exception e) {
            LOG.log(Level.SEVERE, MessageFormat.format(
                    java.util.PropertyResourceBundle.getBundle(
                    "activity.Messages").getString("ACTIVITY-6"),
                        getExpression()), e);
        }
        
        return (ret);
    }
    
    /**
     * This method determines whether the node is namespace aware.
     * 
     * @param node The node
     * @return Whether the node is namespace aware
     */
    protected static boolean isNamespaceAware(org.w3c.dom.Node node) {
    	boolean ret=(node.getLocalName() != null);
    	
		if (LOG.isLoggable(Level.FINEST)) {
			LOG.finest("Is node "+node+" namespace aware? "+ret);
			LOG.finest("nodeName="+node.getNodeName());
			LOG.finest("localName="+node.getLocalName());
			LOG.finest("namespace="+node.getNamespaceURI());
		}  
		
    	return (ret);
    }
}
