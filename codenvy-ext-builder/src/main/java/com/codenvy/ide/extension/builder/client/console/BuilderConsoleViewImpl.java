/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.extension.builder.client.console;

import com.codenvy.ide.api.parts.PartStackUIResources;
import com.codenvy.ide.api.parts.base.BaseView;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
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
    interface BuilderConsoleViewImplUiBinder extends UiBinder<Widget, BuilderConsoleViewImpl> {
    }

    @UiField
    FlowPanel   consoleArea;
    @UiField
    ScrollPanel scrollPanel;

    @Inject
    public BuilderConsoleViewImpl(PartStackUIResources resources, BuilderConsoleViewImplUiBinder uiBinder) {
        super(resources);
        container.add(uiBinder.createAndBindUi(this));
        minimizeButton.ensureDebugId("builder-console-minimizeBut");
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void print(String text) {
        final String preStyle = " style='margin:0px; font-weight:700; font-size: 12;' ";

        HTML html = new HTML();

        final String TEXT = text.toUpperCase();
        if (TEXT.startsWith("[INFO]")) {
            html.setHTML("<pre" + preStyle + ">[<span style='color:lightgreen;'><b>INFO</b></span>] " + text.substring(6) + "</pre>");
        } else if (TEXT.startsWith("[ERROR]")) {
            html.setHTML("<pre" + preStyle + ">[<span style='color:#F62217;'><b>ERROR</b></span>] " + text.substring(7) + "</pre>");
        } else if (TEXT.startsWith("[WARNING]")) {
            html.setHTML("<pre" + preStyle + ">[<span style='color:cyan;'><b>WARNING</b></span>] " + text.substring(9) + "</pre>");
        } else {
            html.setHTML("<pre" + preStyle + ">" + text + "</pre>");
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
