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

import javax.inject.Inject;

import org.overlord.rtgov.ui.client.local.ClientMessages;
import org.overlord.rtgov.ui.client.local.widgets.common.AbstractFilterListBox;
import org.overlord.rtgov.ui.client.model.ResolutionState;

public class ResolutionStateListBox extends AbstractFilterListBox {

    @Inject
    protected ClientMessages i18n;

    /**
     * Constructor.
     */
    public ResolutionStateListBox() {
    }

    /**
     * @see org.overlord.dtgov.ui.client.local.widgets.common.AbstractFilterListBox#configureItems()
     */
    @Override
    protected void configureItems() {
        this.addItem(i18n.format("any"), ""); //$NON-NLS-1$ //$NON-NLS-2$
        this.addItem(i18n.format("resolutionState.unresolved"), ResolutionState.UNRESOLVED.name()); //$NON-NLS-1$ //$NON-NLS-2$
        this.addItem(i18n.format("resolutionState.resolved"), ResolutionState.RESOLVED.name()); //$NON-NLS-1$ //$NON-NLS-2$
        this.addItem(i18n.format("resolutionState.in_progress"), ResolutionState.IN_PROGRESS.name()); //$NON-NLS-1$ //$NON-NLS-2$
        this.addItem(i18n.format("resolutionState.waiting"), ResolutionState.WAITING.name()); //$NON-NLS-1$ //$NON-NLS-2$
        this.addItem(i18n.format("resolutionState.reopened"), ResolutionState.REOPENED.name()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * @see com.google.gwt.user.client.ui.ListBox#clear()
     */
    @Override
    public void clear() {
        super.clear();
        configureItems();
    }

}
