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
package org.overlord.rtgov.service.dependency.presentation;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.overlord.rtgov.analytics.service.InvocationMetric;
import org.overlord.rtgov.common.util.RTGovProperties;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

/**
 * This class provides a MVEL based implementation of the severity
 * analyzer algorithm.
 *
 */
public class MVELSeverityAnalyzer implements SeverityAnalyzer {

    private static final String DEFAULT_SEVERITY_ANALYZER_SCRIPT = "SeverityAnalyzer.mvel";

    private static final Logger LOG=Logger.getLogger(MVELSeverityAnalyzer.class.getName());

    private String _scriptLocation=null;
    private java.io.Serializable _scriptExpression=null;

    /**
     * The default constructor.
     */
    public MVELSeverityAnalyzer() {
        _scriptLocation = RTGovProperties.getProperty("MVELSeverityAnalyzer.scriptLocation");
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
     * This method returns the script location.
     * 
     * @return The script location
     */
    public String getScriptLocation() {
        return (_scriptLocation == null ? DEFAULT_SEVERITY_ANALYZER_SCRIPT : _scriptLocation);
    }
    
    /**
     * This method initializes the color selector.
     * 
     * @throws Exception Failed to initialize
     */
    @SuppressWarnings("resource")
    @PostConstruct
    public void init() throws Exception {
        
        // Only initialize if the script is specified, but not yet compiled
        if (getScriptLocation() != null && _scriptExpression == null) {
            java.io.InputStream is=null;
            
            java.io.File f=new java.io.File(getScriptLocation());
            
            if (f.isAbsolute()) {
                is = new java.io.FileInputStream(f);
            } else {
                is = Thread.currentThread().getContextClassLoader().getResourceAsStream(getScriptLocation());
            }
            
            if (is == null) {
                throw new Exception("Unable to locate '"+getScriptLocation()+"'");
            }
            
            byte[] b=new byte[is.available()];
            is.read(b);
            is.close();
            
            ParserContext context = new ParserContext();
            context.addImport(Severity.class);

            // Compile expression
            _scriptExpression = MVEL.compileExpression(new String(b), context);

            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Initialized severity analyzer script="+getScriptLocation()
                        +" compiled="+_scriptExpression);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Severity getSeverity(Object component, InvocationMetric summary,
                        java.util.List<InvocationMetric> history) {
        Severity ret=Severity.Normal;
        
        if (_scriptExpression != null) {
            java.util.Map<String,Object> vars=
                    new java.util.HashMap<String, Object>();

            vars.put("component", component);
            vars.put("summary", summary);
            vars.put("history", history);
            if (history.size() > 0) {
                vars.put("latest", history.get(history.size()-1));
            }
            
            ret = (Severity)MVEL.executeExpression(_scriptExpression, vars);
        }

        return (ret);
    }

}
