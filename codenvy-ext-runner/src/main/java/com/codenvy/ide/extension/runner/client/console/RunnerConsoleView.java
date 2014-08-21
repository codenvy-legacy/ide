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
 */
public interface RunnerConsoleView extends View<RunnerConsoleView.ActionDelegate> {
    public interface ActionDelegate extends BaseActionDelegate {
        /** Called when Terminal tab opened. */
        void onTerminalTabOpened();

        /** Called when Terminal loaded into frame. */
        void onTerminalLoaded();

        /** Called when tab for app preview is opened. */
        void onAppTabOpened();

        /** Called when app preview loaded into frame. */
        void onAppPreviewLoaded();

        /** Called when console tab opened. */
        void onConsoleTabOpened();
    }

    /** @return toolbar panel */
    AcceptsOneWidget getToolbarPanel();

    /**
     * Set title of console part.
     *
     * @param title
     *         title to set
     */
    void setTitle(String title);

    /** (Re)load Terminal frame. */
    void reloadTerminalFrame(String url);

    /** (Re)load frame for previewing an app. */
    void reloadAppPreviewFrame(String url);

    /** Hide Terminal panel and show special empty panel instead. */
    void hideTerminal();

    /** Hide panel for previewing an app and show special empty panel instead. */
    void hideAppPreview();

    /** Clear console. Remove all messages. */
    void clearConsole();

    /** Scroll to bottom of the view. */
    void scrollBottom();

    /**
     * Print text in console area.
     *
     * @param text
     *         text that need to be shown
     */
    void print(String text);
}