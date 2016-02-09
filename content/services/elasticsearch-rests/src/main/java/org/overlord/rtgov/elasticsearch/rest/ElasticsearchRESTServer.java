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
package org.overlord.rtgov.elasticsearch.rest;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.HeaderGroup;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the authenticating proxy for the ElasticSearch server.
 * 
 * Based on the http servlet proxy implemented by David Smiley:
 * https://github.com/dsmiley/HTTP-Proxy-Servlet
 */
public class ElasticsearchRESTServer extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG=Logger.getLogger(ElasticsearchRESTServer.class.getName());

    private ElasticsearchHttpClient _client=new ElasticsearchHttpClient();

    /**
     * {@inheritDoc}
     */
    protected void service(HttpServletRequest servletRequest, HttpServletResponse servletResponse)
                        throws IOException {

        if (isSupported(servletRequest)) {
            try {
                HttpResponse resp=_client.process(servletRequest);
                
                servletResponse.setStatus(resp.getStatusLine().getStatusCode());
    
                copyResponseHeaders(resp, servletResponse);
    
                // Send the content to the client
                copyResponseContent(resp, servletResponse);
    
            } catch (ConnectException ce) {
                // Return "Service Unavailable" status code
                servletResponse.setStatus(503);
                
            } catch (IOException ioe) {
                throw ioe;
            } catch (Exception e) {
                throw new IOException("Failed to process gateway request", e);
            }
        } else {
            if (LOG.isLoggable(Level.FINER)) {
                LOG.finer("Forbidden request: method="+servletRequest.getMethod()+" path="+servletRequest.getPathInfo());
            }
            
            // Return "Forbidden" status code
            servletResponse.setStatus(403);
        }
    }
    
    /**
     * This method determines whether the supplied request is supported by this gateway.
     * 
     * @param servletRequest The REST request
     * @return Whether the request is supported
     */
    protected boolean isSupported(HttpServletRequest servletRequest) {
        if (servletRequest.getMethod().equalsIgnoreCase("get")) {
            return (true);
        } else if (servletRequest.getMethod().equalsIgnoreCase("post")) {
            if (servletRequest.getPathInfo().endsWith("/_search")) {
                return (true);
            }
        } else if (servletRequest.getMethod().equalsIgnoreCase("put")) {
            if (servletRequest.getPathInfo().startsWith("/kibana-int/dashboard")) {
                return (true);
            }
        }
        return (false);
    }

    /** 
     * Copy proxied response headers back to the servlet client. 
     * 
     * @param proxyResponse The response from the target server
     * @param servletResponse The response back to the client
     */
    protected void copyResponseHeaders(HttpResponse proxyResponse, HttpServletResponse servletResponse) {

        for (int i=0; i < proxyResponse.getAllHeaders().length; i++) {
            Header header=proxyResponse.getAllHeaders()[i];

            if (HOPBYHOPHEADERS.containsHeader(header.getName())) {
                continue;
            }

            servletResponse.addHeader(header.getName(), header.getValue());
        }
    }

    /** 
     * Copy response body data (the entity) from the proxy to the servlet client.
     * 
     * Ensures that proxyInputStream will be closed in any case by closing the inputStream
     * @see org.apache.http.HttpEntity.getContent()
     * 
     * @param proxyResponse The response from the target server
     * @param servletResponse The response back to the client
     * @throws IOException Failed to copy content
     */
    protected void copyResponseContent(HttpResponse proxyResponse, HttpServletResponse servletResponse) throws IOException {
        InputStream proxyInputStream = null;
        OutputStream servletOutputStream = null;

        try {
            proxyInputStream = proxyResponse.getEntity().getContent();
            servletOutputStream = servletResponse.getOutputStream();
            
            proxyResponse.getEntity().writeTo(servletOutputStream);
        } finally {
            secureCloseStream(proxyInputStream);
            secureCloseStream(servletOutputStream);
        }
    }

    private void secureCloseStream(Closeable servletOutputStream) {
        try {
            servletOutputStream.close();
        } catch (Exception e) {
            log(e.getMessage(),e);
        }
    }

    /** These are the "hop-by-hop" headers that should not be copied.
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html
     * I use an HttpClient HeaderGroup class instead of Set<String> because this
     * approach does case insensitive lookup faster.
     */
    private static final HeaderGroup HOPBYHOPHEADERS;
    
    static {
        HOPBYHOPHEADERS = new HeaderGroup();
        String[] headers = new String[] {
                "Connection", "Keep-Alive", "Proxy-Authenticate", "Proxy-Authorization",
                "TE", "Trailers", "Transfer-Encoding", "Upgrade" };
        for (String header : headers) {
            HOPBYHOPHEADERS.addHeader(new BasicHeader(header, null));
        }
    }

}
