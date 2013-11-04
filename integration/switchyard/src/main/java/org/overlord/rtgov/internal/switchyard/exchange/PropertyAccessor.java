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
package org.overlord.rtgov.internal.switchyard.exchange;

/**
 * This class provides a wrapper around the switchyard context to present a
 * map interface.
 *
 */
public class PropertyAccessor extends java.util.HashMap<String,Object> {

    /**
     * 
     */
    private static final long serialVersionUID = -6711280513918769377L;
    
    private org.switchyard.Context _context=null;
    
    /**
     * This is the constructor for the property accessor.
     * 
     * @param context The context
     */
    public PropertyAccessor(org.switchyard.Context context) {
        _context = context;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsKey(Object key) {
        return (_context != null && key instanceof String ?
                    _context.getProperty((String)key) != null : false);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(Object key) {
        Object ret=null;
        
        if (_context != null && key instanceof String) {
            ret = _context.getPropertyValue((String)key);
        }
        
        return (ret);
    }
}

