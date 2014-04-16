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
import static org.overlord.rtgov.ui.client.model.ResolutionState.REOPENED;
import static org.overlord.rtgov.ui.client.model.ResolutionState.RESOLVED;
import static org.overlord.rtgov.ui.client.model.ResolutionState.WAITING;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.InitialState;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.nav.client.local.PageState;
import org.jboss.errai.ui.nav.client.local.TransitionAnchor;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.commons.gwt.client.local.widgets.HtmlSnippet;
import org.overlord.rtgov.ui.client.local.ClientMessages;
import org.overlord.rtgov.ui.client.local.pages.situations.CallTraceDetails;
import org.overlord.rtgov.ui.client.local.pages.situations.CallTraceWidget;
import org.overlord.rtgov.ui.client.local.pages.situations.SituationPropertiesTable;
import org.overlord.rtgov.ui.client.local.services.NotificationService;
import org.overlord.rtgov.ui.client.local.services.SituationsRpcService;
import org.overlord.rtgov.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.rtgov.ui.client.local.services.rpc.IRpcServiceInvocationHandler.RpcServiceInvocationHandlerAdapter;
import org.overlord.rtgov.ui.client.local.util.DOMUtil;
import org.overlord.rtgov.ui.client.local.util.DataBindingDateTimeConverter;
import org.overlord.rtgov.ui.client.local.widgets.common.SourceEditor;
import org.overlord.rtgov.ui.client.model.NotificationBean;
import org.overlord.rtgov.ui.client.model.ResolutionState;
import org.overlord.rtgov.ui.client.model.SituationBean;
import org.overlord.rtgov.ui.client.model.TraceNodeBean;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
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
    protected SituationsRpcService situationsService;
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

    @Inject @DataField("properties-table") @Bound(property="properties")
    SituationPropertiesTable propertiesTable;
    @Inject @DataField("context-table") @Bound(property="context")
    SituationPropertiesTable contextTable;

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
    @Inject @DataField("btn-close")
    Button closeButton;
    @Inject @DataField("btn-start")
    Button startButton;
    @Inject @DataField("btn-stop")
    Button stopButton;
    @Inject @DataField("btn-resolve")
    Button resolveButton;
    @Inject @DataField("btn-reopen")
    Button reopenButton;

    @Inject @DataField("situation-details-loading-spinner")
    protected HtmlSnippet loading;
    protected Element pageContent;

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
    protected void updateMetaData(SituationBean situation) {
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
        resubmitButton.setEnabled(situation.isResubmitPossible());
        messageEditor.setReadOnly(!situation.isResubmitPossible());
        if (situation.getResubmitBy() != null) {
            resubmitDetails.setText(i18n.format("situation-details.resubmit-details",
                    situation.getResubmitBy(), situation.getResubmitAt(), situation.getResubmitResult()));
            resubmitDetails.setTitle(situation.getResubmitErrorMessage());
        } else {
            resubmitDetails.setTitle("");
            resubmitDetails.setText("");
        }
        if (situation.isResubmitError()) {
            resubmitDetails.getElement().addClassName("text-error"); //$NON-NLS-1$
        } else {
            resubmitDetails.getElement().removeClassName("text-error"); //$NON-NLS-1$
        }
		if (situation.isAssignedToCurrentUser() || (situation.getAssignedTo() != null && situation.isTakeoverPossible())) {
			if (situation.isAssignedToCurrentUser()) {
				assignButton.getElement().addClassName("hide");
			} else {
				assignButton.getElement().removeClassName("hide");
			}
			closeButton.getElement().removeClassName("hide"); //$NON-NLS-1$
			ResolutionState resolutionState = ResolutionState.valueOf(situation.getResolutionState());
			if (ResolutionState.UNRESOLVED == resolutionState
					|| ResolutionState.WAITING == resolutionState
					|| ResolutionState.REOPENED == resolutionState) {
				startButton.getElement().removeClassName("hide");
			} else {
				startButton.getElement().addClassName("hide");
			}
			if (ResolutionState.IN_PROGRESS == resolutionState) {
				stopButton.getElement().removeClassName("hide");
				resolveButton.getElement().removeClassName("hide");
			} else {
				resolveButton.getElement().addClassName("hide");
				stopButton.getElement().addClassName("hide");
			}
			if (ResolutionState.RESOLVED == resolutionState) {
				reopenButton.getElement().removeClassName("hide");
			} else {
				reopenButton.getElement().addClassName("hide");
			}
        } else {
        	closeButton.getElement().addClassName("hide"); 
        	startButton.getElement().addClassName("hide");
        	reopenButton.getElement().addClassName("hide");
        	stopButton.getElement().addClassName("hide");
        	resolveButton.getElement().addClassName("hide");
        	assignButton.getElement().removeClassName("hide"); 
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
                loadSituationAndUpdatePageData();
            }
        });
    }
    
	@EventHandler("btn-assign")
	protected void onAssignButtonClick(ClickEvent event) {
		situationsService.assign(id, voidInvocationHandler);
		loadSituationAndUpdatePageData();
	}

	@EventHandler("btn-close")
	protected void onDeassignButtonClick(ClickEvent event) {
		situationsService.close(id, voidInvocationHandler);
		loadSituationAndUpdatePageData();
	}

	@EventHandler("btn-start")
	protected void onStartButtonClick(ClickEvent event) {
		situationsService.updateResolutionState(id, IN_PROGRESS.name(), voidInvocationHandler);
		loadSituationAndUpdatePageData();
	}

	@EventHandler("btn-stop")
	protected void onStopButtonClick(ClickEvent event) {
		situationsService.updateResolutionState(id, WAITING.name(), voidInvocationHandler);
		loadSituationAndUpdatePageData();
	}

	@EventHandler("btn-resolve")
	protected void onResolveButtonClick(ClickEvent event) {
		situationsService.updateResolutionState(id, RESOLVED.name(), voidInvocationHandler);
		loadSituationAndUpdatePageData();
	}

	@EventHandler("btn-reopen")
	protected void onReopenButtonClick(ClickEvent event) {
		situationsService.updateResolutionState(id, REOPENED.name(), voidInvocationHandler);
		loadSituationAndUpdatePageData();
	}

}
