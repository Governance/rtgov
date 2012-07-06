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
package org.overlord.bam.samples.jbossas.activityclient;

import java.util.Random;

import javax.transaction.TransactionManager;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.overlord.bam.activity.collector.ActivityCollector;
import org.overlord.bam.activity.collector.CollectorContext;
import org.overlord.bam.activity.collector.activity.server.ActivityServerLogger;
import org.overlord.bam.activity.collector.embedded.EmbeddedActivityCollector;
import org.overlord.bam.activity.model.ActivityType;
import org.overlord.bam.activity.model.soa.ResponseReceived;
import org.overlord.bam.activity.model.soa.ResponseSent;
import org.overlord.bam.activity.server.rest.client.RESTActivityServer;

/**
 * This class provides a test client for sending sample activity
 * types to the activity server via the collector mechanism.
 *
 */
public class ActivityClient {
    
    private String _activityServerURL=null;
    private ActivityCollector _collector=null;
    private Random _random=new Random();

    /**
     * The main method.
     * 
     * @param args The arguments to the client
     */
    public static void main(String[] args) {
        
        if (args.length != 2) {
            System.err.println("Usage: ActivityClient <url> <filename>");
            System.exit(1);
        }
        
        ActivityClient ac=new ActivityClient(args[0]);
        
        ac.init();
        
        ac.send(args[1]);
        
        ac.close();
    }
    
    /**
     * The constructor initializing the server URL.
     * 
     * @param url The server URL
     */
    public ActivityClient(String url) {
        _activityServerURL = url;
    }
    
    /**
     * The initialization method.
     */
    public void init() {
        // Initialize the activity collector
        _collector = new EmbeddedActivityCollector();
        
        RESTActivityServer restc=new RESTActivityServer();
        restc.setServerURL(_activityServerURL);
        
        ActivityServerLogger activityUnitLogger=new ActivityServerLogger();        
        activityUnitLogger.setActivityServer(restc);
        
        activityUnitLogger.init();
        
        _collector.setActivityUnitLogger(activityUnitLogger);
        
        _collector.setCollectorContext(new TestCollectorContext());
    }
    
    /**
     * Close the activity client.
     */
    public void close() {
        // Wait before closing, to ensure all messages
        // have been flushed
        try {
            synchronized (this) {
                wait(2000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        _collector.getActivityUnitLogger().close();
    }
    
    /**
     * This method pre-processes the supplied activity type.
     * 
     * @param actType The activity type
     * @param txnId A transaction id
     */
    protected void preProcess(ActivityType actType, int txnId) {
        
        if (actType instanceof org.overlord.bam.activity.model.soa.RPCActivityType) {
            org.overlord.bam.activity.model.soa.RPCActivityType rpcType=
                    (org.overlord.bam.activity.model.soa.RPCActivityType)actType;
            
            rpcType.setMessageId(txnId+"-"+rpcType.getMessageId());
            
            if (rpcType instanceof ResponseSent) {
                ((ResponseSent)rpcType).setReplyToId(txnId+"-"
                        +((ResponseSent)rpcType).getReplyToId());
            }
            
            if (rpcType instanceof ResponseReceived) {
                ((ResponseReceived)rpcType).setReplyToId(txnId+"-"
                        +((ResponseReceived)rpcType).getReplyToId());
            }
        }
    }
    
    /**
     * This method sends the contents of the named file to the activity
     * server.
     * 
     * @param filename The filename
     */
    public void send(String filename) {

        try {
            java.io.InputStream is=ClassLoader.getSystemResourceAsStream(filename);

            if (is == null) {
                java.io.File f=new java.io.File(filename);
                
                if (f.exists()) {
                    is = new java.io.FileInputStream(filename);
                }
            }

            if (is == null) {
                throw new java.io.FileNotFoundException(filename);
            }
            
            ObjectMapper mapper=new ObjectMapper();
            
            java.util.List<ActivityType> actTypes=
                      mapper.readValue(is,
                             new TypeReference<java.util.List<ActivityType>>(){});
            
            int rand=_random.nextInt();
            
            _collector.startScope();
            
            for (ActivityType actType : actTypes) {
                
                // Preprocess message ids
                preProcess(actType, rand);
                
                _collector.record(actType);
            }
            
            _collector.endScope();
        
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * A dummy collector context for use with the activity
     * client.
     *
     */
    public class TestCollectorContext implements CollectorContext {

        /**
         * {@inheritDoc}
         */
        public String getPrincipal() {
            return "ActivityClient";
        }

        /**
         * {@inheritDoc}
         */
        public String getHost() {
            // TODO Auto-generated method stub
            return "MyHost";
        }

        /**
         * {@inheritDoc}
         */
        public String getNode() {
            // TODO Auto-generated method stub
            return "MyNode";
        }

        /**
         * {@inheritDoc}
         */
        public String getPort() {
            // TODO Auto-generated method stub
            return "8080";
        }

        /**
         * {@inheritDoc}
         */
        public TransactionManager getTransactionManager() {
            // TODO Auto-generated method stub
            return null;
        }
        
    }
}
