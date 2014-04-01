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
 * This class represents the Item.
 *
 */
public class Item {
    private String _itemId;
    private String _name;
    private int _quantity;
    private double _unitPrice;

    /**
     * This method returns the name.
     * 
     * @return The name
     */
    public String getName() {
        return _name;
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
     * This method returns the quantity.
     * 
     * @return The quantity
     */
    public int getQuantity() {
        return _quantity;
    }
    
    /**
     * This method returns the unit price.
     * 
     * @return The unit price
     */
    public double getUnitPrice() {
        return _unitPrice;
    }
    
    /**
     * This method sets the name.
     * 
     * @param name The name
     * @return The item
     */
    public Item setName(String name) {
        _name = name;
        return this;
    }
    
    /**
     * This method sets the item id.
     * 
     * @param itemId The item id
     * @return The item
     */
    public Item setItemId(String itemId) {
        _itemId = itemId;
        return this;
    }
    
    /**
     * This method sets the quantity.
     * 
     * @param quantity The quantity
     * @return The item
     */
    public Item setQuantity(int quantity) {
        _quantity = quantity;
        return this;
    }
    
    /**
     * This method sets the price.
     * 
     * @param price The price
     * @return The item
     */
    public Item setUnitPrice(double price) {
        _unitPrice = price;
        return this;
    }
}
