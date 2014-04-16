/*
 * Copyright 2012 JBoss Inc
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
package org.overlord.rtgov.ui.client.local.pages;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.bus.client.api.messaging.MessageCallback;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.nav.client.local.PageHiding;
import org.jboss.errai.ui.nav.client.local.PageShown;
import org.jboss.errai.ui.nav.client.local.TransitionAnchor;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.commons.gwt.client.local.widgets.HtmlSnippet;
import org.overlord.commons.gwt.client.local.widgets.Pager;
import org.overlord.rtgov.ui.client.local.ClientMessages;
import org.overlord.rtgov.ui.client.local.events.TableSortEvent;
import org.overlord.rtgov.ui.client.local.pages.situations.SituationFilters;
import org.overlord.rtgov.ui.client.local.pages.situations.SituationTable;
import org.overlord.rtgov.ui.client.local.pages.situations.SituationWatcherEvents;
import org.overlord.rtgov.ui.client.local.services.NotificationService;
import org.overlord.rtgov.ui.client.local.services.SituationsRpcService;
import org.overlord.rtgov.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.rtgov.ui.client.local.services.rpc.IRpcServiceInvocationHandler.RpcServiceInvocationHandlerAdapter;
import org.overlord.rtgov.ui.client.local.widgets.ToggleSwitch;
import org.overlord.rtgov.ui.client.local.widgets.common.SortableTemplatedWidgetTable.SortColumn;
import org.overlord.rtgov.ui.client.model.BatchRetryResult;
import org.overlord.rtgov.ui.client.model.NotificationBean;
import org.overlord.rtgov.ui.client.model.SituationEventBean;
import org.overlord.rtgov.ui.client.model.SituationResultSetBean;
import org.overlord.rtgov.ui.client.model.SituationSummaryBean;
import org.overlord.rtgov.ui.client.model.SituationsFilterBean;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;

/**
 * The "Situations" page.
 *
 * @author eric.wittmann@redhat.com
 */
@Templated("/org/overlord/rtgov/ui/client/local/site/situations.html#page")
@Page(path="situations")
@Dependent
public class SituationsPage extends AbstractPage {

    @Inject
    protected ClientMessages i18n;
    @Inject
    protected SituationsRpcService situationsService;
    @Inject
    protected NotificationService notificationService;

    // Breadcrumbs
    @Inject @DataField("back-to-dashboard")
    private TransitionAnchor<DashboardPage> toDashboardPage;
    @Inject @DataField("to-services")
    private TransitionAnchor<ServicesPage> toServicesPage;

    @Inject @DataField("filter-sidebar")
    protected SituationFilters filtersPanel;
    @Inject
    @DataField
    protected ToggleSwitch toggleFilterSwitch;
    @Inject
    @DataField
    protected Button retrySituations;
    private boolean applyActionToFilteredRowsOnly = true;

    @Inject @DataField("btn-refresh")
    protected Button refreshButton;

    @Inject @DataField("situations-none")
    protected HtmlSnippet noDataMessage;
    @Inject @DataField("situations-searching")
    protected HtmlSnippet searchInProgressMessage;
    @Inject @DataField("situations-table")
    protected SituationTable situationsTable;

    @Inject @DataField("situations-pager")
    protected Pager pager;
    @DataField("situations-range")
    protected SpanElement rangeSpan = Document.get().createSpanElement();
    @DataField("situations-total")
    protected SpanElement totalSpan = Document.get().createSpanElement();

    @Inject @DataField("sitwatch-btn")
    protected Anchor sitWatchButton;
    @Inject @DataField("sitwatch-events")
    protected SituationWatcherEvents sitWatchEvents;

    private int currentPage = 1;
    private int numEvents = 0;

    /**
     * Constructor.
     */
    public SituationsPage() {
    }

    /**
     * Called whenver the page is shown.
     */
    @PageShown
    public void onPageShown() {
        GWT.log("Subscribing to SitWatch topic."); //$NON-NLS-1$
        bus.subscribe("SitWatch", new MessageCallback() { //$NON-NLS-1$
            @Override
            public void callback(Message message) {
                SituationEventBean sitEvent = message.get(SituationEventBean.class, "situation"); //$NON-NLS-1$
                onNewSituation(sitEvent);
            }
        });
    }

    /**
     * Called when the user navigates away from the page.
     */
    @PageHiding
    public void onPageHiding() {
        GWT.log("Unsubscribing *from* SitWatch topic."); //$NON-NLS-1$
        bus.unsubscribeAll("SitWatch"); //$NON-NLS-1$
    }

    /**
     * Called when a new situation event is received from the server.
     * @param sitEvent
     */
    protected void onNewSituation(SituationEventBean sitEvent) {
        GWT.log("Situation event: " + sitEvent.getType()); //$NON-NLS-1$
        this.numEvents++;
        this.sitWatchEvents.add(sitEvent);
        String btnHtml = "<i class=\"icon-warning-sign\"></i> <span>(" + this.numEvents + ")</span>"; //$NON-NLS-1$ //$NON-NLS-2$
        this.sitWatchButton.setHTML(btnHtml);
        sitWatchButton.setVisible(true);
    }

    /**
     * Event handler that fires when the user clicks the situation watcher button.
     * @param event
     */
    @EventHandler("sitwatch-btn")
    public void onSitWatchClick(ClickEvent event) {
        int p = this.sitWatchButton.getAbsoluteTop() + this.sitWatchButton.getOffsetHeight() + 10;
        this.sitWatchEvents.getElement().getStyle().setTop(p, Unit.PX);
        this.sitWatchEvents.setVisible(!this.sitWatchEvents.isVisible());
        this.sitWatchButton.setFocus(false);
        this.sitWatchEvents.getElement().focus();
    }

    /**
     * Called after construction.
     */
    @PostConstruct
    protected void postConstruct() {
        filtersPanel.addValueChangeHandler(new ValueChangeHandler<SituationsFilterBean>() {
            @Override
            public void onValueChange(ValueChangeEvent<SituationsFilterBean> event) {
                doSearch();
            }
        });
        pager.addValueChangeHandler(new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> event) {
                doSearch(event.getValue());
            }
        });
        situationsTable.addTableSortHandler(new TableSortEvent.Handler() {
            @Override
            public void onTableSort(TableSortEvent event) {
                doSearch(currentPage);
            }
        });
        toggleFilterSwitch.addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                applyActionToFilteredRowsOnly = Boolean.valueOf(event.getValue());
            }
        });

        // Hide column 1 when in mobile mode.
        situationsTable.setColumnClasses(1, "desktop-only"); //$NON-NLS-1$

        this.rangeSpan.setInnerText("?"); //$NON-NLS-1$
        this.totalSpan.setInnerText("?"); //$NON-NLS-1$

        sitWatchButton.setVisible(false);
        sitWatchEvents.setVisible(false);
    }

    /**
     * Event handler that fires when the user clicks the refresh button.
     * @param event
     */
    @EventHandler("btn-refresh")
    public void onRefreshClick(ClickEvent event) {
        doSearch(currentPage);
    }

    /**
     * Kick off a search at this point so that we show some data in the UI.
     * @see org.overlord.dtgov.ui.client.local.pages.AbstractPage#onPageShowing()
     */
    @Override
    protected void onPageShowing() {
        // Kick off an artifact search
        doSearch();
        // Refresh the artifact filters
        filtersPanel.refresh();
    }

    /**
     * Search for artifacts based on the current filter settings and search text.
     */
    protected void doSearch() {
        doSearch(1);
    }

    /**
     * Search for situations based on the current filter settings.
     * @param page
     */
    protected void doSearch(int page) {
        onSearchStarting();
        currentPage = page;
        SortColumn currentSortColumn = this.situationsTable.getCurrentSortColumn();
        situationsService.search(filtersPanel.getValue(), page, currentSortColumn.columnId,
                currentSortColumn.ascending, new IRpcServiceInvocationHandler<SituationResultSetBean>() {
            @Override
            public void onReturn(SituationResultSetBean data) {
                updateTable(data);
                updatePager(data);
                resetSituationWatcher();
            }
            @Override
            public void onError(Throwable error) {
                notificationService.sendErrorNotification(i18n.format("situations.error-loading"), error); //$NON-NLS-1$
                noDataMessage.setVisible(true);
                searchInProgressMessage.setVisible(false);
            }
        });
    }

    /**
     * Called when a new search is kicked off.
     */
    protected void onSearchStarting() {
        this.pager.setVisible(false);
        this.searchInProgressMessage.setVisible(true);
        this.situationsTable.setVisible(false);
        this.noDataMessage.setVisible(false);
        this.rangeSpan.setInnerText("?"); //$NON-NLS-1$
        this.totalSpan.setInnerText("?"); //$NON-NLS-1$
    }

    /**
     * Updates the table of situations with the given data.
     * @param data
     */
    protected void updateTable(SituationResultSetBean data) {
        this.situationsTable.clear();
        this.searchInProgressMessage.setVisible(false);
        if (data.getSituations().size() > 0) {
            for (SituationSummaryBean summaryBean : data.getSituations()) {
                this.situationsTable.addRow(summaryBean);
            }
            this.situationsTable.setVisible(true);
        } else {
            this.noDataMessage.setVisible(true);
        }
    }

    /**
     * Updates the pager with the given data.
     * @param data
     */
    protected void updatePager(SituationResultSetBean data) {
        int numPages = ((int) (data.getTotalResults() / data.getItemsPerPage())) + (data.getTotalResults() % data.getItemsPerPage() == 0 ? 0 : 1);
        int thisPage = (data.getStartIndex() / data.getItemsPerPage()) + 1;
        this.pager.setNumPages(numPages);
        this.pager.setPage(thisPage);
        if (numPages > 1)
            this.pager.setVisible(true);

        int startIndex = data.getStartIndex() + 1;
        int endIndex = startIndex + data.getSituations().size() - 1;
        String rangeText = "" + startIndex + "-" + endIndex; //$NON-NLS-1$ //$NON-NLS-2$
        String totalText = String.valueOf(data.getTotalResults());
        this.rangeSpan.setInnerText(rangeText);
        this.totalSpan.setInnerText(totalText);
    }

    /**
     * Resets the situation watcher widget.
     */
    protected void resetSituationWatcher() {
        this.numEvents = 0;
        sitWatchButton.setVisible(false);
        this.sitWatchEvents.setVisible(false);
        this.sitWatchEvents.clear();
    }
    
    /**
     * Event handler that fires when the user clicks the retry button.
     * 
     * @param event
     */
    @EventHandler("retrySituations")
    public void onRetryClick(ClickEvent event) {
        SituationsFilterBean situationsFilterBean = applyActionToFilteredRowsOnly ? filtersPanel.getValue()
                : new SituationsFilterBean();
        final NotificationBean notificationBean = notificationService.startProgressNotification(
                i18n.format("situation-details.resubmit-message-title"), //$NON-NLS-1$
                i18n.format("situation.batch-retry-message-msg")); //$NON-NLS-1$
        situationsService.resubmit(situationsFilterBean,
                new RpcServiceInvocationHandlerAdapter<BatchRetryResult>() {
                    @Override
                    public void doOnReturn(BatchRetryResult data) {
                        notificationService.completeProgressNotification(notificationBean.getUuid(),
                                i18n.format("situation-details.message-resubmitted"), //$NON-NLS-1$
                                i18n.format(
                                        "situation.batch-retry-result", data.getProcessedCount(), data.getIgnoredCount(), data.getFailedCount())); //$NON-NLS-1$
                    }

                    @Override
                    public void doOnError(Throwable error) {
                        notificationService.completeProgressNotification(notificationBean.getUuid(),
                                i18n.format("situation-details.resubmit-error"), //$NON-NLS-1$
                                error);
                    }

                    @Override
                    public void doOnComplete(RpcResult<BatchRetryResult> result) {
                    }
                });
    }

}
