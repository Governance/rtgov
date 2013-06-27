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
