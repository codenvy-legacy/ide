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

import com.codenvy.ide.api.parts.base.BasePresenter;
import com.codenvy.ide.api.ui.workspace.PartPresenter;
import com.codenvy.ide.toolbar.ToolbarPresenter;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Builder console.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class BuilderConsolePresenter extends BasePresenter implements BuilderConsoleView.ActionDelegate {
    private static final String TITLE = "Builder";
    private final BuilderConsoleView view;
    private final ToolbarPresenter   consoleToolbar;

    @Inject
    public BuilderConsolePresenter(BuilderConsoleView view, @BuilderConsoleToolbar ToolbarPresenter consoleToolbar) {
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
        return "Displays Builder output";
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        consoleToolbar.go(view.getToolbarPanel());
        container.setWidget(view);
    }

    /**
     * Print message to console.
     *
     * @param message
     *         message that need to be print
     */
    public void print(String message) {
        PartPresenter activePart = partStack.getActivePart();
        if (activePart == null || !activePart.equals(this)) {
            partStack.setActivePart(this);
        }

        String[] lines = message.split("\n");
        for (String line : lines) {
            view.print(line);
        }

        view.scrollBottom();
    }

    /** Clear console. Remove all messages. */
    public void clear() {
        view.clear();
    }
}