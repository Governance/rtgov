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
package org.overlord.rtgov.service.dependency.presentation;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.overlord.rtgov.analytics.service.InvocationMetric;
import org.overlord.rtgov.common.util.RTGovConfig;
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

    @Inject @RTGovConfig
    private String _scriptLocation=null;
    private java.io.Serializable _scriptExpression=null;

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
