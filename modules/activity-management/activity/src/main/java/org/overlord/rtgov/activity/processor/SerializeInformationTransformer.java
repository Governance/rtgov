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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.dom.DOMSource;

import org.overlord.rtgov.activity.util.ActivityUtil;

/**
 * This class represents a serialization based information transformer.
 *
 */
public class SerializeInformationTransformer extends InformationTransformer {

	private static final Logger LOG=Logger.getLogger(SerializeInformationTransformer.class.getName());
	
    /**
     * {@inheritDoc}
     */
    public String transform(Object information) {
    	
    	if (information instanceof String) {
    		return ((String)information);
    		
    	} else {
    		if (information instanceof DOMSource) {
    			information = ((DOMSource)information).getNode();
    		}
    		
    		if (information instanceof org.w3c.dom.Node) {
	    		try {
					java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
					
					javax.xml.transform.dom.DOMSource source=
							new javax.xml.transform.dom.DOMSource((org.w3c.dom.Node)information);
					javax.xml.transform.stream.StreamResult result=
							new javax.xml.transform.stream.StreamResult(baos);
					
					javax.xml.transform.Transformer transformer=
							javax.xml.transform.TransformerFactory.newInstance().newTransformer();
			    		
					transformer.transform(source, result);
					
					return (baos.toString());
					
	    		} catch (Exception e) {
	    			LOG.log(Level.SEVERE, "Failed to transformer DOM information '"+information+"'", e);
	    		}    		
	
	    	} else {
	    		try {
	    			return (ActivityUtil.objectToJSONString(information));
	    		} catch (Exception e) {
	    			LOG.log(Level.SEVERE, "Failed to transformer information '"+information+"' to JSON", e);
	    		}
	    	}
    	}
    	
    	return null;
    }
    
}
