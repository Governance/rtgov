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

import org.overlord.bam.epn.EventProcessor;
import org.overlord.bam.epn.EPNContext;

/**
 * This class represents the MVEL implementation of the Event
 * Processor.
 *
 */
public class MVELEventProcessor extends EventProcessor {

    private static final Logger LOG=Logger.getLogger(MVELEventProcessor.class.getName());

    private String _script=null;

    /**
     * {@inheritDoc}
     */
    public void init() throws Exception {
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
     * This method gets the EPN context used by the CEP rule engine.
     * 
     * @return The EPN context
     */
    public EPNContext getEPNContext() {
        return (null);
    }

    /**
     * {@inheritDoc}
     */
    public java.io.Serializable process(String source,
                java.io.Serializable event, int retriesLeft) throws Exception {

        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Process event '"+event+" from source '"+source
                    +"' on MVEL Event Processor '"+getScript()
                    +"'");
        }

        
        return (null);
    }

}
