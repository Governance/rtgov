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
package org.savara.bam.active.collection.epn;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.mvel2.MVEL;
import org.savara.bam.active.collection.ActiveCollectionSource;
import org.savara.bam.epn.EPNManager;
import org.savara.bam.epn.EventList;
import org.savara.bam.epn.NodeListener;
import org.savara.bam.epn.NotifyType;

/**
 * This class provides the Active Collection Source for listening to
 * Event Processor Network nodes.
 *
 */
public class EPNActiveCollectionSource extends ActiveCollectionSource implements NodeListener {

    private static final Logger LOG=Logger.getLogger(EPNActiveCollectionSource.class.getName());

    private static final String EPN_MANAGER = "java:global/overlord-bam/EPNManager";

    private EPNManager _epnManager=null;
    private String _network=null;
    private String _node=null;
    private NotifyType _notifyType=null;
    
    private long _aggregationDuration=0;
    private String _groupBy=null;
    private java.io.Serializable _groupByExpression=null;
    private String _aggregationScript=null;
    private java.io.Serializable _aggregationScriptExpression=null;
    
    private java.util.Map<Object, java.util.List<Object>> _groupedEvents=
                new java.util.HashMap<Object, java.util.List<Object>>();
    
    private Aggregator _aggregator=null;
    
    /**
     * This method sets the EPN manager.
     * 
     * @param mgr The EPN Manager
     */
    protected void setEPNManager(EPNManager mgr) {
        _epnManager = mgr;
    }
    
    /**
     * This method returns the list of map of grouped events by key.
     * 
     * @return The grouped events
     */
    @JsonIgnore
    protected java.util.Map<Object, java.util.List<Object>> getGroupedEvents() {
        return (_groupedEvents);
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
    public void setNotifyType(NotifyType type) {
        _notifyType = type;
    }
    
    /**
     * This method gets the notification type.
     * 
     * @return The notification type
     */
    public NotifyType getNotifyType() {
        return (_notifyType);
    }
    
    /**
     * This method sets the aggregation duration.
     * 
     * @param duration The aggregation duration
     */
    public void setAggregationDuration(long duration) {
        _aggregationDuration = duration;
    }
    
    /**
     * This method gets the aggregation duration.
     * 
     * @return The aggregation duration
     */
    public long getAggregationDuration() {
        return (_aggregationDuration);
    }
    
    /**
     * This method sets the 'group by' expression.
     * 
     * @param expr The expression
     */
    public void setGroupBy(String expr) {
        _groupBy = expr;
    }
    
    /**
     * This method gets the 'group by' expression.
     * 
     * @return The expression
     */
    public String getGroupBy() {
        return (_groupBy);
    }
    
    /**
     * This method sets the aggregation script.
     * 
     * @param script The aggregation script
     */
    public void setAggregationScript(String script) {
        _aggregationScript = script;
    }
    
    /**
     * This method gets the aggregation script.
     * 
     * @return The aggregation script
     */
    public String getAggregationScript() {
        return (_aggregationScript);
    }
    
    /**
     * {@inheritDoc}
     */
    public void init() throws Exception {
        
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Initializing EPN Active Collection Source");
        }

        if (_epnManager == null) {
            try {
                InitialContext ctx=new InitialContext();
                
                _epnManager = (EPNManager)ctx.lookup(EPN_MANAGER);
                
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Failed to obtain Event Processor Network Manager", e);
                
                throw e;
            }
        }
        
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Register node listener for network="+_network);
        }

        _epnManager.addNodeListener(_network, this);
        
        if (_groupBy != null) {
            // Compile expression
            _groupByExpression = MVEL.compileExpression(_groupBy);
            
            if (_aggregationDuration > 0) {
                // Create aggregator
                _aggregator = new Aggregator();
            }
        }
        
        preInit();
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
    protected void preInit() throws Exception {
        
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Pre-Initializing EPN Active Collection Source (script="+_aggregationScript
                    +" compiled="+_aggregationScriptExpression+")");
        }

        // Only initialize if the script is specified, but not yet compiled
        if (_aggregationScript != null && _aggregationScriptExpression == null) {
            java.io.InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(_aggregationScript);
            
            if (is == null) {
                LOG.severe("Unable to locate '"+_aggregationScript+"'");
            } else {
                byte[] b=new byte[is.available()];
                is.read(b);
                is.close();

                // Compile expression
                _aggregationScriptExpression = MVEL.compileExpression(new String(b));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws Exception {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Closing EPN Active Collection Source");
        }

        if (_aggregator != null) {
            _aggregator.cancel();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void notify(String network, String version, String node,
            NotifyType type, EventList events) {
        if (isRelevant(network, version, node, type)) {
            
            if (_aggregationDuration > 0 && _groupByExpression != null) {
                aggregateEvents(network, version, node, type, events);
            } else {
                processNotification(network, version, node, type, events);
            }
        }
    }

    /**
     * This method determines whether the notification is relevant for this
     * active collection source.
     * 
     * @param network The network
     * @param version The version
     * @param node The node
     * @param type The type
     * @return Whether the notification is relevant
     */
    protected boolean isRelevant(String network, String version, String node,
                            NotifyType type) {
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("isRelevant network="+network+" version="+version+" node="+node+" type="+type+"?");
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
            LOG.finest("isRelevant network="+network+" version="+version+" node="+node+" type="+type+" TRUE");
        }
        
        return (true);
    }
    
    /**
     * This method processes the notification to update the active collection
     * accordingly.
     * 
     * @param network The network
     * @param version The version
     * @param node The node
     * @param type The type
     * @param events The list of events to be processed
     */
    protected void processNotification(String network, String version, String node,
                            NotifyType type, EventList events) {
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("processNotification network="+network+" version="+version
                    +" node="+node+" type="+type+" events="+events);
        }
        
        // Default behaviour is to simply add all events to the
        // active collection
        for (Object event : events) {
            insert(null, event);
        }
    }
    
    /**
     * This method processes the notification to aggregate information over a
     * particular duration.
     * 
     * @param network The network
     * @param version The version
     * @param node The node
     * @param type The type
     * @param events The list of events to be processed
     */
    protected void aggregateEvents(String network, String version, String node,
                            NotifyType type, EventList events) {
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("aggregateEvents network="+network+" version="+version
                    +" node="+node+" type="+type+" events="+events);
        }
        
        synchronized (_groupedEvents) {
            
            for (java.io.Serializable event : events) {
                
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("Aggregating event: "+event);
                }
                
                // Derive key
                Object key=MVEL.executeExpression(_groupByExpression, event);
                
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("Derived key '"+key+"' for event: "+event);
                }
                
                if (key == null) {
                    LOG.severe("Failed to evaluate expression '"+_groupBy+"' on event: "+event);
                } else {
                    java.util.List<Object> list=_groupedEvents.get(key);
                    
                    if (list == null) {
                        list = new java.util.ArrayList<Object>();
                        _groupedEvents.put(key, list);
                    }
                    
                    list.add(event);
                }
            }
        }
    }
    
    /**
     * This method publishes any aggregated events to the associated active
     * collection.
     */
    protected void publishAggregateEvents() {
        java.util.Map<Object, java.util.List<Object>> source=null;
        
        // Take a copy of the events and clear the original map
        synchronized (_groupedEvents) {
            
            if (_groupedEvents.size() > 0) {
                source = new java.util.HashMap<Object, java.util.List<Object>>(_groupedEvents);
                
                _groupedEvents.clear();
            }
        }
        
        if (source != null) {
            
            if (_aggregationScriptExpression != null) {
                java.util.Map<String,java.util.List<Object>> vars=
                        new java.util.HashMap<String, java.util.List<Object>>();

                for (java.util.List<Object> list : source.values()) {

                    if (LOG.isLoggable(Level.FINEST)) {
                        LOG.finest("publishAggregateEvents list="+list);
                    }
                    
                    vars.clear();
                    vars.put("events", list);
                    
                    Object result= MVEL.executeExpression(_aggregationScriptExpression, vars);
                    
                    if (result == null) {
                        LOG.severe("Aggregation script failed to return a result (network="
                                        +_network+" node="+_node+")");
                        if (LOG.isLoggable(Level.FINEST)) {
                            LOG.finest("Script="+_aggregationScript);
                            LOG.finest("List of Events="+list);
                        }
                    } else {
                        if (LOG.isLoggable(Level.FINEST)) {
                            LOG.finest("publishAggregateEvents result="+result);
                        }
                        
                        insert(null, result);
                    }
                }
            } else {
                LOG.severe("No aggregation script to process events: "+source);
            }
        }
    }
    
    /**
     * This class implements the aggregation functionality triggered
     * at configured intervals.
     *
     */
    public class Aggregator extends java.util.TimerTask {

        private java.util.Timer _timer=new java.util.Timer();
        
        /**
         * This is the constructor.
         */
        public Aggregator() {
            _timer.scheduleAtFixedRate(this, _aggregationDuration, _aggregationDuration);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            publishAggregateEvents();
        }
    }
}
