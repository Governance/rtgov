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

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jaxen.SimpleNamespaceContext;

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
    
}
