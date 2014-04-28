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

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.parts.base.BaseActionDelegate;

/**
 * View of {@link BuilderConsolePresenter}.
 *
 * @author Artem Zatsarynnyy
 */
public interface BuilderConsoleView extends View<BuilderConsoleView.ActionDelegate> {
    public interface ActionDelegate extends BaseActionDelegate {
    }

    /**
     * Print message in console area.
     *
     * @param message
     *         message that need to be shown
     */
    void print(String message);

    /**
     * Set title of console part.
     *
     * @param title
     *         title that need to be set
     */
    void setTitle(String title);

    /** Clear console. Remove all messages. */
    void clear();

    /** Scroll to bottom of the view. */
    void scrollBottom();

    /**
     * Set URL to download artifact.
     *
     * @param link
     *         link to download artifact
     */
    void setDownloadLink(String link);
}