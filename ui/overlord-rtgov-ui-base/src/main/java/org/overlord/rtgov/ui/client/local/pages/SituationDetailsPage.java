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

import static org.overlord.rtgov.ui.client.model.ResolutionState.IN_PROGRESS;
import static org.overlord.rtgov.ui.client.model.ResolutionState.RESOLVED;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.InitialState;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.nav.client.local.PageState;
import org.jboss.errai.ui.nav.client.local.TransitionAnchor;
import org.jboss.errai.ui.nav.client.local.TransitionTo;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.commons.gwt.client.local.widgets.HtmlSnippet;
import org.overlord.commons.gwt.client.local.widgets.Pager;
import org.overlord.rtgov.ui.client.model.ResolutionState;
import org.overlord.rtgov.ui.client.local.ClientMessages;
import org.overlord.rtgov.ui.client.local.events.TableSortEvent;
import org.overlord.rtgov.ui.client.local.pages.situations.CallTraceDetails;
import org.overlord.rtgov.ui.client.local.pages.situations.CallTraceWidget;
import org.overlord.rtgov.ui.client.local.pages.situations.SituationContextTable;
import org.overlord.rtgov.ui.client.local.pages.situations.SituationPropertiesTable;
import org.overlord.rtgov.ui.client.local.pages.situations.SituationTable;
import org.overlord.rtgov.ui.client.local.services.NotificationService;
import org.overlord.rtgov.ui.client.local.services.SituationsServiceCaller;
import org.overlord.rtgov.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.rtgov.ui.client.local.services.rpc.IRpcServiceInvocationHandler.RpcServiceInvocationHandlerAdapter;
import org.overlord.rtgov.ui.client.local.util.DOMUtil;
import org.overlord.rtgov.ui.client.local.util.DataBindingDateTimeConverter;
import org.overlord.rtgov.ui.client.local.widgets.common.SortableTemplatedWidgetTable.SortColumn;
import org.overlord.rtgov.ui.client.local.widgets.common.SourceEditor;
import org.overlord.rtgov.ui.client.model.NotificationBean;
import org.overlord.rtgov.ui.client.model.SituationBean;
import org.overlord.rtgov.ui.client.model.SituationResultSetBean;
import org.overlord.rtgov.ui.client.model.SituationSummaryBean;
import org.overlord.rtgov.ui.client.model.TraceNodeBean;

import com.google.common.collect.HashMultimap;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * The Situation Details page.
 *
 * @author eric.wittmann@redhat.com
 */
@Templated("/org/overlord/rtgov/ui/client/local/site/situationDetails.html#page")
@Page(path="situationDetails")
@Dependent
public class SituationDetailsPage extends AbstractPage {

    @Inject
    protected ClientMessages i18n;
    @Inject
    protected SituationsServiceCaller situationsService;
    @Inject
    protected NotificationService notificationService;
    @Inject
    protected IRpcServiceInvocationHandler.VoidInvocationHandler voidInvocationHandler;

    @PageState
    private String id;

    @Inject @AutoBound
    protected DataBinder<SituationBean> situation;

    // Breadcrumbs
    @Inject @DataField("back-to-dashboard")
    private TransitionAnchor<DashboardPage> toDashboardPage;
    @Inject @DataField("to-situations")
    private TransitionAnchor<SituationsPage> toSituationsPage;
    @Inject @DataField("to-services")
    private TransitionAnchor<ServicesPage> toServicesPage;

    // Header
    @Inject @DataField @Bound(property="type")
    InlineLabel situationName;

    @Inject @DataField
    FlowPanel severity;

    // Properties
    @Inject @DataField @Bound(property="subject")
    InlineLabel subject;
    @Inject @DataField @Bound(property="timestamp", converter=DataBindingDateTimeConverter.class)
    InlineLabel timestamp;
    @Inject @DataField @Bound(property="resolutionState")
    InlineLabel resolutionState;
    @Inject @DataField @Bound(property="description")
    InlineLabel description;
    
    @Inject @DataField("btn-resubmitted-situation")
    Button resubmittedSituationButton;

    @Inject 
    TransitionTo<SituationDetailsPage> resubmittedSituationLink;
    
    @Inject @DataField("properties-table") @Bound(property="properties")
    SituationPropertiesTable propertiesTable;
    @Inject @DataField("context-table") @Bound(property="context")
    SituationContextTable contextTable;

    @Inject @DataField("call-trace") @Bound(property="callTrace")
    CallTraceWidget callTrace;
    @Inject @DataField("call-trace-detail")
    CallTraceDetails callTraceDetail;

    @Inject @DataField("messageTab")
    Anchor messageTabAnchor;
    @Inject @DataField("message-editor")
    SourceEditor messageEditor;
    @Inject  @DataField("btn-resubmit")
    Button resubmitButton;
    @Inject @DataField
    InlineLabel resubmitDetails;
    @Inject @DataField("btn-assign")
    Button assignButton;
    @Inject @DataField("btn-start")
    Button startButton;
    @Inject @DataField("btn-resolve")
    Button resolveButton;
    
    @Inject @DataField("resubmitFailuresTab")
    Anchor resubmitFailuresTabAnchor;

    @Inject @DataField("btn-refresh")
    protected Button refreshButton;

    @Inject @DataField("resubmit-failures-none")
    protected HtmlSnippet noDataMessage;
    @Inject @DataField("resubmit-failures-searching")
    protected HtmlSnippet searchInProgressMessage;
    @Inject @DataField("resubmit-failures-table")
    protected SituationTable resubmitFailuresTable;

    @Inject @DataField("resubmit-failures-pager")
    protected Pager pager;
    @DataField("resubmit-failures-range")
    protected SpanElement rangeSpan = Document.get().createSpanElement();
    @DataField("resubmit-failures-total")
    protected SpanElement totalSpan = Document.get().createSpanElement();

    private int currentPage = 1;

    @Inject @DataField("situation-details-loading-spinner")
    protected HtmlSnippet loading;
    protected Element pageContent;
    
    String resubmittedId=null;

    /**
     * Constructor.
     */
    public SituationDetailsPage() {
    }

    /**
     * Called after the widget is constructed.
     */
    @PostConstruct
    protected void onPostConstruct() {
        pageContent = DOMUtil.findElementById(getElement(), "situation-details-content-wrapper"); //$NON-NLS-1$
        pageContent.addClassName("hide"); //$NON-NLS-1$
        callTraceDetail.setVisible(false);
        callTrace.addSelectionHandler(new SelectionHandler<TraceNodeBean>() {
            @Override
            public void onSelection(SelectionEvent<TraceNodeBean> event) {
                onCallTraceNodeSelected(event.getSelectedItem());
            }
        });
        
        pager.addValueChangeHandler(new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> event) {
                doRetrieveResubmitFailures(event.getValue());
            }
        });
        resubmitFailuresTable.addTableSortHandler(new TableSortEvent.Handler() {
            @Override
            public void onTableSort(TableSortEvent event) {
                doRetrieveResubmitFailures(currentPage);
            }
        });
        
        // Hide column 1 when in mobile mode.
        resubmitFailuresTable.setColumnClasses(1, "desktop-only"); //$NON-NLS-1$

        this.rangeSpan.setInnerText("?"); //$NON-NLS-1$
        this.totalSpan.setInnerText("?"); //$NON-NLS-1$

    }

    /**
     * Event handler that fires when the user clicks the refresh button.
     * @param event
     */
    @EventHandler("btn-refresh")
    public void onRefreshClick(ClickEvent event) {
        doRetrieveResubmitFailures(currentPage);
    }

    /**
     * @see org.overlord.sramp.ui.client.local.pages.AbstractPage#onPageShowing()
     */
    @Override
    protected void onPageShowing() {
        pageContent.addClassName("hide"); //$NON-NLS-1$
        loading.getElement().removeClassName("hide"); //$NON-NLS-1$
        loadSituationAndUpdatePageData();
    }

	private void loadSituationAndUpdatePageData() {
		situationsService.get(id, new IRpcServiceInvocationHandler<SituationBean>() {
            @Override
            public void onReturn(SituationBean data) {
                updateMetaData(data);
                
                if (data.getResubmissionFailureTotalCount() > 0) {
                    doRetrieveResubmitFailures();
                }
            }
            @Override
            public void onError(Throwable error) {
                notificationService.sendErrorNotification(i18n.format("situation-details.error-getting-detail-info"), error); //$NON-NLS-1$
            }
        });
	}

    /**
     * Called when the situation is loaded.
     * @param situation
     */
    protected void updateMetaData(final SituationBean situation) {
        this.situation.setModel(situation, InitialState.FROM_MODEL);
        severity.setStyleName("icon"); //$NON-NLS-1$
        severity.addStyleName("details-icon"); //$NON-NLS-1$
        severity.addStyleName("icon-severity-" + situation.getSeverity()); //$NON-NLS-1$
        loading.getElement().addClassName("hide"); //$NON-NLS-1$
        pageContent.removeClassName("hide"); //$NON-NLS-1$
        this.messageTabAnchor.setVisible(situation.hasMessage());
        if (situation.hasMessage()) {
            messageEditor.setValue(situation.getMessage().getContent());
        } else {
            messageEditor.setValue(""); //$NON-NLS-1$
        }
        resubmitButton.setEnabled(situation.isResubmitPossible()
                        && !situation.getSituationId().equals(resubmittedId));
        messageEditor.setReadOnly(!situation.isResubmitPossible()
                        && !situation.getSituationId().equals(resubmittedId));
        if (situation.resubmitBy() != null) {
            resubmitDetails.setText(i18n.format("situation-details.resubmit-details",
                    situation.resubmitBy(), situation.resubmitAt(), situation.resubmitResult()));
            resubmitDetails.setTitle(situation.resubmitErrorMessage());
        } else {
            resubmitDetails.setTitle("");
            resubmitDetails.setText("");
        }
        if (situation.resubmitError()) {
            resubmitDetails.getElement().addClassName("text-error"); //$NON-NLS-1$
        } else {
            resubmitDetails.getElement().removeClassName("text-error"); //$NON-NLS-1$
        }

        if (situation.isAssignedToCurrentUser()) {
            assignButton.getElement().addClassName("hide");
        } else {
            assignButton.getElement().removeClassName("hide");
        }

        ResolutionState resolutionState = ResolutionState.valueOf(situation.getResolutionState());
        if (situation.isManualResolutionPossible() && ResolutionState.UNRESOLVED == resolutionState) {
            startButton.getElement().removeClassName("hide");
        } else {
            startButton.getElement().addClassName("hide");
        }
        if (situation.isManualResolutionPossible() && ResolutionState.IN_PROGRESS == resolutionState) {
            resolveButton.getElement().removeClassName("hide");
        } else {
            resolveButton.getElement().addClassName("hide");
        }
        
        if (situation.getResubmittedSituationId() == null) {
            resubmittedSituationButton.getElement().addClassName("hide");
        }
        
        this.resubmitFailuresTable.clear();
        
        if (situation.getResubmissionFailureTotalCount() > 0) {
            this.resubmitFailuresTabAnchor.setVisible(true);
        } else {
            this.resubmitFailuresTabAnchor.setVisible(false);
        }
    }

    /**
     * Event handler called when the user clicks an item in the call trace widget.
     * @param event
     */
    public void onCallTraceNodeSelected(TraceNodeBean node) {
        callTraceDetail.setValue(node);
        callTraceDetail.setVisible(true);
    }

    /**
     * Called when the user clicks the Resubmit button.
     * @param event
     */
    @EventHandler("btn-resubmitted-situation")
    protected void onResubmittedSituationClick(ClickEvent event) {
        HashMultimap<String,String> map=HashMultimap.<String,String>create();
        map.put("id", situation.getModel().getResubmittedSituationId());
        resubmittedSituationLink.go(map);
    }
    
   /**
     * Called when the user clicks the Resubmit button.
     * @param event
     */
    @EventHandler("btn-resubmit")
    protected void onResubmitClick(ClickEvent event) {
        final NotificationBean notificationBean = notificationService.startProgressNotification(
                i18n.format("situation-details.resubmit-message-title"), //$NON-NLS-1$
                i18n.format("situation-details.resubmit-message-msg", this.situation.getModel().getSubject())); //$NON-NLS-1$
        situationsService.resubmit(situation.getModel().getSituationId(), this.messageEditor.getValue(), 
                new RpcServiceInvocationHandlerAdapter<Void>() {
            @Override
            public void doOnReturn(Void data) {
                notificationService.completeProgressNotification(notificationBean.getUuid(),
                        i18n.format("situation-details.message-resubmitted"), //$NON-NLS-1$
                        i18n.format("situation-details.resubmit-success-msg", situation.getModel().getSubject())); //$NON-NLS-1$
            }
            @Override
            public void doOnError(Throwable error) {
                notificationService.completeProgressNotification(notificationBean.getUuid(),
                        i18n.format("situation-details.resubmit-error"), //$NON-NLS-1$
                        error);
            }
            @Override
            public void doOnComplete(RpcResult<Void> result) {
                resubmittedId = situation.getModel().getSituationId();
                loadSituationAndUpdatePageData();
            }
        });
    }
    
	@EventHandler("btn-assign")
	protected void onAssignButtonClick(ClickEvent event) {
		situationsService.assign(id, new IRpcServiceInvocationHandler<Void>() {

            @Override
            public void onReturn(Void data) {
                loadSituationAndUpdatePageData();
            }

            @Override
            public void onError(Throwable error) {
            }		    
		});
	}

	@EventHandler("btn-start")
	protected void onStartButtonClick(ClickEvent event) {
		situationsService.updateResolutionState(id, IN_PROGRESS.name(), new IRpcServiceInvocationHandler<Void>() {

            @Override
            public void onReturn(Void data) {
                loadSituationAndUpdatePageData();
            }

            @Override
            public void onError(Throwable error) {
            }           
        });
	}

	@EventHandler("btn-resolve")
	protected void onResolveButtonClick(ClickEvent event) {
        situationsService.updateResolutionState(id, RESOLVED.name(), new IRpcServiceInvocationHandler<Void>() {

            @Override
            public void onReturn(Void data) {
                loadSituationAndUpdatePageData();
            }

            @Override
            public void onError(Throwable error) {
            }           
        });
	}

    /**
     * Retrieve resubmit failures.
     */
    protected void doRetrieveResubmitFailures() {
        doRetrieveResubmitFailures(1);
    }

    /**
     * Retrieve resubmit failures for specified page.
     * @param page
     */
    protected void doRetrieveResubmitFailures(int page) {
        onRetrieveResubmitFailuresStarting();
        currentPage = page;
        SortColumn currentSortColumn = this.resubmitFailuresTable.getCurrentSortColumn();
        situationsService.getResubmitFailures(id, page, currentSortColumn.columnId,
                currentSortColumn.ascending, new IRpcServiceInvocationHandler<SituationResultSetBean>() {
            @Override
            public void onReturn(SituationResultSetBean data) {
                updateTable(data);
                updatePager(data);
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
     * Called when a new retrieval is kicked off.
     */
    protected void onRetrieveResubmitFailuresStarting() {
        this.pager.setVisible(false);
        this.searchInProgressMessage.setVisible(true);
        this.resubmitFailuresTable.setVisible(false);
        this.noDataMessage.setVisible(false);
        this.rangeSpan.setInnerText("?"); //$NON-NLS-1$
        this.totalSpan.setInnerText("?"); //$NON-NLS-1$
    }

    /**
     * Updates the table of resubmit failures with the given data.
     * @param data
     */
    protected void updateTable(SituationResultSetBean data) {
        this.resubmitFailuresTable.clear();
        this.searchInProgressMessage.setVisible(false);
        if (data.getSituations().size() > 0) {
            for (SituationSummaryBean summaryBean : data.getSituations()) {
                this.resubmitFailuresTable.addRow(summaryBean);
            }
            this.resubmitFailuresTable.setVisible(true);
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

}
