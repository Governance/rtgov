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

import java.util.Date;

import org.jboss.errai.databinding.client.api.Converter;
import org.overlord.rtgov.ui.client.local.ClientMessages;

import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * Converts between a String and a Date.
 *
 * @author eric.wittmann@redhat.com
 */
public class DataBindingDateConverter implements Converter<Date, String> {

    /**
     * Constructor.
     */
    public DataBindingDateConverter() {
    }

    /**
     * @see org.jboss.errai.databinding.client.api.Converter#toModelValue(java.lang.Object)
     */
    @Override
    public Date toModelValue(String widgetValue) {
        try {
            DateTimeFormat dateFormat = ClientMessages.getDateFormat();
            return dateFormat.parse(widgetValue);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * @see org.jboss.errai.databinding.client.api.Converter#toWidgetValue(java.lang.Object)
     */
    @Override
    public String toWidgetValue(Date modelValue) {
        if (modelValue == null)
            return ""; //$NON-NLS-1$
        else {
            DateTimeFormat dateFormat = ClientMessages.getDateFormat();
            return dateFormat.format(modelValue);
        }
    }

}
