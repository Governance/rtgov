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
package org.overlord.rtgov.call.trace.descriptors;

import org.overlord.rtgov.activity.model.ActivityType;

/**
 * This class provides a factory for task descriptors.
 *
 */
public final class TaskDescriptorFactory {

    private static TaskDescriptor _defaultDescriptor=new DefaultTaskDescriptor();
    
    private static java.util.List<TaskDescriptor> _descriptors=
                new java.util.ArrayList<TaskDescriptor>();
    
    static {
        _descriptors.add(new LogMessageTaskDescriptor());
    }
    
    /**
     * Private constructor.
     */
    private TaskDescriptorFactory() {
    }
    
    /**
     * This method returns a task descriptor appropriate for the
     * supplied activity type.
     * 
     * @param at The activity type
     * @return The task descriptor
     */
    public static TaskDescriptor getTaskDescriptor(ActivityType at) {
        TaskDescriptor ret=_defaultDescriptor;
        
        for (TaskDescriptor td : _descriptors) {
            if (td.isSupported(at)) {
                ret = td;
                break;
            }
        }
        
        return (ret);
    }
}
