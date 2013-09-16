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
package org.overlord.rtgov.activity.server.rest.client;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Singleton;

import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.activity.server.ActivityServer;
import org.overlord.rtgov.activity.server.QuerySpec;
import org.overlord.rtgov.activity.util.ActivityUtil;

import org.overlord.rtgov.common.util.RTGovProperties;

/**
 * This class provides the REST client implementation of the activity server.
 *
 */
@Singleton
public class RESTActivityServer implements ActivityServer {

    private static final Logger LOG=Logger.getLogger(RESTActivityServer.class.getName());
    
    private static final String STORE="/overlord-rtgov/activity/store";
    private static final String UNIT="/overlord-rtgov/activity/unit";
    private static final String QUERY="/overlord-rtgov/activity/query";
    private static final String EVENTS="/overlord-rtgov/activity/events";
    
    private String _serverURL;            
    private String _serverUsername;
    private String _serverPassword;

    /**
     * The default constructor.
     */
    public RESTActivityServer() {
        _serverURL = RTGovProperties.getProperty("RESTActivityServer.serverURL",
                                    "http://localhost:8080");
        _serverUsername = RTGovProperties.getProperty("RESTActivityServer.serverUsername",
                                    "");
        _serverPassword = RTGovProperties.getProperty("RESTActivityServer.serverPassword",
                                    "");
    }
    
    /**
     * This method sets the URL of the Activity Server.
     * 
     * @param url The URL
     */
    public void setServerURL(String url) {
        _serverURL = url;
    }

    /**
     * This method gets the URL of the Activity Server.
     * 
     * @return The URL
     */
    public String getServerURL() {
        return (_serverURL);
    }

    /**
     * This method sets the username for the Activity Server.
     * 
     * @param username The username
     */
    public void setServerUsername(String username) {
        _serverUsername = username;
    }

    /**
     * This method gets the username for the Activity Server.
     * 
     * @return The username
     */
    public String getServerUsername() {
        return (_serverUsername);
    }

    /**
     * This method sets the password for the Activity Server.
     * 
     * @param password The password
     */
    public void setServerPassword(String password) {
        _serverPassword = password;
    }

    /**
     * This method gets the password for the Activity Server.
     * 
     * @return The password
     */
    public String getServerPassword() {
        return (_serverPassword);
    }

    /**
     * This method initializes the authentication properties on the supplied
     * URL connection.
     * 
     * @param connection The connection
     */
    protected void initAuth(HttpURLConnection connection) {
        String userPassword = _serverUsername + ":" + _serverPassword;
        String encoding = org.apache.commons.codec.binary.Base64.encodeBase64String(userPassword.getBytes());
        
        StringBuffer buf=new StringBuffer(encoding);
        
        for (int i=0; i < buf.length(); i++) {
            if (Character.isWhitespace(buf.charAt(i))) {
                buf.deleteCharAt(i);
                i--;
            }
        }
        
        connection.setRequestProperty("Authorization", "Basic " + buf.toString());
    }
    
    /**
     * {@inheritDoc}
     */
    public void store(List<ActivityUnit> activities) throws Exception {
        URL storeUrl = new URL(_serverURL+STORE);
        
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("RESTActivityServer["+storeUrl+"] store: "+activities);
        }
        
        HttpURLConnection connection = (HttpURLConnection) storeUrl.openConnection();

        initAuth(connection);

        connection.setRequestMethod("POST");
        
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setAllowUserInteraction(false);
        connection.setRequestProperty("Content-Type",
                    "application/json");

        java.io.OutputStream os=connection.getOutputStream();
        
        os.write(ActivityUtil.serializeActivityUnitList(activities));
        
        os.flush();
        os.close();
        
        java.io.InputStream is=connection.getInputStream();
        
        byte[] b = new byte[is.available()];
        
        is.read(b);
        
        is.close();

        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("RESTActivityServer result: "+new String(b));
        }
    }

    /**
     * {@inheritDoc}
     */
    public ActivityUnit getActivityUnit(String id) throws Exception {
        ActivityUnit ret=null;
        
        URL queryUrl = new URL(_serverURL+UNIT+"?id="+id);
        
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("RESTActivityServer["+queryUrl+"] getActivityUnit: "+id);
        }
        
        HttpURLConnection connection = (HttpURLConnection) queryUrl.openConnection();
        
        initAuth(connection);

        connection.setRequestMethod("GET");

        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setAllowUserInteraction(false);
        connection.setRequestProperty("Content-Type",
                    "application/json");

        java.io.InputStream is=connection.getInputStream();
        
        byte[] b=new byte[is.available()];
        
        is.read(b);
        
        is.close();
        
        ret = ActivityUtil.deserializeActivityUnit(b);
        
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("RESTActivityServer getActivityUnit result: "+ret);
        }
        
        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    public List<ActivityType> getActivityTypes(Context context,
                            long from, long to) throws Exception {        
        URL queryUrl = new URL(_serverURL+EVENTS+"?type="+context.getType()
                +"&value="+context.getValue()
                +"&from="+from
                +"&to="+to);

        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("RESTActivityServer["+queryUrl+"] getActivityTypes: "+context
                    +" from="+from+" to="+to);
        }
        
        return (getActivityTypes(queryUrl));
    }
        
    /**
     * {@inheritDoc}
     */
    public List<ActivityType> getActivityTypes(Context context) throws Exception {
        
        URL queryUrl = new URL(_serverURL+EVENTS+"?type="+context.getType()
                +"&value="+context.getValue());

        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("RESTActivityServer["+queryUrl+"] getActivityTypes: "+context);
        }
        
        return (getActivityTypes(queryUrl));
    }
    
    /**
     * This method retrieves the activity types associated with the supplied
     * query URL.
     * 
     * @param queryUrl The query URL
     * @return The list of activity types
     * @throws Exception Failed to get activity types
     */
    protected List<ActivityType> getActivityTypes(URL queryUrl) throws Exception {
        List<ActivityType> ret=null;
        
        HttpURLConnection connection = (HttpURLConnection) queryUrl.openConnection();
        
        initAuth(connection);

        connection.setRequestMethod("GET");

        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setAllowUserInteraction(false);
        connection.setRequestProperty("Content-Type",
                    "application/json");

        java.io.InputStream is=connection.getInputStream();

        byte[] b = new byte[is.available()];
        is.read(b);
        
        ret = ActivityUtil.deserializeActivityTypeList(b);
        
        is.close();
        
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("RESTActivityServer getActivityTypes result: "+ret);
        }
        
        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    public List<ActivityType> query(QuerySpec query) throws Exception {
        List<ActivityType> ret=null;
        
        URL queryUrl = new URL(_serverURL+QUERY);
        
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("RESTActivityServer["+queryUrl+"] query: "+query);
        }
        
        HttpURLConnection connection = (HttpURLConnection) queryUrl.openConnection();
        
        initAuth(connection);

        connection.setRequestMethod("POST");

        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setAllowUserInteraction(false);
        connection.setRequestProperty("Content-Type",
                    "application/json");

        java.io.OutputStream os=connection.getOutputStream();
        
        byte[] b=ActivityUtil.serializeQuerySpec(query);
        os.write(b);
        
        os.flush();
        os.close();
        
        java.io.InputStream is=connection.getInputStream();

        b = new byte[is.available()];
        is.read(b);
        
        ret = ActivityUtil.deserializeActivityTypeList(b);
        
        is.close();
        
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("RESTActivityServer result: "+ret);
        }
        
        return (ret);
    }

}
