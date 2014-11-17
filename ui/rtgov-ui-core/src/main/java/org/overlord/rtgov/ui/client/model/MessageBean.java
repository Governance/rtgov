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

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * This bean represents a request message associated with a situation.
 */
@Portable
public class MessageBean {

    private String content=null;
    
    private java.util.Map<String,String> headers=new java.util.HashMap<String,String>();
    private java.util.Map<String,String> headerFormats=new java.util.HashMap<String,String>();    
    
    private String principal=null;

    /**
     * Constructor.
     */
    public MessageBean() {
    }

    /**
     * @return the contents
     */
    public String getContent() {
        return content;
    }

    /**
     * @param contents the contents
     */
    public void setContent(String contents) {
        this.content = contents;
    }

    /**
     * This method returns the headers.
     * 
     * @return The headers
     */
    public java.util.Map<String,String> getHeaders() {
        return (headers);
    }
    
    /**
     * This method sets the headers.
     * 
     * @param headers The headers
     */
    public void setHeaders(java.util.Map<String,String> headers) {
        this.headers = headers;
    }

    /**
     * This method returns the header formats.
     * 
     * @return The header formats
     */
    public java.util.Map<String,String> getHeaderFormats() {
        return (headerFormats);
    }
    
    /**
     * This method sets the header formats.
     * 
     * @param headerFormats The header formats
     */
    public void setHeaderFormats(java.util.Map<String,String> headerFormats) {
        this.headerFormats = headerFormats;
    }

    /**
     * @return the principal
     */
    public String getPrincipal() {
        return principal;
    }

    /**
     * @param principal the principal
     */
    public void setPrincipal(String principal) {
        this.principal = principal;
    }

}
