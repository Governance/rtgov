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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.rtgov.ui.client.local.widgets.DateTimePicker;
import org.overlord.rtgov.ui.client.model.SituationsFilterBean;

import com.google.common.base.Strings;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;

/**
 * The situations filtersPanel sidebar.  Whenever the user changes any of the settings in
 * the filter sidebar, a ValueChangeEvent will be fired.
 *
 * @author eric.wittmann@redhat.com
 */
@Templated("/org/overlord/rtgov/ui/client/local/site/situations.html#filter-sidebar")
public class SituationFilters extends Composite implements HasValueChangeHandlers<SituationsFilterBean> {

    private static SituationsFilterBean currentState = new SituationsFilterBean();
    private static boolean initialized=false;

    // Owner, type, bundle name
    @Inject @DataField
    protected SeverityListBox severity;
    @Inject @DataField
    protected TextBox type;
    @Inject @DataField
    protected ResolutionStateListBox resolutionState;
    @Inject @DataField
    protected DateTimePicker timestampFrom;
    @Inject @DataField
    protected DateTimePicker timestampTo;
    @Inject @DataField
    protected Anchor clearFilters;
    @Inject @DataField
    protected TextBox description;
    @Inject @DataField
    protected TextBox subject;
    @Inject @DataField("properties")
    protected TextBox properties;

    /**
     * Constructor.
     */
    public SituationFilters() {
    }

    /**
     * Called after construction and injection.
     */
    @SuppressWarnings("unchecked")
    @PostConstruct
    protected void postConstruct() {
        
        // Initialize filter
        severity.setValue(currentState.getSeverity());
        type.setValue(currentState.getType());
        resolutionState.setValue(currentState.getResolutionState());
        timestampFrom.setDateValue(currentState.getTimestampFrom());
        timestampTo.setDateValue(currentState.getTimestampTo());
        description.setValue(currentState.getDescription());
        subject.setValue(currentState.getSubject());
        properties.setValue(currentState.getProperties());
        
        if (!initialized) {
            // Only overwrite the resolution state when the filter
            // is first displayed
            resolutionState.setSelectedIndex(1);
            currentState.setResolutionState(resolutionState.getValue());

            initialized = true;
        }
        
        ClickHandler clearFilterHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setValue(new SituationsFilterBean());
                onFilterValueChange();
            }
        };
        clearFilters.addClickHandler(clearFilterHandler);
        @SuppressWarnings("rawtypes")
        ValueChangeHandler valueChangeHandler = new ValueChangeHandler() {
            @Override
            public void onValueChange(ValueChangeEvent event) {
                onFilterValueChange();
            }
        };
        severity.addValueChangeHandler(valueChangeHandler);
        type.addValueChangeHandler(valueChangeHandler);
        resolutionState.addValueChangeHandler(valueChangeHandler);
        timestampFrom.addValueChangeHandler(valueChangeHandler);
        timestampTo.addValueChangeHandler(valueChangeHandler);
        description.addValueChangeHandler(valueChangeHandler);
        subject.addValueChangeHandler(valueChangeHandler);
        properties.addValueChangeHandler(valueChangeHandler);
    }

    /**
     * Called whenever any filter value changes.
     */
    protected void onFilterValueChange() {
        SituationsFilterBean newState = new SituationsFilterBean();
        newState.setSeverity(severity.getValue())
            .setType(type.getValue())
            .setResolutionState(resolutionState.getValue())
            .setDescription(description.getValue())
            .setSubject(subject.getValue())
            .setProperties(properties.getValue())
            .setTimestampFrom(timestampFrom.getDateValue())
            .setTimestampTo(timestampTo.getDateValue());

        SituationsFilterBean oldState = this.currentState;
        this.currentState = newState;
        // Only fire a change event if something actually changed.
        ValueChangeEvent.fireIfNotEqual(this, oldState, currentState);
    }

    /**th
     * @return the current filter settings
     */
    public SituationsFilterBean getValue() {
        return this.currentState;
    }

    /**
     * @param value the new filter settings
     */
    public void setValue(SituationsFilterBean value) {
        severity.setValue(value.getSeverity() == null ? "" : value.getSeverity()); //$NON-NLS-1$
        type.setValue(value.getType() == null ? "" : value.getType()); //$NON-NLS-1$
        resolutionState.setValue(value.getResolutionState() == null ? "" : value.getResolutionState()); //$NON-NLS-1$
        timestampFrom.setDateValue(value.getTimestampFrom() == null ? null : value.getTimestampFrom());
        timestampTo.setDateValue(value.getTimestampTo() == null ? null : value.getTimestampTo());
        description.setValue(Strings.nullToEmpty(value.getDescription()));
        subject.setValue(Strings.nullToEmpty(value.getSubject()));
        properties.setValue(Strings.nullToEmpty(value.getProperties()));
        onFilterValueChange();
    }

    /**
     * Refresh any data in the filter panel.
     */
    public void refresh() {
    }

    /**
     * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
     */
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<SituationsFilterBean> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

}
