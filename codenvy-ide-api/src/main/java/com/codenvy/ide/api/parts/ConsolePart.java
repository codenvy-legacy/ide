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
package com.codenvy.ide.api.parts;

import com.codenvy.ide.api.extension.SDK;

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
