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
package org.savara.bam.epn;


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
     * @param source The source node
     * @return The notification channel
     * @throws Exception Channel cannot be created
     */
    public Channel getChannel(Network network, String source) throws Exception;

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
