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
 * The payment.
 *
 */
public class Payment {

    private String _customer;
    private double _amount=0;

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

    /**
     * This method sets the amount.
     * 
     * @return The amount
     */
    public double getAmount() {
        return _amount;
    }

    /**
     * This method sets the amount.
     * 
     * @param amount The amount
     */
    public void setAmount(double amount) {
        _amount = amount;
    }
}
