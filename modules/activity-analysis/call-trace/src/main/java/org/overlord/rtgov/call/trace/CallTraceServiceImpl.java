/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-12, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.rtgov.call.trace;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Singleton;

import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.activity.model.app.LogMessage;
import org.overlord.rtgov.activity.model.soa.RPCActivityType;
import org.overlord.rtgov.activity.model.soa.RequestReceived;
import org.overlord.rtgov.activity.model.soa.RequestSent;
import org.overlord.rtgov.activity.model.soa.ResponseReceived;
import org.overlord.rtgov.activity.model.soa.ResponseSent;
import org.overlord.rtgov.activity.server.ActivityServer;
import org.overlord.rtgov.call.trace.descriptors.TaskDescriptorFactory;
import org.overlord.rtgov.call.trace.model.Call;
import org.overlord.rtgov.call.trace.model.CallTrace;
import org.overlord.rtgov.call.trace.model.Task;
import org.overlord.rtgov.call.trace.model.TraceNode;
import org.overlord.rtgov.call.trace.model.TraceNode.Status;
import org.overlord.rtgov.call.trace.util.CallTraceUtil;

/**
 * This class is responsible for deriving a call trace from
 * activity information.
 *
 */
@Singleton
@Transactional
public class CallTraceServiceImpl implements CallTraceService {
    
    private static final Logger LOG=Logger.getLogger(CallTraceServiceImpl.class.getName());

    private ActivityServer _activityServer=null;
    
    /**
     * This method sets the activity server.
     * 
     * @param as The activity server
     */
    public void setActivityServer(ActivityServer as) {
        _activityServer = as;
    }
    
    /**
     * This method gets the activity server.
     * 
     * @return The activity server
     */
    public ActivityServer getActivityServer() {
        return (_activityServer);
    }
    
    /**
     * This method creates a call trace associated with the
     * supplied correlation value.
     * 
     * @param correlation The correlation value
     * @return The call trace, or null if not found
     * @throws Exception Failed to create call trace
     */
    @TransactionAttribute(value= TransactionAttributeType.REQUIRED)
    public CallTrace createCallTrace(String correlation) 
                            throws Exception {
        CTState state=new CTState();
        
        // Recursively load activity units that are directly or
        // indirectly associated with the correlation key
        loadActivityUnits(state, correlation);
        
        return (processAUs(state));
    }
    
    /**
     * This method loads activity units associated with the supplied
     * correlation key.
     * 
     * @param state The state
     * @param correlation The correlation key
     */
    protected void loadActivityUnits(CTState state, String correlation) {
        
        if (!state.isCorrelationInitialized(correlation)) {
            
            // Retrieve activity types associated with correlation
            try {
                java.util.List<ActivityType> ats=
                        _activityServer.getActivityTypes(correlation);
                
                // Check each activity type's unit id to see whether
                // it needs to be retrieved
                java.util.List<ActivityUnit> aus=
                        new java.util.ArrayList<ActivityUnit>();
                
                for (ActivityType at : ats) {
                    if (!state.isActivityUnitLoaded(at.getUnitId())) {
                        ActivityUnit au=_activityServer.getActivityUnit(at.getUnitId());
                        
                        aus.add(au);
                        
                        // Add to state
                        state.add(au);
                    }
                }
                
                // Mark this correlation value as initialized
                state.initialized(correlation);

                // For each new activity unit, scan for unknown correlation
                // fields, and recursively load their associated units
                for (ActivityUnit au : aus) {                    
                    for (ActivityType at : au.getActivityTypes()) {
                        for (Context c : at.getContext()) {                            
                            loadActivityUnits(state, c.getValue());
                        }
                    }
                }
                
            } catch (Exception e) {
                LOG.log(Level.SEVERE, MessageFormat.format(
                        java.util.PropertyResourceBundle.getBundle(
                        "call-trace.Messages").getString("CALL-TRACE-1"),
                            correlation), e);
            }
        }
    }
    
    /**
     * This method processes the supplied call trace state
     * to return the call trace model.
     * 
     * @param state The state
     * @return The model
     */
    protected static CallTrace processAUs(CTState state) {
        CallTrace ret=new CallTrace();
        
        java.util.List<ActivityUnit> topLevel=getTopLevelAUs(state);
        
        state.getTasksStack().push(ret.getTasks());
        
        for (ActivityUnit au : topLevel) {
            processAU(state, au, topLevel);
        }
        
        state.finalizeScope();
        
        return (ret);
    }
    
    /**
     * This method processes the supplied activity unit to
     * create a set of trace nodes.
     * 
     * @param state The state
     * @param startau The activity unit being processed
     * @param topLevel The top level activity units
     */
    protected static void processAU(CTState state, ActivityUnit startau,
                java.util.List<ActivityUnit> topLevel) {
        ActivityType cur=null;
        Call call=(state.getCallStack().size() > 0 ? state.getCallStack().peek() : null);
        java.util.List<TraceNode> tasks=(state.getTasksStack().size() > 0 ? state.getTasksStack().peek() : null);        
        ActivityType prev=null;
       
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Start Process Initial AU="+startau);
        }

        java.util.List<ActivityUnit> aus=state.getActivityUnits();
        
        int aupos=aus.indexOf(startau);        
        if (aupos == -1) {
            LOG.severe("Failed to find activity unit in list="+startau);
            return;
        }
        
        boolean f_end=false;
        boolean f_scopeFinalized=false;
        
        // Process a sequence of activity units, starting with the one supplied,
        // but skipping any that are listed in the top level collection.
        // Break out of the sequence when a response sent is detected.        
        for (int i=aupos; !f_end && i < aus.size(); i++) {
            ActivityUnit au=aus.get(i);
            
            if (i != aupos && topLevel.contains(au)) {
                // Skip top level units
                continue;
            }
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Process AU="+aus.get(i));  
            }
            
            ActivityUnitCursor cursor=state.getCursor(au);

            while ((cur=cursor.next()) != null) {
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("Processing cur="+cur);     
                }
                
                if (shouldPostpone(state, au, topLevel, cur)) {
                    break;
                }
                
                if (cur instanceof RPCActivityType) {
                    
                    if (cur instanceof RequestSent
                            || (cur instanceof RequestReceived && call == null)) {
                        
                        // Create call, and search for activity unit
                        // containing scoped tasks
                        call = createCall((RPCActivityType)cur);
                        
                        if (tasks != null) {
                            tasks.add(call);
                        } else if (LOG.isLoggable(Level.FINE)) {
                            LOG.fine("Attempt to add call node to 'null' tasks list");
                        }

                        tasks = call.getTasks();

                        if (LOG.isLoggable(Level.FINEST)) {
                            LOG.finest("Pushing call="+call);
                            LOG.finest("Pushing tasks="+tasks);
                        }

                        state.getCallStack().push(call);
                        state.getTasksStack().push(tasks);
                        
                        state.getTriggerActivities().put(call, (RPCActivityType)cur);
                    }
                    
                    if (cur instanceof RequestSent) {
                        instrumentCall(call, au, (RequestSent)cur);
                        
                        RPCActivityType rr=state.getSOAActivity(RequestReceived.class,
                                ((RequestSent)cur).getServiceType(),
                                ((RequestSent)cur).getOperation());                        
                        if (rr != null) {
                            call.setRequestLatency(rr.getTimestamp()-cur.getTimestamp());
                            
                            ActivityUnit subAU=state.getActivityUnit(rr.getUnitId());                           
                            if (subAU != null) {
                                processAU(state, subAU, topLevel);
                                
                                call = (state.getCallStack().size() > 0 ? state.getCallStack().peek() : null);
                                tasks = (state.getTasksStack().size() > 0 ? state.getTasksStack().peek() : null);
                            }
                        }
                    } else if (cur instanceof RequestReceived) {                    
                        instrumentCall(call, au, (RequestReceived)cur);
                        call.setRequest(((RequestReceived)cur).getContent());
                        
                    } else if (cur instanceof ResponseSent) {
                        initializeResponseSent(state, (ResponseSent)cur, call);
                        
                        // Finalise the tasks in the scope, and pop the stack
                        state.finalizeScope();
                        
                        f_scopeFinalized = true;

                        // Get new values
                        call = (state.getCallStack().size() > 0 ? state.getCallStack().peek() : null);
                        tasks = (state.getTasksStack().size() > 0 ? state.getTasksStack().peek() : null);
                        
                        // If not top level call, then break out
                        // of loop, to finish processing the scope
                        if (state.getCallStack().size() > 0) {
                            if (LOG.isLoggable(Level.FINEST)) {
                                LOG.finest("Break on response sent");  
                            }
                            
                            // Break out of processing the cursor, and also the method
                            f_end = true;
                            break;
                        }                        
                    } else if (cur instanceof ResponseReceived) {
                        initializeResponseReceived(state, (ResponseReceived)cur);

                        // Set end flag, to break out of this method once
                        // this cursor has finished
                        f_end = true;
                    }                    
                } else {
                    Task task=createTask(cur);
                    
                    tasks.add(task);
                    
                    if (prev != null) {
                        task.setDuration(cur.getTimestamp()-prev.getTimestamp());
                    }
                }
                
                prev = cur;
            }
        }
        
        if (!f_scopeFinalized) {
            state.finalizeScope();
        }
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Finished Process Initial AU="+startau);
        }
    }
    
    /**
     * This method initializes the call details based on current
     * state and the supplied response sent.
     * 
     * @param state The state
     * @param rs The response sent event
     * @param call The call
     */
    protected static void initializeResponseSent(CTState state, ResponseSent rs, Call call) {
        
        // Check if call node found
        if (call == null) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("No call, so unable to initialize ResponseSent '"
                            +rs+"' for state="+state);
            }
            return;
        }
        
        call.setResponse(rs.getContent());
        
        // Add further properties from the response
        call.getProperties().putAll(rs.getProperties());
        
        RPCActivityType rr=state.getSOAActivity(ResponseReceived.class,
                rs.getServiceType(), rs.getOperation());
        
        if (rr != null) {
            call.setResponseLatency(rr.getTimestamp()-rs.getTimestamp());
        }
        
        // Set duration of call, if based on server side scope
        if (state.getTriggerActivities().get(call)
                    instanceof RequestReceived) {
            call.setDuration(rs.getTimestamp()
                    - state.getTriggerActivities().get(call).getTimestamp());
        }
        
        // If fault, then need to set the details on the Call
        if (rs.getFault() != null && rs.getFault().trim().length() > 0) {
            call.setFault(rs.getFault());
            
            call.setStatus(Status.Fail);
        }
    }

    /**
     * This method initializes the call details based on current
     * state and the supplied response received event.
     * 
     * @param state The state
     * @param rr The response received event
     */
    protected static void initializeResponseReceived(CTState state, ResponseReceived rr) {
        
        // Find completed call for this operation
        for (int j=state.getCompletedCallStack().size()-1; j >= 0; j--) {
            Call c=state.getCompletedCallStack().get(j);
            
            // Set duration of call, if based on client side scope
            if (state.getTriggerActivities().get(c)
                        instanceof RequestSent) {
                RequestSent rs=(RequestSent)state.getTriggerActivities().get(c);

                if (rs.getOperation().equals(rr.getOperation())
                        && rs.getServiceType().equals(rr.getServiceType())) {
                    c.setDuration(rr.getTimestamp()
                            - rs.getTimestamp());
                    break;
                }   
            }
        }
    }
    
    /**
     * This method identifies whether processing should be postponed 
     * based on the current activity type.
     * 
     * @param state The state
     * @param au The activity unit
     * @param topLevel The top level units
     * @param cur The current activity type
     * @return Whether the processing of this unit should be postponed
     */
    protected static boolean shouldPostpone(CTState state, ActivityUnit au,
            java.util.List<ActivityUnit> topLevel, ActivityType cur) {
        boolean ret=false;
        
        Call call=(state.getCallStack().size() > 0 ? state.getCallStack().peek() : null);

        // Check RequestReceived, then check whether
        // activity unit is top level - otherwise
        // need to postpone processing of this activity
        // unit until the send request has been processed
        if (cur instanceof RequestReceived && call == null
                && !topLevel.contains(au)) {
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Postpone processing unit due to receiving request before it has been sent");     
            }
            ret = true;
        }
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Should postpone processing of unit="+au+" cur="+cur+"? ret="+ret);     
        }

        return (ret);
    }
    
    /**
     * This method returns a call node associated with the supplied
     * activity event.
     * 
     * @param at The activity event
     * @return The Call node
     */
    protected static Call createCall(RPCActivityType at) {
        Call call = new Call();
        
        call.setInterface(at.getInterface());
        call.setOperation(at.getOperation());
        call.setComponent(at.getServiceType());
        
        call.setPrincipal(at.getPrincipal());
        
        call.getProperties().putAll(at.getProperties());
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Created call="+call);     
        }

        return (call);
    }
    
    /**
     * This method adds client or server specific information to the call,
     * depending upon whether the activity is the client sending or server
     * receiving the interaction.
     * 
     * @param call The call
     * @param au The activity unit
     * @param at The activity type
     */
    protected static void instrumentCall(Call call, ActivityUnit au, RPCActivityType at) {
        
        if (au != null && au.getOrigin() != null) {
            if (at instanceof RequestSent) {
                call.getProperties().put("client-host", au.getOrigin().getHost());
                call.getProperties().put("client-node", au.getOrigin().getNode());
            } else if (at instanceof RequestReceived) {
                call.getProperties().put("server-host", au.getOrigin().getHost());
                call.getProperties().put("server-node", au.getOrigin().getNode());
            }
        }
    }
    
    /**
     * This method returns a task associated with the supplied
     * activity event.
     * 
     * @param at The activity event
     * @return The task
     */
    protected static Task createTask(ActivityType at) {
        Task ret=new Task();
        
        // Transfer properties
        ret.getProperties().putAll(at.getProperties());
        
        try {
            BeanInfo bi=java.beans.Introspector.getBeanInfo(at.getClass());
            
            for (PropertyDescriptor pd : bi.getPropertyDescriptors()) {
                
                if (CallTraceUtil.shouldIncludeProperty(pd)) {
                    try {
                        Object value=pd.getReadMethod().invoke(at);
                         
                        if (value != null) {
                            ret.getProperties().put(pd.getDisplayName(), value.toString());
                        }
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, "Failed to get property '"
                                +pd.getDisplayName()+"'", ex);
                    }
                }
            }
        } catch (IntrospectionException e) {
            LOG.log(Level.SEVERE, MessageFormat.format(
                    java.util.PropertyResourceBundle.getBundle(
                    "call-trace.Messages").getString("CALL-TRACE-2"),
                        at.getClass().getName()), e);
        }
        
        // Set the description
        ret.setDescription(TaskDescriptorFactory.getTaskDescriptor(at).getDescription(at));
        
        ret.setStatus(getStatus(at));
        
        return (ret);
    }
    
    /**
     * This method determines the status for the supplied
     * activity type.
     * 
     * @param at The activity type
     * @return The status
     */
    protected static Status getStatus(ActivityType at) {
        Status ret=Status.Success;
        
        if (at instanceof LogMessage) {
            LogMessage lm=(LogMessage)at;
            
            if (lm.getLevel() == LogMessage.Level.Warning) {
                ret = Status.Warning;
            } else if (lm.getLevel() == LogMessage.Level.Error) {
                ret = Status.Fail;
            }
        }
        
        return (ret);
    }
    
    /**
     * This method identifies the top level activity units that
     * contain receive request activities, with no corresponding
     * send request.
     * 
     * @param state The state
     * @return The list of top level activity units
     */
    protected static java.util.List<ActivityUnit> getTopLevelAUs(CTState state) {
        java.util.List<ActivityUnit> ret=new java.util.ArrayList<ActivityUnit>();
        
        // Identify top level candidates - where a receive request
        // exists with no equivalent send request
        for (ActivityUnit au : state.getActivityUnits()) {
            
            for (ActivityType at : au.getActivityTypes()) {
                if (at instanceof RequestReceived
                        && state.getSOAActivity(RequestSent.class,
                            ((RequestReceived)at).getServiceType(),
                            ((RequestReceived)at).getOperation()) == null) {
                    ret.add(au);
                    
                    continue;
                }
            }
        }
        
        return (ret);
    }
    
    /**
     * This class provides a cursor for working through the
     * activity types associated with the unit.
     *
     */
    public static class ActivityUnitCursor {
        
        private ActivityUnit _unit=null;
        private int _index=0;
        
        /**
         * This is the constructor for the cursor.
         * 
         * @param unit The activity unit
         */
        public ActivityUnitCursor(ActivityUnit unit) {
            _unit = unit;
        }
        
        /**
         * This method returns the list of remaining activity
         * types that have not yet been visited using the
         * cursor.
         * 
         * @return The list of remaining activity types
         */
        public java.util.List<ActivityType> getActivityTypes() {
            java.util.List<ActivityType> ret=new java.util.ArrayList<ActivityType>();
            
            for (int i=_index; i < _unit.getActivityTypes().size(); i++) {
                ret.add(_unit.getActivityTypes().get(i));
            }
            
            return (ret);
        }
        
        /**
         * This method peeks at the next activity type.
         * 
         * @return The activity type
         */
        public ActivityType peek() {
            
            if (_index < _unit.getActivityTypes().size()) {
                return (_unit.getActivityTypes().get(_index));
            }
            
            return (null);
        }
        
        /**
         * This method returns the current activity type and
         * moves the cursor to the next entry.
         * 
         * @return The activity type
         */
        public ActivityType next() {
            
            if (_index < _unit.getActivityTypes().size()) {
                return (_unit.getActivityTypes().get(_index++));
            }
            
            return (null);
        }
        
    }
    
    /**
     * This class maintains state information associated with the
     * derivation of a call trace.
     *
     */
    public static class CTState {
        
        private java.util.List<String> _correlations=new java.util.ArrayList<String>();
        private java.util.List<ActivityUnit> _units=new java.util.ArrayList<ActivityUnit>();
        private java.util.Map<String,ActivityUnit> _unitIndex=new java.util.HashMap<String,ActivityUnit>();
        private java.util.Map<String,ActivityUnitCursor> _cursors=
                new java.util.HashMap<String,ActivityUnitCursor>();
        private java.util.Stack<Call> _callStack=new java.util.Stack<Call>();
        private java.util.Stack<Call> _completedCallStack=new java.util.Stack<Call>();
        private java.util.Stack<java.util.List<TraceNode>> _tasksStack=new java.util.Stack<java.util.List<TraceNode>>();
        private java.util.Map<Call,RPCActivityType> _triggerActivity=
                new java.util.HashMap<Call,RPCActivityType>();
        
        /**
         * This method determines whether the supplied correlation
         * key is already initialized.
         * 
         * @param correlation The correlation to check
         * @return Whether the correlation key is already initialized
         */
        public boolean isCorrelationInitialized(String correlation) {
            return (_correlations.contains(correlation));
        }
        
        /**
         * This method indicates that the supplied correlation
         * key has now been initialized.
         * 
         * @param correlation The correlation key
         */
        public void initialized(String correlation) {
            if (!_correlations.contains(correlation)) {
                _correlations.add(correlation);
            }
        }
        
        /**
         * This method determines whether the activity unit,
         * associated with the supplied id, has already been loaded.
         * 
         * @param id The id
         * @return Whether the activity unit has been loaded
         */
        public boolean isActivityUnitLoaded(String id) {
            return (_unitIndex.containsKey(id));
        }
        
        /**
         * This method adds the supplied activity unit to the
         * state information.
         * 
         * @param au The activity unit
         */
        public void add(ActivityUnit au) {
            _units.add(au);
            _unitIndex.put(au.getId(), au);
            _cursors.put(au.getId(), new ActivityUnitCursor(au));
        }
        
        /**
         * This method returns the list of activity units.
         * 
         * @return The activity units
         */
        public java.util.List<ActivityUnit> getActivityUnits() {
            return (_units);
        }
        
        /**
         * This method returns the map of call to trigger activity.
         * 
         * @return The map of call to trigger activities
         */
        public java.util.Map<Call,RPCActivityType> getTriggerActivities() {
            return (_triggerActivity);
        }
        
        /**
         * This method returns the activity unit associated with
         * the supplied id.
         * 
         * @param id The id
         * @return The activity unit, or null if not found
         */
        public ActivityUnit getActivityUnit(String id) {
            return (_unitIndex.get(id));
        }
        
        /**
         * This method returns the cursor associated with the
         * supplied activity unit.
         * 
         * @param au The activity unit
         * @return The cursor, or null if not found
         */
        public ActivityUnitCursor getCursor(ActivityUnit au) {
            return (_cursors.get(au.getId()));
        }
        
        /**
         * This method sorts the list of activity units
         * based on time.
         */
        public void sortActivityUnitsByTime() {
            Collections.sort(_units, new Comparator<ActivityUnit>() {

                public int compare(ActivityUnit o1, ActivityUnit o2) {
                    return ((int)(o1.getActivityTypes().get(0).getTimestamp()
                            - o2.getActivityTypes().get(0).getTimestamp()));
                }
                
            });
        }
        
        /**
         * This method returns the call stack.
         * 
         * @return The stack of Call nodes
         */
        public java.util.Stack<Call> getCallStack() {
            return (_callStack);
        }
        
        /**
         * This method returns the completed call stack.
         * 
         * @return The stack of completed Call nodes
         */
        public java.util.Stack<Call> getCompletedCallStack() {
            return (_completedCallStack);
        }
        
        /**
         * This method returns the tasks stack.
         * 
         * @return The stack of Tasks
         */
        public java.util.Stack<java.util.List<TraceNode>> getTasksStack() {
            return (_tasksStack);
        }
        
        /**
         * This method finalizes the set of tasks within the current
         * task list and then pops the relevant stacks.
         */
        public void finalizeScope() {
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Finalize scope");
            }
            
            // If no work performed in this scope, then just return
            if (getTasksStack().isEmpty()) {
                return;
            }
            
            java.util.List<TraceNode> tasks=getTasksStack().peek();
            long duration=0;
            Status status=Status.Success;
            
            for (TraceNode task : tasks) {
                duration += task.getDuration();
                
                if (task.getStatus().ordinal() > status.ordinal()) {
                    status = task.getStatus();
                }
            }
            
            if (duration > 0) {
                for (TraceNode task : tasks) {
                    task.setPercentage((int)(((double)task.getDuration()/duration) * 100));
                }
            }
            
            Call call=(getCallStack().size()>0 ? getCallStack().pop() : null);
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Popping call="+call);
                LOG.finest("Popping tasks="+getTasksStack().peek());
            }
            
            if (call != null) {
                _completedCallStack.push(call);
                
                // Only overwrite the status of the call, if the lowest status
                // of its contained task nodes is not success, and it is
                // a lower status than warning (i.e. to avoid reducing the
                // status of the call node based on its contents)
                if (status != Status.Success
                        && call.getStatus().ordinal() < Status.Warning.ordinal()) {
                    call.setStatus(Status.Warning);
                }
            }
            
            getTasksStack().pop();
        }
        
        /**
         * This method identifies the RPC activity associated with the
         * specified class, service type and operation.
         * 
         * @param cls The class
         * @param serviceType The service type
         * @param operation The operation
         * @return The RPC activity, or null if not found
         */
        protected RPCActivityType getSOAActivity(Class<?> cls,
                        String serviceType, String operation) {
            
            // Use the activity unit order found in the units field
            for (ActivityUnit au : _units) {
                ActivityUnitCursor cursor=_cursors.get(au.getId());
                
                for (ActivityType at : cursor.getActivityTypes()) {
                    if (at.getClass() == cls
                            && ((RPCActivityType)at).getServiceType().equals(serviceType)
                            && ((RPCActivityType)at).getOperation().equals(operation)) {
                        return ((RPCActivityType)at);
                    }
                }
            }
            
            return (null);
        }
        
    }
}
