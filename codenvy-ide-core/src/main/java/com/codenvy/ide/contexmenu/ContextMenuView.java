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
package com.codenvy.ide.contexmenu;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.mvp.View;

/**
 * The view of {@link ContextMenuPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface ContextMenuView extends View<ContextMenuView.ActionDelegate> {
    /** Needs for delegate some function into MainMenu view. */
    public interface ActionDelegate {
    }

    /**
     * Set place name.
     *
     * @param place
     *         place name
     */
    void setPlace(@NotNull String place);

    /**
     * Show menu in specified position.
     *
     * @param x
     *         the x-position on the browser window's client area.
     * @param y
     *         the y-position on the browser window's client area.
     */
    void show(int x, int y);
}