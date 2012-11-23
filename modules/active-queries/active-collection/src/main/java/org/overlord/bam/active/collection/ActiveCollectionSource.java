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
package org.overlord.bam.active.collection;

import java.text.MessageFormat;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.mvel2.MVEL;

/**
 * This class defines an Active Collection Source that is
 * responsible for retrieving the data (with optional pre-
 * processing) to be placed within an associated active
 * collection, and then maintaining that information with
 * subsequent updates and eventual removal.
 *
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class ActiveCollectionSource {

    private static final Logger LOG=Logger.getLogger(ActiveCollectionSource.class.getName());
    
    private String _name=null;
    private ActiveCollectionType _type=ActiveCollectionType.List;
    
    private ActiveCollectionFactory _factory=null;
    
    private long _itemExpiration=0;
    private int _maxItems=0;
    private int _highWaterMark=0;
    private ActiveCollection _activeCollection=null;
    private java.util.List<AbstractActiveChangeListener> _listeners=
                    new java.util.ArrayList<AbstractActiveChangeListener>();

    private String _maintenanceScript=null;
    private java.io.Serializable _maintenanceScriptExpression=null;

    private String _scheduledScript=null;
    private java.io.Serializable _scheduledScriptExpression=null;
    private long _scheduledInterval=0;
    
    private java.util.Timer _scheduledTimer=null;
    
    private java.util.Map<String,Object> _variables=new java.util.HashMap<String, Object>();
    
    private java.util.Map<String,Object> _properties=new java.util.HashMap<String, Object>();

    private long _aggregationDuration=0;
    private String _groupBy=null;
    private java.io.Serializable _groupByExpression=null;
    private String _aggregationScript=null;
    private java.io.Serializable _aggregationScriptCompiled=null;
    
    private java.util.Map<Object, java.util.List<Object>> _groupedEvents=
                new java.util.HashMap<Object, java.util.List<Object>>();
    
    private Aggregator _aggregator=null;

    private boolean _preinitialized=false;

    /**
     * The default constructor.
     */
    public ActiveCollectionSource() {
    }
    
    /**
     * The copy constructor.
     * 
     * @param source The source to copy
     */
    public ActiveCollectionSource(ActiveCollectionSource source) {
        _name = source._name;
        _type = source._type;
        _itemExpiration = source._itemExpiration;
        _maxItems = source._maxItems;
        _highWaterMark = source._highWaterMark;
        _activeCollection = source._activeCollection;
        _listeners.addAll(source._listeners);
        _maintenanceScript = source._maintenanceScript;
        _maintenanceScriptExpression = source._maintenanceScriptExpression;
        _scheduledScript = source._scheduledScript;
        _scheduledScriptExpression = source._scheduledScriptExpression;
        _scheduledInterval = source._scheduledInterval;
        _scheduledTimer = source._scheduledTimer;
        _variables = new java.util.HashMap<String, Object>(source._variables);
        _properties = new java.util.HashMap<String, Object>(source._properties);
        _aggregationDuration = source._aggregationDuration;
        _groupBy = source._groupBy;
        _groupByExpression = source._groupByExpression;
        _aggregationScript = source._aggregationScript;
        _aggregationScriptCompiled = source._aggregationScriptCompiled;
        _groupedEvents = new java.util.HashMap<Object,java.util.List<Object>>(source._groupedEvents);
        _aggregator = source._aggregator;
        _preinitialized = source._preinitialized;
    }
    
    /**
     * This method sets the name of the active collection that
     * this source represents.
     * 
     * @param name The active collection name
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * This method returns the name of the active collection associated
     * with this source.
     * 
     * @return The name
     */
    public String getName() {
        return (_name);
    }

    /**
     * This method returns the active collection type associated
     * with the source.
     * 
     * @return The type
     */
    public ActiveCollectionType getType() {
        return (_type);
    }
    
    /**
     * This method sets the active collection type.
     * 
     * @param type The type
     */
    public void setType(ActiveCollectionType type) {
        _type = type;
    }

    /**
     * This method returns the factory responsible for
     * creating the active collection.
     * 
     * @return The factory
     */
    public ActiveCollectionFactory getFactory() {
        return (_factory);
    }
    
    /**
     * This method sets the factory responsible for
     * creating the active collection.
     * 
     * @param factory The factory
     */
    public void setFactory(ActiveCollectionFactory factory) {
       _factory = factory;
    }
    
    /**
     * This method returns the item expiration duration.
     * 
     * @return The number of milliseconds that the item should remain
     *          in the active collection, or 0 if not relevant
     */
    public long getItemExpiration() {
        return (_itemExpiration);
    }
    
    /**
     * This method sets the item expiration duration.
     * 
     * @param expire The item expiration duration, or zero
     *              for no expiration duration
     */
    public void setItemExpiration(long expire) {
        _itemExpiration = expire;
    }

    /**
     * This method returns the maximum number of items that should be
     * contained within the active collection. The default policy will
     * be to remove oldest entry when maximum number is reached.
     * 
     * @return The maximum number of items, or 0 if not relevant
     */
    public int getMaxItems() {
        return (_maxItems);
    }

    /**
     * This method sets the maximum number of items
     * that will be in the active collection.
     * 
     * @param max The maximum number of items, or zero
     *              for no limit
     */
    public void setMaxItems(int max) {
        _maxItems = max;
    }
    
    /**
     * This method gets the high water mark, used to indicate
     * when a warning should be issued.
     * 
     * @return The high water mark, or 0 if not relevant
     */
    public int getHighWaterMark() {
        return (_highWaterMark);
    }

    /**
     * This method sets the high water mark, used to indicate
     * when a warning should be issued.
     * 
     * @param highWaterMark The high water mark
     */
    public void setHighWaterMark(int highWaterMark) {
        _highWaterMark = highWaterMark;
    }
    
    /**
     * This method returns the Active Collection associated with the
     * source.
     * 
     * @return The active collection
     */
    @JsonIgnore
    public ActiveCollection getActiveCollection() {
        return (_activeCollection);
    }

    /**
     * This method sets the Active Collection associated with the
     * source.
     * 
     * @param ac The active collection
     */
    public void setActiveCollection(ActiveCollection ac) {
        _activeCollection = ac;
    }

    /**
     * This method returns the list of active change listeners to be
     * automatically registered against the active collection associated
     * with this source..
     * 
     * @return The list of active change listeners
     */
    public java.util.List<AbstractActiveChangeListener> getActiveChangeListeners() {
        return (_listeners);
    }

    /**
     * This method returns the list of active change listeners to be
     * automatically registered against the active collection associated
     * with this source..
     * 
     * @param listeners The list of active change listeners
     */
    public void setActiveChangeListeners(java.util.List<AbstractActiveChangeListener> listeners) {
        _listeners = listeners;
    }

    /**
     * This method sets the maintenance script.
     * 
     * @param script The maintenance script
     */
    public void setMaintenanceScript(String script) {
        _maintenanceScript = script;
    }
    
    /**
     * This method gets the maintenance script.
     * 
     * @return The maintenance script
     */
    public String getMaintenanceScript() {
        return (_maintenanceScript);
    }
    
    /**
     * This method sets the scheduled script.
     * 
     * @param script The scheduled script
     */
    public void setScheduledScript(String script) {
        _scheduledScript = script;
    }
    
    /**
     * This method gets the scheduled script.
     * 
     * @return The scheduled script
     */
    public String getScheduledScript() {
        return (_scheduledScript);
    }
    
    /**
     * This method sets the scheduled interval.
     * 
     * @param interval The scheduled interval
     */
    public void setScheduledInterval(long interval) {
        _scheduledInterval = interval;
    }
    
    /**
     * This method gets the scheduled interval.
     * 
     * @return The scheduled interval
     */
    public long getScheduledInterval() {
        return (_scheduledInterval);
    }
    
    /**
     * This method returns the interval variables that
     * can be used by scripts to cache information used
     * between invocations.
     * 
     * @return The internal variables
     */
    protected java.util.Map<String,Object> getVariables() {
        return (_variables);
    }
    
    /**
     * This method returns the properties.
     * 
     * @return The properties
     */
    public java.util.Map<String,Object> getProperties() {
        return (_properties);
    }
    
    /**
     * This method sets the properties.
     * 
     * @param props The properties
     */
    public void setProperties(java.util.Map<String,Object> props) {
        _properties = props;
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
     * This method pre-initializes the active collection source
     * in situations where it needs to be initialized before
     * registration with the manager. This may be required
     * where the registration is performed in a different
     * contextual classloader than the source was loaded.
     * 
     * @throws Exception Failed to pre-initialize
     */
    protected void preInit() throws Exception {
        
        if (!_preinitialized) {
            _preinitialized = true;
            
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Pre-Initializing script="+_aggregationScript
                        +" compiled="+_aggregationScriptCompiled);
            }
    
            // Only initialize if the script is specified, but not yet compiled
            if (_aggregationScript != null && _aggregationScriptCompiled == null) {
                java.io.InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(_aggregationScript);
                
                if (is == null) {
                    LOG.severe(MessageFormat.format(
                            java.util.PropertyResourceBundle.getBundle(
                            "active-collection.Messages").getString("ACTIVE-COLLECTION-1"),
                            _aggregationScript));
                } else {
                    byte[] b=new byte[is.available()];
                    is.read(b);
                    is.close();
    
                    // Compile expression
                    _aggregationScriptCompiled = MVEL.compileExpression(new String(b));
                }
            }

            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Pre-Initializing maintenance script="+_maintenanceScript
                        +" compiled="+_maintenanceScriptExpression);
            }
    
            // Only initialize if the script is specified, but not yet compiled
            if (_maintenanceScript != null && _maintenanceScriptExpression == null) {
                java.io.InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(_maintenanceScript);
                
                if (is == null) {
                    LOG.severe(MessageFormat.format(
                            java.util.PropertyResourceBundle.getBundle(
                            "active-collection.Messages").getString("ACTIVE-COLLECTION-1"),
                            _maintenanceScript));
                } else {
                    byte[] b=new byte[is.available()];
                    is.read(b);
                    is.close();

                    // Compile expression
                    _maintenanceScriptExpression = MVEL.compileExpression(new String(b));
                }
            }


            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Pre-Initializing scheduled script="+_scheduledScript
                        +" compiled="+_scheduledScriptExpression);
            }
    
            // Only initialize if the script is specified, but not yet compiled
            if (_scheduledScript != null && _scheduledScriptExpression == null) {
                java.io.InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(_scheduledScript);
                
                if (is == null) {
                    LOG.severe(MessageFormat.format(
                            java.util.PropertyResourceBundle.getBundle(
                            "active-collection.Messages").getString("ACTIVE-COLLECTION-1"),
                            _scheduledScript));
                 } else {
                    byte[] b=new byte[is.available()];
                    is.read(b);
                    is.close();

                    // Compile expression
                    _scheduledScriptExpression = MVEL.compileExpression(new String(b));
                }
            }

            // If active change listeners defined, then pre-init them
            if (_listeners.size() > 0) {                
                for (AbstractActiveChangeListener l : _listeners) {
                    l.preInit();
                 }
            }
            
        }
    }
    
    /**
     * This method initializes the active collection source.
     * 
     * @throws Exception Failed to initialize source
     */
    public void init() throws Exception {
        
        preInit();
        
        // Create the active collection, if not already defined
        if (_activeCollection == null) {
            ActiveCollectionFactory factory=(_factory == null ?
                    ActiveCollectionFactory.DEFAULT_FACTORY : _factory);
            
            _activeCollection = factory.createActiveCollection(this);
        }
        
        // Check that active collection has been set
        if (_activeCollection == null) {
            throw new Exception("Active collection has not been associated with the '"
                                +getName()+"' source");
        }
        
        // If active change listeners defined, then instantiate them
        // and add them to the active collection
        if (_listeners.size() > 0) {
            
            for (AbstractActiveChangeListener l : _listeners) {
                
                if (LOG.isLoggable(Level.FINER)) {
                    LOG.finer("Initialize active collection '"
                               +getName()+"' with listener from source: "+l);
                }
                
                l.init();
                
                _activeCollection.addActiveChangeListener(l);
            }
        }
        
        if (_groupBy != null) {
            // Compile expression
            _groupByExpression = MVEL.compileExpression(_groupBy);
            
            if (_aggregationDuration > 0) {
                // Create aggregator
                _aggregator = new Aggregator();
            }
        }
        
        // Check if scheduled timer should be started
        if (_scheduledScriptExpression != null && _scheduledInterval > 0) {
            _scheduledTimer = new java.util.Timer();
            _scheduledTimer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    java.util.Map<String,Object> vars=
                            new java.util.HashMap<String, Object>();

                    vars.put("acs", ActiveCollectionSource.this);
                    vars.put("variables", _variables);
                    
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine("Call scheduled script on '"+getName()
                                +"' with variables: "+_variables);
                    }
                    
                    synchronized (ActiveCollectionSource.this) {
                        MVEL.executeExpression(_scheduledScriptExpression, vars);
                    }
                }                
            }, 0, _scheduledInterval);
        }
    }

    /**
     * This method is invoked to handle the supplied entry details.
     * If a maintenance script has been defined, then it will be used
     * to manage the entry, otherwise it will be inserted
     * into the associated collection.
     * 
     * @param key The key
     * @param value The value
     */
    public void maintainEntry(Object key, Object value) {
        
        if (_maintenanceScriptExpression != null) {
            java.util.Map<String,Object> vars=
                    new java.util.HashMap<String, Object>();

            vars.put("acs", this);
            vars.put("key", key);
            vars.put("value", value);
            vars.put("variables", _variables);

            synchronized (this) {
                MVEL.executeExpression(_maintenanceScriptExpression, vars);
            }
        } else {
            insert(key, value);
        }
    }

    /**
     * This method adds the supplied object to the active collection.
     * If the optional key is provided, it can either be an index
     * if inserting into a particular position in a list (otherwise
     * default is to add to the end of the list), or a specific value
     * intended to be the key for a map.
     * 
     * @param key The optional key
     * @param value The value
     */
    public void insert(Object key, Object value) {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("insert key="+key+" value="+value+" ac="+_activeCollection);
        }
        _activeCollection.insert(key, value);
    }

    /**
     * This method updates the supplied value within the active collection,
     * based on the supplied key. If the active collection is a list, then
     * the key will be an integer reflecting the index of the element being
     * updated. If the active collection is a map, then the key will be
     * associated with the element to be updated.
     * 
     * @param key The key
     * @param value The value
     */
    public void update(Object key, Object value) {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("update key="+key+" value="+value+" ac="+_activeCollection);
        }
        _activeCollection.update(key, value);        
    }

    /**
     * This method removes the supplied object from the active collection.
     * 
     * @param key The optional key, not required for lists
     * @param value The value
     */
    public void remove(Object key, Object value) {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("remove key="+key+" value="+value+" ac="+_activeCollection);
        }
        _activeCollection.remove(key, value);
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
     * This method processes the supplied event to determine its group
     * for subsequent aggregation.
     * 
     * @param event The event to be processed
     */
    protected void aggregateEvent(java.io.Serializable event) {
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("aggregateEvent event="+event);
        }
        
        synchronized (_groupedEvents) {

            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Aggregating event: "+event);
            }
            
            // Derive key
            Object key=MVEL.executeExpression(_groupByExpression, event);
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Derived key '"+key+"' for event: "+event);
            }
            
            if (key == null) {
                LOG.severe(MessageFormat.format(
                        java.util.PropertyResourceBundle.getBundle(
                        "active-collection.Messages").getString("ACTIVE-COLLECTION-4"),
                        _groupBy, event));
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
            
            if (_aggregationScriptCompiled != null) {
                java.util.Map<String,java.util.List<Object>> vars=
                        new java.util.HashMap<String, java.util.List<Object>>();

                for (java.util.List<Object> list : source.values()) {

                    if (LOG.isLoggable(Level.FINEST)) {
                        LOG.finest("publishAggregateEvents list="+list);
                    }
                    
                    vars.clear();
                    vars.put("events", list);
                    
                    Object result= MVEL.executeExpression(_aggregationScriptCompiled, vars);
                    
                    if (result == null) {
                        LOG.severe(java.util.PropertyResourceBundle.getBundle(
                                "active-collection.Messages").getString("ACTIVE-COLLECTION-5"));
                        
                        if (LOG.isLoggable(Level.FINEST)) {
                            LOG.finest("Script="+_aggregationScript);
                            LOG.finest("List of Events="+list);
                        }
                    } else {
                        if (LOG.isLoggable(Level.FINEST)) {
                            LOG.finest("publishAggregateEvents result="+result);
                        }
                        
                        maintainEntry(null, result);
                    }
                }
            } else {
                LOG.severe(MessageFormat.format(
                        java.util.PropertyResourceBundle.getBundle(
                        "active-collection.Messages").getString("ACTIVE-COLLECTION-6"),
                        source));
            }
        }
    }
    
    /**
     * This method closes the active collection source.
     * 
     * @throws Exception Failed to close source
     */
    public void close() throws Exception {
        
        // Unregister any pre-defined listeners
        if (_listeners.size() > 0) {
                        
            for (AbstractActiveChangeListener l : _listeners) {
                _activeCollection.removeActiveChangeListener(l);
                
                l.close();
            }
        }

        if (_aggregator != null) {
            _aggregator.cancel();
        }
        
        if (_scheduledTimer != null) {
            _scheduledTimer.cancel();
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
