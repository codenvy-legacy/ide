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

import org.eclipse.che.ide.api.parts.PartStackUIResources;
import org.eclipse.che.ide.api.parts.base.BaseView;
import org.eclipse.che.ide.util.MessageBuilder;
import org.eclipse.che.ide.util.MessageType;
import com.google.gwt.dom.client.Style;
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
 * Implements {@link BuilderConsoleView}.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class BuilderConsoleViewImpl extends BaseView<BuilderConsoleView.ActionDelegate> implements BuilderConsoleView {
    private static final String INFO   = "INFO";
    private static final String ERROR  = "ERROR";
    private static final String WARN   = "WARNING";
    private static final String MAVEN  = "MAVEN";
    private static final String GRADLE = "GRADLE";

    interface BuilderConsoleViewImplUiBinder extends UiBinder<Widget, BuilderConsoleViewImpl> {
    }

    @UiField
    SimplePanel toolbarPanel;
    @UiField
    ScrollPanel scrollPanel;
    @UiField
    FlowPanel   consoleArea;

    @Inject
    public BuilderConsoleViewImpl(PartStackUIResources resources,
                                  BuilderConsoleViewImplUiBinder uiBinder) {
        super(resources);
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
        HTML html = new HTML();
        MessageBuilder messageBuilder = new MessageBuilder().message(message);

        if (message.startsWith("[" + INFO + "]")) {
            html.setHTML(messageBuilder.type(MessageType.INFO).sanitizing(false).build());
        } else if (message.startsWith("[" + ERROR + "]")) {
            html.setHTML(messageBuilder.type(MessageType.ERROR).build());
        } else if (message.startsWith("[" + WARN + "]")) {
            html.setHTML(messageBuilder.type(MessageType.WARNING).build());
        } else if (message.startsWith("[" + MAVEN + "]")) {
            html.setHTML(messageBuilder.type(MessageType.MAVEN).build());
        } else if (message.startsWith("[" + GRADLE + "]")) {
            html.setHTML(messageBuilder.type(MessageType.GRADLE).build());
        } else {
            html.setHTML(messageBuilder.build());
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

    @Override
    protected void focusView() {
        scrollPanel.getElement().focus();
    }

}
