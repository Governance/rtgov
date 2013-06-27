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
package org.overlord.rtgov.epn.testdata;

import java.util.List;

import org.overlord.rtgov.epn.Channel;
import org.overlord.rtgov.epn.EPNContainer;
import org.overlord.rtgov.epn.EventList;
import org.overlord.rtgov.epn.Network;

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
