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
 * The order acknowledgement.
 *
 */
public class OrderAck {
    
    private String _orderId;
    private boolean _accepted;
    private String _status;
    private String _customer;
    private double _total=0;
    
    /**
     * This method returns the order id.
     * 
     * @return The order id
     */
    public String getOrderId() {
        return _orderId;
    }
    
    /**
     * This method returns the accepted status.
     * 
     * @return The accepted status
     */
    public boolean isAccepted() {
        return _accepted;
    }
    
    /**
     * This method returns order status.
     * 
     * @return The order status
     */
    public String getStatus() {
        return _status;
    }
    
    /**
     * This method sets the order id.
     * 
     * @param orderId The order id
     * @return Order ack
     */
    public OrderAck setOrderId(String orderId) {
        _orderId = orderId;
        return this;
    }
    
    /**
     * This method sets the order status.
     * 
     * @param status The order status
     * @return Order ack
     */
    public OrderAck setStatus(String status) {
        _status = status;
        return this;
    }

    /**
     * This method sets the order accepted.
     * 
     * @param accepted The order accepted
     * @return Order ack
     */
    public OrderAck setAccepted(boolean accepted) {
        _accepted = accepted;
        return this;
    }
    
    /**
     * This method returns the order id.
     * 
     * @return The order id
     */
    public double getTotal() {
        return _total;
    }

    /**
     * This method sets the total.
     * 
     * @param total The total
     */
    public void setTotal(double total) {
        this._total = total;
    }
    
    /**
     * This method sets the customer.
     * 
     * @param customer The customer
     * @return Order ack
     */
    public OrderAck setCustomer(String customer) {
        this._customer = customer;
        return this;
    }
    
    /**
     * This method returns the order id.
     * 
     * @return The order id
     */
    public String getCustomer() {
        return _customer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        if (_accepted) {
            return "Order " + _orderId + " Accepted.";
        } else {
            return "Order " + _orderId + " Not Accepted: " + _status;
        }
    }
}
