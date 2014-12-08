/*
 * 2012-4 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.overlord.rtgov.active.collection.ws;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;
import org.overlord.rtgov.active.collection.command.ActiveChangeNotification;
import org.overlord.rtgov.active.collection.command.ActiveChangeNotification.ActiveChangeType;
import org.overlord.rtgov.active.collection.command.ActiveCollectionCommand;
import org.overlord.rtgov.active.collection.ActiveChangeListener;
import org.overlord.rtgov.active.collection.ActiveCollection;
import org.overlord.rtgov.active.collection.ActiveCollectionManager;
import org.overlord.rtgov.common.registry.ServiceRegistryUtil;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * This class represents the Websocket interface to the active collection server.
 *
 */
@ServerEndpoint("/acmws")
@ApplicationScoped
public class WSActiveCollectionServer {

    private static final Logger LOG=Logger.getLogger(WSActiveCollectionServer.class.getName());
    
    private static final ObjectMapper MAPPER=new ObjectMapper();

    private ActiveCollectionManager _acmManager=null;
    
    private org.overlord.rtgov.common.registry.ServiceListener<ActiveCollectionManager> _listener;
    
    private java.util.Map<String, ACMListener> _acmListeners=new java.util.HashMap<String, ACMListener>();

    /**
     * This is the default constructor.
     */
    public WSActiveCollectionServer() {
    }
    
    /**
     * This method initializes the active collection REST service.
     */
    @PostConstruct
    public void init() {
        _listener = new org.overlord.rtgov.common.registry.ServiceListener<ActiveCollectionManager>() {

            @Override
            public void registered(ActiveCollectionManager service) {
                _acmManager = service;
                
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Active collection manager="+_acmManager);
                }
            }

            @Override
            public void unregistered(ActiveCollectionManager service) {
                _acmManager = null;
                
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Unset active collection manager");
                }
            }
        };
        
        ServiceRegistryUtil.addServiceListener(ActiveCollectionManager.class, _listener);
    }
    
    /**
     * This method sets the active collection manager.
     * 
     * @param acm The active collection manager
     */
    public void setActiveCollectionManager(ActiveCollectionManager acm) {
        LOG.info("Set Active Collection Manager="+acm);
        _acmManager = acm;
    }
    
    /**
     * This method returns the active collection manager.
     * 
     * @return The active collection manager
     */
    public ActiveCollectionManager getActiveCollectionManager() {
        return (_acmManager);
    }
    
    /**
     * This method handles a new websocket connection.
     * 
     * @param session The session
     */
    @OnOpen
    public void onOpen(Session session) {
        synchronized (_acmListeners) {
            if (_acmListeners.containsKey(session.getId())) {
                LOG.severe("Websocket session already registered");
            } else {
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("Registering websocket session '"+session.getId()+"'");
                }
                _acmListeners.put(session.getId(), new ACMListener(session,_acmManager));
            }
        }
    }
    
    /**
     * This method processes an inbound message on the websocket.
     * 
     * @param message The message
     * @param session The associated websocket session
     */
    @OnMessage
    public void onMessage(String message, Session session) {     
        synchronized (_acmListeners) {
            if (!_acmListeners.containsKey(session.getId())) {
                LOG.severe("Websocket session not registered");
            } else {            
                ACMListener l=_acmListeners.get(session.getId());
                
                try {
                    ActiveCollectionCommand command=MAPPER.readValue(message.getBytes(), ActiveCollectionCommand.class);
                    
                    if (command.getRegister() != null) {
                        if (LOG.isLoggable(Level.FINEST)) {
                            LOG.finest("Register listener for notifications on active collection '"+command.getRegister().getCollection()+"'");
                        }
                        
                        l.register(command.getRegister().getCollection());
                    }
                    
                    if (command.getUnregister() != null) {
                        if (LOG.isLoggable(Level.FINEST)) {
                            LOG.finest("Unregister listener for notifications on active collection '"+command.getUnregister().getCollection()+"'");
                        }
                        
                        l.register(command.getUnregister().getCollection());
                    }
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Failed to deserialize the active collection command", e);
                }
            }
        }
    }
    
    /**
     * This method closes a websocket connection.
     * 
     * @param session The session
     */
    @OnClose
    public void onClose(Session session) {
        synchronized (_acmListeners) {
            if (!_acmListeners.containsKey(session.getId())) {
                LOG.severe("Websocket session not registered");
            } else {
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("Unregistering websocket session '"+session.getId()+"'");
                }
                
                ACMListener l=_acmListeners.remove(session.getId());
                
                l.close();
            }
        }
    }
    
    /**
     * This class manages the active collection listeners for a particular websocket
     * session.
     *
     */
    public static class ACMListener {
        
        private ActiveCollectionManager _activeCollectionManager;
        private Session _session;
        private java.util.Map<String, ActiveChangeListener> _listeners=new java.util.HashMap<String, ActiveChangeListener>();
        
        /**
         * This is the constructor.
         * 
         * @param session The session
         * @param acm The active collection manager
         */
        public ACMListener(Session session, ActiveCollectionManager acm) {
            _session = session;
            _activeCollectionManager = acm;
        }
               
        /**
         * This method registers a new active collection listener.
         * 
         * @param name The active collection name
         */
        public void register(String name) {
            ActiveCollection ac=_activeCollectionManager.getActiveCollection(name);
            
            if (ac != null) {
                ActiveChangeListener l=new ActiveChangeListener() {

                    @Override
                    public void inserted(Object key, Object value) {
                        try {
                            _session.getAsyncRemote().sendText(MAPPER.writeValueAsString(
                                    new ActiveChangeNotification(ActiveChangeType.Insert, key, value)));
                        } catch (Exception e) {
                            LOG.log(Level.SEVERE, "Failed to send notification", e);
                        }
                    }

                    @Override
                    public void updated(Object key, Object value) {
                        try {
                            _session.getAsyncRemote().sendObject(MAPPER.writeValueAsString(
                                    new ActiveChangeNotification(ActiveChangeType.Update, key, value)));
                        } catch (Exception e) {
                            LOG.log(Level.SEVERE, "Failed to send notification", e);
                        }
                    }

                    @Override
                    public void removed(Object key, Object value) {
                        try {
                            _session.getAsyncRemote().sendObject(MAPPER.writeValueAsString(
                                    new ActiveChangeNotification(ActiveChangeType.Remove, key, value)));
                        } catch (Exception e) {
                            LOG.log(Level.SEVERE, "Failed to send notification", e);
                        }
                    }
                    
                };
                
                ac.addActiveChangeListener(l);
                
                _listeners.put(name, l);
            } else {
                LOG.severe("Active collection '"+name+"' not found");
            }
        }
        
        /**
         * This method unregisters an active collection listener.
         * 
         * @param name The active collection name
         */
        public void unregister(String name) {
            if (_listeners.containsKey(name)) {
                unregisterListener(name, _listeners.remove(name));
            } else {
                LOG.severe("Active collection '"+name+"' was not registered");
            }
        }
        
        /**
         * This method unregisters the supplied listener from the active collection
         * with the specified name.
         * 
         * @param name The active collection name
         * @param l The listener
         */
        protected void unregisterListener(String name, ActiveChangeListener l) {
            ActiveCollection ac=_activeCollectionManager.getActiveCollection(name);

            if (ac != null) {
                ac.removeActiveChangeListener(l);
            } else {
                LOG.severe("Active change listener for '"+name+"' was found, but not the active collection");
            }
        }
        
        /**
         * This method closes the active collection listeners associated with the
         * websocket session.
         */
        public void close() {
            for (String name : _listeners.keySet()) {
                unregisterListener(name, _listeners.remove(name));
            }
        }
    }
}
