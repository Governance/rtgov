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
package org.overlord.rtgov.call.trace.model;

/**
 * This class represents the call trace.
 *
 */
public class CallTrace {

    private java.util.List<TraceNode> _tasks=new java.util.ArrayList<TraceNode>();
    
    /**
     * This method returns the tasks.
     * 
     * @return The tasks
     */
    public java.util.List<TraceNode> getTasks() {
        return (_tasks);
    }
    
    /**
     * This method sets the tasks.
     * 
     * @param tasks The tasks
     */
    public void setTasks(java.util.List<TraceNode> tasks) {
        _tasks = tasks;
    }
    
}
