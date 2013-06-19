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
package org.overlord.rtgov.reports;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.mvel2.MVEL;
import org.overlord.rtgov.reports.model.Report;

public class MVELReportGenerator extends ReportGenerator {
    
    private static final Logger LOG=Logger.getLogger(MVELReportGenerator.class.getName());
    
    private String _scriptLocation=null;
    private Object _scriptExpression=null;
    
    /**
     * {@inheritDoc}
     */
    public void init() throws Exception {
        super.init();
        
        // Load the script
        java.io.InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(_scriptLocation);
        
        if (is == null) {
            throw new Exception("Unable to locate MVEL script '"+_scriptLocation+"'");
        } else {
            byte[] b=new byte[is.available()];
            is.read(b);
            is.close();

            // Compile expression
            _scriptExpression = MVEL.compileExpression(new String(b));

            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Initialized script="+_scriptLocation
                        +" compiled="+_scriptExpression);
            }
        }
    }
    
    /**
     * This method returns the script location.
     * 
     * @return The script location
     */
    public String getScriptLocation() {
        return (_scriptLocation);
    }
    
    /**
     * This method sets the script location.
     * 
     * @param location The script location
     */
    public void setScriptLocation(String location) {
        _scriptLocation = location;
    }

    /**
     * {@inheritDoc}
     */
    public String getReportType() {
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public Class<? extends ReportDefinition> getReportDefinitionType() {
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public Report generate(ReportContext context, java.util.Map<String,Object> properties) throws Exception {
        
        if (_scriptExpression != null) {
            java.util.Map<String,Object> vars=
                    new java.util.HashMap<String, Object>();
            vars.put("context", context);
            vars.put("properties", properties);
                        
            return ((Report)MVEL.executeExpression(_scriptExpression, vars));
        }
        
        return (null);
    }
}
