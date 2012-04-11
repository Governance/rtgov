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
import java.util.List;

import org.savara.bam.epn.NodeListener;

public class TestNodeListener implements NodeListener {

    private java.util.List<Entry> _entries=new java.util.Vector<Entry>();
    
    public void eventsProcessed(String network, String node, List<Serializable> events) {
        _entries.add(new Entry(network, node, events));
    }

    public java.util.List<Entry> getEntries() {
        return (_entries);
    }
    
    public class Entry {
        private String _network=null;
        private String _node=null;
        private List<Serializable> _events=null;
        
        public Entry(String network, String node, List<Serializable> events) {
            _network = network;
            _node = node;
            _events = events;
        }
        
        public String getNetwork() {
            return(_network);
        }
        
        public String getNode() {
            return(_node);
        }
        
        public List<Serializable> getEvents() {
            return(_events);
        }
    }
}
