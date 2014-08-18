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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.nav.client.local.TransitionAnchorFactory;
import org.overlord.rtgov.ui.client.local.ClientMessages;
import org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage;
import org.overlord.rtgov.ui.client.local.widgets.common.SortableTemplatedWidgetTable;
import org.overlord.rtgov.ui.client.model.Constants;
import org.overlord.rtgov.ui.client.model.SituationSummaryBean;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A table of deployments.
 *
 * @author eric.wittmann@redhat.com
 */
@Dependent
public class SituationTable extends SortableTemplatedWidgetTable {

    @Inject
    protected ClientMessages i18n;
    @Inject
    protected TransitionAnchorFactory<SituationDetailsPage> toDetailsPageLinkFactory;

    /**
     * Constructor.
     */
    public SituationTable() {
    }

    /**
     * @see org.overlord.rtgov.ui.client.local.widgets.common.SortableTemplatedWidgetTable#getDefaultSortColumn()
     */
    @Override
    protected SortColumn getDefaultSortColumn() {
        SortColumn sortColumn = new SortColumn();
        sortColumn.columnId = Constants.SORT_COLID_TIMESTAMP;
        sortColumn.ascending = false;
        return sortColumn;
    }

    /**
     * @see org.overlord.rtgov.ui.client.local.widgets.common.SortableTemplatedWidgetTable#configureColumnSorting()
     */
    @Override
    protected void configureColumnSorting() {
        setColumnSortable(1, Constants.SORT_COLID_TYPE);
        setColumnSortable(2, Constants.SORT_COLID_RESOLUTION_STATE);
        setColumnSortable(3, Constants.SORT_COLID_SUBJECT);
        setColumnSortable(4, Constants.SORT_COLID_TIMESTAMP);
        sortBy(Constants.SORT_COLID_TIMESTAMP, false);
    }

    /**
     * Adds a single row to the table.
     * @param situationSummaryBean
     */
    public void addRow(final SituationSummaryBean situationSummaryBean) {
        int rowIdx = this.rowElements.size();
        DateTimeFormat format = DateTimeFormat.getFormat(i18n.format("dateTime-format")); //$NON-NLS-1$

        FlowPanel icon = new FlowPanel();
        icon.getElement().setClassName("icon"); //$NON-NLS-1$
        icon.getElement().addClassName("icon-severity-" + situationSummaryBean.getSeverity()); //$NON-NLS-1$
        Anchor type = toDetailsPageLinkFactory.get("id", situationSummaryBean.getSituationId()); //$NON-NLS-1$
        type.setText(situationSummaryBean.getType());
        InlineLabel resolutionState = new InlineLabel(situationSummaryBean.getResolutionState());
        InlineLabel subject = new InlineLabel(situationSummaryBean.getSubject());
        InlineLabel description = new InlineLabel(situationSummaryBean.getDescription());
        InlineLabel timestamp = new InlineLabel(format.format(situationSummaryBean.getTimestamp()));
        Widget infoIcons = createInfoPanel(situationSummaryBean);

        add(rowIdx, 0, icon);
        add(rowIdx, 1, type);
        add(rowIdx, 2, resolutionState);
        add(rowIdx, 3, subject);
        add(rowIdx, 4, timestamp);
        add(rowIdx, 5, description);
        add(rowIdx, 6, infoIcons);
    }

    /**
     * Creates the action buttons.
     * @param situationSummaryBean
     */
    private Widget createInfoPanel(SituationSummaryBean situationSummaryBean) {
        FlowPanel infoPanel = new FlowPanel();
        infoPanel.getElement().setClassName(""); //$NON-NLS-1$

        if (!situationSummaryBean.getProperties().isEmpty()) {
            final Anchor properties = new Anchor();
            properties.setHref("#"); //$NON-NLS-1$
            properties.getElement().setAttribute("data-toggle", "popover"); //$NON-NLS-1$ //$NON-NLS-2$
            properties.getElement().setAttribute("data-placement", "left"); //$NON-NLS-1$ //$NON-NLS-2$
            properties.getElement().setAttribute("data-html", "true"); //$NON-NLS-1$ //$NON-NLS-2$
            properties.getElement().setAttribute("data-original-title", i18n.format("situation-table.properties")); //$NON-NLS-1$ //$NON-NLS-2$
            properties.getElement().setAttribute("data-trigger", "hover"); //$NON-NLS-1$ //$NON-NLS-2$
            properties.getElement().setAttribute("data-content", createPropertiesTableHtml(situationSummaryBean.getProperties())); //$NON-NLS-1$
            properties.setHTML("<div class=\"icon icon-properties\"></div>"); //$NON-NLS-1$
            properties.addAttachHandler(new Handler() {
                @Override
                public void onAttachOrDetach(AttachEvent event) {
                    if (event.isAttached()) {
                        Element element2 = properties.getElement();
                        activatePopover(element2);
                    }
                }
            });
            infoPanel.add(properties);
        }

//        infoPanel.add(downloadInitialActionButton);
//        infoPanel.add(retryActionButton);
        return infoPanel;
    }

    /**
     * Creates the html for the table shown in the properties popover.
     */
    protected String createPropertiesTableHtml(Map<String, String> properties) {
        StringBuilder builder = new StringBuilder();

        builder.append("<table class='table table-condensed table-hover table-striped' style='border-right: 1px solid rgb(211, 211, 211); border-bottom: 1px solid rgb(211, 211, 211);'>"); //$NON-NLS-1$
        builder.append("<tbody>"); //$NON-NLS-1$
        for (Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey();
            if (key != null && key.length() > 32) {
                key = key.substring(0, 31) + "..."; //$NON-NLS-1$
            }
            String value = entry.getValue();
            if (value != null && value.length() > 128) {
                value = value.substring(0, 127) + "..."; //$NON-NLS-1$
            }
            builder.append("<tr>"); //$NON-NLS-1$
            builder.append("<td>"); //$NON-NLS-1$
            builder.append(key);
            builder.append("</td>"); //$NON-NLS-1$
            builder.append("<td>"); //$NON-NLS-1$
            builder.append(value);
            builder.append("</td>"); //$NON-NLS-1$
            builder.append("</tr>"); //$NON-NLS-1$
        }
        builder.append("</tbody>"); //$NON-NLS-1$
        builder.append("</table>"); //$NON-NLS-1$
        return builder.toString();
    }

    public static native void activatePopover(Element elem) /*-{
          $wnd.jQuery(elem).popover();
    }-*/;


}
