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

package org.overlord.rtgov.quickstarts.demos.orders;

/**
 * This class represents an Order.
 *
 */
public class Order {

    private String _orderId;
    private String _itemId;
    private int _quantity = 1;
    private String _customer;

    /**
     * This method returns the order id.
     * 
     * @return The order id
     */
    public String getOrderId() {
        return _orderId;
    }

    /**
     * This method sets the order id.
     * 
     * @param orderId The order id
     */
    public void setOrderId(String orderId) {
        _orderId = orderId;
    }

    /**
     * This method returns the item id.
     * 
     * @return The item id
     */
    public String getItemId() {
        return _itemId;
    }

    /**
     * This method sets the item id.
     * 
     * @param itemId The item id
     */
    public void setItemId(String itemId) {
        _itemId = itemId;
    }

    /**
     * This method returns the quantity.
     * 
     * @return The quantity
     */
    public int getQuantity() {
        return _quantity;
    }

    /**
     * This method sets the quantity.
     * 
     * @param quantity The quantity
     */
    public void setQuantity(int quantity) {
        _quantity = quantity;
    }
    
    /**
     * This method sets the customer.
     * 
     * @param customer The customer
     */
    public void setCustomer(String customer) {
        _customer = customer;
    }
    
    /**
     * This method returns the customer.
     * 
     * @return The customer
     */
    public String getCustomer() {
        return _customer;
    }
}
