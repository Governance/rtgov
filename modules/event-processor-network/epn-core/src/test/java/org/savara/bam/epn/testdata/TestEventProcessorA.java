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
package org.savara.bam.epn.testdata;

import java.io.Serializable;

import org.savara.bam.epn.EventProcessor;

public class TestEventProcessorA extends EventProcessor {

    private java.util.List<Serializable> _events=new java.util.Vector<Serializable>();
    private java.util.List<Serializable> _retries=new java.util.Vector<Serializable>();
    private boolean _forwardEvents=false;
    
    public void setForwardEvents(boolean b) {
        _forwardEvents = b;
    }
    
    public void retry(Serializable event) {
        _retries.add(event);
    }
    
    public Serializable process(String source, Serializable event,
            int retriesLeft) throws Exception {
        if (_retries.contains(event)) {
            _retries.remove(event);
            throw new Exception("Please retry this event");
        }

        _events.add(event);

        return _forwardEvents ? event : null;
    }
    
    public java.util.List<Serializable> getEvents() {
        return(_events);
    }
}
