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
package org.overlord.bam.activity.processor;

import org.overlord.bam.activity.model.ActivityType;

/**
 * This class provides an information processor implementation that is
 * based on a set of expressions used to extract relevant data from
 * the supplied information.
 *
 */
public class ExpressionInformationProcessor implements InformationProcessor {

    private String _name=null;
    private String _version=null;
    private java.util.Map<String,TypeProcessor> _typeProcessors=
            new java.util.HashMap<String,TypeProcessor>();
    
    /**
     * {@inheritDoc}
     */
    public String getName() {
        return (_name);
    }
    
    /**
     * This method sets the name of the information processor.
     * 
     * @param name The name
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * {@inheritDoc}
     */
    public String getVersion() {
        return (_version);
    }

    /**
     * This method sets the version of the information processor.
     * 
     * @param version The version
     */
    public void setVersion(String version) {
        _version = version;
    }
    
    /**
     * The map of types to processors.
     * 
     * @return The type to processor map
     */
    public java.util.Map<String, TypeProcessor> getTypeProcessors() {
        return (_typeProcessors);
    }

    /**
     * {@inheritDoc}
     */
    public void init() throws Exception {
        for (TypeProcessor tp : _typeProcessors.values()) {
            tp.init();
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSupported(String type) {
        return (_typeProcessors.containsKey(type));
    }

    /**
     * {@inheritDoc}
     */
    public String process(String type, Object info, ActivityType actType) {
        TypeProcessor processor=_typeProcessors.get(type);
        
        if (processor != null) {
            // Process the context and property details
            return (processor.process(info, actType));
        }
        
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws Exception {
        for (TypeProcessor tp : _typeProcessors.values()) {
            tp.close();
        }
    }

}
