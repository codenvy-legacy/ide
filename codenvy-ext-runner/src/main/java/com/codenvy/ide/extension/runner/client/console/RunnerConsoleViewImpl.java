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

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.runner.gwt.client.utils.RunnerUtils;
import com.codenvy.api.runner.internal.Constants;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.parts.PartStackUIResources;
import com.codenvy.ide.api.parts.base.BaseView;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Anchor;
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
    private static final String              PRE_STYLE         = "style='margin:0px;'";

    private static final String              INFO              = "[INFO]";
    private static final String              INFO_COLOR        = "lightgreen";

    private static final String              WARN              = "[WARNING]";
    private static final String              WARN_COLOR        = "#FFBA00";

    private static final String              ERROR             = "[ERROR]";
    private static final String              ERROR_COLOR       = "#F62217";

    private static final String              DOCKER            = "[DOCKER]";
    private static final String              DOCKER_COLOR      = "#00B7EC";

    private static final String              STDOUT            = "[STDOUT]";
    private static final String              STDOUT_COLOR      = "lightgreen";

    private static final String              STDERR            = "[STDERR]";
    private static final String              STDERR_COLOR      = "#F62217";

    private static final int                 MAX_CONSOLE_LINES = 1000;

    private final AppContext                 appContext;
    private final RunnerLocalizationConstant localizationConstant;
    private RunnerResources                  runnerResources;

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
                                 RunnerConsoleViewImplUiBinder uiBinder, AppContext appContext, RunnerLocalizationConstant localizationConstant) {
        super(resources);

        this.runnerResources = runnerResources;
        this.appContext = appContext;
        this.localizationConstant = localizationConstant;

        container.add(uiBinder.createAndBindUi(this));

        minimizeButton.ensureDebugId("runner-console-minimizeButton");

        terminalFrame.removeStyleName("gwt-Frame");
        terminalFrame.getElement().setAttribute("allowtransparency", "true");
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
        if (consoleOutput.getWidgetCount() >= MAX_CONSOLE_LINES) {
            Log.info(RunnerConsoleViewImpl.class, "MAX_CONSOLE_LINES ("+MAX_CONSOLE_LINES+") limit reached !");
            // remove first 10% of current lines on screen
            for (int i = 0; i < MAX_CONSOLE_LINES * 0.1; i++) {
                consoleOutput.remove(0);
            }
            // print link to full logs in top of console
            CurrentProject currentProject = appContext.getCurrentProject();
            if (currentProject != null && currentProject.getProcessDescriptor() != null) {
                final Link viewLogsLink = RunnerUtils.getLink(appContext.getCurrentProject().getProcessDescriptor(),
                                                              Constants.LINK_REL_VIEW_LOG);
                if (viewLogsLink != null) {
                    String href = viewLogsLink.getHref();
                    HTML html = new HTML();
                    html.getElement().getStyle().setProperty("fontFamily", "\"Droid Sans Mono\", monospace");
                    html.getElement().getStyle().setProperty("fontSize", "11px");
                    html.getElement().getStyle().setProperty("paddingLeft", "2px");

                    Element text = DOM.createSpan();
                    text.setInnerHTML(localizationConstant.fullLogTraceConsoleLink());
                    html.getElement().appendChild(text);

                    Anchor link = new Anchor();
                    link.setHref(href);
                    link.setText(href);
                    link.setTitle(href);
                    link.getElement().getStyle().setProperty("color", "#61b7ef");
                    html.getElement().appendChild(link.getElement());

                    consoleOutput.insert(html, 0);
                }
            }
        }
        Widget html = messageToHTML(message);
        consoleOutput.add(html);
    }

    private Widget messageToHTML(String message) {
        HTML html = new HTML();
        if (message.startsWith(INFO)) {
            html.setHTML("<pre " + PRE_STYLE + ">[<span style='color:" + INFO_COLOR + ";'><b>INFO</b></span>]" +
                         message.substring(INFO.length()) + "</pre>");
        } else if (message.startsWith(ERROR)) {
            html.setHTML(buildSafeHtmlMessage(ERROR, ERROR_COLOR, message));
        } else if (message.startsWith(WARN)) {
            html.setHTML(buildSafeHtmlMessage(WARN, WARN_COLOR, message));
        } else if (message.startsWith(DOCKER + " " + ERROR)) {
            html.setHTML(buildSafeHtmlMessage(DOCKER, DOCKER_COLOR, ERROR , ERROR_COLOR, message));
        } else if (message.startsWith(DOCKER)) {
            html.setHTML(buildSafeHtmlMessage(DOCKER, DOCKER_COLOR, message));
        } else if (message.startsWith(STDOUT)) {
            html.setHTML(buildSafeHtmlMessage(STDOUT, STDOUT_COLOR, message));
        } else if (message.startsWith(STDERR)) {
            html.setHTML(buildSafeHtmlMessage(STDERR, STDERR_COLOR, message));
        } else {
            html.setHTML(buildSafeHtmlMessage(message));
        }
        html.getElement().setAttribute("style", "padding-left: 2px;");
        return html;
    }

    /**
     * Return sanitized message (with all restricted HTML-tags escaped) in SafeHtml.
     * @param type message type (e.g. INFO, ERROR etc.)
     * @param color color constant
     * @param message message to print
     * @return message in SafeHtml
     */
    private SafeHtml buildSafeHtmlMessage(String type, String color, String message) {
        return new SafeHtmlBuilder()
                .appendHtmlConstant("<pre " + PRE_STYLE + ">")
                .appendHtmlConstant("[<span style='color:" + color + ";'>")
                .appendHtmlConstant("<b>" + type.replaceAll("[\\[\\]]", "") + "</b></span>]")
                .append(SimpleHtmlSanitizer.sanitizeHtml(message.substring((type).length())))
                .appendHtmlConstant("</pre>")
                .toSafeHtml();
    }

    /**
     * Return sanitized message (with all restricted HTML-tags escaped) in SafeHtml. Use for two-words message types,
     * e.g. [DOCKER] [ERROR].
     * @param type message type (e.g. DOCKER)
     * @param color color constant
     * @param subtype message subtype (e.g. ERROR)
     * @param subcolor color constant
     * @param message message to print
     * @return message in SafeHtml
     */
    private SafeHtml buildSafeHtmlMessage(String type,
                                          String color,
                                          String subtype,
                                          String subcolor,
                                          String message) {
        return new SafeHtmlBuilder()
                .appendHtmlConstant("<pre " + PRE_STYLE + ">")
                .appendHtmlConstant("[<span style='color:" + color + ";'>")
                .appendHtmlConstant("<b>" + type.replaceAll("[\\[\\]]", "") + "</b></span>]")
                .appendHtmlConstant(" [<span style='color:" + subcolor + ";'>")
                .appendHtmlConstant("<b>" + subtype.replaceAll("[\\[\\]]", "") + "</b></span>]")
                .append(SimpleHtmlSanitizer.sanitizeHtml(message.substring((type + " " + subtype).length())))
                .appendHtmlConstant("</pre>")
                .toSafeHtml();
    }

    /**
     * Return sanitized message (with all restricted HTML-tags escaped) in SafeHtml
     * @param message message to print
     * @return message in SafeHtml
     */
    private SafeHtml buildSafeHtmlMessage(String message) {
        return new SafeHtmlBuilder()
                .appendHtmlConstant("<pre " + PRE_STYLE + ">")
                .append(SimpleHtmlSanitizer.sanitizeHtml(message))
                .appendHtmlConstant("</pre>")
                .toSafeHtml();
    }
}
