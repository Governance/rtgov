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
package org.savara.bam.epn;

import java.io.IOException;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a list of events that can be serialized.
 *
 */
public class EventList implements java.io.Serializable, java.lang.Iterable<java.io.Serializable> {

    private static final long serialVersionUID = 4108141437156875407L;

    private transient java.util.List<? extends java.io.Serializable> _list=null;
    private byte[] _serializedList=null;
    
    private static final Logger LOG=Logger.getLogger(EventList.class.getName());
    
    /**
     * The default constructor.
     */
    public EventList() {
    }
    
    /**
     * This method represents a constructor to initialize the event list
     * from a standard Java list.
     * 
     * @param list The list of events
     */
    public EventList(java.util.List<? extends java.io.Serializable> list) {
        _list = list;
        
        try {
            java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
            java.io.ObjectOutputStream oos=new java.io.ObjectOutputStream(baos);
            
            oos.writeObject(list);
            
            oos.close();
            baos.close();
            
            _serializedList = baos.toByteArray();
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Serialized event list: "+_list);
            }
        } catch (Throwable e) {
            LOG.severe("Failed to serialize the supplied list");
            
            throw new IllegalArgumentException("Failed to serialize the supplied event list", e);
        }
    }

    /**
     * This method resolves the contained list.
     * 
     * @param cl The classloader
     */
    @SuppressWarnings("unchecked")
    protected void resolve(final java.lang.ClassLoader cl) {
        
        try {
            java.io.ByteArrayInputStream bais=new java.io.ByteArrayInputStream(_serializedList);
            java.io.ObjectInputStream ois=new java.io.ObjectInputStream(bais) {
                protected Class<?> resolveClass(ObjectStreamClass desc)
                        throws IOException, ClassNotFoundException {
                    return(Class.forName(desc.getName(), false, cl));
                }
            };
            
            _list = (java.util.List<? extends Serializable>)ois.readObject();
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Deserialized event list: "+_list);
            }
            
            ois.close();
            bais.close();
            
        } catch (Throwable e) {
            LOG.severe("Failed to deserialize the event list");
            
            throw new IllegalArgumentException("Failed to deserialize the event list", e);
        }
    }
    
    /**
     * This method resets the contents so they are no longer available
     * until resolved under another classloader.
     * 
     */
    protected void reset() {
        _list = null;
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Iterator<Serializable> iterator() {
        if (_list != null) {
            return ((Iterator<Serializable>)_list.iterator());
        }
        return (null);
    }
    
    /**
     * This method determines whether the event is contained
     * within the list.
     * 
     * @param evt The event
     * @return Whether the event is contained in the list
     */
    public boolean contains(Serializable evt) {
        return (_list.contains(evt));
    }
    
    /**
     * This method returns the number of events.
     * 
     * @return The number of events
     */
    public int size() {
        if (_list != null) {
            return (_list.size());
        }
        return (0);
    }

}
