/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-11, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.rtgov.activity.server.rest.client;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.server.ActivityServer;
import org.overlord.rtgov.activity.server.QuerySpec;
import org.overlord.rtgov.activity.util.ActivityUtil;

import org.overlord.rtgov.common.util.RTGovConfig;

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
    
    @Inject @RTGovConfig
    private String _serverURL="http://localhost:8080";
            
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
     * {@inheritDoc}
     */
    public void store(List<ActivityUnit> activities) throws Exception {
        URL storeUrl = new URL(_serverURL+STORE);
        
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("RESTActivityServer["+storeUrl+"] store: "+activities);
        }
        
        LOG.info("STORE to "+storeUrl);

        HttpURLConnection connection = (HttpURLConnection) storeUrl.openConnection();
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
    public List<ActivityType> getActivityTypes(String context) throws Exception {
        List<ActivityType> ret=null;
        
        URL queryUrl = new URL(_serverURL+EVENTS+"?context="+context);
        
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("RESTActivityServer["+queryUrl+"] getActivityTypes: "+context);
        }
        
        HttpURLConnection connection = (HttpURLConnection) queryUrl.openConnection();
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
