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

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.overlord.rtgov.ui.client.local.animations.FadeInAnimation;
import org.overlord.rtgov.ui.client.model.SituationEventBean;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * A widget that displays a bunch of situation events.
 * @author eric.wittmann@redhat.com
 */
public class SituationWatcherEvents extends FlowPanel {

    @Inject
    protected Instance<SituationWatcherEvent> sitWatchEventFactory;

    /**
     * Constructor.
     */
    public SituationWatcherEvents() {
    }

    /**
     * Adds an event.
     * @param event
     */
    public void add(SituationEventBean event) {
        SituationWatcherEvent widget = sitWatchEventFactory.get();
        widget.setValue(event);
        widget.getElement().getStyle().setOpacity(0.0);
        insert(widget, 0);
        FadeInAnimation animation = new FadeInAnimation(widget);
        animation.run(500);
    }

}
