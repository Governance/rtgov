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
package org.overlord.bam.service.dependency.svg;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.overlord.bam.analytics.service.InvocationMetric;
import org.mvel2.MVEL;

public class MVELColourSelector implements ColourSelector {

    private static final Logger LOG=Logger.getLogger(MVELColourSelector.class.getName());

    private String _scriptLocation=null;
    private java.io.Serializable _scriptExpression=null;

    /**
     * This method sets the script location.
     * 
     * @param script The script location
     */
    public void setScriptLocation(String location) {
        _scriptLocation = location;
    }
    
    /**
     * This method initializes the colour selector.
     * 
     * @throws Exception Failed to initialize
     */
    public void init() throws Exception {
        
        // Only initialize if the script is specified, but not yet compiled
        if (_scriptLocation != null && _scriptExpression == null) {
            java.io.InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(_scriptLocation);
            
            if (is == null) {
                throw new Exception("Unable to locate '"+_scriptLocation+"'");
            } else {
                byte[] b=new byte[is.available()];
                is.read(b);
                is.close();

                // Compile expression
                _scriptExpression = MVEL.compileExpression(new String(b));

                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Initialized description script="+_scriptLocation
                            +" compiled="+_scriptExpression);
                }
            }
        }
    }
    
    /**
     * This method returns the colour relevant for the supplied
     * metric.
     * 
     * @param component The source component
     * @param metric The metric
     * @return The colour code
     */
    public String getColour(Object component, InvocationMetric metric) {
        String ret=null;
        
        if (_scriptExpression != null) {
            java.util.Map<String,Object> vars=
                    new java.util.HashMap<String, Object>();

            vars.put("component", component);
            vars.put("metric", metric);
            
            ret = (String)MVEL.executeExpression(_scriptExpression, vars);
        }

        return (ret);
    }

}
