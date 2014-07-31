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
package org.overlord.rtgov.active.collection.epn;

import java.io.Serializable;
import java.util.List;

import org.overlord.rtgov.epn.AbstractEPNManager;
import org.overlord.rtgov.epn.EPNContainer;
import org.overlord.rtgov.epn.EPNManager;
import org.overlord.rtgov.epn.EventList;
import org.overlord.rtgov.epn.Network;
import org.overlord.rtgov.epn.NetworkListener;
import org.overlord.rtgov.epn.NotificationListener;

public class TestEPNManager extends AbstractEPNManager implements EPNManager {
    
    private java.util.List<NotificationListener> _nodeListeners=new java.util.ArrayList<NotificationListener>();
    private java.util.List<NetworkListener> _networkListeners=new java.util.ArrayList<NetworkListener>();

    public void register(Network network) throws Exception {
    }

    public void unregister(String networkName, String version)
            throws Exception {
    }

    public void addNotificationListener(String network, NotificationListener l) {
        _nodeListeners.add(l);
    }

    public void removeNotificationListener(String network, NotificationListener l) {
        _nodeListeners.remove(l);
    }
    
    public void addNetworkListener(NetworkListener l) {
        _networkListeners.add(l);
    }

    public void removeNetworkListener(NetworkListener l) {
        _networkListeners.remove(l);
    }
    
    public void publish(String subject, EventList events) {
        for (NotificationListener l : _nodeListeners) {
            l.notify(subject, events);
        }
    }

    public void publish(String subject, List<? extends Serializable> events)
            throws Exception {
    }

    public void close() throws Exception {
    }

    protected EPNContainer getContainer() {
        return null;
    }
    
}
