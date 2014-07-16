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
package org.overlord.rtgov.ui.client.local.pages.services;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.nav.client.local.TransitionAnchorFactory;
import org.overlord.commons.gwt.client.local.widgets.TemplatedWidgetTable;
import org.overlord.rtgov.ui.client.local.ClientMessages;
import org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage;
import org.overlord.rtgov.ui.client.model.BindingBean;
import org.overlord.rtgov.ui.client.model.ReferenceSummaryBean;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

/**
 * A table of reference.
 *
 * @author eric.wittmann@redhat.com
 */
@Dependent
public class ReferenceTable extends TemplatedWidgetTable implements HasValue<List<ReferenceSummaryBean>> {

    @Inject
    protected ClientMessages i18n;
    @Inject
    protected TransitionAnchorFactory<ReferenceDetailsPage> toDetailsPageLinkFactory;

    /**
     * Constructor.
     */
    public ReferenceTable() {
    }

    /**
     * Adds a single row to the table.
     * @param summaryBean
     */
	public void addRow(final ReferenceSummaryBean summaryBean) {
		int rowIdx = this.rowElements.size();

		Anchor name = toDetailsPageLinkFactory.get("id", summaryBean.getReferenceId()); //$NON-NLS-1$
		name.setText(summaryBean.getName());
		InlineLabel interf4ce = new InlineLabel(summaryBean.getIface());
		FlowPanel div = new FlowPanel();
		add(rowIdx, 0, name);
		add(rowIdx, 1, interf4ce);
		add(rowIdx, 2, div);
		for (BindingBean bindingBean : summaryBean.getBindings()) {
			Label bindingLabel = new Label(bindingBean.getType());
			bindingLabel.setStyleName(!bindingBean.isActive() ? "alert-danger" : "alert-success");
			div.add(bindingLabel);
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<List<ReferenceSummaryBean>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public List<ReferenceSummaryBean> getValue() {
        // Not implemented (the table is read-only)
        return null;
	}

	@Override
	public void setValue(List<ReferenceSummaryBean> value) {
        setValue(value, true);
	}

	@Override
	public void setValue(List<ReferenceSummaryBean> value, boolean fireEvents) {
        clear();
        for (ReferenceSummaryBean ref : value) {
            addRow(ref);
        }
	}

}
