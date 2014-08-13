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
package com.codenvy.ide.extension.runner.client.console;

import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.api.runner.internal.Constants;
import com.codenvy.ide.api.event.ActivePartChangedEvent;
import com.codenvy.ide.api.event.ActivePartChangedHandler;
import com.codenvy.ide.api.parts.PartPresenter;
import com.codenvy.ide.api.parts.base.BasePresenter;
import com.codenvy.ide.extension.runner.client.RunnerUtils;
import com.codenvy.ide.toolbar.ToolbarPresenter;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.vectomatic.dom.svg.ui.SVGResource;

/**
 * Runner console.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class RunnerConsolePresenter extends BasePresenter implements RunnerConsoleView.ActionDelegate {
    private static final String TITLE = "Runner";
    private       RunnerConsoleView view;
    private final ToolbarPresenter  consoleToolbar;
    private       String            appURL;
    private       String            shellURL;
    private boolean isUnread = false;
    private boolean isTerminalFrameAlreadyLoaded;
    private boolean isAppPreviewFrameAlreadyLoaded;

    @Inject
    public RunnerConsolePresenter(RunnerConsoleView view, @RunnerConsoleToolbar ToolbarPresenter consoleToolbar, EventBus eventBus) {
        this.view = view;
        this.consoleToolbar = consoleToolbar;
        this.view.setTitle(TITLE);
        this.view.setDelegate(this);

        eventBus.addHandler(ActivePartChangedEvent.TYPE, new ActivePartChangedHandler() {
            @Override
            public void onActivePartChanged(ActivePartChangedEvent event) {
                onPartActivated(event.getActivePart());
            }
        });
    }

    private void onPartActivated(PartPresenter part) {
        if (part != null && part.equals(this) && isUnread) {
            isUnread = false;
            firePropertyChange(TITLE_PROPERTY);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return TITLE + (isUnread ? " *" : "");
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
        return "Displays Runner output";
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        consoleToolbar.go(view.getToolbarPanel());
        container.setWidget(view);
    }

    /**
     * Print message on console.
     *
     * @param message
     *         message that need to be print
     */
    public void print(String message) {
        PartPresenter activePart = partStack.getActivePart();
        if (activePart == null || !activePart.equals(this)) {
            isUnread = true;
            firePropertyChange(TITLE_PROPERTY);
        }
        view.print(message);
        view.scrollBottom();
    }

    /** Sets the console active (selected) in the parts stack. */
    public void setActive() {
        PartPresenter activePart = partStack.getActivePart();
        if (activePart == null || !activePart.equals(this)) {
            partStack.setActivePart(this);
        }
    }

    /** Clear console. Remove all messages. */
    public void clear() {
        view.clearConsole();
    }

    /** Set shell URL. */
    public void setShellURL(String url) {
        this.shellURL = url;
    }

    /** Should be called when current app is stopped. */
    public void onAppStopped() {
        shellURL = null;
        appURL = null;
        isTerminalFrameAlreadyLoaded = false;
        isAppPreviewFrameAlreadyLoaded = false;
        view.hideTerminal();
        view.hideAppPreview();
    }

    /** Should be called when current app is stopped. */
    public void onAppStarted(ApplicationProcessDescriptor processDescriptor) {
        shellURL = RunnerUtils.getLink(processDescriptor, Constants.LINK_REL_SHELL_URL) != null ? RunnerUtils.getLink(processDescriptor, Constants.LINK_REL_SHELL_URL).getHref() : null;
        appURL = RunnerUtils.getLink(processDescriptor, Constants.LINK_REL_WEB_URL) != null ? RunnerUtils.getLink(processDescriptor, Constants.LINK_REL_WEB_URL).getHref() : null;
        if (shellURL != null)
            view.reloadTerminalFrame(shellURL);
        if (appURL != null)
            view.reloadAppPreviewFrame(appURL);
    }

    /** Set URL to preview an app. */
    public void setAppURL(String url) {
        this.appURL = url;
    }

    /** {@inheritDoc} */
    @Override
    public void onTerminalTabOpened() {
        // Note: in order to avoid some troubles of loading shell page into IFrame,
        // page should be loaded into view when tab becomes visible.
        if (shellURL != null && !isTerminalFrameAlreadyLoaded) {
            view.reloadTerminalFrame(shellURL);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onTerminalLoaded() {
        isTerminalFrameAlreadyLoaded = true;
    }

    /** {@inheritDoc} */
    @Override
    public void onAppTabOpened() {
        // Note: in order to avoid some troubles of loading app page into IFrame,
        // page should be loaded into view when tab becomes visible.
        if (appURL != null && !isAppPreviewFrameAlreadyLoaded) {
            view.reloadAppPreviewFrame(appURL);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onAppPreviewLoaded() {
        isAppPreviewFrameAlreadyLoaded = true;
    }
}