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
package org.overlord.rtgov.call.trace.model;

/**
 * This class represents a call, such as a method or
 * service operation invocation.
 *
 */
public class Call extends ParentNode {

    private String _interface=null;
    private String _operation=null;
    private String _fault=null;
    private String _component=null;
    private String _request=null;
    private String _response=null;
    private String _principal=null;
    private long _requestLatency=0;
    private long _responseLatency=0;
    
    /**
     * This method returns the interface
     * that is being called.
     * 
     * @return The interface
     */
    public String getInterface() {
        return (_interface);
    }
    
    /**
     * This method sets the interface
     * that is being called.
     * 
     * @param intf The interface
     */
    public void setInterface(String intf) {
        _interface = intf;
    }
    
    /**
     * This method returns the name of the operation
     * that is being called.
     * 
     * @return The operation
     */
    public String getOperation() {
        return (_operation);
    }
    
    /**
     * This method sets the name of the operation
     * that is being called.
     * 
     * @param operation The operation
     */
    public void setOperation(String operation) {
        _operation = operation;
    }
    
    /**
     * This method returns the name of the optional fault
     * that is being returned.
     * 
     * @return The fault
     */
    public String getFault() {
        return (_fault);
    }
    
    /**
     * This method sets the name of the optional fault
     * that is being returned.
     * 
     * @param fault The fault
     */
    public void setFault(String fault) {
        _fault = fault;
    }
    
    /**
     * This method returns the name of the component
     * (or service) that is being called.
     * 
     * @return The component
     */
    public String getComponent() {
        return (_component);
    }
    
    /**
     * This method sets the name of the component
     * (or service) that is being called.
     * 
     * @param component The component
     */
    public void setComponent(String component) {
        _component = component;
    }
    
    /**
     * This method returns the request content.
     * 
     * @return The request
     */
    public String getRequest() {
        return (_request);
    }
    
    /**
     * This method sets the request content.
     * 
     * @param request The request
     */
    public void setRequest(String request) {
        _request = request;
    }
    
    /**
     * This method returns the response content.
     * 
     * @return The response
     */
    public String getResponse() {
        return (_response);
    }
    
    /**
     * This method sets the response content.
     * 
     * @param response The response
     */
    public void setResponse(String response) {
        _response = response;
    }
    
    /**
     * This method returns the principal.
     * 
     * @return The principal
     */
    public String getPrincipal() {
        return (_principal);
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
     * This method returns the request latency.
     * 
     * @return The request latency
     */
    public long getRequestLatency() {
        return (_requestLatency);
    }
    
    /**
     * This method sets the request latency.
     * 
     * @param request The request latency
     */
    public void setRequestLatency(long request) {
        _requestLatency = request;
    }
    
    /**
     * This method returns the response latency.
     * 
     * @return The response latency
     */
    public long getResponseLatency() {
        return (_responseLatency);
    }
    
    /**
     * This method sets the response latency.
     * 
     * @param response The response latency
     */
    public void setResponseLatency(long response) {
        _responseLatency = response;
    }
    
}
