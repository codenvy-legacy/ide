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
package com.codenvy.ide.extension.builder.client.console;

import com.codenvy.ide.api.parts.PartStackUIResources;
import com.codenvy.ide.api.parts.base.BaseView;
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
 * Implements {@link BuilderConsoleView}.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class BuilderConsoleViewImpl extends BaseView<BuilderConsoleView.ActionDelegate> implements BuilderConsoleView {
    private final String PRE_STYLE   = "style='margin:0px; font-weight:700;'";
    private final String INFO_COLOR  = "lightgreen'";
    private final String WARN_COLOR  = "cyan'";
    private final String ERROR_COLOR = "#F62217'";

    interface BuilderConsoleViewImplUiBinder extends UiBinder<Widget, BuilderConsoleViewImpl> {
    }

    @UiField
    SimplePanel toolbarPanel;
    @UiField
    ScrollPanel scrollPanel;
    @UiField
    FlowPanel   consoleArea;

    @Inject
    public BuilderConsoleViewImpl(PartStackUIResources resources, BuilderConsoleViewImplUiBinder uiBinder) {
        super(resources);
        container.add(uiBinder.createAndBindUi(this));
        minimizeButton.ensureDebugId("builder-console-minimizeButton");
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
        final String capMessage = message.toUpperCase();
        if (capMessage.startsWith("[INFO]")) {
            html.setHTML("<pre " + PRE_STYLE + ">[<span style='color:" + INFO_COLOR + ";'><b>INFO</b></span>] " + message.substring(6) +
                         "</pre>");
        } else if (capMessage.startsWith("[ERROR]")) {
            html.setHTML("<pre " + PRE_STYLE + ">[<span style='color:" + ERROR_COLOR + ";'><b>ERROR</b></span>] " + message.substring(7) +
                         "</pre>");
        } else if (capMessage.startsWith("[WARNING]")) {
            html.setHTML("<pre " + PRE_STYLE + ">[<span style='color:" + WARN_COLOR + ";'><b>WARNING</b></span>] " + message.substring(9) +
                         "</pre>");
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
