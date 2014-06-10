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
package com.codenvy.ide.contexmenu;

import com.codenvy.ide.api.mvp.View;

import javax.validation.constraints.NotNull;

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