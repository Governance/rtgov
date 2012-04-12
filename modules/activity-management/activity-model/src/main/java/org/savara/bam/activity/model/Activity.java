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
package org.savara.bam.activity.model;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

// NOTE: Still need to be able to record business transaction/session id information
// somewhere, possibly derived from MessageExchange data - should this info be
// located with the MessageExchange as it is only derived for those activity types?
// and then associated with the service/process ids locally to relate to other
// activity events? Or could have a Session component at a higher level?

/**
 * This class represents an activity event.
 *
 */
public class Activity implements java.io.Externalizable {

    private static final int VERSION = 1;
    
    private String _id=null;
    private long _timestamp=0;
    
    private Context _context=null;
    private Component _component=null;
    private ActivityType _activityType=null;
    
    /**
     * The default constructor.
     */
    public Activity() {
    }
    
    /**
     * The copy constructor.
     * 
     * @param act The activity to copy.
     */
    public Activity(Activity act) {
        _id = act._id;
        _timestamp = act._timestamp;
        
        if (act._context != null) {
            _context = new Context(act._context);
        }
        
        if (act._component != null) {
            _component = new Component(act._component);
        }
        
        _activityType = act._activityType;
    }
    
    /**
     * This method sets the activity id.
     * 
     * @param id The id
     */
    public void setId(String id) {
        _id = id;
    }
    
    /**
     * This method gets the activity id.
     * 
     * @return The id
     */
    public String getId() {
        return (_id);
    }
    
    /**
     * This method sets the timestamp.
     * 
     * @param timestamp The timestamp
     */
    public void setTimestamp(long timestamp) {
        _timestamp = timestamp;
    }
    
    /**
     * This method gets the timestamp.
     * 
     * @return The timestamp
     */
    public long getTimestamp() {
        return (_timestamp);
    }
    
    /**
     * This method sets the context.
     * 
     * @param context The context
     */
    public void setContext(Context context) {
        _context = context;
    }
    
    /**
     * This method gets the context.
     * 
     * @return The context
     */
    public Context getContext() {
        return (_context);
    }
    
    /**
     * This method sets the component.
     * 
     * @param component The component
     */
    public void setComponent(Component component) {
        _component = component;
    }
    
    /**
     * This method gets the component.
     * 
     * @return The component
     */
    public Component getComponent() {
        return (_component);
    }
    
    /**
     * This method sets the activity type.
     * 
     * @param activityType The activity type
     */
    public void setActivityType(ActivityType activityType) {
        _activityType = activityType;
    }
    
    /**
     * This method gets the activity type.
     * 
     * @return The activity type
     */
    public ActivityType getActivityType() {
        return (_activityType);
    }

    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION);
        
        out.writeUTF(_id);
        out.writeLong(_timestamp);
        out.writeObject(_context);
        out.writeObject(_component);
        out.writeObject(_activityType);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        in.readInt(); // Consume version, as not required for now
        
        _id = in.readUTF();
        _timestamp = in.readLong();
        _context = (Context)in.readObject();
        _component = (Component)in.readObject();
        _activityType = (ActivityType)in.readObject();
    }
    
}
