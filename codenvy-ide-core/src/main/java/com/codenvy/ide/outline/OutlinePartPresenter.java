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
package com.codenvy.ide.outline;

import com.codenvy.ide.api.editor.TextEditorPartPresenter;
import com.codenvy.ide.api.event.ActivePartChangedEvent;
import com.codenvy.ide.api.event.ActivePartChangedHandler;
import com.codenvy.ide.api.parts.OutlinePart;
import com.codenvy.ide.api.parts.base.BasePresenter;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;


/**
 * Part presenter for Outline.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 */
@Singleton
public class OutlinePartPresenter extends BasePresenter implements ActivePartChangedHandler, OutlinePart, OutlinePartView.ActionDelegate {
    private final OutlinePartView         view;
    private       TextEditorPartPresenter activePart;

    @Inject
    public OutlinePartPresenter(OutlinePartView view, EventBus eventBus) {
        this.view = view;
        eventBus.addHandler(ActivePartChangedEvent.TYPE, this);
        view.setTitle("Outline");
        view.setDelegate(this);
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return "Outline";
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getTitleImage() {
        // TODO need to add an image
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getTitleToolTip() {
        // TODO need to add a tooltip
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    /** {@inheritDoc} */
    @Override
    public void onActivePartChanged(ActivePartChangedEvent event) {
        if (event.getActivePart() instanceof TextEditorPartPresenter) {
            if (activePart != event.getActivePart()) {
                activePart = (TextEditorPartPresenter)event.getActivePart();
                if (activePart.getOutline() != null) {
                    activePart.getOutline().go(view.getContainer());
                } else {
                    view.showNoOutline();
                }
            }
        }
    }
}