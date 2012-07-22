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
package org.overlord.bam.epn.testdata;

import java.util.List;

import org.overlord.bam.epn.Channel;
import org.overlord.bam.epn.EPNContainer;
import org.overlord.bam.epn.EventList;
import org.overlord.bam.epn.Network;

public class TestEPNContainer implements EPNContainer {
    
    private TestChannel _channel;

    public TestEPNContainer(TestChannel ch) {
        _channel = ch;
    }
    
    public Channel getChannel(Network network, String source, String dest)
            throws Exception {
        return _channel;
    }

    public Channel getNotificationChannel(Network network, String source)
            throws Exception {
        return _channel;
    }

    public Channel getChannel(String subject) throws Exception {
        return _channel;
    }

    public void send(EventList events, List<Channel> channels) throws Exception {
        send(events, -1, channels);
    }

    public void send(EventList events, int retriesLeft, List<Channel> channels)
            throws Exception {
        for (Channel channel : channels) {
            if (channel instanceof TestChannel) {
                ((TestChannel)channel).send(events, retriesLeft);
            } else {
                throw new Exception("Unexpected channel type: "+channel);
            }
        }
    }
    
}
