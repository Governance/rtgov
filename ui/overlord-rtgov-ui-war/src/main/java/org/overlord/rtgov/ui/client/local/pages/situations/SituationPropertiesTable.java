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
package org.overlord.rtgov.ui.client.local.pages.situations;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.overlord.commons.gwt.client.local.widgets.TemplatedWidgetTable;
import org.overlord.rtgov.ui.client.local.ClientMessages;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * A table of situation properties.
 *
 * @author eric.wittmann@redhat.com
 */
@Dependent
public class SituationPropertiesTable extends TemplatedWidgetTable implements HasValue<Map<String, String>> {

    @Inject
    protected ClientMessages i18n;

    /**
     * Constructor.
     */
    public SituationPropertiesTable() {
    }

    /**
     * Adds a single row to the table.
     */
    public void addRow(String name, String value) {
        int rowIdx = this.rowElements.size();

        InlineLabel nameLabel = new InlineLabel(name);
        InlineLabel valueLabel = new InlineLabel(value);

        add(rowIdx, 0, nameLabel);
        add(rowIdx, 1, valueLabel);
    }

    /**
     * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
     */
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Map<String, String>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#getValue()
     */
    @Override
    public Map<String, String> getValue() {
        // Not implemented (the table is read-only)
        return null;
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object)
     */
    @Override
    public void setValue(Map<String, String> value) {
        setValue(value, true);
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object, boolean)
     */
    @Override
    public void setValue(Map<String, String> value, boolean fireEvents) {
        clear();
        Set<Entry<String,String>> entrySet = value.entrySet();
        for (Entry<String, String> entry : entrySet) {
            addRow(entry.getKey(), entry.getValue());
        }
    }

}
