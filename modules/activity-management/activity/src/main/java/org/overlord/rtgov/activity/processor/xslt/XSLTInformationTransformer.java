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
package org.overlord.rtgov.activity.processor.xslt;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.overlord.rtgov.activity.processor.InformationTransformer;

/**
 * This class represents a XSLT based information transformer.
 *
 */
public class XSLTInformationTransformer extends InformationTransformer {

    private String _styleSheet=null;
    
    private Transformer _transformer=null;
    
    private static final Logger LOG=Logger.getLogger(XSLTInformationTransformer.class.getName());
    
    /**
     * This method returns the stylesheet.
     * 
     * @return The stylesheet
     */
    public String getStyleSheet() {
        return (_styleSheet);
    }
    
    /**
     * This method sets the stylesheet.
     * 
     * @param stylesheet The stylesheet
     */
    public void setStyleSheet(String stylesheet) {
        _styleSheet = stylesheet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() throws Exception {
        super.init();
        
        if (_transformer == null) {
            // Initialize the XSLT style sheet
            TransformerFactory factory = TransformerFactory.newInstance();
            
            java.io.InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(_styleSheet);
            
            if (is == null) {
                LOG.severe(MessageFormat.format(
                        java.util.PropertyResourceBundle.getBundle(
                        "activity.Messages").getString("ACTIVITY-9"),_styleSheet));
            } else {
                Source xslt = new StreamSource(is);
                
                _transformer = factory.newTransformer(xslt);
                
                is.close();
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public String transform(Object information) {   
        
        if (_transformer == null) {
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Transformer does not exist for information: "+information);
            }
            return (null);
        }
        
        Source source=null;
        
        if (information instanceof String) {
            source = new StreamSource(new java.io.ByteArrayInputStream(((String)information).getBytes()));
        } else if (information instanceof org.w3c.dom.Node) {
            source = new DOMSource((org.w3c.dom.Node)information);
        }
        
        java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
        StreamResult target=new StreamResult(baos);
        
        try {
            _transformer.transform(source, target);
            
            baos.flush();
            baos.close();
            
            return (baos.toString());
            
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Transformation failed", e);
        }
                
        return (null);
    }
    
}
