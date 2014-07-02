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
package com.codenvy.ide.extension.runner.client.shell;

import com.codenvy.ide.api.event.ActivePartChangedEvent;
import com.codenvy.ide.api.event.ActivePartChangedHandler;
import com.codenvy.ide.api.parts.base.BasePresenter;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import org.vectomatic.dom.svg.ui.SVGResource;

/**
 * WebShell to runner instance.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ShellConsolePresenter extends BasePresenter implements ShellConsoleView.ActionDelegate, ActivePartChangedHandler {
    private static final String TITLE = "WebShell";
    private HandlerRegistration handlerRegistration;
    private ShellConsoleView    view;
    private EventBus            eventBus;
    private String              url;

    @Inject
    public ShellConsolePresenter(final ShellConsoleView view, EventBus eventBus) {
        this.view = view;
        this.eventBus = eventBus;
        this.view.setTitle(TITLE);
        this.view.setDelegate(this);
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return TITLE;
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getTitleImage() {
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
        return "WebShell";
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
        handlerRegistration = eventBus.addHandler(ActivePartChangedEvent.TYPE, this);
    }

    /** Set WebShell URL. */
    public void setUrl(final String url) {
        this.url = url;
    }

    @Override
    public void onActivePartChanged(ActivePartChangedEvent event) {
        // In order to avoid some troubles of loading WebShell page into IFrame,
        // page should be loaded into view when part becomes active.
        if (event.getActivePart() == this && url != null) {
            view.setUrl(url);
            handlerRegistration.removeHandler();
        }
    }
}