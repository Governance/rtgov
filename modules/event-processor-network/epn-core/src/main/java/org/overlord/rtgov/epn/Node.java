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
package org.overlord.rtgov.epn;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class represents a node in the Event Processor Network.
 *
 */
public class Node {

    private static final Logger LOG=Logger.getLogger(Node.class.getName());
    
    private String _name=null;
    private int _maxRetries=3;
    private long _retryInterval=0;
    private EventProcessor _eventProcessor=null;
    private Predicate _predicate=null;
    private java.util.List<String> _sourceNodes=new java.util.ArrayList<String>();
    private java.util.List<String> _destinationSubjects=new java.util.ArrayList<String>();
    private java.util.List<Notification> _notifications=new java.util.ArrayList<Notification>();
    
    private java.util.List<Channel> _channels=new java.util.Vector<Channel>();
    
    /**
     * The default constructor for the event processor node.
     * 
     */
    public Node() {
    }
    
    /**
     * This method returns the node name.
     * 
     * @return The name
     */
    public String getName() {
        return (_name);
    }
    
    /**
     * This method sets the node name.
     * 
     * @param name The name
     */
    public void setName(String name) {
        _name = name;
    }
    
    /**
     * This method returns the maximum number of retries
     * for processing an event.
     * 
     * @return The maximum number of events
     */
    public int getMaxRetries() {
        return (_maxRetries);
    }
    
    /**
     * This method sets the maximum number of retries
     * for processing an event.
     * 
     * @param max The maximum number of events
     */
    public void setMaxRetries(int max) {
        _maxRetries = max;
    }
    
    /**
     * This method returns the retry interval. A
     * value of 0 means use the default for the
     * channel.
     * 
     * @return The retry interval, or 0 to use the default for the channel
     */
    public long getRetryInterval() {
        return (_retryInterval);
    }
    
    /**
     * This method sets the retry interval. A
     * value of 0 means use the default value for the
     * channel.
     * 
     * @param interval The retry interval
     */
    public void setRetryInterval(long interval) {
        _retryInterval = interval;
    }
    
    /**
     * This method returns the list of source nodes.
     * 
     * @return The source nodes
     */
    public java.util.List<String> getSourceNodes() {
        return (_sourceNodes);
    }
    
    /**
     * This method sets the list of sources.
     * 
     * @param sources The source nodes
     */
    public void setSourceNodes(java.util.List<String> sources) {
        _sourceNodes = sources;
    }
    
    /**
     * This method returns the list of destination subjects.
     * 
     * @return The destination subjects
     */
    public java.util.List<String> getDestinationSubjects() {
        return (_destinationSubjects);
    }
    
    /**
     * This method sets the list of destination subjects.
     * 
     * @param destinations The destination subjects
     */
    public void setDestinationSubjects(java.util.List<String> destinations) {
        _destinationSubjects = destinations;
    }
    
    /**
     * This method returns the event processor.
     * 
     * @return The event processor
     */
    public EventProcessor getEventProcessor() {
        return (_eventProcessor);
    }
    
    /**
     * This method sets the event processor.
     * 
     * @param ep The event processor
     */
    public void setEventProcessor(EventProcessor ep) {
        _eventProcessor = ep;
    }
    
    /**
     * This method returns the optional predicate that can be used
     * to filter the source events that should be processed.
     * 
     * @return The optional predicate
     */
    public Predicate getPredicate() {
        return (_predicate);
    }
    
    /**
     * This method sets the optional predicate that can be used
     * to filter the source events that should be processed.
     * 
     * @param pred The optional predicate
     */
    public void setPredicate(Predicate pred) {
        _predicate = pred;
    }
    
    /**
     * This method returns the list of notifications.
     * 
     * @return The list of notifications
     */
    public java.util.List<Notification> getNotifications() {
        return (_notifications);
    }
    
    /**
     * This method sets the list of notifications.
     * 
     * @param notifications The list of notifications
     */
    public void setNotificationEnabled(java.util.List<Notification> notifications) {
        _notifications = notifications;
    }
    
    /**
     * This method returns the list of channels associated with this
     * node.
     * 
     * @return The channels
     */
    protected java.util.List<Channel> getChannels() {
        return (_channels);
    }
    
    /**
     * This method initializes the node.
     * 
     * @throws Exception Failed to initialize the node
     */
    protected void init() throws Exception {
        
        if (getPredicate() != null) {
            getPredicate().init();
        }
        
        if (getEventProcessor() == null) {
            throw new Exception("Event Processor has not been configured for node");
        }
        
        getEventProcessor().init();
    }
    
    /**
     * This method processes the supplied list of events against the
     * event processor configured with the node, to determine
     * which transformed events should be forwarded, and which need
     * to be returned to be retried.
     * 
     * @param container The container
     * @param source The source node/subject that generated the event
     * @param events The list of events to be processed
     * @param retriesLeft The number of remaining retries
     * @return The events to retry, or null if no retries necessary
     * @throws Exception Failed to process events, and should result in transaction rollback
     */
    @SuppressWarnings("unchecked")
    protected EventList process(EPNContainer container, String source,
                      EventList events, int retriesLeft) throws Exception {
        java.util.List<Serializable> retries=null;
        java.util.List<Serializable> results=null;
        
        for (java.io.Serializable event : events) {
            
            if (getPredicate() == null || getPredicate().evaluate(event)) {
                try {
                    java.io.Serializable processed=getEventProcessor().process(source, event, retriesLeft);
                    
                    if (LOG.isLoggable(Level.FINEST)) {
                        LOG.log(Level.FINEST, "Processed event (retriesLeft="+retriesLeft+"): "
                                    +event+" processed="+processed);
                    }

                    if (processed != null) {
                        if (results == null) {
                            results = new java.util.ArrayList<Serializable>();
                        }
                        if (processed instanceof java.util.Collection<?>) {
                            results.addAll((java.util.Collection<? extends java.io.Serializable>)processed);
                        } else {
                            results.add(processed);
                        }
                    }
                    
                } catch (Exception e) {
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.log(Level.FINE, "Retry event (retriesLeft="+retriesLeft+"): "+event, e);
                    }
                    if (retries == null) {
                        retries = new java.util.ArrayList<Serializable>();
                        
                        if (retriesLeft == 0) {
                            LOG.log(Level.WARNING, "No more retries left on node '"
                                    +getName()+"' (exception from first failed event)", e);
                        }
                    }
                    retries.add(event);
                }
            }
        }
        
        if (results != null) {
            forward(container, new EventList(results));
        }
        
        return (retries != null ? new EventList(retries) : null);
    }
    
    /**
     * This method forwards the results to any destinations that have been
     * defined.
     * 
     * @param container The container
     * @param results The results
     * @throws Exception Failed to forward results
     */
    protected void forward(EPNContainer container, EventList results) throws Exception {
        container.send(results, _channels);
    }

    /**
     * This method closes the node.
     * 
     * @param container The container
     * @throws Exception Failed to close the node
     */
    protected void close(EPNContainer container) throws Exception {
        
        for (Channel ch : _channels) {
            ch.close();
        }
        
        if (getPredicate() != null) {
            getPredicate().close();
        }
        
        if (getEventProcessor() == null) {
            getEventProcessor().close();
        }
    }
    
}
