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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
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
    SimplePanel toolbarPanel;
    @UiField
    ScrollPanel scrollPanel;
    @UiField
    FlowPanel   consoleArea;

    @Inject
    public RunnerConsoleViewImpl(PartStackUIResources resources, RunnerConsoleViewImplUiBinder uiBinder) {
        super(resources);
        container.add(uiBinder.createAndBindUi(this));
        minimizeButton.ensureDebugId("runner-console-minimizeButton");

        // this hack used for adding box shadow effect to toolbar
        toolbarPanel.getElement().getParentElement().getStyle().setOverflow(Overflow.VISIBLE);
        toolbarPanel.getElement().getParentElement().getStyle().setZIndex(1);
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

    /** {@inheritDoc} */
    @Override
    public void clear() {
        consoleArea.clear();
    }

    /** {@inheritDoc} */
    @Override
    public void scrollBottom() {
        scrollPanel.getElement().setScrollTop(scrollPanel.getElement().getScrollHeight());
    }
}
