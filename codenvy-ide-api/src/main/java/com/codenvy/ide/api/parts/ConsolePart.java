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

import com.codenvy.ide.api.extension.SDK;
import com.codenvy.ide.api.ui.workspace.PartPresenter;

import javax.validation.constraints.NotNull;


/**
 * Provides output messages on console.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@SDK(title = "ide.api.ui.console")
public interface ConsolePart extends PartPresenter {

    /**
     * Print text on console.
     *
     * @param text
     *         text that need to be shown
     */
    void print(@NotNull String text);

    /**
     * Display an exception.
     *
     * @param e
     */
    void displayException(Exception e);

    /**
     * [INFO] text
     *
     * @param text
     */
    void printInfo(String text);

    /**
     * [ERROR] text
     *
     * @param text
     */
    void printError(String text);

    /**
     * [WARNING] text
     *
     * @param text
     */
    void printWarn(String text);

    /** Clear console. Remove all messages. */
    void clear();

}
