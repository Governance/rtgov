/*
 * 2012-4 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.overlord.rtgov.active.collection.command;

/**
 * This class represents an active change to a collection.
 *
 */
public class ActiveChangeNotification {

    private ActiveChangeType _type;
    private Object _key;
    private Object _value;
    
    /**
     * The default constructor.
     */
    public ActiveChangeNotification() {
    }
    
    /**
     * This constructor initializes the type, key and value.
     * 
     * @param type The type
     * @param key The key
     * @param value The value
     */
    public ActiveChangeNotification(ActiveChangeType type, Object key, Object value) {
        _type = type;
        _key = key;
        _value = value;
    }
    
    /**
     * This method returns the active change type.
     * 
     * @return The active change type
     */
    public ActiveChangeType getType() {
        return (_type);
    }
    
    /**
     * This method sets the active change type.
     * 
     * @param type The active change type
     */
    public void setType(ActiveChangeType type) {
        _type = type;
    }
    
    /**
     * This method returns the key.
     * 
     * @return The key
     */
    public Object getKey() {
        return (_key);
    }
    
    /**
     * This method sets the key.
     * 
     * @param key The key
     */
    public void setKey(Object key) {
        _key = key;
    }
    
    /**
     * This method returns the value.
     * 
     * @return The value
     */
    public Object getValue() {
        return (_value);
    }
    
    /**
     * This method sets the value.
     * 
     * @param value The value
     */
    public void setValue(Object value) {
        _value = value;
    }
    
    /**
     * This enumerated type represents the active change types that
     * may occur.
     *
     */
    public static enum ActiveChangeType {
        
        /**
         * This value represents an insertion into the collection.
         */
        Insert,
        
        /**
         * This value represents an update to the collection.
         */
        Update,
        
        /**
         * This value represents a removal from the collection.
         */
        Remove
        
    }
}
