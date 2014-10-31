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

import com.codenvy.api.project.server.ProjectTypeResolver;
import com.codenvy.ide.api.parts.PartStackUIResources;
import com.codenvy.ide.api.parts.base.BaseView;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Implements {@link RunnerConsoleView}.
 *
 * @author Artem Zatsarynnyy
 * @author Vitaliy Guliy
 */
@Singleton
public class RunnerConsoleViewImpl extends BaseView<RunnerConsoleView.ActionDelegate> implements RunnerConsoleView {

    private static final String PRE_STYLE           = "style='margin:0px; font-weight:700;'";

    private static final String INFO                = "[INFO]";
    private static final String INFO_COLOR          = "lightgreen";

    private static final String WARN                = "[WARNING]";
    private static final String WARN_COLOR          = "#FFBA00";

    private static final String ERROR               = "[ERROR]";
    private static final String ERROR_COLOR         = "#F62217";

    private static final String DOCKER              = "[DOCKER]";
    private static final String DOCKER_COLOR        = "#00B7EC";

    private static final String DOCKER_ERROR        = "[DOCKER] [ERROR]";
    private static final String DOCKER_ERROR_COLOR  = "#F62217";

    private static final String STDOUT              = "[STDOUT]";
    private static final String STDOUT_COLOR        = "lightgreen";

    private static final String STDERR              = "[STDERR]";
    private static final String STDERR_COLOR        = "#F62217";

    private RunnerResources runnerResources;

    @UiField
    DockLayoutPanel topPanel;
    @UiField
    DeckPanel       tabPanel;
    @UiField
    FlowPanel       tabsPanel;
    @UiField
    SimplePanel     toolbarPanel;

    /**
     * Tab Console
     */
    @UiField
    SimplePanel consoleButton;
    @UiField
    ScrollPanel consolePanel;
    @UiField
    FlowPanel   consoleOutput;

    /**
     * Tab Terminal
     */
    @UiField
    SimplePanel terminalButton;
    @UiField
    FlowPanel   terminalPanel;
    @UiField
    Label       terminalUnavailableLabel;
    @UiField
    Frame       terminalFrame;

    /**
     * Tab App
     */
    /* @UiField
    SimplePanel appPreviewButton;
    @UiField
    FlowPanel appPreviewPanel;
    @UiField
    Label     appPreviewUnavailablePanel;
    @UiField
    Frame     appPreviewFrame; */

    /**
     * Terminal URL
     */
    private String terminalURL;

    /**
     * Preview application URL
     */
    /* private String appURL; */


    interface RunnerConsoleViewImplUiBinder extends UiBinder<Widget, RunnerConsoleViewImpl> {
    }

    @Inject
    public RunnerConsoleViewImpl(PartStackUIResources resources, RunnerResources runnerResources,
                                 RunnerConsoleViewImplUiBinder uiBinder) {
        super(resources);

        this.runnerResources = runnerResources;

        container.add(uiBinder.createAndBindUi(this));

        minimizeButton.ensureDebugId("runner-console-minimizeButton");

        terminalFrame.removeStyleName("gwt-Frame");
        /* appPreviewFrame.removeStyleName("gwt-Frame"); */

        terminalUnavailableLabel.setVisible(true);
        terminalFrame.setVisible(false);
        /* appPreviewUnavailablePanel.setVisible(true);
        appPreviewFrame.setVisible(false); */

        // this hack used for adding box shadow effect to top panel (tabs+toolbar)
        topPanel.getElement().getParentElement().getStyle().setOverflow(Overflow.VISIBLE);
        topPanel.getElement().getParentElement().getStyle().setZIndex(1);
        tabsPanel.getElement().getParentElement().getStyle().setOverflow(Overflow.VISIBLE);
        toolbarPanel.getElement().getParentElement().getStyle().setOverflow(Overflow.VISIBLE);

        activateConsole();

        consoleButton.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                activateConsole();
            }
        }, ClickEvent.getType());

        terminalButton.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                activateTerminal();
            }
        }, ClickEvent.getType());

        /* appPreviewButton.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                activateApp();
            }
        }, ClickEvent.getType()); */
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public AcceptsOneWidget getToolbarPanel() {
        return toolbarPanel;
    }

    @Override
    public void setTerminalURL(String terminalURL) {
        this.terminalURL = terminalURL;

        if (terminalURL == null) {
            terminalUnavailableLabel.setVisible(true);
            terminalFrame.setVisible(false);
            terminalFrame.getElement().removeAttribute("src");

        } else if (terminalPanel.isVisible()) {
            terminalUnavailableLabel.setVisible(false);
            terminalFrame.setUrl(terminalURL);
            terminalFrame.setVisible(true);
        }
    }

    /* @Override
    public void setAppURL(String appURL) {
        this.appURL = appURL;

        if (appURL == null) {
            appPreviewUnavailablePanel.setVisible(true);
            appPreviewFrame.setVisible(false);
            appPreviewFrame.getElement().removeAttribute("src");
        } else if (appPreviewPanel.isVisible()) {
            appPreviewUnavailablePanel.setVisible(false);
            appPreviewFrame.setUrl(appURL);
            appPreviewFrame.setVisible(true);
        }
    } */

    @Override
    public void activateConsole() {
        if (tabPanel.getVisibleWidget() == 0) {
            return;
        }

        tabPanel.showWidget(0);

        consoleButton.addStyleName(runnerResources.runner().tabSelected());
        terminalButton.removeStyleName(runnerResources.runner().tabSelected());
        /* appPreviewButton.removeStyleName(runnerResources.runner().tabSelected()); */

        scrollBottom();
    }

    @Override
    public void activateTerminal() {
        if (tabPanel.getVisibleWidget() == 1) {
            return;
        }

        tabPanel.showWidget(1);

        consoleButton.removeStyleName(runnerResources.runner().tabSelected());
        terminalButton.addStyleName(runnerResources.runner().tabSelected());
        /* appPreviewButton.removeStyleName(runnerResources.runner().tabSelected()); */

        if (terminalURL == null && !terminalFrame.getUrl().isEmpty()) {
            terminalUnavailableLabel.setVisible(true);
            terminalFrame.getElement().removeAttribute("src");
            terminalFrame.setVisible(false);

        } else if (terminalURL != null && terminalFrame.getUrl().isEmpty()) {
            terminalUnavailableLabel.setVisible(false);
            terminalFrame.setUrl(terminalURL);
            terminalFrame.setVisible(true);
        }
    }

    /* @Override
    public void activateApp() {
        if (tabPanel.getVisibleWidget() == 2) {
            return;
        }

        tabPanel.showWidget(2);

        consoleButton.removeStyleName(runnerResources.runner().tabSelected());
        terminalButton.removeStyleName(runnerResources.runner().tabSelected());
        appPreviewButton.addStyleName(runnerResources.runner().tabSelected());

        if (appURL == null && !appPreviewFrame.getUrl().isEmpty()) {
            appPreviewUnavailablePanel.setVisible(true);
            appPreviewFrame.getElement().removeAttribute("src");
            appPreviewFrame.setVisible(false);
        } else if (appURL != null && appPreviewFrame.getUrl().isEmpty()) {
            appPreviewUnavailablePanel.setVisible(false);
            appPreviewFrame.setUrl(appURL);
            appPreviewFrame.setVisible(true);
        }
    } */

    /** {@inheritDoc} */
    @Override
    public void clearConsole() {
        consoleOutput.clear();
    }

    /** {@inheritDoc} */
    @Override
    public void scrollBottom() {
        consolePanel.getElement().setScrollTop(consolePanel.getElement().getScrollHeight());
    }

    public void print(String message) {
        HTML html = new HTML();

        if (message.startsWith(INFO)) {
            html.setHTML("<pre " + PRE_STYLE + ">[<span style='color:" + INFO_COLOR + ";'><b>INFO</b></span>]" +
                        SafeHtmlUtils.fromString(message.substring(INFO.length())).asString() + "</pre>");
        } else if (message.startsWith(ERROR)) {
            html.setHTML("<pre " + PRE_STYLE + ">[<span style='color:" + ERROR_COLOR + ";'><b>ERROR</b></span>]" +
                         SafeHtmlUtils.fromString(message.substring(ERROR.length())).asString() + "</pre>");
        } else if (message.startsWith(WARN)) {
            html.setHTML("<pre " + PRE_STYLE + ">[<span style='color:" + WARN_COLOR + ";'><b>WARNING</b></span>]" +
                         SafeHtmlUtils.fromString(message.substring(WARN.length())).asString() + "</pre>");
        } else if (message.startsWith(DOCKER_ERROR)) {
            html.setHTML("<pre " + PRE_STYLE + ">[<span style='color:" + DOCKER_COLOR + ";'><b>DOCKER</b></span>]" +
                         " [<span style='color:" + DOCKER_ERROR_COLOR + ";'><b>ERROR</b></span>]" +
                         SafeHtmlUtils.fromString(message.substring(DOCKER_ERROR.length())).asString() + "</pre>");
        } else if (message.startsWith(DOCKER)) {
            html.setHTML("<pre " + PRE_STYLE + ">[<span style='color:" + DOCKER_COLOR + ";'><b>DOCKER</b></span>]" +
                         SafeHtmlUtils.fromString(message.substring(DOCKER.length())).asString() + "</pre>");
        } else if (message.startsWith(STDERR)) {
            html.setHTML("<pre " + PRE_STYLE + ">[<span style='color:" + STDERR_COLOR + ";'><b>STDERR</b></span>]" +
                         SafeHtmlUtils.fromString(message.substring(STDERR.length())).asString() + "</pre>");
        } else {
            html.setHTML("<pre " + PRE_STYLE + ">" + SafeHtmlUtils.fromString(message).asString() + "</pre>");
        }
        html.getElement().setAttribute("style", "padding-left: 2px;");

        consoleOutput.add(html);
    }
}
