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
package org.overlord.bam.active.collection.jmx;

import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;

import org.overlord.bam.active.collection.AbstractActiveChangeListener;

/**
 * This class provides an active change listener for reporting
 * changes as JMX notifications.
 *
 */
public class JMXNotifier extends AbstractActiveChangeListener
                implements JMXNotifierMBean, javax.management.NotificationEmitter {

    private String _objectName=null;
    private String _insertType=null;
    private String _updateType=null;
    private String _removeType=null;
    
    private int _sequenceNumber=1;
    
    private java.util.List<NotificationDetails> _notificationDetails=
                    new java.util.ArrayList<NotificationDetails>();
    
    /**
     * This method sets the object name for the JMX MBean.
     * 
     * @param name The object name
     */
    public void setObjectName(String name) {
        _objectName = name;
    }
    
    /**
     * This method gets the object name for the JMX MBean.
     * 
     * @return The object name
     */
    public String getObjectName() {
        return (_objectName);
    }
    
    /**
     * This method sets the insert type for the JMX MBean.
     * 
     * @param type The insert type
     */
    public void setInsertType(String type) {
        _insertType = type;
    }
    
    /**
     * This method gets the insert type for the JMX MBean.
     * 
     * @return The insert type
     */
    public String getInsertType() {
        return (_insertType);
    }
    
    /**
     * This method sets the update type for the JMX MBean.
     * 
     * @param type The update type
     */
    public void setUpdateType(String type) {
        _updateType = type;
    }
    
    /**
     * This method gets the update type for the JMX MBean.
     * 
     * @return The update type
     */
    public String getUpdateType() {
        return (_updateType);
    }
    
    /**
     * This method sets the remove type for the JMX MBean.
     * 
     * @param type The remove type
     */
    public void setRemoveType(String type) {
        _removeType = type;
    }
    
    /**
     * This method gets the remove type for the JMX MBean.
     * 
     * @return The remove type
     */
    public String getRemoveType() {
        return (_removeType);
    }
    
    /**
     * {@inheritDoc}
     */
    public void inserted(Object key, Object value) {
        Notification notification=new Notification(_insertType, this,
                _sequenceNumber++, value.toString());
        
        for (NotificationDetails n : _notificationDetails) {
            n.getListener().handleNotification(notification, n.getHandback());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void updated(Object key, Object value) {
        Notification notification=new Notification(_updateType, this,
                _sequenceNumber++, value.toString());
        
        for (NotificationDetails n : _notificationDetails) {
            n.getListener().handleNotification(notification, n.getHandback());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removed(Object key, Object value) {
        Notification notification=new Notification(_removeType, this,
                _sequenceNumber++, value.toString());
        
        for (NotificationDetails n : _notificationDetails) {
            n.getListener().handleNotification(notification, n.getHandback());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addNotificationListener(NotificationListener l,
            NotificationFilter filter, Object handback)
            throws IllegalArgumentException {
        _notificationDetails.add(new NotificationDetails(l, filter, handback));
    }

    /**
     * {@inheritDoc}
     */
    public MBeanNotificationInfo[] getNotificationInfo() {
        return new MBeanNotificationInfo[0];
    }

    /**
     * {@inheritDoc}
     */
    public void removeNotificationListener(NotificationListener l)
            throws ListenerNotFoundException {
        for (int i=_notificationDetails.size(); i >= 0; i--) {
            NotificationDetails n=_notificationDetails.get(i);

            if (n.getListener() == l) {
                _notificationDetails.remove(i);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeNotificationListener(NotificationListener l,
            NotificationFilter filter, Object handback)
            throws ListenerNotFoundException {
        for (int i=_notificationDetails.size(); i >= 0; i--) {
            NotificationDetails n=_notificationDetails.get(i);

            if (n.getListener() == l && n.getFilter() == filter
                    && n.getHandback() == handback) {
                _notificationDetails.remove(i);
            }
        }
    }

    /**
     * This class provides a container for the listener details.
     *
     */
    protected class NotificationDetails {
        
        private NotificationListener _listener=null;
        private NotificationFilter _filter=null;
        private Object _handback=null;
        
        /**
         * This is the constructor.
         * 
         * @param listener The listener
         * @param filter The filter
         * @param handback The handback
         */
        public NotificationDetails(NotificationListener listener,
                NotificationFilter filter, Object handback) {
            _listener = listener;
            _filter = filter;
            _handback = handback;
        }
        
        /**
         * The listener.
         * 
         * @return The listener
         */
        public NotificationListener getListener() {
            return (_listener);
        }
        
        /**
         * The filter.
         * 
         * @return The filter
         */
        public NotificationFilter getFilter() {
            return (_filter);
        }
        
        /**
         * The handback.
         * 
         * @return The handback
         */
        public Object getHandback() {
            return (_handback);
        }
    }
}
