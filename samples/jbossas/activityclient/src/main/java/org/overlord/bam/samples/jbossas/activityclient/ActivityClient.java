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
package org.overlord.rtgov.samples.jbossas.activityclient;

import java.util.Random;

import javax.transaction.TransactionManager;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.TypeReference;
import org.overlord.rtgov.activity.collector.ActivityCollector;
import org.overlord.rtgov.activity.collector.CollectorContext;
import org.overlord.rtgov.activity.collector.activity.server.ActivityServerLogger;
import org.overlord.rtgov.activity.collector.embedded.EmbeddedActivityCollector;
import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.soa.ResponseReceived;
import org.overlord.rtgov.activity.model.soa.ResponseSent;
import org.overlord.rtgov.activity.server.rest.client.RESTActivityServer;

/**
 * This class provides a test client for sending sample activity
 * types to the activity server via the collector mechanism.
 *
 */
public class ActivityClient {
    
    private String _activityServerURL=null;
    private ActivityCollector _collector=null;
    private Random _random=new Random();
    
    private java.util.Map<String, java.io.File> _txnFileMap=
                        new java.util.HashMap<String, java.io.File>();
    private java.util.List<String> _txnList=new java.util.Vector<String>();

    private static final ObjectMapper MAPPER=new ObjectMapper();

    static {
        SerializationConfig config=MAPPER.getSerializationConfig()
                .withSerializationInclusion(JsonSerialize.Inclusion.NON_NULL)
                .withSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
        
        MAPPER.setSerializationConfig(config);
    }
    
    /**
     * The main method.
     * 
     * @param args The arguments to the client
     */
    public static void main(String[] args) {
        
        if (args.length != 3) {
            System.err.println("Usage: ActivityClient <url> <filename> <numOfTxns>\r\n"
                    +"Set numOfTxns to -1 for continous");
            System.exit(1);
        }
        
        ActivityClient ac=new ActivityClient(args[0]);
        
        ac.init();
        
        ac.loadTransactions(args[1]);
        
        try {
            ac.scheduleTxns(Integer.parseInt(args[2]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
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
        
        ((EmbeddedActivityCollector)_collector).setActivityUnitLogger(activityUnitLogger);
        
        ((EmbeddedActivityCollector)_collector).setCollectorContext(new TestCollectorContext());
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
        
        if (_collector instanceof EmbeddedActivityCollector) {
            ((EmbeddedActivityCollector)_collector).getActivityUnitLogger().close();
        }
    }
    
    /**
     * This method pre-processes the supplied activity type.
     * 
     * @param actType The activity type
     * @param txnId A transaction id
     */
    protected void preProcess(ActivityType actType, int txnId) {
        
        if (actType instanceof org.overlord.rtgov.activity.model.soa.RPCActivityType) {
            org.overlord.rtgov.activity.model.soa.RPCActivityType rpcType=
                    (org.overlord.rtgov.activity.model.soa.RPCActivityType)actType;
            
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
     * This method loads the information from the transaction properties
     * file.
     * 
     * @param filename The transaction properties file
     */
    public void loadTransactions(String filename) {
        
        try {
            java.io.File f=null;

            java.net.URL url=ClassLoader.getSystemResource(filename);

            if (url == null) {
                f = new java.io.File(filename);
                
                if (!f.exists()) {
                    f = null;
                }
            } else {
                f = new java.io.File(url.getFile());
            }

            if (f == null) {
                throw new java.io.FileNotFoundException(filename);
            }
            
            java.io.FileInputStream is=new java.io.FileInputStream(f);
            
            java.util.Properties props=new java.util.Properties();
            props.load(is);
            
            is.close();
            
            // Scan properties for file names
            for (String key : props.stringPropertyNames()) {
                if (key.endsWith(".txn")) {
                    String fn=props.getProperty(key);
                                        
                    String name=key.substring(0, key.length()-4);
                    
                    java.io.File txnFile=new java.io.File(f.getParentFile(), fn);
                    
                    if (!txnFile.exists()) {
                        System.err.println("Could not find transaction ("+name+
                                ") file '"+fn+"' relative to: "+f.getParentFile());
                        continue;
                    }
                    
                    _txnFileMap.put(name, txnFile);
                    
                    String w=props.getProperty(name+".weight");
                    
                    if (w != null) {
                        try {
                            int weight=Integer.parseInt(w);
                            
                            for (int i=0; i < weight; i++) {
                                _txnList.add(name);
                            }
                            
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.err.println("Weight for '"+name+"' not found");
                    }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This method performs a random series of business transactions.
     * 
     */
    public void scheduleTxns(int num) {
        
        Random rand=new Random(System.currentTimeMillis());
        
        int count=0;
        
        while (num == -1 || count < num) {
            
            int pos=Math.abs(rand.nextInt()) % _txnList.size();
            
            String txnName=_txnList.get(pos);
            
            send(txnName, count);
            
            count++;
        }
    }
    
    /**
     * This method sends the contents of the named transaction to the activity
     * server.
     * 
     * @param txnName The txnName
     */
    public void send(String txnName, int count) {

        try {
            int id=count;
            
            java.io.InputStream is=new java.io.FileInputStream(_txnFileMap.get(txnName));
            
            byte[] b=new byte[is.available()];
            
            is.read(b);
            
            is.close();
            
            // Transform any ID fields in the txn with the unique id
            String txn=new String(b);
            
            txn = txn.replaceAll("\\{ID\\}", ""+id);
            
            is = new java.io.ByteArrayInputStream(txn.getBytes());
            
            java.util.List<ActivityType> actTypes=
                      MAPPER.readValue(is,
                             new TypeReference<java.util.List<ActivityType>>(){});
            
            is.close();
            
            _collector.startScope();
            
            for (ActivityType actType : actTypes) {
                
                // Preprocess message ids
                preProcess(actType, id);
                
                // Check the timestamp, to see if a delay should occur
                if (actType.getTimestamp() > 0) {
                    synchronized (this) {
                        int variation=_random.nextInt()%20;
                        wait(actType.getTimestamp()+variation);
                    }
                }
                
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
