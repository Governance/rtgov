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
package org.overlord.rtgov.service.dependency;

/**
 * This class represents the service call tree.
 *
 */
public class ServiceTree {

    private ServiceNode _rootNode=null;
 
    /**
     * The default constructor.
     */
    public ServiceTree() {
    }
    
    /**
     * This method returns the root node.
     * 
     * @return The root node
     */
    public ServiceNode getRootNode() {
        return (_rootNode);
    }
    
    /**
     * This method sets the root node.
     * 
     * @param node The root node
     */
    public void setRootNode(ServiceNode node) {
        _rootNode = node;
    }
}
