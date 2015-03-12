/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.extension.builder.client.console;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.builder.dto.BuildTaskDescriptor;
import org.eclipse.che.api.core.rest.shared.dto.Link;
import org.eclipse.che.ide.api.build.BuildContext;
import org.eclipse.che.ide.api.parts.PartStackUIResources;
import org.eclipse.che.ide.api.parts.base.BaseView;
import org.eclipse.che.ide.extension.builder.client.BuilderLocalizationConstant;

/**
 * Implements {@link BuilderConsoleView}.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class BuilderConsoleViewImpl extends BaseView<BuilderConsoleView.ActionDelegate> implements BuilderConsoleView {

    private static final int MAX_LINE_COUNT = 1000;

    private static final String INFO  = "INFO";
    private static final String ERROR = "ERROR";
    private static final String WARN  = "WARNING";
    private static final String MAVEN = "MAVEN";

    private static final String PRE_STYLE = "style='margin:0px;'";

    private static final String INFO_COLOR  = "lightgreen";
    private static final String WARN_COLOR  = "#FFBA00";
    private static final String ERROR_COLOR = "#F62217";
    private static final String MAVEN_COLOR = "#61b7ef";

    private final BuildContext                buildContext;
    private final BuilderLocalizationConstant localizationConstant;
    @UiField
    SimplePanel toolbarPanel;
    @UiField
    ScrollPanel scrollPanel;
    @UiField
    FlowPanel   consoleArea;

    @Inject
    public BuilderConsoleViewImpl(PartStackUIResources resources,
                                  BuilderConsoleViewImplUiBinder uiBinder,
                                  BuildContext buildContext,
                                  BuilderLocalizationConstant localizationConstant) {
        super(resources);
        this.buildContext = buildContext;
        this.localizationConstant = localizationConstant;

        setContentWidget(uiBinder.createAndBindUi(this));

        minimizeButton.ensureDebugId("builder-console-minimizeButton");

        // this hack used for adding box shadow effect to toolbar
        toolbarPanel.getElement().getParentElement().getStyle().setOverflow(Overflow.VISIBLE);
        toolbarPanel.getElement().getParentElement().getStyle().setZIndex(1);

        scrollPanel.getElement().setTabIndex(0);
    }

    /** {@inheritDoc} */
    @Override
    public AcceptsOneWidget getToolbarPanel() {
        return toolbarPanel;
    }

    /** {@inheritDoc} */
    @Override
    public void print(String message) {
        cleanOverHeadLines();

        HTML html = new HTML();
        if (message.startsWith("[" + INFO + "]")) {
            html.setHTML(buildSafeHtmlMessage(INFO, INFO_COLOR, message));
        } else if (message.startsWith("[" + ERROR + "]")) {
            html.setHTML(buildSafeHtmlMessage(ERROR, ERROR_COLOR, message));
        } else if (message.startsWith("[" + WARN + "]")) {
            html.setHTML(buildSafeHtmlMessage(WARN, WARN_COLOR, message));
        } else if (message.startsWith("[" + MAVEN + "]")) {
            html.setHTML(buildHtmlMessage(MAVEN, MAVEN_COLOR, message));
        } else {
            html.setHTML(buildSafeHtmlMessage(message));
        }
        html.getElement().getStyle().setPaddingLeft(2, Style.Unit.PX);

        consoleArea.add(html);
    }

    private void cleanOverHeadLines() {
        if (consoleArea.getWidgetCount() < MAX_LINE_COUNT) {
            return;
        }

        // remove first 10% of current lines on screen
        for (int i = 0; i < MAX_LINE_COUNT * 0.1; i++) {
            consoleArea.remove(0);
        }

        final BuildTaskDescriptor buildTaskDescriptor = buildContext.getBuildTaskDescriptor();
        if (buildTaskDescriptor == null) {
            return;
        }

        final Link logLink = buildTaskDescriptor.getLink("view build log");
        if (logLink == null) {
            return;
        }

        final String logUrl = logLink.getHref();
        if (logUrl == null) {
            return;
        }

        // print link to full logs in top of console
        consoleArea.insert(getFullLogsWidget(logUrl), 0);
    }

    private Widget getFullLogsWidget(String logUrl) {
        HTML html = new HTML();
        html.getElement().getStyle().setProperty("fontFamily", "\"Droid Sans Mono\", monospace");
        html.getElement().getStyle().setProperty("fontSize", "11px");
        html.getElement().getStyle().setProperty("paddingLeft", "2px");

        Element text = DOM.createSpan();
        text.setInnerHTML(localizationConstant.fullBuildLogConsoleLink());
        html.getElement().appendChild(text);

        Anchor link = new Anchor();
        link.setHref(logUrl);
        link.setText(logUrl);
        link.setTitle(logUrl);
        link.setTarget("_blank");
        link.getElement().getStyle().setProperty("color", "#61b7ef");
        html.getElement().appendChild(link.getElement());

        return html;
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
     *
     * @param type
     *         message type (info, error etc.)
     * @param color
     *         color constant
     * @param message
     *         message to print
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
     * @param type
     *         message type (info, error etc.)
     * @param color
     *         color constant
     * @param message
     *         message to print
     * @return message in SafeHtml
     */
    private SafeHtml buildHtmlMessage(String type, String color, String message) {
        return new SafeHtmlBuilder()
                .appendHtmlConstant("<pre " + PRE_STYLE + ">")
                .appendHtmlConstant("[<span style='color:" + color + ";'><b>" + type + "</b></span>]")
                .appendHtmlConstant(message.substring(("[" + type + "]").length()))
                .appendHtmlConstant("</pre>")
                .toSafeHtml();
    }

    /**
     * Return sanitized message (with all restricted HTML-tags escaped) in SafeHtml
     *
     * @param message
     *         message to print
     * @return message in SafeHtml
     */
    private SafeHtml buildSafeHtmlMessage(String message) {
        return new SafeHtmlBuilder()
                .appendHtmlConstant("<pre " + PRE_STYLE + ">")
                .append(SimpleHtmlSanitizer.sanitizeHtml(message))
                .appendHtmlConstant("</pre>")
                .toSafeHtml();
    }

    @Override
    protected void focusView() {
        scrollPanel.getElement().focus();
    }

    interface BuilderConsoleViewImplUiBinder extends UiBinder<Widget, BuilderConsoleViewImpl> {
    }

}
