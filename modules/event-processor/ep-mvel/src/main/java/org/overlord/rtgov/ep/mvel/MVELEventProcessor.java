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
package org.overlord.rtgov.ep.mvel;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.mvel2.MVEL;
import org.overlord.rtgov.ep.EventProcessor;
import org.overlord.rtgov.internal.ep.DefaultEPContext;

/**
 * This class represents the MVEL implementation of the Event
 * Processor.
 *
 */
public class MVELEventProcessor extends EventProcessor {

    private static final Logger LOG=Logger.getLogger(MVELEventProcessor.class.getName());

    private DefaultEPContext _context=null;

    private String _script=null;
    private Object _scriptExpression=null;

    /**
     * {@inheritDoc}
     */
    public void init() throws Exception {
        super.init();
        
        // Load the script
        java.io.InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(_script);
        
        if (is == null) {
            throw new Exception("Unable to locate MVEL script '"+_script+"'");
        } else {
            byte[] b=new byte[is.available()];
            is.read(b);
            is.close();

            // Compile expression
            _scriptExpression = MVEL.compileExpression(new String(b));

            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Initialized script="+_script
                        +" compiled="+_scriptExpression);
            }
        }
        
        _context = new DefaultEPContext(getServices(), getParameters());
    }
    
    /**
     * This method returns the script.
     * 
     * @return The script
     */
    public String getScript() {
        return (_script);
    }
    
    /**
     * This method sets the script.
     * 
     * @param script The script
     */
    public void setScript(String script) {
        _script = script;
    }
    
    /**
     * {@inheritDoc}
     */
    public java.io.Serializable process(String source,
                java.io.Serializable event, int retriesLeft) throws Exception {
        java.io.Serializable ret=null;
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Process event '"+event+" from source '"+source
                    +"' on MVEL Event Processor '"+getScript()
                    +"'");
        }

        if (_scriptExpression != null) {
            java.util.Map<String,Object> vars=
                    new java.util.HashMap<String, Object>();
            
            vars.put("source", source);
            vars.put("event", event);
            vars.put("retriesLeft", retriesLeft);
            vars.put("epc", _context);
            
            synchronized (this) {
                _context.handle(null);

                MVEL.executeExpression(_scriptExpression, vars);

                ret = (java.io.Serializable)_context.getResult();
            }

            if (ret instanceof Exception) {
                throw (Exception)ret;
            }
        }

        return (ret);
    }

}
