/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.rtgov.ui.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Models a single node in a call trace.
 *
 * @author eric.wittmann@redhat.com
 */
@Portable
public class TraceNodeBean {

    private String type;
    private String iface;
    private String operation;
    private String fault;
    private String component;
    private String request;
    private String response;
    private String principal;
    private long requestLatency = -1;
    private long responseLatency = -1;

    private long duration = -1;
    private int percentage = -1;
    private String status;
    private Map<String, String> properties = new HashMap<String, String>();
    private String description;
    private List<TraceNodeBean> tasks = new ArrayList<TraceNodeBean>();

    /**
     * Constructor.
     */
    public TraceNodeBean() {
    }

    /**
     * @return the duration
     */
    public long getDuration() {
        return duration;
    }

    /**
     * @return the percentage
     */
    public int getPercentage() {
        return percentage;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @return the properties
     */
    public Map<String, String> getProperties() {
        return properties;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the tasks
     */
    public List<TraceNodeBean> getTasks() {
        return tasks;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     * @param percentage the percentage to set
     */
    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @param properties the properties to set
     */
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param tasks the tasks to set
     */
    public void setTasks(List<TraceNodeBean> tasks) {
        this.tasks = tasks;
    }

    /**
     * @return the iface
     */
    public String getIface() {
        return iface;
    }

    /**
     * @return the operation
     */
    public String getOperation() {
        return operation;
    }

    /**
     * @return the fault
     */
    public String getFault() {
        return fault;
    }

    /**
     * @return the component
     */
    public String getComponent() {
        return component;
    }

    /**
     * @return the request
     */
    public String getRequest() {
        return request;
    }

    /**
     * @return the response
     */
    public String getResponse() {
        return response;
    }

    /**
     * @return the principal
     */
    public String getPrincipal() {
        return principal;
    }

    /**
     * @return the requestLatency
     */
    public long getRequestLatency() {
        return requestLatency;
    }

    /**
     * @return the responseLatency
     */
    public long getResponseLatency() {
        return responseLatency;
    }

    /**
     * @param iface the iface to set
     */
    public void setIface(String iface) {
        this.iface = iface;
    }

    /**
     * @param operation the operation to set
     */
    public void setOperation(String operation) {
        this.operation = operation;
    }

    /**
     * @param fault the fault to set
     */
    public void setFault(String fault) {
        this.fault = fault;
    }

    /**
     * @param component the component to set
     */
    public void setComponent(String component) {
        this.component = component;
    }

    /**
     * @param request the request to set
     */
    public void setRequest(String request) {
        this.request = request;
    }

    /**
     * @param response the response to set
     */
    public void setResponse(String response) {
        this.response = response;
    }

    /**
     * @param principal the principal to set
     */
    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    /**
     * @param requestLatency the requestLatency to set
     */
    public void setRequestLatency(long requestLatency) {
        this.requestLatency = requestLatency;
    }

    /**
     * @param responseLatency the responseLatency to set
     */
    public void setResponseLatency(long responseLatency) {
        this.responseLatency = responseLatency;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
}
