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
package org.overlord.rtgov.ui.client.local.widgets.common;

import javax.annotation.PostConstruct;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ListBox;

/**
 * Injectable list box for a filter.
 * @author eric.wittmann@redhat.com
 */
public abstract class AbstractFilterListBox extends ListBox implements HasValue<String> {

    /**
     * Constructor.
     */
    public AbstractFilterListBox() {
        this.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                fireValueChangeEvent();
            }
        });
    }

    @PostConstruct
    protected void postConstruct() {
        configureItems();
    }

    /**
     * Fires a value change event.
     */
    protected void fireValueChangeEvent() {
        ValueChangeEvent.fire(this, getValue());
    }

    /**
     * Called by the c'tor to configure the items in the list box (if static or pulled from existing
     * data like JSON data).
     */
    protected abstract void configureItems();

    /**
     * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
     */
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#getValue()
     */
    @Override
    public String getValue() {
        int selIdx = getSelectedIndex();
        if (selIdx == 0) {
            return null;
        }
        String value = getValue(selIdx);
        return value;
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object)
     */
    @Override
    public void setValue(String value) {
        setValue(value, false);
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object, boolean)
     */
    @Override
    public void setValue(String value, boolean fireEvents) {
        if (value == null) {
            this.setSelectedIndex(0);
        } else {
            for (int idx = 0; idx < this.getItemCount(); idx++) {
                if (value.equals(this.getValue(idx))) {
                    this.setSelectedIndex(idx);
                }
            }
        }
    }

}
