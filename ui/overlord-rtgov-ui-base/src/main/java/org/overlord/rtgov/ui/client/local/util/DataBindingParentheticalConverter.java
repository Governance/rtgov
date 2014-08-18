/*
 * Copyright 2013 JBoss Inc
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
package org.overlord.rtgov.ui.client.local.util;

import org.jboss.errai.databinding.client.api.Converter;

/**
 * Adds parentheses to the content when binding to a control.
 *
 * @author eric.wittmann@redhat.com
 */
public class DataBindingParentheticalConverter implements Converter<String, String> {

    /**
     * Constructor.
     */
    public DataBindingParentheticalConverter() {
    }

    /**
     * @see org.jboss.errai.databinding.client.api.Converter#toModelValue(java.lang.Object)
     */
    @Override
    public String toModelValue(String widgetValue) {
        if (widgetValue != null && widgetValue.trim().length() > 0)
            return widgetValue.substring(1, widgetValue.length() - 1);
        else
            return null;
    }

    /**
     * @see org.jboss.errai.databinding.client.api.Converter#toWidgetValue(java.lang.Object)
     */
    @Override
    public String toWidgetValue(String modelValue) {
        if (modelValue == null)
            return ""; //$NON-NLS-1$
        else
            return "(" + modelValue + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }

}
