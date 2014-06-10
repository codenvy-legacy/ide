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
package com.codenvy.ide.part.console;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.parts.base.BaseActionDelegate;

/**
 * View of {@link ConsolePartPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface ConsolePartView extends View<ConsolePartView.ActionDelegate> {
    public interface ActionDelegate extends BaseActionDelegate {
        /**
         * Handle user clicks on clear console button.
         */
        void onClearClicked();
    }

    /**
     * Print text in console area.
     *
     * @param text
     *         text that need to be shown
     */
    void print(String text);

    void print(String text, String color);

    void printInfo(String text);

    void printWarn(String text);

    void printError(String text);

    /**
     * Set title of console part.
     *
     * @param title
     *         title that need to be set
     */
    void setTitle(String title);

    /** Clear console. Remove all messages. */
    void clear();
    
    /**
     * Scroll to bottom of the view.
     */
    void scrollBottom();
}