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
package org.overlord.rtgov.epn;

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
            String mesg=java.util.PropertyResourceBundle.getBundle(
                    "epn-core.Messages").getString("EPN-CORE-3");
            
            LOG.severe(mesg);
            
            throw new IllegalArgumentException(mesg, e);
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
                    return (Class.forName(desc.getName(), false, cl));
                }
            };
            
            _list = (java.util.List<? extends Serializable>)ois.readObject();
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Deserialized event list: "+_list);
            }
            
            ois.close();
            bais.close();
            
        } catch (Throwable e) {
            String mesg=java.util.PropertyResourceBundle.getBundle(
                    "epn-core.Messages").getString("EPN-CORE-4");
            
            LOG.severe(mesg);
            
            throw new IllegalArgumentException(mesg, e);
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
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Event list is null");
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
     * This method returns the event at the specified index.
     * 
     * @param index The index
     * @return The event
     * @throws IndexOutOfBoundsException Index is out of bounds
     */
    public Serializable get(int index) throws IndexOutOfBoundsException {
        if (_list != null) {
            return (_list.get(index));
        }
        return (null);
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

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return (_list == null ? "<Unresolved EventList>" : _list.toString());
    }
}
