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
 * This interface represents the container in which the network will be
 * initialized and processed.
 *
 */
public interface EPNContainer {

    /**
     * This method returns the channel associated with the supplied
     * destination. If a channel cannot be established for any
     * reason, then an exception will be thrown indicating the
     * problem.
     * 
     * @param network The network
     * @param source The source node
     * @param dest The destination node
     * @return The channel
     * @throws Exception Channel cannot be created
     */
    public Channel getChannel(Network network, String source, String dest) throws Exception;

    /**
     * This method returns the notification channel associated with 
     * the supplied network and source. If a node emits notifications,
     * then a notification channel will be created to notify registered
     * listeners of the results produced by the node.
     * 
     * @param network The network
     * @param subject The subject
     * @return The notification channel
     * @throws Exception Channel cannot be created
     */
    public Channel getNotificationChannel(Network network, String subject) throws Exception;

    /**
     * This method returns the channel associated with the supplied
     * subject. If a channel cannot be established for any
     * reason, then an exception will be thrown indicating the
     * problem.
     * 
     * @param subject The subject on which the events will be published
     * @return The channel
     * @throws Exception Channel cannot be created
     */
    public Channel getChannel(String subject) throws Exception;

    /**
     * This method sends the supplied events to the supplied list
     * of channels. These events represent the results of a node
     * processing another groups of events.
     * 
     * @param events The events
     * @param channels The list of channels
     * @throws Exception Failed to send the events
     */
    public void send(EventList events,
                    java.util.List<Channel> channels) throws Exception;

}
