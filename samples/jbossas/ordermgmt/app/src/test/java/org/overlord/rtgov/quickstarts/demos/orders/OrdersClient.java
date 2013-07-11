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

/**
 * This class provides the client for sending SOAP messages
 * to the Orders switchyard application.
 *
 */
public class OrdersClient {

    private static final String URL = "/demo-orders/OrderService";
    private static final String XML_PATH = "src/test/resources/xml/";

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
}
