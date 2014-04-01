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

package org.overlord.rtgov.quickstarts.demos.ordermgmt.orderservice;

import org.overlord.rtgov.quickstarts.demos.ordermgmt.inventoryservice.InventoryService;
import org.overlord.rtgov.quickstarts.demos.ordermgmt.inventoryservice.ItemNotFoundException;
import org.overlord.rtgov.quickstarts.demos.ordermgmt.logisticsservice.LogisticsService;
import org.overlord.rtgov.quickstarts.demos.ordermgmt.model.Item;
import org.overlord.rtgov.quickstarts.demos.ordermgmt.model.Order;
import org.overlord.rtgov.quickstarts.demos.ordermgmt.model.OrderAck;
import org.overlord.rtgov.quickstarts.demos.ordermgmt.model.Payment;
import org.overlord.rtgov.quickstarts.demos.ordermgmt.model.Receipt;

/**
 * This class provides the implementation of the order service.
 *
 */
public class OrderServiceBean implements OrderService {
    
    private InventoryService _inventory;
    
    private LogisticsService _logistics;
    
    /**
     * This method sets the inventory service.
     * 
     * @param is The inventory service
     */
    public void setInventoryService(InventoryService is) {
        _inventory = is;
    }
    
    /**
     * This method sets the logistics service.
     * 
     * @param ls The logistics service
     */
    public void setLogisticsService(LogisticsService ls) {
        _logistics = ls;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public OrderAck submitOrder(Order order) {
        // Create an order ack
        OrderAck orderAck = new OrderAck().setOrderId(order.getOrderId())
                        .setCustomer(order.getCustomer());

        // Check the inventory
        try {
            Item orderItem = _inventory.lookupItem(order.getItemId());
            
            // Check quantity on hand and generate the ack
            if (orderItem.getQuantity() >= order.getQuantity()) {
                
                // Arrange delivery
                _logistics.deliver(order);
                
                orderAck.setAccepted(true).setStatus("Order Accepted");
                orderAck.setTotal(orderItem.getUnitPrice() * order.getQuantity());
            } else {
                orderAck.setAccepted(false).setStatus("Insufficient Quantity");
            }
            
        } catch (ItemNotFoundException infEx) {
            orderAck.setAccepted(false).setStatus("Item Not Available");
        }
        return orderAck;
    }

    /**
     * {@inheritDoc}
     */
    public Receipt makePayment(Payment payment) {
        Receipt ret=new Receipt();
        
        ret.setCustomer(payment.getCustomer());
        ret.setAmount(payment.getAmount());
        
        return (ret);
    }
}
