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

import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.InitialState;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.nav.client.local.PageState;
import org.jboss.errai.ui.nav.client.local.TransitionAnchor;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.commons.gwt.client.local.widgets.HtmlSnippet;
import org.overlord.rtgov.ui.client.local.ClientMessages;
import org.overlord.rtgov.ui.client.local.services.NotificationService;
import org.overlord.rtgov.ui.client.local.services.ServicesRpcService;
import org.overlord.rtgov.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.rtgov.ui.client.local.util.DOMUtil;
import org.overlord.rtgov.ui.client.local.util.DataBindingQNameLocalPartConverter;
import org.overlord.rtgov.ui.client.local.util.DataBindingQNameNamespaceConverter;
import org.overlord.rtgov.ui.client.model.ReferenceBean;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * The Reference Details page.
 *
 * @author eric.wittmann@redhat.com
 */
@Templated("/org/overlord/rtgov/ui/client/local/site/referenceDetails.html#page")
@Page(path="referenceDetails")
@Dependent
public class ReferenceDetailsPage extends AbstractPage {

    @Inject
    protected ClientMessages i18n;
    @Inject
    protected ServicesRpcService servicesService;
    @Inject
    protected NotificationService notificationService;

    @PageState
    private String id;

    @Inject @AutoBound
    protected DataBinder<ReferenceBean> reference;

    // Breadcrumbs
    @Inject @DataField("back-to-dashboard")
    private TransitionAnchor<DashboardPage> toDashboardPage;
    @Inject @DataField("back-to-services")
    private TransitionAnchor<ServicesPage> toServicesPage;
    @Inject @DataField("to-situations")
    private TransitionAnchor<SituationsPage> toSituationsPage;

    // Header
    @Inject @DataField @Bound(property="name", converter=DataBindingQNameLocalPartConverter.class)
    InlineLabel referenceName;

    // Properties
    @Inject @DataField @Bound(property="name", converter=DataBindingQNameNamespaceConverter.class)
    InlineLabel referenceNamespace;
    @Inject @DataField @Bound(property="application", converter=DataBindingQNameNamespaceConverter.class)
    InlineLabel applicationNamespace;
    @Inject @DataField @Bound(property="application", converter=DataBindingQNameLocalPartConverter.class)
    InlineLabel applicationName;
    @Inject @DataField @Bound(property="referenceInterface")
    InlineLabel referenceInterface;

    @Inject @DataField("reference-details-loading-spinner")
    protected HtmlSnippet loading;
    protected Element pageContent;

    /**
     * Constructor.
     */
    public ReferenceDetailsPage() {
    }

    /**
     * Called after the widget is constructed.
     */
    @PostConstruct
    protected void onPostConstruct() {
        pageContent = DOMUtil.findElementById(getElement(), "reference-details-content-wrapper"); //$NON-NLS-1$
        pageContent.addClassName("hide"); //$NON-NLS-1$
    }

    /**
     * @see org.overlord.sramp.ui.client.local.pages.AbstractPage#onPageShowing()
     */
    @Override
    protected void onPageShowing() {
        pageContent.addClassName("hide"); //$NON-NLS-1$
        loading.getElement().removeClassName("hide"); //$NON-NLS-1$
        servicesService.getReference(id, new IRpcServiceInvocationHandler<ReferenceBean>() {
            @Override
            public void onReturn(ReferenceBean data) {
                updateMetaData(data);
            }
            @Override
            public void onError(Throwable error) {
                notificationService.sendErrorNotification(i18n.format("reference-details.error-getting-detail-info"), error); //$NON-NLS-1$
            }
        });
    }

    /**
     * Called when the reference is loaded.
     * @param reference
     */
    protected void updateMetaData(ReferenceBean reference) {
        this.reference.setModel(reference, InitialState.FROM_MODEL);
        loading.getElement().addClassName("hide"); //$NON-NLS-1$
        pageContent.removeClassName("hide"); //$NON-NLS-1$
    }

}
