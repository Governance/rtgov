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
package org.overlord.rtgov.quickstarts.demos.ordermgmt.orderservice.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.overlord.rtgov.quickstarts.demos.ordermgmt.model.Order;
import org.overlord.rtgov.quickstarts.demos.ordermgmt.model.OrderAck;
import org.overlord.rtgov.quickstarts.demos.ordermgmt.model.Payment;
import org.overlord.rtgov.quickstarts.demos.ordermgmt.model.Receipt;
import org.overlord.rtgov.quickstarts.demos.ordermgmt.orderservice.OrderService;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * This class represents the RESTful interface to the activity server.
 *
 */
@Path("/orders")
@ApplicationScoped
public class RESTOrderService {

    private static final Logger LOG=Logger.getLogger(RESTOrderService.class.getName());
    
    private static final ObjectMapper MAPPER=new ObjectMapper();
    
    private OrderService _orderService=null;

    static {
        SerializationConfig config=MAPPER.getSerializationConfig().with(SerializationConfig.Feature.INDENT_OUTPUT);
        
        MAPPER.setSerializationConfig(config);
    }

    /**
     * This is the default constructor.
     */
    public RESTOrderService() {
    }
    
    /**
     * This method sets the order service.
     * 
     * @param os The order service
     */
    public void setOrderService(OrderService os) {
        LOG.info("Set Order Service="+os);
        _orderService = os;
    }
    
    @POST
    @Path("/submit")
    @Produces("application/json")
    public Response submit(String json) throws Exception {
        String ret="";
        
        try {
            // Convert json format into Order object
            java.io.ByteArrayInputStream bais=new java.io.ByteArrayInputStream(json.getBytes());
            
            Order order=(Order)MAPPER.readValue(bais, Order.class);
            
            bais.close();            
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Submit order json="+json+" object="+order);        
            }
            
            if (_orderService == null) {
                throw new Exception("Order Service is not available");
            }
            
            OrderAck ack=_orderService.submitOrder(order);
            
            if (ack != null) {
                java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
                
                MAPPER.writeValue(baos, ack);
                
                ret = new String(baos.toByteArray());
                
                baos.close();
            }
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Order acknowledgement object="+ack+" json="+ret);        
            }
        } catch (Exception e) {
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.log(Level.FINEST, "Order failed", e);        
            }
            return (Response.serverError().entity(e.getMessage()).build());
        }

        return (Response.ok(ret).build());
    }
    
    @POST
    @Path("/pay")
    @Produces("application/json")
    public Response pay(String json) throws Exception {
        String ret="";
        
        try {
            // Convert json format into Order object
            java.io.ByteArrayInputStream bais=new java.io.ByteArrayInputStream(json.getBytes());
            
            Payment payment=(Payment)MAPPER.readValue(bais, Payment.class);
            
            bais.close();            
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Payment json="+json+" object="+payment);        
            }
            
            if (_orderService == null) {
                throw new Exception("Order Service is not available");
            }
            
            Receipt receipt=_orderService.makePayment(payment);
            
            if (receipt != null) {
                java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
                
                MAPPER.writeValue(baos, receipt);
                
                ret = new String(baos.toByteArray());
                
                baos.close();
            }
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Payment receipt object="+receipt+" json="+ret);        
            }
        } catch (Exception e) {
            return (Response.serverError().entity(e.getMessage()).build());
        }

        return (Response.ok(ret).build());
    }
}
