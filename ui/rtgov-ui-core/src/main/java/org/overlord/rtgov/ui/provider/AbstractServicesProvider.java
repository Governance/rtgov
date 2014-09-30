/*
 * Copyright 2013-4 Red Hat Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.rtgov.ui.provider;

import java.util.List;

import org.overlord.rtgov.ui.client.model.QName;
import org.overlord.rtgov.ui.client.model.ReferenceBean;
import org.overlord.rtgov.ui.client.model.UiException;

/**
 * This class provides an abstract base implementation of the ServicesProvider
 * interface.
 *
 */
public abstract class AbstractServicesProvider implements ServicesProvider {
    
    private java.util.Map<Class<? extends ActionProvider>, ActionProvider> _actionProviders=
            new java.util.HashMap<Class<? extends ActionProvider>,ActionProvider>();

    /**
     * This method registers an action.
     * 
     * @param type The action type
     * @param action The action
     */
    protected void registerAction(Class<? extends ActionProvider> type, ActionProvider action) {
        _actionProviders.put(type, action);
    }
    
    /**
     * This method unregisters an action.
     * 
     * @param type The action type
     * @return The action
     */
    protected ActionProvider unregisterAction(Class<? extends ActionProvider> type) {
        return (_actionProviders.remove(type));
    }
    
    /**
     * {@inheritDoc}
     */
    public List<QName> getApplicationNames() throws UiException {
        return (java.util.Collections.<QName>emptyList());
    }

    /**
     * {@inheritDoc}
     */
    public ReferenceBean getReference(String uuid) throws UiException {
        return (null);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T extends ActionProvider> T getAction(Class<T> actionType) {
        return ((T)_actionProviders.get(actionType));
    }

}
