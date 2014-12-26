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
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
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
    private static final String INFO  = "INFO";
    private static final String ERROR = "ERROR";
    private static final String WARN  = "WARNING";
    private static final String MAVEN = "MAVEN";

    private static final String PRE_STYLE   = "style='margin:0px;'";

    private static final String INFO_COLOR  = "lightgreen";
    private static final String WARN_COLOR  = "#FFBA00";
    private static final String ERROR_COLOR = "#F62217";
    private static final String MAVEN_COLOR = "#61b7ef";

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
        if (message.startsWith("["+INFO+"]")) {
            html.setHTML(buildSafeHtmlMessage(INFO, INFO_COLOR, message));
        } else if (message.startsWith("["+ERROR+"]")) {
            html.setHTML(buildSafeHtmlMessage(ERROR, ERROR_COLOR, message));
        } else if (message.startsWith("["+WARN+"]")) {
            html.setHTML(buildSafeHtmlMessage(WARN, WARN_COLOR, message));
        } else if (message.startsWith("["+MAVEN+"]")) {
            html.setHTML(buildHtmlMessage(MAVEN, MAVEN_COLOR, message));
        } else {
            html.setHTML(buildSafeHtmlMessage(message));
        }
        html.getElement().getStyle().setPaddingLeft(2, Style.Unit.PX);
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

    /**
     * Return sanitized message (with all restricted HTML-tags escaped) in SafeHtml.
     * @param type message type (info, error etc.)
     * @param color color constant
     * @param message message to print
     * @return message in SafeHtml
     */
    private SafeHtml buildSafeHtmlMessage(String type, String color, String message) {
        return new SafeHtmlBuilder()
                .appendHtmlConstant("<pre " + PRE_STYLE + ">")
                .appendHtmlConstant("[<span style='color:" + color + ";'><b>" + type + "</b></span>]")
                .append(SimpleHtmlSanitizer.sanitizeHtml(message.substring(("[" + type + "]").length())))
                .appendHtmlConstant("</pre>")
                .toSafeHtml();
    }

    /**
     * Returns partially sanitized message. The method allows to place html inside the message.
     *
     * @param type message type (info, error etc.)
     * @param color color constant
     * @param message message to print
     * @return message in SafeHtml
     */
    private SafeHtml buildHtmlMessage(String type, String color, String message) {
        return new SafeHtmlBuilder()
                .appendHtmlConstant("<pre " + PRE_STYLE + ">")
                .appendHtmlConstant("[<span style='color:" + color + ";'><b>" + type + "</b></span>]")
                .appendHtmlConstant (message.substring(("[" + type + "]").length()))
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
