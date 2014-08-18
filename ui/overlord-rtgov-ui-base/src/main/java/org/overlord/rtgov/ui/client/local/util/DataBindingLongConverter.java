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
 * Converts between a String and a long.
 *
 * @author eric.wittmann@redhat.com
 */
public class DataBindingLongConverter implements Converter<Long, String> {

    /**
     * Constructor.
     */
    public DataBindingLongConverter() {
    }

    /**
     * @see org.jboss.errai.databinding.client.api.Converter#toModelValue(java.lang.Object)
     */
    @Override
    public Long toModelValue(String widgetValue) {
        try {
            return new Long(widgetValue);
        } catch (NumberFormatException e) {
            return new Long(-1);
        }
    }

    /**
     * @see org.jboss.errai.databinding.client.api.Converter#toWidgetValue(java.lang.Object)
     */
    @Override
    public String toWidgetValue(Long modelValue) {
        if (modelValue == -1)
            return ""; //$NON-NLS-1$
        else
            return String.valueOf(modelValue);
    }

}
