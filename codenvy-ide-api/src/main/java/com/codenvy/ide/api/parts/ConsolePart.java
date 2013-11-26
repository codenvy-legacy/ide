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
package com.codenvy.ide.api.parts;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.extension.SDK;
import com.codenvy.ide.api.ui.workspace.PartPresenter;


/**
 * Provides output messages on console.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@SDK(title = "ide.api.ui.console")
public interface ConsolePart extends PartPresenter {
    /**
     * Print message on console.
     *
     * @param message
     *         message that need to be shown
     */
    void print(@NotNull String message);

    /**
     * Print message on console in @pre tag. Useful for format plain text.
     * Don't use it for HTML content.
     *
     * @param message message that need to be shown
     */
    void printf(@NotNull String message);

    /** Clear console. Remove all messages. */
    void clear();
}