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

/**
 * This interfaces represents the Event Process Network
 * Manager.
 *
 */
public interface EPNManager {
    
    /**
     * The URI for the EPNManager registered in JNDI.
     */
    public static final String URI="java:global/overlord-rtgov/EPNManager";

    /**
     * This method registers a network.
     * 
     * @param network The network
     * @throws Exception Failed to register the network
     */
    public void register(Network network) throws Exception;
    
    /**
     * This method unregisters a network, associated with
     * the supplied name and version.
     * 
     * @param networkName The network name
     * @param version The version, or null if current
     * @throws Exception Failed to unregister the network
     */
    public void unregister(String networkName, String version) throws Exception;
    
    /**
     * This method registers a network listener.
     * 
     * @param l The listener
     */
    public void addNetworkListener(NetworkListener l);
    
    /**
     * This method unregisters a network listener.
     * 
     * @param l The listener
     */
    public void removeNetworkListener(NetworkListener l);
    
    /**
     * This method registers a node listener for the specified network.
     * 
     * @param network The network to listen to
     * @param l The listener
     */
    public void addNotificationListener(String network, NotificationListener l);
    
    /**
     * This method unregisters a node listener for the specified network.
     * 
     * @param network The network was listening to
     * @param l The listener
     */
    public void removeNotificationListener(String network, NotificationListener l);
    
    /**
     * This method publishes the supplied events to be processed
     * by any network subscribed to the nominated subject.
     * 
     * @param subject The subject upon which to publish the events
     * @param events The list of events to be processed
     * @throws Exception Failed to publish the events
     */
    public void publish(String subject,
                 java.util.List<? extends java.io.Serializable> events) throws Exception;
    
    /**
     * This method closes the manager.
     * 
     * @throws Exception Failed to close manager
     */
    public void close() throws Exception;
    
}
