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
package org.overlord.rtgov.call.trace;

import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.activity.server.ActivityServer;
import org.overlord.rtgov.call.trace.model.CallTrace;

/**
 * This interface is responsible for deriving a call trace from
 * activity information.
 *
 */
public interface CallTraceService {
    
    /**
     * This method sets the activity server.
     * 
     * @param as The activity server
     */
    public void setActivityServer(ActivityServer as);
    
    /**
     * This method gets the activity server.
     * 
     * @return The activity server
     */
    public ActivityServer getActivityServer();
    
    /**
     * This method creates a call trace associated with the
     * supplied context.
     * 
     * @param context The context
     * @return The call trace, or null if not found
     * @throws Exception Failed to create call trace
     */
    public CallTrace createCallTrace(Context context) 
                            throws Exception;

}
