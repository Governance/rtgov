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
package org.overlord.rtgov.quickstarts.demos.orders;

import org.switchyard.component.test.mixins.http.HTTPMixIn;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;

import org.switchyard.remote.RemoteInvoker;
import org.switchyard.remote.RemoteMessage;
import org.switchyard.remote.http.HttpInvoker;

/**
 * This class provides the client for sending SOAP messages
 * to the Orders switchyard application.
 *
 */
public class OrdersClient {

    private static final String OPERATION = "submitOrder";
    private static final String URL = "/demo-orders/OrderService";
    private static final String XML_PATH = "src/test/resources/xml/";
    
    private static final QName SERVICE = new QName(
            "urn:switchyard-quickstart-demo:orders:0.1.0",
            "OrderService");
    private static final String REMOTE_INVOKER_URL = "http://localhost:8080/switchyard-remote";

    /**
     * Private no-args constructor.
     */
    private OrdersClient() {
    }

    /**
     * Main method for Orders client.
     * 
     * @param args The arguments
     * @throws Exception Failed to send SOAP message
     */
    public static void main(final String[] args) throws Exception {

        if (args.length < 2 || args.length > 3) {
            System.err.println("Usage: OrderClient host:port requestId [count]");
            System.exit(1);
        }
        
        if (args[1].endsWith("resubmit")) {
            OrdersClient.resubmit(args);
        } else {
            OrdersClient.send(args);
        }
        
    }
    
    protected static void send(String[] args) {
        HTTPMixIn soapMixIn = new HTTPMixIn();
        soapMixIn.initialize();

        try {
            String url=args[0]+URL;
            String request=XML_PATH+args[1]+".xml";
            int count=1;
            
            if (args.length == 3 && args[2] != null && args[2].trim().length() > 0) {
                count = Integer.parseInt(args[2]);
            }
            
            for (int i=0; i < count; i++) {
                String result = soapMixIn.postFile(url, request);
            
                System.out.println("Reply "+(i+1)+":\n" + result);
            }
        } finally {
            soapMixIn.uninitialize();
        }
    }
    
    protected static void resubmit(String[] args) {
        String request="/xml/"+args[1]+".xml";
        
        try {
            java.io.InputStream is=OrdersClient.class.getResourceAsStream(request);
            
            DocumentBuilder builder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
            org.w3c.dom.Document doc=builder.parse(is);
            
            is.close();
            
            Object content=new DOMSource(doc.getDocumentElement());

            // Create a new remote client invoker
            RemoteInvoker invoker = new HttpInvoker(REMOTE_INVOKER_URL);
    
            // Create the request message
            RemoteMessage message = new RemoteMessage();
            message.setService(SERVICE).setOperation(OPERATION).setContent(content);
    
            // Invoke the service
            RemoteMessage reply = invoker.invoke(message);
            if (reply.isFault()) {
                System.err.println("Oops ... something bad happened.  "
                        + reply.getContent());
                if (reply.getContent() instanceof Exception) {
                    ((Exception)reply.getContent()).printStackTrace();
                }
            } else {
                System.out.println("Response: "+reply.getContent());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
