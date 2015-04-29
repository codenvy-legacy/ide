/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.extension.builder.client.console;

import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.api.parts.base.BaseActionDelegate;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * View of {@link BuilderConsolePresenter}.
 *
 * @author Artem Zatsarynnyy
 */
public interface BuilderConsoleView extends View<BuilderConsoleView.ActionDelegate> {

    interface ActionDelegate extends BaseActionDelegate {
    }

    /**
     * Returns toolbar panel.
     *
     * @return toolbar panel
     */
     AcceptsOneWidget getToolbarPanel();

    /**
     * Print message to console area.
     *
     * @param message message that need to be shown
     */
    void print(String message);

    /** Do not use it. This is workaround of freezing FF (see IDEX-2327). */
    void printFF(char ch);

    /**
     * Set title of console part.
     *
     * @param title title that need to be set
     */
    void setTitle(String title);

    /**
     * Clears the console.
     */
    void clear();

    /**
     * Scrolls console bottom.
     */
    void scrollBottom();
}