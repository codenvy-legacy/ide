/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.outline;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.event.ActivePartChangedEvent;
import com.codenvy.ide.api.event.ActivePartChangedHandler;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.parts.OutlinePart;
import com.codenvy.ide.api.parts.base.BasePresenter;
import com.codenvy.ide.api.texteditor.outline.HasOutline;
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
    private final OutlinePartView view;
    private final CoreLocalizationConstant coreLocalizationConstant;
    private HasOutline activePart;

    @Inject
    public OutlinePartPresenter(final OutlinePartView view, EventBus eventBus, CoreLocalizationConstant coreLocalizationConstant) {
        this.view = view;
        this.coreLocalizationConstant = coreLocalizationConstant;

        view.setTitle(coreLocalizationConstant.outlineTitleBarText());
        view.showNoOutline(coreLocalizationConstant.outlineNoFileOpenedMessage());
        view.setDelegate(this);

        eventBus.addHandler(ActivePartChangedEvent.TYPE, this);
        eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
            @Override
            public void onProjectOpened(ProjectActionEvent event) {

            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
                view.clear();
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return coreLocalizationConstant.outlineButtonTitle();
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getTitleImage() {
        // TODO need to add an image
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public SVGResource getTitleSVGImage() {
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
        if (event.getActivePart() == null) {
            view.showNoOutline(coreLocalizationConstant.outlineNoFileOpenedMessage());
        }
        if (event.getActivePart() instanceof HasOutline) {
            if (activePart != event.getActivePart()) {
                activePart = (HasOutline)event.getActivePart();
                if (activePart.getOutline() != null) {
                    activePart.getOutline().go(view.getContainer());
                } else {
                    view.showNoOutline(coreLocalizationConstant.outlineNotAvailableMessage());
                }
            }
        }
    }
}
