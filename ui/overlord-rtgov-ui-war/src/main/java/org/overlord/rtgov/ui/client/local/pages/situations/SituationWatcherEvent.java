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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.nav.client.local.TransitionAnchorFactory;
import org.overlord.rtgov.ui.client.local.ClientMessages;
import org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage;
import org.overlord.rtgov.ui.client.model.SituationEventBean;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * Widget used to show a single event in the situation event watcher panel.
 * @author eric.wittmann@redhat.com
 */
@Dependent
public class SituationWatcherEvent extends FlowPanel implements HasValue<SituationEventBean> {

    @Inject
    protected TransitionAnchorFactory<SituationDetailsPage> toDetailsPageLinkFactory;

    /**
     * Constructor.
     */
    public SituationWatcherEvent() {
        setStyleName("sitwatch-event"); //$NON-NLS-1$
    }

    /**
     * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
     */
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<SituationEventBean> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#getValue()
     */
    @Override
    public SituationEventBean getValue() {
        return null;
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object)
     */
    @Override
    public void setValue(SituationEventBean value) {
        setValue(value, false);
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object, boolean)
     *
     *      <div class="sitwatch-event">
     *        <div class="icon icon-severity-critical"></div>
     *        <a class="title" href="#">Rate Limit Exceeded</a>
     *        <span class="timestamp">9:16 AM</span>
     *        <span class="subject">{urn:namespace}ImportantService|VeryImportantOperation</span>
     *      </div>
     */
    @Override
    public void setValue(SituationEventBean value, boolean fireEvents) {
        clear();
        FlowPanel icon = new FlowPanel();
        icon.setStyleName("icon"); //$NON-NLS-1$
        icon.addStyleName("icon-severity-" + value.getSeverity()); //$NON-NLS-1$
        Anchor type = toDetailsPageLinkFactory.get("id", value.getSituationId()); //$NON-NLS-1$
        type.setText(value.getType());
        type.setStyleName("title"); //$NON-NLS-1$
        InlineLabel timestamp = new InlineLabel(ClientMessages.getTimeFormat().format(value.getTimestamp()));
        timestamp.setStyleName("timestamp"); //$NON-NLS-1$
        InlineLabel subject = new InlineLabel(value.getSubject());
        subject.setStyleName("subject"); //$NON-NLS-1$

        add(icon);
        add(type);
        add(timestamp);
        add(subject);
    }

}
