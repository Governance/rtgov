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
package org.overlord.bam.activity.server.rest.client;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;
import org.overlord.bam.activity.model.ActivityUnit;
import org.overlord.bam.activity.server.ActivityServer;
import org.overlord.bam.activity.server.QuerySpec;
import org.overlord.bam.activity.util.ActivityUtil;

/**
 * This class provides the REST client implementation of the activity server.
 *
 */
public class RESTActivityServer implements ActivityServer {

    private static final Logger LOG=Logger.getLogger(RESTActivityServer.class.getName());
    
    private static final ObjectMapper MAPPER=new ObjectMapper();
    
    private static final String STORE="/overlord-bam/activity/store";
    private static final String QUERY="/overlord-bam/activity/query";
    
    private String _url="http://localhost:8080";
            
    /**
     * This method sets the URL of the Activity Server.
     * 
     * @param url The URL
     */
    public void setServerURL(String url) {
        _url = url;
    }

    /**
     * This method gets the URL of the Activity Server.
     * 
     * @return The URL
     */
    public String getServerURL() {
        return (_url);
    }

    /**
     * {@inheritDoc}
     */
    public void store(List<ActivityUnit> activities) throws Exception {
        URL storeUrl = new URL(_url+STORE);
        
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("RESTActivityServer store: "+activities);
        }

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
        
        byte[] b=new byte[is.available()];
        
        is.read(b);
        
        is.close();

        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("RESTActivityServer result: "+new String(b));
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<ActivityUnit> query(QuerySpec query) throws Exception {
        List<ActivityUnit> ret=null;
        
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("RESTActivityServer query: "+query);
        }
        
        URL queryUrl = new URL(_url+QUERY);
        
        HttpURLConnection connection = (HttpURLConnection) queryUrl.openConnection();
        connection.setRequestMethod("POST");

        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setAllowUserInteraction(false);
        connection.setRequestProperty("Content-Type",
                    "application/json");

        java.io.OutputStream os=connection.getOutputStream();
        
        MAPPER.writeValue(os, query);
        
        os.flush();
        os.close();
        
        java.io.InputStream is=connection.getInputStream();

        byte[] b=new byte[is.available()];
        
        is.read(b);
        
        is.close();
        
        ret = ActivityUtil.deserializeActivityUnitList(b);
        
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("RESTActivityServer result: "+ret);
        }
        
        return (ret);
    }

}
