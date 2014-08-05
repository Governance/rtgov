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
package org.overlord.rtgov.tests.epn;

import org.overlord.rtgov.epn.Network;
import org.overlord.rtgov.epn.Node;
import org.overlord.rtgov.epn.Subscription;
import org.overlord.rtgov.epn.util.NetworkUtil;

/**
 * This class is responsible for loading the test network and registering it with the
 * Event Processor Network Manager.
 *
 */
public class NetworkLoader {
    
    /** The root. **/
    public static final String ROOT = "Root";
    /** The child A. **/
    public static final String CHILD_A = "ChildA";
    /** The child B. **/
    public static final String CHILD_B = "ChildB";
    /** The subject. **/
    public static final String TEST_SUBJECT = "TestSubject";
    /** The network. **/
    public static final String TEST_NETWORK = "TestNetwork";
    /** The file. **/
    public static final String NETWORK_FILE="/networks/TestNetwork.json";
    
    /**
     * The main method.
     * 
     * @param args The list of args
     */
    public static void main(String[] args) {
        NetworkLoader loader=new NetworkLoader();
        
        Network net=loader.createNetwork();
        
        try {
            byte[] b=NetworkUtil.serialize(net);
            
            java.net.URL url=NetworkLoader.class.getResource(NETWORK_FILE);
            
            java.io.FileOutputStream fos=new java.io.FileOutputStream(url.getFile());
            
            fos.write(b);
            fos.flush();
            
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This method loads the network.
     * 
     * @return The network
     */
    public Network loadNetwork() {
        Network ret=null;
        
        try {
            java.io.InputStream is=NetworkLoader.class.getResourceAsStream(NETWORK_FILE);
            
            byte[] b=new byte[is.available()];
            is.read(b);
            
            is.close();
            
            ret = NetworkUtil.deserialize(b);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return (ret);
    }

    /**
     * This method creates the network.
     * 
     * @return The new network
     */
    public Network createNetwork() {
        Network ret=new Network();
        ret.setName(TEST_NETWORK);
        ret.setVersion(""+System.currentTimeMillis());
        
        ret.subjects().add(TEST_SUBJECT);
        
        Root rootep=new Root();
        Child childAep=new Child();
        Child childBep=new Child();
        
        ChildPredicate childApred=new ChildPredicate();
        childApred.setMin(0);
        childApred.setMax(9);
        
        ChildPredicate childBpred=new ChildPredicate();
        childBpred.setMin(10);
        childBpred.setMax(19);
        
        Node root=new Node();
        root.setName(ROOT);
        root.setEventProcessor(rootep);
        ret.getNodes().add(root);
        
        Node childA=new Node();
        childA.setName(CHILD_A);
        childA.setPredicate(childApred);
        childA.setEventProcessor(childAep);
        childA.getSourceNodes().add(ROOT);
        ret.getNodes().add(childA);

        Node childB=new Node();
        childB.setName(CHILD_B);
        childB.setPredicate(childBpred);
        childB.setEventProcessor(childBep);
        childB.getSourceNodes().add(ROOT);
        ret.getNodes().add(childB);
        
        Subscription sub=new Subscription();
        sub.setNodeName(ROOT);
        sub.setSubject(TEST_SUBJECT);
        ret.getSubscriptions().add(sub);
        
        return (ret);
    }
}
