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
package org.overlord.bam.active.collection.epn;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;

import org.overlord.bam.active.collection.ActiveCollectionSource;
import org.overlord.bam.epn.ContextualNotificationListener;
import org.overlord.bam.epn.EPNManager;
import org.overlord.bam.epn.EventList;
import org.overlord.bam.epn.NotificationType;

/**
 * This class provides the Active Collection Source for listening to
 * Event Processor Network nodes.
 *
 */
public class EPNActiveCollectionSource extends ActiveCollectionSource {

    private static final Logger LOG=Logger.getLogger(EPNActiveCollectionSource.class.getName());

    private EPNManager _epnManager=null;
    private String _subject=null;
    private String _network=null;
    private String _node=null;
    private NotificationType _notifyType=null;
    
    
    private ClassLoader _contextClassLoader=null;
    private boolean _preinitialized=false;

    private EPNACSNotificationListener _listener=new EPNACSNotificationListener();
    
    /**
     * This method sets the EPN manager.
     * 
     * @param mgr The EPN Manager
     */
    protected void setEPNManager(EPNManager mgr) {
        _epnManager = mgr;
    }
    
    /**
     * This method sets the subject.
     * 
     * @param subject The subject
     */
    public void setSubject(String subject) {
        _subject = subject;
    }
    
    /**
     * This method gets the subject.
     * 
     * @return The subject
     */
    public String getSubject() {
        return (_subject);
    }
    
    /**
     * This method sets the network name.
     * 
     * @param network The network name
     */
    public void setNetwork(String network) {
        _network = network;
    }
    
    /**
     * This method gets the network name.
     * 
     * @return The network name
     */
    public String getNetwork() {
        return (_network);
    }
    
    /**
     * This method sets the node name.
     * 
     * @param node The node name
     */
    public void setNode(String node) {
        _node = node;
    }
    
    /**
     * This method gets the node name.
     * 
     * @return The node name
     */
    public String getNode() {
        return (_node);
    }
    
    /**
     * This method sets the notification type.
     * 
     * @param type The notification type
     */
    public void setNotifyType(NotificationType type) {
        _notifyType = type;
    }
    
    /**
     * This method gets the notification type.
     * 
     * @return The notification type
     */
    public NotificationType getNotifyType() {
        return (_notifyType);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void init() throws Exception {
        super.init();
        
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Initializing EPN Active Collection Source");
        }

        if (_epnManager == null) {
            try {
                InitialContext ctx=new InitialContext();
                
                _epnManager = (EPNManager)ctx.lookup(EPNManager.URI);
                
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Failed to obtain Event Processor Network Manager", e);
                
                throw e;
            }
        }
        
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Register notification listener for subject="+_subject);
        }

        _epnManager.addNotificationListener(_subject, _listener);
    }
    
    /**
     * This method pre-initializes the active collection source
     * in situations where it needs to be initialized before
     * registration with the manager. This may be required
     * where the registration is performed in a different
     * contextual classloader than the source was loaded.
     * 
     * @throws Exception Failed to pre-initialize
     */
    @Override
    protected void preInit() throws Exception {
        // Don't want to perform pre-init twice, as will overwrite
        // context classloader with the wrong value
        
        if (!_preinitialized) {
            _preinitialized = true;
            
            super.preInit();
            
            // Cache context classloader for use deserializing
            // events in this context
            _contextClassLoader = Thread.currentThread().getContextClassLoader();
        }
    }

    /**
     * This method processes the notification to aggregate information over a
     * particular duration.
     * 
     * @param subject The subject
     * @param network The network
     * @param version The version
     * @param node The node
     * @param type The type
     * @param events The list of events to be processed
     */
    protected void aggregateEvents(String subject, String network,
                        String version, String node,
                            NotificationType type, EventList events) {
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("aggregateEvents subject="+subject+" network="
                    +network+" version="+version
                    +" node="+node+" type="+type+" events="+events);
        }
        
        for (java.io.Serializable event : events) {
            aggregateEvent(event);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws Exception {
        super.close();
        
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Closing EPN Active Collection Source");
        }

        _epnManager.removeNotificationListener(_subject, _listener);
    }

    /**
     * This method determines whether the notification is relevant for this
     * active collection source.
     * 
     * @param subject The subject
     * @param network The network
     * @param version The version
     * @param node The node
     * @param type The type
     * @return Whether the notification is relevant
     */
    protected boolean isRelevant(String subject, String network, String version, String node,
                            NotificationType type) {
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("isRelevant subject="+subject+" network="
                     +network+" version="+version+" node="+node+" type="+type+"?");
        }
        
        if (_subject != null && !subject.equals(_subject)) {
            return (false);
        }

        if (_network != null && !network.equals(_network)) {
            return (false);
        }
        
        if (_node != null && !node.equals(_node)) {
            return (false);
        }
        
        if (_notifyType != null && !type.equals(_notifyType)) {
            return (false);
        }
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("isRelevant subject="+subject+" network="
                    +network+" version="+version+" node="+node+" type="+type+" TRUE");
        }
        
        return (true);
    }
    
    /**
     * This method processes the notification to update the active collection
     * accordingly.
     * 
     * @param subject The subject
     * @param network The network
     * @param version The version
     * @param node The node
     * @param type The type
     * @param events The list of events to be processed
     */
    protected void processNotification(String subject, String network,
                            String version, String node,
                            NotificationType type, EventList events) {
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("processNotification subject="+subject
                    +" network="+network+" version="+version
                    +" node="+node+" type="+type+" events="+events);
        }
        
        // Default behaviour is to simply add all events to the
        // active collection
        for (Object event : events) {
            handleItem(null, event);
        }
    }
    
    /**
     * This class handles the events from the EPN.
     *
     */
    public class EPNACSNotificationListener extends ContextualNotificationListener {

        /**
         * {@inheritDoc}
         */
        public ClassLoader getContextClassLoader() {
            return (_contextClassLoader);
        }

        /**
         * {@inheritDoc}
         */
        public void handleEvents(String subject, String network, String version, String node,
                NotificationType type, EventList events) {
            if (isRelevant(subject, network, version, node, type)) {
                
                if (getAggregationDuration() > 0 && getGroupBy() != null) {
                    aggregateEvents(subject, network, version, node, type, events);
                } else {
                    processNotification(subject, network, version, node, type, events);
                }
            }            
        }
        
    }
}
