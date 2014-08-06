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

import com.codenvy.ide.api.parts.PartStackUIResources;
import com.codenvy.ide.api.parts.base.BaseView;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.DeckPanel;
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
 */
@Singleton
public class RunnerConsoleViewImpl extends BaseView<RunnerConsoleView.ActionDelegate> implements RunnerConsoleView {
    private static final String INFO         = "[INFO]";
    private static final String DOCKER       = "[DOCKER]";
    private static final String DOCKER_ERROR = "[DOCKER] [ERROR]";

    private static final String PRE_STYLE          = "style='margin:0px; font-weight:700;'";
    private static final String INFO_COLOR         = "lightgreen'";
    private static final String DOCKER_COLOR       = "#00B7EC'";
    private static final String DOCKER_ERROR_COLOR = "#F62217'";

    interface RunnerConsoleViewImplUiBinder extends UiBinder<Widget, RunnerConsoleViewImpl> {
    }

    @UiField
    FlowPanel   topPanel;
    @UiField
    DeckPanel   tabPanel;
    @UiField
    SimplePanel toolbarPanel;

    @UiField
    Label consoleButton;
    @UiField
    Label terminalButton;
    @UiField
    Label appPreviewButton;

    @UiField
    ScrollPanel scrollPanel;
    @UiField
    FlowPanel   consoleArea;

    @UiField
    DeckPanel terminalPanel;
    @UiField
    Frame     terminalFrame;

    @UiField
    DeckPanel appPreviewPanel;
    @UiField
    Frame     appPreviewFrame;

    private Label activeTabButton;

    @Inject
    public RunnerConsoleViewImpl(PartStackUIResources resources, RunnerConsoleViewImplUiBinder uiBinder) {
        super(resources);
        container.add(uiBinder.createAndBindUi(this));
        minimizeButton.ensureDebugId("runner-console-minimizeButton");

        terminalFrame.removeStyleName("gwt-Frame");

        // this hack used for adding box shadow effect to top panel (tabs+toolbar)
        topPanel.getElement().getParentElement().getStyle().setOverflow(Overflow.VISIBLE);
        topPanel.getElement().getParentElement().getStyle().setZIndex(1);

        setActiveTab(0); // show Console panel
        hideTerminal();
        hideAppPreview();

        terminalFrame.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                delegate.onTerminalLoaded();
            }
        });
        appPreviewFrame.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                delegate.onAppPreviewLoaded();
            }
        });

        consoleButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setActiveTab(0);
            }
        });
        terminalButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setActiveTab(1);
                delegate.onTerminalTabOpened();
            }
        });
        appPreviewButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setActiveTab(2);
                delegate.onAppTabOpened();
            }
        });
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

    private void setActiveTab(int index) {
        if (activeTabButton != null) {
            activeTabButton.getElement().getStyle().clearBackgroundColor();
            activeTabButton.getElement().getStyle().clearColor();
        }
        if (index == 0) {
            activeTabButton = consoleButton;
        } else if (index == 1) {
            activeTabButton = terminalButton;
        } else if (index == 2) {
            activeTabButton = appPreviewButton;
        }
        activeTabButton.getElement().getStyle().setBackgroundColor("#343434");
        activeTabButton.getElement().getStyle().setColor("#00B7EC");

        tabPanel.showWidget(index);
    }

    /** {@inheritDoc} */
    @Override
    public void reloadTerminalFrame(String url) {
        terminalPanel.showWidget(1);
        terminalFrame.setUrl(url);
    }

    /** {@inheritDoc} */
    @Override
    public void reloadAppPreviewFrame(String url) {
        appPreviewPanel.showWidget(1);
        appPreviewFrame.setUrl(url);
    }

    /** {@inheritDoc} */
    @Override
    public void hideTerminal() {
        terminalPanel.showWidget(0);
    }

    /** {@inheritDoc} */
    @Override
    public void hideAppPreview() {
        appPreviewPanel.showWidget(0);
    }

    /** {@inheritDoc} */
    @Override
    public void clearConsole() {
        consoleArea.clear();
    }

    /** {@inheritDoc} */
    @Override
    public void scrollBottom() {
        scrollPanel.getElement().setScrollTop(scrollPanel.getElement().getScrollHeight());
    }

    /** {@inheritDoc} */
    @Override
    public void print(String message) {
        HTML html = new HTML();
        if (message.startsWith(INFO)) {
            html.setHTML("<pre " + PRE_STYLE + ">[<span style='color:" + INFO_COLOR + ";'><b>INFO</b></span>]" +
                         message.substring(INFO.length()) + "</pre>");
        } else if (message.startsWith(DOCKER_ERROR)) {
            html.setHTML("<pre " + PRE_STYLE + ">[<span style='color:" + DOCKER_COLOR + ";'><b>DOCKER</b></span>]" +
                         " [<span style='color:" + DOCKER_ERROR_COLOR + ";'><b>ERROR</b></span>]" +
                         message.substring(DOCKER_ERROR.length()) + "</pre>");
        } else if (message.startsWith(DOCKER)) {
            html.setHTML("<pre " + PRE_STYLE + ">[<span style='color:" + DOCKER_COLOR + ";'><b>DOCKER</b></span>]" +
                         message.substring(DOCKER.length()) + "</pre>");
        } else {
            html.setHTML("<pre " + PRE_STYLE + ">" + message + "</pre>");
        }
        html.getElement().setAttribute("style", "padding-left: 2px;");
        consoleArea.add(html);
    }
}
