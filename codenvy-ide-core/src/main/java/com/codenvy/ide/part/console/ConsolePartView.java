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