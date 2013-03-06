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
package org.overlord.rtgov.active.collection.jmx;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;

import org.mvel2.MVEL;
import org.overlord.rtgov.active.collection.AbstractActiveChangeListener;

/**
 * This class provides an active change listener for reporting
 * changes as JMX notifications.
 *
 */
public class JMXNotifier extends AbstractActiveChangeListener
                implements JMXNotifierMBean, javax.management.NotificationEmitter {

    private static final Logger LOG=Logger.getLogger(JMXNotifier.class.getName());
    
    private String _objectName=null;
    private String _insertType=null;
    private String _updateType=null;
    private String _removeType=null;
    
    private int _sequenceNumber=1;
    
    private java.util.List<NotificationDetails> _notificationDetails=
                    new java.util.ArrayList<NotificationDetails>();
    
    private String _descriptionScript=null;
    private java.io.Serializable _descriptionScriptExpression=null;
    private String _insertTypeScript=null;
    private java.io.Serializable _insertTypeScriptExpression=null;
    private String _updateTypeScript=null;
    private java.io.Serializable _updateTypeScriptExpression=null;
    private String _removeTypeScript=null;
    private java.io.Serializable _removeTypeScriptExpression=null;

    private boolean _preinitialized=false;

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
     * This method sets the description script for the JMX MBean.
     * 
     * @param script The description script
     */
    public void setDescriptionScript(String script) {
        _descriptionScript = script;
    }
    
    /**
     * This method gets the description script for the JMX MBean.
     * 
     * @return The description script
     */
    public String getDescriptionScript() {
        return (_descriptionScript);
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
     * This method sets the insert type script for the JMX MBean.
     * 
     * @param script The insert type script
     */
    public void setInsertTypeScript(String script) {
        _insertTypeScript = script;
    }
    
    /**
     * This method gets the insert type script for the JMX MBean.
     * 
     * @return The insert type script
     */
    public String getInsertTypeScript() {
        return (_insertTypeScript);
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
     * This method sets the update type script for the JMX MBean.
     * 
     * @param script The update type script
     */
    public void setUpdateTypeScript(String script) {
        _updateTypeScript = script;
    }
    
    /**
     * This method gets the update type script for the JMX MBean.
     * 
     * @return The update type script
     */
    public String getUpdateTypeScript() {
        return (_updateTypeScript);
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
     * This method sets the remove type script for the JMX MBean.
     * 
     * @param script The remove type script
     */
    public void setRemoveTypeScript(String script) {
        _removeTypeScript = script;
    }
    
    /**
     * This method gets the remove type script for the JMX MBean.
     * 
     * @return The remove type script
     */
    public String getRemoveTypeScript() {
        return (_removeTypeScript);
    }
    
    /**
     * {@inheritDoc}
     */
    protected void preInit() throws Exception {
        super.preInit();
        
        if (!_preinitialized) {
            _preinitialized = true;
            
            // Only initialize if the script is specified, but not yet compiled
            if (_descriptionScript != null && _descriptionScriptExpression == null) {
                java.io.InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(_descriptionScript);
                
                if (is == null) {
                    LOG.severe(MessageFormat.format(
                            java.util.PropertyResourceBundle.getBundle(
                            "active-collection.Messages").getString("ACTIVE-COLLECTION-1"),
                            _descriptionScript));
                } else {
                    byte[] b=new byte[is.available()];
                    is.read(b);
                    is.close();
    
                    // Compile expression
                    _descriptionScriptExpression = MVEL.compileExpression(new String(b));
    
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine("Pre-Initialized description script="+_descriptionScript
                                +" compiled="+_descriptionScriptExpression);
                    }
                }
            }
            
            // Only initialize if the script is specified, but not yet compiled
            if (_insertTypeScript != null && _insertTypeScriptExpression == null) {
                java.io.InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(_insertTypeScript);
                
                if (is == null) {
                    LOG.severe(MessageFormat.format(
                            java.util.PropertyResourceBundle.getBundle(
                            "active-collection.Messages").getString("ACTIVE-COLLECTION-1"),
                            _insertTypeScript));
                } else {
                    byte[] b=new byte[is.available()];
                    is.read(b);
                    is.close();
    
                    // Compile expression
                    _insertTypeScriptExpression = MVEL.compileExpression(new String(b));
    
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine("Pre-Initialized insert type script="+_insertTypeScript
                                +" compiled="+_insertTypeScriptExpression);
                    }
                }
            }
            
            // Only initialize if the script is specified, but not yet compiled
            if (_updateTypeScript != null && _updateTypeScriptExpression == null) {
                java.io.InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(_updateTypeScript);
                
                if (is == null) {
                    LOG.severe(MessageFormat.format(
                            java.util.PropertyResourceBundle.getBundle(
                            "active-collection.Messages").getString("ACTIVE-COLLECTION-1"),
                            _updateTypeScript));
                } else {
                    byte[] b=new byte[is.available()];
                    is.read(b);
                    is.close();
    
                    // Compile expression
                    _updateTypeScriptExpression = MVEL.compileExpression(new String(b));
    
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine("Pre-Initialized update type script="+_updateTypeScript
                                +" compiled="+_updateTypeScriptExpression);
                    }
                }
            }
            
            // Only initialize if the script is specified, but not yet compiled
            if (_removeTypeScript != null && _removeTypeScriptExpression == null) {
                java.io.InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(_removeTypeScript);
                
                if (is == null) {
                    LOG.severe(MessageFormat.format(
                            java.util.PropertyResourceBundle.getBundle(
                            "active-collection.Messages").getString("ACTIVE-COLLECTION-1"),
                            _removeTypeScript));
                } else {
                    byte[] b=new byte[is.available()];
                    is.read(b);
                    is.close();
    
                    // Compile expression
                    _removeTypeScriptExpression = MVEL.compileExpression(new String(b));
    
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine("Pre-Initialized remove type script="+_removeTypeScript
                                +" compiled="+_removeTypeScriptExpression);
                    }
                }
            }
        }
    }
    
    /**
     * This method returns the description associated with the
     * supplied value.
     * 
     * @param value The value
     * @return The description
     */
    protected String getDescription(Object value) {
        String description=value.toString();
        
        if (_descriptionScriptExpression != null) {
            description = (String)MVEL.executeExpression(_descriptionScriptExpression, value);
        }
        
        return (description);
    }
    
    /**
     * This method returns the type associated with the value.
     * 
     * @param value The value
     * @return The insert type
     */
    protected String getInsertType(Object value) {
        String type=_insertType;
        
        if (type == null && _insertTypeScriptExpression != null) {
            type = (String)MVEL.executeExpression(_insertTypeScriptExpression, value);
        }
        
        return (type);
    }

    /**
     * {@inheritDoc}
     */
    public void inserted(Object key, Object value) {
        String type=getInsertType(value);
        
        if (type != null) {
            Notification notification=new Notification(type, this,
                    _sequenceNumber++, getDescription(value));
            
            for (NotificationDetails n : _notificationDetails) {
                n.getListener().handleNotification(notification, n.getHandback());
            }
        }
    }
    
    /**
     * This method returns the type associated with the value.
     * 
     * @param value The value
     * @return The update type
     */
    protected String getUpdateType(Object value) {
        String type=_updateType;
        
        if (type == null && _updateTypeScriptExpression != null) {
            type = (String)MVEL.executeExpression(_updateTypeScriptExpression, value);
        }
        
        return (type);
    }

    /**
     * {@inheritDoc}
     */
    public void updated(Object key, Object value) {
        String type=getUpdateType(value);
        
        if (type != null) {
            Notification notification=new Notification(type, this,
                    _sequenceNumber++, getDescription(value));
            
            for (NotificationDetails n : _notificationDetails) {
                n.getListener().handleNotification(notification, n.getHandback());
            }
        }
    }
    
    /**
     * This method returns the type associated with the value.
     * 
     * @param value The value
     * @return The remove type
     */
    protected String getRemoveType(Object value) {
        String type=_removeType;
        
        if (type == null && _removeTypeScriptExpression != null) {
            type = (String)MVEL.executeExpression(_removeTypeScriptExpression, value);
        }
        
        return (type);
    }

    /**
     * {@inheritDoc}
     */
    public void removed(Object key, Object value) {
        String type=getRemoveType(value);
        
        if (type != null) {
            Notification notification=new Notification(type, this,
                    _sequenceNumber++, getDescription(value));
            
            for (NotificationDetails n : _notificationDetails) {
                n.getListener().handleNotification(notification, n.getHandback());
            }   
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
