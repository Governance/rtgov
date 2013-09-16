/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-13, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.rtgov.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.MessageFormat;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.overlord.rtgov.activity.collector.ActivityCollector;
import org.overlord.rtgov.activity.model.soa.RequestReceived;
import org.overlord.rtgov.activity.model.soa.RequestSent;
import org.overlord.rtgov.activity.model.soa.ResponseReceived;
import org.overlord.rtgov.activity.model.soa.ResponseSent;

/**
 * This class provides helper functions for creating proxies that record
 * activity events based on Java invocations.
 *
 */
public class ActivityProxyHelper {
    
    private static final Logger LOG=Logger.getLogger(ActivityProxyHelper.class.getName());
    
    private static ActivityCollector _collector=null;
    
    /**
     * This method sets the activity collector.
     * 
     * @param collector The activity collector
     */
    public static ActivityProxyHelper setActivityCollector(ActivityCollector collector) {
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("ActivityProxyHelper.setActivityCollector="+collector);
        }
        _collector = collector;
        
        return (new ActivityProxyHelper());
    }

    /**
     * This class creates a proxy for reporting activities based on the caller
     * invoking methods on the callee, via the supplied interface.
     * 
     * @param intf The interface definition for the component being invoked
     * @param caller The caller
     * @param callee The service component
     * @return The proxy
     */
    public static <T> T createClientProxy(Class<T> intf, Object caller, T callee) {
        return (createClientProxy(intf, caller, callee, new JSONContentSerializer()));
    }
    
    /**
     * This class creates a proxy for reporting activities based on the caller
     * invoking methods on the callee, via the supplied interface.
     * 
     * @param intfName The interface name for the component being invoked
     * @param caller The caller
     * @param callee The service component
     * @return The proxy
     */
    public static <T> T createClientProxy(String intfName, Object caller, T callee) {
        return (createClientProxy(intfName, caller, callee, new JSONContentSerializer()));
    }
    
    /**
     * This class creates a proxy for reporting activities based on the caller
     * invoking methods on the callee, via the supplied interface.
     * 
     * @param intf The interface definition for the component being invoked
     * @param caller The caller
     * @param callee The service component
     * @param ser The content serializer
     * @return The proxy
     */
    @SuppressWarnings("unchecked")
    public static <T> T createClientProxy(String intfName, Object caller, T callee,
                                ContentSerializer ser) {
        Class<?> intf=null;
        
        for (Class<?> i : callee.getClass().getInterfaces()) {
            if (i.getName().equals(intfName)) {
                intf = i;
                break;
            }
        }
        
        if (intf != null) {
            return (createClientProxy((Class<T>)intf, caller, callee, ser));
        } else {
            LOG.severe("Interface '"+intfName+"' does not exist on class '"+callee.getClass()+"'");
            return (null);
        }
    }
    
    /**
     * This class creates a proxy for reporting activities based on the caller
     * invoking methods on the callee, via the supplied interface.
     * 
     * @param intf The interface definition for the component being invoked
     * @param caller The caller
     * @param callee The service component
     * @param ser The content serializer
     * @return The proxy
     */
    @SuppressWarnings("unchecked")
    public static <T> T createClientProxy(final Class<T> intf, final Object caller, final T callee,
                                final ContentSerializer ser) {
        return ((T)Proxy.newProxyInstance(callee.getClass().getClassLoader(), new Class<?>[]{intf}, new InvocationHandler() {

            public Object invoke(Object proxy, Method method, Object[] args)
                    throws Throwable {
                String content=null;
                String mesgType=null;
                String reqId=null;
                String respId=null;
                boolean scopeStarted=false;
                boolean supportedMethod=!method.getName().equals("toString");
                
                /*
                try {
                    supportedMethod = (intf.getMethod(method.getName(), method.getParameterTypes()) != null);
                } catch (Throwable t) {
                    // Ignore
                }
                */

                if (supportedMethod) {
                    if (args != null && args.length > 0) {
                        mesgType = "";
                        
                        try {
                            content = ser.serialize(args);
                            
                            for (int i=0; i < args.length; i++) {
                                if (i > 0) {
                                    mesgType += ",";
                                }
                                mesgType += args[i].getClass().getName();
                            }
                        } catch (Exception e) {
                            LOG.log(Level.SEVERE, MessageFormat.format(
                                    java.util.PropertyResourceBundle.getBundle(
                                            "rtgov-client.Messages").getString("RTGOV-CLIENT-1"),
                                            method.getName()), e);
                        }
                    }
                    
                    // Check if initial activity in thread
                    if (!_collector.isScopeActive()) {
                        _collector.startScope();
                        scopeStarted = true;
                    }
                    
                    // Create a unique identifier for the request and response
                    reqId = UUID.randomUUID().toString();
                    respId = UUID.randomUUID().toString();
                    
                    if (caller != null) {
                        RequestSent rs=new RequestSent();
                        rs.setMessageId(reqId);
                        rs.setOperation(method.getName());
                        rs.setInterface(intf.getName());
                        rs.setServiceType(caller.getClass().getName());
                        rs.setContent(content);
                        rs.setMessageType(mesgType);
                        
                        _collector.record(rs);
                    }
                }
                
                // Invoke the target method
                String respContent=null;
                String faultName=null;
                Object resp=null;
                Throwable excResp=null;
                
                try {
                    resp = method.invoke(callee, args);
                    
                    respContent = ser.serialize(resp);
                    
                } catch (java.lang.reflect.InvocationTargetException e) {
                    faultName = e.getCause().getClass().getName();
                    
                    excResp = e.getCause();
                }
                
                if (supportedMethod) {
                    if (caller != null) {
                        ResponseReceived rr=new ResponseReceived();
                        rr.setMessageId(respId);
                        rr.setReplyToId(reqId);
                        rr.setOperation(method.getName());
                        rr.setFault(faultName);
                        rr.setInterface(intf.getName());
                        rr.setServiceType(caller.getClass().getName());
                        rr.setContent(respContent);
                        
                        if (resp != null) {
                            rr.setMessageType(resp.getClass().getName());
                        }
                        
                        _collector.record(rr);
                    }
                    
                    // Check if final activity in thread
                    if (scopeStarted) {
                        _collector.endScope();
                    }
                }
                
                if (excResp != null) {
                    throw excResp;
                }
                
                return (resp);
            }
            
        }));
    }
    
    /**
     * This class creates a service proxy for reporting activities associated with the
     * service being invoked.
     * 
     * @param intfName The interface name for the component being invoked
     * @param callee The service component
     * @return The proxy
     */
    @SuppressWarnings("unchecked")
    public static <T> T createServiceProxy(final Class<T> intf, final T callee) {
        return ((T)createServiceProxy(intf, callee, new JSONContentSerializer()));
    }
    
    /**
     * This class creates a service proxy for reporting activities associated with the
     * service being invoked.
     * 
     * @param intfName The interface name for the component being invoked
     * @param callee The service component
     * @return The proxy
     */
    public static Object createServiceProxy(String intfName, Object callee) {
        return (createServiceProxy(intfName, callee, new JSONContentSerializer()));
    }
    
    /**
     * This class creates a service proxy for reporting activities associated with the
     * service being invoked.
     * 
     * @param intfName The interface name for the component being invoked
     * @param callee The service component
     * @param ser The content serializer
     * @return The proxy
     */
    public static Object createServiceProxy(final String intfName, final Object callee, final ContentSerializer ser) {
        Class<?> intf=null;
        
        for (Class<?> i : callee.getClass().getInterfaces()) {
            if (i.getName().equals(intfName)) {
                intf = i;
                break;
            }
        }
        
        if (intf != null) {
            return (createServiceProxy(intf, callee, ser));
        } else {
            LOG.severe("Interface '"+intfName+"' does not exist on class '"+callee.getClass()+"'");
            return (null);
        }
    }
    
    /**
     * This class creates a service proxy for reporting activities associated with the
     * service being invoked.
     * 
     * @param intf The interface for the component being invoked
     * @param callee The service component
     * @param ser The content serializer
     * @return The proxy
     */
    public static Object createServiceProxy(final Class<?> intf, final Object callee, final ContentSerializer ser) {
        return (Proxy.newProxyInstance(callee.getClass().getClassLoader(), new Class<?>[]{intf}, new InvocationHandler() {

            public Object invoke(Object proxy, Method method, Object[] args)
                    throws Throwable {
                String content=null;
                String mesgType=null;
                String reqId=null;
                String respId=null;
                boolean scopeStarted=false;                    
                boolean supportedMethod=!method.getName().equals("toString");
                
                /*
                try {
                    supportedMethod = (intf.getMethod(method.getName(), method.getParameterTypes()) != null);
                } catch (Throwable t) {
                    // Ignore
                }
                */

                if (supportedMethod) {             
                    if (args != null && args.length > 0) {
                        mesgType = "";
                        
                        try {
                            content = ser.serialize(args);
                            
                            for (int i=0; i < args.length; i++) {
                                if (i > 0) {
                                    mesgType += ",";
                                }
                                mesgType += args[i].getClass().getName();
                            }
                        } catch (Exception e) {
                            LOG.log(Level.SEVERE, MessageFormat.format(
                                    java.util.PropertyResourceBundle.getBundle(
                                            "rtgov-client.Messages").getString("RTGOV-CLIENT-1"),
                                            method.getName()), e);
                        }
                    }
                    
                    // Check if initial activity in thread
                    if (!_collector.isScopeActive()) {
                        _collector.startScope();
                        scopeStarted = true;
                    }
                    
                    // Create a unique identifier for the request and response
                    reqId = UUID.randomUUID().toString();
                    respId = UUID.randomUUID().toString();
                    
                    if (callee != null) {                    
                        RequestReceived rr=new RequestReceived();
                        rr.setMessageId(reqId);
                        rr.setOperation(method.getName());
                        rr.setInterface(intf.getName());
                        rr.setServiceType(callee.getClass().getName());
                        rr.setContent(content);
                        rr.setMessageType(mesgType);
                        
                        _collector.record(rr);
                    }
                }
                
                // Invoke the target method
                String respContent=null;
                String faultName=null;
                Object resp=null;
                Throwable excResp=null;
                
                try {
                    resp = method.invoke(callee, args);
                    
                    respContent = ser.serialize(resp);
                    
                } catch (java.lang.reflect.InvocationTargetException e) {
                    faultName = e.getCause().getClass().getName();
                    
                    excResp = e.getCause();
                }
                
                if (supportedMethod) {
                    if (callee != null) {
                        ResponseSent rs=new ResponseSent();
                        rs.setMessageId(respId);
                        rs.setReplyToId(reqId);
                        rs.setOperation(method.getName());
                        rs.setFault(faultName);
                        rs.setInterface(intf.getName());
                        rs.setServiceType(callee.getClass().getName());
                        rs.setContent(respContent);
                        
                        if (resp != null) {
                            rs.setMessageType(resp.getClass().getName());
                        }
                        
                        _collector.record(rs);
                    }                
                    
                    // Check if final activity in thread
                    if (scopeStarted) {
                        _collector.endScope();
                    }
                }
                
                if (excResp != null) {
                    throw excResp;
                }
                
                return (resp);
            }            
        }));
    }
    
    /**
     * This abstract class provides the base for content serializer
     * implementations, which convert Java objects into a string
     * representation suitable for inclusion within the activity events.
     *
     */
    public static abstract class ContentSerializer {
        
        /**
         * This method serializes the supplied object into a
         * string representation.
         * 
         * @param obj The object
         * @return The string representation
         * @throws Exception Failed to serialize
         */
        public abstract String serialize(Object obj) throws Exception;
        
    }
    
    /**
     * This class provides a JSON based content serializer.
     *
     */
    public static class JSONContentSerializer extends ContentSerializer {
        
        private static final ObjectMapper MAPPER=new ObjectMapper();
        
        static {
            SerializationConfig config=MAPPER.getSerializationConfig().with(SerializationConfig.Feature.INDENT_OUTPUT);
            
            MAPPER.setSerializationConfig(config);
        }
        
        /**
         * {@inheritDoc}
         */
        public String serialize(Object obj) throws Exception {
            if (obj.getClass().isArray()) {
                obj = ((Object[])obj)[0];
            }
            return (MAPPER.writeValueAsString(obj));
        }
    }
}
