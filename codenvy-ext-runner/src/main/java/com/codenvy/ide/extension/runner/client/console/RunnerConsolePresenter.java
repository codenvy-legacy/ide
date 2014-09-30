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
import com.codenvy.api.runner.gwt.client.utils.RunnerUtils;
import com.codenvy.api.runner.internal.Constants;
import com.codenvy.ide.api.event.ActivePartChangedEvent;
import com.codenvy.ide.api.event.ActivePartChangedHandler;
import com.codenvy.ide.api.parts.PartPresenter;
import com.codenvy.ide.api.parts.base.BasePresenter;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.codenvy.ide.extension.runner.client.run.RunnerStatus;
import com.codenvy.ide.toolbar.ToolbarPresenter;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.ui.SVGResource;

/**
 * Runner console.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class RunnerConsolePresenter extends BasePresenter implements RunnerConsoleView.ActionDelegate {
    private final RunnerConsoleView view;
    private final ToolbarPresenter  consoleToolbar;
    private final RunnerResources   runnerResources;
    private final RunnerLocalizationConstant runnerLocalizationConstant;
    private       String            appURL;
    private       String            shellURL;
    private boolean isUnread;
    private boolean isTerminalFrameAlreadyLoaded;
    private boolean isAppPreviewFrameAlreadyLoaded;
    private RunnerStatus currentRunnerStatus;

    private enum Tab {
        CONSOLE, TERMINAL, APP
    }

    private Tab activeTab = Tab.CONSOLE;

    @Inject
    public RunnerConsolePresenter(RunnerConsoleView view, @RunnerConsoleToolbar ToolbarPresenter consoleToolbar, EventBus eventBus,
                                  RunnerResources runnerResources, RunnerLocalizationConstant runnerLocalizationConstant) {
        this.view = view;
        this.consoleToolbar = consoleToolbar;
        this.runnerResources = runnerResources;
        this.view.setTitle(runnerLocalizationConstant.runnerConsoleViewTitle());
        this.view.setDelegate(this);
        this.isUnread = false;
        this.currentRunnerStatus = RunnerStatus.IDLE;
        this.runnerLocalizationConstant = runnerLocalizationConstant;

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
        return runnerLocalizationConstant.runnerConsoleViewTitle() + (isUnread ? " *" : "");
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getTitleImage() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public SVGResource getTitleSVGImage() {
        switch (currentRunnerStatus) {
            case IN_QUEUE:
                return runnerResources.inQueue();
            case IN_PROGRESS:
                return runnerResources.inProgress();
            case RUNNING:
                return runnerResources.running();
            case DONE:
                return runnerResources.done();
            case FAILED:
                return runnerResources.failed();
            case TIMEOUT:
                return runnerResources.timeout();
            case IDLE:
            default:
                return null;
        }
    }

    @Override
    public SVGImage decorateIcon(SVGImage svgImage) {
        if (svgImage == null) {
            return null;
        }
        svgImage.setClassNameBaseVal(runnerResources.runner().partIcon());
        switch (currentRunnerStatus) {
            case IN_QUEUE:
                svgImage.addClassNameBaseVal(runnerResources.runner().inQueue());
                break;
            case IN_PROGRESS:
                svgImage.addClassNameBaseVal(runnerResources.runner().inProgress());
                break;
            case RUNNING:
                svgImage.addClassNameBaseVal(runnerResources.runner().running());
                break;
            case DONE:
                svgImage.addClassNameBaseVal(runnerResources.runner().done());
                break;
            case FAILED:
                svgImage.addClassNameBaseVal(runnerResources.runner().failed());
                break;
            case TIMEOUT:
                svgImage.addClassNameBaseVal(runnerResources.runner().timeout());
                break;
            case IDLE:
            default:
                break;
        }
        return svgImage;
    }

    /** {@inheritDoc} */
    @Override
    public String getTitleToolTip() {
        return "Displays Runner output";
    }

    @Override
    public IsWidget getTitleWidget() {
        return super.getTitleWidget();
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        consoleToolbar.go(view.getToolbarPanel());
        container.setWidget(view);
    }

    public void setCurrentRunnerStatus(RunnerStatus currentRunnerStatus) {
        this.currentRunnerStatus = currentRunnerStatus;
        firePropertyChange(TITLE_PROPERTY);
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
        }
        view.print(message);
        view.scrollBottom();
        firePropertyChange(TITLE_PROPERTY);
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
        appURL = RunnerUtils.getLink(processDescriptor, Constants.LINK_REL_WEB_URL) != null ? RunnerUtils.getLink(processDescriptor, Constants.LINK_REL_WEB_URL).getHref() : null;
        if (appURL != null && activeTab == Tab.APP)
            view.reloadAppPreviewFrame(appURL);
    }


    /** Should be called when current app is stopped. */
    public void onShellStarted(ApplicationProcessDescriptor processDescriptor) {
        shellURL = RunnerUtils.getLink(processDescriptor, Constants.LINK_REL_SHELL_URL) != null ? RunnerUtils.getLink(processDescriptor, Constants.LINK_REL_SHELL_URL).getHref() : null;
        if (shellURL != null && activeTab == Tab.TERMINAL)
            view.reloadTerminalFrame(shellURL);
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
        activeTab = Tab.TERMINAL;
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
        activeTab = Tab.APP;
        if (appURL != null && !isAppPreviewFrameAlreadyLoaded) {
            view.reloadAppPreviewFrame(appURL);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onAppPreviewLoaded() {
        isAppPreviewFrameAlreadyLoaded = true;
    }


    @Override
    public void onConsoleTabOpened() {
        activeTab = Tab.CONSOLE;
    }
}