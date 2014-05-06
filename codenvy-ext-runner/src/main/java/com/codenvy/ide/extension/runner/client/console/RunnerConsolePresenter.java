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
package com.codenvy.ide.extension.runner.client.console;

import com.codenvy.ide.api.parts.base.BasePresenter;
import com.codenvy.ide.api.ui.workspace.PartPresenter;
import com.codenvy.ide.toolbar.ToolbarPresenter;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Runner console.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class RunnerConsolePresenter extends BasePresenter implements RunnerConsoleView.ActionDelegate {
    private static final String TITLE = "Runner";
    private RunnerConsoleView view;
    private final ToolbarPresenter consoleToolbar;

    /** Construct empty Part */
    @Inject
    public RunnerConsolePresenter(RunnerConsoleView view, @RunnerConsoleToolbar ToolbarPresenter consoleToolbar) {
        this.view = view;
        this.consoleToolbar = consoleToolbar;
        this.view.setTitle(TITLE);
        this.view.setDelegate(this);
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return TITLE;
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getTitleImage() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getTitleToolTip() {
        return "Displays Runner output";
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        consoleToolbar.go(view.getToolbarPanel());
        container.setWidget(view);
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
            partStack.setActivePart(this);
        }
        view.print(message);
        view.scrollBottom();
    }

    /** Clear console. Remove all messages. */
    public void clear() {
        view.clear();
    }
}