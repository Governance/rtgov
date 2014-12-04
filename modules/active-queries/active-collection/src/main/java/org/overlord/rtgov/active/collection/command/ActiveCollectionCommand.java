/*
 * 2012-5 Red Hat Inc. and/or its affiliates and other contributors.
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
 * This class represents the top level command containing details of the
 * operation to be performed.
 *
 */
public class ActiveCollectionCommand {

    private Register _register;
    private Unregister _unregister;

    /**
     * This method returns the Register command.
     * 
     * @return The optional register command
     */
    public Register getRegister() {
        return (_register);
    }
    
    /**
     * This method sets the Register command.
     * 
     * @param reg The optional register command
     */
    public void setRegister(Register reg) {
        _register = reg;
    }

    /**
     * This method returns the Unregister command.
     * 
     * @return The optional unregister command
     */
    public Unregister getUnregister() {
        return (_unregister);
    }
    
    /**
     * This method sets the Unregister command.
     * 
     * @param unreg The optional unregister command
     */
    public void setUnregister(Unregister unreg) {
        _unregister = unreg;
    }
    
}
