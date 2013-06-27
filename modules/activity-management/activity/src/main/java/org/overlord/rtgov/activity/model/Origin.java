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
package org.overlord.rtgov.activity.model;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.persistence.Embeddable;

/**
 * This class represents information about the origin of the activity
 * events reported.
 *
 */
@Embeddable
public class Origin implements java.io.Externalizable {

    private static final int VERSION = 1;

    private String _principal=null;
    private String _thread=null;
    private String _host=null;
    private String _node=null;

    /**
     * The default constructor.
     */
    public Origin() {
    }
 
    /**
     * The copy constructor.
     * 
     * @param origin The origin to copy
     */
    public Origin(Origin origin) {
        _principal = origin._principal;
        _thread = origin._thread;
        _host = origin._host;
        _node = origin._node;
    }
    
    /**
     * This method sets the principal.
     * 
     * @param principal The principal
     */
    public void setPrincipal(String principal) {
        _principal = principal;
    }
    
    /**
     * This method gets the principal.
     * 
     * @return The principal
     */
    public String getPrincipal() {
        return (_principal);
    }
    
    /**
     * This method sets the thread.
     * 
     * @param thread The thread
     */
    public void setThread(String thread) {
        _thread = thread;
    }
    
    /**
     * This method gets the thread.
     * 
     * @return The thread
     */
    public String getThread() {
        return (_thread);
    }
    
    /**
     * This method sets the host.
     * 
     * @param host The host
     */
    public void setHost(String host) {
        _host = host;
    }
    
    /**
     * This method gets the host.
     * 
     * @return The host
     */
    public String getHost() {
        return (_host);
    }
    
    /**
     * This method sets the name of the node.
     * This name may identify the environment
     * within a clustered configuration.
     * 
     * @param node The node
     */
    public void setNode(String node) {
        _node = node;
    }
    
    /**
     * This method gets the name of the node.
     * This name may identify the environment
     * within a clustered configuration.
     * 
     * @return The node
     */
    public String getNode() {
        return (_node);
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION);
        
        out.writeObject(_principal);
        out.writeObject(_thread);
        out.writeObject(_host);
        out.writeObject(_node);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        in.readInt(); // Consume version, as not required for now
        
        _principal = (String)in.readObject();
        _thread = (String)in.readObject();
        _host = (String)in.readObject();
        _node = (String)in.readObject();
    }
}
