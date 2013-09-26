/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.selection;

import com.codenvy.ide.api.event.ActivePartChangedEvent;
import com.codenvy.ide.api.event.ActivePartChangedHandler;
import com.codenvy.ide.api.event.SelectionChangedEvent;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.workspace.PartPresenter;
import com.codenvy.ide.api.ui.workspace.PropertyListener;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;


/**
 * Implements {@link SelectionAgent}
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@Singleton
public class SelectionAgentImpl implements ActivePartChangedHandler, PropertyListener, SelectionAgent {

    private PartPresenter activePart;

    private final EventBus eventBus;

    @Inject
    public SelectionAgentImpl(EventBus eventBus) {
        this.eventBus = eventBus;
        // bind event listener
        eventBus.addHandler(ActivePartChangedEvent.TYPE, this);
    }

    /** {@inheritDoc} */
    @Override
    public Selection<?> getSelection() {
        return activePart != null ? activePart.getSelection() : null;
    }

    protected void notifySelectionChanged() {
        eventBus.fireEvent(new SelectionChangedEvent(getSelection()));
    }

    /** {@inheritDoc} */
    @Override
    public void onActivePartChanged(ActivePartChangedEvent event) {
        // remove listener from previous active part
        if (activePart != null) {
            activePart.removePropertyListener(this);
        }
        // set new active part
        activePart = event.getActivePart();
        if (activePart != null) {
            activePart.addPropertyListener(this);
        }
        notifySelectionChanged();
    }

    /** {@inheritDoc} */
    @Override
    public void propertyChanged(PartPresenter source, int propId) {
        // Check property and ensure came from active part
        if (propId == PartPresenter.SELECTION_PROPERTY && source == activePart) {
            notifySelectionChanged();
        }
    }

}
