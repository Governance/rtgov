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
package org.overlord.rtgov.service.dependency.layout;

import org.overlord.rtgov.service.dependency.ServiceGraph;

/**
 * This interface represents the service graph layout
 * algorithm.
 *
 */
public interface ServiceGraphLayout {
    
    /**
     * This property defines the X position.
     */
    public static final String X_POSITION="XPosition";

    /**
     * This property defines the Y position.
     */
    public static final String Y_POSITION="YPosition";

    /**
     * This property defines the width.
     */
    public static final String WIDTH="Width";
    
    /**
     * This property defines the height.
     */
    public static final String HEIGHT="Height";

    /**
     * This method establishes the layout of the
     * nodes and links within the supplied service
     * graph.
     * 
     * @param sg The service graph
     */
    public void layout(ServiceGraph sg);
    
}
