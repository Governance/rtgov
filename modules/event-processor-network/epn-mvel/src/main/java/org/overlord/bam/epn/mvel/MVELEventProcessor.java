/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-11, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.bam.epn.mvel;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.mvel2.MVEL;
import org.overlord.bam.epn.EventProcessor;
import org.overlord.bam.internal.epn.DefaultEPNContext;

/**
 * This class represents the MVEL implementation of the Event
 * Processor.
 *
 */
public class MVELEventProcessor extends EventProcessor {

    private static final Logger LOG=Logger.getLogger(MVELEventProcessor.class.getName());

    private DefaultEPNContext _context=null;

    private String _script=null;
    private Object _scriptExpression=null;

    /**
     * {@inheritDoc}
     */
    public void init() throws Exception {
        
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
        
        _context = new DefaultEPNContext(getServices());
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
            vars.put("epn", _context);

            MVEL.executeExpression(_scriptExpression, vars);
            
            ret = (java.io.Serializable)_context.getResult();
        }

        return (ret);
    }

}
