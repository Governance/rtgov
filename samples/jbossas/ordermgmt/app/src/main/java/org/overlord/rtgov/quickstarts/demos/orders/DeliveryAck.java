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
 * This class represents the delivery acknowledgement.
 *
 */
public class DeliveryAck {
    
    private String _orderId;
    
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
     * @return The delivery ack
     */
    public DeliveryAck setOrderId(String orderId) {
        _orderId = orderId;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Delivery " + _orderId + " acknowledged";
    }
}
