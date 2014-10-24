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

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.parts.base.BaseActionDelegate;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * View of {@link RunnerConsolePresenter}.
 *
 * @author Artem Zatsarynnyy
 * @author Vitaliy Guliy
 */
public interface RunnerConsoleView extends View<RunnerConsoleView.ActionDelegate> {

    public interface ActionDelegate extends BaseActionDelegate {
    }

    /** @return toolbar panel */
    AcceptsOneWidget getToolbarPanel();

    /**
     * Set title of console part.
     * @param title title to set
     */
    void setTitle(String title);


    /** Sets new Terminal frame URL. */
    void setTerminalURL(String terminalURL);

    /** Sets new App frame URL. */
    void setAppURL(String appURL);

    /**
     * Activate Console tab
     */
    void activateConsole();

    /**
     * Activate Terminal tab
     */
    void activateTerminal();

    /**
     * Activate App preview tab
     */
    void activateApp();

    /** Clear console. Remove all messages. */
    void clearConsole();

    /** Scroll to bottom of the view. */
    void scrollBottom();

    /**
     * Print text in console area.
     * @param text text that need to be shown
     */
    void print(String text);
}